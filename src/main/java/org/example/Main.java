package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Pokemon> pokedex = SeedData.createSeed();

        boolean running = true;
        while (running) {                                          // loopen körs tills användaren väljer Avsluta
            printMenu();
            int choice = InputHelper.readIntInRange(scanner, "Välj (1-8): ", 1, 8);   // alla val går genom samma säkra inläsning
            switch (choice) {
                case 1 -> showAll(pokedex);
                case 2 -> addPokemon(scanner, pokedex);
                case 8 -> running = false;
                default -> System.out.println("Det här menyvalet är inte klart än.");   // tills resten är byggt
            }
        }
        System.out.println("Hej då!");
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
                String attackName = InputHelper.readText(scanner, "  Namn: ", 30);
                int damage = InputHelper.readIntInRange(scanner, "  Skada (1-300): ", 1, 300);
                int accuracy = InputHelper.readIntInRange(scanner, "  Träffsäkerhet (1-100): ", 1, 100);
                Type attackType = chooseType(scanner);
                pokemon.attacks.add(new Attack(attackName, damage, accuracy, attackType));
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

}
