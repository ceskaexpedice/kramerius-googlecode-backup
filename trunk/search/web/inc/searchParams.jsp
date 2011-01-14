<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>
<%@ page import="java.util.*, cz.incad.Kramerius.*, cz.incad.Solr.*" %>



<%@ include file="initVars.jsp" %>
<c:set var="pageType" value="search" />
<jsp:useBean id="pageType" type="java.lang.String" />
    <c:set var="rowsdefault" value="20" scope="request" />
    <c:set var="rows" value="0" scope="request" />
<c:url var="url" value="${kconfig.solrHost}/select/select" >
    <c:choose>
        <c:when test="${empty param.q}" >
            <c:param name="q" value="*:*" />
        </c:when>
        <c:when test="${param.q != null}" >
            <c:if test="${fn:containsIgnoreCase(param.q, '*')}" >
                <c:param name="qt" value="czparser" />
            </c:if>
            <c:param name="q" value="${param.q}" />
            <c:set var="rows" value="${rowsdefault}" scope="request" />
        </c:when>
        
    </c:choose>
    
    <c:if test="${param.facet != 'false'}">
        <c:param name="facet.field" value="document_type" />
        <c:param name="facet.field" value="path" />
        <c:param name="f.path.facet.sort" value="false" />
        <c:param name="facet.field" value="language" />
        <c:param name="facet.field" value="rok" />
        <c:param name="f.rok.facet.limit" value="-1" />
        <c:param name="f.rok.facet.sort" value="false" />
        <c:param name="facet" value="true" />
        <c:param name="facet.mincount" value="1" />
    </c:if>
    
    <c:param name="fl" value="PID,score,root_title,pages_count,path,pid_path,root_pid,dc.title,details,fedora.model,path,dc.creator,datum,page_format,text" />
    <c:forEach var="fqs" items="${paramValues.fq}">
        <c:param name="fq" value="${fqs}" />
            <c:set var="rows" value="${rowsdefault}" scope="request" />
    </c:forEach>
    <c:if test="${param.f1 != null}">
        <c:param name="fq" value="rok:[${param.f1} TO ${param.f2}] OR (datum_begin:[1 TO ${param.f1}] AND datum_end:[${param.f2} TO 3000])" />
            <c:set var="rows" value="${rowsdefault}" scope="request" />
    </c:if>
    <c:param name="start" value="${param.offset}" />
    
    
    <c:if test="${param.collapsed != 'false'}">
        <c:param name="collapse.field" value="root_pid" />
        <c:param name="collapse.type" value="normal" />
        <c:param name="collapse.threshold" value="1" />
        <c:param name="collapse.facet" value="before" />
    </c:if>
    
    <c:set var="fieldedSearch" value="false" scope="request" />
    <%-- advanced params --%>
    <c:if test="${!empty param.issn}">
        <c:param name="fq" value="issn:${param.issn}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.title}">
        <c:param name="fq" value="dc.title:${param.title}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.author}">
        <c:param name="fq" value="dc.creator:${param.author}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.rok}">
        <c:param name="fq" value="rok:${param.rok}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.udc}">
        <c:param name="fq" value="udc:${param.udc}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.ddc}">
        <c:param name="fq" value="ddc:${param.ddc}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.keywords}">
        <c:param name="fq" value="keywords:${param.keywords}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
    <c:if test="${!empty param.onlyPublic}">
        <c:param name="fq" value="dostupnost:${param.onlyPublic}" />
        <c:set var="rows" value="${rowsdefault}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>

    <c:if test="${param.rows != null}" >
        <c:set var="rows" value="${param.rows}" scope="request" />
        <c:set var="fieldedSearch" value="true" scope="request" />
    </c:if>
        
    <c:param name="rows" value="${rows}" />
    <c:if test="${rows!='0'}">
        <c:param name="facet.field" value="facet_autor" />
        <c:param name="facet.field" value="dostupnost" />
        <c:param name="f.facet_autor.facet.sort" value="false" />
    </c:if>
    
    <%-- Hit highlight --%>
    <c:param name="hl" value="true" />
    <c:param name="hl.fl" value="text" />
    <c:param name="hl.simple.pre" value="<span>" />
    <c:param name="hl.simple.post" value="</span>"  />
    <c:param name="hl.mergeContiguous" value="true" />
    <c:param name="hl.snippets" value="2" />
    
    <%-- sort param --%>    
    <c:choose>
        <c:when test="${param.sort != null}" >
            <c:param name="sort" value="${param.sort}" />
        </c:when>
        <c:when test="${sort != null}" >
            <c:param name="sort" value="${sort}" />
        </c:when>
        <c:when test="${fieldedSearch}">
            <c:param name="sort" value="level asc, title asc, score desc" />
        </c:when>
        <c:when test="${empty param.q}" >
            <c:param name="sort" value="level asc, title asc, score desc" />
        </c:when>
        <c:otherwise>
            <c:param name="sort" value="level asc, score desc" />
        </c:otherwise>
    </c:choose>
</c:url>

<c:catch var="exceptions"> 
    <c:import url="${url}" var="xml" charEncoding="UTF-8" />
</c:catch>
<c:if test="${exceptions != null}" >
    
    <c:import url="empty.xml" var="xml" />
</c:if>
<x:parse var="doc" xml="${xml}"  />
<c:set var="numDocs" scope="request" >
    <x:out select="$doc/response/result/@numFound" />
</c:set>
<c:set var="numCollapsed" scope="request" value="0" />
<x:forEach select="//response/lst[@name='collapse_counts']/lst[@name='results']/lst">
    <c:set var="collapseCount" >
        <a href="javascript:uncollapse('<c:out value="${root_pid}" />', 'uncollapsed_<c:out value="${uuid}"/>', 0)"><img src="img/collapsed.png" 
           alt="<x:out select="./int[@name='collapseCount']/text()"/> <fmt:message bundle="${lctx}">collapsed</fmt:message>"
           title="<x:out select="./int[@name='collapseCount']/text()"/> <fmt:message bundle="${lctx}">collapsed</fmt:message>" border="0" /></a>
    </c:set>
</x:forEach>

<c:set var="numDocsStr" scope="request" >
    <c:choose>
        <c:when test="${numDocs==1}"><fmt:message bundle="${lctx}">common.documents.singular</fmt:message></c:when>
        <c:when test="${numDocs>1 && numDocs<5}"><fmt:message bundle="${lctx}">common.documents.plural_1</fmt:message></c:when>
        <c:when test="${numDocs>4}"><fmt:message bundle="${lctx}">common.documents.plural_2</fmt:message></c:when>
    </c:choose>
    
</c:set>