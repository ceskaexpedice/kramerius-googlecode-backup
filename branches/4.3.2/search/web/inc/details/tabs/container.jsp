<%@page import="com.google.inject.Injector"%>
<%@page import="java.util.Locale"%>
<%@page import="com.google.inject.Provider"%>
<%@page import="cz.incad.Kramerius.backend.guice.LocalesProvider"%>
<%@page import="java.io.*, cz.incad.kramerius.service.*"  %>
<%@page import="cz.incad.kramerius.utils.conf.KConfiguration"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page isELIgnored="false"%>
<%
            //Injector ctxInj = (Injector) application.getAttribute(Injector.class.getName());
            //KConfiguration kconfig = ctxInj.getProvider(KConfiguration.class).get();
            //pageContext.setAttribute("kconfig", kconfig);
            String[] tabs = kconfig.getPropertyList("search.item.tabs");
            pageContext.setAttribute("tabs", tabs);
%>
<div id="centralContent">
    <ul>
        <li><a href="#bigThumbZone" class="vertical-text" ><fmt:message bundle="${lctx}">item.tab.image</fmt:message></a>        </li>
        <li><a href="#extendedMetadata" class="vertical-text" ><fmt:message bundle="${lctx}">item.tab.metadata</fmt:message></a></li>
        <c:forEach varStatus="status" var="tab" items="${tabs}">
        <c:if test="${fn:length(tab)>0}" >
        <li><a href="#itemtab_${tab}" class="vertical-text"><fmt:message bundle="${lctx}">item.tab.${tab}</fmt:message></a></li>
        </c:if>
        </c:forEach>
    </ul>
    <%@include file="metadata.jsp" %>
    <%@include file="image.jsp" %>
    <c:forEach varStatus="status" var="tab" items="${tabs}">
        <c:if test="${fn:length(tab)>0}" >
        <div id="itemtab_${tab}" style="overflow:hidden;"></div>
        <script type="text/javascript">
			customTabs[customTabs.length] = '${tab}';
			$(document).ready(function(){
                updateCustomTab('${tab}', '${param.pid}');
            });
        </script>
        </c:if>
    </c:forEach>
</div>
<script type="text/javascript">

    function updateCustomTab(tab, pid){
         $.get('inc/details/tabs/loadCustom.jsp?tab='+tab+'&pid=' + pid, function(data){
            $('#itemtab_'+tab).html(data) ;
        });
    }
   function updateCustomTabs(pid){

       for(var i=0; i<customTabs.length; i++){
	   updateCustomTab(customTabs[i], pid);
       }
    }

</script>
