<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20080219
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: displays the complete information about a precise data-type
  	
	TODO:
	- add documentation links
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
	
	<script type="text/javascript" language="javascript">
	//<![CDATA[
	// Show more or less resource links
	function displayResources(res)
	{
	  var str = "document." + res;
	  var classStr = "optional " + res;
	
	  for (i=0; i<document.getElementsByTagName('li').length; i++)
	  {
	    if (document.getElementsByTagName('li')[i].className == classStr)
	    {
	      if (document.getElementsByTagName('li')[i].style.display == "block")
	      {
	        document.getElementsByTagName('li')[i].style.display = "none";
	        (eval(str)).title = "Display more resources";
	        (eval(str)).src = "${initParam.www}img/plus.gif";
	      }
	      else
	      {
	        document.getElementsByTagName('li')[i].style.display = "block";
	        (eval(str)).title = "Display only one resource";
	        (eval(str)).src = "${initParam.www}img/minus.gif";
	      }
	    }
	  }
	}
	//]]>
	</script>
	
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
					
					<h2>Data type: <em>${data.name}</em>&nbsp;<a href="mdb?section=edit&amp;data=${data.id}" title="Edit the following data type: ${data.name}"><img src="${initParam.www}img/Edit.gif" alt="Edit icon" align="bottom" /></a></h2>
					
					<table class="datatyperesult" summary="data resource information">
						<tr>
							<th colspan="3">Name</th>
						</tr>
						<tr>
							<td class="desc" colspan="2">Identifier</td><td class="element">${data.id}</td>
						</tr>
						<tr>
							<td class="desc" colspan="2">Name</td><td class="element">${data.name}</td>
						</tr>
						<tr>
						<c:choose>
							<c:when test="${empty data.synonyms}">
								<td class="desc" colspan="2">Synonyms</td>
								<td>
									<table class="nothing" summary="synonyms of the data type">
										<tr>
											<td class="element"><i>No synonym</i></td>
										</tr>
									</table>
								</td>
							</c:when>
							<c:otherwise>
								<td class="desc" colspan="2">Synonyms</td><td><table class="nothing" summary="synonyms of the data type"><c:forEach var="synonym" items="${data.synonyms}"><tr><td class="element">${synonym}</td></tr></c:forEach></table></td>
							</c:otherwise>
						</c:choose>
						</tr>
						<tr>
							<th colspan="3">URIs</th>
						</tr>
						<tr>
							<td class="desc" colspan="2">Official URL</td><td class="element">${data.URL}</td>
						</tr>
						<tr>
							<td class="desc" colspan="2">Official URN</td><td class="element">${data.URN}</td>
						</tr>
						<tr>
						<c:choose>
							<c:when test="${empty data.deprecatedURIs}">
								<td class="desc" colspan="2">Deprecated</td>
								<td>
									<table class="nothing" summary="deprecated URI(s)">
										<tr>
											<td class="element"><i>No deprecated URI</i></td>
										</tr>
									</table>
								</td>
							</c:when>
							<c:otherwise>
								<td class="desc" colspan="2">Deprecated</td>
								<td>
									<table class="nothing" summary="deprecated URI(s)">
										<c:forEach var="deprecated" items="${data.deprecatedURIs}">
											<tr>
												<td class="element">${deprecated}</td>
											</tr>
										</c:forEach>
									</table>
								</td>
							</c:otherwise>
						</c:choose>
						</tr>
						<tr>
							<th colspan="3">Information</th>
						</tr>
						<tr>
							<td class="desc" colspan="2">Definition</td><td class="element">${data.definition}</td>
						</tr>
						<tr>
							<td class="desc" colspan="2">Identifier Pattern</td><td class="element">${data.regexp}</td>
						</tr>
						<tr>
							<th colspan="3">Physical Locations</th>
						</tr>
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
						<tr>
							<th colspan="3">Documentation</th>
						</tr>
						<tr>
							<td class="desc" colspan="2">URL(s)</td>
							<td>
								<table class="nothing" summary="Physical URL of the documentation of the data type">
									<c:if test="${empty data.docHtmlURLs}">
										<tr>
											<td class="element"><i>No URL or ID of a documentation</i></td>
										</tr>
									</c:if>
									<c:forEach var="docs_url" items="${data.documentationURLs}">
										<tr>
											<td class="element"><a href="${docs_url}" title="External link to: ${docs_url}">${docs_url}</a></td>
										</tr>
									</c:forEach>
									<tr>
										<td>
											<mir:docBrowse data="${data.documentationIDs}" pool="${initParam.miriam_db_pool}">
												<table class="simpleTableBottom" summary="Links towards documentation">
													<tr>
														<td class="subElement">
															<ul class="noPoint">
																<c:choose>
																	<c:when test="${empty urlList}">
																		<li>
																			<a href="${url}" title="${info}: ${institution}, ${location}">${url}</a>
																		</li>
																	</c:when>
																	<c:otherwise>
																		<li>
																			<img src="${initParam.www}img/plus.gif" alt="Display more (or less) resources" title="Display more resources" onclick="displayResources('res${id}')" class="action_button" id="res${id}" name="res${id}" />&nbsp;<a href="${url}" title="${info}: ${institution}, ${location}">${url}</a>
																		</li>
																		<mir:docBrowseTwo urls="${urlList}" infos="${infoList}" institutions="${institutionList}" locations="${locationList}">
																			<li class="optional res${id}">
																				<a href="${url2}" title="${info2}: ${institution2}, ${location2}">${url2}</a>
																			</li>
																		</mir:docBrowseTwo>
																	</c:otherwise>
																</c:choose>
															</ul>
														</td>
													</tr>
												</table>
											</mir:docBrowse>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<th colspan="3">Miscellaneous</th>
						</tr>
						<tr>
							<td class="desc" colspan="2">Date of creation</td><td class="element">${data.dateCreationStr}</td>
						</tr>
						<tr>
							<td class="desc" colspan="2">Date of last modification</td><td class="element">${data.dateModificationStr}</td>
						</tr>
					</table>
					
					<table width="100%" summary="table bottom links">
						<tr>
							<td align="left">
								<a href="mdb?section=browse" title="Return to the list of data types"><img src="${initParam.www}img/prev.png" alt="Previous arrow icon" align="top" />&nbsp;Go back</a>
							</td>
							<td align="right">
								<a href="mdb?section=annotation&amp;data=${data.id}" title="Go to the examples of annotations using this data type"><img src="${initParam.www}img/next.png" alt="Next arrow icon" align="top" />&nbsp;Examples of annotations using this data type</a>
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
