package org.example;

import java.util.Scanner;

/**
 * Säker inläsning från tangentbordet. Alla metoder läser en hel rad som text
 * och frågar om tills användaren ger giltig input, så att programmet aldrig kraschar.
 */
public class InputHelper {

    // "int" före namnet = metoden lämnar tillbaka ett heltal
    public static int readInt(Scanner scanner, String prompt) {
        while (true) {                                  // Upprepa tills vi får ett giltigt tal
            System.out.print(prompt);                   // Visa frågan, utan radbrytning
            String input = scanner.nextLine().trim();   // Läs hela raden, ta bort mellanslag i början och slutet
            try {
                return Integer.parseInt(input);         // Gick det att tolka? Lämna tillbaka talet och avsluta metoden
            } catch (NumberFormatException e) {         // Texten var inget heltal/int
                System.out.println("Det där var inget heltal. Försök igen.");   // Felmeddlande, loops körs om
            }
        }
    }

    // Läser ett heltal mellan min och max (gränserna ingår)
    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {                                  // Upprepa tills talet ligger inom gränsen
            int value = readInt(scanner, prompt);       // read Int sköter allt som har med siffror att göra
            if (value >= min && value <= max) {         // kollar om talet är inom min och max-gränsen
                return value;                           // Ja -> lämna tillbaka det
            }
            System.out.printf("Ange ett tal mellan %d och %d.%n", min, max);   // Nej: förklara och fråga igen
        }
    }

    // Läser en text som varken får vara tom eller för lång
    public static String readText(Scanner scanner, String prompt, int maxLength) {
        while (true) {                                  // Upprepa tills texten är giltig
            System.out.print(prompt);                   // Visa frågan
            String input = scanner.nextLine().trim();   // Läs raden, ta bort mellanslag runtom
            if (input.isEmpty()) {                      // isEmpty() är true om texten är tom
                System.out.println("Texten får inte vara tom.");
            } else if (input.length() > maxLength) {    // length() = antal tecken i texten
                System.out.println("Max " + maxLength + " tecken.");
            } else {
                return input;                           // Giltig text -> lämna tillbaka den
            }
        }
    }
}
