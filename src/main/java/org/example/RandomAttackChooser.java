package org.example;

import java.util.List;
import java.util.Random;

/**
 * Datorns val: en random attack bland Pokémons egna attacker.
 */
public class RandomAttackChooser implements AttackChooser {
    private final Random random;

    public RandomAttackChooser(Random random) {
        this.random = random;
    }

    @Override
    public Attack choose(Pokemon self, Pokemon opponent) {
        List<Attack> attacks = self.getAttacks();
        return attacks.get(random.nextInt(attacks.size()));        // Battle har redan kontrollerat att det finns minst en attack
    }
}