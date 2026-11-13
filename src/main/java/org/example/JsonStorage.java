package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Sparar och laddar Pokédexen som JSON. Fel hanteras här så att programmet aldrig kraschar.
 */
public class JsonStorage {

    public static void saveJson(Path path, ArrayList<Pokemon> pokedex) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), pokedex);   // pretty printer så att filen går att läsa
            System.out.println("Sparade " + pokedex.size() + " Pokémon till " + path);
        } catch (IOException e) {                                    // disk full, saknad skrivrätt och liknande går inte att förutse
            System.out.println("Kunde inte spara: " + e.getMessage());
        }
    }

    public static ArrayList<Pokemon> loadJson(Path path) {
        ArrayList<Pokemon> result = new ArrayList<>();
        if (!Files.exists(path)) {                                   // första körningen: ingen fil är inget fel
            return result;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Pokemon[] fromFile = mapper.readValue(path.toFile(), Pokemon[].class);
            if (fromFile == null) {                                  // filen kan innehålla bara ordet null
                return result;
            }
            for (Pokemon p : fromFile) {
                try {
                    Validator.validatePokemon(p);                    // filen kan ha redigerats för hand, så inget litas på
                    result.add(p);
                } catch (InvalidPokemonException e) {
                    System.out.println("Hoppade över en ogiltig post i filen: " + e.getMessage());
                }
            }
        } catch (IOException e) {                                    // korrupt fil eller fel format
            System.out.println("Kunde inte ladda filen (korrupt eller fel format).");
        }
        return result;
    }
}
