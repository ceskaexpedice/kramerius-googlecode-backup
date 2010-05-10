<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-3.xsd">
<xsl:output encoding='UTF-8' indent='yes' />
<xsl:template match="/">
<mods:modsCollection xmlns:mods="http://www.loc.gov/mods/v3"> 
	
	<mods:mods version="3.3">
	
		<mods:identifier type="urn"><xsl:value-of select="/Monograph/UniqueIdentifier/UniqueIdentifierURNType" /></mods:identifier>
		<mods:identifier type="sici"><xsl:value-of select="/Monograph/UniqueIdentifier/UniqueIdentifierSICIType" /></mods:identifier>
		
		<mods:identifier type="isbn"><xsl:value-of select="/Monograph/MonographBibliographicRecord/ISBN" /></mods:identifier>
		<mods:identifier type="issn"><xsl:value-of select="/Monograph/MonographBibliographicRecord/Series/ISSN" /></mods:identifier>
	
		<mods:titleInfo>
			<mods:title><xsl:value-of select="/Monograph/MonographBibliographicRecord/Title/MainTitle" /></mods:title>
			<mods:subTitle><xsl:value-of select="/Monograph/MonographBibliographicRecord/Title/SubTitle" /></mods:subTitle>
		</mods:titleInfo>
		<mods:titleInfo type="alternative">
			<mods:title><xsl:value-of select="/Monograph/MonographBibliographicRecord/Title/ParallelTitle" /></mods:title>
		</mods:titleInfo>
		
		<mods:subject>
		    <mods:topic>
		    	<xsl:value-of select="/Monograph/MonographBibliographicRecord/Keyword" />
		    </mods:topic>
		    
			<mods:cartographics>
		 		<mods:scale>
		 			<xsl:value-of select="/Monograph/MonographBibliographicRecord/PhysicalDescription/Scale" />
		 		</mods:scale>
			</mods:cartographics>
		</mods:subject>
	
		<mods:abstract><xsl:value-of select="/Monograph/MonographBibliographicRecord/Annotation" /></mods:abstract>
		
		<xsl:for-each select="/Monograph/MonographOwner">
			<mods:location>
				<mods:physicalLocation><xsl:value-of select="Library" /></mods:physicalLocation>
				<xsl:for-each select="ShelfNumber">
					<mods:shelfLocator><xsl:value-of select="." /></mods:shelfLocator>
				</xsl:for-each>
			</mods:location>
		</xsl:for-each>
		
		<!-- 
		  - Creator
		  - /Monograph/MonographBibliographicRecord/Creator
		  -->
		<xsl:for-each select="/Monograph/MonographBibliographicRecord/Creator">
			<xsl:if test="not(CreatorSurname='***Donator NF***')">
				<mods:name type="personal">
					<mods:namePart type="family">
						<xsl:value-of select="CreatorSurname" />
					</mods:namePart>
					<xsl:for-each select="CreatorName">
						<mods:namePart type="given">
							<xsl:value-of select="." />
						</mods:namePart>
					</xsl:for-each>
					<mods:role>
						<mods:roleTerm type="code">cre</mods:roleTerm>
						<mods:roleTerm type="text"><xsl:value-of select="@Role" /></mods:roleTerm>
					</mods:role>
				</mods:name>
			</xsl:if>
		</xsl:for-each>

		<!-- 
		  - Contributor
		  - /Monograph/MonographBibliographicRecord/Contributor
		  -->
		<xsl:for-each select="/Monograph/MonographBibliographicRecord/Contributor">
			<mods:name type="personal">
				<mods:namePart type="family">	
					<xsl:value-of select="ContributorSurname" />
				</mods:namePart>			
				<xsl:for-each select="ContributorName">
					<mods:namePart type="given">
						<xsl:value-of select="." />
					</mods:namePart>
				</xsl:for-each>
				<mods:role>		
					<!-- NOTE: Pouzit kod pro i text pro blizsi urrceni-->		
					<mods:roleTerm type="code">ctb</mods:roleTerm>
					<mods:roleTerm type="text"><xsl:value-of select="@Role" /></mods:roleTerm>	
				</mods:role>
			</mods:name>
		</xsl:for-each>

		<!-- 
		  - Current version of Kramerius contains texts only  
		  -->		
		<mods:typeOfResource>text</mods:typeOfResource>

		<!-- Publisher -->
		<xsl:for-each select="/Monograph/MonographBibliographicRecord/Publisher">
			<mods:originInfo transliteration="publisher">
				<mods:place>
					<mods:placeTerm type="text"><xsl:value-of select="PlaceOfPublication" /></mods:placeTerm>
				</mods:place>
				<mods:publisher><xsl:value-of select="PublisherName" /></mods:publisher>
				<mods:dateIssued><xsl:value-of select="DateOfPublication" /></mods:dateIssued>
			</mods:originInfo>
		</xsl:for-each>

		<!-- Printer -->
		<xsl:for-each select="/Monograph/MonographBibliographicRecord/Printer">
			<mods:originInfo transliteration="printer">
				<mods:place>
					<mods:placeTerm><xsl:value-of select="PlaceOfPrinting" /></mods:placeTerm>
				</mods:place>
				<mods:publisher><xsl:value-of select="PrinterName" /></mods:publisher>
				<mods:dateCreated><xsl:value-of select="DateOfPrinting" /></mods:dateCreated>
			</mods:originInfo>
		</xsl:for-each>
		
		<mods:originInfo>
			<mods:issuance>monographic</mods:issuance>
		</mods:originInfo>

		<xsl:for-each select="/Monograph/MonographBibliographicRecord/Language">
			<mods:language>
				<mods:languageTerm type="code" authority="iso639-2b"><xsl:value-of select="." /></mods:languageTerm>
			</mods:language>
		</xsl:for-each>
		
		<mods:physicalDescription>
			<mods:form type="technique"><xsl:value-of select="/Monograph/MonographBibliographicRecord/PhysicalDescription/Technique" /></mods:form>
			<mods:form type="material"><xsl:value-of select="/Monograph/MonographBibliographicRecord/PhysicalDescription/Material" /></mods:form>
			<mods:extent>
				<xsl:value-of select="/Monograph/MonographBibliographicRecord/PhysicalDescription/Extent" />
				<xsl:if test="/Monograph/MonographBibliographicRecord/PhysicalDescription/Extent/text() and
						/Monograph/MonographBibliographicRecord/PhysicalDescription/Size/text()">,</xsl:if>
				<xsl:value-of select="/Monograph/MonographBibliographicRecord/PhysicalDescription/Size" />
			</mods:extent>
			<mods:note><xsl:value-of select="/Monograph/MonographBibliographicRecord/Notes" /></mods:note>
		</mods:physicalDescription>

		<xsl:for-each select="/Monograph/MonographBibliographicRecord/PhysicalDescription/PreservationStatus"> 
			<mods:physicalDescription>
				<mods:note type="action"><xsl:value-of select="PreservationTreatment" /></mods:note>
				<mods:note type="preservationStateOfArt"><xsl:value-of select="PreservationStateOfArt" /></mods:note>	
			</mods:physicalDescription>
		</xsl:for-each>

		<!-- Monograph subject -->
		<mods:classification authority="ddc"><xsl:value-of select="/Monograph/MonographBibliographicRecord/Subject/DDC" /></mods:classification>
		<mods:classification authority="udc"><xsl:value-of select="/Monograph/MonographBibliographicRecord/Subject/UDC" /></mods:classification>
		
		<mods:relatedItem type="series">
			<mods:titleInfo>
				<mods:title><xsl:value-of select="/Monograph/MonographBibliographicRecord/Series/SeriesTitle" /></mods:title>
			</mods:titleInfo>
		</mods:relatedItem>
		
		<mods:accessCondition type="restrictionOnAccess"><xsl:value-of select="/Monograph/MonographBibliographicRecord/Accessibility" /></mods:accessCondition>
	</mods:mods>
</mods:modsCollection>

</xsl:template>
</xsl:stylesheet>