package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);                                         // Bygg en avläsare för tangentbordet

        String name = InputHelper.readText(scanner, "Namn: ", 20);                        // InputHelper. = hämta verktyget i klassen InputHelper
        int choice = InputHelper.readIntInRange(scanner, "Val (1-4): ", 1, 4);            // Frågar tills talet är 1-4
        System.out.println("Hej " + name + ", du valde " + choice + ".");                       // Visa resultatet
    }
}
