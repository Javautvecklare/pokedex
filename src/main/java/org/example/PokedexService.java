package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Hanterar samlingen av Pokémon (skapa, läsa, ta bort, söka).
 * Ingen inmatning eller utskrift här, det sköter PokedexMenu.
 */
public class PokedexService {
    private ArrayList<Pokemon> pokemons = new ArrayList<>();

    // Skrivskyddad vy: ingen utanför klassen kan lägga till eller ta bort Pokémon bakom ryggen på servicen
    public List<Pokemon> getAll() {
        return Collections.unmodifiableList(pokemons);
    }

    public boolean isEmpty() {
        return pokemons.isEmpty();
    }

    public int size() {
        return pokemons.size();
    }

    public Pokemon get(int index) {
        checkIndex(index);
        return pokemons.get(index);
    }

    public void add(Pokemon pokemon) {
        if (pokemon == null) {
            throw new InvalidPokemonException("Pokémon får inte saknas.");
        }
        pokemons.add(pokemon);
    }

    public Pokemon remove(int index) {
        checkIndex(index);
        return pokemons.remove(index);
    }

    public Pokemon findByName(String name) {
        for (Pokemon p : pokemons) {
            if (p.getName().toUpperCase().equals(name.toUpperCase())) {   // toUpperCase så att "pikachu" och "Pikachu" räknas som samma
                return p;
            }
        }
        throw new PokemonNotFoundException("Hittade ingen Pokémon med namnet " + name);
    }

    // Byter ut hela samlingen, t.ex. vid laddning från fil eller återställning till seed-data
    public void replaceAll(List<Pokemon> newPokemons) {
        pokemons = new ArrayList<>(newPokemons);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= pokemons.size()) {
            throw new PokemonNotFoundException("Ingen Pokémon på plats " + (index + 1));
        }
    }
}