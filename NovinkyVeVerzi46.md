

# Replikace K4 #
Byla přidána možnost replikace mezi systémy K4. Celý popis replikace je vidět zde [Replikace](Replikace.md)

# Remote API #
Bylo vytvořeno nové API nyní pokytující možnosti replikace nebo správu procesů [RemoteAPI](RemoteAPI.md).

# Rozšíření editace práv o správu parametrů #
V hlavním menu přibyla položka pro editaci dodatečných podmínek. Po spuštění se objeví dialog
se seznamem všech parametrů s tlačítky pro přejmenování, smazání nebo editaci práv.

![http://kramerius.googlecode.com/svn/wiki/features_46/rights-criterium-params-editor.png](http://kramerius.googlecode.com/svn/wiki/features_46/rights-criterium-params-editor.png)

Zakázané tlačítko pro smazání parametrů znamená, že jsou použity v některém právu a je nejdříve
nutné učinit tyto parametry nepoužívanými. K tomu slouží tlačítko `Editace práv`. Po stisku se objeví standardní dialog pro editaci
práv (popisovaný například zde [PravaGUI](PravaGUI.md)), vybrané jsou pouze ty objekty, které mají nejaké právo svázané s daným parametrem. Stejně je tomu u chráněných akcí.


# Parametrizace procesu pomocí web gui #
Od verze 4.6 je možné parametry procesům předávat přes webové rozhraní. Více zde [Processes](Processes.md). Změněny byly následující procesy:

  * Import
  * Export PDF

## Import ##
U importu byly převedeny všechny předávané prametry do jednoduchých textových polí. Pouze položka pro importní adresář je opatřena tlačítkem pro výběr
adresáře pomocí stromové struktury.

![http://kramerius.googlecode.com/svn/wiki/features_46/parametrized-import.png](http://kramerius.googlecode.com/svn/wiki/features_46/parametrized-import.png)

![http://kramerius.googlecode.com/svn/wiki/features_46/parametrized-import-browse.png](http://kramerius.googlecode.com/svn/wiki/features_46/parametrized-import-browse.png)

## Export PDF ##
Položky Export do PDF DVD a Export do PDF CD byly sloučeny do jedné Expoport do PDF s tím, že po zobrazení se objeví dialog s volbou média a možnosti volby
výstupního adresáře.

![http://kramerius.googlecode.com/svn/wiki/features_46/parametrized-static-export.png](http://kramerius.googlecode.com/svn/wiki/features_46/parametrized-static-export.png)

# Procesy - dávkový stav, změny #
V rámci řešení [Issue 331](https://code.google.com/p/kramerius/issues/detail?id=331) byl přidán dávkový stav reprezentující stav spouštěné dávky, pokud nějaká je.
V tabulce je zobrazen jako samostatný sloupec. Dále byly přidány navigační tlačítka pro rychlý posun na
určité strany [Issue 363](https://code.google.com/p/kramerius/issues/detail?id=363), Stavy RUNNING a BATCH\_STARTED jsou nyní zeleně, stav FAILED a BATCH\_FAILED jsou červeně.
Přibyl sloupec informující o datu skončení procesu a v jeho tooltipu se objeví i jak dlouho proces běžel.

![http://kramerius.googlecode.com/svn/wiki/features_46/processes.png](http://kramerius.googlecode.com/svn/wiki/features_46/processes.png)