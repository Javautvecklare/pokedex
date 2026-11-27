package org.example;

/**
 * Bestämmer vilken attack en Pokémon använder i sin tur.
 * Striden (Battle) bryr sig inte om vem som väljer: spelaren via meny eller datorn via slump.
 * Det är polymorfism: samma anrop choose(), olika beteende beroende på vilken klass som finns bakom.
 */
public interface AttackChooser {
    Attack choose(Pokemon self, Pokemon opponent);
}