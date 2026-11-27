package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * En strid mellan spelarens Pokémon och en vild Pokémon, tur för tur.
 *
 * Turordning: den snabbaste Pokémon anfaller först, vid lika fart börjar spelaren. Sedan turas de om.
 * Striden slutar när ena sidans Pokémon har 0 HP.
 * Striden arbetar på kopior, så spelarens riktiga Pokémon påverkas inte (t.ex. om striden avbryts).
 * Battle vet inte vem som väljer attack: det sköter en AttackChooser.
 * Ingen utskrift här: takeTurn() lämnar tillbaka loggraderna och menyn skriver ut dem.
 */
public class Battle {
    private final Pokemon player;
    private final Pokemon wild;
    private final AttackChooser playerChooser;
    private final AttackChooser wildChooser;
    private final DamageCalculator calculator;
    private final ArrayList<String> playerAttacksUsed = new ArrayList<>();
    private Pokemon attacker;                                      // den vars tur det är
    private int turnNumber = 1;

    public Battle(Pokemon player, AttackChooser playerChooser, Pokemon wild, AttackChooser wildChooser, DamageCalculator calculator) {
        checkCanFight(player);
        checkCanFight(wild);
        this.player = player.copy();
        this.wild = wild.copy();
        this.playerChooser = playerChooser;
        this.wildChooser = wildChooser;
        this.calculator = calculator;
        this.attacker = (this.wild.getSpeed() > this.player.getSpeed()) ? this.wild : this.player;
    }

    // Skyddar mot Pokémon som inte kan strida, så striden aldrig startar i ett ogiltigt läge
    private void checkCanFight(Pokemon p) {
        if (p == null) {
            throw new InvalidPokemonException("Pokémon saknas.");
        }
        if (p.hasFainted()) {
            throw new InvalidPokemonException(p.getName() + " har 0 HP och kan inte strida. Läk den först.");
        }
        if (p.getAttacks().isEmpty()) {
            throw new InvalidPokemonException(p.getName() + " har inga attacker och kan inte strida.");
        }
    }

    public boolean isOver() {
        return player.hasFainted() || wild.hasFainted();
    }

    public boolean playerWon() {
        return wild.hasFainted();
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public String getPlayerName() {
        return player.getName();
    }

    public String getWildName() {
        return wild.getName();
    }

    public List<String> getPlayerAttacksUsed() {
        return Collections.unmodifiableList(playerAttacksUsed);
    }

    public String statusLine() {
        return "Din " + player.getName() + ": " + player.getCurrentHp() + "/" + player.getMaxHp() + " HP"
                + "   |   Vilda " + wild.getName() + ": " + wild.getCurrentHp() + "/" + wild.getMaxHp() + " HP";
    }

    // Spelar en enda tur och lämnar tillbaka vad som hände som loggrader
    public List<String> takeTurn() {
        if (isOver()) {
            throw new IllegalStateException("Striden är redan slut.");
        }
        boolean playersTurn = (attacker == player);
        Pokemon defender = playersTurn ? wild : player;
        AttackChooser chooser = playersTurn ? playerChooser : wildChooser;

        Attack attack = chooser.choose(attacker, defender);         // här skiljer sig spelare och dator, men Battle ser ingen skillnad
        if (playersTurn) {
            playerAttacksUsed.add(attack.getName());
        }

        ArrayList<String> log = new ArrayList<>();
        log.add(label(attacker) + " använder " + attack.getName() + "!");
        if (!calculator.hits(attack)) {
            log.add("Attacken missade!");
        } else {
            boolean critical = calculator.isCritical();
            double effectiveness = calculator.effectiveness(attack, defender);
            int damage = calculator.calculate(attack, defender, critical);
            defender.takeDamage(damage);

            if (critical) {
                log.add("Kritisk träff!");
            }
            if (effectiveness > 1.0) {
                log.add("Det var supereffektivt! (x" + effectiveness + ")");
            } else if (effectiveness < 1.0) {
                log.add("Det var inte särskilt effektivt... (x" + effectiveness + ")");
            }
            log.add(label(defender) + " tar " + damage + " skada. (HP " + defender.getCurrentHp() + "/" + defender.getMaxHp() + ")");
            if (defender.hasFainted()) {
                log.add(label(defender) + " är besegrad!");
            }
        }

        attacker = defender;                                       // nästa tur tillhör motståndaren
        turnNumber++;
        return log;
    }

    private String label(Pokemon p) {
        return (p == player ? "Din " : "Vilda ") + p.getName();
    }
}