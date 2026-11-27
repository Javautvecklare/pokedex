package org.example;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Konsolmenyn. All inmatning och utskrift finns här,
 * medan själva hanteringen av samlingen sköts av PokedexService.
 */
public class PokedexMenu {
    private final Scanner scanner;
    private final PokedexService pokedex = new PokedexService();
    private final Path savePath = Path.of("pokedex.json");
    private final Path statsPath = Path.of("stats.json");
    private final Stats stats;
    private final Random random = new Random();

    public PokedexMenu(Scanner scanner) {
        this.scanner = scanner;
        this.stats = JsonStorage.loadStats(statsPath);
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
                int choice = InputHelper.readIntInRange(scanner, "Välj (1-11): ", 1, 11);   // alla val går genom samma säkra inläsning
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
                    case 10 -> startBattle();
                    case 11 -> showStats();
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
        System.out.println("10. Starta strid");
        System.out.println("11. Visa stridsresultat");
    }

    private void showStats() {
        if (stats.totalBattles() == 0) {                           // tom historik är inget fel
            System.out.println("Inga strider har spelats än, så det finns ingen statistik att visa.");
            return;
        }
        System.out.println("=== STRIDSRESULTAT ===");
        System.out.println("Totalt: " + describe(stats.getWins(), stats.getLosses()));
        System.out.println();
        System.out.println("Per Pokémon:");
        for (String name : stats.pokemonNames()) {
            PokemonRecord record = stats.recordFor(name);
            System.out.printf("  %-12s %s%n", name, describe(record.getWins(), record.getLosses()));
        }
        System.out.println();
        String topPokemon = stats.mostUsedPokemon();
        if (topPokemon != null) {
            System.out.println("Mest använda Pokémon: " + topPokemon + " (" + stats.recordFor(topPokemon).battles() + " strider)");
        }
        String topAttack = stats.mostUsedAttack();
        if (topAttack != null) {
            System.out.println("Mest använda attack: " + topAttack + " (" + stats.usesOf(topAttack) + " gånger)");
        }
    }

    // Gemensam text för totalt och per Pokémon: vinster, förluster, vinstandel och vinst/förlust-kvot
    private String describe(int wins, int losses) {
        int total = wins + losses;
        int percent = total == 0 ? 0 : Math.round(wins * 100f / total);
        String ratio = losses == 0 ? "-" : String.format("%.2f", (double) wins / losses);   // kvot går inte att räkna utan förluster
        return wins + " vinster, " + losses + " förluster (vinstandel " + percent + " %, vinst/förlust-kvot " + ratio + ")";
    }

    private void startBattle() {
        if (pokedex.isEmpty()) {
            System.out.println("Du äger inga Pokémon. Lägg till en först.");
            return;
        }
        showAll();
        int index = InputHelper.readIntInRange(scanner, "Vilken Pokémon vill du strida med (nummer)? ", 1, pokedex.size()) - 1;
        Pokemon mine = pokedex.get(index);

        ArrayList<Pokemon> wildPool = SeedData.createWildPool();
        Pokemon wild = wildPool.get(random.nextInt(wildPool.size()));   // datorn väljer motståndare slumpvis

        try {
            Battle battle = new Battle(mine, new PlayerAttackChooser(scanner),
                    wild, new RandomAttackChooser(random), new DamageCalculator(new TypeChart(), random));
            playBattle(battle);
        } catch (InvalidPokemonException e) {                      // t.ex. 0 HP eller inga attacker: striden startar aldrig
            System.out.println("Striden kunde inte starta: " + e.getMessage());
        }
    }

    private void playBattle(Battle battle) {
        System.out.println();
        System.out.println("=== STRID: " + battle.getPlayerName() + " mot en vild " + battle.getWildName() + " ===");
        while (!battle.isOver()) {
            System.out.println();
            System.out.println("--- Tur " + battle.getTurnNumber() + " ---");
            System.out.println(battle.statusLine());
            for (String line : battle.takeTurn()) {
                System.out.println(line);
            }
        }
        System.out.println();
        if (battle.playerWon()) {
            System.out.println("Du vann! Din " + battle.getPlayerName() + " vann över en vild " + battle.getWildName() + ".");
        } else {
            System.out.println("Du förlorade... Din " + battle.getPlayerName() + " besegrades av en vild " + battle.getWildName() + ".");
        }
        stats.recordBattle(battle.getPlayerName(), battle.playerWon(), battle.getPlayerAttacksUsed());
        JsonStorage.saveStats(statsPath, stats);                   // sparas direkt efter varje strid, så inget går förlorat vid en krasch
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