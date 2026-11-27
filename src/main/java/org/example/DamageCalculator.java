package org.example;

import java.util.Random;

/**
 * Räknar ut träff, kritisk träff och skada. All slump går genom ett Random-objekt som skickas in,
 * så att beräkningen går att testa med ett förutsägbart Random.
 *
 * Formel (för en attack som träffar):
 *   skada = round(basskada × typeffektivitet × slumpfaktor × kritisk)
 *   slumpfaktor = slumptal mellan 0,85 och 1,00
 *   kritisk     = 2 vid kritisk träff (1 chans på 16), annars 1
 *   skadan är minst 1 när attacken träffar
 * Träff: slumptal 0-99 är lägre än attackens träffsäkerhet i procent.
 */
public class DamageCalculator {
    private final TypeChart typeChart;
    private final Random random;

    public DamageCalculator(TypeChart typeChart, Random random) {
        this.typeChart = typeChart;
        this.random = random;
    }

    public boolean hits(Attack attack) {
        return random.nextInt(100) < attack.getAccuracy();
    }

    public boolean isCritical() {
        return random.nextInt(16) == 0;                            // 1 chans på 16
    }

    public double effectiveness(Attack attack, Pokemon defender) {
        return typeChart.getMultiplier(attack.getType(), defender.getType());
    }

    public int calculate(Attack attack, Pokemon defender, boolean critical) {
        double randomFactor = 0.85 + random.nextDouble() * 0.15;   // 0,85 upp till strax under 1,00
        double criticalFactor = critical ? 2.0 : 1.0;
        double damage = attack.getBaseDamage() * effectiveness(attack, defender) * randomFactor * criticalFactor;
        return (int) Math.max(1, Math.round(damage));              // en träff gör alltid minst 1 i skada
    }
}