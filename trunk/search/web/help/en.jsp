<%@page import="java.util.Locale"%>
<%@page import="com.google.inject.Provider"%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page isELIgnored="false" %>
<%@page import="java.io.*, cz.incad.kramerius.service.*"  %>
<%@page import="com.google.inject.Injector"%>
<%@page import="cz.incad.Kramerius.views.help.HelpViewObject"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
	HelpViewObject helpViewObject = new HelpViewObject();
	Injector hinj = (Injector)application.getAttribute(Injector.class.getName());
	((Injector)application.getAttribute(Injector.class.getName())).injectMembers(helpViewObject);
	pageContext.setAttribute("helpViewObject", helpViewObject);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<title>Kramerius - Help</title>
	<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=UTF-8">
	<META HTTP-EQUIV="Content-Language" CONTENT="cs">
	<META NAME="description" CONTENT="Help for Kramerius ">
	<META NAME="keywords" CONTENT="periodicals, library, book, publication, kramerius">

	<META NAME="Copyright" content="">
	<LINK REL="StyleSheet" HREF="../css/styles.css" type="text/css">
	<SCRIPT LANGUAGE="JavaScript" TYPE="text/javascript" src="add.js"></SCRIPT>
</head>
<body marginwidth="0" marginheight="0" leftmargin="0" topmargin="0">

<table id="help" class="header ui-corner-top-8" >
<tr><td> <img src="../img/logo_knav.png" border="0" /></td>
<td width="500px"> </td> <td>revision:${helpViewObject.revision}</td></tr>
</table>
    
<table cellpadding="0" cellspacing="0" border="0" height="100%">
  <tr>
    <td width="13" background="img/main_bg_grey.gif" height="100%"><img src="img/spacer.gif" width="13" height="1" alt="" border="0"></td>
    <td valign="top" width="100%">

    <table cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr>
        <td height="20" width="100%" class="mainHeadGrey"><div align="right"><a href="javascript:print();"   class="mainServ">PRINT</a>&nbsp; - &nbsp;<a href="javascript:openhelp=window.close();" class="mainServ">CLOSE WINDOW</a><a name="top">&nbsp;</a>&nbsp;</div></td>
      </tr>
      <tr>
        <td width="100%" colspan="3"><img src="img/spacer.gif" width="1" height="2" alt="" border="0"></td>
      </tr>

      <tr>
        <td width="100%" class="mainHeadGrey"><img src="img/spacer.gif" width="1" height="1" alt="" border="0"></td>
      </tr>
      <tr>
        <td valign="top" align="left" width="100%">
        <table cellpadding="0" cellspacing="0" border="0" width="100%">
          <tr>
            <td><img src="img/spacer.gif" width="15" height="1" alt="" border="0"></td>
            <td>


<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
  <td class="mainHeadGrey" height="1"><img src="img/spacer.gif" width="1" height="1" alt="" border="0"></td>
</tr>
</table>
<br><div align="right"><a href="#top" name="a" class="mainServ">TOP</a></div>
<strong>What do I find in the Digital library</strong><br><br>

    Digital library contains rare documents digitized in the national programs 
    Memoriae Mundi Series Bohemica (mostly manuscripts, old prints and other documents) 
    and Kramerius (old newspapers and journals and other rare documents printed on acid paper). 
    Access to the digital library is restricted with access rights. 
    Metadata, ie. bibliographical and other descriptions of the documents are available to anyone, as well as are the graphical data 
    of the documents not protected by the copyright law. Grahical data protected by the copyright law are available only to users 
    accessing the application from teh computers at the premises of the library.
<br><br>
<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
  <td class="mainHeadGrey" height="1"><img src="img/spacer.gif" width="1" height="1" alt="" border="0"></td>
</tr>
</table>
<br><div align="right"><a href="#top" name="b" class="mainServ">TOP</a></div>
<strong>What do I need to use the Digital library?</strong><br><br>
Any modern web browser, preferably Firefox 3, Safari or Google Chrome. 
You need to install additional plugin module to display DjVu and PDF files.
The recommended display resolution is 1024x768 or more pixels.
<p>
<br><div align="right"><a href="#top" name="a" class="mainServ">TOP</a></div>
<strong>Main screen</strong> <br><br>
The main screen is used for searching the documents in the library. <p>
The large input field in the middle of the page header can be used to perform full text search in the library documents:
type in the desired word a click on the magnifying glass symbol. 
The wildcard character (*) representing any part of the word can be also used.<p>
The Advanced search link opens the form for searching in the documents metadata (MODS).
You can search by ISBN/ISSN, author, title, date of publishing and  MDC/DDC classifications. The search is started by clicking the OK button.<p>
In the right corner of the page header are the links to this help text, to switch the user interface language and to login to the administration services.<p>
The Browse box can be used for quick search of the document by it's title or author.
The popup list of found documents is continuously updated as you type in additional letters.<p>
By clicking on any item in the Document type box you can search the documents of the corresponding type. 
The numbers in the parentheses next to each item show the count of the corresponding repository documents.<p>
The Time line box show the histogram of the document counts published in each  year.
You can limit the search time interval by typing the years into the fields at the top of the diagram or by dragging the vertical handles.<p>
The tab Latest contains the icons of title pages of the documents recently added to the repository.
Click at the corresponding icon to display the required document.
Likewise, the tab Most desirable contains icons of the most frequently used documents.<p>
<br><br>
<br><div align="right"><a href="#top" name="a" class="mainServ">TOP</a></div>
<strong>Search results</strong> <br><br>
This page is diplayed after spawning the search from the main screen.
The central box contains the list of found documents grouped by the top-level titles (i.e. monographs or periodicals.)
Items inside the groups can be displayed by clicking at the arrows next to the title. 
The expanded list can be paged through using the links Next and Prev. 
The titles can be sorted by relevance or alphabetically by clicking at the corresponding link at the top of the box.<p>
The boxes at the left contain items for selection of the subcategories within the found documents. Their function is similar to the Document type box on the main screen.<p>
The right part of the screen is filled with the same time line box as the main screen.<br><br>

<br><div align="right"><a href="#top" name="a" class="mainServ">TOP</a></div>
<strong>Title display</strong><br><br>
The third level screen is used to display the document's metadata and to browse it's structure.<p>
At the top there is a bar with icons of all pages in the document or the selected part.
Icons can be scrolled by the arrows at the edges or by teh scrollbar bellow. The icon bar is not diplayed for the documents with the single PDF file.<p>
The center of the screen is filled with the preview of the selected page.<br>
If the access to the documet contents is restricted by the copyright law, the preview is replaced by the informational message.<p>
The box to the right of the preview is used for browsing of the document structure.
Each tab corresponds to a certain level in the tree of the document structure. 
If one level contains more documents (e.g. volumes or pages etc.), it's tab has an arrow to display popup menu of those documents.
The subset of the metadata of teh current document is displayed bellow the tab.
The arrow at the far right edge of the box opens teh contextual popup menu with operations that can be applied to the document selected in the corresponding tab.<p>
The following operations are available:<br>   
View metadata: Opens the window with complete MODS metadata of the selected document.<br>
Persistent URL: Dsiplays URL of the document, that can be used as the bookmark for direct access to the document.<br>
Generate PDF: Allows to generate and download the PDF document with the page range starting with the selected page.<br>
METS record: Opens the window with XML in the format Fedora METS for the selected document.<p>
The full size image of the page is opened by clicking on the large preview of the page. 
You have to install corresponding plugin module to view the docuemnts in the DjVu or PDF formats. <p>
<br><br>

 
<br><br>

            </td>
            <td><img src="img/spacer.gif" width="15" height="1" alt="" border="0"></td>
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
