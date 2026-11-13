# Pokédex

Inlämningsuppgift i kursen Javaprogrammering, grund.

## Köra projektet

Kräver Java 21 och Maven.

Första gången (ingen sparad fil) fylls Pokédexen med sex Pokémon.

## Meny

1. Visa alla Pokémon
2. Lägg till Pokémon (guidad inmatning)
3. Redigera Pokémon (namn, HP, typ, lägg till/ta bort attacker)
4. Ta bort Pokémon
5. Spara till fil (pokedex.json)
6. Ladda från fil
7. Återställ till seed-data
8. Avsluta (sparar automatiskt)
9. Sök Pokémon på namn

## Exempel

    === POKÉDEX ===
    Välj (1-9): 1
    1. Pikachu      | Typ: ELECTRIC | HP: 35/35 | Attacker: 2

    Välj (1-9): 9
    Sök namn: mewtwo
    Hittade ingen Pokémon med namnet mewtwo

## Struktur

- `Pokemon`, `Attack`, `Type`: datamodellen
- `Validator`: alla regler för giltig data
- `InvalidPokemonException`, `PokemonNotFoundException`: egna exceptions
- `InputHelper`: säker inläsning från tangentbordet
- `JsonStorage`: spara och ladda med Jackson
- `SeedData`: fördefinierade Pokémon

## VG-krav jag försökt uppfylla

JSON med Jackson, och två egna exceptions.
