# Optimalizace relací v Kramerius 4 #

## Popis stávajících relací ##
Hierarchie Fedora objektů jsou popsány prostřednictvím RDF vazeb. Každá RDF vazba určuje vztah mezi dvěma objekty a v případě hierarchie popisuje vazbu mezi objekty předek a potomek.

Kramerius používá k popisu hierarchie několik vlastních [relací](Data#RDF_vazby.md) (isOnPage, hasDonator, hasIntCompPart, hasItem, hasPage, hasUnit, hasVolume) definovaných nelogicky v XML namespace http://www.nsdl.org/ontologies/relationships#. Původně byla množina vazeb a modelů pevně daná a systém mohl pomocí těchto vazeb optimalizovat práci s hierarchiemi objektů.

**Příklad 1:** stačilo načíst vazby objektu monografie a v případě vazeb hasPage nebylo nutné načítat objekty těchto potomků, protože se vědělo, že stránka už žádné nemá.

**Příklad 2:** z načtených vazeb šlo určit typ (model) potomků, aniž by se načítaly jejich objekty.

Nyní lze systém modelů volně rozšiřovat a s tím je nutné přidávat také nové relace. Kramerius proto musí na relace pohlížet obecně a není možné využít znalosti o dané vazbě. To systém komplikuje a hlavně to snižuje jeho výkonnost. Bude nutné vyřešit následující problémy.

### Problém 1: neexistující popis relací ###
Relace nejsou v systému nikde definované. Ani v modelu, ani pomocí DTD/XSD.

Z toho vyplývá nemožnost validace dat při importu. Je vazba platná? Odkazuje na správný typ objektu?  hasPage -> model:page vs. hasPage -> model:monograph

Editor má problém s vytvářením nových objektů a vazeb. Není možné prezentovat nějaký seznam.

Lze řešit deklarací možných relací a jejich vtahu k modelům objektů. Přineslo by pouze složitější metamodel.

### Problém 2: rychlost načítání vazeb ###
Stávající implementace průchodem FOXML dokumentů je značně neefektivní. Výpis hierarchie např. Lidových novin, což je asi 280000 objektů, zabere 2 h 10 min 31 s.

Při využití resource indexu, viz. níže, lze čas zkrátit na 6 min 15 s. Tento čas je pak ale lineárně závislý na počtu vazeb.

### Problém 3: nemožnost efektivních dotazů ###
Nemožnost efektivních dotazů na potomky a jejich vlastnosti. Chci-li zjistit první objekt v hierarchii, který obsahuje ikonu nutnou pro prezentaci rodičovského objektu, např. monografie, je potřeba postupně načítat objekty hierarchie a kontrolovat jestli obsahují příslušný datastream. Toto výrazně zpomaluje načtení stránek v uživatelském rozhraní nebo export ikon pro registr digitalizace.

## Návrh řešení ##
Jako nezbytnost se jeví využití Fedora resource indexu, který obsahuje RDF vazby všech objektů v systému. Tento index je automaticky udržovaný Fedorou a oproti získání informací čtením FOXML dokumentů dosahuje index řádově lepší časy.

Pro efektivní využití indexu je nutné konsolidovat vazby definující hierarchii. Jelikož rozlišení vazeb v současném systému nic nepřináší, systém je nedokáže využít, navrhuji tyto vazby nahradit jedinou standardní RDF vazbou, která by definovala hierarchie objektů. Tento přístup by odstranil problém 1 a výrazně by zrychlil řešení ostatních problémů. Umožnilo by to minimalizovat počet dotazů do indexu.

Fedora definuje [základní množinu vazeb](http://www.fedora.info/definitions/1/0/fedora-relsext-ontology.rdfs). Pro naše účely by byla nejvhodnější vazba hasPart nebo hasMember.

## Dopad ##
### Transformace vazeb ###
Největším dopadem navrhovaného řešení je nutnost konverze existujících FOXML objektů, ve kterých by bylo potřeba stávající vazby transformovat.

Jedná se především o instalaci kramerius4.mzk.cz, ~ 900000 objektů. Transformace by mohla trvat asi 1-2 dny.

V případě NKP je situace mnohem jednodušší. Existující FOXML objekty nebyly dosud importované a transformace by mohla probíhat až při jejich importu.

### Úprava editoru vazeb a MZK editoru ###
Tato úprava bude nutná tak jako tak, protože editory nyní počítají s neměnnou množinou vazeb.

## Nevýhody ##
Při použití jediné vazby nebude běžným přečtením FOXML dokumentu předka zřejmé, jakého typu jsou odkazované objekty potomků. To může ztížit ruční úpravu objektu uživatelem.