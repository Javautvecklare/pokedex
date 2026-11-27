package org.example;

/**
 * Vinster och förluster för en enskild Pokémon. Sparas i stats.json som en del av Stats.
 */
public class PokemonRecord {
    private int wins;
    private int losses;

    // Jackson skapar först ett tomt objekt och fyller sedan i värdena via settrarna
    public PokemonRecord() {
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = Math.max(0, wins);                             // en handredigerad fil ska aldrig ge negativa tal
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = Math.max(0, losses);
    }

    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }

    // Heter inte getXxx med flit, så att Jackson inte försöker spara det i filen
    public int battles() {
        return wins + losses;
    }
}