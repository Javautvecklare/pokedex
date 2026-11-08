package org.example;

public class Main {
    public static void main(String[] args) {
        Attack thunder = new Attack("Thunderbolt", 40, 100, Type.ELECTRIC);
        System.out.println(thunder.name + " " + thunder.baseDamage + " " + thunder.accuracy + " " + thunder.type);

        try {
            Attack bad = new Attack("", -5, 500, null);
            System.out.println("Byggd ändå: " + bad.name);   // Krasch-test - ska ej se detta om jag skrivit rätT!
        } catch (InvalidPokemonException e) {
            System.out.println("Fel: " + e.getMessage());
        }
    }
}
