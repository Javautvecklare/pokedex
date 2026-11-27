package org.example;

import java.util.ArrayList;

/**
 * Fördefinierad data som används första gången programmet körs o vid återställning
 */
public class SeedData {

    public static ArrayList<Pokemon> createSeed() {
        ArrayList<Pokemon> list = new ArrayList<>();

        Pokemon pikachu = new Pokemon("Pikachu", Type.ELECTRIC, 35);
        pikachu.addAttack(new Attack("Thunderbolt", 40, 100, Type.ELECTRIC));
        pikachu.addAttack(new Attack("Quick Attack", 20, 100, Type.NORMAL));
        list.add(pikachu);

        Pokemon charmander = new Pokemon("Charmander", Type.FIRE, 39);
        charmander.addAttack(new Attack("Ember", 40, 100, Type.FIRE));
        charmander.addAttack(new Attack("Scratch", 40, 100, Type.NORMAL));
        list.add(charmander);

        Pokemon squirtle = new Pokemon("Squirtle", Type.WATER, 44);
        squirtle.addAttack(new Attack("Water Gun", 40, 100, Type.WATER));
        squirtle.addAttack(new Attack("Tackle", 40, 100, Type.NORMAL));
        list.add(squirtle);

        Pokemon bulbasaur = new Pokemon("Bulbasaur", Type.GRASS, 45);
        bulbasaur.addAttack(new Attack("Vine Whip", 45, 100, Type.GRASS));
        bulbasaur.addAttack(new Attack("Tackle", 40, 100, Type.NORMAL));
        list.add(bulbasaur);

        Pokemon eevee = new Pokemon("Eevee", Type.NORMAL, 55);
        eevee.addAttack(new Attack("Tackle", 40, 100, Type.NORMAL));
        eevee.addAttack(new Attack("Quick Attack", 40, 100, Type.NORMAL));
        list.add(eevee);

        Pokemon psyduck = new Pokemon("Psyduck", Type.WATER, 50);
        psyduck.addAttack(new Attack("Water Gun", 40, 100, Type.WATER));
        psyduck.addAttack(new Attack("Scratch", 40, 100, Type.NORMAL));
        list.add(psyduck);

        return list;       // metoden lämnar tillbaka hela listan med sex Pokémon
    }
}
