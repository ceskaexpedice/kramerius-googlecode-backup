<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page isELIgnored="false"%>
<%@page import="com.google.inject.Injector"%>
<%@page import="javax.servlet.jsp.jstl.fmt.LocalizationContext"%>


<%
	Injector inj = (Injector)application.getAttribute(Injector.class.getName());
	pageContext.setAttribute("lrProcessManager",inj.getInstance(LRProcessManager.class));
	pageContext.setAttribute("dfManager",inj.getInstance(DefinitionManager.class));
	
	LocalizationContext lctx= inj.getProvider(LocalizationContext.class).get();
	pageContext.setAttribute("lctx", lctx);

	String pidPath = request.getParameter("pid_path");
	StringTokenizer tokenizer = new StringTokenizer(pidPath,"/");
	if (tokenizer.hasMoreTokens()) {
		inj.getInstance(MostDesirable.class).saveAccess(tokenizer.nextToken(), new Date());	
	}

%>

<%@ include file="inc/initVars.jsp" %>
<c:set var="pageType" value="search" />
<jsp:useBean id="pageType" type="java.lang.String" />
<fmt:setBundle basename="labels" />
<fmt:setBundle basename="labels" var="bundleVar" />
<c:url var="url" value="${kconfig.solrHost}/select/select" >
    <c:param name="q" value="PID:\"${param.pid}\"" />
</c:url>

<c:catch var="exceptions"> 
    <c:import url="${url}" var="xml" charEncoding="UTF-8" />
</c:catch>
<c:if test="${exceptions != null}" >
    <c:import url="empty.xml" var="xml" />
</c:if>

<x:parse var="doc" xml="${xml}"  />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">

<%@page import="java.io.InputStream"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="cz.incad.kramerius.utils.RESTHelper"%>
<%@page import="cz.incad.kramerius.utils.IOUtils"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="com.google.inject.Injector"%>
<%@page import="cz.incad.kramerius.processes.LRProcessManager"%>
<%@page import="cz.incad.kramerius.processes.DefinitionManager"%>
<%@page import="cz.incad.kramerius.MostDesirable"%>
<%@page import="cz.incad.kramerius.utils.pid.PIDParser"%><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="cs" lang="cs">
    <%@ include file="inc/html_header.jsp" %>
    <body >
		<!--  procesy - dialogy -->
	    <%@ include file="dialogs/_processes_dialogs.jsp" %>
        <table style="width:100%" id="mainItemTable"><tr><td align="center">
        <c:if test="${param.debug}" >
        <c:out value="${url}" />
        <br/>
        <c:out value="${exceptions}" />
        </c:if>
        <%@ include file="inc/searchForm.jsp" %>
        <table>
            <tr valign='top'>
                <td><%//@ include file="usedFilters.jsp" %></td>
            </tr>
        </table>
        <table class="main">
            <tr valign='top'>
                <td colspan="2" valign="middle" align="center">
                    <table style="width: 100%"><tr>
                        <td width="20px" align="center"><a class="prevArrow"  href="javascript:selectPrevious();"><img src="img/la.png" border="0" /></a></td>
                        <td align="center"><%--<%@ include file="gwtView.jsp" %>--%><%@ include file="thumbsViewer.jsp" %></td>
                        <td width="20px" align="center"><a class="nextArrow"  href="javascript:selectNext();"><img src="img/ra.png" border="0" /></a></td>
                    </tr></table>
                    </td>
            </tr>
            <tr valign='top'>
                <td>
                    
                    <%
                    ArrayList<String> pids2 =  new ArrayList<String>(Arrays.asList((String [])request.getParameter("pid_path").split("/")));
                    ArrayList<String> models2 =  new ArrayList<String>(Arrays.asList((String [])request.getParameter("path").split("/")));
                    FedoraUtils.fillFirstPagePid(pids2, models2);
                    imagePid = pids2.get(pids2.size()-1);
                    %>
                    <table cellpadding="0" cellspacing="0" width="100%"><tr>
                        <td valign="top" align="center" width="20px"><a class="prevArrow"  href="javascript:selectPrevious();"><img src="img/la.png" border="0" /></a></td>
                        <td valign="top" align="center" id="mainContent"><a href="javascript:showFullImage('<%=imagePid%>')" class="lighbox">
                                <img border="0" width="544px" onerror="showError();" src="djvu?uuid=<%=imagePid%>&amp;scaledWidth=540" id="imgBig">
                            </a></td>
                        <td valign="top" align="center" width="20px"><a class="nextArrow"  href="javascript:selectNext();"><img src="img/ra.png" border="0" /></a></td>
                    </tr></table>
                </td>
                <td class="itemMenu">
                    <div id="itemTree">
                        <script>
                            var firstCalled = false;
                        </script>
                    <%@ include file="inc/details/itemMenu.jsp" %>
                    
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="3" align="right" id="donatorContainer"></td>
            </tr>
        </table>
        <table>
            <tr valign='top'>
                <td><%@ include file="templates/footer.jsp" %></td>
            </tr>
        </table>
        </td></tr></table>

<!-- dialogs -->
<div id="pdf_options" style="display:none;">
        <span>rozsah stran:&nbsp;(max.&nbsp;<%=kconfig.getProperty("generatePdfMaxRange")%>)</span><br>&nbsp;&nbsp;                           
        <input type="text" id="genPdfStart" value="1" name="genPdfStart" size="3" > -
        <input type="text" id="genPdfEnd" value="1" name="genPdfEnd" size="3">
</div>


<div id="fullImageContainer" style="display:none;">
    <div id="djvuContainer" style="display:none;">
        <object width="100%" border="0" height="100%" style="border: 0px none ;" codebase="http://www.lizardtech.com/download/files/win/djvuplugin/en_US/DjVuControl_en_US.cab" classid="clsid:0e8d0700-75df-11d3-8b4a-0008c7450c4a" id="docframe" name="docframe">
            <param name="src" value="" />
            <embed width="100%" height="100%" src="" type="image/vnd.djvu" id="docframe2" name="docframe2"/>
            If you don't see picture, your browser has no plugin to view DjVu picture files. You can install plugin from <a target="_blank" href="http://www.celartem.com/en/download/djvu.asp"><b>LizardTech</b></a>.<br/>
            <a href="http://www.celartem.com/en/download/djvu.asp">File download</a><br/> <br/> <br/> 
        </object>
    </div>
    <div id="imgContainer" style="display:none;" align="center">
        <img id="imgFullImage" src="img/empty.gif" />
    </div>
    <div id="divFullImageZoom" style="display:none;">
        <span class="ui-dialog-titlebar-zoom"><fmt:message bundle="${lctx}">velikost</fmt:message>: <select onchange="changeFullImageZoom()" id="fullImageZoom">
            <option value="width"><fmt:message bundle="${lctx}">šířka okna</fmt:message></option>
            <option value="height"><fmt:message bundle="${lctx}">výška okna</fmt:message></option>
            <option value="0.1">10%</option>
    <option value="0.2" >20%</option>
    <option value="0.3" >30%</option>
    <option value="0.4" >40%</option>
    <option value="0.5" >50%</option>
    <option value="0.6" >60%</option>
    <option value="0.7" >70%</option>
    <option value="0.8" >80%</option>
    <option value="0.9" >90%</option>
    <option value="1" selected="selected" >100%</option></select></span>
    </div>
</div>
</body></html>