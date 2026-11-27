package org.example;

/**
 * Samlar alla regler för giltig data. Samma regler används när något skapas och när det redigeras.
 */
public class Validator {

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {                       // isBlank är true och för tomt + för bara mellanslag
            throw new InvalidPokemonException("Namnet får inte vara tomt.");   // Kasta felet: metoden avbryts här
        }
        if (name.trim().length() > 30) {                                   // Max 30 tecken (under)
            throw new InvalidPokemonException("Namnet får vara högst 30 tecken.");
        }
    }

    public static void validateHp(int hp) {
        if (hp < 1 || hp > 999) {                                          // || betyder ELLER: för litet eller för stort
            throw new InvalidPokemonException("HP måste vara mellan 1 och 999, fick: " + hp);
        }
    }

    public static void validateDamage(int damage) {
        if (damage < 1 || damage > 300) {
            throw new InvalidPokemonException("Skadan måste vara mellan 1 och 300, fick: " + damage);
        }
    }

    public static void validateAccuracy(int accuracy) {
        if (accuracy < 1 || accuracy > 100) {                              // Träffsäkerhet i procent
            throw new InvalidPokemonException("Träffsäkerheten måste vara mellan 1 och 100, fick: " + accuracy);
        }
    }

    public static void validateType(Type type) {
        if (type == null) {                                                // En typ måste vara vald
            throw new InvalidPokemonException("Typen får inte saknas.");
        }
    }

    public static void validateAttackCount(int count) {
        if (count < 1 || count > 4) {                                      // En Pokémon har 1 till 4 attacker
            throw new InvalidPokemonException("En Pokémon måste ha 1 till 4 attacker, har: " + count);
        }
    }

    public static void validateSpeed(int speed) {
        if (speed < 1 || speed > 200) {                                    // Fart avgör vem som anfaller först
            throw new InvalidPokemonException("Farten måste vara mellan 1 och 200, fick: " + speed);
        }
    }

    // Kontrollerar en hel Pokémon, t.ex. efter laddning från fil där konstruktorn aldrig körts
    public static void validatePokemon(Pokemon p) {
        if (p == null) {
            throw new InvalidPokemonException("Tom post i filen.");
        }
        validateName(p.getName());
        validateType(p.getType());
        validateHp(p.getMaxHp());
        validateSpeed(p.getSpeed());
        if (p.getCurrentHp() < 0 || p.getCurrentHp() > p.getMaxHp()) {
            throw new InvalidPokemonException("Aktuellt HP måste vara mellan 0 och max HP för " + p.getName());
        }
        validateAttackCount(p.getAttacks().size());
        for (Attack a : p.getAttacks()) {
            validateName(a.getName());
            validateDamage(a.getBaseDamage());
            validateAccuracy(a.getAccuracy());
            validateType(a.getType());
        }
    }
}
