package org.example;

import java.util.List;
import java.util.Scanner;

/**
 * Spelarens val: visar attackerna som en menu och frågar tills spelaren anger ett giltigt nummer.
 * Ogiltig inmatning (-1, 99, "abc") hanteras av InputHelper, som frågar om.
 */
public class PlayerAttackChooser implements AttackChooser {
    private final Scanner scanner;

    public PlayerAttackChooser(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public Attack choose(Pokemon self, Pokemon opponent) {
        List<Attack> attacks = self.getAttacks();
        System.out.println("Välj attack för " + self.getName() + ":");
        for (int i = 0; i < attacks.size(); i++) {
            Attack a = attacks.get(i);
            System.out.println("  " + (i + 1) + ". " + a.getName() + " (" + a.getType() + ", " + a.getBaseDamage() + " dmg, " + a.getAccuracy() + " % träff)");
        }
        int index = InputHelper.readIntInRange(scanner, "Attack (1-" + attacks.size() + "): ", 1, attacks.size());
        return attacks.get(index - 1);
    }
}