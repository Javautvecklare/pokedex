package org.example;

import java.util.ArrayList;

/**
 * Fördefinierad data: spelarens startsamling (första körningen och vid återställning)
 * och poolen av vilda Pokémon som datorn väljer motståndare från.
 */
public class SeedData {

    public static ArrayList<Pokemon> createSeed() {
        ArrayList<Pokemon> list = new ArrayList<>();

        Pokemon pikachu = new Pokemon("Pikachu", Type.ELECTRIC, 35, 90);
        pikachu.addAttack(new Attack("Thunderbolt", 40, 100, Type.ELECTRIC));
        pikachu.addAttack(new Attack("Quick Attack", 20, 100, Type.NORMAL));
        list.add(pikachu);

        Pokemon charmander = new Pokemon("Charmander", Type.FIRE, 39, 65);
        charmander.addAttack(new Attack("Ember", 40, 100, Type.FIRE));
        charmander.addAttack(new Attack("Scratch", 40, 100, Type.NORMAL));
        list.add(charmander);

        Pokemon squirtle = new Pokemon("Squirtle", Type.WATER, 44, 43);
        squirtle.addAttack(new Attack("Water Gun", 40, 100, Type.WATER));
        squirtle.addAttack(new Attack("Tackle", 40, 100, Type.NORMAL));
        list.add(squirtle);

        Pokemon bulbasaur = new Pokemon("Bulbasaur", Type.GRASS, 45, 45);
        bulbasaur.addAttack(new Attack("Vine Whip", 45, 100, Type.GRASS));
        bulbasaur.addAttack(new Attack("Tackle", 40, 100, Type.NORMAL));
        list.add(bulbasaur);

        Pokemon eevee = new Pokemon("Eevee", Type.NORMAL, 55, 55);
        eevee.addAttack(new Attack("Tackle", 40, 100, Type.NORMAL));
        eevee.addAttack(new Attack("Quick Attack", 40, 100, Type.NORMAL));
        list.add(eevee);

        Pokemon psyduck = new Pokemon("Psyduck", Type.WATER, 50, 55);
        psyduck.addAttack(new Attack("Water Gun", 40, 100, Type.WATER));
        psyduck.addAttack(new Attack("Scratch", 40, 100, Type.NORMAL));
        list.add(psyduck);

        return list;       // metoden lämnar tillbaka hela listan med sex Pokémon
    }

    // De vilda Pokémon har lite mer HP och svagare attacker, så att striderna varar några turer
    public static ArrayList<Pokemon> createWildPool() {
        ArrayList<Pokemon> list = new ArrayList<>();

        Pokemon rattata = new Pokemon("Rattata", Type.NORMAL, 50, 72);
        rattata.addAttack(new Attack("Tackle", 30, 95, Type.NORMAL));
        rattata.addAttack(new Attack("Quick Attack", 25, 100, Type.NORMAL));
        list.add(rattata);

        Pokemon vulpix = new Pokemon("Vulpix", Type.FIRE, 55, 65);
        vulpix.addAttack(new Attack("Ember", 35, 100, Type.FIRE));
        vulpix.addAttack(new Attack("Quick Attack", 25, 100, Type.NORMAL));
        list.add(vulpix);

        Pokemon oddish = new Pokemon("Oddish", Type.GRASS, 60, 30);
        oddish.addAttack(new Attack("Absorb", 30, 100, Type.GRASS));
        oddish.addAttack(new Attack("Tackle", 30, 95, Type.NORMAL));
        list.add(oddish);

        Pokemon poliwag = new Pokemon("Poliwag", Type.WATER, 55, 90);
        poliwag.addAttack(new Attack("Water Gun", 35, 100, Type.WATER));
        poliwag.addAttack(new Attack("Bubble", 25, 100, Type.WATER));
        list.add(poliwag);

        Pokemon voltorb = new Pokemon("Voltorb", Type.ELECTRIC, 50, 100);
        voltorb.addAttack(new Attack("Thunder Shock", 35, 100, Type.ELECTRIC));
        voltorb.addAttack(new Attack("Tackle", 30, 90, Type.NORMAL));
        list.add(voltorb);

        Pokemon meowth = new Pokemon("Meowth", Type.NORMAL, 55, 90);
        meowth.addAttack(new Attack("Scratch", 30, 100, Type.NORMAL));
        meowth.addAttack(new Attack("Bite", 35, 90, Type.NORMAL));
        list.add(meowth);

        return list;
    }
}