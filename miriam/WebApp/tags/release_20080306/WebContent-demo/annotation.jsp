<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20080219
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: displays the extra information about a precise data-type
  	(annotation using a specific format, like SBML, CellML or BioPAX)  
  
--%><?xml version="1.0" encoding="UTF-8" ?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>


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
							<c:import url="${initParam.www}iframe_leftmenu_compneur.html" charEncoding="UTF-8" />
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
					
					<c:if test="${sessionScope.login != null}">
						<div id="loginfo">
							Login: <em>${sessionScope.login}</em> [<a href="signOut" title="Sign Out">Sign Out</a>]
						</div>
					</c:if>
					
					<c:choose>
						<%-- the data type exist --%>
						<c:when test="${data != null}">
							
							<h2>Data type: <em>${data.name}</em>&nbsp;<a href="mdb?section=edit&amp;data=${data.id}" title="Edit the following data type: ${data.name}"><img src="${initParam.www}img/Edit.gif" alt="Edit icon" align="bottom" /></a></h2>
							
							<c:choose>
								<%-- no annotation stored  --%>
								<c:when test="${(data.annotation == null) || (empty data.annotation)}">
									<p>
										Sorry, no possible annotation is stored for this data type.
									</p>
									<p>
										You can contribute to MIRIAM Resources by adding more annotation through the 'submit' form. Thank you.
									</p>
								</c:when>
								
								<%-- some annotation exist --%>
								<c:otherwise>
									<p>
										Here are possible annotations using this data type. These are classified by format (like <a href="http://sbml.org/" title="SBML">SBML</a>, <a href="http://www.cellml.org/" title="CellML">CellML</a> or <a href="http://www.biopax.org/" title="BioPAX">BioPAX</a>) and show the element(s) (tags for XML based formats) which can have a MIRIAM URI -using the current data type- to bring extra knowledge about the entity described.
									</p>
									<br />
									
									<c:forEach var="anno" items="${data.annotation}">
										<h3>${anno.format}</h3>
										
										<ul>
											<c:forEach var="tag" items="${anno.tags}">
												<li><b>${tag.name}</b> (${tag.info})</li>
											</c:forEach>
										</ul>
									</c:forEach>
								</c:otherwise>
							</c:choose>
							
							<br />
							<p>
								Annotation information for <em>MIRIAM Resources</em> purpose only. For complete details about an element of a format, please always refer to the official specifications.
							</p>
							
							<br />
							
							<p>
								<a href="mdb?section=browse&amp;data=${data.id}" title="Return to the main page of ${data.id}"><img src="${initParam.www}img/prev.png" alt="Previous arrow icon" align="top" />&nbsp;Go back to ${data.name}</a>
							</p>
							
						</c:when>
						
						<%-- the data type doesn't exist, SHOULD NEVER OCCURED: redirection towards the introduction page --%>
						<c:otherwise>
							<p>
								Sorry, this data type doesn't exist in the database...
							</p>
						</c:otherwise>
					</c:choose>
					
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
