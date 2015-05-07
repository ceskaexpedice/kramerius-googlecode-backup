# Úvod #

Od verze 4.4.0 systém kramerius odděluje správu uživatelů a rolí. Důvodem je možnost autentizace uživatelů pomocí jiných mechanismů než poskytuje samotný kramerius, například protokolem Shibboleth http://shibboleth.net/.


# Správa rolí #

Pro správu rolí je nutné najet do administračního menu a zvolit položku Správa rolí ...


![http://kramerius.googlecode.com/svn/wiki/rights/prava-editor-roli-menu.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-editor-roli-menu.png)


Po spuštění se objeví dialog správy rolí. Jsou zde vidět všechny role, na které má přihlášený uživatel práva.

![http://kramerius.googlecode.com/svn/wiki/rights/prava-editor-roli.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-editor-roli.png)

Ten obsahuje následující:

  1. Tlačítka pro přidání nové role a obnovu obsahu.
  1. Tabulku s rolemi. Každý řádek obsahuje název role, název administrátorské role a nakonec tlačítka pro manipulaci. Pokud je s rolí svázáno právo, nejde smazat a tlačítko je zašedlé.


# Správa uživatelů #

Uživatelé se nyní mohou autentizovat vůči databázi nebo pomocí shibbolethu. Pro nastavení shibbolethích uživatelů klikněte [zde](Shibb.md)

Pro interní účty je vyvinuta samostatná aplikace.

## Spuštění aplikace ##

Aplikace se spouští pres administrátorské menu.


![http://kramerius.googlecode.com/svn/wiki/rights/prava-editor-uziv-menu.png](http://kramerius.googlecode.com/svn/wiki/rights/prava-editor-uziv-menu.png)


Aplikace má dva různé módy, **superadministrátorský**, kde je možné vidět všechny uživatele a administrovat je, **subadministrátoský**, kde přihlášený uživatel může editovat pouze jemu přiřazené uživatele.



![http://kramerius.googlecode.com/svn/wiki/rights/userseditor.png](http://kramerius.googlecode.com/svn/wiki/rights/userseditor.png)

Popis částí:
  1. Přepínací menu.
  1. Datová tabulka.
  1. Ovládací panel.


## Ovládací panel ##

![http://kramerius.googlecode.com/svn/wiki/rights/userseditor-ovladacipanel.png](http://kramerius.googlecode.com/svn/wiki/rights/userseditor-ovladacipanel.png)

Popis:
  1. Navigační tlačítka. Posun na předchozí nebo následující záznam resp. na první nebo poslední.
  1. Tlačítka pro editaci.  Vytvoření nového záznamu, editaci existujícího, smazání existujícho, duplikování a refresh.
  1. Vyhledávací pole. V uživatelích se vyhledává dle přihlašovacího jména a ve skupinách dle jména skupiny.

## Editační a vytvářecí dialog uživatele ##

![http://kramerius.googlecode.com/svn/wiki/rights/userseditor-userformular.png](http://kramerius.googlecode.com/svn/wiki/rights/userseditor-userformular.png)

Popis:
  1. Data uživatele.
  1. Tlačítka pro vygenerování nového hesla, které se následně pošle na email uživatele. Pokud se jedná o nově vytvářený záznam, je tlačítko zakázané a email se posílá automaticky po uložení záznamu.
  1. Reference na roli, která daného uživatele administruje. Pokud je reference prázdná, může uživatele měnit pouze superadmin, pokud je vyplněná, může ji spravovat příslušník dané role nebo superadmin.
  1. Reference na role do kterých uživatel patří.


Poznámka: Pokud se uživatel přihlásí jako subadministrátor nemůže měnit referenci na administrátorskou roli.
Jím vytvořený uživatel bude mít v referenci automaticky přiřazenou jeho administrátorskou roli.