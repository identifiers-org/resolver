<%--
  @author Camille Laibe
  @version 20070109
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: displays the complete information about a precise data-type
  	
	TODO:
	- add documentation links
--%><?xml version="1.0" encoding="UTF-8" ?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mir" uri="MiriamCustomTags" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<%-- EBI headers --%>
		<c:import url="${initParam.www}ebi_headers.html" charEncoding="UTF-8" />
		
		<%-- Style sheet to copy the design of the Biomodels site (content) --%>
		<link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/Miriam_Biomodels.css" />
		<%-- Style sheet to copy the design of the EBI site (banner and menus) --%>
		<link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/Miriam_EBI.css" />
		<%-- Style sheet for the demo of the web services part --%>
		<link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/Miriam_WS.css" />
		
		<title>Miriam Resources</title>
		
		<script type="text/javascript">
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
    </script>
	</head>
	
	<body>
		<%-- EBI top menus --%>
		<c:import url="${initParam.www}ebi_top_menus.html" charEncoding="UTF-8" />
	
		<h1 class="ebi_h1"><a href="mdb" title="Miriam" class="ebi_h1_link">MIRIAM ALPHA</a></h1>
		
		<%-- Compneur group menu --%>
		<div id="ebi_group_menu">
			<c:import url="${initParam.www}compneur_menu.html" charEncoding="UTF-8" />
		</div>   <!-- id="ebi_group_menu" -->
		
		<div id="content">
		
			<c:if test="${sessionScope.login != null}">
				<div id="loginfo">
					Login: <em>${sessionScope.login}</em> [<a href="signOut" title="Sign Out">Sign Out</a>]
				</div>
			</c:if>
			
			<h2><span class="box_green">Data-type</span>&nbsp;${data.name}
				&nbsp;<a href="mdb?section=edit&amp;data=${data.id}" title="Edit the following data-type: ${data.name}"><img src="${initParam.www}img/Edit.gif" alt="Edit icon" align="bottom" /></a>
			</h2>
			
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
		                      	<li>
		                        	<img src="${initParam.www}img/plus.gif" alt="Display more (or less) resources" title="Display more resources" onclick="displayResources('res${id}')" class="action_button" id="res${id}" name="res${id}" />&nbsp;<a href="${url}" title="${info}: ${institution}, ${location}">${url}</a>
		                        </li>
														<mir:docBrowseTwo urls="${urlList}" infos="${infoList}" institutions="${institutionList}" locations="${locationList}">
															<li class="optional res${id}">
																<a href="${url2}" title="${info2}: ${institution2}, ${location2}">${url2}</a>
															</li>
														</mir:docBrowseTwo>
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
			</table>
			
			<p>
				<a href="mdb?section=browse" title="Return to the list of data-types"><img src="${initParam.www}img/prev.png" alt="Previous arrow icon" align="top" />&nbsp;Go back</a>
			</p>
			
			<%-- Compneur group menu --%>
			<div id="bottom_page">
				<c:import url="${initParam.www}compneur_bottom.html" charEncoding="UTF-8" />
			</div>   <!-- id="bottom_page" -->
		
		</div>   <!-- id="content" -->
	
	</body>
</html>
