


# Statistiky #
Byla přidána možnost sledování statistik přístupů na jednotlivé tituly a jejich agregace. Sleduje se každý přístup na datové streamy IMG\_FULL a IMG\_PREVIEW, a
a záznam o přístupu se ukládá do servisní databáze.  Statistiky jsou dostupné pomocí administrátoreského menu:

![http://kramerius.googlecode.com/svn/wiki/features_47/statistiky-menu.png](http://kramerius.googlecode.com/svn/wiki/features_47/statistiky-menu.png)

![http://kramerius.googlecode.com/svn/wiki/features_47/statistiky-dialog.png](http://kramerius.googlecode.com/svn/wiki/features_47/statistiky-dialog.png)

## Agregace ##

Jsou k dispozici následující druhy agregací:

  * Dle modelu
  * Dle rozsahu datumu
  * Dle autora
  * Dle jazyků

Po volbě druhu agregace se objeví dialog s grafem a tabulkou zobrazující data.

![http://kramerius.googlecode.com/svn/wiki/features_47/statistiky-agregace.png](http://kramerius.googlecode.com/svn/wiki/features_47/statistiky-agregace.png)


## Sledované informace ##

Při přístupu na datový stream se ukládá následující:

  * PID objektu obsahujícího IMG\_FULL případne IMG\_PREVIEW
  * Datum a čas přístupu
  * IP adresa dotazovatele
  * Uživatelské jméno (pokud byl uživatel přihlášen)

Pro každý záznam existuje vazba 1:N detailním záznamů, které reprezentují úplnou informaci o zobrazovaném titulu. Příklad pro periodikum: Pro každý přístup
na datový stream se ukládá informace o zobrazené stránce, zobrazeném výtisku periodika, zobrazeném ročníku periodika, zobrazeném periodiku.
Detailní záznamy obsahují:
  * PID objketu
  * Model objketu
  * Datum vydání monografie nebo periodika
  * Autor případně autoři
  * Příznak viditelnosti
  * Jazyk
  * Název
  * Prováděná akce (Tisk, Generování PDF, Prohlížení)

Každá z agregací může mít výstup v CSV nebo XML formátu.

## Získání plného logu ##
Pomocí tlačítek v dialogu je možno získat plný log ve formátu XML.


# Prohlížečka zoomify #
Pro prohlížení obrázků z image serveru se nyní standardně používá prohlížečka zoomify. Původní prohlížečka je dostupná jako alternativa pomocí konfigurační
položky:
```
    # mozne volby deepzoom|zoomify
    zoom.viewer=deepzoom
```

# Mazání starších procesů #
Do administrátorksé části menu byla přídána akce umožňující mazat starší, skončené procesy. Mazat lze dle zvoleného filtru. Viz. Dialog:

![http://kramerius.googlecode.com/svn/wiki/features_47/processes-delete-dialog.png](http://kramerius.googlecode.com/svn/wiki/features_47/processes-delete-dialog.png)

# Automatické třídění součástí dokumentů #

Ve verzi od verze 4.7.1.beta přibyla možnost automatického třídění částí dokumentů a to buď jako součást
procesu import. Řídí se konfigurační proměnnou ze souboru  [migration.properties](http://code.google.com/p/kramerius/source/browse/trunk/import-cmdtool/src/main/java/res/configuration.properties):
```
    ingest.sortRelations=false
```


Nebo je možné proces spouštět samostatně z kontextového menu.

Proces třídí součásti podle dat v jejich datastreamu BIBLIO\_MODS, příslušný údaj je vybírán pomocí výrazu XPath, který je definován v konfigurační property sort.xpaths ze souboru [configuration.properties](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/res/configuration.properties), jejíž defaultní nastavení je následující:

```
sort.xpaths=page;//mods:mods/mods:part/mods:detail[@type='pageIndex']/mods:number;true,\
  periodicalvolume;//mods:mods/mods:part/mods:detail[@type='volume']/mods:number | //mods:mods/mods:titleInfo/mods:partNumber;true,\
  periodicalitem;//mods:mods/mods:part/mods:detail[@type='issue']/mods:number | //mods:mods/mods:titleInfo/mods:partNumber;true,\
  monographunit;//mods:mods/mods:part/mods:detail/mods:number | //mods:mods/mods:titleInfo/mods:partNumber;true,\
  internalpart;//mods:mods/mods:part/mods:detail[@type='pageNumber']/mods:number | //mods:mods/mods:titleInfo/mods:partNumber;true,\
  article;//mods:mods/mods:part/mods:detail[@type='pageNumber']/mods:number | //mods:mods/mods:titleInfo/mods:partNumber;true,\
  supplement;//mods:mods/mods:part/mods:detail[@type='pageNumber']/mods:number | //mods:mods/mods:titleInfo/mods:partNumber;true,\
  picture;//mods:mods/mods:part/mods:detail[@type='pageNumber']/mods:number | //mods:mods/mods:titleInfo/mods:partNumber;true
```

Jednotlivé řádky tohoto nastavení obsahují 3 prvky oddělené středníkem - první je název FOXML modelu, druhý je XPath pro výběr z BIBLIO\_MODS a poslední je logická hodnota určující, jestli vybraná hodnota má být tříděna jako číslo (nastavení true) nebo jako řetězec (nastavení false). Výchozí nastavení selektorů Xpath obsahují alternativy jak pro původní formát MODS používaný při konverzi dat z K3, tak nový formát MODS podle specifikace NDK.


# Podpora IIP image serveru v konvertoru METS NDK #


Nastavení property `convert.useImageServer=true` (v `migration.properties`) změní chování konvertoru METS NDK -> K4 tak, že namísto ukládání obrazových souborů v datastreamech IMG\_FULL, IMG\_PREVIEW a IMG\_THUMB přesune zdrojové soubory do nakonfigurovaného adresáře s dokumenty imageserveru a příslušné datastreamy nastaví jako externí reference do imageserveru. Adresáře imageserveru a tvar URL jednotlivých externích referencí se nastavují pomocí následujících hodnot v `migration.properties` :

```
# when true, images in FOXML files will be external references to IIP Image Server
convert.useImageServer=false

# base directory of the image server data. Image files will be copied to the subfolders of this directory, name of hte subfolder is the packageid of the imported PSP
convert.imageServerDirectory=${sys:user.home}/iip-data

# base URL of the tiles references from FOXML to imageserver. This base URL will be appended with packageid, image filename and convert.imageServerSuffix.tiles (see properties convert.imageServerSuffix...)
convert.imageServerTilesURLPrefix=http://localhost/fcgi-bin/iipsrv.fcgi?Zoomify=${convert.imageServerDirectory}

# base URL of the image references from FOXML to imageserver. This base URL will be appended with packageid, image filename and appropriate suffix (see properties convert.imageServerSuffix...)
convert.imageServerImagesURLPrefix=http://localhost/fcgi-bin/iipsrv.fcgi?FIF=${convert.imageServerDirectory}

# suffix of the image reference URL for full size images
convert.imageServerSuffix.big=&CVT=jpeg

# suffix of the image reference URL for thumbnail images
convert.imageServerSuffix.thumb=&HEI=128&CVT=jpeg

# suffix of the image reference URL for preview images
convert.imageServerSuffix.preview=&HEI=700&CVT=jpeg

# suffix of the image reference URL for zoomify/deepzoom tiles
convert.imageServerSuffix.tiles=

```