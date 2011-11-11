<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>

    
    <div id="more_<c:out value="${uuid}"/>" 
    <c:if test="${status.count > 5}" >
        style="display:none;"
    </c:if>
    >
    <x:if select="./arr[@name='dc.creator']/str/text()">
    <x:forEach varStatus="c" select="./arr[@name='dc.creator']/str">
        <c:if test="${c.count>1}">;&#160;</c:if><x:out select="."/>
    </x:forEach><br/>
    </x:if>
    <x:if select="./str[@name='datum']">
    <x:out select="./str[@name='datum']"/><br/>
    </x:if>
    
        <x:choose>
            <x:when select="./str[@name='fedora.model'] = 'monograph'">
                
            </x:when>
            <x:when select="./str[@name='fedora.model'] = 'monographunit'">
                
            </x:when>
            <x:when select="./str[@name='fedora.model'] = 'page'">
                <fmt:message bundle="${lctx}">fedora.model.page</fmt:message>&#160;
                <x:out select="substring-before(./arr[@name='details']/str, '##')" />&#160;
                <c:set var="s"><fmt:message bundle="${lctx}"><x:out select="substring-after(./arr[@name='details']/str, '##')" /></fmt:message></c:set>
                <c:out value="${fn:replace(s, '???', '')}" />&#160;
            </x:when>
            <x:when select="./str[@name='fedora.model'] = 'periodical2'">
                
            </x:when>
            <x:when select="./str[@name='fedora.model'] = 'periodicalvolume2'">
                
            </x:when>
            <x:when select="./str[@name='fedora.model'] = 'periodicalitem2'">
                
            </x:when>
            <x:when select="./str[@name='fedora.model'] = 'internalpart'">
                <fmt:message bundle="${lctx}"><x:out select="substring-before(./arr[@name='details']/str, '##')" /></fmt:message>&#160;
                <c:set var="remaining"><x:out select="substring-after(./arr[@name='details']/str, '##')" /></c:set>
                <c:out value="${fn:substringBefore(remaining, '##')}" />&#160;
                <c:set var="remaining" value="${fn:substringAfter(remaining, '##')}" />
                <c:out value="${fn:substringBefore(remaining, '##')}" />&#160;
                <c:out value="${fn:substringAfter(remaining, '##')}" />
            </x:when>
            <x:otherwise>
                <x:forEach select="./arr[@name='details']/str">
                    <c:set var="s"><fmt:message bundle="${lctx}"><x:out select="."/></fmt:message></c:set>
                    <c:out value="${fn:replace(s, '???', '')}" />&#160;
                </x:forEach>
            </x:otherwise>
        </x:choose>
        
        <div class="teaser">
        <x:forEach select="../../lst[@name='highlighting']/lst">
            <c:set var="hituuid"><x:out select="@name" /></c:set>
            <c:if test="${hituuid==solruuid}">
                <x:forEach select="./arr[@name='text']/str">
                (... <x:out select="." escapeXml="false" /> ...)<br/>
                </x:forEach>
            </c:if>
        </x:forEach>
        </div>
    </div>
    <br/>