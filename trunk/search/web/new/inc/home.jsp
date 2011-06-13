<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>
<%@page import="com.google.inject.Injector"%>
<%@page import="java.util.Locale"%>
<%@page import="com.google.inject.Provider"%>
<%@page import="cz.incad.Kramerius.backend.guice.LocalesProvider"%>
<%@page import="java.io.*, cz.incad.kramerius.service.*"  %>
<%@page import="cz.incad.kramerius.utils.conf.KConfiguration"%>
<%@page import="javax.servlet.jsp.jstl.fmt.LocalizationContext"%>
<%@page import="cz.incad.kramerius.FedoraAccess"%>

<div style="float:left;width:750px;">
    
<div id="dt_home">
    <%--<c:set var="facetname" value="document_type" />
<%@ include file="facet.jsp" %>--%>
    <c:url var="dtxslurl" value="inc/home/dt.xsl" />
    <c:import url="${dtxslurl}" var="facetxsl" charEncoding="UTF-8"  />
    <x:transform doc="${xml}"  xslt="${facetxsl}">
        <x:param name="bundle_url" value="${i18nServlet}"/>
    </x:transform>
</div>

<%@ include file="home/tabs.jsp" %>
</div>
<div id="homedabox" style="float:left;width:230px;margin-top:7px;margin-left:4px;">
    <ul><li><a href="#dadiv"><fmt:message bundle="${lctx}" key="Časová osa" /></a></li></ul>
<div id="dadiv" style="padding:3px;"><%@ include file="dateAxisV.jsp" %></div>
</div>