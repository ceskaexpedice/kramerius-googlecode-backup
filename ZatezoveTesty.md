## Průběžné informace z měření ##
### Výchozí stav ###
1. Počáteční stav (6350 objektů)
  * otevření titulu : 2.5s
  * vyhledání ze záložky Tituly: 1 s
  * vyhledání  fulltext "zlatá": 1 s
  * document indexation (browse model): 3s

### Úvodní testy ###
2. Import ze samostatného procesu bez indexace: 1 dávka (635 objektů) cca 17 s. (37 obj/s) - RI Mulgara

3. Import z dvou paralalně běžících procesů: 1 dávka(1270 objektů) cca 25s (50 obj/s) - RI Mulgara

RI Mulgara při paralelním importu padá kvůli memory leaku, další memory leak se objevoval při použití s OAI providerem. Instalace překonfigurována na použití MPT triplestore. Import opakován:

2a.  Import ze samostatného procesu bez indexace: 1 dávka (635 objektů) cca 30 s. (20 obj/s) - RI MPTStore

3a. Import z dvou paralalně běžících procesů: 1 dávka(1270 objektů) cca 50s (25 obj/s) - RI MPTstore

Při použití indexu MPT store tedy nedochází k memory leakům, ale paralelní import nepřináší prakticky žádné zrychlení a celkově je import téměř 2x pomalejší než při použití RI Mulgara.

### Testy kapacity a odezvy úložiště ###
#### Objem dat 1M ####
Import cca 1M objektů se zapnutým indexem MPT store trval cca 12 hodin.

Rebuild RI MPT store s 1M objektů trval přibližně 20 hodin.

4. Rychlost odezvy GUI při 1M objektů:
  * otevření titulu: 3s
  * vyhledání ze záložky Tituly: 1.5 s
  * vyhledání  fulltext "zlatá": 1 s
  * document indexation (browse model): 2.5s

#### Objem dat 6,5M ####
5. V průběhu importu po naimportování asi 6.5M objektů byl vyčerpán stávající diskový prostor pro databázi. FOXML objekty ve fedoře zabírají cca 50GB, odpovídající databáze riTriples 130GB. Prostor pro fedoru zvýšen cca na 600GB, pro postgres na cca 900GB.
Resource index MPTstore se jeví jako neefektivní řešení, Mulgara nefunkční. Bude zkoumána možnost vyžití implementace 4store.org.

Rychlost importu na nové konfiguraci disků mírně vzrostla, 1 dávka (1270 objektů) nyní trvá cca 40s (původně 50s).

#### Testování možnosti implementace resource indexu pomocí Virtuoso Open-Source Edition ####
Místo výše zmíněného 4store se nakonec zdálo nadějnější podrobněji prozkoumat systém „Virtuoso Open-Source Edition 6.1.6“. Testování Virtuosa probíhalo na notebooku (4 GB RAM, Intel Core i5-2450M CPU 2,5 GHz, Windows 7 64bit).

Bylo dohodnuto, že se provede podobný import jako při testování MPTstore, ale s tím, že importovat se bude přímo do Virtuosa (ne přes Fedoru), aby bylo výrazně rychleji dosaženo dohodnutého potřebného počtu cca 100 miliónů RDF vazeb uložených v systému Virtuoso. Při takovémto počtu RDF vazeb už byly při použití MPTstore problémy s dotazy popsané níže v odstavci „Objem dat 35M“. Hlavním cílem tedy bylo zjistit, zda budou uvedené dotazy při použití Virtuosa rychlejší. Dále se při tomto mělo zjistit, zda by při použití Virtuosa mohl být import rychlejší a samozřejmě se také jednoduše podívat, kolik zabírá takto naplněný Virtuoso místa na disku.

Import do Virtuosa:

Import přímo do Virtuosa ze samostatného procesu probíhal rychlostí cca 42 objektů za sekundu. Takováto rychlost byla po celou dobu importování. Do Virtuosa bylo takto postupně naimportováno dohodnutých cca 100 miliónů RDF vazeb, které vznikly z cca 20-ti miliónů importovaných objektů. V dokumentaci Virtuosa je uvedeno v systémových požadavcích Virtuosa toto: „500M triples per 16G RAM can be a ballpark guideline“.

Když se takto naplněný Virtuoso napojil na Fedoru a zkusilo se do něj přes Fedoru nástrojem „C:\fedora\client\bin\fedora-ingest.bat“ naimportovat dalších cca 500 objektů, tak tento import probíhal rychlostí cca 2 objekty za sekundu. Poté se na stejném počítači přes stejnou Fedoru stejným nástrojem zkusilo naimportovat také cca 500 objektů do velmi málo naplněného MPTstore, který byl napojen pod Fedoru místo Virtuosa. Tento import probíhal také rychlostí cca 2 objekty za sekundu. I z tohoto je tedy zřejmé, že čas vkládání do resource indexu je při importování do Fedory pravděpodobně téměř zanedbatelný oproti času, který při tom spotřebuje Fedora. Z tohoto a z výše uvedených informací lze usuzovat, že použití Virtuosa jako resource indexu místo MPTstore by nepřineslo zrychlení importů – pravděpodobně by rychlost importů byla přibližně stejná jako při použití resource indexu MPTstore.

Dotazy do Virtuosa:

Dotazy (SPARQL) byly testovány proti Virtuosovi, ve kterém bylo výše uvedených cca 100 miliónů RDF vazeb (přesně: 108 469 446 RDF vazeb). Dotazy byly posílány do Virtuosa z Java programu přímo přes příslušné Java API Virtuosa.

Po prozkoumání obdobných dotazů, jako byl dotaz, o kterém se píše níže v odstavci „Objem dat 35M“, se ohledně doby trvání provádění takovýchto dotazů ve Virtuosovi ukázalo následující:

Pro rychlost provádění dotazů je zdaleka nejvíce rozhodující, zda je výsledkem dotazu hodně velký počet (např. 21 miliónů) záznamů, který se třídí. Zdaleka nejvíce času zabere v takovém případě třídění. A pro výsledný čas nehraje roli, zda je požadovaný výsledek takového dotazu nějak omezen pomocí OFFSET a LIMIT. V Javě průchod přes celý takový výsledný result set cca 21 miliónů záznamů trval cca 16 minut.  Kdyby tedy bylo potřeba výsledek takového dotazu postupně procházet, tak nejlepší je držet si v Javě výsledný result set a podle potřeby donačítat záznamy.

Když byl takový dotaz vracející 21M záznamů se tříděním, tak trval například někdy 30 minut, někdy 20 minut, někdy 15 minut. Čas trvání dotazu ovlivňovalo například, když byl stejný dotaz poslán do Virtuosa opakovaně, tak opakované provedení dotazu bylo rychlejší. Dotaz obsahoval tři podmínky, a když byla každá podmínka vyhodnocena jednotlivě, tak každé z těchto podmínek samostatně vyhovovalo také cca 21M záznamů.

Když byl takový dotaz vracející 21M záznamů bez třídění, tak trval například někdy 8 sekund, někdy 2 sekundy, někdy 0,1 sekundy. Čas trvání dotazu ovlivňovalo opět například, když byl stejný dotaz poslán do Virtuosa opakovaně, tak opakované provedení dotazu bylo rychlejší.

Když byl takový obdobný dotaz se tříděním, jen jeho podmínky byly trochu jiné, že vracel jen 169 záznamů, tak trval například někdy 200 milisekund, někdy 30 milisekund, někdy 15 milisekund. Čas trvání dotazu ovlivňovalo opět například, když byl stejný dotaz poslán do Virtuosa opakovaně, tak opakované provedení dotazu bylo rychlejší. Dotaz obsahoval opět tři podmínky, a když byla v tomto případě každá podmínka vyhodnocena jednotlivě, tak každé z těchto podmínek samostatně vyhovovaly následující počty záznamů: První podmínce vyhovovalo cca 21M záznamů, druhé podmínce 187 záznamů a třetí podmínce 1951 záznamů.

Paměťové nároky Virtuosa:

Virtuoso naplněný výše uvedeným počtem cca 100 miliónů RDF vazeb (přesně: 108 469 446 RDF vazeb) zabíral na disku cca 8GB.

#### Objem dat 35M ####
6. K 29.7.2012 je naimportováno 35M objektů.
Rychlost importu i odezva GUI K4 zůstává stejná, dotazy do MPT store se výrazně zpomalují, složitější dotaz spojující 3 RDF vazby trvá místo počátečních jednotek vteřin jednotky minut.
Je třeba provézt opatření a tento typ dotazů nebude v K4 používán.
Databáze MPTStore zabírá cca 850GB, úložiště Fedora cca 300GB

#### Objem dat 40M ####
7. 5.8.2012 po naimportování 40M objektů byly vyčerpány všechny inodes v datovém úložišti fedora.
Po navýšení potřebné kapacity inodes na 100 000 000 byla dne 24.8.2012 spuštěna další dávka importu s cílem 48M importovaných objektů.

#### Objem dat 48M ####
8. 29.8.2012 naimportováno 48M objektů, spuštěna závěrečná dávka importu s cílem 60M. Rychlost importu i odezvy systému se nemění.

#### Objem dat 60M ####
9. 3.9.2012 naimportováno 60M objektů. Rychlost importu i odezvy systému se nezměnila.

### Test indexu SOLR ###
Současně s testem kapacity úložiště fedora a MPT store proběhne (původně nepožadovaný) test kapacity indexu SOLR.
Po indexaci 5M objektů byl vyčerpán dostupný diskový prostor na serveru KSOLRKRAM1, další testy budou pokračovat po navýšení kapacity administrátory NKČR.
Stávající rychlost odezvy GUI závislá na indexu SOLR při 5M záznamech se neliší oproti rychlostem uvedeným v bodu 4 pro 1M záznamů.

Po zvýšení místa n adisku pro index SOLR byl vygenerován index s 60M záznamů. Počáteční rychlost odezvy při prvním pokusu o připojení k aplikaci se zvýšila na cca 20s, odezva na následující dotazy a navigace v systému po úplné inicializaci indexu a cache se pohybují pod 1s.