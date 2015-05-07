## Zobrazovací šablony ##

Obsah většiny částí uživatelského rozhraní K4 je získáván z FOXML objektů nebo z výsledků dotazů do indexu SOLR a jejich transformací do požadovaného tvaru pomocí XSLT šablon. Výchozí šablony je možno přepsat umístěním vlastní šablony se stejným názvem jako má výchozí šablona do adresáře  `~/.kramerius4/xsl`.

V následující tabulce je uveden přehled názvů všech použitých šablon, odkazů na jejich výchozí obsah, zdroj dat, na který jsou aplikovány a místo v GUI, ve kterém je použit jejich výstup.

Zdrojem dat je buď XML obsah uvedeného datastreamu z FOXML objektu, jeho formát můžete najít v odpovídající veřejné specifikaci (MODS, ALTO). Pokud je vstupem dat výsledek dotazu do indexu SOLR,  jeho obecný tvar je pospán zde:

_SOLR response xml_: http://wiki.apache.org/solr/XMLResponseFormat

_SOLR Terms Component response_: http://wiki.apache.org/solr/TermsComponent

Konkrétní obsah formátu SOLR K4 je určen konfiguračním souborem schema.xml a šablonou K4.xslt, jejich obsah je posán níže v sekci Indexovací šablona.


| **Název souboru** (v adresáři `~/.kramerius4/xsl)` | **Výchozí šablona** (v souboru `search.war`) | **Vstupní data** | **Použití** |
|:------------------------------------------------------|:------------------------------------------------|:------------------|:--------------|
| alto.xsl | [/inc/details/xsl/alto.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/details/xsl/alto.xsl) | FOXML datastream  ALTO  | Zobrazení rámečku kolem vyhledávaného slova v náhledu |
| autocomplete.xsl | [/inc/home/xsl/autocomplete.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/home/xsl/autocomplete.xsl) | SOLR Terms Component response | Našeptávač |
| da.xsl | [/inc/results/xsl/da.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/results/xsl/da.xsl) | SOLR response xml | Časová osa |
| dt.xsl | [/inc/home/dt.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/home/dt.xsl) | SOLR response xml | Animace "Digitální knihovna obsahuje" na úvodní stránce |
| extInfo.xsl | [/inc/results/xsl/extInfo.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/results/xsl/extInfo.xsl) | SOLR response xml | Zobrazení informace o nadřazených objektech ve výsledcích vyhledávání |
| facets.xsl | [/inc/results/xsl/facets.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/results/xsl/facets.xsl) | SOLR response xml | Zobrazení facetu ve výsledcích vyhledávání |
| facets\_home.xsl | [/inc/home/xsl/facets\_home.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/home/xsl/facets_home.xsl) | SOLR response xml | Zobrazení záložky "Navigace" na úvodní stránce |
| grouped\_results.xsl | [/inc/results/xsl/grouped\_results.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/results/xsl/grouped_results.xsl) | SOLR response xml | Zobrazení centrální části výsledků vyhledávání |
| indexer.xsl | [/inc/admin/indexer.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/admin/indexer.xsl) | SPARQL response xml | Zobrazení dokumentu v dialogu "indexace dokumentů" |
| insearch.xsl | [/inc/details/xsl/insearch.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/details/xsl/insearch.xsl) | SOLR response xml | Zobrazení výsledků vyhledávaní uvnitř dokumentu |
| mods.xsl | [/inc/details/xsl/mods.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/details/xsl/mods.xsl) | FOXML datastream BIBLIO-MODS  | Zobrazení přehledu metadat dokumentu |
| modsFull.xsl | [/inc/details/xsl/modsFull.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/details/xsl/modsFull.xsl) | FOXML datastream BIBLIO-MODS | Zobrazení metadat dokumentu |
| not\_grouped\_results.xsl | [/inc/results/xsl/not\_grouped\_results.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/results/xsl/not_grouped_results.xsl) | SOLR response xml | Zobrazení centrální části výsledků vyhledávání |
| results\_header.xsl | [/inc/results/xsl/results\_header.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/results/xsl/results_header.xsl) | SOLR response xml | Zobrazení lišty stránkování výsledků vyhledávání |
| results\_main\_rss.xsl | [/inc/results/xsl/results\_main\_rss.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/results/xsl/results_main_rss.xsl) | SOLR response xml | Zobrazení rss výsledků vyhledávání |
| suggest.xsl | [/inc/details/xsl/suggest.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/details/xsl/suggest.xsl) | SOLR response xml | Zobrazení výsledků vyhledávání souvisejících dokumentů |
| treeNode.xsl | [/inc/details/xsl/treeNode.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/details/xsl/treeNode.xsl) | SOLR response xml | Zobrazení stromu v detailu |
| uncollapsed.xsl | [/inc/results/xsl/uncollapsed.xsl](http://code.google.com/p/kramerius/source/browse/trunk/search/web/inc/results/xsl/uncollapsed.xsl) | SOLR response xml | Zobrazení rozbalených výsledků |


## Indexovací šablona ##

Vstupem procesu indexace jsou jednotlivé objekty FOXML, na něž je aplikována indexační XSLT šablona, která data transformuje do tvaru definovaného v [konfiguračním schematu SOLR](http://code.google.com/p/kramerius/source/browse/trunk/installation/solr/conf/schema.xml).

Defaultní šablona je v knihovně indexer.jar uvnitř search.war, její obsah je [zde](http://code.google.com/p/kramerius/source/browse/trunk/indexer/src/cz/incad/kramerius/indexer/res/K4.xslt).

Šablonu je možno předefinovat souborem `K4.xslt` v  adresáři `~/.kramerius4/xsl`

Následující tabulka obsahuje přehled jednotlivých polí ze schematu SOLR a jejich mapování ve výchozí indexační šabloně:

| **Pole v `schema.xml`** | **Zdroj dat v FOXML** |
|:------------------------|:----------------------|
|  name="fedora.model" type="string"  | select="substring(/foxml:digitalObject/foxml:datastream[@CONTROL\_GROUP='X' and @ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/fedora-model:hasModel/@rdf:resource, 19)" |
|  name="document\_type" type="string" multiValued="true"  | `<`xsl:for-each select="/foxml:digitalObject/foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/oai\_dc:dc/dc:type"/`>` |
|  name="handle" type="string"  | select="/foxml:digitalObject/foxml:datastream[@CONTROL\_GROUP='X' and @ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/kramerius:handle/text()" |
|  name="status" type="string"  | select="foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state']/@VALUE" |
|  name="created\_date" type="date"  | select="foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#createdDate']/@VALUE" |
|  name="modified\_date" type="date"  | select="foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/view#lastModifiedDate']/@VALUE" |
|  name="datum\_str" type="string"  | ExtendedFields.java. Prochází postupně nadřazené FOXML a vybírá hodnoty mods:part/mods:date/text() nebo mods:originInfo[@transliteration='publisher']/mods:dateIssued/text() nebo mods:originInfo/mods:dateIssued/text() |
|  name="issn" type="string"  | `<`xsl:value-of select="mods:identifier[@type='isbn']/text()"/`>` nebo `<`xsl:value-of select="mods:identifier[@type='issn']/text()"/`>` |
|  name="mdt" type="string"  | `<`xsl:value-of select="mods:classification[@authority='udc']/text()"/`>` |
|  name="ddt" type="string"  | `<`xsl:value-of select="mods:classification[@authority='ddc']/text()"/`>` |
|  name="dostupnost" type="string"  | select="substring(/foxml:digitalObject/foxml:datastream[@CONTROL\_GROUP='X' and @ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/kramerius:policy, 8)" |
|  name="keywords" type="string" multiValued="true"   | `<`xsl:for-each select="mods:subject/mods:topic/text()"`>` |
|  name="collection" type="string" multiValued="true"   | `<`xsl:for-each  select="$rels/rdf:isMemberOfCollection"`>` `<`field name="collection" `>` `<`xsl:value-of select="substring-after(./@rdf:resource, 'info:fedora/')"/`>` `<`/field`>` `<`/xsl:for-each`>` |
|  name="dc.title" type="text\_fgs"  | select="translate(normalize-space(/foxml:digitalObject/foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/oai\_dc:dc/dc:title/text()),'&#xA;','')" |
|  name="title\_sort" type="string"  |   |
|  name="dc.creator" type="text\_fgs" multiValued="true"  | `<`xsl:for-each select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/oai\_dc:dc/dc:creator"`>` `<`field name="dc.creator" boost="1.5"`>` `<`xsl:value-of select="text()"/`>` `<`/field`>` `<`/xsl:for-each`>` |
|  name="language" type="string" multiValued="true"   | `<`xsl:for-each select="mods:language/mods:languageTerm/text()"`>` `<`field name="language"`>` `<`xsl:value-of select="." /`>` `<`/field`>` `<`/xsl:for-each`>` |
|  name="dc.description" type="text\_fgs" multiValued="true"  |    |
|  name="text\_ocr" type="text\_general"  | /foxml:digitalObject/foxml:datastream[@ID='TEXT\_OCR']/foxml:datastreamVersion[last()] |
|  name="details" type="string" multiValued="true"   | xpath per model. Used to show minimal data in tree |
|  name="img\_full\_mime" type="string"  | /foxml:digitalObject/foxml:datastream[@ID='IMG\_FULL']/foxml:datastreamVersion[last()]/@MIMETYPE" |
|  name="viewable" type="boolean"  | `<`xsl:if test="/foxml:digitalObject/foxml:datastream[@ID='IMG\_FULL']/foxml:datastreamVersion[last()]"/`>` |
