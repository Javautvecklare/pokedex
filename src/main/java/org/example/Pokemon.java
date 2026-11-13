package org.example;

import java.util.ArrayList;

/**
 * En Pokémon med namn, typ, HP och 1 till 4 attacker.
 */
public class Pokemon {
    public String name;
    public Type type;
    public int maxHp;
    public int currentHp;
    public ArrayList<Attack> attacks = new ArrayList<>();   // börjar tom, attacker läggs till efteråt

    public Pokemon() {
    }
    // Alla regler kontrolleras före tilldelningen, så att ingen ogiltig Pokémon kan skapas
    public Pokemon(String name, Type type, int maxHp) {
        Validator.validateName(name);
        Validator.validateType(type);
        Validator.validateHp(maxHp);
        this.name = name.trim();
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = maxHp;        // en ny Pokémon ska börja med full HP
    }
}
