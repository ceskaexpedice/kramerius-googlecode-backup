<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  version="1.0" 
                 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                 xmlns:mods="http://www.loc.gov/mods/v3" 
                 xmlns:fmt="http://java.sun.com/jsp/jstl/fmt" >
    <xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
    <%@ page pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
	<%@page import="com.google.inject.Injector"%>
	<%@page import="javax.servlet.jsp.jstl.fmt.LocalizationContext"%>
    <%@ page isELIgnored="false"%>
    <c:choose>
        <c:when test="${param.language != null}" >
            <fmt:setLocale value="${param.language}" />
        </c:when>
    </c:choose>
    <%
	Injector ctxInj = (Injector)application.getAttribute(Injector.class.getName());
	LocalizationContext lctx= ctxInj.getProvider(LocalizationContext.class).get();
	pageContext.setAttribute("lctx", lctx);
%>
    
    <fmt:setBundle basename="labels" />
    <fmt:setBundle basename="labels" var="bundleVar" />
    
    <!-- TODO customize transformation rules 
    syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="/">
        <xsl:apply-templates mode="info"/>
    </xsl:template>
    <xsl:template match="/mods:modsCollection/mods:mods" mode="info">
        <xsl:variable name="unitPid">uuid:<xsl:value-of select="./mods:identifier[@type='urn']"/></xsl:variable>
        <xsl:variable name="pageType"><xsl:value-of select="mods:part/@type" /></xsl:variable>
                <xsl:choose>
                    <xsl:when test="$pageType='Chapter'">
                        (<fmt:message bundle="${lctx}">Chapter</fmt:message>)</xsl:when>
                    <xsl:when test="$pageType='Table'">
                        (<fmt:message bundle="${lctx}">Table</fmt:message>)</xsl:when>
                    <xsl:when test="$pageType='Introduction'">
                        (<fmt:message bundle="${lctx}">Introduction</fmt:message>)</xsl:when>
                </xsl:choose> - <xsl:value-of select="mods:titleInfo/mods:title" /> - 
                <xsl:value-of select="mods:part/mods:extent/mods:list" />
        
    </xsl:template>
</xsl:stylesheet>
