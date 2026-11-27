package org.example;

/**
 * En attack som en Pokémon kan använda.
 * Fälten är private och ändras bara via validerade settrar, så ingen ogiltig attack kan finnas.
 */
public class Attack {
    private String name;
    private int baseDamage;
    private int accuracy;     // i procent
    private Type type;        // samma enum som Pokémon, så att typerna aldrig kan skilja sig åt

    // Jackson skapar först ett tomt objekt och fyller sedan i värdena via settrarna
    public Attack() {
    }

    public Attack(String name, int baseDamage, int accuracy, Type type) {
        setName(name);
        setBaseDamage(baseDamage);
        setAccuracy(accuracy);
        setType(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Validator.validateName(name);
        this.name = name.trim();
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(int baseDamage) {
        Validator.validateDamage(baseDamage);
        this.baseDamage = baseDamage;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        Validator.validateAccuracy(accuracy);
        this.accuracy = accuracy;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        Validator.validateType(type);
        this.type = type;
    }
}
