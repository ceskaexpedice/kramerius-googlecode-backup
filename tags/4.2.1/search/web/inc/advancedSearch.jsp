<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div id="advSearch" class="advSearch shadow10">
    <img src="img/x.png" align="right" onclick="$('#advSearch').hide();"/><br/>
    <table class="advancedSearch">
        <col width="150px">
        <tbody>
            <tr><td colspan="2"><strong>Metadata<strong></strong></strong></td></tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.isbnissn" /></td>
                <td><input type="text" value="<c:out value="${param.issn}" />" size="20" name="issn"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.title" /></td>
                <td><input type="text" value="<c:out value="${param.title}" />" size="20" name="title"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.author" /></td>
                <td><input type="text" value="<c:out value="${param.author}" />" size="20" name="author"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.year" /></td>
                <td><input type="text" value="<c:out value="${param.rok}" />" size="10" name="rok"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.mdt" /></td>
                <td><input type="text" value="<c:out value="${param.udc}" />" size="20" name="udc"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="filter.query.ddt" /></td>
                <td><input type="text" value="<c:out value="${param.ddc}" />" size="20" name="ddc"></td>
            </tr>
            <tr>
                <td><fmt:message bundle="${lctx}" key="Pouze veřejné dokumenty" /></td>
                <td><input type="checkbox" value="on" name="onlyPublic"></td>
            </tr>
            <tr>
                <td colspan="2" align="right"><input type="submit" value="OK" class="ui-state-default ui-corner-all" /></td>
            </tr>
    </tbody></table>
</div>