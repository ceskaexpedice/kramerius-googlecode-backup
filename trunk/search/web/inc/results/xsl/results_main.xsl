<xsl:stylesheet  version="1.0"
    xmlns:exts="java://cz.incad.utils.XslHelper"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="exts java"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="no" encoding="UTF-8" omit-xml-declaration="yes" />

    <xsl:param name="bundle_url" select="bundle_url" />
    <xsl:param name="bundle" select="document($bundle_url)/bundle" />
    <xsl:param name="fqs" select="fqs" />
    <xsl:param name="q" select="q" />
    <xsl:param name="numOpenedRows" select="5" />
    <xsl:variable name="numDocs"><xsl:value-of select="number(/response/result/@numFound)" /></xsl:variable>
    <xsl:variable name="generic" select="exts:new()" />
    
    <xsl:template match="/">
        <div class="loading_docs" style="position:absolute; z-index:3;margin:auto;background:white;border:1px silver;width:1000px;height:200px;">
            <img src="img/loading.gif" />
        </div>
        <xsl:variable name="rows"><xsl:value-of select="number(/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='rows'])" /></xsl:variable>
        <xsl:variable name="start">
            <xsl:choose>
                <xsl:when test="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='start']/text()">
                    <xsl:value-of select="number(/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='start'])" />
                </xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>

        </xsl:variable>
        <xsl:choose>   
        <xsl:when test="/response/result/doc" >
            <xsl:choose>
                <xsl:when test="$start = 0">
                <xsl:call-template name="head" />
                <div class="clear"></div>
                <div>
                    <xsl:attribute name="id">offset_<xsl:value-of select="$start"/></xsl:attribute>
                    <xsl:call-template name="docs" />
                </div>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="docs" />
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="count(/response/result/doc) &lt; $numDocs">
            <div class="more_docs">
                <xsl:attribute name="id">offset_<xsl:value-of select="$start + $rows"/></xsl:attribute>
            <img src="img/loading.gif" /><br/>loading more documents...</div>
            </xsl:if>
        </xsl:when>
        <xsl:otherwise>
            <xsl:if test="$start = 0">
                <div style="font-weight:bold;"><xsl:value-of select="$bundle/value[@key='results.nohits']"/></div>
                <div class="clear"></div>
            </xsl:if>
        </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="head">
        <xsl:variable name="numDocsStr">
            <xsl:choose>
                <xsl:when test="$numDocs = 1"><xsl:value-of select="$bundle/value[@key='common.documents.singular']"/></xsl:when>
                <xsl:when test="$numDocs &gt; 1 and $numDocs &lt; 5"><xsl:value-of select="$bundle/value[@key='common.documents.plural_1']"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$bundle/value[@key='common.documents.plural_2']"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <div class="header">
            <div style="float:left;margin-left:5px;width:100px;">
                <span><xsl:value-of select="$numDocs" />&#160;<xsl:value-of select="$numDocsStr" /></span>
            </div>
            <div style="float:left;margin-left:100px;width:300px;text-align:center;">
                <span><xsl:value-of select="$bundle/value[@key='results.sortby']"/>:</span>&#160;&#160;
                <xsl:choose>
                    <xsl:when test="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='sort']='root_title_cs asc'">
                        <a href="javascript:sortByTitle('desc');" ><xsl:value-of select="$bundle/value[@key='results.sortby.name']"/></a>&#160;
                        <span class="ui-icon ui-icon-triangle-1-n"  >asc</span>
                        <a href="javascript:sortByRank();"><xsl:value-of select="$bundle/value[@key='results.sortby.relevance']"/></a>
                    </xsl:when>
                    <xsl:when test="/response/lst[@name='responseHeader']/lst[@name='params']/str[@name='sort']='root_title_cs desc'">
                        <a href="javascript:sortByTitle('asc');" ><xsl:value-of select="$bundle/value[@key='results.sortby.name']"/></a>&#160;
                        <span class="ui-icon ui-icon-triangle-1-s"  >desc</span>
                        <a href="javascript:sortByRank();"><xsl:value-of select="$bundle/value[@key='results.sortby.relevance']"/></a>
                    </xsl:when>
                    <xsl:otherwise>
                        <a href="javascript:sortByTitle('asc');"><xsl:value-of select="$bundle/value[@key='results.sortby.name']"/></a>&#160;
                        <span>&#160;<xsl:value-of select="$bundle/value[@key='results.sortby.relevance']"/></span>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
            <div style="float:right;margin-right:30px;">
                <a id="cols1" class="cols" href="javascript:toggleColumns();"><xsl:value-of select="$bundle/value[@key='results.1column']"/></a>
                <a id="cols2" class="cols" href="javascript:toggleColumns();" style="display:none;"><xsl:value-of select="$bundle/value[@key='results.2column']"/></a>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="collapse">
        <xsl:param name="pid" />
        <xsl:param name="root_pid" />
        <xsl:param name="model_path" />
        <xsl:for-each select="/response/lst[@name='collapse_counts']/lst[@name='results']/lst">
            <xsl:if test="./@name=$pid">
                <xsl:variable name="collapseText" ><xsl:value-of select="./int[@name='collapseCount']/text()"/>&#160;<xsl:value-of select="$bundle/value[@key='collapsed']"/></xsl:variable>
                <xsl:variable name="collapseCount" >
                    (<a><xsl:attribute name="href">javascript:toggleCollapsed('<xsl:value-of select="$model_path"/>_<xsl:value-of select="$root_pid" />', '<xsl:value-of select="$pid" />', 0);</xsl:attribute>
                    <xsl:value-of select="$collapseText"/>
                    <img border="0" src="img/empty.gif" class="collapseIcon">
                        <xsl:attribute name="id">uimg_<xsl:value-of select="$root_pid"/></xsl:attribute>
                        <xsl:attribute name="alt"><xsl:value-of select="$collapseText"/></xsl:attribute>
                    </img>
                    </a>)
                </xsl:variable>
                <span><xsl:copy-of select="$collapseCount"  /></span>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

<xsl:template name="docs">
    <xsl:for-each select="/response/result/doc" >
                    <xsl:if test="position() mod 2 = 1" ><div class="clear"></div></xsl:if>
                    <xsl:variable name="pid" ><xsl:value-of select="./str[@name='PID']" /></xsl:variable>
                    <div>
                        <xsl:attribute name="class">search_result <xsl:value-of select="position() mod 2"/></xsl:attribute>
                        <xsl:attribute name="id">res_<xsl:value-of select="./arr[@name='model_path']/str[position()=1]"/>_<xsl:value-of select="./str[@name='root_pid']"/></xsl:attribute>
                    <div>
                        <xsl:attribute name="class">result</xsl:attribute>
                        <xsl:call-template name="doc">
                            <xsl:with-param name="pos"><xsl:value-of select="position()" /></xsl:with-param>
                            <xsl:with-param name="pid"><xsl:value-of select="$pid" /></xsl:with-param>
                        </xsl:call-template>
                    </div>
                    
                    <div class="collapse_label" style="text-align:right;">&#160;
                    <xsl:call-template name="collapse">
                        <xsl:with-param name="pid"><xsl:value-of select="$pid" /></xsl:with-param>
                        <xsl:with-param name="root_pid"><xsl:value-of select="./str[@name='root_pid']" /></xsl:with-param>
                        <xsl:with-param name="model_path"><xsl:value-of select="./arr[@name='model_path']/str[position()=1]"/></xsl:with-param>
                    </xsl:call-template>
                        <div style="display:none;">
                            <xsl:attribute name="class">shadow-bottom uncollapsed </xsl:attribute>
                            <xsl:attribute name="id">uncollapsed_<xsl:value-of select="./str[@name='root_pid']"/></xsl:attribute>
                        </div>
                    </div>
                    </div>
                </xsl:for-each>
</xsl:template>

    <xsl:template name="doc">
        <xsl:param name="pid" />
        <xsl:param name="pos" />
        <xsl:variable name="fmodel" ><xsl:value-of select="./str[@name='fedora.model']" /></xsl:variable>
        <xsl:variable name="root_pid" ><xsl:value-of select="./str[@name='root_pid']" /></xsl:variable>
        <xsl:variable name="link" >./i.jsp?pid=<xsl:value-of
        select="$pid"/>&amp;q=<xsl:value-of
        select="$q"/><xsl:value-of select="$fqs" disable-output-escaping="yes" />
        </xsl:variable>
        <xsl:variable name="imagepid" >
            <xsl:choose>
                <xsl:when test="contains($pid, '/@')">img?uuid=<xsl:value-of select="substring-before($pid, '/@')"/>&amp;stream=IMG_THUMB&amp;action=SCALE&amp;scaledHeight=128</xsl:when>
                <xsl:when test="$fmodel='page'">img?uuid=<xsl:value-of select="$pid"/>&amp;stream=IMG_THUMB&amp;action=SCALE&amp;scaledHeight=128</xsl:when>
                <xsl:otherwise>img?uuid=<xsl:value-of select="$pid" />&amp;stream=IMG_THUMB&amp;action=SCALE&amp;scaledHeight=128</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="model"><xsl:value-of select="concat('fedora.model.', ./str[@name='fedora.model'])" /></xsl:variable>
        <table><tr><td valign="top">
        <div class="resultThumb" valign="top">
            <a>
                <xsl:attribute name="href"><xsl:value-of select="normalize-space($link)"/></xsl:attribute>
            <img class="th" border="1" ><xsl:attribute name="id">img_<xsl:value-of select="$pid"/></xsl:attribute>
            <xsl:attribute name="src"><xsl:value-of select="$imagepid" /></xsl:attribute>
            </img></a>
            <br/><xsl:value-of select="$bundle/value[@key=$model]"/>
        </div>
        </td><td valign="top">
        <div class="resultText">
            <a><xsl:attribute name="href"><xsl:value-of select="normalize-space($link)"/></xsl:attribute>
            <b><xsl:value-of select="./str[@name='dc.title']"/></b></a>&#160;
            
            <div><xsl:attribute name="id">more_<xsl:value-of select="$pid"/></xsl:attribute>
            
            <xsl:call-template name="othersfields">
                <xsl:with-param name="fmodel"><xsl:value-of select="$fmodel" /></xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="details">
                <xsl:with-param name="fmodel"><xsl:value-of select="$fmodel" /></xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="teaser">
                <xsl:with-param name="pid"><xsl:value-of select="$pid" /></xsl:with-param>
            </xsl:call-template>
            <div class="extInfo" style="display:none;"><xsl:value-of select="./arr[@name='pid_path']/str[position()=1]"/></div>
            </div>
        </div>
        </td></tr></table>
    </xsl:template>

    <xsl:template name="othersfields" >
        <xsl:if test="./arr[@name='dc.creator']/str/text()">
            <xsl:for-each select="./arr[@name='dc.creator']/str">
                <xsl:if test="position() > 1">;&#160;</xsl:if><xsl:value-of select="."/>
            </xsl:for-each><br/>
        </xsl:if>
        <xsl:if test="./str[@name='datum']">
        <xsl:value-of select="./str[@name='datum']" /><br/>
        </xsl:if>
    </xsl:template>

    <xsl:template name="teaser">
        <xsl:param name="pid" />
        <div class="teaser">
        <xsl:for-each select="../../lst[@name='highlighting']/lst">
            <xsl:if test="@name = $pid">
                <xsl:for-each select="./arr[@name='text_ocr']/str">
                (... <xsl:value-of select="." disable-output-escaping="yes" /> ...)<br/>
                </xsl:for-each>
            </xsl:if>
        </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template name="details">
        <xsl:param name="fmodel" />
        <xsl:for-each select="./arr[@name='details']/str">
            <xsl:choose>
                <xsl:when test="$fmodel='periodicalvolume'">
                    <xsl:call-template name="periodicalvolume">
                        <xsl:with-param name="detail"><xsl:value-of select="." /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$fmodel='periodicalitem'">
                    <xsl:call-template name="periodicalitem">
                        <xsl:with-param name="detail"><xsl:value-of select="." /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$fmodel='monographunit'">
                    <xsl:value-of select="../../str[@name='dc.title']" /><br/>
                    <xsl:call-template name="monographunit">
                        <xsl:with-param name="detail"><xsl:value-of select="." /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$fmodel='internalpart'">
                    <xsl:value-of select="dc.title" />&#160;
                    <xsl:call-template name="internalpart">
                        <xsl:with-param name="detail"><xsl:value-of select="." /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$fmodel='page'">
                    <xsl:call-template name="page">
                        <xsl:with-param name="detail"><xsl:value-of select="." /></xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <span class="translate"><xsl:value-of select="." /></span>&#160;
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="periodicalvolume">
        <xsl:param name="detail" />
        <xsl:value-of select="$bundle/value[@key='Datum vydání']"/>:
        <xsl:value-of select="substring-before($detail, '##')" />&#160;
        <xsl:value-of select="$bundle/value[@key='Číslo']"/>
        <xsl:value-of select="substring-after($detail, '##')" />
    </xsl:template>
    
    <xsl:template name="periodicalitem">
        <xsl:param name="detail" />
        <xsl:value-of select="substring-before($detail, '##')" /><br/>
        <xsl:variable name="remaining" select="substring-after($detail, '##')" />
        <xsl:value-of select="substring-before($remaining, '##')" /><br/>
        <xsl:variable name="remaining" select="substring-after($remaining, '##')" />
        <xsl:value-of select="$bundle/value[@key='Datum vydání']"/>:&#160;
        <xsl:value-of select="substring-before($remaining, '##')" />&#160;
        <xsl:value-of select="$bundle/value[@key='Číslo']"/>&#160;
        <xsl:value-of select="substring-after($remaining, '##')" />
    </xsl:template>

    <xsl:template name="monographunit">
        <xsl:param name="detail" />
        <xsl:value-of select="$bundle/value[@key='Volume']"/>:&#160;<xsl:value-of select="substring-before($detail, '##')" />&#160;
        <xsl:value-of select="substring-after($detail, '##')" />
    </xsl:template>

    <xsl:template name="internalpart">
        <xsl:param name="detail" />
        <!--
        <xsl:value-of select="$bundle/value[@key=substring-before($detail, '##')]"/>:&#160;
        <xsl:variable name="remaining" select="substring-after($detail, '##')" />
        <xsl:value-of select="substring-before($remaining, '##')" />&#160;
        <xsl:variable name="remaining" select="substring-after($remaining, '##')" />
        <xsl:value-of select="substring-before($remaining, '##')" />&#160;
        <xsl:value-of select="substring-after($remaining, '##')" />
        -->
    </xsl:template>

    <xsl:template name="page">
        <xsl:param name="detail" />
        <xsl:value-of select="substring-before($detail, '##')" />&#160;
        <xsl:value-of select="$bundle/value[@key=substring-after($detail, '##')]"/>
    </xsl:template>

</xsl:stylesheet>
