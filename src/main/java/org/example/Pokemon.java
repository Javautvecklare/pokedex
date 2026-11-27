package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * En Pokémon med namn, typ, HP, fart och 1 till 4 attacker.
 * Alla fält är private: det enda sättet att ändra dem är via metoderna nedan,
 * och de validerar först. Därför kan ingen kod (inte ens under en strid) sätta ogiltiga värden.
 */
public class Pokemon {
    private String name;
    private Type type;
    private int maxHp;
    private int currentHp;
    private int speed = 50;                                  // startvärde så att gamla sparfiler utan fart ändå går att ladda
    private ArrayList<Attack> attacks = new ArrayList<>();   // börjar tom, attacker läggs till efteråt

    // Jackson skapar först ett tomt objekt och fyller sedan i värdena via settrarna
    public Pokemon() {
    }

    public Pokemon(String name, Type type, int maxHp) {
        this(name, type, maxHp, 50);
    }

    // Konstruktorn använder samma validerade settrar som övrig kod, så reglerna finns på ett enda ställe
    public Pokemon(String name, Type type, int maxHp, int speed) {
        setName(name);
        setType(type);
        setMaxHp(maxHp);
        setSpeed(speed);
        this.currentHp = maxHp;        // en ny Pokémon ska börja med full HP
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Validator.validateName(name);
        this.name = name.trim();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        Validator.validateType(type);
        this.type = type;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        Validator.validateHp(maxHp);
        this.maxHp = maxHp;
        if (currentHp > maxHp) {                             // aktuellt HP får aldrig vara högre än max
            currentHp = maxHp;
        }
    }

    public int getCurrentHp() {
        return currentHp;
    }

    // Gränsen mot maxHp kontrolleras inte här, eftersom Jackson kan sätta fälten i valfri ordning.
    // Validator.validatePokemon kontrollerar helheten efter laddning.
    public void setCurrentHp(int currentHp) {
        if (currentHp < 0 || currentHp > 999) {
            throw new InvalidPokemonException("Aktuellt HP måste vara mellan 0 och 999, fick: " + currentHp);
        }
        this.currentHp = currentHp;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        Validator.validateSpeed(speed);
        this.speed = speed;
    }

    // En skrivskyddad vy: utomstående kan titta på attackerna men inte lägga till en femte och köra förbi reglerna
    public List<Attack> getAttacks() {
        return Collections.unmodifiableList(attacks);
    }

    // Används av Jackson vid laddning: hela listan kontrolleras innan den accepteras
    public void setAttacks(List<Attack> newAttacks) {
        if (newAttacks == null) {
            throw new InvalidPokemonException("Attacker saknas.");
        }
        Validator.validateAttackCount(newAttacks.size());
        for (Attack a : newAttacks) {
            if (a == null) {
                throw new InvalidPokemonException("Tom attack i listan.");
            }
        }
        this.attacks = new ArrayList<>(newAttacks);
    }

    public void addAttack(Attack attack) {
        if (attack == null) {
            throw new InvalidPokemonException("Attacken får inte saknas.");
        }
        if (attacks.size() >= 4) {                           // en Pokémon har högst 4 attacker
            throw new InvalidPokemonException("En Pokémon kan ha högst 4 attacker.");
        }
        attacks.add(attack);
    }

    public void removeAttack(int index) {
        if (attacks.size() <= 1) {                           // en Pokémon måste ha minst 1 attack
            throw new InvalidPokemonException("En Pokémon måste ha minst 1 attack.");
        }
        if (index < 0 || index >= attacks.size()) {
            throw new InvalidPokemonException("Ingen attack på plats " + (index + 1));
        }
        attacks.remove(index);
    }

    // HP kan aldrig bli lägre än 0, så en besegrad Pokémon har alltid exakt 0
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new InvalidPokemonException("Skadan kan inte vara negativ.");
        }
        currentHp = Math.max(0, currentHp - damage);
    }

    // Heter inte isXxx med flit: Jackson skulle annars tolka det som ett fält som ska sparas i filen
    public boolean hasFainted() {
        return currentHp <= 0;
    }

    // En kopia att strida med, så att den riktiga Pokémon i samlingen inte skadas om striden avbryts
    public Pokemon copy() {
        Pokemon copy = new Pokemon(name, type, maxHp, speed);
        copy.setCurrentHp(currentHp);
        for (Attack a : attacks) {
            copy.addAttack(a);
        }
        return copy;
    }
}
