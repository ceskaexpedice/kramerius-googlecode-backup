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
            String[] tabs = kconfig.getPropertyList("search.item.tabs");
            pageContext.setAttribute("tabs", tabs);
%>
<div id="centralContent">
    <ul>
        <li><a href="#bigThumbZone" class="vertical-text" ><fmt:message bundle="${lctx}">item.tab.image</fmt:message></a>        </li>
        <li><a href="#extendedMetadata" class="vertical-text" ><fmt:message bundle="${lctx}">item.tab.metadata</fmt:message></a></li>
        <c:forEach varStatus="status" var="tab" items="${tabs}"><c:if test="${! empty tab}">
                <li><a href="#itemtab_${tab}" class="vertical-text"><fmt:message bundle="${lctx}">item.tab.${tab}</fmt:message></a></li>
            </c:if></c:forEach>
        </ul>
    <%@include file="metadata.jsp" %>
    <%@include file="image.jsp" %>
    <c:forEach varStatus="status" var="tab" items="${tabs}">
        <c:if test="${! empty tab}">
            <div id="itemtab_${tab}" class="viewer" style="overflow:hidden;"></div>
            <script type="text/javascript">
                updateCustomTab('${tab}', '${param.pid}');
                $(document).ready(function(){
                    $('#itemtab_${tab}.viewer').bind('viewReady', function(event, viewerOptions){
                        updateCustomTab('${tab}', viewerOptions.uuid);
                    });
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
    function setMainContentWidth(){ 
        var w = $(window).width()-6-$('#itemTree').width();

        //alert(w);
        $("#mainContent").css('width', w);
        //alert($('#centralContent').width());
        w = w-45;
        $("#centralContent").css('width', w);

        //w = $('#centralContent').width();
        $("#centralContent>div").css('width', w-30-25);
        $("#extendedMetadata").css('width', w-30-25);
        $("#bigThumbZone").css('width', w-30-25);
    }

    $(document).ready(function() {
        $("#centralContent").tabs().addClass('ui-tabs-vertical ui-helper-clearfix');
        $("#centralContent li").removeClass('ui-corner-top').addClass('ui-corner-left');
        $("#centralContent").css('position', 'static');
        //$('.vertical-text').mbFlipText(false);
    });

</script>