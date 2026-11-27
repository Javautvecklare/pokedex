package org.example;

import java.util.HashMap;

/**
 * Typeffektivitetstabellen: hur mycket skadan multipliceras när en attack av en typ träffar en Pokémon av en annan typ.
 * Relationer som inte finns i tabellen (tex alla med Normal) ger 1x.
 */
public class TypeChart {
    // Yttre nyckel = anfallande typ, inre nyckel = försvarande typ, värdet = multiplikator
    private final HashMap<Type, HashMap<Type, Double>> table = new HashMap<>();

    public TypeChart() {
        add(Type.FIRE, Type.GRASS, 2.0);
        add(Type.WATER, Type.FIRE, 2.0);
        add(Type.GRASS, Type.WATER, 2.0);
        add(Type.ELECTRIC, Type.WATER, 2.0);
        add(Type.FIRE, Type.WATER, 0.5);
        add(Type.WATER, Type.GRASS, 0.5);
        add(Type.GRASS, Type.FIRE, 0.5);
    }

    private void add(Type attacker, Type defender, double multiplier) {
        if (!table.containsKey(attacker)) {                        // första relationen för den här anfallande typen
            table.put(attacker, new HashMap<>());
        }
        table.get(attacker).put(defender, multiplier);
    }

    public double getMultiplier(Type attacker, Type defender) {
        HashMap<Type, Double> row = table.get(attacker);
        if (row == null || !row.containsKey(defender)) {           // ingen särskild relation betyder normal skada
            return 1.0;
        }
        return row.get(defender);
    }
}