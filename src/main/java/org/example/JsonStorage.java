package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Sparar och laddar Pokédexen som JSON. Fel hanteras här så att programmet aldrig kraschar.
 */
public class JsonStorage {

    public static void saveJson(Path path, List<Pokemon> pokedex) {
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
            JsonNode root = mapper.readTree(path.toFile());          // läser filen som ett träd först, så en trasig post inte fäller hela filen
            if (root == null || !root.isArray()) {                   // filen ska innehålla en lista, inte t.ex. bara ordet null
                System.out.println("Kunde inte ladda filen (fel format).");
                return result;
            }
            for (JsonNode node : root) {
                try {
                    Pokemon p = mapper.treeToValue(node, Pokemon.class);   // setters validerar varje värde
                    Validator.validatePokemon(p);                    // och helheten kontrolleras, filen kan ha redigerats för hand
                    result.add(p);
                } catch (IOException | InvalidPokemonException e) {
                    System.out.println("Hoppade över en ogiltig post i filen: " + reason(e));
                }
            }
        } catch (IOException e) {                                    // korrupt fil
            System.out.println("Kunde inte ladda filen (korrupt eller fel format).");
        }
        return result;
    }
    
    public static void saveStats(Path path, Stats stats) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), stats);
        } catch (IOException e) {
            System.out.println("Kunde inte spara statistiken: " + e.getMessage());
        }
    }

    // Saknad fil är inget fel (inga strider spelade än). Korrupt fil ger tom statistik i stället för en krasch.
    public static Stats loadStats(Path path) {
        if (!Files.exists(path)) {
            return new Stats();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Stats stats = mapper.readValue(path.toFile(), Stats.class);
            return stats == null ? new Stats() : stats;            // filen kan innehålla bara ordet null
        } catch (IOException e) {
            System.out.println("Kunde inte ladda statistiken (korrupt eller fel format). Börjar om med tom statistik.");
            return new Stats();
        }
    }

    // Jackson lindar in våra egna felmeddelanden, så vi plockar fram det ursprungliga om det finns
    private static String reason(Exception e) {
        if (e instanceof InvalidPokemonException) {
            return e.getMessage();
        }
        if (e.getCause() instanceof InvalidPokemonException) {
            return e.getCause().getMessage();
        }
        return "fel format";
    }
}