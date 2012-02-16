<%@page import="cz.incad.Kramerius.views.HeaderViewObject"%>
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
    <meta name="description" content="Digitized documents access aplication." />
    <meta name="keywords" content="periodical, monograph, library,  book, publication, kramerius, fedora" />
    <meta name="author" content="INCAD, www.incad.cz" />

    <link rel="icon" href="img/favicon.ico"/>
    <link rel="shortcut icon" href="img/favicon.ico" type="image/x-icon" />

    <link type="text/css" href="css/themes/base/ui.base.css" rel="stylesheet" />
    <link type="text/css" href="css/themes/base/ui.theme.css" rel="stylesheet" />
    <link type="text/css" href="css/themes/base/ui.dialog.css" rel="stylesheet" />
    <link type="text/css" href="css/themes/base/ui.slider.css" rel="stylesheet" />

    <link rel="stylesheet" href="css/dateAxisV.css" type="text/css"/>
    <link rel="stylesheet" href="css/dtree.css" type="text/css" />
    <link rel="StyleSheet" href="css/styles.css" type="text/css" />
    
    <!--[if IE ]>
    <link rel="StyleSheet" href="css/ie.css" type="text/css" />
    <![endif]-->
    <!--
    <script src="js/jquery-1.3.2.min.js" type="text/javascript" ></script>

    <script src="js/jquery-ui-1.7.2.custom.min.js" language="javascript" type="text/javascript"></script>
    -->
    <script src="js/jquery-1.5.1.min.js" type="text/javascript" ></script>
    <script src="js/jquery-ui-1.8.11.custom.min.js" language="javascript" type="text/javascript"></script>
    <script src="js/jquery.mousewheel.js" type="text/javascript" ></script>
    <script src="js/jquery.cookie.js" type="text/javascript"></script>

    <script src="js/pageQuery.js" language="JavaScript" type="text/javascript"></script>
    <script src="js/docMenu.js" language="JavaScript" type="text/javascript"></script>
    <script src="js/item.js" language="JavaScript" type="text/javascript"></script>
    <script src="js/incad.js" language="JavaScript" type="text/javascript"></script>
    <script src="js/dateAxis_formatV.js" language="javascript" type="text/javascript"></script>
    <script src="js/dateAxisV.js" language="javascript" type="text/javascript"></script>
    <script  src="js/autocomplete.js" language="javascript" type="text/javascript"></script>

    <script type="text/javascript"  src="js/seadragon-min.js"></script>
    <script  src="js/pdf/pdf.js" language="javascript" type="text/javascript"></script>
    <script  src="js/cmn.js" language="javascript" type="text/javascript"></script>
    <!-- condition ?? -->
    
    <%
		Injector headerInjector = (Injector) application.getAttribute(Injector.class.getName());
		HeaderViewObject headerViewObject = new HeaderViewObject();
		headerInjector.injectMembers(headerViewObject);
		pageContext.setAttribute("headerViewObject", headerViewObject);
    %>
    
        
    ${headerViewObject.injectedAdminScripts}
    
    <title><fmt:message bundle="${lctx}">application.title</fmt:message></title>
    <script language="JavaScript" type="text/javascript">
        var searchPage = "./";
        // time axis 
        var fromField = "<%=fromField%>";
        var toField = "<%=toField%>";
        var dateAxisAdditionalParams = "";
        var fromStr = "od";
        var toStr = "do";
        var selectStart = "";
        var selectEnd = "";

        // localization
        ${headerViewObject.dictionary}
            // selekce
        ${headerViewObject.levelsModelSelectionArray}

        ${headerViewObject.config}
        
            // upravuje polozky menu tak aby byly resp. nebyly videt
            // presunout jinam, ale kam?
            function postProcessContextMenu() {
		    
                // polozky, ktere jsou viditelne (neviditelne) jenom kvuli roli
                $(".adminMenuItems").each(function(menuindex, menuelm) {
                    $(menuelm).children("div").each(function(itemidex,itemelm){
				    
                        var roleDiv = $(itemelm).children("div._data_x_role");
                        var uuidDiv = $(itemelm).children("div._data_x_uuid")
                        var levelDiv = $(itemelm).children("div._data_x_level")

                        // role element
                        if ((roleDiv.length == 1) && (roleDiv.text() != '')) {
                            var actionToPerform = roleDiv.text();
                            //var uuid = uuidDiv.text();
                            var level = levelDiv.text();
                            var uuid = $("#tabs_"+level).attr('pid');
					    
                            if (viewerOptions.rights[actionToPerform]) {
                                if (viewerOptions.rights[actionToPerform][uuid]) {
                                    $(itemelm).show();
                                } else if (viewerOptions.rights[actionToPerform]["1"]){
                                    $(itemelm).show();
                                } else {
                                    $(itemelm).hide();
                                }
                            } else {
                                $(itemelm).hide();
                            }
                        }
                    });
                });
            }
    </script>
</head>