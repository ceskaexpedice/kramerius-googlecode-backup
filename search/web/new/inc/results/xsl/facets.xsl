<xsl:stylesheet  version="1.0"
    xmlns:exts="java://cz.incad.utils.XslHelper"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="exts java"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="no" encoding="UTF-8" omit-xml-declaration="yes" />

    <xsl:param name="bundle_url" select="bundle_url" />
    <xsl:param name="bundle" select="document($bundle_url)/bundle" />
    <xsl:param name="numOpenedRows" select="numOpenedRows" />
    <xsl:variable name="generic" select="exts:new()" />
    
    <xsl:template match="/">
        <ul>
            <xsl:call-template name="facets" />
        </ul>
    </xsl:template>

    <xsl:template name="facets">
        <xsl:for-each select="response/lst[@name='facet_counts']/lst[@name='facet_fields']/lst">
            <xsl:variable name="nav" >
                <xsl:copy-of select="."/>
            </xsl:variable>
            <xsl:variable name="navName" >
                <xsl:value-of select="./@name"/>
            </xsl:variable>
            <xsl:if test="count(./int) > 1 and not($navName = 'rok')">
                <xsl:call-template name="facet">
                    <xsl:with-param name="facetname"><xsl:value-of select="./@name" /></xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
        <script type="text/javascript">
        <xsl:comment><![CDATA[
        $(document).ready(function(){
            $('#facets>ul>li>ul>li.more_facets').toggle();
            $('#facets>ul>li').click(function(event){
                var id = $(this).attr('id');
                $('#'+id+'>ul>li.more_facets').toggle();
                $('#'+id).toggleClass('ui-icon-triangle-1-s');
            });
        });

        ]]></xsl:comment>
        </script>
    </xsl:template>

    <xsl:template name="facet">
        <xsl:param name="facetname" />
        <li>
            <xsl:attribute name="id">facet_<xsl:value-of select="$facetname"/></xsl:attribute>
            <span class="ui-icon ui-icon-triangle-1-e folder" ></span>
            <a href="#"><xsl:value-of select="$bundle/value[@key=$facetname]" /></a>
            <ul><xsl:for-each select="./int">
                <li><xsl:if test="position() &gt; $numOpenedRows">
                    <xsl:attribute name="class">more_facets</xsl:attribute>
                </xsl:if>
                <a><xsl:attribute name="href">javascript:addFilter()</xsl:attribute><xsl:value-of select="@name" /></a> (<xsl:value-of select="." />)
                </li>
            </xsl:for-each>
            </ul>
        </li>
    </xsl:template>

</xsl:stylesheet>
