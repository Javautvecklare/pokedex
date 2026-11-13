package org.example;

import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Path savePath = Path.of("pokedex.json");
        ArrayList<Pokemon> pokedex = JsonStorage.loadJson(savePath);
        if (pokedex.isEmpty()) {
            pokedex = SeedData.createSeed();
        }

        boolean running = true;
        while (running) {                                          // loopen körs tills användaren väljer Avsluta
            try {
                printMenu();
                int choice = InputHelper.readIntInRange(scanner, "Välj (1-9): ", 1, 9);   // alla val går genom samma säkra inläsning
                switch (choice) {
                    case 1 -> showAll(pokedex);
                    case 2 -> addPokemon(scanner, pokedex);
                    case 3 -> editPokemon(scanner, pokedex);
                    case 4 -> removePokemon(scanner, pokedex);
                    case 5 -> JsonStorage.saveJson(savePath, pokedex);
                    case 6 -> {
                        ArrayList<Pokemon> loaded = JsonStorage.loadJson(savePath);
                        if (loaded.isEmpty()) {                            // tom lista betyder att inget kunde laddas, så vi behåller det vi har
                            System.out.println("Ingen data laddades. Din nuvarande lista behålls.");
                        } else {
                            pokedex = loaded;
                            System.out.println("Laddade " + pokedex.size() + " Pokémon från filen.");
                        }
                    }
                    case 7 -> {
                        pokedex = SeedData.createSeed();
                        System.out.println("Pokédexen är återställd till seed-data.");
                    }
                    case 8 -> running = false;
                    case 9 -> searchPokemon(scanner, pokedex);
                    default -> System.out.println("Det här menyvalet är inte klart än.");   // tills resten är byggt
                }
            } catch (Exception e) {
                System.out.println("Oväntat fel, programmet avslutas kontrollerat: " + e.getMessage());
                running = false;
            }
        }
        JsonStorage.saveJson(savePath, pokedex);
        System.out.println("Hej då!");
    }

    public static void editPokemon(Scanner scanner, ArrayList<Pokemon> pokedex) {
        if (pokedex.isEmpty()) {
            System.out.println("Pokédexen är tom.");
        } else {
            showAll(pokedex);
            int index = InputHelper.readIntInRange(scanner, "Vilken Pokémon vill du redigera (nummer)? ", 1, pokedex.size()) - 1;
            Pokemon p = pokedex.get(index);

            boolean editing = true;
            while (editing) {
                System.out.println();
                System.out.println("Redigerar " + p.name);
                System.out.println("1. Ändra namn  2. Ändra HP  3. Ändra typ  4. Lägg till attack  5. Ta bort attack  6. Klar");
                int choice = InputHelper.readIntInRange(scanner, "Välj (1-6): ", 1, 6);
                try {
                    switch (choice) {
                        case 1 -> changeName(scanner, p);
                        case 2 -> changeHp(scanner, p);
                        case 3 -> p.type = chooseType(scanner);
                        case 4 -> addAttackTo(scanner, p);
                        case 5 -> removeAttackFrom(scanner, p);
                        case 6 -> editing = false;
                    }
                } catch (InvalidPokemonException e) {          // samma regler som vid skapande, så ingen ogiltig ändring kan sparas
                    System.out.println("Ändringen sparades inte: " + e.getMessage());
                }
            }
        }
    }

    public static void changeName(Scanner scanner, Pokemon p) {
        String newName = InputHelper.readText(scanner, "Nytt namn: ", 30);
        Validator.validateName(newName);                       // kollas före tilldelningen
        p.name = newName;
    }

    public static void changeHp(Scanner scanner, Pokemon p) {
        int newHp = InputHelper.readIntInRange(scanner, "Ny max HP (1-999): ", 1, 999);
        Validator.validateHp(newHp);
        p.maxHp = newHp;
        if (p.currentHp > p.maxHp) {                           // aktuellt HP får aldrig vara högre än max
            p.currentHp = p.maxHp;
        }
    }

    public static void addAttackTo(Scanner scanner, Pokemon p) {
        if (p.attacks.size() >= 4) {                           // en Pokémon har högst 4 attacker
            System.out.println("En Pokémon kan ha högst 4 attacker.");
        } else {
            p.attacks.add(readAttack(scanner));
        }
    }

    public static void removeAttackFrom(Scanner scanner, Pokemon p) {
        if (p.attacks.size() <= 1) {                           // en Pokémon måste ha minst 1 attack
            System.out.println("En Pokémon måste ha minst 1 attack.");
        } else {
            for (int i = 0; i < p.attacks.size(); i++) {
                System.out.println((i + 1) + ". " + p.attacks.get(i).name);
            }
            int index = InputHelper.readIntInRange(scanner, "Vilken attack vill du ta bort (nummer)? ", 1, p.attacks.size()) - 1;
            p.attacks.remove(index);
        }
    }

    public static void searchPokemon(Scanner scanner, ArrayList<Pokemon> pokedex) {
        String name = InputHelper.readText(scanner, "Sök namn: ", 30);
        try {
            Pokemon p = findByName(pokedex, name);
            System.out.printf("%-12s | Typ: %-8s | HP: %d/%d%n", p.name, p.type, p.currentHp, p.maxHp);
            for (Attack a : p.attacks) {
                System.out.println("   - " + a.name + " (" + a.baseDamage + " dmg, " + a.accuracy + " % träff, " + a.type + ")");
            }
        } catch (PokemonNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Pokemon findByName(ArrayList<Pokemon> pokedex, String name) {
        for (Pokemon p : pokedex) {
            if (p.name.toUpperCase().equals(name.toUpperCase())) {   // toUpperCase så att "pikachu" och "Pikachu" räknas som samma
                return p;
            }
        }
        throw new PokemonNotFoundException("Hittade ingen Pokémon med namnet " + name);
    }

    public static void removePokemon(Scanner scanner, ArrayList<Pokemon> pokedex) {
        if (pokedex.isEmpty()) {
            System.out.println("Pokédexen är tom.");
        } else {
            showAll(pokedex);
            int index = InputHelper.readIntInRange(scanner, "Vilken Pokémon vill du ta bort (nummer) ", 1, pokedex.size()) -1;
            Pokemon removed = pokedex.get(index);
            pokedex.remove(index);
            System.out.println(removed.name + " togs bort.");
        }
    }

    // Håller main kort: varje menyval har en egen metod
    public static void printMenu() {
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

    public static void showAll(ArrayList<Pokemon> pokedex) {
        if (pokedex.isEmpty()) {
            System.out.println("Pokédexen är tom.");
        } else {
            for (int i = 0; i < pokedex.size(); i++) {
                Pokemon p = pokedex.get(i);
                System.out.printf("%d. %-12s | Typ: %-8s | HP: %d/%d | Attacker: %d%n",
                        i + 1, p.name, p.type, p.currentHp, p.maxHp, p.attacks.size());
            }
        }
    }
    public static void addPokemon(Scanner scanner, ArrayList<Pokemon> pokedex) {
        String name = InputHelper.readText(scanner, "Namn: ", 30);
        Type type = chooseType(scanner);
        int maxHp = InputHelper.readIntInRange(scanner, "Max HP (1-999): ", 1, 999);
        int attackCount = InputHelper.readIntInRange(scanner, "Antal attacker (1-4): ", 1, 4);   // en Pokémon ska ha 1-4 attacker

        try {
            Pokemon pokemon = new Pokemon(name, type, maxHp);
            for (int i = 1; i <= attackCount; i++) {
                System.out.println("Attack " + i + ":");
                pokemon.attacks.add(readAttack(scanner));
            }
            pokedex.add(pokemon);
            System.out.println(pokemon.name + " lades till.");
        } catch (InvalidPokemonException e) {
            System.out.println("Kunde inte skapa Pokémon: " + e.getMessage());
        }
    }

    // Användaren väljer typ med en siffra, så att felstavningar inte går att skriva
    public static Type chooseType(Scanner scanner) {
        System.out.println("  1. FIRE  2. WATER  3. GRASS  4. ELECTRIC  5. NORMAL");
        int choice = InputHelper.readIntInRange(scanner, "  Välj typ (1-5): ", 1, 5);
        return switch (choice) {
            case 1 -> Type.FIRE;
            case 2 -> Type.WATER;
            case 3 -> Type.GRASS;
            case 4 -> Type.ELECTRIC;
            case 5 -> Type.NORMAL;
            default -> Type.NORMAL;
        };
    }

    public static Attack readAttack(Scanner scanner) {
        String name = InputHelper.readText(scanner, "  Namn: ", 30);
        int damage = InputHelper.readIntInRange(scanner, "  Skada (1-300): ", 1, 300);
        int accuracy = InputHelper.readIntInRange(scanner, "  Träffsäkerhet (1-100): ", 1, 100);
        Type type = chooseType(scanner);
        return new Attack(name, damage, accuracy, type);     // kastar InvalidPokemonException om något ändå är fel
    }


}
