Instalace z předpřipraveného distribučního souboru je určena pro testovací účely, případně pro malé jednoduché instalace systému Kramerius 4 (s maximálně desítkami titulů a minimálním provozem). Výhodou této instalace je minimální nutnost konfigurace jednotlivých komponent (většina nastvaení je předkonfigurována v rámci distribučního archivu). Pro provoz reálné aplikace s vyšší zátěží je ale vhodné nainstalovat systém K4 a jeho jednotlivé komponenty samostatně na více serverů, popis instalace a konfigurace jednotlivých komponent je uveden zde: [Instalace](Instalace.md)

# Před instalací #

Před samotnou instalací systému K4 je nutno mít nainstalováno a nastaveno následující:
  * Databázový stroj **Postgres** ve verzi 9.1.X nebo vyšší
  * V databázi vytvořeného uživatele **fedoraAdmin** s heslem **fedoraAdmin**
  * Vytvořené databáze **riTriples**, **fedora3**, **kramerius4**.

Poznámka: vlastníkem všech databází by měl být výše zmíněný uživatel **fedoraAdmin**.

Dále je třeba mít nainstalován JDK verze 1.6 (Sun/Oracle) na počítači s minimálně 8GM RAM.


# Instalace #

Samotná instalace je rozdělěna do čtyř kroků.
  * Rozbalení předpřipravené instalace do zvoleného adresáře
  * Nastavení systémových proměnných
  * Import dat.
  * Nastavení mail.properites

## Rozbalení ##
Z adresy
http://dl.dropbox.com/u/9887614/fedora_k4.5.1_installation_nindex.zip si stáněte soubor fedora\_k4.5.1\_installation\_nindex.zip a rozbalte do vámi zvoleného adresáře.

Příklad:

```
~/Programy/
```

Po rozbalení:

```
~/Programy/fedora/.
                 client/
                 docs/
                 install/
                 pdp/
                 server/
                 solr/
                 tomcat/
```


## Nastavení systémových proměnných ##
Nastavte systémové proměnné
**$FEDORA\_HOME** resp **%FEDORA\_HOME%** tak aby ukazovala do adresáře, kde leží rozbalený **fedora\_k4\_instalation.zip**


Příklad:

```
export FEDORA_HOME=~/Programy/fedora/
export CATALINA_HOME=$FEDORA_HOME/tomcat
```

Zkontrolujte také, že máte nastavenou systémovou proměnnou JAVA\_HOME na adresář s instalací JDK.

## Spuštění Tomcatu ##

V terminálu spuštěném v adresáři $FEDORA\_HOME/tomcat zadejte příkaz bin/startup.sh (resp. bin\startup.bat na Windows).

Nyní můžete ověřit správnost instalace úložiště Fedora  zadáním adresy `http://localhost:8080/fedora` v prohlížeči.

## Import dat ##

Pomocí administračního nástroje fedory ($FEDORA\_HOME/client/bin/fedora-admin.sh) naimportovat data z adresáře **$FEDORA\_HOME/import-data**.


## mail.properties ##
Posledním krokem je nastavení přístupu k SMTP serveru. Je potřeba vytvořit soubor mail.properties v adresáři ~/.kramerius4/ a definovat nastavení dle:


[Instalace#Konfigurace](Instalace#Konfigurace.md)



## Ověření instalace ##

V prohlížeči zadejte adresu `http://localhost:8080/search`.

Nyní by na stránce navigace měla být vidět jedna zaindexovaná monografie a 16 stránek a mělo by fungovat přihlášení uživatelem **krameriusAdmin**.