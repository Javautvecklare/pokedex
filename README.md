# Pokémon Battler

Inlämning 2 i kursen Java grund. Projektet bygger vidare på min Pokédex från inlämning 1
och har nu en turbaserad, textbaserad strid mot en datorstyrd motståndare.

Man hanterar sin samling av Pokémon (visa, lägga till, redigera, ta bort, söka), väljer en Pokémon och slåss mot en
slumpvald vild Pokémon. Striden har typeffektivitet, träffsäkerhet, slumpfaktor och kritiska träffar.
Stridsresultaten sparas mellan sessioner.

## Köra projektet

Kräver Java 21 och Maven.

- **IntelliJ:** öppna projektmappen (pom.xml), kör `org.example.Main`.
- **Maven:** `mvn compile exec:java -Dexec.mainClass=org.example.Main`

Första gången (ingen sparad fil) fylls samlingen med sex Pokémon. Samlingen sparas i `pokedex.json` och
stridsstatistiken i `stats.json`, båda i mappen där programmet startas. Filerna ligger i `.gitignore`.

## Meny

| Val | Funktion |
|---|---|
| 1 | Visa alla Pokémon |
| 2 | Lägg till Pokémon (guidad inmatning) |
| 3 | Redigera Pokémon (namn, HP, typ, attacker, läk till full HP) |
| 4 | Ta bort Pokémon |
| 5 | Spara till fil |
| 6 | Ladda från fil |
| 7 | Återställ till seed-data |
| 8 | Avsluta (sparar automatiskt) |
| 9 | Sök Pokémon på namn |
| 10 | Starta strid |
| 11 | Visa stridsresultat |

## Så går en strid till

1. Spelaren väljer en Pokémon ur sin samling. Datorn drar en slumpvis motståndare ur en pool med sex vilda Pokémon.
2. **Turordning:** den Pokémon med högst *fart* anfaller först. Vid lika fart börjar spelaren. Sedan turas de om.
3. Spelaren väljer attack via en meny. Datorn väljer en slumpvis attack bland sina egna.
4. Träffsäkerheten avgör om attacken träffar. Vid träff räknas skadan ut enligt formeln nedan och HP minskar.
5. När en Pokémon når 0 HP är den besegrad och kan inte attackera mer. Striden slutar och resultatet visas.
6. Varje tur skrivs ut i en battle-logg (vem som attackerar, miss, kritisk träff, effektivitet, skada, kvarvarande HP).

Striden spelas på kopior av Pokémon, så spelarens riktiga Pokémon tappar aldrig HP och inget ändras om en strid avbryts.

## Skadeberäkning

```
träff:   slumptal 0-99 är lägre än attackens träffsäkerhet (i procent)
skada  = round( basskada × typeffektivitet × slumpfaktor × kritisk )
```

| Del | Värde |
|---|---|
| basskada | attackens skada (1-300) |
| typeffektivitet | från typtabellen: 2x, 0,5x eller 1x |
| slumpfaktor | ett slumptal mellan 0,85 och 1,00 |
| kritisk | 2 vid kritisk träff (chans 1 på 16), annars 1 |

En träff gör alltid minst 1 i skada.

Typtabellen (allt annat är 1x, Normal är alltid 1x):

| Anfallande | Försvarande | Effekt |
|---|---|---|
| Fire | Grass | 2x |
| Water | Fire | 2x |
| Grass | Water | 2x |
| Electric | Water | 2x |
| Fire | Water | 0,5x |
| Water | Grass | 0,5x |
| Grass | Fire | 0,5x |

Exempel: Ember (basskada 40) mot Grass, slumpfaktor 1,00 och kritisk träff: 40 × 2 × 1,00 × 2 = 160.

## OOP-val

**Inkapsling.** Alla fält i `Pokemon` och `Attack` är `private`. De ändras bara genom settrar som validerar via
`Validator`, så ingen kod (inte ens under en strid) kan sätta ett ogiltigt värde som negativt HP. Listan med attacker
lämnas ut som en skrivskyddad vy, så en femte attack kan inte läggas till utan att gå förbi reglerna.

**Interface och polymorfism.** `AttackChooser` har metoden `choose(...)`. Två klasser implementerar den:
`PlayerAttackChooser` (spelaren väljer via meny) och `RandomAttackChooser` (datorn slumpar). `Battle` anropar bara
`choose()` och vet inte vem som svarar. Därför är striden samma kod oavsett vem som spelar, och en ny sorts motståndare
(t.ex. en som alltid väljer den starkaste attacken) går att lägga till utan att röra `Battle`.

**Arv.** `InvalidPokemonException` och `PokemonNotFoundException` ärver `RuntimeException`. Jag valde att *inte* göra
en arvshierarki av Pokémon (`FirePokemon`, `WaterPokemon`...). Typ är data, inte beteende: skillnaden mellan typerna är
bara siffrorna i typtabellen, så en `enum Type` och klassen `TypeChart` räcker och är enklare.

**Separation of concerns.** Varje klass har en uppgift:

| Roll | Klasser |
|---|---|
| Datamodell | `Pokemon`, `Attack`, `Type`, `Stats`, `PokemonRecord` |
| Regler och validering | `Validator`, `InvalidPokemonException`, `PokemonNotFoundException` |
| Logik | `PokedexService` (samlingen), `Battle` (striden), `DamageCalculator`, `TypeChart` |
| Val av attack | `AttackChooser`, `PlayerAttackChooser`, `RandomAttackChooser` |
| Lagring | `JsonStorage` (Jackson), `SeedData` (startdata och vilda Pokémon) |
| Användargränssnitt | `Main` (startar), `PokedexMenu` (menyn), `InputHelper` (säker inläsning) |

`Battle`, `PokedexService` och `Stats` skriver aldrig ut något och läser aldrig tangentbordet. All inmatning och
utskrift ligger i menyn, så logiken går att testa och byta gränssnitt på. `Battle` lämnar tillbaka loggraderna som en
lista som menyn skriver ut.

**Slumpen** går genom ett `Random` som skickas in i `DamageCalculator`, vilket gör beräkningen testbar med förutsägbara tal.

## VG-krav jag försökt uppfylla

- **Typeffektivitetstabell** med alla relationer i uppgiften (`TypeChart`).
- **JSON med Jackson** för all persistens: `pokedex.json` och `stats.json`.
- **Kritiska träffar** med chans 1/16 som dubblar skadan, och som visas i loggen.
- **Persistent statistik** mellan sessioner (menyval 11): totala vinster och förluster, vinst/förlust per Pokémon
  (antal, vinstandel och kvot), mest använda attack och mest använda Pokémon.
- **Polymorfism** via `AttackChooser`, se OOP-val.
- Två egna exceptions som används meningsfullt (från inlämning 1).

## Felhantering och kraschtest

Programmet ska aldrig krascha. Det här har jag testat med skriptad inmatning:

| Test | Resultat |
|---|---|
| Starta strid utan att äga någon Pokémon | Meddelande, tillbaka till menyn |
| Pokémon med 0 attacker | Laddas inte från fil, och `Battle` vägrar starta om det ändå sker |
| Pokémon med 0 HP | "Striden kunde inte starta", tips om att läka den |
| Ogiltigt attackindex (-1, 99, "abc", tom rad) | Frågar om tills svaret är giltigt |
| Korrupt JSON-fil | Meddelande, startar med seed-data |
| Saknad JSON-fil | Startar med seed-data / tom statistik |
| Enstaka ogiltig post i `pokedex.json` | Posten hoppas över, resten laddas |
| Avbruten inmatning (Ctrl+D) mitt i en strid | Programmet avslutas kontrollerat, ingen halv strid sparas |
| Visa statistik utan tidigare strider | "Inga strider har spelats än" |

## Kända begränsningar

- Striden är en mot en. Inga statuseffekter, byten eller flera Pokémon per sida.
- Statistiken kopplas till Pokémons namn. Byter man namn på en Pokémon börjar dess historik om.
- Vid lika antal strider/användningar i statistiken visas den som kommer först i bokstavsordning.
- De vilda Pokémon ligger i koden (`SeedData`), inte i en fil.
- Samlingen tillåter flera Pokémon med samma namn.
- Gamla `pokedex.json` från inlämning 1 saknar fart. De laddas ändå och får farten 50.
- Inga enhetstester än (JUnit hann jag tyvärr inte med). Skadeberäkningen är gjord så att den går att testa.