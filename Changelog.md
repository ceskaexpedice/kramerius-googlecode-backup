# 4.8.3 ([r5076](https://code.google.com/p/kramerius/source/detail?r=5076)) #

  * robustnější algoritmus třídění vazeb
  * doplněna podpora loadNonSeq parseru při konverzi a indexaci PDF (property `convert.pdf.loadNonSeq`)
  * opravena chyba ve skrolování seznamu titulů
  * v konvertoru K3->K4 změněna defaultní viditelnost objektů z private na inherit

# 4.8.2 ([r5056](https://code.google.com/p/kramerius/source/detail?r=5056)) #

  * opravena chyba ve vyhledávání podle klíčových slov
  * opravena chyba v třídění součástí dokumentů s duplicitními identifikátory
  * vylepšeno automatické spouštění indexace po importu víceúrovňových dokumentů
  * doplněna možnost indexace více PID v jednom procesu (PID defaultně odděleny středníkem, možno konfigurovat property indexer.pidSeparator
  * indexace více dokumentů může při chybě pokračovat, konfigurovatelné property indexer.continueOnError=false
  * doplněno filtrování neplatných znaků v OCR a PDF při indexaci

# 4.8.1 ([r5009](https://code.google.com/p/kramerius/source/detail?r=5009)) #

  * opravena chyba v stránkování seznamu autorů na záložce Autoři
  * opravena chyba ve fulltextovém indexování PDF dokumentů
  * vylepšeno generování dvouvrstvých PDF
  * [Issue 627](https://code.google.com/p/kramerius/issues/detail?id=627): změněno generování pořadí komponent v konvertoru METS-> K4. Stránky jsou řazeny před ostatní součásti. Původní chování možno obnovit nastavením property convert.pagesFirst = false
  * Isuue 633: chyba při skrývání pásu náhledů

# 4.8.0 ([r4957](https://code.google.com/p/kramerius/source/detail?r=4957)) #

  * [Issue 567](https://code.google.com/p/kramerius/issues/detail?id=567): korektní vypínání Tomcatu
  * [Issue 577](https://code.google.com/p/kramerius/issues/detail?id=577): opraveno chybné vyhledávání textu s diakritikou v MSIE
  * [Issue 574](https://code.google.com/p/kramerius/issues/detail?id=574): opraveno chybné vyhledávání textu s řídicími znaky (např. : )
  * [Issue 558](https://code.google.com/p/kramerius/issues/detail?id=558): změna pořadí sloupců v seznamu procesů
  * [Issue 569](https://code.google.com/p/kramerius/issues/detail?id=569): úprava stránkování v seznamu procesů
  * [Issue 553](https://code.google.com/p/kramerius/issues/detail?id=553): Chybný součet ve správci procesů
  * [Issue 557](https://code.google.com/p/kramerius/issues/detail?id=557): Domenovy filter - Chyba
  * [Issue 555](https://code.google.com/p/kramerius/issues/detail?id=555): Polozka tisk - přesunuta
  * [Issue 556](https://code.google.com/p/kramerius/issues/detail?id=556): Konfigurovatelny prikaz tisk
  * [Issue 524](https://code.google.com/p/kramerius/issues/detail?id=524): Statistiky - vyhledávání podle UUID
  * [Issue 565](https://code.google.com/p/kramerius/issues/detail?id=565): 4.7.1 doplnění dokumentace
  * [Issue 570](https://code.google.com/p/kramerius/issues/detail?id=570): Třídit... se hned spustí
  * [Issue 571](https://code.google.com/p/kramerius/issues/detail?id=571): drobné chyby na wiki stránce StyleGuidlines
  * [Issue 580](https://code.google.com/p/kramerius/issues/detail?id=580): Bundles pro EN, CZ - doplnění
  * [Issue 183](https://code.google.com/p/kramerius/issues/detail?id=183): Shibboleth - odhlášení
  * [Issue 369](https://code.google.com/p/kramerius/issues/detail?id=369): Generování PDF AZ chráněného dokumentu
  * [Issue 375](https://code.google.com/p/kramerius/issues/detail?id=375): Špatný tvar URL po kliknutí na tlačítka sociálních sítí
  * [Issue 441](https://code.google.com/p/kramerius/issues/detail?id=441): Obarvení stavů ve Správě admin. procesů
  * [Issue 442](https://code.google.com/p/kramerius/issues/detail?id=442): Pojmenování procesů ve Správě admin. procesů
  * [Issue 547](https://code.google.com/p/kramerius/issues/detail?id=547): Chybějící konzole v IE
  * [Issue 350](https://code.google.com/p/kramerius/issues/detail?id=350): Chyba při vytváření sbírky
  * [Issue 405](https://code.google.com/p/kramerius/issues/detail?id=405): špatné zobrazení počtu dokumentů autora pro monografie obsahující pdf
  * [Issue 447](https://code.google.com/p/kramerius/issues/detail?id=447): Hledani uvnitr dokumentu a enter
  * [Issue 474](https://code.google.com/p/kramerius/issues/detail?id=474): Optimalizace načítání úvodní stránky
  * [Issue 497](https://code.google.com/p/kramerius/issues/detail?id=497): Připomínky ke Krameriovi 4
  * [Issue 500](https://code.google.com/p/kramerius/issues/detail?id=500): jak opravit virtuální sbírky?
  * [Issue 501](https://code.google.com/p/kramerius/issues/detail?id=501): editace virtuální sbírky
  * [Issue 415](https://code.google.com/p/kramerius/issues/detail?id=415): Napsat na wiki podporované prohlížeče
  * [Issue 508](https://code.google.com/p/kramerius/issues/detail?id=508): Cislo dvojstrany
  * [Issue 515](https://code.google.com/p/kramerius/issues/detail?id=515): Error getting virtual collections
  * [Issue 546](https://code.google.com/p/kramerius/issues/detail?id=546): rozbita struktura dokumentu v levem panelu
  * [Issue 563](https://code.google.com/p/kramerius/issues/detail?id=563): K4 v 4.7.1 [revision 4699](https://code.google.com/p/kramerius/source/detail?r=4699) - problem s IE
  * [Issue 564](https://code.google.com/p/kramerius/issues/detail?id=564): K4 v 4.7.1 [revision 4699](https://code.google.com/p/kramerius/source/detail?r=4699) - problem s dlouhym nazvem clanku
  * [Issue 582](https://code.google.com/p/kramerius/issues/detail?id=582): Chyba při použití convert.useImageServer
  * [Issue 385](https://code.google.com/p/kramerius/issues/detail?id=385): Automatické rozbalování stromu konfigurovatelné pomocí property search.expand.tree
  * [Issue 578](https://code.google.com/p/kramerius/issues/detail?id=578): Indikátor přístupnosti plublic/private konfigurovatelný pomocí search.policy.public
  * [Issue 576](https://code.google.com/p/kramerius/issues/detail?id=576): Možnost skoku na začátek seznamu titulů se znaky mimo abecedu
  * [Issue 494](https://code.google.com/p/kramerius/issues/detail?id=494): Opravena chyba v zobrazovací šabloně pro OCR
  * [Issue 482](https://code.google.com/p/kramerius/issues/detail?id=482): Opravena synchronizace Fedora => K4 po smazání objektu ve Fedora/admin
  * [Issue 588](https://code.google.com/p/kramerius/issues/detail?id=588), [Issue 609](https://code.google.com/p/kramerius/issues/detail?id=609): Procesy, které úspěšně skončí, ale zapíšou něco do error logu, jsou označeny stavem WARNING, resp. BATCH\_WARNING
  * [Issue 590](https://code.google.com/p/kramerius/issues/detail?id=590): Opravena chyba v popisku stránky typu obsah
  * [Issue 600](https://code.google.com/p/kramerius/issues/detail?id=600): Opraven problém při startu K4 se zcela prázdným úložištěm Fedora a MPTstore
  * [Issue 596](https://code.google.com/p/kramerius/issues/detail?id=596): Rámečky pro vyhledávání ALTO se vykreslují vzhledem ke stránce a ne tiskové oblasti
  * [Issue 481](https://code.google.com/p/kramerius/issues/detail?id=481): Vylepšeno RSS nových přírůstků
  * [Issue 598](https://code.google.com/p/kramerius/issues/detail?id=598): Kontrola maximálního počtu stran v generovaném PDF při ručním výběru
  * [Issue 597](https://code.google.com/p/kramerius/issues/detail?id=597): Statistiky - doplněno sledování přístupů na imageserver
  * [Issue 603](https://code.google.com/p/kramerius/issues/detail?id=603): Aktualizována verze knihovny pdfBox pro generování náhledů z PDF
  * [Issue 608](https://code.google.com/p/kramerius/issues/detail?id=608): Zablokováno generování pdf z originálů v pdf
  * [Issue 610](https://code.google.com/p/kramerius/issues/detail?id=610): Při konverzi K3->K4 z PDF je ignorována property convert.originalToJPG
  * [Issue 611](https://code.google.com/p/kramerius/issues/detail?id=611): Pokročilé vyhledávání - formulář - pole pro klíčová slova
  * [Issue 607](https://code.google.com/p/kramerius/issues/detail?id=607): Velký náhled PDF doplněn o kurzor typu pointer
  * [Issue 613](https://code.google.com/p/kramerius/issues/detail?id=613): Opraveny problémy s texty vloženými do generovaného PDF
  * [Issue 615](https://code.google.com/p/kramerius/issues/detail?id=615): Nestandardní znaky v názvech souborů při konverzi z K3 způsobovaly problémy
  * [Issue 616](https://code.google.com/p/kramerius/issues/detail?id=616): Podpora replikace s ČDK
  * [Issue 618](https://code.google.com/p/kramerius/issues/detail?id=618): Podpora encrypted PDF v konvertoru K3->K4
  * [Issue 619](https://code.google.com/p/kramerius/issues/detail?id=619): Oprava chyby v inicializaci tabulek statistik
  * [Issue 621](https://code.google.com/p/kramerius/issues/detail?id=621): Volitelný prefix externích URL v konvertorech K3->K4 a NDK->K4
  * [Issue 622](https://code.google.com/p/kramerius/issues/detail?id=622): Výstupní podadresáře konvertoru K3->K4 volitelně pojmenovány podle UUID (místo čísla zakázky)
  * [Issue 594](https://code.google.com/p/kramerius/issues/detail?id=594): Konvertor NDK->K4: náhrada nevalidních znaků v packageID
  * [Issue 333](https://code.google.com/p/kramerius/issues/detail?id=333): Proces mazání objektů z fedory možno kvůli kompatibilitě s OAI providerem konfigurovat tak, aby pouze nastavoval stav "Deleted" (property delete.purgeObjects=false)
  * [Issue 625](https://code.google.com/p/kramerius/issues/detail?id=625): Opraven problém v implementaci kriteria Moving wall
  * [Issue 529](https://code.google.com/p/kramerius/issues/detail?id=529): Změna orientace zobrazení stránky
  * [Issue 513](https://code.google.com/p/kramerius/issues/detail?id=513): Upraveno RSS nových přírůstků při masivních importech
  * [Issue 624](https://code.google.com/p/kramerius/issues/detail?id=624): opraveno vyhledávání podle MDT
  * [Issue 614](https://code.google.com/p/kramerius/issues/detail?id=614): indexace dc:identifier a mods:identifier
  * [Issue 599](https://code.google.com/p/kramerius/issues/detail?id=599): indexace autorů bez kódu role
  * [Issue 605](https://code.google.com/p/kramerius/issues/detail?id=605): opravena chyba v aktualizaci SOLR indexu
  * [Issue 511](https://code.google.com/p/kramerius/issues/detail?id=511): Opravena výstupní šablona procesu replikace

Podrobný popis významných změn je na stránce NovinkyVeVerzi48

# 4.7.1 ([r4699](https://code.google.com/p/kramerius/source/detail?r=4699)) #
  * Automatické třídění [popis](http://code.google.com/p/kramerius/wiki/NovinkyVeVerzi47#Automatické_třídění_součástí_dokumentů)
  * [Issue 552](https://code.google.com/p/kramerius/issues/detail?id=552) - Přidáno tlačítko pro zobrazení přes celou obrazovku
  * [Issue 548](https://code.google.com/p/kramerius/issues/detail?id=548) - Upravena funkce tisku
  * [Issue 531](https://code.google.com/p/kramerius/issues/detail?id=531) - podpora Imageserveru v METS konvertoru
  * [Issue 514](https://code.google.com/p/kramerius/issues/detail?id=514) - METS konvertor prochází rekurzivně podadresáře
  * [Issue 544](https://code.google.com/p/kramerius/issues/detail?id=544) - opravy a dokumentace REST API pro spouštění procesů


# 4.7.0 ([r4610](https://code.google.com/p/kramerius/source/detail?r=4610)) #
Podrobný popis významných změn je na stránce NovinkyVeVerzi47


# 4.6.2 ([r4517](https://code.google.com/p/kramerius/source/detail?r=4517)) #

  * [Issue 510](https://code.google.com/p/kramerius/issues/detail?id=510) - opraven problém s reindexací

# 4.6.1 ([r4482](https://code.google.com/p/kramerius/source/detail?r=4482)) #

  * Obnovena možnost spouštění administrátorských procesů přes původní servlet lr
  * [Issue 498](https://code.google.com/p/kramerius/issues/detail?id=498) - opravena chyba při zpracování výjimek v procesu Import FOXML

# 4.6.0 ([r4445](https://code.google.com/p/kramerius/source/detail?r=4445)) #

Podrobný popis významných změn je na stránce NovinkyVeVerzi46

  * Replikace částí  - změny v procesu replikace  - [Replikace](Replikace.md)
  * [Issue 486](https://code.google.com/p/kramerius/issues/detail?id=486) - Podpora Fedory 3.6
  * [Issue 478](https://code.google.com/p/kramerius/issues/detail?id=478) - Externě referencované data streamy
  * [Issue 471](https://code.google.com/p/kramerius/issues/detail?id=471) - Problémy spouštění parametrizovaných procesů na os windows
  * [Issue 465](https://code.google.com/p/kramerius/issues/detail?id=465) - Podoby dialogů pro procesy
  * [Issue 467](https://code.google.com/p/kramerius/issues/detail?id=467) - Špatný stav procesu
  * [Issue 466](https://code.google.com/p/kramerius/issues/detail?id=466) - Unexpected token
  * [Issue 470](https://code.google.com/p/kramerius/issues/detail?id=470) - Dialog indexace dokumentů
  * [Issue 475](https://code.google.com/p/kramerius/issues/detail?id=475) - Import NDK je chráněn samostatnou akcí
  * [Issue 477](https://code.google.com/p/kramerius/issues/detail?id=477) - Parametrizované procesy jsou chráněny stejnou akcí, jako jejich neparametrizované protějšky
  * [Issue 474](https://code.google.com/p/kramerius/issues/detail?id=474) - Načítání časové osy
  * [Issue 476](https://code.google.com/p/kramerius/issues/detail?id=476) - Pruh s náhledy a víceřádkový název
  * [Issue 479](https://code.google.com/p/kramerius/issues/detail?id=479) - Dialogy pro generování pdf a sjednocené texty
  * [Issue 485](https://code.google.com/p/kramerius/issues/detail?id=485) - Špatné znaky v konfiguračním souboru
  * [Issue 400](https://code.google.com/p/kramerius/issues/detail?id=400) - Nepřiřaditelná uživatelská role
  * [Issue 187](https://code.google.com/p/kramerius/issues/detail?id=187) - Parametrizovaný mail uživateli
  * [Issue 270](https://code.google.com/p/kramerius/issues/detail?id=270) - Mazání dokumentu se špatnou referencí
  * [Issue 408](https://code.google.com/p/kramerius/issues/detail?id=408) - Verze a revize v patičce


# 4.6-beta (trunk) #

Podrobný popis významných změn je na stránce NovinkyVeVerzi46

  * [Issue 445](https://code.google.com/p/kramerius/issues/detail?id=445) - Zapracované změny API (dokument z mailové konference)  - [RemoteAPI](RemoteAPI.md)
  * [Issue 425](https://code.google.com/p/kramerius/issues/detail?id=425), [Issue 424](https://code.google.com/p/kramerius/issues/detail?id=424), [Issue 423](https://code.google.com/p/kramerius/issues/detail?id=423), [Issue 421](https://code.google.com/p/kramerius/issues/detail?id=421)  - Opravy a návrhy v API
  * [Issue 436](https://code.google.com/p/kramerius/issues/detail?id=436) - Přidána BASIC autentizace
  * [Issue 435](https://code.google.com/p/kramerius/issues/detail?id=435) - K4 replikace - Předávání autentizace
  * [Issue 450](https://code.google.com/p/kramerius/issues/detail?id=450) - Časová osa, špatný formát
  * [Issue 332](https://code.google.com/p/kramerius/issues/detail?id=332) - Výjimka NumberFormatException při konverzi dat
  * [Issue 457](https://code.google.com/p/kramerius/issues/detail?id=457), [Issue 446](https://code.google.com/p/kramerius/issues/detail?id=446) - Dlaždice z image serveru,
  * [Issue 407](https://code.google.com/p/kramerius/issues/detail?id=407) - Replikace K3->K4
  * [Issue 422](https://code.google.com/p/kramerius/issues/detail?id=422) - Popisky procesu a přeorganizovaní menu
  * [Issue 449](https://code.google.com/p/kramerius/issues/detail?id=449) - Mazání rolí s českými znaky
  * [Issue 448](https://code.google.com/p/kramerius/issues/detail?id=448) - Čeština při registraci uživatele
  * [Issue 434](https://code.google.com/p/kramerius/issues/detail?id=434) - Info o viditelnosti
  * [Issue 438](https://code.google.com/p/kramerius/issues/detail?id=438) - Doménový filtr a lokalizace
  * [Issue 431](https://code.google.com/p/kramerius/issues/detail?id=431) - Přidání pravidla criteria\_rights\_manage
  * [Issue 429](https://code.google.com/p/kramerius/issues/detail?id=429) - Chybějící položka Export do FOXML
  * [Issue 419](https://code.google.com/p/kramerius/issues/detail?id=419) - Správa procesu,  mezery
  * [Issue 430](https://code.google.com/p/kramerius/issues/detail?id=430) - Parametrizovaný import, vstupní formular
  * [Issue 409](https://code.google.com/p/kramerius/issues/detail?id=409) - Změna anglického textu v labels.properties
  * [Issue 453](https://code.google.com/p/kramerius/issues/detail?id=453) - Nesprávné typy stránek z formátu METS NDK

# 4.6-alfa (trunk) #

Podrobný popis významných změn je na stránce NovinkyVeVerzi46

  * [Issue 215](https://code.google.com/p/kramerius/issues/detail?id=215) - API pro plánování procesů
  * [Issue 364](https://code.google.com/p/kramerius/issues/detail?id=364) - opraveny problémy s diakritikou v přístupových právech
  * [Issue 365](https://code.google.com/p/kramerius/issues/detail?id=365) - doplněno potvrzování při mazání pravidel práv
  * [Issue 156](https://code.google.com/p/kramerius/issues/detail?id=156) - doplněny všechny chybějící závislosti pro automatický build mavenem
  * [Issue 114](https://code.google.com/p/kramerius/issues/detail?id=114) - replikace mezi systémy K4
  * [Issue 396](https://code.google.com/p/kramerius/issues/detail?id=396) - v registračním formuláři označena povinná pole a doplněna CAPTCHA
  * [Issue 354](https://code.google.com/p/kramerius/issues/detail?id=354) - upraven proces statického exportu PDF
  * [Issue 245](https://code.google.com/p/kramerius/issues/detail?id=245) - oprava drobných nedostatků v GUI
  * [Issue 348](https://code.google.com/p/kramerius/issues/detail?id=348) - oprava textů pro správu obsahu virtuálních sbírek
  * [Issue 389](https://code.google.com/p/kramerius/issues/detail?id=389) - doplněno hlídání přístupových práv k dlaždicím DeepZoom z imageserveru
  * [Issue 378](https://code.google.com/p/kramerius/issues/detail?id=378) - úprava šablony pro zobrazení metadat
  * [Issue 384](https://code.google.com/p/kramerius/issues/detail?id=384) - upraven log v procesu Import FOXML
  * [Issue 474](https://code.google.com/p/kramerius/issues/detail?id=474) - doplněno ovládání zaškrtávacích políček v seznamu výsledků vyhledávání
  * [Issue 356](https://code.google.com/p/kramerius/issues/detail?id=356) - opraveno zobrazování stránek na šířku
  * [Issue 370](https://code.google.com/p/kramerius/issues/detail?id=370) - odstraněno zdvojování modelů v konvertoru METS
  * [Issue 386](https://code.google.com/p/kramerius/issues/detail?id=386) - upraveny texty zobrazované při spouštění procesů
  * [Issue 380](https://code.google.com/p/kramerius/issues/detail?id=380) - nastavení přístupových práv podle názvu domény
  * [Issue 327](https://code.google.com/p/kramerius/issues/detail?id=327) - zobrazení hodnoty příznaku viditelnosti vybraných dokumentů
  * [Issue 334](https://code.google.com/p/kramerius/issues/detail?id=334), [Issue 336](https://code.google.com/p/kramerius/issues/detail?id=336) - změna editace datumů v časové ose
  * [Issue 362](https://code.google.com/p/kramerius/issues/detail?id=362) - v tabulce procesů je doplněn čas ukončení, procesy s chybou označeny červeně
  * [Issue 347](https://code.google.com/p/kramerius/issues/detail?id=347) - opravena chyba při expiraci session
  * [Issue 340](https://code.google.com/p/kramerius/issues/detail?id=340) - odebrání Oblíbené položky
  * [Issue 363](https://code.google.com/p/kramerius/issues/detail?id=363) - navigace ve stránkování procesů
  * [Issue 331](https://code.google.com/p/kramerius/issues/detail?id=331) - vylepšena správa dávkových procesů
  * [Issue 329](https://code.google.com/p/kramerius/issues/detail?id=329) - změna heapspace pro procesy
  * [Issue 217](https://code.google.com/p/kramerius/issues/detail?id=217) - správa dodatečných podmínek v editoru práv
  * [Issue 305](https://code.google.com/p/kramerius/issues/detail?id=305) - název verze K4 v nápovědě
  * [Issue 308](https://code.google.com/p/kramerius/issues/detail?id=308) - informace o expiraci session
# Verze 4.5.4 ([r3908](https://code.google.com/p/kramerius/source/detail?r=3908)) #

  * [Issue 381](https://code.google.com/p/kramerius/issues/detail?id=381) - Úprava vyhodnocování parametrů kritéria "Pohyblivá zeď" podle požadavků KNAV.

# Verze 4.5.3 ([r3824](https://code.google.com/p/kramerius/source/detail?r=3824)) #

  * [Issue 341](https://code.google.com/p/kramerius/issues/detail?id=341) - Konfigurovatelné spouštění indexeru z importních procesů. Indexer je po importu automaticky spuštěn, pokud booleovská property `ingest.startIndexer` v souboru migration.properties má hodnotu true (což je výchozí nastavení)
  * [Issue 342](https://code.google.com/p/kramerius/issues/detail?id=342) - Oprava problému, kdy při indexaci některých rozsáhlých titulů docházelo k timeoutu spojení do databáze riTriples
  * [Issue 343](https://code.google.com/p/kramerius/issues/detail?id=343) - Opraveno chybné stránkování v dialogu Indexace dokumentů
  * [Issue 352](https://code.google.com/p/kramerius/issues/detail?id=352), [Issue 368](https://code.google.com/p/kramerius/issues/detail?id=368) - V některých konfiguracích K4 s předřazeným proxyserverem nebyly některé interní URL v K4 nastaveny správně, takže nebylo možné přepínat jazyk uživatelského rozhraní a v editoru pořadí částí nebyly vidět náhledy stran.

# Verze 4.5.2 ([r3661](https://code.google.com/p/kramerius/source/detail?r=3661)) #

  * [Issue 271](https://code.google.com/p/kramerius/issues/detail?id=271) - dokumentace doplněna o přehled šablon XSLT
  * [Issue 325](https://code.google.com/p/kramerius/issues/detail?id=325) - doplněna podpora zobrazování metadat MODS podle nové specifikace NDK
  * [Issue 318](https://code.google.com/p/kramerius/issues/detail?id=318) - opraven problém při replikaci dokumentů z K3, které měly mezery v názvech obrazových souborů
  * [Issue 324](https://code.google.com/p/kramerius/issues/detail?id=324) - časová osa zpracuje i dokumenty s chybnými datumy (<1000)
  * [Issue 280](https://code.google.com/p/kramerius/issues/detail?id=280) - upraveno zadávání stran pro generování PDF
  * [Issue 309](https://code.google.com/p/kramerius/issues/detail?id=309) - opraveno zobrazení ukončených dávkových procesů
  * [Issue 311](https://code.google.com/p/kramerius/issues/detail?id=311) - zjednodušeno vytváření virtuálních sbírek
  * [Issue 228](https://code.google.com/p/kramerius/issues/detail?id=228) - opraveny nedostatky v emailu odesílaném nástrojem pro hlášení chyb
  * [Issue 313](https://code.google.com/p/kramerius/issues/detail?id=313) - opravena chyba v kritériu přístupového práva Pohyblivá zeď
  * [Issue 306](https://code.google.com/p/kramerius/issues/detail?id=306) - opraveno zobrazování výsledků replikace K3->K4
  * [Issue 264](https://code.google.com/p/kramerius/issues/detail?id=264) - pořadí poduzlů ve stromu dokumentu důsledně odpovídá pořadí elementů v RELS-EXT
  * [Issue 251](https://code.google.com/p/kramerius/issues/detail?id=251) - opraveno zobrazování počtu dokumentů s modely bez lokalizačního klíče
  * [Issue 246](https://code.google.com/p/kramerius/issues/detail?id=246) - opraven problém s PDF dokumenty v NTK
  * [Issue 249](https://code.google.com/p/kramerius/issues/detail?id=249) - opraveno chování "abeced" v seznamu titulů a autorů
  * [Issue 281](https://code.google.com/p/kramerius/issues/detail?id=281), 222, 282 - rozvržení stránek (výška) reaguje na změnu velikosti okna a skrývání prvků
  * [Issue 290](https://code.google.com/p/kramerius/issues/detail?id=290) - opraveny pozice tlačítek Logy, Zastavit a Smazat v přehledu procesů
  * [Issue 277](https://code.google.com/p/kramerius/issues/detail?id=277), 292 - opraveno chybné zobrazování neúspěšných procesů indexace
  * [Issue 303](https://code.google.com/p/kramerius/issues/detail?id=303) - opraveno ořezávání textů ve výsledcích replikace K3->K4
  * [Issue 300](https://code.google.com/p/kramerius/issues/detail?id=300) - doplněn chybějící text při spouštění procesu změna viditelnosti
  * [Issue 301](https://code.google.com/p/kramerius/issues/detail?id=301) - příkaz Tisk je nyní přístupný pouze pro autorizované uživatele
  * [Issue 211](https://code.google.com/p/kramerius/issues/detail?id=211) - doplněno potvrzení pro tlačítka Zastavit a Smazat ve správě procesů
  * [Issue 273](https://code.google.com/p/kramerius/issues/detail?id=273) - opravena chyba při zobrazování PDF
  * [Issue 288](https://code.google.com/p/kramerius/issues/detail?id=288)  - opraveno opakování poslední položky v seznamu autorů a titulů
  * [Issue 220](https://code.google.com/p/kramerius/issues/detail?id=220), 289 - záložka Tituly obsahuje pouze názvy dokumentů nejvyšší úrovně
  * [Issue 207](https://code.google.com/p/kramerius/issues/detail?id=207), 263 - opravy časové osy
  * [Issue 172](https://code.google.com/p/kramerius/issues/detail?id=172) - zobrazení jazyků u vícejazyčných dokumentů
  * [Issue 160](https://code.google.com/p/kramerius/issues/detail?id=160) - zobrazeno datum u rozbalených výsledků vyhledávání
  * [Issue 149](https://code.google.com/p/kramerius/issues/detail?id=149) - opraveno třídění písmen s diakritikou
  * [Issue 274](https://code.google.com/p/kramerius/issues/detail?id=274) - doplněna dokumentace akcí v přístupových právech
  * [Issue 298](https://code.google.com/p/kramerius/issues/detail?id=298) - název dc.title ve stromu a v zobrazení stránky doplněn o jméno modelu
  * [Issue 276](https://code.google.com/p/kramerius/issues/detail?id=276) - odkazy pro sociální sítě a RSS změněny na persistentní URL
  * [Issue 295](https://code.google.com/p/kramerius/issues/detail?id=295) - rozklikávání dávkových procesů v IE
  * [Issue 293](https://code.google.com/p/kramerius/issues/detail?id=293) - kopírování z logu procesu
  * [Issue 294](https://code.google.com/p/kramerius/issues/detail?id=294) - opraven problém při importu celé struktury FOXML dokumentů s externími referencemi

# Verze 4.5.1 ([r3508](https://code.google.com/p/kramerius/source/detail?r=3508)) #

  * [Issue 291](https://code.google.com/p/kramerius/issues/detail?id=291) - opraven problém s indexací dokumentů, jejichž čas modifikace neobsahuje milisekundy
  * [Issue 287](https://code.google.com/p/kramerius/issues/detail?id=287) - po indexaci se místo optimize volá commit, pomocí boolean property indexer.isSoftCommit (default false) lze nastavit, jestli je použit hardCommit nebo softCommit
  * [Issue 278](https://code.google.com/p/kramerius/issues/detail?id=278) - opraveny odkazy v RSS kanálech
  * [Issue 279](https://code.google.com/p/kramerius/issues/detail?id=279) - řešen problém s místem pro obrázky z imageserveru
  * [Issue 201](https://code.google.com/p/kramerius/issues/detail?id=201) - v editoru uživatelů lze mazat uživatele s přiřazenou rolí
  * [Issue 23](https://code.google.com/p/kramerius/issues/detail?id=23) - při importu FOXML dokumentů jsou detekovány kořenové dokumenty a automaticky pro ně spuštěna indexace
  * [Issue 210](https://code.google.com/p/kramerius/issues/detail?id=210) - sjednoceno pořadí tlačítek v přehledu procesů
  * [Issue 212](https://code.google.com/p/kramerius/issues/detail?id=212) - nezobrazuje se prázdné datum v přehledu procesů
  * [Issue 205](https://code.google.com/p/kramerius/issues/detail?id=205) - pdf má v titulu uvedeno skutečné UUID namísto generického "img.pdf"
  * [Issue 272](https://code.google.com/p/kramerius/issues/detail?id=272) - opraven problém při změně viditelnosti dokumentů
  * [Issue 234](https://code.google.com/p/kramerius/issues/detail?id=234), [Issue 235](https://code.google.com/p/kramerius/issues/detail?id=235) - odstraněny problémy s autentizací single-sign-on
  * [Issue 269](https://code.google.com/p/kramerius/issues/detail?id=269) - odstraněna konfigurační property indexerHost

# Verze 4.5.0 ([r3415](https://code.google.com/p/kramerius/source/detail?r=3415)) #

Popis změn v GUI verze 4.5 najdete na stránce NovinkyVeVerzi45

  * [Issue 283](https://code.google.com/p/kramerius/issues/detail?id=283) - spouštění pokročilého vyhledávání
  * [Issue 154](https://code.google.com/p/kramerius/issues/detail?id=154) - zlepšena správa procesů
  * [Issue 257](https://code.google.com/p/kramerius/issues/detail?id=257) - opraveno zobrazování výsledků replikace dat z K3 do K4
  * [Issue 255](https://code.google.com/p/kramerius/issues/detail?id=255) - opraven problém s omezeným místem pro obrázky z imageserveru
  * [Issue 258](https://code.google.com/p/kramerius/issues/detail?id=258) - opravena chyba při vytváření sloupce DEACTIVATED v databázi uživatelských účtů
  * [Issue 268](https://code.google.com/p/kramerius/issues/detail?id=268), [Issue 225](https://code.google.com/p/kramerius/issues/detail?id=225), [Issue 247](https://code.google.com/p/kramerius/issues/detail?id=247) - opraveny problémy při exportu PDF
  * [Issue 267](https://code.google.com/p/kramerius/issues/detail?id=267) - položky v kontextovém menu, které přímo spouštějí akci, už nemají v názvu tři tečky
  * [Issue 237](https://code.google.com/p/kramerius/issues/detail?id=237) - je zobrazována informace o úspěšné či neúspěšné aktivaci nového uživatele
  * [Issue 261](https://code.google.com/p/kramerius/issues/detail?id=261) - opraven problém s indexací dokumentů, které obsahovaly v textu html entitu ampersand
  * [Issue 243](https://code.google.com/p/kramerius/issues/detail?id=243) - opraveny problémy ve funkčnosti editoru pořadí částí dokumentů
  * [Issue 248](https://code.google.com/p/kramerius/issues/detail?id=248) - odstraněno připojování k databázi Fedory při použití Imageserveru
  * [Issue 252](https://code.google.com/p/kramerius/issues/detail?id=252) - proces Export FOXML do logu vypisuje adresář, do kterého jsou data exportována
  * [Issue 110](https://code.google.com/p/kramerius/issues/detail?id=110), [Issue 262](https://code.google.com/p/kramerius/issues/detail?id=262) - opraveny chyby při zakládání a mazání uživatelského účtu
  * [Issue 204](https://code.google.com/p/kramerius/issues/detail?id=204) - podpora virtuálních sbírek
  * [Issue 226](https://code.google.com/p/kramerius/issues/detail?id=226) - aktualizována verze SOLR na 3.5 (nutno použít novou konfiguraci, schema.xml a přeindexovat)
  * [Issue 200](https://code.google.com/p/kramerius/issues/detail?id=200), [Issue 229](https://code.google.com/p/kramerius/issues/detail?id=229) - podpora uživatelských profilů
  * [Issue 231](https://code.google.com/p/kramerius/issues/detail?id=231) - podpora RSS
  * [Issue 227](https://code.google.com/p/kramerius/issues/detail?id=227) - podpora sociálních sítí
  * [Issue 228](https://code.google.com/p/kramerius/issues/detail?id=228) - možnost zaslání komentáře ke konkrétnímu dokumentu administrátorovi
  * [Issue 209](https://code.google.com/p/kramerius/issues/detail?id=209) - podpora doporučování souvisejících dokumentů
  * [Issue 230](https://code.google.com/p/kramerius/issues/detail?id=230) - podpora generování PDF pro elektronické čtečky
  * [Issue 206](https://code.google.com/p/kramerius/issues/detail?id=206) - doplněna možnost autonomní registrace nových uživatelů
  * [Issue 183](https://code.google.com/p/kramerius/issues/detail?id=183) - opraveno odhlašování ze Shibbolethu
  * [Issue 214](https://code.google.com/p/kramerius/issues/detail?id=214) - přejmenována položka menu Globální akce
  * [Issue 208](https://code.google.com/p/kramerius/issues/detail?id=208) - nastaveny limity pro data v časové ose
  * [Issue 202](https://code.google.com/p/kramerius/issues/detail?id=202) - odstraněna editovatelnost logů v přehledu procesů
  * [Issue 198](https://code.google.com/p/kramerius/issues/detail?id=198) - doplněna a zobecněna dokumentace pro instalaci a konfiguraci SOLR a JAAS
  * [Issue 178](https://code.google.com/p/kramerius/issues/detail?id=178) - opravena šířka sloupců v dialogu nastavení práv
  * [Issue 177](https://code.google.com/p/kramerius/issues/detail?id=177) - doplněny chybějící ... u položek v menu
  * [Issue 175](https://code.google.com/p/kramerius/issues/detail?id=175) - opraven matoucí text u synchronizace indexů
  * [Issue 176](https://code.google.com/p/kramerius/issues/detail?id=176) - opraveny problémy s indexací některých typů PDF
  * [Issue 167](https://code.google.com/p/kramerius/issues/detail?id=167) - oprava problému při indexaci celého modelu
  * [Issue 188](https://code.google.com/p/kramerius/issues/detail?id=188), [Issue 179](https://code.google.com/p/kramerius/issues/detail?id=179) - zlepšena kompatibilita s různými typy prohlížečů
  * [Issue 196](https://code.google.com/p/kramerius/issues/detail?id=196) - doplněny definice modelů z MZK

# Verze 4.4.1 ([r3127](https://code.google.com/p/kramerius/source/detail?r=3127)) #

  * [Issue 194](https://code.google.com/p/kramerius/issues/detail?id=194) - Opravena chyba, kdy u některých titulů (obsahujících InternalParts) docházelo k chybnému řazení stránek. Chybné tituly je třeba po instalaci této verze znovu přeindexovat.
  * [Issue 191](https://code.google.com/p/kramerius/issues/detail?id=191), [Issue 193](https://code.google.com/p/kramerius/issues/detail?id=193) - opraveny nedostatky v datumových polích časové osy
  * [Issue 189](https://code.google.com/p/kramerius/issues/detail?id=189) - změna pořadí priorit pravidel přístupových práv stejné úrovně

# Verze 4.4.0 ([r3096](https://code.google.com/p/kramerius/source/detail?r=3096)) #

  * [Issue 151](https://code.google.com/p/kramerius/issues/detail?id=151) - Podpora Google Analytics - kód Web property ID stačí zadat do `search.properties`, klíč `googleanalytics.webpropertyid`
  * [Issue 181](https://code.google.com/p/kramerius/issues/detail?id=181) - Náhrada za text "Tento dokument není veřejně přístupný" může být kromě property rightMsg umístěna do samostatného souboru ~/.kramerius4/texts/rightMsg\_CZ\_cs (resp. rightMsg\_EN\_en)
  * [Issue 186](https://code.google.com/p/kramerius/issues/detail?id=186) - Doplněna možnost nastavování přístupových práv na úrovni jednotlivých datastreamů
  * [Issue 182](https://code.google.com/p/kramerius/issues/detail?id=182) - Opravena chyba zavírání souborů cache v konvertoru K3->K4
  * [Issue 125](https://code.google.com/p/kramerius/issues/detail?id=125) - Možnost zablokování uživatelského účtu
  * [Issue 174](https://code.google.com/p/kramerius/issues/detail?id=174) - Opravena chybná konverze ISSN v Monograph/Series
  * [Issue 162](https://code.google.com/p/kramerius/issues/detail?id=162) - Doplněn chybějící scrollbar v úvodních informacích
  * [Issue 147](https://code.google.com/p/kramerius/issues/detail?id=147) - Doplněna lišta pro druhou úroveň dokumentů v seznamu vyhledaných
  * [Issue 130](https://code.google.com/p/kramerius/issues/detail?id=130) - Opravena chyba v chování pásu náhledů
  * [Issue 107](https://code.google.com/p/kramerius/issues/detail?id=107) - Položka Editor dostupná pro všechny úrovně objektů
  * [Issue 95](https://code.google.com/p/kramerius/issues/detail?id=95) - Signalizace průběhu vyhledávání
  * [Issue 165](https://code.google.com/p/kramerius/issues/detail?id=165) - Změněn způsob zadávání IP filteru v editoru práv
  * [Issue 140](https://code.google.com/p/kramerius/issues/detail?id=140), [Issue 183](https://code.google.com/p/kramerius/issues/detail?id=183) - Podpora Shibbolethu
  * [Issue 166](https://code.google.com/p/kramerius/issues/detail?id=166) - Podpora Fedory 3.5
  * [Issue 164](https://code.google.com/p/kramerius/issues/detail?id=164) - Tisk do fronty na serveru
  * [Issue 148](https://code.google.com/p/kramerius/issues/detail?id=148), 145, 137, 120, 108, 102 - Zlepšení indexace
  * [Issue 142](https://code.google.com/p/kramerius/issues/detail?id=142), 133, 155 - Vylepšeno zobrazení informací o procesech
  * [Issue 152](https://code.google.com/p/kramerius/issues/detail?id=152) - Podpora dokumentů s více typy
  * [Issue 93](https://code.google.com/p/kramerius/issues/detail?id=93) - Nová podoba navigačního panelu
  * [Issue 97](https://code.google.com/p/kramerius/issues/detail?id=97) - Konfigurovatelné panely metadat
  * [Issue 90](https://code.google.com/p/kramerius/issues/detail?id=90) - Automatické načítání další stránky výsledků při skrolování
  * [Issue 88](https://code.google.com/p/kramerius/issues/detail?id=88) - Opraveny chyba v chování posuvníku časové osy
  * [Issue 18](https://code.google.com/p/kramerius/issues/detail?id=18) - Změna zobrazování výsledků vyhledávání
  * [Issue 113](https://code.google.com/p/kramerius/issues/detail?id=113) - Opraven problém s vytvářením prázdných tabulek správy procesů
  * Konvertor K3->K4 umožňuje automatickou konverzi obrázků v plném rozlišení do JPG (property convert.originalToJPG=true)

# Verze 4.3.5 ([r2693](https://code.google.com/p/kramerius/source/detail?r=2693)) #

  * [Issue 153](https://code.google.com/p/kramerius/issues/detail?id=153) - přidána možnost záložky se zobrazením obsahu streamu TEXT\_OCR (aktivace pomocí search.properties, klíč search.item.tabs=text\_ocr)
  * Opraveno zobrazování rámečků pro data ALTO s českými řetězci
  * Indexace dokumentů (z hlavního menu) vždy nejdříve smaže existující položky v indexu

# Verze 4.3.3 ([r2630](https://code.google.com/p/kramerius/source/detail?r=2630)) #

Distribuční zip soubor je k dispozici v sekci  [Downloads](http://code.google.com/p/kramerius/downloads/detail?name=Kramerius-4.3.3.zip) ,  při aktualizaci z předchozí verze nakopírujte soubory search.war, rightseditor.war a editor.war do adresáře tomcat/webapps. Soubor security-core.jar do adresáře tomcat/lib. Soubor schema.xml do adresáře fedora/solr/conf

  * [Issue 146](https://code.google.com/p/kramerius/issues/detail?id=146) - opravena chyba při zobrazení informace o private dokumentu
  * [Issue 77](https://code.google.com/p/kramerius/issues/detail?id=77) - podpora session timeout
  * [Issue 141](https://code.google.com/p/kramerius/issues/detail?id=141) - Zlepšené nastavení replikačních práv pro konvertor K3->K4
  * [Issue 123](https://code.google.com/p/kramerius/issues/detail?id=123) - XSL šablony pro transformaci MODS v konvertoru K3->K4 mohou být předefinovány v adresáři .kramerius4/xsl
  * Opravena reindexace po změně viditelnosti nebo smazání části dokumentu
  * [Issue 135](https://code.google.com/p/kramerius/issues/detail?id=135) -	Možnost nastavení výchozích přístupových práv replikovaných monografií (property convert.defaultRights)
  * [Issue 127](https://code.google.com/p/kramerius/issues/detail?id=127) -	Podpora ALTO 1.4 a 2.0 v konvertoru K3->K4, indexaci i zobrazování
  * [Issue 122](https://code.google.com/p/kramerius/issues/detail?id=122) - Opravena chyba v zobrazování náhledů v editoru vazeb
  * [Issue 118](https://code.google.com/p/kramerius/issues/detail?id=118) - Opraven problém při konverzi externě referencovaných souborů
  * [Issue 103](https://code.google.com/p/kramerius/issues/detail?id=103) - Podpora e-knih (souborů pouze pro download)
  * [Issue 106](https://code.google.com/p/kramerius/issues/detail?id=106) - Opravena chyba: Internet Explorer 9 - správa dlouhotrvajících procesů - aktualizace
  * [Issue 116](https://code.google.com/p/kramerius/issues/detail?id=116) - opravena chyba při rušení plánovaných procesů
  * [Issue 115](https://code.google.com/p/kramerius/issues/detail?id=115), 104 - Zlepšeno hlášení chyb v procesech
  * [Issue 112](https://code.google.com/p/kramerius/issues/detail?id=112) - oprava XSLT šablony pro OAI ESE
  * [Issue 49](https://code.google.com/p/kramerius/issues/detail?id=49) - Uživatelsky definovatelné podmínky přístupových práv (skripty)
  * [Issue 94](https://code.google.com/p/kramerius/issues/detail?id=94) - opraveny defaultní XSLT šablony pro metadata
  * [Issue 99](https://code.google.com/p/kramerius/issues/detail?id=99) - Přihlašovací dialog obsahuje konfigurovatelný text (konfigurační soubor logininfo, případně v jazykových mutacích logininfo\_CZ\_cs, logininfo\_EN\_en atd...)
  * [Issue 101](https://code.google.com/p/kramerius/issues/detail?id=101) - Dílčí úpravy v XSLT šablonách MODS v konvertoru K3->K4
  * [Issue 100](https://code.google.com/p/kramerius/issues/detail?id=100) - Odstraněna nefunkční položka "odkazy" v hlavičce stránky. Odkazy mohou  být uvedeny v záložce Informace
  * [Issue 98](https://code.google.com/p/kramerius/issues/detail?id=98) - Úpravy defaultních popisek prvků UI (podle schůze 18.4.)
  * [Issue 92](https://code.google.com/p/kramerius/issues/detail?id=92) - Konfigurace počtu rozbalených položek v seznamu (property search.results.numOpenedRows=3)
  * [Issue 91](https://code.google.com/p/kramerius/issues/detail?id=91) - Nová záložka na titulní stránce - Vybrané. Její obsah je definován seznamem PID v property search.home.tab.custom.uuids. (PID musí být uvedeny včetně prefixu uuid:) Možnost konfigurovat pořadí záložek na titulní stránce (property search.home.tabs=custom,info,newest,mostDesirables)
  * [Issue 89](https://code.google.com/p/kramerius/issues/detail?id=89) - konfigurovtelný počet výsledků vyhledávání na stránce (property search.results.rows=15)
  * Sjednoceny servlety pro práci s obrázky


# Verze 4.3.1 ([r2385](https://code.google.com/p/kramerius/source/detail?r=2385)) #

  * Opraveno chybné zobrazení stran uložených v  IIP image serveru.
  * Úpravy v podpoře OAI (tag OAI:itemId nastavován konvertorem pro všechny modely, nový exportní formát drkramerius4 pro registr digitalizace)

# Verze 4.3.0 ([r2376](https://code.google.com/p/kramerius/source/detail?r=2376)) #

  * [Issue 57](https://code.google.com/p/kramerius/issues/detail?id=57) - opraveno našeptávání u vyhledávání podle autora
  * [Issue 56](https://code.google.com/p/kramerius/issues/detail?id=56) - vylepšeno sestavení nadpisu na úvodní stránce generovaného PDF
  * [Issue 60](https://code.google.com/p/kramerius/issues/detail?id=60) - administrátorské procesy logují do stdout místo stderr
  * [Issue 22](https://code.google.com/p/kramerius/issues/detail?id=22), [Issue 76](https://code.google.com/p/kramerius/issues/detail?id=76) - administrátorem editovatelný systém XSLT šablon pro zobrazení metadat. Defaultní XSLT šablony pro zobrazení MODS v jednotlivých částech stránek jsou definovány v adresářích `search/web/inc/details/xsl` a `search/web/inc/results/xsl`. Kteroukoli z šablon je možno nahradit vlastní, uloženou pod stejným názvem (bez cesty) v adresáři `~/.kramerius4/xsl`.
  * [Issue 74](https://code.google.com/p/kramerius/issues/detail?id=74) - konvertor K3->K4 - podpora zdrojového a cílového souboru na různých filesystémech
  * [Issue 73](https://code.google.com/p/kramerius/issues/detail?id=73) - konvertor K3->K4 ukládá foxml soubory do podadresáře xml (při konverzi do adresářů podle zakázek)
  * [Issue 47](https://code.google.com/p/kramerius/issues/detail?id=47) - opravena chyba při automatickém spouštění indexace po mazání dokumentu
  * Indexer - přidány nové akce: checkIntegrityByModel, checkIntegrityByDocument
  * [Issue 75](https://code.google.com/p/kramerius/issues/detail?id=75), [Issue 68](https://code.google.com/p/kramerius/issues/detail?id=68) - opraveny nekozistence při navigaci stromem dokumentů pomocí pop-up menu v pravém panelu
  * [Issue 80](https://code.google.com/p/kramerius/issues/detail?id=80) - opravena chyba při mazání titulů s vícenásobnými vazbami
  * [Issue 61](https://code.google.com/p/kramerius/issues/detail?id=61) - při generování PDF se nezadává požadovaný rozsah stran, ale jen počet stran od aktuálně vybrané. Odpadá tím problém s nejednoznačným číslováním stran.
  * [Issue 83](https://code.google.com/p/kramerius/issues/detail?id=83) - opravena chyba v přesměrování při zadání persistentního URL dokumentu
  * [Issue 82](https://code.google.com/p/kramerius/issues/detail?id=82) - při požadavku na zobrazení dokumentu s neexistujícím UUID je zobrazena úvodní stránka doplněná o chybovou zprávu
  * [Issue 50](https://code.google.com/p/kramerius/issues/detail?id=50) - podpora aktuální verze Fedory 3.4.2
  * Odstraněna nutnost připojení k databázi fedora3. Z konfiguračního souboru Tomcat je možno odstranit příslušný datasource.
  * [Issue 69](https://code.google.com/p/kramerius/issues/detail?id=69) - opravena chyba při zobrazování internalPart (K4 vyžadoval existenci streamu IMG\_FULL)
  * [Issue 81](https://code.google.com/p/kramerius/issues/detail?id=81) - konvertor K3->K4 při při konverzi do adresářů podle zakázek kopíruje i původní XML ve formátu K3. Řízeno příznakem `convert.copyOriginal=true` v migration.properties
  * [Issue 70](https://code.google.com/p/kramerius/issues/detail?id=70) - konvertor K3->K4 při konverzi dokumentu s chybějícím obrázkovým souborem je konverze ukončena s výjimkou IllegalStateException. Původní chování (vytvořen FOXML objekt bez obrázku a konverze pokračuje) je možno nastavit pomocí property `convert.ignoreMissingFiles=true`.
  * [Issue 79](https://code.google.com/p/kramerius/issues/detail?id=79) - konvertor K3->K4: opravy a optimalizace XSLT šablon pro datastream BIBLIO-MODS
  * [Issue 37](https://code.google.com/p/kramerius/issues/detail?id=37) - při generování PDF je omezen počet současně zpracovávaných požadavků, při překročení může uživatel požadavek opakovat. Maximální počet požadavků je řízen položkou  `pdfQueue.activeProcess` v configuration.properties (výchozí hodnota 2).
  * [Issue 78](https://code.google.com/p/kramerius/issues/detail?id=78) - opravena chyba při zapisování uživatele, který spustil proces, do přehledu procesů
  * Licenční text, zobrazovaný u dokumentů s donátorem je přesunut z FOXML objektu s logem donátora do standardního souboru labels.properties.
  * [Issue 86](https://code.google.com/p/kramerius/issues/detail?id=86) - opravena chyba v detekci předčasného ukončení procesů
  * [Issue 85](https://code.google.com/p/kramerius/issues/detail?id=85) - doplněna optimalizace indexu po smazání záznamu. Existující nekonzistence v indexech budou automaticky opraveny jakémkoli dalším mazání dokumentů, případně je lze vynutit okamžitě zadáním URL `http://localhost:8080/solr/update?stream.body=optimize`
  * [Issue 67](https://code.google.com/p/kramerius/issues/detail?id=67) - editor pořadí stránek se otevírá v novém okně, odstraněn zpětný odkaz do K4
  * OAI - nový provider pro Registr digitalizace, optimalizace stávajícího provideru ESE. Při použití OAI je třeba nainstalovat příslušné disseminátory a aktuální verzi FOXML modelů z distribučního souboru installation.zip


# Verze 4.2.3 (2168) #

_Poznámka:_ Při upgrade z verze 4.2.1 je třeba nahradit všechny 4 soubory z `Kramerius-4.2.3.zip` , ze souboru `installation-4.2.3.zip` stačí nahradit soubor `solr/conf/schema.xml`.

  * [Issue 34](https://code.google.com/p/kramerius/issues/detail?id=34) - opraven email s generovaným heslem
  * [Issue 35](https://code.google.com/p/kramerius/issues/detail?id=35) - opraveny chyby u záložky Informace
  * [Issue 11](https://code.google.com/p/kramerius/issues/detail?id=11) - zachování URL po přihlášení
  * [Issue 39](https://code.google.com/p/kramerius/issues/detail?id=39) - alternativní způsob předání parametrů procesům
  * [Issue 40](https://code.google.com/p/kramerius/issues/detail?id=40) - podpora PDF v konvertoru K3->K4
  * [Issue 36](https://code.google.com/p/kramerius/issues/detail?id=36) - proces importer prochází podadresáře v abecedním pořadí
  * [Issue 45](https://code.google.com/p/kramerius/issues/detail?id=45) - opravena chyba ve zpracování persistentního URL (handle)
  * [Issue 44](https://code.google.com/p/kramerius/issues/detail?id=44) - URL pro spouštění externích editorů je odvozeno z URL requestu, ne ze systémové property
  * [Issue 46](https://code.google.com/p/kramerius/issues/detail?id=46) - předávání autentizačních údajů mezi kaskádovitě spouštěnými procesy
  * [Issue 38](https://code.google.com/p/kramerius/issues/detail?id=38) - vylepšeno chování procesu pro generování cache deepZoom dlaždic
  * [Issue 54](https://code.google.com/p/kramerius/issues/detail?id=54) - změna názvu parametru v URL pro volání externího editoru metadat
  * [Issue 51](https://code.google.com/p/kramerius/issues/detail?id=51) - automatické zavírání kontextového menu
  * [Issue 52](https://code.google.com/p/kramerius/issues/detail?id=52) - opraveno chování lišty pro stránkování ve výsledcích vyhledávání
  * [Issue 53](https://code.google.com/p/kramerius/issues/detail?id=53) - opravena nefunkční tlačítka stránkování při zobrazení plné velikosti stran
  * [Issue 43](https://code.google.com/p/kramerius/issues/detail?id=43) - opravena chyba zobrazování velkého náhledu v prohlížečích s jádrem Webkit


# Verze 4.2.1 (2068) #

  * Opravena chyba v nastavování přístupových práv bez parametru
  * Upraven dialog pro generování PDF

# Verze 4.2.0 #

  * [Issue 33](https://code.google.com/p/kramerius/issues/detail?id=33) - v přehledu procesů je zaznamenáván uživatel spouštějící proces
> > (Struktura databázových tabulek je automaticky aktulizována při prvním spuštění K4)

  * Zlepšená podpora uživatelsky definované datové struktury
> > nová konfigurační property fedora.treePredicates, opravy v šablonách, kontextovém menu

  * Reindexace dokumentů rekursivně s ohledem na datum importu. Není potřeba reindexovat celý strom, jen tu větev, kde došlo k změně.

  * Možnost reindexace všech dokumentů s daným modelem nejvyší úrovně (např. všechny monografie)

  * [Issue 15](https://code.google.com/p/kramerius/issues/detail?id=15) - sjednoceno indexování a lokalizace položek ve facetu Typ dokumentu

  * [Issue 32](https://code.google.com/p/kramerius/issues/detail?id=32) - pro spuštění procesu mimo GUI je třeba autentizace dle parametru v url

  * [Issue 27](https://code.google.com/p/kramerius/issues/detail?id=27) - Dialog pro změnu příznaku kramerius:policy obsahuje informaci o tom, jak je aktuálně nastavený

  * [Issue 30](https://code.google.com/p/kramerius/issues/detail?id=30) - Prohlížečka Seadragon distribuována jako součást K4

  * [Issue 31](https://code.google.com/p/kramerius/issues/detail?id=31), [Issue 17](https://code.google.com/p/kramerius/issues/detail?id=17), [Issue 16](https://code.google.com/p/kramerius/issues/detail?id=16) - Změna definice procesů v lp.xml nevyžaduje restart aplikace, sjednocena konfigurace logování (všechno logování je přesměrováno na standardní logovací API JDK)

  * [Issue 26](https://code.google.com/p/kramerius/issues/detail?id=26) - Lokalizace login dialogu

  * [Issue 19](https://code.google.com/p/kramerius/issues/detail?id=19) - Opraveno zobrazování neobvyklých struktur dokumentů

  * [Issue 25](https://code.google.com/p/kramerius/issues/detail?id=25) - Odstraněn středník za autorem v seznamu monografií

  * Sjednocen styl uživatelského rozhraní

  * [Issue 29](https://code.google.com/p/kramerius/issues/detail?id=29) - Nový systém správy uživatelů a přístupových práv
> > Popis a způsob použití je v dokumentu [Prava](Prava.md), instalace v [Instalace#Přístupová práva uživatelů]


> Upozornění: Při přechodu ze starších verzí K4 do tomcat/lib nakopírujte jar s jdbc ovladačem pro postgresql, nejlépe z distribuce databáze PostgreSQL nebo z archivu search.war/WEB-INF/lib  - ovladač z distribuce fedora 3.3 patří k PostgreSQL 8.3 a s Krameriem 4.2 a novějším nefunguje.

  * [Issue 28](https://code.google.com/p/kramerius/issues/detail?id=28) - Podpora pro obrazové soubory mimo úložiště Fedora (včetně IIP image serveru)
> > (zaveden nový literál v RELS-EXT, viz [Instalace](Instalace.md))

  * Podpora pro ALTO
> > (Soubor s informacemi ALTO se ukládá do datastreamu ALTO, který v tom případě nahrazuje datastream TEXT\_OCR)

  * Fulltextové vyhledávání v rámci vybraného dokumentu

  * [Issue 9](https://code.google.com/p/kramerius/issues/detail?id=9) - Opraveny zjištěné problémy GUI v prohlížeči MSIE

  * Konvertor K3 -> K4 umožňuje využití externích odkazů v binárních datastreamech (file URL)
> > zavedeny nové properties do migration.properties:
```
# target directory for conversion
convert.target.directory=${sys:user.home}/.kramerius4/convert-converted


#target directory for replication and conversion
migration.target.directory=${sys:user.home}/.kramerius4/replication-converted

#convertor will create subfolder with contract name in the conversion target subfolder, the contents of the target directory is not deleted prior conversion batch  
convert.useContractSubfolders=false

# controls how convertor K3->K4 should embed binary streams ( encoded/referenced/external)
convert.files=encoded
# controls how convertor K3->K4 should embed preview streams ( encoded/referenced/external)
convert.previews=encoded
# controls how convertor K3->K4 should embed thumbnail streams ( encoded/referenced/external)
convert.thumbnails=encoded


#ingest and indexing phases of migration processes will be skipped
ingest.skip=false

```

  * Generování velkých náhledů a jejich parametrizovaná velikost v konvertoru
> > zavedeny nové properties do migration.properties:
```
# controls if convertor K3->K4 should generate IMG_PREVIEW
convert.generatePreview=true

# size of the generated preview in pixels (maximum height or width)
convert.previewSize=700
```

  * Integrován editor pořadí podřízených částí dokumentu  (tedy RDF vazeb a jejich pořadí v RELS-EXT)