# Úvod #

Tento dokument popisuje pravidla a konvence používané při vývoji systému Kramerius4 a jeho modulů.

Pravidla vycházejí z obecně uznávaných pravidel stylu programování v jazyce Java, která jsou k popsána například zde :
  * [Java Code Conventions](http://java.sun.com/docs/codeconv/html/CodeConvTOC.doc.html)

Nový kód pro Kramerius musí navazovat na existující stávající kód Krameria 4 a být napsán v obdobném duchu.

# Obecná pravidla #

  * **Většinu pravidel v tomto dokumentu je možno porušit, pokud to v konkrétním případě přispěje k čitelnosti kódu.** Tento obecný dokument nemůže pokrýt všechny případy, se kterými se programátor může setkat.
  * Používejte pokud možno anglické názvy tříd, metod, proměnných atd.
  * Zdrojové soubory musí používat kódování **UTF-8** (pokud obsahují znaky mimo základní kódy ASCII)
  * **Nepoužívejte tabulátory**, konce stránek a další podobné speciální znaky.
  * Není nutno striktně dodržovat maximální délku řádků 80 znaků

# Jmenné konvence #

Jmenné konvence odppovídají standardu používanému ve zdrojových kódech JDK, v následujícím seznamu jsou uvedeny pouze některé významnější:

  * Názvy balíčků (package) jsou vždy malými písmeny
```
mypackage, com.company.application.ui
```

  * Názvy typů musí tvořit podstatná jména začínající velkým písmenem
```
Line, AudioSystem
```

  * Názvy proměnných začínají malým písmenem
```
line, audioSystem
```

  * Názvy konstant musí být velkými písmeny se slovy oddělenými podtržítky
```
MAX_ITERATIONS, COLOR_RED
```

  * Názvy metod jsou slovesa, začínají malým písmenem
```
getName(), computeTotalWidth()
```

  * Zkratky použité v názvech metod by neměly být psány velkými písmeny
```
exportHtmlSource(); // NE: exportHTMLSource();
openDvdPlayer();    // NE: openDVDPlayer();
```

  * V názvech **nepoužívejte přípony a předpony**, které by měly označovat jejich druh.
```

class Person
{
    private String name_; //NEPOUŽÍVAT
    private String m_Surname //NEPOUŽÍVAT
    ...
}
interface ICollection{ //NEPOUŽÍVAT
    ...
}
```

  * Proměnné s širší viditelností by měly mít dlouhá popisná jména, lokální a dočasné proměnné naopak jména krátká, případně i zkratky.

# Struktura a komentáře #

## Struktura kódu ##

  * Základní odsazení řádků v bloku je 4 mezery.
```
for (i = 0; i < max; i++) {
    a[i] = 0;
}
```


  * Otevírací závorky bloků mají být na stejném řádku, jako klíčové slovo nebo název třídy, metody apod., ke které blok patří:
```
while (!done) {
    doSomething();
    done = moreToDo();
}

class Rectangle extends Shape implements Cloneable, Serializable {
    ...
}

//TAKTO NE
while (!done)
{
    doSomething();
    done = moreToDo();
}

//TAKTO TAKÉ NE
class Rectangle extends Shape
    implements Cloneable, Serializable
{
    ...
}
```

  * Příkazy if-else mají mít následující tvar:
```
if (condition) {
    statements;
} else if (condition) {
    statements;
} else {
    statements;
}
```


## Komentáře ##

  * Minimalizujte použití komentářů. Nepřehledný kód nevylepšujte komentáři, ale přepište jej tak, aby byl samovysvětlující, především použitím vhodných názvů tříd, metod a proměnných.

  * Třídy, které tvoří součást veřejného API příslušné služby nebo modulu, mají mít všechny public a protected metody opatřeny komentáři pro javadoc.

  * Komentáře by měly být v angličtině.

# Logy a chybová hlášení #

  * Pro logování používejte standardní třídy z JDK balíčku java.util.logging.  Knihovny, které používají jiné systémy logování, přesměrujte na logování JDK, nejlépe pomocí knihovny slf4j.

  * Zásadně nepoužívejte logování pomocí System.out.print


# Issues a správa verzí #

  * Jakákoli změna, oprava nebo vylepšení v kódu musí mít přiřazen odpovídající záznam v databázi Issues  na googlecode. Při uzavření Issue musí být v jeho komentáři zapsaná čísla revizí kódu ve VCS, které daný issue řeší.