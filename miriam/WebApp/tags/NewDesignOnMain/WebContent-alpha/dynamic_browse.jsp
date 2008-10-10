<%--
	@author Camille Laibe
  @version 20070109
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: displays the overview of the content of the database (browse)
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
			
			<h2><span class="box_green">Browse</span> data-types</h2>
			
			<p>
				Brief overview of the different data-types stored in <em>Miriam Resource</em>.
			</p>
			
			<form method="post" action="deleteDataType">
			
			<table class="sqlresult" summary="data types overview">
				<tr>
					<c:if test="${sessionScope.role == 'admi'}">
						<th class="action"><img src="${initParam.www}img/Delete.gif" alt="delete icon" title="Deletes data-type(s)" height="16" width="16" /></th>
					</c:if>
					<th>Name</th><th>URI</th><th>Definition</th>
				</tr>
				
				<mir:arrayBrowse data="${data}">
					<tr class="${class}">
						<c:if test="${sessionScope.role == 'admi'}">
							<td class="${class}">
								<input name="datatype2remove" type="checkbox" value="${id}" />
							</td>
						</c:if>
						<td class="${class}"><a href="mdb?section=browse&amp;data=${id}" title="Access to the complete information about the data-type: ${name}">${name}</a></td><td class="${class}">${uri}</td><td class="${class}">${definition}</td>
					</tr>
				</mir:arrayBrowse>
			</table>
			
			<c:if test="${sessionScope.role == 'admi'}">
				<p>
					<input type="submit" value="Delete selected data-type(s)" />
					<input type="reset" value="Reset" />
				</p>
			</c:if>
			
			</form>
			
			<%-- Compneur group menu --%>
			<div id="bottom_page">
				<c:import url="${initParam.www}compneur_bottom.html" charEncoding="UTF-8" />
			</div>   <!-- id="bottom_page" -->
		
		</div>   <!-- id="content" -->
	
	</body>
</html>
