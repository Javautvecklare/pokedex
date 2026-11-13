package org.example;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Pokemon> pokedex = SeedData.createSeed();

        for (Pokemon p : pokedex) {
            System.out.printf("%-12s | Typ: %-8s | HP: %d/%d%n", p.name, p.type, p.currentHp, p.maxHp);
            for (Attack a : p.attacks) {
                System.out.println("   - " + a.name + " (" + a.baseDamage + " dmg)");
            }
        }
    }
}
