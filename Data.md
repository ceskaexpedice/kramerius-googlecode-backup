
Digitální objekty systému Kramerius 4 jsou uloženy v úložišti Fedora Commons ve formátu FOXML 1.1. Popis tohoto formátu je zde: https://wiki.duraspace.org/display/FCR30/Introduction+to+FOXML.

Kramerius 4 používá zobecněný datový model vycházející z návrhu společnosti Qbizm vytvořeném v rámci proof of concept převodu dat ze struktury Kramerius 3 do úložiště Fedora (dále POC). Dokumentace tohoto původního návrhu je k dispozici zde: http://digit.lib.cas.cz/index.php?cat=fedora.

Příklad jednoduché monografie ve formátu FOXML pro Kramerius 4 je v sekci Downloads, soubor PrikladFOXML.zip.


# Identifikátor digitálních objektů #
Každý digitální objekt systému Kramerius 4 má jednoznačný identifikátor UUID, generovaný stejným algoritmem, který byl použit v systému Kramerius 3 (tedy UUID version 1, viz [RFC4122](http://tools.ietf.org/html/rfc4122)), doplněný prefixem uuid:, například uuid:0eaa6730-9068-11dd-97de-000d606f5dc6


# Datové streamy #
Objekty používají datové streamy navržené v rámci POC, povinné streamy jsou DC, BIBLIO\_MODS a RELS\_EXT.

Objekty, které obsahují binární stream IMG\_FULL s obrázkem v plném rozlišení, jsou oproti POC rozšířeny o další binární stream IMG\_THUMB s miniaturou obrázku (ve formátu JPG se šířkou 128 px) pro zobrazení v souhrnných přehledech a binární stream IMG\_PREVIEW se zmenšeným náhledem obrázku (ve formátu JPG se šířkou 512 px)

Pokud k obrázku stránky existuje OCR, musí být uložen v příslušném FOXML objektu v datastreamu TEXT-OCR (MIME type text/plain) v kódování UTF-8.

Pokud k OCR existuje i dokument ALTO (verze 1 nebo 2), musí být uložen v tomtéž FOXML objektu v datastreamu ALTO (MIME type text/xml), rovněž v kódování UTF-8.

Další rozšíření oproti POC je standardní datový stream POLICY, který obsahuje přístupová  práva ve formátu XACML - použit pouze u objektů s omezeným přístupem.

Pokud pro digitální objekt existuje donátor, jehož logo má být zobrazeno spolu s titulem,  je uveden jako vazba v RELS-EXT na speciální FO-XML objekt, který obsahuje logo donátora v binárním datovém streamu LOGO.  Tento způsob umožňuje informace automaticky indexovat v RI a navíc je možno snáze přidávat nové donátory. Příklad FOXML objektu donátora (Norské fondy) je v balíčku installation v sekci downloads, soubor donator\_norway.xml.

V budoucích verzích K4 bude podporováno vyznačování vyšších logických částí (např. článků) na jejich podřízených stránkách, založené na informacích v datastreamech ALTO příslušných  stránek. FOXML objekt nadřízené části (článku) v tom případě bude obsahovat datastream "STRUCT\_MAP" (MIME type text/xml, kódování UTF-8) s následujícím obsahem:

```
<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<parts>
    <part type="TITLE/SUBTITLE/NORMAL_TEXT..." order="1/2/3..." alto="uuid:eed93968-e2ef-11e1-abbb-001b63bd97ba/ALTO" begin="TextBlock5" />
    ... element part opakovat pro každou podřízenou stránku
</parts>
```

Atributy elementu part jsou převzaty z odpovídajícího elementu div v logické struktuře METS dle nového standardu NKP pro digitalizaci (viz níže)


# Hierarchie digitálních objektů #

Dokumenty v původním formátu DTD Kramerius 3 mají hierarchickou stromovou strukturu (typicky monografie/svazek/stránka nebo periodikum/ročník/číslo/stránka). Úložiště Fedora není hierarchické. Každému uzlu původní stromové struktury odpovídá digitální objekt FOXML s příslušným content modelem, jednotlivé objekty jsou do stromové struktury propojeny pomocí RDF vazeb definovaných v datovém streamu RELS-EXT.

## Pořadí objektů v hierarchii ##

Pořadí objektů zobrazovaných v hierarchické stromové struktuře K4 je určeno výhradně pořadím RDF vazeb uvedených v datastreamu RELS-EXT. Pořadí vazeb je možno editovat pomocí editoru, který je součástí instalace K4 ( [popis zde](MenuAdministrace.md))

Při konverzi dat do FOXML z formátů K3 nebo METS NDK je zachováno pořadí součástí uvedené v rámci původního konvertovaného dokumentu.

Pokud je titul do K4 importován po jednotlivých částech (například samostatné ročníky periodika), importní proces K4 automaticky přidá RDF vazby na nově importované součásti na konec datastreamu RELS-EXT  v již existujícím nadřazeném FOXML objektu. K tomuto spojení vazeb dojde tehdy, pokud existující i nově přidávaný nadřazený FOXML objekt mají stejný PID.



## Content modely ##

Datová struktura, vycházející z formátu dat Kramerius 3, předpokládá následující content modely:

  * monograph
  * monographunit
  * page
  * internalpart
  * periodical
  * periodicalvolume
  * periodicalitem

Definiční soubory těchto modelů jsou součástí  [POC](http://digit.lib.cas.cz/index.php?cat=fedora), aktuální verze jsou součástí balíčku installation v sekci downloads, případně v SVN.

Modely monograph a periodical jsou přiřazeny digitálním objektům, které tvoří kořen stromové struktury jednotlivých dokumentů. Při případném rozšiřování datové struktury Kramerius4 o nové typy objektů je třeba pro každý nový typ definovat odpovídající content model a nový kořenový model přidat do seznamu kořenových modelů, definovaných konfiguračním parametrem fedora.topLevelModels (viz [Instalace](Instalace.md))


## RDF vazby ##

| **název vazby** | **namespace** | **RDF:resource** | **poznámka** |
|:-----------------|:--------------|:-----------------|:--------------|
|hasModel | xmlns:fedora-model="info:fedora/fedora-system:def/model#" | content model | standardní vazba Fedory přiřazující content model |
|hasUnit |xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"| monographunit| vazba K4 přiřazující část monografie|
|hasPage |xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"| page| vazba K4 přiřazující stránku|
|hasVolume |xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"| periodicalvolume| vazba K4 přiřazující ročník|
|hasItem |xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"|periodicalitem| vazba K4 přiřazující číslo|
|hasIntCompPart| xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"| internalpart| vazba K4 přiřazující samostatnou část|
|isOnPage |xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"|page| vazba K4 přiřazující stránky k samostatné části|
|hasDonator | xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"| donator| vazba K4 na speciální FOXML objekt s logem případného donátora|
|contract |xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"|literal| identifikátor čísla zakázky|
|handle |xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"|literal| persistentní URL (identifikátor typu handle) |
|policy |xmlns:kramerius="http://www.nsdl.org/ontologies/relationships#"|literal| hodnota "public" nebo "private"|
|itemId |xmlns:oai="http://www.openarchives.org/OAI/2.0/"|literal| identifikátor, pod kterým bude objekt zveřejněn v protokolu OAI-PMH, obvykle identický s PID objektu|


# Rozšiřování modelu o nové typy dokumentů #
Digitální objekty v systému Kramerius 4 tvoří obecnou stromovou strukturu s vazbami definovanými  pomocí RDF vazeb, které jsou potomky vazeb fedora:hasPart a fedora:isPartOf.

Systém lze libovolně rozšiřovat o nové typy objektů přidáním příslušného objektu cmodel s povinnými datovými streamy DC, BIBLIO\_MODS a RELS\_EXT.

# Zobrazení loga donátora #

Ikona donátora se u titulu v K4 zobrazuje, pokud je v FOXML daného titulu v datastreamu `RELS-EXT` uvedena RDF vazba `kramerius:hasDonator`. V ukázkové monografii Drobnůstky, která je k dispozici v sekci Downloads na http://code.google.com/p/kramerius/downloads/detail?name=PrikladFOXML.zip, je následující RDF vazba:

```
 <kramerius:hasDonator rdf:resource="info:fedora/donator:norway"/>   
```

Cílem této vazby je speciální FOXML objekt ( v tomto případě má PID `donator:norway`), ve kterém je v datastreamu `LOGO` uloženo logo příslušného donátora ve formátu JPG.
Soubor s logem Norských fondů je součástí standardní distribuce K4, najdete jej v souboru `installation-x.x.x.zip` v adresáři fedora.

Chcete-li tedy v K4 zobrazovat ikonu jiných sponzorů, musíte jen  vytvořit podle vzoru `donator:norway` vlastní FOXML soubor s logem sponzora, jeho PID nastavit  například na `donator:mujSponzor` , nainstalovat jej do úložiště Fedora a v titulech, u kterých se má toto logo zobrazovat, doplnit RDF vazbu `<kramerius:hasDonator rdf:resource="info:fedora/donator:mujSponzor"/>`

_Poznámka - import z K3:
K3 takto obecně ikony donátora nepodporuje, obsahuje jen specifickou podporu pro případ Norských fondů, kde se jejich logo zobrazuje u titulů, které mají v poli CreatorSurname uveden text `***Donator NF***`. Konvertor K3->K4 pro takovéto tituly automaticky přidává do RELS-EXT zmiňovanou vazbu  `<kramerius:hasDonator rdf:resource="info:fedora/donator:norway"/>`._


# Přehled obsahu streamů DC a RELS\_EXT pro jednotlivé typy objektů #

## monograph ##

RELS\_EXT:
> hasModel

> hasUnit

> hasPage

> hasIntCompPart

> hasDonator

> contract (literal)

> handle (literal)

> policy (literal)

DC:
> title

> creator

> publisher

> contributor

> identifier - uuid

> identifier - contract

> identifier - isbn / extid

> identifier - handle

> subject - ddc

> subject - udc

> description

> date

> type

> language

> rights


## monographUnit ##

RELS\_EXT:
> hasModel

> hasPage

> hasIntCompPart

> contract (literal)

> handle (literal)

> policy (literal)

DC:
> title

> creator

> publisher

> contributor

> identifier - uuid

> identifier - contract

> identifier - handle

> type

> rights

IMG\_FULL, IMG\_THUMB, IMG\_FULL\_ADM, TEXT\_OCR, TEXT\_OCR\_ADM


## monograph internalpart ##

RELS\_EXT:
> hasModel

> isOnPage

> handle (literal)

> policy (literal)

DC:
> title

> creator

> contributor

> identifier - uuid

> identifier - handle

> type

> rights


## monograph page ##

RELS\_EXT:
> hasModel

> file (literal)

> handle (literal)

> policy (literal)

DC:
> title

> identifier - uuid

> identifier - handle

> type

> rights

IMG\_FULL, IMG\_THUMB, IMG\_FULL\_ADM, TEXT\_OCR, TEXT\_OCR\_ADM


## periodical ##

RELS\_EXT:
> hasModel

> hasVolume

> hasDonator

> handle (literal)

> policy (literal)

DC:
> title

> creator

> publisher

> contributor

> identifier - uuid

> identifier - issn

> identifier - handle

> subject - ddc

> subject - udc

> description

> date

> type

> language

> rights


## periodicalvolume ##

RELS\_EXT:
> hasModel

> hasItem

> hasPage

> hasIntCompPart

> contract (literal)

> handle (literal)

> policy (literal)

DC:
> title

> creator

> publisher

> contributor

> identifier - uuid

> identifier - contract

> identifier - handle

> description

> date

> type

> rights


## periodicalitem ##

RELS\_EXT:
> hasModel

> hasPage

> hasIntCompPart

> contract (literal)

> handle (literal)

> policy (literal)

DC:
> title

> creator

> publisher

> contributor

> identifier - uuid

> identifier - contract

> identifier - handle

> description

> date

> type

> rights

IMG\_FULL, IMG\_THUMB, IMG\_FULL\_ADM, TEXT\_OCR, TEXT\_OCR\_ADM


## periodical internalpart ##

RELS\_EXT:
> hasModel

> isOnPage

> handle (literal)

> policy (literal)

DC:
> title

> creator

> contributor

> identifier - uuid

> identifier - handle

> type

> rights


## periodical page ##

RELS\_EXT:
> hasModel

> file (literal)

> handle (literal)

> policy (literal)

DC:
> title

> identifier - uuid

> identifier - handle

> type

> rights

IMG\_FULL, IMG\_THUMB, IMG\_FULL\_ADM, TEXT\_OCR, TEXT\_OCR\_ADM


# Budoucí definice datových formátů #

Od roku 2012 budou v platnosti nové standardy datových formátů pro digitalizaci, vyvíjené v NK. K dispozici jsou na této adrese: http://ndk.cz/digitalizace/nove-standardy-digitalizace-od-roku-2011/anl-zadani-verze-1.0

# Optimalizace relací #

V rámci dalšího vývoje K4 vznikl dokument [Optimalizace relací v Kramerius 4](OptimalizaceRelaci.md)