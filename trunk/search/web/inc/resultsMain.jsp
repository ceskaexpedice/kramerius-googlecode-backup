<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>


<div id="results_main">
    <x:forEach varStatus="status" select="$doc/response/result/doc">
    <div id="res_<c:out value="${uuid}"/>" class="r<c:out value="${status.count % 2}" />">
        <c:set var="uuid" >
            <x:out select="./str[@name='PID']"/>
        </c:set>
        <c:set var="root_pid" >
            <x:out select="./str[@name='root_pid']"/>
        </c:set>
        <jsp:useBean id="uuid" type="java.lang.String" />
        <c:set var="itemUrl" >
            ./item.jsp?pid=<c:out value="${uuid}"/>&pid_path=<x:out select="./str[@name='pid_path']"/>&path=<x:out select="./str[@name='path']"/>
        </c:set>
        <x:if select="./str[@name='fedora.model'] = 'page'">
            <c:set var="itemUrl" ><c:out value="${itemUrl}"/>&format=<x:out select="./str[@name='page_format']"/></c:set>
        </x:if>
        <x:set select="./str[@name='PID']" var="pid" />
    <%@ include file="../admin/resultOptions.jsp" %>
        <x:forEach select="//response/lst[@name='collapse_counts']/lst[@name='results']/lst">
            <x:if select="./@name=$pid">
            <c:set var="collapseCount" >
                <a href="javascript:uncollapse('<c:out value="${root_pid}" />', 'uncollapsed_<c:out value="${uuid}"/>', 0)"><x:out select="./int[@name='collapseCount']/text()"/> collapsed</a>
            </c:set>  
            </x:if>
        </x:forEach>
        
       
    <img src="img/empty.gif" 
    <c:if test="${status.count > 5}" >
    class="plus" onclick="$('#more_<c:out value="${uuid}"/>').toggle();$(this).toggleClass('minus')" 
    </c:if>
    />
    <img src="<c:out value="${kconfig.fedoraHost}" />/get/uuid:<x:out select="./str[@name='PID']"/>/IMG_THUMB" height="72px" onerror="this.src='img/empty.gif';this.height='1px';" />
    <a href="<c:out value="${itemUrl}" escapeXml="false" />" ><b><x:out select="./str[@name='root_title']"/></b></a>
    <span class="textpole">(<fmt:message><x:out select="./str[@name='fedora.model']"/></fmt:message>)</span>
    <x:if select="./int[@name='pages_count'] != '0'">
    <span><x:out select="./int[@name='pages_count']"/></span>
    </x:if>
    <span><c:out value="${collapseCount}" escapeXml="false" /></span>
    <x:choose>
        <x:when select="./str[@name='fedora.model'] = 'monograph2'">
            <%@ include file="results/monograph.jsp" %>
        </x:when>
        <x:when select="./str[@name='fedora.model'] = 'monographunit2'">
            <%@ include file="results/monographunit.jsp" %>
        </x:when>
        <x:when select="./str[@name='fedora.model'] = 'page2'">
            <%@ include file="results/page.jsp" %>
        </x:when>
        <x:when select="./str[@name='fedora.model'] = 'periodical2'">
            <%@ include file="results/periodical.jsp" %>
        </x:when>
        <x:when select="./str[@name='fedora.model'] = 'periodicalvolume2'">
            <%@ include file="results/periodicalvolume.jsp" %>
        </x:when>
        <x:when select="./str[@name='fedora.model'] = 'periodicalitem'">
            <%@ include file="results/periodicalitem.jsp" %>
        </x:when>
        <x:otherwise>
            <%@ include file="results/default.jsp" %>
        </x:otherwise>
    </x:choose>
    </div>
    <div id="uncollapsed_<c:out value="${uuid}"/>" class="uncollapsed"></div>
</x:forEach>
    </div>