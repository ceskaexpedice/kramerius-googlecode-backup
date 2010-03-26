<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>

<c:choose>
    <c:when test="${param.language != null}" >
        <fmt:setLocale value="${param.language}" />
    </c:when>
</c:choose>

<fmt:setBundle basename="labels" />
<fmt:setBundle basename="labels" var="bundleVar" />

<div id="intro">
     <ul>
         <li><a href="#intro1"><fmt:message>Nejnovější</fmt:message></a></li>
         <li><a href="#intro2"><fmt:message>Nejžádanější</fmt:message></a></li>
         <li><a href="#intro3"><fmt:message>Informace</fmt:message></a></li>
     </ul>
     <div id="intro1"><img src="img/intro1.png" /></div>
     <div id="intro2"><img src="img/intro2.png" /></div>
     <div id="intro3"><c:choose>
                        <c:when test="${param.language == 'en'}"><%@ include file="text/intro_en.jsp" %></c:when>
                        <c:when test="${param.language == 'cs'}"><%@ include file="text/intro_cs.jsp" %></c:when>
                        <c:otherwise><%@ include file="text/intro_cs.jsp" %></c:otherwise>
                </c:choose></div>
</div>
<script language="javascript">
        $(document).ready(function(){
            $('#intro').tabs();
        });
    </script>