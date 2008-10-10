<%--
  @author Camille Laibe
  @version 20070109
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: diplay all the information about a new data-type (in order to submit it)
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
			
			<%--<c:set var="nameUTF8" scope="page" value="${name}" charEncoding="UTF-8" /> --%>
			
			<c:choose>
				<c:when test="${actionType == 'add'}">
					<h2><span class="box_green">New data-type</span> ${data.name}</h2>
				</c:when>
				<c:when test="${actionType == 'edit'}">
					<h2><span class="box_green">Updated data-type</span> ${data.name}</h2>
				</c:when>
			</c:choose>
			
			<c:choose>
				<c:when test="${actionType == 'edit'}">
					<c:choose>
						<c:when test="${(empty data.name) || (empty data.URL && empty data.URN) || (empty data.definition) || (empty data.regexp) || (empty data.resources)}">
							<p class="warning">
								One or more mandatory field(s) are not filled! These ones are shown in red colour. Please, re-edit the data-type taking care of the mandatory fields.
							</p>
						</c:when>
						<c:otherwise>
							<p>
								<c:choose>
									<c:when test="${sessionScope.login != null}">
										The data-type has been successfully updated in the database!
									</c:when>
									<c:otherwise>
										An email has been sent to the administrator to ask him to update the data-type in the database.
									</c:otherwise>
								</c:choose>
								We thank you for taking part in the development of Miriam Resources.
							</p>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:when test="${actionType == 'add'}">
					<c:choose>
						<c:when test="${(empty data.name) || (empty data.URL && empty data.URN) || (empty data.definition) || (empty data.regexp) || (empty data.resources)}">
							<p class="warning">
								One or more mandatory field(s) are not filled! These ones are shown in red colour. Please, resubmit your data-type taking care of the mandatory fields.
							</p>
						</c:when>
						<c:otherwise>
							<p>
							<c:choose>
								<c:when test="${sessionScope.login != null}">
									The data-type has been successfully added to the database! 
								</c:when>
								<c:otherwise>
									An email has been sent to the administrator to ask him to add your new data-type in the database. 
								</c:otherwise>
							</c:choose>
							We thank you for taking part in the development of Miriam Resources.
							</p>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<%-- nothing for the moment --%>
				</c:otherwise>
			</c:choose>
			
			<h3><span class="h3_right">Data-type summary</span></h3>
			
			<table class="datatyperesult" summary="Information about the new data-type">
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
			
			<%-- Compneur group menu --%>
			<div id="bottom_page">
				<c:import url="${initParam.www}compneur_bottom.html" charEncoding="UTF-8" />
			</div>   <!-- id="bottom_page" -->
		
		</div>   <!-- id="content" -->
	
	</body>
</html>
