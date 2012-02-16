<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false"%>
<%@ page trimDirectiveWhitespaces="true"%>

<%
        String fromField = "f1";
        String toField = "f2";
        String fromValue = "";
        String toValue = "";
%>

<%@page import="com.google.inject.Injector"%>
<%@page import="cz.incad.kramerius.service.ResourceBundleService"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.ResourceBundle"%>
<%@page import="java.util.Enumeration"%><head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="Pragma" content="no-cache" />
    <meta http-equiv="Cache-Control" content="no-cache" />
    <meta name="description" content="National Library of Czech Republic digitized documents (periodical, monographs) access aplication." />
    <meta name="keywords" content="periodical, monograph, library, National Library of Czech Republic, book, publication, kramerius" />
    <meta name="AUTHOR" content="INCAD, www.incad.cz" />
    
    <link rel="icon" href="img/favicon.ico"/>
    <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />
    
    <link rel="stylesheet" href="css/gwt.css" type="text/css"/>
    
    <link type="text/css" href="css/themes/base/ui.base.css" rel="stylesheet" />
    <link type="text/css" href="css/themes/base/ui.theme.css" rel="stylesheet" />
    <link type="text/css" href="css/themes/base/ui.dialog.css" rel="stylesheet" />
    <link type="text/css" href="css/themes/base/ui.slider.css" rel="stylesheet" />
    <!--
    <link type="text/css" href="js/jquery.lightbox-0.5/css/jquery.lightbox-0.5.css" rel="stylesheet" />
    -->
    <link rel="stylesheet" href="css/dateAxisV.css" type="text/css"/>
    <link rel="stylesheet" href="css/dtree.css" type="text/css" />
    <link rel="StyleSheet" href="css/styles.css" type="text/css" />
    <link rel="StyleSheet" href="css/details.css" type="text/css" />

        
    <script src="js/jquery-1.3.2.min.js" type="text/javascript" ></script>
    <script src="js/jquery-ui-1.7.2.custom.min.js" language="javascript" type="text/javascript"></script>
    <script src="js/jquery.cookie.js" type="text/javascript"></script>
    <script src="js/jquery.history.js" type="text/javascript"></script>

    <script src="js/jquery.backgroundPosition.js" type="text/javascript"></script>
	
    <!--
    <script  src="js/jquery.lightbox-0.5/js/jquery.lightbox-0.5.js" type="text/javascript"></script>
    <script  src="js/jquery.scrollTo.js" type="text/javascript"></script>
    -->
    
    <script src="js/pageQuery.js" language="JavaScript" type="text/javascript"></script>
    <script src="js/item.js" language="JavaScript" type="text/javascript"></script>
    <script src="js/incad.js" language="JavaScript" type="text/javascript"></script>
    <script src="js/dateAxis_formatV.js" language="javascript" type="text/javascript"></script>
    <script src="js/dateAxisV.js" language="javascript" type="text/javascript"></script>
    <script src="js/dtree.js" language="javascript" type="text/javascript"></script>
    
    <script  src="js/autocomplete.js" language="javascript" type="text/javascript"></script>


	<%  if(request.getRemoteUser()!=null) {  %>
	    <script  src="js/admin.js" language="javascript" type="text/javascript"></script>
	<% } %>

    <title>Kramerius 4</title>
    <script language="JavaScript" type="text/javascript">
        var pagesTitle = "<fmt:message bundle="${lctx}">Stránka</fmt:message>";
        var unitsTitle = "<fmt:message bundle="${lctx}">Unit</fmt:message>";
        var volumesTitle = "<fmt:message bundle="${lctx}">Volume</fmt:message>";
        var issuesTitle = "<fmt:message bundle="${lctx}">Issue</fmt:message>";
        var internalPartTitle = "<fmt:message bundle="${lctx}">InternalPart</fmt:message>";
        var readingPages = "<fmt:message bundle="${lctx}">Načítám stránky</fmt:message>";
        var readingUnits = "<fmt:message bundle="${lctx}">Načítám části</fmt:message>";
        var readingVolumes = "<fmt:message bundle="${lctx}">Načítám ročníky</fmt:message>";
        var readingIssues = "<fmt:message bundle="${lctx}">Načítám ročníky</fmt:message>";
        var readingIntarnalParts = "<fmt:message bundle="${lctx}">Načítám kapitoly</fmt:message>";
        var language = "<c:out value="${param.language}" />";
        var searchPage = "./";
        
        var searchInTreePage = "inc/searchWithoutFacets.jsp";
        var fedoraImg = "<c:out value="${kconfig.fedoraHost}" />/get/uuid:";
        var fromField = "<%=fromField%>";
        var toField = "<%=toField%>";
        var dateAxisAdditionalParams = "";
        var fromStr = "od";
        var toStr = "do";
        var selectStart = "";
        var selectEnd = "";
        var initParent = "";
        var initPage = "";
        //var generatePdfTitle = "<fmt:message bundle="${lctx}">generatePdfTitle</fmt:message>";
        var generatePdfErrorText = "<fmt:message bundle="${lctx}">generatePdfErrorText</fmt:message>";
        var generatePdfMaxRange = <%=kconfig.getProperty("generatePdfMaxRange")%>;


var dictionary = {
<%
			Injector resInj  = (Injector)application.getAttribute(Injector.class.getName());
			ResourceBundleService resService = resInj.getInstance(ResourceBundleService.class);			
			Locale locale = resInj.getInstance(Locale.class);
			ResourceBundle res = resService.getResourceBundle("labels", locale);
            for (Enumeration e = res.getKeys(); e.hasMoreElements();) {
                String key = (String)e.nextElement();
                out.print("'" + key + "':'" + res.getString(key).replace("'", "\\'") + "',");
			}
%>jedennavic:''};
    </script>
    
    
</head>