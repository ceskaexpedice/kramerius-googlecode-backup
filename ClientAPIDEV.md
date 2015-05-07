


# POZNAMKA #
Pracovni dokument. Vse co je napsno nize se muze v prubehu praci menit!

# O clanku #
Strucny pracovni popis klientskeho API.

# Cil #
Cilem je vytvorit ucelene API, pomoci ktereho je mozno vytvaret nove alternativni klienty pro system K4. API by melo pokryvat veskerou funkcionalitu klientske casti.

Implementacne se vychazi z [RemoteAPI](RemoteAPI.md) a to je obohacovano o nove zdroje nesouci informace o zobrazovanych titulech. Standardnim formatem je JSON, pri ziskavani xml metadat (mods, dc) pak xml.

Prvni implementace samostatneho klienta pracujici nad timto api se predpoklada pro system CDK. (cdk-test.lib.cas.cz)

