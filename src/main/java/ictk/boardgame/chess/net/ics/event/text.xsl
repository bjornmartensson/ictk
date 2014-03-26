<?xml version="1.0"?>
<!--
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke <ictk.jvarsoke [at] neverbox.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:str="http://ictk.sourceforge.net/namespace/string"
		xmlns:text="http://ictk.sourceforge.net/namespace/text"
		extension-element-prefixes="text">

<xsl:include href="string.xsl"/>
<!-- text:justify ..................................................-->
<!-- centers, left and right justifies the text.
     The code is derived from "XSLT Cookbook" by Sal Mangano
-->
<xsl:template name="text:justify">
   <xsl:param name="value" />
   <xsl:param name="width" select="10"/>
   <xsl:param name="align" select=" 'left' "/>

   <!-- truncate if too long -->
   <xsl:variable name="output" select="substring($value, 1, $width)"/>

   <xsl:choose>
      <!-- left -->
      <xsl:when test="$align = 'left'">
         <xsl:value-of select="$output"/>
	 <xsl:call-template name="str:dup">
	    <xsl:with-param name="input" select=" ' ' "/>
	    <xsl:with-param name="count"
	                    select="$width - string-length($output)"/>
	 </xsl:call-template>
      </xsl:when>

      <!-- right -->
      <xsl:when test="$align = 'right'">
	 <xsl:call-template name="str:dup">
	    <xsl:with-param name="input" select=" ' ' "/>
	    <xsl:with-param name="count"
	                    select="$width - string-length($output)"/>
	 </xsl:call-template>
         <xsl:value-of select="$output"/>
      </xsl:when>

      <!-- center -->
      <xsl:when test="$align = 'center'">
	 <xsl:call-template name="str:dup">
	    <xsl:with-param name="input" select=" ' ' "/>
	    <xsl:with-param name="count"
	         select="floor(($width - string-length($output)) div 2)"/>
	 </xsl:call-template>
         <xsl:value-of select="$output"/>
	 <xsl:call-template name="str:dup">
	    <xsl:with-param name="input" select=" ' ' "/>
	    <xsl:with-param name="count"
	         select="ceiling(($width - string-length($output)) div 2)"/>
	 </xsl:call-template>
      </xsl:when>

      <xsl:otherwise>INVALID ALIGN</xsl:otherwise>
   </xsl:choose>
</xsl:template>

<!-- text:regex-format .............................................-->
<xsl:template name="text:regex-format">
   <xsl:param name="input" select="normalize-space()"/>
   <xsl:param name="indent" select="0"/>
   <xsl:param name="cat_oper" select="'+'"/>
   <xsl:param name="skip_first" select="'no'"/>
   

   <xsl:variable name="line" select="normalize-space($input)"/>

   <!-- how many spaces to indent the comment? -->
   <xsl:variable name="spaces">
      <xsl:call-template name="str:dup">
         <xsl:with-param name="input" select="' '"/>
	 <xsl:with-param name="count" select="$indent"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="prefix">
      <xsl:value-of select="$spaces"/>
      <xsl:text>+ "</xsl:text>
   </xsl:variable>

   <xsl:variable name="postfix">
      <xsl:text>"</xsl:text>
      <xsl:text>&#xa;</xsl:text>
   </xsl:variable>

   <!-- do we skip the beginning $cat_oper? -->
   <xsl:choose>
      <xsl:when test="$skip_first = 'no'">
         <xsl:value-of select="$prefix"/>
      </xsl:when>

      <xsl:otherwise>
         <xsl:value-of select="$spaces"/>
         <xsl:text>  "</xsl:text>
      </xsl:otherwise>
   </xsl:choose>

   <xsl:choose>

      <xsl:when test="contains($line, ' ')">
         <xsl:value-of select="substring-before($line, ' ')"/>
         <xsl:value-of select="$postfix"/>
	 <xsl:call-template name="_mini_format">
	    <xsl:with-param name="input" select="substring-after($line, ' ')"/>
	    <xsl:with-param name="prefix" select="$prefix"/>
	    <xsl:with-param name="postfix" select="$postfix"/>
	 </xsl:call-template>
      </xsl:when>

      <xsl:otherwise>
         <xsl:value-of select="$line"/>
         <xsl:value-of select="$postfix"/>
      </xsl:otherwise>
   </xsl:choose>

</xsl:template>

<xsl:template name="_mini_format">
   <xsl:param name="input"/>
   <xsl:param name="prefix"/>
   <xsl:param name="postfix"/>

   <xsl:if test="$input">
      <xsl:value-of select="$prefix"/>
      <xsl:choose>
	 <xsl:when test="substring-before($input, ' ')">
	    <xsl:value-of select="substring-before($input, ' ')"/>
	 </xsl:when>
	 <xsl:otherwise>
	    <xsl:value-of select="$input"/>
	 </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$postfix"/>
	 <xsl:call-template name="_mini_format">
	    <xsl:with-param name="input" select="substring-after($input, ' ')"/>
	    <xsl:with-param name="prefix" select="$prefix"/>
	    <xsl:with-param name="postfix" select="$postfix"/>
	 </xsl:call-template>
   </xsl:if>
</xsl:template>


<!-- text:wrap .....................................................-->
<!-- wraps text to a specified length and justifies the text.
     The code is derived from "XSLT Cookbook" by Sal Mangano
-->
<xsl:template match="node() | @*" mode="text:wrap" name="text:wrap">
   <xsl:param name="input" select="normalize-space()"/>
   <xsl:param name="width" select="70"/>
   <xsl:param name="align-width" select="$width"/>
   <xsl:param name="align" select=" 'left' "/>

   <xsl:if test="$input">
      <xsl:variable name="line">
         <xsl:choose>
	    <xsl:when test="string-length($input) > $width">
	       <xsl:call-template name="str:substring-before-last">
	          <xsl:with-param name="input"
		                  select="substring($input, 1, $width)"/>
		  <xsl:with-param name="substr" select=" ' ' "/>
	       </xsl:call-template>
	    </xsl:when>
	    <xsl:otherwise>
	       <xsl:value-of select="$input"/>
	    </xsl:otherwise>
	 </xsl:choose>
      </xsl:variable>

      <!-- justify the text -->
      <xsl:if test="$line">
         <xsl:call-template name="text:justify">
	    <xsl:with-param name="value" select="$line"/>
	    <xsl:with-param name="width" select="$align-width"/>
	    <xsl:with-param name="align" select="$align"/>
	 </xsl:call-template>
         <xsl:text>&#xa;</xsl:text>
      </xsl:if>

      <!-- recurse -->
      <xsl:call-template name="text:wrap">
         <xsl:with-param name="input"
	                 select="substring($input, string-length($line) + 2)"/>
	 <xsl:with-param name="width" select="$width"/>
	 <xsl:with-param name="align-width" select="$align-width"/>
	 <xsl:with-param name="align" select="$align"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<!-- text:comment-wrap ...............................................-->
<!-- comment-wrap takes a chunk of text and line-wraps it and prefixes
     each line with a coding style of comment that can be indented a 
     number of spaces.  The width will be re-calculated to account for
     the prefix and spaces.
-->
<xsl:template match="node() | @*" mode="text:comment-wrap" 
                                  name="text:comment-wrap">
   <xsl:param name="input" select="normalize-space()"/>
   <xsl:param name="width" select="70"/>
   <xsl:param name="comment-style" select=" 'C' "/>
   <xsl:param name="indent" select="0"/>

   <!-- how many spaces to indent the comment? -->
   <xsl:variable name="spaces">
      <xsl:call-template name="str:dup">
         <xsl:with-param name="input" select="' '"/>
	 <xsl:with-param name="count" select="$indent"/>
      </xsl:call-template>
   </xsl:variable>

   <xsl:variable name="prefix">
      <xsl:value-of select="$spaces"/>
      <xsl:choose>

         <xsl:when test="$comment-style = 'C'">
	    <xsl:text>* </xsl:text>
	 </xsl:when>

         <xsl:when test="$comment-style = 'C++'">
	    <xsl:text>// </xsl:text>
	 </xsl:when>

         <xsl:when test="$comment-style = 'perl'">
	    <xsl:text># </xsl:text>
	 </xsl:when>

	 <xsl:otherwise>INVALID COMMENT-STYLE</xsl:otherwise>
      </xsl:choose>
   </xsl:variable>

   <xsl:value-of select="$prefix"/>
   <xsl:call-template name="str:search-and-replace">
      
      <!-- the wrapped text w/o the last \n -->
      <xsl:with-param name="input">
         <xsl:call-template name="str:substring-before-last">
	    <xsl:with-param name="input">
	       <xsl:call-template name="text:wrap">
		  <xsl:with-param name="input"
				  select="$input"/>
		  <xsl:with-param name="width" 
				  select="($width - string-length($prefix))"/>
	       </xsl:call-template>
	    </xsl:with-param>
	    <xsl:with-param name="substr" select="'&#xa;'"/>
	 </xsl:call-template>
      </xsl:with-param>

      <!-- search for \n -->
      <xsl:with-param name="search"
                      select="'&#xa;'"/>

      <!-- replace it with our comment prefix -->
      <xsl:with-param name="replace"
                      select="concat('&#xa;', $prefix)"/>
   </xsl:call-template>

</xsl:template>
</xsl:stylesheet>
