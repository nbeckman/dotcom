<?xml version="1.0" encoding="ISO-8859-1"?>
<html xsl:version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns="http://www.w3.org/1999/xhtml">
<head><title><xsl:value-of select="recipeml/recipe/head/title"/></title></head>
<body
	style="scrollbar-face-color:#AED09E,
	scrollbar-shadow-color:#436532,
	scrollbar-highlight-color:#FFFFFF,
	scrollbar-3dlight-color:#000000,
	scrollbar-darkshadow-color:#000000,
	scrollbar-arrow-color:#000000"
	bottomMargin="0" leftMargin="0" background="../background.jpg" topMargin="0" rightMargin="0" marginwidth="0" marginheight="0">

<table height="143" cellSpacing="0" cellPadding="0" width="780" border="0">
<tr vAlign="top">
<td width="780"><img height="143" alt="" src="/blanklogo.jpg" width="780" border="0"/></td>
</tr>
</table>
	<table cellSpacing="0" cellPadding="0" width="100%" border="0">
			<tr vAlign="top">
				<td width="175">
					<table cellSpacing="0" cellPadding="4" width="175" border="0">
						<tr vAlign="top">
							<td width="175">
							</td>
						</tr>
					</table>
				</td>
				<td width="510">
					<table cellSpacing="5" cellPadding="5" width="510" border="0">
						<tr vAlign="top">
							<td width="510">
<h3><center><xsl:value-of select="recipeml/recipe/head/title"/></center></h3>
<hr/>
<b>Ingredients:</b><br/>
<ul>
	<xsl:for-each select="recipeml/recipe/ingredients/ing">
    	<li><xsl:value-of select="item"/>
	<ul><li><xsl:value-of select="amt/qty"/> <xsl:value-of select="amt/unit"/></li></ul>
	</li>
	</xsl:for-each>
</ul>
<hr/>
<b>Directions:</b><br/>
<ul>
	<xsl:for-each select="recipeml/recipe/directions/step">
	<li><xsl:value-of select="current()"/></li>
	</xsl:for-each>
</ul>
<hr/>
</td>
</tr>
</table>
</td>
</tr>
</table>
</body>
</html>