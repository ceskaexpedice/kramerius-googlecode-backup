<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div id="advSearch" class="shadow" style="display:none;z-index:11">
    <table class="advancedSearch">
        <col width="150px">
        <tbody>
            <tr><td colspan="2"><strong>Metadata</strong></td></tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.isbnissn" /></td>
                <td><input type="text" value="<c:out value="${param.issn}" />" size="20" name="issn" id="issn"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.title" /></td>
                <td><input type="text" value="<c:out value="${param.title}" />" size="20" name="title" id="title"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.author" /></td>
                <td><input type="text" value="<c:out value="${param.author}" />" size="20" name="author" id="author"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.year" /></td>
                <td><input type="text" value="<c:out value="${param.rok}" />" size="10" name="rok" id="rok"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.mdt" /></td>
                <td><input type="text" value="<c:out value="${param.udc}" />" size="20" name="udc" id="udc"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.ddt" /></td>
                <td><input type="text" value="<c:out value="${param.ddc}" />" size="20" name="ddc" id="ddc"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="Pouze veřejné dokumenty" /></td>
                <td><input type="checkbox" value="on" name="onlyPublic"></td>
            </tr>
            <tr>
                <td colspan="2" align="right">
                    <input type="submit" value="OK" class="ui-state-default ui-corner-all" />
                    <input type="button" value="close" class="ui-state-default ui-corner-all"  onclick="$('#advSearch').hide();" />
                </td>
            </tr>
    </tbody></table>
</div>