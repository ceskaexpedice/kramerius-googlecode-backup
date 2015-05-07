

# Replikace K4 #
Od verze 4.6 je možno replikovat data mezi jednotlivými instancemi
K4 pomocí funkce replikace.

# Popis procesu #
Proces umožňuje na základě znalosti persistentní adresy ( K4 handle ) a replikačního účtu replikovat data ze vzdáleného repositáře do repositáře cílového.
Proces je složen ze tří fází které  nejlépe vystihuje následující diagram.

![http://kramerius.googlecode.com/svn/wiki/features_46/K4replications.png](http://kramerius.googlecode.com/svn/wiki/features_46/K4replications.png)


  * Nejdříve (bod 1.) si replikační proces vyžádá replikační deskriptor.  Pokud deskriptor dostane, uloží ho a pokusí parsovat. Tím končí fáze 1.
  * Potom (bod 2.) algoritmus prochází jednotlivé identifikátory objektů a pro každý získá FOXML objekt, ten importuje do cílového uložistě.  Po zpracování všech identifikátorů končí fáze 2.
  * Nakonec (bod 3.) smaže nekonzistence (v případě replikování pouze časti dokumentu, ne celku) a  naplánuje indexaci. Fáze 3.



# Gui #
Spouštění procesu se děje pomocí vstupního dialogu, kde je nutno vyplnit základní informace (K4 handle, replikační účet).

![http://kramerius.googlecode.com/svn/wiki/features_46/replication-template-input.png](http://kramerius.googlecode.com/svn/wiki/features_46/replication-template-input.png)


Výsledek procesu je možné (krom standardních výstupů) kontrolovat ve výstupní šabloně schované pod položkou .


![http://kramerius.googlecode.com/svn/wiki/features_46/replication-template-item.png](http://kramerius.googlecode.com/svn/wiki/features_46/replication-template-item.png)


![http://kramerius.googlecode.com/svn/wiki/features_46/replication-template.png](http://kramerius.googlecode.com/svn/wiki/features_46/replication-template.png)


V případě, že se ve fázi 2 nebo 3 objeví chyba, je možné celý proces restartovat. Tím se nastartuje nový proces,
který ovšem bude pokračovat až tam, kde předchozí skončil. Restart je možné provést z výstupní šablony.

![http://kramerius.googlecode.com/svn/wiki/features_46/replication-template-error.png](http://kramerius.googlecode.com/svn/wiki/features_46/replication-template-error.png)

_Poznámka:_ Získání replikačního deskriptoru může pro rozsáhlé tituly trvat poměrně dlouho. V případě potíží je třeba zvýšit nastavení timeoutů requestů na serveru zdroje replikace.

# Nastavení práv #

Pro replikace jsou definovány akce pro import a export:

  * export\_k4\_replications
  * import\_k4\_replications

## Akce export\_k4\_replications ##
Definuje možnost exportu deskriptoru a foxml souboru na straně poskytující data (exportujícího serveru).
Akci je možno definovat ja jakékoliv úrovni objektového stromu. Nastavení probíhá pomocí kontextového menu.


## Akce import\_k4\_replications ##
Akce operaci importu vyexportovaného foxml do cílového uložiště. Právo s touto akcí je možno definovat pouze na úrovni
kořenového objektu REPOSITORY.

## Poznámky k implementaci ##

Replikace používá pro získání dat [RemoteAPI](RemoteAPI.md).

Pokud jsou data v exportující instanci externě referencovaná, tzn. leží mimo repositář, mohou nastat dva případy.

  * URL je definováno protokolem file
  * URL je definováno protkolem http resp. https

Pro první případ platí, že exportující instance získá data z uložiště, obsah vloží přímo do exportovaného foxml zakódovaný v Base64 a datový stream změní na typ `Managed Content`.
Pro druhý případ platí, že data zůstávají externě referencovaná i v cílovém repositáři, URL zůstane nezměněno.

Pokud se replikuje pouze část celku (výtisk periodika, ročník, část monografie, atd..) proces provádí kontrolu konzistence a ruší
vazby z vyšších částí na nižší, dosud nereplikované.

Pokud digitální objekt obsahuje odkaz na IIPserver, zůstává tento nezměněn.