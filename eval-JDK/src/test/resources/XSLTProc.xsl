<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="/">
           <xsl:value-of select="XYZ/Grp/Employee/@name"/>,
           <xsl:value-of select="XYZ/Grp/Manager/@name"/>,
  </xsl:template>
</xsl:stylesheet>