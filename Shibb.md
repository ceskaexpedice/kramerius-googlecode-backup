# Úvod #

Aplikaci kramerius je možné integrovat s autentizačním systémem Shibboleth. Integrace je postavena na standardních komponentách dodávaných konzorciem
[Internet2](http://internet2.edu/).

# Předpoklady #
Článek předpokládá, že čtenář má zkušenosti s konfigurací webového serveru apache, servlet kontejneru tomcat, služby shibboleth service provider .
Důležité odkazy:

  * http://shibboleth.net/downloads/service-provider/
  * https://wiki.shibboleth.net/confluence/display/SHIB2/Installation+and+Configuration
  * http://httpd.apache.org/docs/trunk/
  * http://tomcat.apache.org/tomcat-7.0-doc/index.html


# Nastavení aplikace #

Správně nastavený service provider propaguje přihlášeného uživatele (identitu a atributy získáné z IdP) přímo do servlet kontaineru
tomcat, aplikace K4 umí tyto atributy svázat (pomocí mapovacího souboru) s rolí resp. rolemi.



### Mapující soubor ###

Mapování je definováno souborem `~/.kramerius4/shibrules.txt`. Struktura souboru je následující:

```

	shibrules = matchrule+

	matchrule = 'match' condition body
	condition =  '(' value ',' value ')'
	body = '{'  (command | matchrule) * '}'
	command= userassoc | roleassoc

	userassoc = 'user' '(' userattr ',' value ')'  // asociuje vlastnost
        userattr = '"' userattrval '"'  
        userattrval = firstname|surname //vycet vlastnosti - jmeno, prijmeni

	roleassoc = 'role' '(' STRING ')' // asociuje roli
	
	value = STRING | funcvalue // reprezentuje hodnotu, buď string nebo funkci

	funcvalue = 'header' '(' STRING ')' | 'principal' '(' ')'  // buď vrátí přihlášeného uživatele nebo hodnotu z hlavičky.

```


### Příklady ###

```

/** aplikuje se pokud je  v hlavičce definován attribut AJP_affiliation a má hodnotu knav */
match(header("AJP_affilation"),"knav") {
       user("firstname",header("AJP_cn"))  // jméno uživatele přiřadí z hodnoty atributu cn
       user("surname", header("AJP_surname"))  // příjmení uživatele přiřadí z hodnoty atributu surname

       role("k4_admins")  // přiřazení rolí
       role("knav_users")
       role("shib_users")
}

```


```

/** aplikuje se, pokud je v přihlášený uživatel (loginname) happy */
match((principal(),"happy") {
       role("k4_admins")
}

```


### Poznámky k sémantice ###

#### Pravidlo match ####

Řídí se podmínkou definovanou v závorkách. Pokud je podmínka splněna, provádí se kód v těle pravidla. Při vykonávání se provedou všechna pravidla mapovacího souboru pro která platí, že podmínka je rovna ` true `

#### Přiřazení vlastnosti uživateli (příkaz user) ####

Umožňuje přiřadit vlastnost uživateli. Momentálně systém umožňuje přiřadit jméno a příjmení:
```
       user("firstname",...
       user("surname", ....
```

Opakované volání funkce přepíše původní hodnotu.

#### Přiřazení role (příkaz role) ####

Umožňuje přiřadit roli uživateli. Pokud se přiřazuje role, která není aplikací k4 definována, aplikace vypíše chybovou hláškou ale vykonávání pravidla pokračuje dál.

#### Funkce principal ####

Vrací indentifikátor přihlášeného uživatele. Obvykle se jedná o přihlašovací jméno.

Konkrétně se jedná o následující volání:
  * http://download.oracle.com/javaee/1.4/api/javax/servlet/http/HttpServletRequest.html#getUserPrincipal()
  * http://download.oracle.com/javase/1.4.2/docs/api/java/security/Principal.html#getName()