<?xml version='1.0'?>

<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	xmlns:saxon="http://icl.com/saxon"
	extension-element-prefixes="saxon"> 

	<xsl:import href="../docbook-xsl-ns-1.75.2/fo/docbook.xsl"/>
	<xsl:param name="body.font.family" select="'DejaVu Serif'"/>
	<xsl:param name="dingbat.font.family" select="'DejaVu Sans'"/>
	<xsl:param name="monospace.font.family" select="'DejaVu Sans Mono'"/>
	<xsl:param name="sans.font.family" select="'DejaVu Sans'"/>
	<xsl:param name="title.font.family" select="'DejaVu Sans'"/>
	<xsl:param name="symbol.font.family" select="'DejaVu Sans'"/>
	<xsl:output
		method="xml"
		encoding="UTF-8"
		indent="no"
		saxon:character-representation="native;decimal"
	/>
</xsl:stylesheet> 