<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>

<%@page import="cz.incad.kramerius.utils.FedoraUtils"%>
<c:url var="xslPage" value="inc/results/xsl/results_main.xsl" />
<c:catch var="exceptions">
    <c:import url="${xslPage}" var="xsltPage" charEncoding="UTF-8"  />
</c:catch>
<c:choose>
    <c:when test="${exceptions != null}">
        <c:out value="${exceptions}" />
        <c:out value="${url}" />
        <c:out value="${xml}" />
    </c:when>
    <c:otherwise>
        <c:if test="${param.debug =='true'}"><c:out value="${url}" /></c:if>
        <c:catch var="exceptions2">
            <x:transform doc="${xml}"  xslt="${xsltPage}">
                <x:param name="bundle_url" value="${i18nServlet}"/>
                <x:param name="q" value="${param.q}"/>
                <x:param name="fqs"><c:forEach var="fqs" items="${paramValues.fq}">&fq=<c:out value="${fqs}" escapeXml="false" /></c:forEach>
                </x:param>
            </x:transform>
        </c:catch>
        <c:if test="${exceptions2 != null}"><c:out value="${exceptions2}" />
        </c:if>
    </c:otherwise>
</c:choose>
<%--
<table id="results_main" cellspacing="0" cellpadding="0" border="0">
    <x:forEach varStatus="status" select="$doc/response/result/doc">
        <c:set var="uuid" >
            <x:out select="./str[@name='PID']"/>
        </c:set>
        <c:set var="solruuid" >
            <x:out select="./str[@name='PID']"/>
        </c:set>
        <c:if test="${fn:contains(uuid, '/@')}">
        <c:set var="uuid" >
            <c:out value="${fn:substringBefore(uuid, '/@')}"/>
        </c:set>
        </c:if>
        <c:set var="root_pid" >
            <x:out select="./str[@name='root_pid']"/>
        </c:set>
        <jsp:useBean id="uuid" type="java.lang.String" />
        <c:set var="fedora_model" >
            <x:out select="./str[@name='fedora.model']"/>
        </c:set>
        <jsp:useBean id="fedora_model" type="java.lang.String" />
        <c:set var="itemUrl" >
            ./item.jsp?pid=<c:out value="${uuid}"/>&pid_path=<x:out select="./str[@name='pid_path']"/>&path=<x:out select="./str[@name='path']"/>
        </c:set>
        
            <c:set var="itemUrl" ><c:out value="${itemUrl}"/>&format=<x:out select="./str[@name='page_format']"/>&q=<c:out value="${param.q}" />
                <c:forEach var="fqs" items="${paramValues.fq}">&fq=<c:out value="${fqs}" /></c:forEach></c:set>
        
        <x:set select="./str[@name='PID']" var="pid" />
    <tr id="res_<c:out value="${uuid}"/>" class="result r<c:out value="${status.count % 2}" />">
        <c:set var="collapseText" ></c:set>
        <c:set var="collapseCount" ></c:set>
        <x:forEach select="//response/lst[@name='collapse_counts']/lst[@name='results']/lst">
            <x:if select="./@name=$pid">
                <c:set var="collapseText" ><x:out select="./int[@name='collapseCount']/text()"/><c:out value=" "/><fmt:message bundle="${lctx}">collapsed</fmt:message></c:set>
                <c:set var="collapseCount" >
                    (<a href="javascript:toggleCollapsed('<c:out value="${root_pid}" />', 'uncollapsed_<c:out value="${root_pid}"/>', 0)"><c:out value="${collapseText}"/>
                    <img id="uimg_<c:out value="${root_pid}"/>" src="img/empty.gif" class="collapseIcon"
                       alt="${collapseText}"
                       border="0" />
                    </a>)
                </c:set>
            </x:if>
        </x:forEach>
        
    <td style="float:left;">
    <img src="img/empty.gif" 
    <c:if test="${status.count > 5}" >
    class="plus" onclick="$('#more_<c:out value="${uuid}"/>').toggle();$('#img_<c:out value="${uuid}"/>').toggle();$(this).toggleClass('minus')" 
    </c:if>
    />
    </td>
    <td class="resultThumb">
    <% 
    if(fedora_model.equals("page")){
        imagePid = "thumb?uuid=" + uuid.split("/@")[0];
    }else{
        //String pageuuid = FedoraUtils.findFirstPagePid("uuid:" + uuid);

        String pageuuid = fedoraAccess.findFirstViewablePid(uuid);
        if (pageuuid==null) pageuuid = uuid;
        imagePid = "thumb?uuid=" + pageuuid;
    }
    %>
    <a href="<c:out value="${itemUrl}" escapeXml="false" />"><img id="img_<c:out value="${uuid}"/>" 
    <c:if test="${status.count > 5}" >
        style="display:none;"
    </c:if>
    src="<%=imagePid%>&scaledHeight=64" 
    border="1"   /></a>
    </td>
    <td class="resultText">
    <a href="<c:out value="${itemUrl}" escapeXml="false" />" ><b><x:out select="./str[@name='root_title']"/></b></a>
    <span class="textpole">(<fmt:message bundle="${lctx}">fedora.model.<x:out select="./str[@name='fedora.model']"/></fmt:message>)</span>
    <x:if select="./int[@name='pages_count'] != '0'">
    <span class="count"><x:out select="./int[@name='pages_count']"/></span>
    </x:if>
    <span><c:out value="${collapseCount}" escapeXml="false" /></span>
    <%@ include file="results/default.jsp" %>
    </td>
    
    </tr>
    <tr class="uncollapsed r<c:out value="${status.count % 2}"/>"><td></td><td></td>
    <td id="uncollapsed_<c:out value="${root_pid}"/>"></td>
    </tr>
</x:forEach>
    </table>
--%>