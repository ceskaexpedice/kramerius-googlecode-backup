<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:sp="http://www.w3.org/2001/sw/DataAccess/rf1/result"
version="1.0">
    <xsl:output method="html" encoding="UTF-8" />
    <xsl:param name="rows" select="'10'"/>
    <xsl:param name="offset" select="'0'"/>
    <xsl:param name="sort" select="'title'"/>
    <xsl:param name="sort_dir" select="'asc'"/>
    <xsl:param name="model" select="model"/>
    <xsl:template match="/">
        
        <xsl:for-each select="/sp:sparql/sp:results/sp:result">
            <xsl:variable name="title" select="normalize-space(./sp:title)" />
            <xsl:variable name="date" select="normalize-space(./sp:date)" />
            <tr class="indexer_result"><xsl:attribute name="pid"><xsl:value-of select="./sp:object/@uri" /></xsl:attribute>
            <td class="indexer_result_status"></td><td>
            - 
            <a><xsl:attribute name="href">javascript:indexDoc('<xsl:value-of select="./sp:object/@uri" />', '<xsl:value-of select="$title" />');</xsl:attribute><xsl:value-of select="./sp:title" /></a>
            </td><td><xsl:value-of select="./sp:date" /></td>
            </tr>
        </xsl:for-each>
        <tr><td class="indexer_pager" colspan="2">
            <xsl:if test="$offset>0"><a><xsl:attribute name="href">javascript:loadFedoraDocuments('<xsl:value-of select="$model" />', <xsl:value-of select="$offset - $rows" />, '<xsl:value-of select="$sort" />', '<xsl:value-of select="$sort_dir" />')</xsl:attribute>previous</a></xsl:if>
            <xsl:if test="count(/sp:sparql/sp:results/sp:result)=$rows"><a><xsl:attribute name="href">javascript:loadFedoraDocuments('<xsl:value-of select="$model" />', <xsl:value-of select="$offset + $rows" />, '<xsl:value-of select="$sort" />', '<xsl:value-of select="$sort_dir" />')</xsl:attribute>next</a></xsl:if>
        </td></tr>
    </xsl:template>

</xsl:stylesheet>
