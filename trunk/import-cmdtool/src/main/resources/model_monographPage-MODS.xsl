<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output encoding='UTF-8' indent='yes' />
<xsl:template match="/">
<mods:modsCollection xmlns:mods="http://www.loc.gov/mods/v3">
	<mods:mods version="3.4">
		<xsl:if test="/MonographPage/UniqueIdentifier/UniqueIdentifierURNType">
			<mods:identifier type="urn"><xsl:value-of select="/MonographPage/UniqueIdentifier/UniqueIdentifierURNType" /></mods:identifier>
		</xsl:if>
		<xsl:if test="/MonographPage/UniqueIdentifier/UniqueIdentifierSICIType">
			<mods:identifier type="sici"><xsl:value-of select="/MonographPage/UniqueIdentifier/UniqueIdentifierSICIType" /></mods:identifier>
		</xsl:if>			
		<!-- 
		  - Current version of Kramerius contains texts only  
		  -->		
		<mods:typeOfResource>text</mods:typeOfResource>
		
		<mods:part>
			<xsl:attribute name="type"><xsl:value-of select="/MonographPage/@Type" /></xsl:attribute>
			<xsl:for-each select="/MonographPage/PageNumber">
				<mods:detail type="pageNumber">
					<mods:number><xsl:value-of select="." /></mods:number>
				</mods:detail>
			</xsl:for-each>
			<xsl:if test="/MonographPage/@Index">
				<mods:detail type="pageIndex">
					<mods:number><xsl:value-of select="/MonographPage/@Index" /></mods:number>
				</mods:detail>
			</xsl:if>	
			<xsl:if test="/MonographPage/Notes">
				<mods:text><xsl:value-of select="/MonographPage/Notes" /></mods:text>
			</xsl:if>	
		</mods:part>
	</mods:mods>
</mods:modsCollection>
</xsl:template>
</xsl:stylesheet>