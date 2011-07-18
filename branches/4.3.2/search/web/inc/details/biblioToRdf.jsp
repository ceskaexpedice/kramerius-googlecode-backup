<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>
<%@page import="cz.incad.Kramerius.I18NServlet"%>
<%@page import="com.google.inject.Injector"%>
<%@page import="javax.servlet.jsp.jstl.fmt.LocalizationContext"%>

<%
            Injector ctxInj = (Injector) application.getAttribute(Injector.class.getName());
            LocalizationContext lctx = ctxInj.getProvider(LocalizationContext.class).get();
            pageContext.setAttribute("lctx", lctx);
            String i18nServlet = I18NServlet.i18nServlet(request) + "?action=bundle&lang="+lctx.getLocale().getLanguage()+"&country="+lctx.getLocale().getCountry()+"&name=labels";
            pageContext.setAttribute("i18nServlet", i18nServlet);

%>
<c:choose>
    <c:when test="${param.language != null}" >
        <fmt:setLocale value="${param.language}" />
    </c:when>
</c:choose>
<fmt:setBundle basename="labels" />
<fmt:setBundle basename="labels" var="bundleVar" />

<c:choose>
    <c:when test="${empty uuid || uuid==null || uuid==''}">
        <c:set var="pid" ><c:out value="${param.pid}" /></c:set>
    </c:when>
    <c:otherwise>
        <c:set var="pid" ><c:out value="${uuid}" /></c:set>
    </c:otherwise>
</c:choose>
<c:set var="urlPageStr" >
    <c:out value="${kconfig.fedoraHost}" />/get/<c:out value="${pid}" />/BIBLIO_MODS
</c:set>
<c:url var="urlPage" value="${urlPageStr}" />

<c:choose>
    <c:when test="${xsl==null || xsl==''}">
        <c:set var="xsl" value="xsl/${param.xsl}" scope="request" />
    </c:when>
    <c:otherwise>
        <c:set var="xsl" value="${xsl}" scope="request" />
    </c:otherwise>
</c:choose>
<c:set var="xsl" value="xsl/mods.xsl" scope="request" />
<c:url var="xslPage" value="${xsl}" >
</c:url>
<c:catch var="exceptions"> 
    <c:import url="${urlPage}" var="xmlPage" charEncoding="UTF-8"  />
    <c:import url="${xslPage}" var="xsltPage" charEncoding="UTF-8"  />
</c:catch>
<c:choose>
    <c:when test="${exceptions != null}" >
        <jsp:useBean id="exceptions" type="java.lang.Exception" />
        <% System.out.println(exceptions); %>
    </c:when>
    <c:otherwise>
        <c:catch var="exceptions2"> 
            <% out.clear(); %>
            <x:transform doc="${xmlPage}"  xslt="${xsltPage}"  >
                <x:param name="pid" value="${pid}"/>
                <x:param name="bundle_url" value="${i18nServlet}"/>
                <x:param name="model" value="${param.model}"/>
            </x:transform>
        </c:catch>
        <c:choose>
            <c:when test="${exceptions2 != null}" >
                <%--
                <c:out value="${exceptions}" /><br/>
                xsl --- <c:out value="${xsl}" /><br/>
                xslPage --- <c:out value="${xslPage}" /><br/>
                xsltPage --- <c:out value="${xsltPage}" />
                --%>
                <jsp:useBean id="exceptions2" type="java.lang.Exception" />
                <% System.out.println(exceptions2); %>
            </c:when>
            <c:otherwise></c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>