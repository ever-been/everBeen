<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>
	<xsl:output method="text" omit-xml-declaration="yes"/>

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="chapter">
		echo "# <xsl:value-of select="title"/>"
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="section">
		cat src/book/markdown/<xsl:value-of select="id"/>.md
		echo
	</xsl:template>

	<xsl:template match="book">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="chapters">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="sections">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="id"/>
	<xsl:template match="title"/>
	<xsl:template match="author"/>

</xsl:stylesheet>
