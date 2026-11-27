package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Statistik som sparas mellan sessioner (stats.json): vinster och förluster totalt och per Pokémon,
 * samt hur många gånger spelaren använt varje attack.
 * Metoderna som räknar ut saker heter inte getXxx med flit, så att Jackson inte försöker spara dem i filen.
 */
public class Stats {
    private int wins;
    private int losses;
    private HashMap<String, PokemonRecord> pokemonRecords = new HashMap<>();
    private HashMap<String, Integer> attackUses = new HashMap<>();

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = Math.max(0, wins);
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = Math.max(0, losses);
    }

    public HashMap<String, PokemonRecord> getPokemonRecords() {
        return pokemonRecords;
    }

    // Tomma eller trasiga poster i en handredigerad fil släpps igenom inte, så resten av koden slipper null-kontroller
    public void setPokemonRecords(HashMap<String, PokemonRecord> records) {
        pokemonRecords = new HashMap<>();
        if (records != null) {
            for (Map.Entry<String, PokemonRecord> entry : records.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    pokemonRecords.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public HashMap<String, Integer> getAttackUses() {
        return attackUses;
    }

    public void setAttackUses(HashMap<String, Integer> uses) {
        attackUses = new HashMap<>();
        if (uses != null) {
            for (Map.Entry<String, Integer> entry : uses.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0) {
                    attackUses.put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    // Registrerar en avslutad strid: totalt, för spelarens Pokémon och vilka attacker som användes
    public void recordBattle(String pokemonName, boolean won, List<String> attacksUsed) {
        PokemonRecord record = pokemonRecords.get(pokemonName);
        if (record == null) {                                      // första striden för den här Pokémon
            record = new PokemonRecord();
            pokemonRecords.put(pokemonName, record);
        }
        if (won) {
            wins++;
            record.addWin();
        } else {
            losses++;
            record.addLoss();
        }
        for (String attack : attacksUsed) {
            attackUses.put(attack, attackUses.getOrDefault(attack, 0) + 1);
        }
    }

    public int totalBattles() {
        return wins + losses;
    }

    // Pokémonnamnen i bokstavsordning, så att utskriften alltid ser likadan ut
    public List<String> pokemonNames() {
        ArrayList<String> names = new ArrayList<>(pokemonRecords.keySet());
        Collections.sort(names);
        return names;
    }

    public PokemonRecord recordFor(String pokemonName) {
        return pokemonRecords.get(pokemonName);
    }

    public int usesOf(String attackName) {
        return attackUses.getOrDefault(attackName, 0);
    }

    // Den Pokémon som strider flest gånger (vinster + förluster). Null om ingen strid spelats.
    public String mostUsedPokemon() {
        String best = null;
        int bestCount = 0;
        for (Map.Entry<String, PokemonRecord> entry : pokemonRecords.entrySet()) {
            int count = entry.getValue().battles();
            if (count > bestCount || (count == bestCount && best != null && entry.getKey().compareTo(best) < 0)) {   // vid lika: första i bokstavsordning
                best = entry.getKey();
                bestCount = count;
            }
        }
        return best;
    }

    // Den attack spelaren använt flest gånger. Null om ingen attack använts.
    public String mostUsedAttack() {
        String best = null;
        int bestCount = 0;
        for (Map.Entry<String, Integer> entry : attackUses.entrySet()) {
            int count = entry.getValue();
            if (count > bestCount || (count == bestCount && best != null && entry.getKey().compareTo(best) < 0)) {
                best = entry.getKey();
                bestCount = count;
            }
        }
        return best;
    }
}