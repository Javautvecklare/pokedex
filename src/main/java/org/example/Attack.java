package org.example;

/**
 * En attack som en Pokémon kan använda.
 */
public class Attack {
    // Klassens FÄLT
    public String name;
    public int baseDamage;
    public int accuracy;     // i procent
    public Type type;        // samma enum som Pokémon, så att typerna aldrig kan skilja sig åt

    // Konstruktorn kräver alla värden, så att en attack aldrig skapas halvfärdig. Nu även med kontroll före tilldelning, så ingen ogiltig attack kan skapas.
    public Attack(String name, int baseDamage, int accuracy, Type type) {   // Konstruktorns rubrik. Parametrar innanför parantensen
        Validator.validateName(name);
        Validator.validateDamage(baseDamage);
        Validator.validateAccuracy(accuracy);
        Validator.validateType(type);
        this.name = name.trim();                  // this. behövs eftersom parametern heter samma som fältet. trim() tar bort all leading och trailing space.
        this.baseDamage = baseDamage;
        this.accuracy = accuracy;
        this.type = type;
    }
}
