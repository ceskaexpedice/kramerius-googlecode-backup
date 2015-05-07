# Úvod #

Kramerius 4 je standardní webová aplikace v jazyce Java odpovídající specifikacím Servlet API 2.4 a Java Server Pages 1.1. Aplikace je definována v hlavním modulu [search](http://code.google.com/p/kramerius/source/browse/#svn%2Ftrunk%2Fsearch).

**Způsob dokumentace projektu**
  * Popis obecné struktury aplikace K4
  * Popis jednotlivých modulů a informace o jejich účelu
  * Popis hlavních tříd, které mají dokumentované public API
  * Popis, jak postupovat, když bude někdo chtít přispívat do kódu

# Přehled modulů #

![http://kramerius.googlecode.com/svn/wiki/Modules.png](http://kramerius.googlecode.com/svn/wiki/Modules.png)

Kromě hlavního modulu aplikace [search](http://code.google.com/p/kramerius/source/browse/#svn%2Ftrunk%2Fsearch) je systém Kramerius 4 tvořen následujícími moduly:

### Společné moduly ###

  * common - služby sdílené jednotlivými moduly
    * [FedoraAccess](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/FedoraAccess.java) - přístup ke službám REST API fedora commons
    * [SolrAccess](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/SolrAccess.java) - přístup ke službám indexu SOLR
    * [database](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/#kramerius%2Fdatabase) - správa verzí databáze K4
    * [imaging](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/#kramerius%2Fimaging) - podpora práce s obrazovými daty. Využívá standardní knihovnu Java ImageIO. Kodeky pro další psecifické formáty je možno najít např. zde: https://github.com/geosolutions-it/imageio-ext
    * [pdf](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/#kramerius%2Fpdf) - online generování PDF
    * [printing](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/#kramerius%2Fprinting) - tisková fronta na serveru
    * [processes](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/#kramerius%2Fprocesses) - API pro správu administrátorských procesů  ([RemoteAPI](RemoteAPI.md) - Popis remote API k procesům, [Processes](Processes.md) - Popis implemtenace procesů)
    * [security](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/#kramerius%2Fsecurity) - API pro správu přístupových práv ([Prava](Prava.md) - Popis práv, [PravaStreams](PravaStreams.md) - Rozšíření práv o podporu streamů)
    * [service](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/#kramerius%2Fservice) - ostatní sdílené služby
    * [virtualcollections](http://code.google.com/p/kramerius/source/browse/trunk/common/src/main/java/cz/incad/kramerius/#kramerius%2Fvirtualcollections) - podpora virtuálních sbírek
  * fedora-api - mapování SOAP API-M fedora commons
  * security-core - společné služby správy uživatelů a rolí

### Moduly administrátorských procesů ###

  * import - třídy pro spouštění importních procesů z formátu K3 a FOXML
  * import-mets - konvertor z nové specifikace formátu METS
  * indexer - proces indexace dokumentů v indexu SOLR
  * static-export - export dokumentů ve formátu PDF na CD a DVD

Podpůrné moduly procesů import a import-mets:

  * import-cmd-tool - konvertor z formátu K3, generátor FOXML
  * import-jaxb-dc - JAXB mapování schematu Dublin Core
  * import-jaxb-mets - JAXB mapování schematu METS
  * import-jaxb-mods - JAXB mapování schematu MODS
  * import-jaxb-periodical - JAXB mapování schematu periodik Kramerius3
  * import-jaxb	- JAXB mapování schematu monografií Kramerius3


### Moduly externích administrátorských aplikací ###

  * editor - grafický editor pořadí součástí v datastreamu RELS-EXT
  * rightseditor - editor uživatelských účtů  využitelný při použití databázového security realmu pro aplikační servery bez vlastní správy uživatelů (Tomcat)


# Build #

Kramerius4 využívá standardní buildovací systém [Maven](http://maven.apache.org). Základní deskriptor [POM](http://code.google.com/p/kramerius/source/browse/trunk/pom.xml) aplikace je v kořenovém adresáři projektu, slouží zároveň jako multi-module POM i parent POM pro jednotlivé moduly systému.

Distribuční archiv search.war lze vytvořit příkazem `mvn install` v kořenovém adresáři projektu. Potřebné knihovny závislostí jsou automaticky staženy z centrálního úložiště systému Maven.


# Programátorské konvence #

Přehled programátorských konvencí, používaných při vývoji sytsému Kramerius 4, je na stránce wiki StyleGuidelines .