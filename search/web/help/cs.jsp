<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page isELIgnored="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/cmn.tld" prefix="view" %>

<view:object name="helpViewObject" clz="cz.incad.Kramerius.views.help.HelpViewObject"></view:object>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<title>Kramerius - Nápověda</title>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<META HTTP-EQUIV="Content-Language" CONTENT="cs">
	<META NAME="description" CONTENT="Nápověda aplikace Kramerius ">
	<META NAME="keywords" CONTENT="periodika, knihovna, kniha, publikace, kramerius">

	<META NAME="Copyright" content="">
	<LINK REL="StyleSheet" HREF="../css/styles.css" type="text/css">
	<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript" src="add.js"></SCRIPT>
</head>
<body marginwidth="0" marginheight="0" leftmargin="0" topmargin="0">

<table id="help" class="header ui-corner-top-8" >
<tr><td> <img src="../img/logo.png" border="0" /></td>
<td width="500px"> </td> <td>revize:${helpViewObject.revision}</td></tr>
</table>
    
<table cellpadding="0" cellspacing="0" border="0" height="100%">
  <tr>
    <td width="13" background="img/main_bg_grey.gif" height="100%"><img src="img/empty.gif" width="13" height="1" alt="" border="0"></td>
    <td valign="top" width="100%">

    <table cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr>
        <td height="20" width="100%" class="mainHeadGrey"><div align="right"><a href="javascript:print();"   class="mainServ">TISK</a>&nbsp; - &nbsp;<a href="javascript:openhelp=window.close();" class="mainServ">ZAVŘÍT OKNO</a><a name="top">&nbsp;</a>&nbsp;</div></td>
      </tr>
      <tr>
        <td width="100%" colspan="3"><img src="img/empty.gif" width="1" height="2" alt="" border="0"></td>
      </tr>

      <tr>
        <td width="100%" class="mainHeadGrey"><img src="img/empty.gif" width="1" height="1" alt="" border="0"></td>
      </tr>
      <tr>
        <td valign="top" align="left" width="100%">
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
          <tr>
            <td><img src="img/empty.gif" width="15" height="1" alt="" border="0"></td>
            <td>


<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
  <td class="mainHeadGrey" height="1"><img src="img/empty.gif" width="1" height="1" alt="" border="0"></td>
</tr>
</table>
<br><div align="right"><a href="#top" name="a" class="mainServ">TOP</a></div>
<strong>Co najdu v Digitální knihovně</strong><br><br>

    Digitální knihovna může obsahovat digitalizované dokumenty (například vzácné a ohrožené dokumenty digitalizované v národních programech Memoriae Mundi Series 
    Bohemica a Kramerius) i elektronické dokumenty vytvořené přímo v digitální podobě (například odborné články ve formátu PDF). <p>
    Přístup do Digitální knihovny je omezen přístupovými právy. 
    Metadata, tj. bibliografické a další popisy dokumentů jsou přístupné všem, obrazová data dokumentů nechráněných 
    autorským zákonem také, obrazová data chráněná autorským zákonem pouze uživatelům přistupujícím 
    z počítačů umístěných v prostorách příslušné knihovny vlastnící dokument.
<br><br>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
  <td class="mainHeadGrey" height="1"><img src="img/empty.gif" width="1" height="1" alt="" border="0"></td>
</tr>
</table>
<br><div align="right"><a href="#top" name="b" class="mainServ">TOP</a></div>
<strong>Co potřebuji pro práci v Digitální knihovně?</strong><br><br>
Moderní prohlížeč webových stránek, nejlépe Firefox 3, Safari, Google Chrome nebo Internet Explorer 8. 
Pro zobrazení souborů DjVu a PDF je třeba doinstalovat příslušný plugin, prohlížeč Vám jej sám nabídne. 
Doporučené rozlišení Vašeho monitoru je 1024x768 bodů a více. Při menším rozlišení počítejte se ztížením manipulace s obrazovými soubory.
<p>

<br><div align="right"><a href="#top" name="a" class="mainServ">TOP</a></div>
<strong>Úvodní obrazovka</strong> <br><br>
Úvodní obrazovka slouží především k vyhledávání dokumentů v digitální knihovně. <p>
<b>Záhlaví stránky</b><p>
Velké pole uprostřed záhlaví stránky umožňuje vyhledávat v plném textu dokumentů v digitální knihovně - zapište hledané slovo a klikněte na symbol lupy. 
Pro hledání je možné použít zástupný znak * nahrazující libovolnou část slova.<p>
Odkaz Pokročilé vyhledávání otevře dialogové okno s formulářem pro vyhledávání v metadatech (MODS) dokumentů
 - můžete hledat podle ISBN/ISSN, autora, názvu, roku vydání a třídění MDT/DDT a můžete také omezit výběr na pouze veřejné dokumenty. Vyhledávání spustíte kliknutím na tlačítko OK. 
 Okno pro pokročilé vyhledávání zavřete stiskem křížku v jeho pravém horním rohu.<p>
V pravé části záhlaví stránky je odkaz pro zobrazení této nápovědy, přepínání jazyka uživatelského rozhraní a přihlášení do administrátorské části aplikace.<p>
<b>Rámeček Procházet</b><p> 
Je určen k rychlému vyhledání dokumentu podle názvu nebo autora. 
Během postupného zapisování písmen se pod příslušným polem objeví okénko se seznamem nalezených dokumentů. 
Nerozlišují se malá a velká písmena, je ale potřeba dodržovat diakritiku. <p>
<b>Rámeček Typ dokumentu</b><p>
Kliknutím na některou položku v rámečku Typ dokumentu jsou vyhledány pouze dokumenty příslušného typu. 
Čísla v závorce za jednotlivými položkami udávají počet odpovídajících dokumentů v digitální knihovně.<p>
<b>Časová osa</b><p>
Rámeček Časová osa obsahuje histogram s počty dokumentů vydaných v jednotlivých letech. 
Zapsáním roků do políček v horní části rámečku (nebo posunutím značek vertikálních posuvníků) můžete omezit vyhledávání na požadované časové období. 
Oba výběry je potřeba potvrdit stiskem lupy umístěné vedle polí Od a Do.<p>
<b>Záložky Nejnovější a Nejžádanější</b><p>
Pod záložkou Nejnovější jsou ikony s miniaturami prvních stránek dokumentů nejnověji přidaných do digitální knihovny. Kliknutím na ikonu příslušný dokument zobrazíte. 
Podobně záložka Nejžádanější obsahuje ikony nejčastěji zobrazovaných dokumentů.<p>
<br><br>
<br><div align="right"><a href="#top" name="a" class="mainServ">TOP</a></div>
<strong>Výsledky vyhledávání</strong> <br><br>
Tato stránka je zobrazena po spuštění vyhledávání z úvodní obrazovky. 
V prostředním rámečku je seznam nalezených dokumentů seskupených podle titulů nejvyšší úrovně (tedy většinou monografií nebo periodik). 
Jednotlivé dokumenty náležející k titulu je možno zobrazit kliknutím na šipku vpravo od titulu, v rozbaleném seznamu se jich na jedné stránce zobrazí 10.  
Případné stránkování je možné klikáním na jednotlivá čísla stránek.
Tituly v rámečku lze třídit podle relevance nebo abecedně podle názvu, kliknutím na příslušný odkaz v pravé části záhlaví rámečku.<p>      
Rámečky na levé straně obrazovky obsahují položky pro výběr jednotlivých podkategorií v rámci vyhledaných dokumentů, 
funkce je obdobná rámečku Typ dokumentu na úvodní straně.<p>
V pravé části obrazovky je časová osa se stejnou funkcí jako na úvodní straně.<br><br>

<br><div align="right"><a href="#top" name="a" class="mainServ">TOP</a></div>
<strong>Zobrazení titulu</strong><br><br>
Obrazovka třetí úrovně slouží k přehlednému zobrazení titulu, jeho metadat a listování v jeho obsahu.<p>
V horní části je pás ikonek s miniaturami všech stránek v dokumentu nebo jeho vybrané části. 
Pásem je možno procházet šipkami na jeho koncích nebo posuvníkem pod ním. 
Posuvníkem si prohlížíte miniatury stránek, šipkami pak přecházíte mezi jednotlivými náhledy. 
Stejnou funkci pak mají i šipky vpravo a vlevo na klávesnici. 
Pás s přehledem stran není zobrazen pro dokumenty uložené v jediném souboru PDF.<p>
Hlavní prostor stránky zabírá náhled na vybranou stránku. 
Pokud je přístup k obsahu dokumentu omezen z důvodu ochrany autorských práv, je místo náhledu zobrazen text informující o této skutečnosti. 
 Vpravo od náhledu se nacházejí 3 tlačítka - plus, šipka vlevo a šipka vpravo. Šipky opět slouží k přecházení mezi jednotlivými náhledy. 
 Po stisknutí tlačítka plus jsou zpřístupněna tlačítka Zvětšit, Zmenšit, Domů a Plná stránka. Tato tlačítka ovšem zmizí při přechodu na další náhled.<p>
 Tlačítky Zvětšit a Zmenšit si můžete náhled přiblížit nebo oddálit, stejně funguje i rolovací tlačítko u myši. 
 Původní velikost náhledu získáte stiskem tlačítka Domů. Tlačítko Plná stránka roztáhne náhled přes celou stránku.
  I zde jsou pak dostupná tlačítka Zvětšit, Zmenšit, Domů, Plná stránka a šipky doleva a doprava. 
  Tlačítka jsou dostupná i při přechodu na další náhled. Náhled přes celou stránku zrušíte dalším stiskem tlačítka Plná stránka.
 <p>
Rámeček vpravo od náhledu slouží k navigaci ve struktuře titulu. 
Jednotlivé záložky odpovídají příslušným úrovním v hierarchii stromové struktury dokumentů, 
pokud je na dané úrovni více dokumentů (např. ročníků nebo stran), obsahuje záložka šipku, 
kterou můžete zobrazit rozevírací nabídku pro přechod mezi danými dokumenty. Opětovným stiskem šipky rozevírací nabídku opět skryjete.
Pod záložkou je pak zobrazen výběr z metadat aktuálního dokumentu. 
Šipkou zcela u okraje rámečku vpravo od záložky můžete otevřít kontextovou nabídku s operacemi, 
které je možné aplikovat na dokument vybraný v dané záložce.<p>
K dispozici jsou tyto operace:<br>   
<b>Zobrazit metadata:</b> Otevře okno s bibliografickými údaji daného dokumentu ve formátu MODS.<br>
<b>Persistentní URL:</b> Zobrazí URL dokumentu, které lze použít jako záložku pro přímý přístup k dokumentu z externích aplikací.<br>
<b>Generování PDF:</b> Umožňuje vygenerovat a stáhnout PDF dokument obsahující zadaný rozsah stran začínající vybranou stránkou. Maximální počet stran v PDF dokumentu může být omezen.<br>
<b>Stáhnout originál</b> (jen pro typ dokumentu stránka):  Stáhne naskenovaný dokument v plné velikosti.<br>
<b>Záznam METS:</b> Otevře okno se záznamem ve formátu Fedora METS pro vybraný dokument.<p>

<br><br>

 
<br><br>

            </td>
            <td><img src="img/empty.gif" width="15" height="1" alt="" border="0"></td>
          </tr>

        </table>
        </td>
      </tr>
    </table>
    </td>
  </tr>
</table>
</body>
</html>