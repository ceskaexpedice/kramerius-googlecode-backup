# GUI #

Uživatelské rozhraní pro správu je součástí systému kramerius. Spouští se z administrátorského nebo kontextového menu. Z administrátorského menu můžeme administrovat celé repository, z kontextového pak konrétní titul, nebo jeho část.

## Administrace objektu REPOSITORY ##

Pro administraci repository se spouští položku v menu **Globální akce...**

![http://kramerius.googlecode.com/svn/wiki/rights/prava-adminmenu.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-adminmenu.png)

po spuštění nabídky admin menu se objeví dialog s výběrem akce, kterou chceme administrovat.

![http://kramerius.googlecode.com/svn/wiki/rights/prava-seznamakci.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-seznamakci.png)

Po výběru akce následuje administrační dialog práv k objektu, kde jsou seřazena všechna práva tak, jak se budou interpretovat.

![http://kramerius.googlecode.com/svn/wiki/rights/prava-dialogprav.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-dialogprav.png)

Popis částí:
  1. Tlačítko pro přidání nového práva
  1. Tlačítka pro manipulaci. Rušení a editace existujícího.


## Administrace objektu pomocí kontextového menu ##

Kontextové menu doznalo změn od poslední verze.  Nyní je možné pomocí něj administrovat více objektů. Nejdříve si uživatel musí zvolit objekty, se kterými chce pracovat a následně zavolat položku  **Zobrazení práv...**

![http://kramerius.googlecode.com/svn/wiki/rights/prava-ctxmenu.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-ctxmenu.png)

po spuštění nabídky se objeví následující dialog:

![http://kramerius.googlecode.com/svn/wiki/rights/prava-dialog-seznamobjektu.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-dialog-seznamobjektu.png)

Popis částí:
  1. Záložky. První obsahuje seznam vybraných objektů, ostatní pak chráněné akce
  1. Seznam vybraných objektů z kontextového menu

Změna pravidel na objektu vypadá následovně:

![http://kramerius.googlecode.com/svn/wiki/rights/prava-dialog-seznamprav.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-dialog-seznamprav.png)

Popis částí:
  1. Tlačítka pro editaci práv nad celým výběrem. Vytvoření, editace, rušění a poslední tlačítko je refresh.
  1. Sekce pro manipulaci práv nad jedním vybraným objektem. Funkce v dialogu jsou stejné jako u [dialogu práv](#prava-dialogprav.md)


Vytvoření nebo změna jednoho práva


![http://kramerius.googlecode.com/svn/wiki/rights/prava-zmenaprava.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-zmenaprava.png)

Popis částí:
  1. Výber role, ke které bude právo přiřazeno
  1. Výběr dodatečná podmínky
  1. Hodnoty podmínky. První záložka slouží pro vytváření nové hodnoty, druhá pro výběr již použité.
  1. Prirota.  Mění se pouze pokud je potřeba mít pořadí práv jinak, než jak je implicitně řazeno.



[Popis aplikace správa uživatelů](http://code.google.com/p/kramerius/wiki/PravaUsers)