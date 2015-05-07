

# Přihlášení #
### Popis požadavku: ###
Systém umožňuje přihlášení administrátora pomocí standardního mechanismu zadáním uživatelského jména a hesla.

### Poznámky k technickému řešení: ###
Jako autentizační mechanismus je použit systém JAAS (viz http://java.sun.com/j2se/1.5.0/docs/guide/security/jaas), který je standardním mechanismem nového systému zabezpečení úložiště Fedora - FESL.

V aktuální implementaci se autentizace týká pouze administrátorů, v dalších verzích rovněž běžných uživatelů (případně budou jejich přístupová práva nadále určována pouze podle IP adresy). Veškerá práva budou držena ve Fedoře.


# Editace úvodní stránky #
### Popis požadavku: ###
Administrátor s příslušnou rolí může editovat obsah hlavního panelu úvodní stránky, zobrazené po přihlášení do systému.

### Poznámky k technickému řešení: ###
Pokud je Kramerius4 nainstalován jako součást portálu, je možnost takové editace zbytečná a k danému účelu poslouží lépe standardní nástroje návrhu stránek příslušného portálu.

Pokud bude systém provozován jako samostatná webová aplikace, bude použito stejné řešení, jako v systému Kramerius 3, s využitím  javascriptové komponenty RichTextEditor.

# Správa uživatelů #
### Popis požadavku: ###
Systém má umožnit správu seznamu administrátorů a jejich přístupových práv.

### Poznámky k technickému řešení: ###
Vzhledem k použití autentizačního mechanismu FESL / JAAS je správa uživatelů zcela nezávislá na systému Kramerius a bude prováděna nástroji pro správu příslušného aplikačního serveru nebo portálu. Systém JAAS je možno nakonfigurovat například pro napojení na LDAP apod. Pro Tomcat vytvoříme GUI pro správu uživatelů v JDBC realmu.

Kramerius musí podporovat následující role: Import, Export, Replikace, Edit metadata, Správa viditelnosti, Správa uživatelů, Správa replikačních institucí, Správa replikačních práv, Hromadné úpravy.

# Import #
### Popis požadavku: ###
Systém umožňuje importovat do úložiště nové dokumenty ve formátu FOXML 1.1. Dokumenty připravené v dřívějším formátu DTD Monografie a Periodika je možno do nového formátu konvertovat samostatným konverzním nástrojem vyvinutém v rámci Proof of Concept.

Administrátor pro nový import zadává prostřednictvím GUI následující parametry:
  * cestu k importnímu adresáři (v rámci serveru)
  * příznak, zda mají být importované objekty automaticky aktivní
  * příznak, zda mají být importované objekty automaticky přístupné
zpracování duplicitních objektů (automatické sloučení, automatické přepsání, vynechání)

Informace o zadaných importech, jejich průběhu a výsledku (včetně příslušných log souborů) jsou zobrazeny  ve stránce přehledu importů. Bude doplněna možnost volby pořadí spuštění importů a zrušení probíhajícího importu.

Po restartu serveru je nutné kontrolovat stav probíhajících importů před pádem a případně je znovu spustit.  Doplnit možnost nastavit úrovně logování.


### Poznámky k technickému řešení: ###
Systém importuje všechny soubory v zadané složce a jejích podsložkách pomocí funkce ingest z Fedora API-M.
Podle požadavků administrátora u všech objektů nastaví příznak Active a do datastreamu POLICY zapíše výchozí přístupová pravidla XACML.

Slučování duplicitních objektů: v úložišti je zachován původní objekt, do jehož datastreamu RELS-EXT jsou doplněny nové RDF záznamy typu hasPart a isPartOf z nově importovaného objektu.

Prověřit možnost zapisování checksumu u obrázků pro zpětnou kontrolu správnosti importů?

# Export #
### Popis požadavku: ###
Aktuálně vybraný objekt libovolné úrovně je možno exportovat do formátu FOXML, METS nebo ATOM. Uživatel pomocí GUI zadává požadovaný exportní formát, kontext exportu (zveřejnění, replikace, archivace), cílovou složku exportu na serveru a to, zda se má exportovat pouze daný objekt nebo i všechny jeho součásti, odkazované pomocí RDF.

Přehled zadaných exportů a informace o jejich průběhu včetně příslušných log souborů jsou k dispozici na přehledové stránce.

### Poznámky k technickému řešení: ###
Export je prováděn pomocí funkce export Fedora API-M, výsledné soubory jsou uloženy na serveru (s výjimkou exportu právě jednoho aktuálního objektu, který je nabídnut ke stažení na klientský počítač).

Export do formátu FOXML s kontextem replikace s následným importem na cílovém úložišti nahrazuje funkci replikace v systému Kramerius3. Replikaci je možno též nakonfigurovat na úrovni úložišť Fedora ve zcela automatickém režimu s využitím funkcí žurnálu a JMS, kde cílové úložiště je automaticky aktualizováno v režimu slave.


# Hromadné úpravy #
### Popis požadavku: ###
Systém umožňuje spouštění operací, které hromadně modifikují obsah objektů v úložišti.
Administrátor může vybrat operaci ze seznamu dostupných operací, obsah tohoto seznamu je součástí konfigurace systému.

Přehled zadaných operací a informace o jejich průběhu včetně příslušných log souborů jsou k dispozici na přehledové stránce.

### Poznámky k technickému řešení: ###
Hromadné úpravy jsou programovány jako dynamické moduly v interpretovaném jazyce pro JVM (např. Beanshell nebo Groovy), s možností využití všech funkcí rozhraní úložiště Fedora.


# Editace objektů #
### Popis požadavku: ###
Administrátor může u aktuálně vybraného objektu modifikovat jeho obsah následujícími způsoby:

  * smazání objektu. Po potvrzení operace je objekt odstraněn z úložiště včetně všech objektů, na které odkazuje prostřednictvím vazeb RDF. Pokud je objekt referencován z jiných objektů vyšší úrovně, jsou z těchto objektů rovněž odstraněny příslušné RDF vazby.
    * změna viditelnosti - u vybarného objektu je příslušným způsobem nastaven příznak Active
    * změna přístupových práv - u vybraného objektu je pomocí GUI editoru modifikován obsah datastreamu POLICY. Je možno nastavovat přístupová práva veřejný - neveřejný - zděděný. Dále je možno nastavovat instituce s oprávněním replikací. Správa seznamu těchto institucí je prováděna samostatně pomocí nástrojů pro správu autentizace JAAS.
    * změna pořadí zobrazení odkazovaných podčástí. Komponenta CoverFlow se seznamem podčástí je při aktivaci této funkce nahrazena seznamem (tabulkou)podčástí s ovládacími prvky, které umožňují přesun vybraného řádku v seznamu na jinou pozici.
    * přesuny odkazovaných podčástí mezi dvěma dokumenty - při této funkci je hlavní panel rozdělen do dvou panelů obsahujících zdrojový a cílový objekt, komponenty Coverflow jsou v tomto modu opět na hrazeny tabulkovými seznamy podčástí, s ovládacími prvky, které umožní přesouvat vybrané položky z těchto seznamů mezi panely

### Poznámky k technickému řešení: ###
  * Řazení podčástí je určeno pořadím RDF záznamů v datstreamu RELS-EXT.
  * V konfiguraci bude seznam top level modelů, pro každý model seznam povolených vazeb.
  * Zvážit možnost editace obsahu datastreamu MODS a DC - pomocí externího editoru vyvíjeného v MZK Brno. Tento editor by zároveň řešil požadavek na kopii obsahu metadat z jednoho objektu do druhého.
  * U funkce přesunu položek je možno využít funkcionalitu Drag&Drop .
  * Každá změna objektu musí volat aktualizaci indexů.
  * Verzování - u každého datastreamu je možno nastavit, zda má jeho bsah být verzován. Výchozí hodnota je vypnuto.
  * Jak řešit rekurzi přístupových práv?
  * Vyřešit roli subadministrátora s právem  editovat další oprávnění


# Statický export HTML #
### Popis požadavku: ###
_Z dokumentace Kramerius 3:_

Statický export slouží k vytvoření statické verze prezentace určitého titulu nebo jeho části, která může být poté umístěna na přenosné médium (CD, DVD) a distribuována podle potřeb. Součástí statické prezentace jsou vzájemně provázané HTML stránky s metadaty obdobné jako v dynamické verzi na webu a obrazové soubory s digitalizovanými dokumenty.

Při spouštění statického exportu je možné určit typ cílového média (CD, DVD atp.) a systém zajistí automatické rozdělení prezentace na více částí v případě, že kapacita jednoho média je nedostatečná aby pojmula celou prezentaci.

Výsledek statického exportu je uložen do určeného adresáře na filesystému serveru kde je provozován systém Kramerius (podrobnosti dále). Systém neřeší umístění výsledku na zvolené médium (vypalování CD, DVD atp.).


### Poznámky k technickému řešení: ###
Bude řešeno obdobně jako ve verzi  Kramerius 3

# Přepočítání statistik #
### Popis požadavku: ###
Administrátor může z GUI nebo pomocí systémového časovače spustit přepočítání statistik (tedy údajů o počtu dokumentů v jednotlivých kategoriích apod.). Tyto údaje jsou uloženy v indexech služeb úložiště GSearch a Resource Index.

### Poznámky k technickému řešení: ###
Funkce spustí operaci nového vytvoření indexů v úložišti.


# Mazání ztracených souborů #
### Popis požadavku: ###
_Z dokumentace Kramerius 3:_

Díky občasné chybě při mazání metadat a souvisejících dokumentů může v systému dojít k situaci kdy v úložišti dokumentů zůstanou soubory, na které neexistuje žádný odkaz v databázi. Aplikace obsahuje nástroj pro vyhledání a smazání těchto souborů. Pro spuštění tohoto nástroje je možné využít odkaz „Smazat ztracené soubory“ z administrátorského menu aplikace

### Poznámky k technickému řešení: ###
Tato funkce systému Kramerius 3 s přechodem na úložiště Fedora ztrácí smysl, datové soubory jsou logickou součástí digitálních objektů a jsou spolu s nimi automaticky mazány.

V dalších verzích bude podle potřeby doplněna funkce kontroly konzistence RDF vazeb, pokud se potvrdí výskyt “sirotků” v důsledku chyb po mazání vnořených objektů.


# Propojení na externí systémy (ALEPH, DODO) #
### Popis požadavku: ###
_Z dokumentace Kramerius 3:_

#### _Propojení na elektronický katalog ALEPH_ ####
Systém je obousměrně propojen s elektronickým katalogem ALEPH provozovaným v NKP, konkrétně s bází „SKC - Souborný katalog ČR“.

_Propojení z ALEPH do systému Kramerius pro Periodika_

Propojení je na úrovni titulů periodik, jako identifikátor je při propojení použito ISSN periodika.
Systém Kramerius obsahuje vstupní bod http://kramerius.nkp.cz/kramerius/PShowPeriodical.do (tato adresa může být ovlivněna změnami při instalaci/konfiguraci aplikace, přesné znění určí osoba zodpovědná za tyto činnosti), které na základě předaného parametru issn zobrazí stránku „Titul“ pro požadovaný titul. Pokud není titul s uvedeným ISSN nalezen, je zobrazena stránka s informací „Požadovaný titul nebyl nalezen“. Systém ALEPH může použít tento vstupní bod pro odkazy do systému Kramerius.

Příklad adresy:
http://kramerius.nkp.cz/kramerius/PShowPeriodical.do?issn=0862-5921

Na straně systému ALEPH musí být zadáno, které tituly existují v Krameriovi, aby byl zobrazen odkaz na patřičné stránce popisující titul v systému ALEPH. Odkazy nemohou být v ALEPHU automaticky u všech titulů, protože ALEPH obsahuje velké množství titulů které v budované aplikaci nejsou. Zadávání existujících odkazů do ALEPH může být realizováno následujícím způsobem:

Systém ALEPH vždy po uplynutí určitého intervalu získá pomocí protokolu HTTP soubor obsahující seznamy identifikátorů (pro periodika jsou to ISSN). Odkazy s těmito identifikátory v parametrech pak umísťuje na příslušné stránky titulů se stejnými identifikátory. Systém Kramerius obsahuje pro podporu tohoto způsobu vstupní bod http://kramerius.nkp.cz/kramerius/PGetAllISSN.do (tato adresa může být ovlivněna změnami při instalaci/konfiguraci aplikace, přesné znění určí osoba zodpovědná za tyto činnosti), jehož použití vrátí čistě textový soubor obsahující ISSN všech viditelných titulů periodik v systému ve formátu každé ISSN na jednom řádku. ALEPH tedy může používat dotaz na dané URL k získání ISSN pro potřeby doplnění odkazů. Příklad souboru získaného z dané adresy:

0862-5921
0545-9855
1545-9775

_Propojení ze systému Kramerius do systému ALEPH pro Periodika_

Propojení je na úrovni titulů periodik, jako identifikátor je při propojení použito ISSN periodika.
Systém Kramerius obsahuje na stránce „Titul“ odkaz s názvem „Zobrazit titul v elektronickém katalogu NK“. Tento odkaz vede do systému ALEPH, ve kterém provede vyhledání podle ISSN titulu. Odkaz otevírá nové okno internetového prohlížeče.

URL odkazu je: http://sigma.nkp.cz:4505/F/?func=find-c&local_base=SKC&ccl_term=isn=0862-5921  kde 0862-5921 je příklad ISSN.
Jelikož ALEPH obsahuje výrazně více titulů než je v Krameriovi, je toto vyhledávání ve většině případů úspěšné a zobrazí požadovaný záznam.
Jelikož některé tituly nemají v současných datech ISSN a ani ho nemají přiděleno, musí NKP požádat o přidělení ISSN pro tyto tituly a zajistit jejich doplnění k příslušným záznamům do ALEPHU a do metadat určených pro import do Krameria.

_Propojení z ALEPH do systému Kramerius pro Monografie_

Propojení je na úrovni monografií a volných částí monografií, jako identifikátor je při propojení použito ISBN nebo systémové číslo. Toto řešení je zvoleno proto, že ISBN je nepovinné a mnoho monografií ho nemá. Pokud tedy monografie ISBN nemá, je pro propojení na ALEPH třeba použít systémové číslo.

Systémové číslo není součástí metadat, ale lze ho zadat v administrativní části aplikace. Předpokládá se že tímto číslem bude SYSNO z ALEPHu.

Systém Kramerius obsahuje vstupní bod http://kramerius.nkp.cz/kramerius/MShowMonograph.do (tato adresa může být ovlivněna změnami při instalaci/konfiguraci aplikace, přesné znění určí osoba zodpovědná za tyto činnosti), které na základě předaného parametru isbn nebo extid (systémové číslo) zobrazí stránku „Monografie“ nebo „Volná část“. Pokud není monografie ani volná část s uvedeným ISBN nebo systémovým číslem nalezena, je zobrazena stránka s informací „Požadovaná monografie nebyla nalezena“. Systém ALEPH může použít tento vstupní bod pro odkazy do systému Kramerius.

Příklady adres:
http://kramerius.nkp.cz/kramerius/MShowMonograph.do?isbn=80-86097-09-9
http://kramerius.nkp.cz/kramerius/MShowMonograph.do?extid=600000

Na straně systému ALEPH musí být zadáno, které monografie existují v Krameriovi, aby byl zobrazen odkaz na patřičné stránce popisující monografii v systému ALEPH. Odkazy nemohou být automaticky u všech monografií, protože ALEPH obsahuje velké množství titulů které v Krameriovi nejsou. Zadávání existujících odkazů do ALEPHu může být realizováno následujícím způsobem:

Systém ALEPH vždy po uplynutí určitého intervalu získá pomocí protokolu HTTP soubory obsahující seznamy identifikátorů (zvlášť soubor s ISBN a zvlášť se systémovými čísly). Odkazy s těmito identifikátory v parametrech pak umísťuje na příslušné stránky. Systém Kramerius obsahuje pro podporu tohoto způsobu vstupní body http://kramerius.nkp.cz/kramerius/MGetAllISBN.do a http://kramerius.nkp.cz/kramerius/MGetAllExtId.do (tyto adresy můžou být ovlivněny změnami při instalaci/konfiguraci aplikace, přesné znění určí osoba zodpovědná za tyto činnosti), jejichž použití vrátí čistě textový soubor obsahující ISBN respektive systémová čísla všech viditelných monografií a volných částí v systému ve formátu každé ISBN/systémové číslo na jednom řádku. ALEPH tedy může používat dotaz na dané URL k získání ISBN/systémových čísel pro potřeby doplnění odkazů. Pokud má některá monografie nebo volná část vyplněno jak ISBN tak i systémové číslo, není její systémové číslo zařazeno do seznamu systémových čísel, ale je uvedena pouze v seznamu ISBN, aby nedocházelo v ALEPHu ke zobrazení duplicitních odkazů.

Příklad souboru získaného z dané adresy:

0862-5921
0545-9855
1545-9775

_Propojení ze systému Kramerius do systému ALEPH pro Monografie_

Propojení je na úrovni monografií a volných částí monografií, jako identifikátor je při propojení použito ISBN, nebo systémové číslo (viz. vysvětlení v předchozí kapitole).

Systém Kramerius obsahuje na stránce „Monografie“ a „Volná část“ odkaz s názvem „Zobrazit v elektronickém katalogu NK“, odkaz je přítomen pouze pokud zobrazená monografie/volná část má zadáno ISBN nebo systémové číslo. Tento odkaz vede do systému ALEPH, ve kterém provede vyhledání podle ISBN nebo systémového čísla. Odkaz otevírá nové okno internetového prohlížeče.

URL odkazu pro ISBN je: http://sigma.nkp.cz:4505/F/?func=find-c&local_base=SKC&ccl_term=isn=80-86097-09-9 kde 80-86097-09-9 je příklad ISBN.
URL odkazu pro SYSNO je: http://sigma.nkp.cz:4505/F/?func=find-c&local_base=SKC&ccl_term=sys=600000 kde 600000 je příklad SYSNO.
Jelikož ALEPH obsahuje výrazně více titulů než bude v Krameriovi, je toto vyhledávání ve většině případů úspěšné a zobrazí požadovaný záznam.

#### _Propojení na systém pro dodávání elektronických dokumentů (DODO)_ ####

Systém pro dodávání elektronických dokumentů slouží pro adresné dodání elektronické verze dokumentu komukoliv na internetu a respektuje omezení autorského zákona (Zákon č. 121/2000 Sb. (autorský zákon), oddíl 2 - Volná užití, § 30) které říká, že dokument musí být pro tento případ zdigitalizován pro osobní potřebu koncového uživatele a vystaven pouze jemu a na omezenou dobu.

Integrace systému Kramerius s tímto systémem znamená, že na příslušných stránkách našeho systému je zobrazen odkaz na formulář pro objednávku dokumentu v DODO. Po použití tohoto odkazu je otevřeno nové okno se stránkou pro autorizaci v systému DODO (uživatel musí mít založený účet v systému DoDo viz http://doc.nkp.cz - testovací účet: uživatel=JIPO heslo=jipo). Po autorizaci do systému DoDo je zobrazena stránka s formulářem na objednávku vystavení dokumentu – formulář je předvyplněn dle zvoleného (nalezeného) dokumentu z aplikace Kramerius.

Stejný princip propojení se systémem DODO je již používán v projektu JIB viz http://www.jib.cz.

_Propojení pro Periodika_

Ukázkový příklad odkazu na DODO pro periodika vypadá následovně:
http://doc.nkp.cz/katalog/index.htm?ITEM0=uvedte%20cislo%20objednavky&ITEM1=Jahrbuch%20Der%20Psychoanalyse&ITEM2=Freud&ITEM3=Zur%20Einfhrung%20des%20Narzissmus&ITEM4=&ITEM5=0075-2363&ITEM6=&ITEM7=1914&ITEM8=6&ITEM9=1&ITEM10=&ITEM12=uvedte%20poznamku&ORDERTYPEID=2

Analogický odkaz do systému DODO je v systému kramerius v sekci periodika zobrazen na stránkách, kde by měl být odkaz na soubor s dokumentem, ale tento dokument není veřejně přístupný a uživatel přistupuje k aplikaci z internetu. Konkrétně se jedná o stránky:
„Výtisk“
„Stránka“

_Propojení pro Monografie_

Ukázkový příklad odkazu na DODO pro monografie vypadá následovně:
http://doc.nkp.cz/katalog/index.htm?ITEM0=uvedte%20cislo%20objednavky&ITEM1=Jak+pracuj%ed+s%edt%ec&ITEM2=Derfler%2c+Derfler&ITEM3=1994&ITEM5=1-56276-129-3&ITEM6=15&ITEM9=uvedte%20poznamku&ORDERTYPEID=3

Analogický odkaz do systému DODO je v systému kramerius v sekci monografie zobrazen na stránkách, kde by měl být odkaz na soubor s dokumentem, ale tento dokument není veřejně přístupný a uživatel přistupuje k aplikaci z internetu. Konkrétně se jedná o stránky:
„Volná část“
„Vnitřní součást“
„Stránka“




### Poznámky k technickému řešení: ###
Implementace bude převzata z verze Kramerius 3. Systémové číslo pro ALEPH by bylo možné ukládat do pole AlternateID


# OAI-PMH #
### Popis požadavku: ###
Aplikace zpřístupňuje metadata prostřednictvím protokolu OAI-PMH.

### Poznámky k technickému řešení: ###
Implementováno prostřednictvím služby úložiště Fedora OAI Provider Service.

Konfigurace této služby je popsána zde:
http://www.fedora-commons.org/confluence/display/FCSVCS/OAI+Provider+Service+1.2


# METS #
### Popis požadavku: ###
Kramerius umožňuje exportovat bibliografické informace v kontejnerovém formátu METS.

### Poznámky k technickému řešení: ###
Tato funkcionalita je podporována prostřednictvím funkce exportu úložiště Fedora do formátu METS - viz funkční požadavek Export.