package org.example;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);                  // en enda Scanner som skickas vidare till de som behöver läsa
        ArrayList<Pokemon> pokedex = SeedData.createSeed();        // ersätts med inläsning från fil senare

        boolean running = true;
        while (running) {                                          // loopen körs tills användaren väljer Avsluta
            printMenu();
            int choice = InputHelper.readIntInRange(scanner, "Välj (1-8): ", 1, 8);   // alla val går genom samma säkra inläsning
            switch (choice) {
                case 1 -> showAll(pokedex);
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
        if (pokedex.isEmpty()) {                                   // en tom lista ska ge ett tydligt besked, inte en tom skärm
            System.out.println("Pokédexen är tom.");
        } else {
            for (int i = 0; i < pokedex.size(); i++) {             // klassisk for, eftersom vi behöver numret till redigera/ta bort
                Pokemon p = pokedex.get(i);
                System.out.printf("%d. %-12s | Typ: %-8s | HP: %d/%d | Attacker: %d%n",
                        i + 1, p.name, p.type, p.currentHp, p.maxHp, p.attacks.size());   // i + 1 så att listan börjar på 1, inte 0
            }
        }
    }
}
