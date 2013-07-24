<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tlds/securedContent.tld" prefix="scrd" %>
<%@ taglib uri="/WEB-INF/tlds/cmn.tld" prefix="view" %>

<%@ page isELIgnored="false"%>
<div  id="main_menu_in">
    <view:object name="buttons" clz="cz.incad.Kramerius.views.inc.MenuButtonsViewObject"></view:object>

    <c:forEach items="${buttons.languageItems}" var="langitm">
        <a href="${langitm.link}">${langitm.name}</a>
    </c:forEach>
        <!-- Registrace pouze pro neprihlasene -->
        <scrd:notloggedusers>
            <view:kconfig var="showthisbutton" key="search.mainbuttons.showregistrationbutton"></view:kconfig>
            <c:if test="${showthisbutton == 'true'}">
                <a id="registerHref" href="javascript:registerUser.register();"><view:msg>registeruser.menu.title</view:msg></a>
            </c:if>
        </scrd:notloggedusers>

        <!--  show admin menu - only for logged users -->
        <scrd:loggedusers>
            <a id="adminHref" href="javascript:showAdminMenu();"><view:msg>administrator.menu</view:msg></a>
        </scrd:loggedusers>
        
        <!-- login - only for notlogged -->
        <scrd:notloggedusers>
            <a href="redirect.jsp?redirectURL=${searchFormViewObject.requestedAddress}"><view:msg>application.login</view:msg></a>
        </scrd:notloggedusers>
        
        <!-- logout - only for logged -->
        <scrd:loggedusers>
            <c:choose>
                <c:when test="${empty buttons.shibbLogout}">
                            <a href="logout.jsp?redirectURL=${searchFormViewObject.requestedAddress}"><fmt:message bundle="${lctx}">application.logout</fmt:message></a>
                </c:when>
                <c:otherwise>
                            <a href="${buttons.shibbLogout}"><view:msg>application.logout</view:msg></a>
                </c:otherwise>
            </c:choose>
        </scrd:loggedusers>

<a href="javascript:showHelp('<c:out value="${param.language}" />');"><view:msg>application.help</view:msg>
</a>
<c:if test="${rows != 0}" ><a href="."><view:msg>application.home</view:msg></a></c:if>
</div>

<scrd:loggedusers>
    <%@include file="adminMenu.jsp" %>
</scrd:loggedusers>