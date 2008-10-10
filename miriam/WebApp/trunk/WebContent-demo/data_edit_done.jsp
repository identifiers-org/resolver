<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20080611
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: diplay all the information about a new data type (in order to submit it)
--%><?xml version="1.0" encoding="UTF-8" ?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <meta name="description" content="European Bioinformatics Institute - Computational Neurobiology" />
  <meta name="author" content="Camille Laibe and Nicolas Le Novère" />
  <meta http-equiv="Content-Language" content="en-GB" />
  <meta http-equiv="Window-target" content="_top" />
  <meta name="no-email-collection" content="http://www.unspam.com/noemailcollection/" />
  <!-- ===================================== -->
  <!-- TemplateBeginEditable name="doctitle" -->
  <!-- ===================================== -->

  <title>MIRIAM Resources</title>
  <!-- ===================================== -->
  <!-- TemplateEndEditable -->
  <!-- ===================================== -->
  <link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/contents.css"     type="text/css" />
  <link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/userstyles.css"   type="text/css" />
  <script src="http://www.ebi.ac.uk/inc/js/contents.js" type="text/javascript"></script>
  <link rel="stylesheet"  href="http://www.ebi.ac.uk/inc/css/sidebars.css"   type="text/css" />
  <link rel="SHORTCUT ICON" href="http://www.ebi.ac.uk/bookmark.ico" />
  <!-- ===================================== -->
  <!-- TemplateBeginEditable name="head" -->
  <!-- ===================================== -->
  <!-- start meta tags, css , javascript here -->
  <!-- ===================================== -->

  <meta name="keywords" content="Nicolas Le Novère, Camille Laibe, EBI, EMBL, bioinformatics, software, databases, genomics, computational neurobiology, neuroinformatics, systems biology" />
  <link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/MIRIAM.css" />
  <link rel="alternate" type="application/rss+xml" title="MIRIAM News Feed" href="${initParam.www}rss/MiriamNews.xml" />

  <!-- ===================================== -->
  <!-- end meta tags, css , javascript here -->
  <!-- ===================================== -->
  <!-- TemplateEndEditable -->
  <!-- ===================================== -->

</head>

<!-- WHAT IS THIS SHIT? -->
<body onload="if(navigator.userAgent.indexOf('MSIE') != -1) {document.getElementById('head').allowTransparency = true;}">
  <!-- ===================================== -->
  <div class="headerdiv" id="headerdiv" style="position: absolute; z-index: 1;">
    <iframe src="/inc/head.html" name="head" id="head" marginwidth="0" marginheight="0" style="position: absolute; z-index: 1; height: 57px;" frameborder="0" scrolling="no" width="100%"></iframe>
  </div>
  <!-- ===================================== -->


  <div class="contents" id="contents">
    <table class="contentspane" id="contentspane" summary="The main content pane of the page" style="width: 100%">
      <tr>
        <td class="leftmargin">
          <img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" />
        </td>
        <!-- ===================================== -->

        <td class="leftmenucell" id="leftmenucell">
          <div class="leftmenu" id="leftmenu" style="width: 145px; visibility: visible; display: block;">
            <!-- InstanceBeginEditable name="leftnav" -->

            <!-- start left menu here  -->
            <div id="leftmenu2">
            	<mir:menuSelect user="${sessionScope.role}">
                    <c:import url="${initParam.www}${menu}.html" charEncoding="UTF-8" />
                </mir:menuSelect>
            </div>
            <!-- end left menu here -->

            <!-- ===================================== -->
            <!-- TemplateEndEditable -->
            <script type="text/javascript" src="http://www.ebi.ac.uk/inc/js/sidebars.js"></script>
            <img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" />
          </div>
        </td>
        <!-- TemplateBeginEditable name="contents" -->
        <!-- ===================================== -->
        <!-- start contents here -->
        <!-- ===================================== -->

        <td class="contentsarea" id="contentsarea">
          <div class="breadcrumbs">
            <a href="http://www.ebi.ac.uk/" class="firstbreadcrumb">EBI</a>
            <a href="http://www.ebi.ac.uk/Groups/">Groups</a>
            <a href="http://www.ebi.ac.uk/compneur/">Computational Neurobiology</a>
            <a href="http://www.ebi.ac.uk/compneur/research.html">Research</a>
            <a href="http://www.ebi.ac.uk/compneur-srv/miriam/">MIRIAM</a>
          </div>
          
          <h1>MIRIAM - Demo</h1>
				
					<%-- displays the login box (if a user is logged) --%>
                    <m:loginBox login="${sessionScope.login}" />
					
					<%--<c:set var="nameUTF8" scope="page" value="${name}" charEncoding="UTF-8" /> --%>
					
					<c:choose>
						<c:when test="${actionType == 'add'}">
							<h2>New data type: <em>${data.name}</em></h2>
						</c:when>
						<c:when test="${actionType == 'edit'}">
							<h2>Updated data type: <em>${data.name}</em></h2>
						</c:when>
					</c:choose>
					
					<c:choose>
						<c:when test="${actionType == 'edit'}">
							<c:choose>
								<c:when test="${(empty data.name) || (empty data.URL && empty data.URN) || (empty data.definition) || (empty data.regexp) || (empty data.resources)}">
									<p class="warning">
										One or more mandatory field(s) are not filled! These ones are shown in red colour. Please, re-edit the data type taking care of the mandatory fields.
									</p>
								</c:when>
								<c:otherwise>
									<p>
										<c:choose>
											<c:when test="${sessionScope.login != null}">
												The data type has been successfully updated in the database!
											</c:when>
											<c:otherwise>
												The data type is now pending. As soon as a curator has checked it correctness, it will be available on the public Website.
											</c:otherwise>
										</c:choose>
										We thank you for taking part in the development of MIRIAM Resources.
									</p>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:when test="${actionType == 'add'}">
							<c:choose>
								<c:when test="${(empty data.name) || (empty data.URL && empty data.URN) || (empty data.definition) || (empty data.regexp) || (empty data.resources)}">
									<p class="warning">
										One or more mandatory field(s) are not filled! These ones are shown in red colour. Please, resubmit your data type taking care of the mandatory fields.
									</p>
								</c:when>
								<c:otherwise>
									<p>
									<c:choose>
										<c:when test="${sessionScope.login != null}">
											The data type has been successfully added to the database! 
										</c:when>
										<c:otherwise>
											Your submission has been recorded. A curator will now verify if it complies with the terms and conditions of MIRIAM Resources in order to add it to the public Website. 
										</c:otherwise>
									</c:choose>
									We thank you for taking part in the development of MIRIAM Resources.
									</p>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<%-- nothing for the moment --%>
						</c:otherwise>
					</c:choose>
					
					
					<table class="datatyperesult" summary="Information about the new data type">
						<tr>
							<th colspan="3">Name</th>
						</tr>
						<tr>
							<td class="desc" colspan="2">Identifier</td><td class="element">${data.id}</td>
						</tr>
						<tr>
							<c:choose>
								<c:when test="${empty data.name}">
									<td class="desc" colspan="2">Offical</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</c:when>
								<c:otherwise>
									<td class="desc" colspan="2">Offical</td><td class="element">${data.name}</td>
								</c:otherwise>
							</c:choose>
						</tr>
						<tr>
							<td class="desc" colspan="2">Synonyms</td>
							<td>
								<table class="nothing" summary="synonyms of the data type">
									<c:choose>
										<c:when test="${empty data.synonyms}">
											<tr>
												<td class="element"><i>No synonym</i></td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:forEach var="synonym" items="${data.synonyms}"><tr><td class="element">${synonym}</td></tr></c:forEach>
										</c:otherwise>
									</c:choose>
								</table>
							</td>
						</tr>
						<tr>
							<c:choose>
								<c:when test="${empty data.URN && empty data.URL}">
									<td class="desc" colspan="2">Official URL</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</c:when>
								<c:otherwise>
									<td class="desc" colspan="2">Official URL</td><td class="element">${data.URL}</td>
								</c:otherwise>
							</c:choose>
						</tr>
						<tr>
							<c:choose>
								<c:when test="${empty data.URL && empty data.URN}">
									<td class="desc" colspan="2">Official URN</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</c:when>
								<c:otherwise>
									<td class="desc" colspan="2">Official URN</td><td class="element">${data.URN}</td>
								</c:otherwise>
							</c:choose>
						</tr>
						<tr>
							<td class="desc" colspan="2">Deprecated</td>
							<td>
								<table class="nothing" summary="deprecated URI(s)">
									<c:choose>
										<c:when test="${empty data.deprecatedURIs}">
											<tr>
												<td class="element"><i>No deprecated URI</i></td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:forEach var="deprecated" items="${data.deprecatedURIs}"><tr><td class="element">${deprecated}</td></tr></c:forEach>
										</c:otherwise>
									</c:choose>
								</table>
							</td>
						</tr>
						<tr>
							<th colspan="3">Information</th>
						</tr>
						<tr>
							<c:choose>
								<c:when test="${empty data.definition}">
									<td class="desc" colspan="2">Definition</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</c:when>
								<c:otherwise>
									<td class="desc" colspan="2">Definition</td><td class="element">${data.definition}</td>
								</c:otherwise>
							</c:choose>
						</tr>
						<tr>
							<c:choose>
								<c:when test="${empty data.regexp}">
									<td class="desc" colspan="2">Identifier Pattern</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</c:when>
								<c:otherwise>
									<td class="desc" colspan="2">Identifier Pattern</td><td class="element">${data.regexp}</td>
								</c:otherwise>
							</c:choose>
						</tr>
						<tr>
							<th colspan="3">Physical Locations</th>
						</tr>
						<c:choose>
							<c:when test="${empty data.resources}">
								<tr>
									<td class="desc2" rowspan="4">Resource #1</td><td class="desc2">Data Entry</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</tr>
								<tr>
									<td class="desc2">Data Resource</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</tr>
								<tr>
									<td class="desc2">Information</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</tr>
								<tr>
									<td class="desc2">Institution</td><td class="element"><span class="warning">NOT FILLED!</span></td>
								</tr>
							</c:when>
							<c:otherwise>
								<mir:resourcesBrowse data="${data.resources}">
									<tr>
										<td class="desc2" rowspan="4">
											<acronym class="idInfo" title="Stable identifier or this resource: ${resourceId}">Resource #${id}</acronym>
											<c:if test="${obsolete == '1'}">
												&nbsp;<img src="${initParam.www}img/Warning.gif" title="WARNING: this resource is obsolete!" alt="Resource obsolete!" align="bottom" />
											</c:if>
										</td>
										<td class="desc2">Data Entry</td>
										<td class="element">${prefix}<b><acronym class="idInfo" title="This identifier follows the pattern: ${data.regexp}">$id</acronym></b>${suffix}</td>
									</tr>
									<tr>
										<td class="desc2">Data Resource</td><td class="element"><a href="${base}" title="External link to: ${base}">${base}</a></td>
									</tr>
									<tr>
										<td class="desc2">Information</td><td class="element">${info}</td>
									</tr>
									<tr>
										<td class="desc2">Institution</td><td class="element">${institution}, ${location}</td>
									</tr>
								</mir:resourcesBrowse>
							</c:otherwise>
						</c:choose>
						<tr>
							<th colspan="3">Documentation</th>
						</tr>
						<tr>
							<td class="desc" colspan="2">
								URI(s)
							</td>
							<td>
								<table class="nothing" summary="Physical URIs of the documentation of the data type">
									<c:if test="${empty data.documentationIDs}">
										<tr><td class="element"><i>No ID of a documentation</i></td></tr>
									</c:if>
									<c:forEach var="docs_id" items="${data.documentationIDs}">
										<tr><td class="element">${docs_id}</td></tr>
									</c:forEach>
								</table>
							</td>
						</tr>
						<tr>
							<td class="desc" colspan="2">
								URL(s)
							</td>
							<td>
								<table class="nothing" summary="Physical URLs of the documentation of the data type">
									<c:if test="${empty data.documentationURLs}">
										<tr><td class="element"><i>No URL of a documentation</i></td></tr>
									</c:if>
									<c:forEach var="docs_url" items="${data.documentationURLs}">
										<tr><td class="element"><a href="${docs_url}" title="External link to: ${docs_url}">${docs_url}</a></td></tr>
									</c:forEach>
								</table>
							</td>
						</tr>
					</table>
					
					<!-- end contents here -->

          <!-- InstanceEndEditable -->
          <img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" />
        </td>
        <td class="rightmenucell" id="rightmenucell">
          <div class="rightmenu" id="rightmenu">
            <img src="http://www.ebi.ac.uk/inc/images/spacer.gif" class="spacer" alt="spacer" />
          </div>
        </td>
      </tr>
    </table>
    <table class="footerpane" id="footerpane" summary="The main footer pane of the page">
      <tr>
        <td colspan ="4" class="footerrow">
          <div class="footerdiv" id="footerdiv" style="z-index:2;">
            <iframe src="/inc/foot.html" name="foot" frameborder="0" marginwidth="0" marginheight="0" scrolling="no"  height="22" width="100%"  style="z-index:2;">
            </iframe>
          </div>
        </td>
      </tr>
    </table>
    <script src="http://www.ebi.ac.uk/inc/js/footer.js" type="text/javascript"></script>
  </div>
</body>
<!-- InstanceEnd -->
</html>
