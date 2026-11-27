package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        new PokedexMenu(scanner).run();                            // Main startar bara programmet, menyn sköter resten
    }
}