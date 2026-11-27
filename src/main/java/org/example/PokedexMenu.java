package org.example;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Konsolmenyn. All inmatning och utskrift finns här,
 * medan själva hanteringen av samlingen sköts av PokedexService.
 */
public class PokedexMenu {
    private final Scanner scanner;
    private final PokedexService pokedex = new PokedexService();
    private final Path savePath = Path.of("pokedex.json");

    public PokedexMenu(Scanner scanner) {
        this.scanner = scanner;
        pokedex.replaceAll(JsonStorage.loadJson(savePath));
        if (pokedex.isEmpty()) {                                   // första körningen eller tom/trasig fil
            pokedex.replaceAll(SeedData.createSeed());
        }
    }

    public void run() {
        boolean running = true;
        while (running) {                                          // loopen körs tills användaren väljer Avsluta
            try {
                printMenu();
                int choice = InputHelper.readIntInRange(scanner, "Välj (1-9): ", 1, 9);   // alla val går genom samma säkra inläsning
                switch (choice) {
                    case 1 -> showAll();
                    case 2 -> addPokemon();
                    case 3 -> editPokemon();
                    case 4 -> removePokemon();
                    case 5 -> JsonStorage.saveJson(savePath, pokedex.getAll());
                    case 6 -> loadFromFile();
                    case 7 -> {
                        pokedex.replaceAll(SeedData.createSeed());
                        System.out.println("Pokédexen är återställd till seed-data.");
                    }
                    case 8 -> running = false;
                    case 9 -> searchPokemon();
                }
            } catch (NoSuchElementException e) {                   // Ctrl+D eller avbruten inmatning: det finns ingen mer input att läsa
                System.out.println();
                System.out.println("Ingen mer inmatning. Programmet avslutas.");
                running = false;
            } catch (Exception e) {
                System.out.println("Oväntat fel, programmet avslutas kontrollerat: " + e.getMessage());
                running = false;
            }
        }
        JsonStorage.saveJson(savePath, pokedex.getAll());
        System.out.println("Hej då!");
    }

    private void loadFromFile() {
        ArrayList<Pokemon> loaded = JsonStorage.loadJson(savePath);
        if (loaded.isEmpty()) {                                    // tom lista betyder att inget kunde laddas, så vi behåller det vi har
            System.out.println("Ingen data laddades. Din nuvarande lista behålls.");
        } else {
            pokedex.replaceAll(loaded);
            System.out.println("Laddade " + pokedex.size() + " Pokémon från filen.");
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== POKÉDEX ===");
        System.out.println("1. Visa alla Pokémon");
        System.out.println("2. Lägg till Pokémon");
        System.out.println("3. Redigera Pokémon");
        System.out.println("4. Ta bort Pokémon");
        System.out.println("5. Spara till fil");
        System.out.println("6. Ladda från fil");
        System.out.println("7. Återställ till seed-data");
        System.out.println("8. Avsluta");
        System.out.println("9. Sök Pokémon");
    }

    private void showAll() {
        if (pokedex.isEmpty()) {
            System.out.println("Pokédexen är tom.");
        } else {
            for (int i = 0; i < pokedex.size(); i++) {
                Pokemon p = pokedex.get(i);
                System.out.printf("%d. %-12s | Typ: %-8s | HP: %d/%d | Fart: %d | Attacker: %d%n",
                        i + 1, p.getName(), p.getType(), p.getCurrentHp(), p.getMaxHp(), p.getSpeed(), p.getAttacks().size());
            }
        }
    }

    private void addPokemon() {
        String name = InputHelper.readText(scanner, "Namn: ", 30);
        Type type = chooseType();
        int maxHp = InputHelper.readIntInRange(scanner, "Max HP (1-999): ", 1, 999);
        int speed = InputHelper.readIntInRange(scanner, "Fart (1-200): ", 1, 200);   // snabbast Pokémon anfaller först i en strid
        int attackCount = InputHelper.readIntInRange(scanner, "Antal attacker (1-4): ", 1, 4);   // en Pokémon ska ha 1-4 attacker

        try {
            Pokemon pokemon = new Pokemon(name, type, maxHp, speed);
            for (int i = 1; i <= attackCount; i++) {
                System.out.println("Attack " + i + ":");
                pokemon.addAttack(readAttack());
            }
            pokedex.add(pokemon);
            System.out.println(pokemon.getName() + " lades till.");
        } catch (InvalidPokemonException e) {
            System.out.println("Kunde inte skapa Pokémon: " + e.getMessage());
        }
    }

    private void editPokemon() {
        if (pokedex.isEmpty()) {
            System.out.println("Pokédexen är tom.");
            return;
        }
        showAll();
        int index = InputHelper.readIntInRange(scanner, "Vilken Pokémon vill du redigera (nummer)? ", 1, pokedex.size()) - 1;
        Pokemon p = pokedex.get(index);

        boolean editing = true;
        while (editing) {
            System.out.println();
            System.out.println("Redigerar " + p.getName());
            System.out.println("1. Ändra namn  2. Ändra HP  3. Ändra typ  4. Lägg till attack  5. Ta bort attack  6. Läk till full HP  7. Klar");
            int choice = InputHelper.readIntInRange(scanner, "Välj (1-7): ", 1, 7);
            try {
                switch (choice) {
                    case 1 -> p.setName(InputHelper.readText(scanner, "Nytt namn: ", 30));
                    case 2 -> p.setMaxHp(InputHelper.readIntInRange(scanner, "Ny max HP (1-999): ", 1, 999));
                    case 3 -> p.setType(chooseType());
                    case 4 -> addAttackTo(p);
                    case 5 -> removeAttackFrom(p);
                    case 6 -> p.setCurrentHp(p.getMaxHp());
                    case 7 -> editing = false;
                }
            } catch (InvalidPokemonException e) {                  // samma regler som vid skapande, så ingen ogiltig ändring kan sparas
                System.out.println("Ändringen sparades inte: " + e.getMessage());
            }
        }
    }

    private void addAttackTo(Pokemon p) {
        if (p.getAttacks().size() >= 4) {                          // frågar inte efter attacken i onödan
            System.out.println("En Pokémon kan ha högst 4 attacker.");
        } else {
            p.addAttack(readAttack());
        }
    }

    private void removeAttackFrom(Pokemon p) {
        if (p.getAttacks().size() <= 1) {
            System.out.println("En Pokémon måste ha minst 1 attack.");
        } else {
            for (int i = 0; i < p.getAttacks().size(); i++) {
                System.out.println((i + 1) + ". " + p.getAttacks().get(i).getName());
            }
            int index = InputHelper.readIntInRange(scanner, "Vilken attack vill du ta bort (nummer)? ", 1, p.getAttacks().size()) - 1;
            p.removeAttack(index);
        }
    }

    private void removePokemon() {
        if (pokedex.isEmpty()) {
            System.out.println("Pokédexen är tom.");
        } else {
            showAll();
            int index = InputHelper.readIntInRange(scanner, "Vilken Pokémon vill du ta bort (nummer) ", 1, pokedex.size()) - 1;
            Pokemon removed = pokedex.remove(index);
            System.out.println(removed.getName() + " togs bort.");
        }
    }

    private void searchPokemon() {
        String name = InputHelper.readText(scanner, "Sök namn: ", 30);
        try {
            Pokemon p = pokedex.findByName(name);
            System.out.printf("%-12s | Typ: %-8s | HP: %d/%d | Fart: %d%n", p.getName(), p.getType(), p.getCurrentHp(), p.getMaxHp(), p.getSpeed());
            for (Attack a : p.getAttacks()) {
                System.out.println("   - " + a.getName() + " (" + a.getBaseDamage() + " dmg, " + a.getAccuracy() + " % träff, " + a.getType() + ")");
            }
        } catch (PokemonNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // Användaren väljer typ med en siffra, så att felstavningar inte går att skriva
    private Type chooseType() {
        System.out.println("  1. FIRE  2. WATER  3. GRASS  4. ELECTRIC  5. NORMAL");
        int choice = InputHelper.readIntInRange(scanner, "  Välj typ (1-5): ", 1, 5);
        return switch (choice) {
            case 1 -> Type.FIRE;
            case 2 -> Type.WATER;
            case 3 -> Type.GRASS;
            case 4 -> Type.ELECTRIC;
            default -> Type.NORMAL;
        };
    }

    private Attack readAttack() {
        String name = InputHelper.readText(scanner, "  Namn: ", 30);
        int damage = InputHelper.readIntInRange(scanner, "  Skada (1-300): ", 1, 300);
        int accuracy = InputHelper.readIntInRange(scanner, "  Träffsäkerhet (1-100): ", 1, 100);
        Type type = chooseType();
        return new Attack(name, damage, accuracy, type);           // kastar InvalidPokemonException om något ändå är fel
    }
}