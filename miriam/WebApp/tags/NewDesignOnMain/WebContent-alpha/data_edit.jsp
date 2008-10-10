<%--
  @author Camille Laibe
  @version 20070109
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: displays the complete information about a precise data-type in edit mode

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
		
		<%-- javascript functions for dynamic forms --%>
		<script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>
		
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
			
			<h2><span class="box_green">Edit</span>&nbsp;${data.name}</h2>
			
			<form method="post" id="submission_form" action="dataTypeEditPart2" onsubmit="return validate_submission();">
				
				<fieldset id="form_name_synonyms">
					<legend>Name and synonyms&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpNames')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<table summary="Name and synonyms information">
						<tr>
							<td>
								ID:&nbsp;
							</td>
							<td>
								<input type="text" name="id" size="45" id="id" value="${data.id}" style="background-color: #C5CED5;" onfocus="this.blur();" />
							</td>
						</tr>
						<tr>
							<td>
								Name:&nbsp;
							</td>
							<td>
								<input type="text" name="name" size="45" id="name_id" value="${data.name}" />
							</td>
						</tr>
					</table>
					
					<div id="synonyms_id">
						<c:set var="synnonymsCounter" value="0" />
					  <c:forEach var="syn" items="${data.synonyms}" varStatus="synCount">
							<div id="synonym${synCount.count}Div">
								Synonym:&nbsp;<input name="synonym${synCount.count}" size="45" type="text" value="${syn}" /> &nbsp; <a href="javascript:;" title="Remove this synonym" onclick="removeSynonym('synonym${synCount.count}Div')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>
							</div>
							<c:set var="synnonymsCounter" value="${synCount.count}" />
						</c:forEach>
					</div>
					
					<input type="hidden" value="${synnonymsCounter}" name="synonymsCounter" id="synonymsCounter" />
					<input type="hidden" value="${synnonymsCounter}" name="synonymsCountReal" id="synonymsCounterReal" />
					<div id="add_synonym_id">
						<a href="javascript:;" title="Add a synonym" onclick="addSynonym();">[Add a synonym]</a>
					</div>
				</fieldset>
				
				<div class="help_submission" id="HelpNames" style="display: none;">
					<p>
						The field "Primary name" is mandatory and must be the official name of the data-type (for example: "Gene Ontology" or "Uniprot").
					</p>
					<p>
						You can add several synonyms to the official name. That can be useful if the name is an acronym or can be summarised with an acronym (for example: "Gene Ontology" and "GO").
					</p>
				</div>
				
				<fieldset id="form_def_pattern">
					<legend>Definition and pattern&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpDef')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<table summary="Definition and pattern information">
						<tr>
							<td>
								Definition:&nbsp;
							</td>
							<td>
								<textarea cols="50" rows="2" name="def" id="def_id">${data.definition}</textarea>
							</td>
						</tr>
						<tr>
							<td>
								Identifier pattern:&nbsp;
							</td>
							<td>
								<textarea cols="50" rows="2" name="pattern" id="pattern_id">${data.regexp}</textarea>
							</td>
						</tr>
					</table>
				</fieldset>
				
				<div class="help_submission" id="HelpDef" style="display: none;">
					<p>
						The field "Definition" must be filled with one or two of sentences explaining what kind of elements are stored in the data-type.
					</p>
					<p>
						The field "Identifier pattern" is the pattern (or regular expression) of the identifiers used by the data-type (for example: "^GO:\d{7}$" for Gene Ontology).
					</p>
				</div>
				
				<fieldset id="form_uris">
					<legend>URIs&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpURIs')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<table summary="URIs information">
						<tr>
							<td>
								Official URL:&nbsp;
							</td>
							<td>
								<input type="text" name="url" size="45" id="url_id" value="${data.URL}" />
							</td>
						</tr>
						<tr>
							<td>
								Official URN:&nbsp;
							</td>
							<td>
								<input type="text" name="urn" size="45" id="urn_id" value="${data.URN}" />
							</td>
						</tr>
					</table>
					<div id="deprecated_id">
						<c:set var="deprecatedCounter" value="0" />
						<c:forEach var="deprec" items="${data.deprecatedURIs}" varStatus="deprecCount">
							<div id="deprecated${deprecCount.count}Div">
								Deprecated URI:&nbsp;<input name="deprecated${deprecCount.count}" size="45" type="text" value="${deprec}" /> &nbsp; <a href="javascript:;" title="Remove this deprecated URI" onclick="removeDeprecated('deprecated${deprecCount.count}Div')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>
							</div>
							<c:set var="deprecatedCounter" value="${deprecCount.count}" />
						</c:forEach>
					</div>
					<input type="hidden" value="${deprecatedCounter}" name="deprecatedCounter" id="deprecatedCounter" />
					<input type="hidden" value="${deprecatedCounter}" name="deprecatedCountReal" id="deprecatedCounterReal" />
					<div id="add_deprecated_id">
						<a href="javascript:;" title="Add a deprecated URI" onclick="addDeprecated();">[Add a deprecated URI]</a>
					</div>
				</fieldset>
				
				<div class="help_submission" id="HelpURIs" style="display: none;">
					<p>
						The URI is a unique string of characters (which can be a URL or a URN) used to identify the data-type (for example: "http://www.uniprot.org/" for Uniprot). There can be only one URL and one URN officials.
					</p>
					<p>
						<b>Warning:</b> the URL is not necessarily a valid physical address. It's only an identifier and it will never be used to access the data-type on Internet using a web browser!
					</p>
					<p>
						You can add several deprecated versions of the URIs, which can be URLs or URNs.
					</p>
				</div>
				
				<fieldset id="form_resources">
					<legend>Resources&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpResources')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<div id="resources_id">
						<mir:resourcesBrowse data="${data.resources}">
							<div id="resources${id}Div">
								<input type="hidden" value="${resourceId}" name="resourceId${id}" id="resourceId${id}" />
								<table summary="Resources information" class="resources">
									<tr>
										<td rowspan="3" valign="bottom">
											<b>#${id}:&nbsp;</b>
										</td>
										<td>
											Data entry:&nbsp;
										</td>
										<td>
											<input type="text" name="dataEntryPrefix${id}" size="34" id="depId${id}" value="${prefix}" />
										</td>
										<td>
											<b>$id</b>
										</td>
										<td>
											<input type="text" name="dataEntrySuffix${id}" size="15" id="desId${id}" value="${suffix}" />
										</td>
									</tr>
									<tr>
										<td>
											Data resource:&nbsp;
										</td>
										<td colspan="3">
											<input type="text" name="dataResource${id}" size="45" id="drId${id}" value="${base}" />
										</td>
									</tr>
									<tr>
										<td>
											Information:&nbsp;
										</td>
										<td colspan="3">
											<input type="text" name="information${id}" size="55" id="infoId${id}" value="${info}" />
										</td>
									</tr>
									<tr>
										<td rowspan="2" valign="top">
											<a href="javascript:;" title="Remove this resource" onclick="removeResource('resources${id}Div')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>
										</td>
										<td>
											Institution:&nbsp;
										</td>
										<td colspan="3">
											<input type="text" name="institution${id}" size="45" id="instituteId${id}" value="${institution}" />
										</td>
									</tr>
									<tr>
										<td>
											Country:&nbsp;
										</td>
										<td colspan="3">
											<input type="text" name="country${id}" size="30" id="countryId${id}" value="${location}" />
										</td>
									</tr>
								</table>
							</div>
							
							<c:if test="${end == 'true'}">
								</div>
								<input type="hidden" value="${id}" name="resourcesCounter" id="resourcesCounter" />
								<input type="hidden" value="${id}" name="resourcesCounterReal" id="resourcesCounterReal" />
							</c:if>
							
						</mir:resourcesBrowse>
					<div id="add_resources_id">
						<a href="javascript:;" title="Add a resource" onclick="addResource();">[Add a resource]</a>
					</div>
				</fieldset>
				
				<div class="help_submission" id="HelpResources" style="display: none;">
					<p>
						To access (via a web browser or a piece of software) a data-type, you need to enter, at least, one resource.
					</p>
					<p>
						The "Data entry" field is divided into three parts (one of them cannot be editable): this is the physical address used to access a precise element stored by the data-type. The field in the middle ($id) stands for the identifier of an element (it follows the pattern entered previously).
					</p>
					<p>
						The "Data resource" field must contains a physical link to the main page of the resource.
					</p>
					<p>
						The three other fields give more information about the institution managing the resource (one sentence of information, the name of the institution and the country).
					</p>
				</div>
				
				<fieldset id="form_docs">
					<legend>Documentation&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpDocumentation')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<%-- TODO: create a Tag Handler (and check if the documentationURLs contains also the transformed URIs -if this is the case: this a problem-) --%>
					<c:set var="docsCounter" value="0" />
					<div id="doc_id">
						<c:forEach var="url" items="${data.documentationURLs}" varStatus="id">
							<div class="docSubmitForm" id="docSubmitForm${id.count}">
								<div>
									<a href="javascript:;" title="Remove this documentation" onclick="removeDoc('docSubmitForm${id.count}')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;<span id="docTypeBox${id.count}">Type: MIRIAM ID: <input name="docType${id.count}" value="MIRIAM" onclick="return displayMiriamForm('docForm${id.count}', ${id.count});" type="radio" />Physical URL: <input name="docType${id.count}" value="URL" checked="checked" onclick="return displayUrlForm('docForm${id.count}', ${id.count});" type="radio" /></span>
								</div>
								<div id="docForm${id.count}" class="indentDiv">
									Address: <input name="docUri${id.count}" size="45" id="docUri${id.count}" value="${url}" type="text" />
								</div>
							</div>
							<c:set var="docsCounter" value="${id.count}" />
						</c:forEach>
						<c:forEach var="uri" items="${data.documentationIDs}" varStatus="uriCount">
							<c:set var="id" value="${docsCounter + uriCount.count}" />
								<div class="docSubmitForm" id="docSubmitForm${id}">
									<div>
										<a href="javascript:;" title="Remove this documentation" onclick="removeDoc('docSubmitForm${id}')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;<span id="docTypeBox${id}">Type: MIRIAM ID: <input name="docType${id}" value="MIRIAM" checked="checked" onclick="return displayMiriamForm('docForm${id}', ${id});" type="radio" />Physical URL: <input name="docType${id}" value="URL" onclick="return displayUrlForm('docForm${id}', ${id});" type="radio" /></span>
									</div>
									<div id="docForm${id}" class="indentDiv">
										ID: <input name="docSuffixId${id}" size="18" id="docSuffixId${id}" value="http://www.pubmed.gov/" onfocus="this.blur();" style="background-color: rgb(197, 206, 213);" type="text" />#<input name="docUri${id}" size="8" id="docUri${id}" type="text" value="${uri}" />
										<%-- ID: <input name="docUri${id}" size="45" id="docUri${id}" type="text" value="${uri}" /> --%>
									</div>
								</div>
							<c:set var="docsCounter" value="${id}" />
						</c:forEach>
					</div>
					<input type="hidden" value="${docsCounter}" name ="docCounter" id="docCounter" />
					<input type="hidden" value="${docsCounter}" name ="docCounterReal" id="docCounterReal" />
					<div id="add_doc_id">
						<a href="javascript:;" title="Add a documentation" onclick="addDoc();">[Add a documentation]</a>
					</div>
				</fieldset>
				
				<div class="help_submission" id="HelpDocumentation" style="display: none;">
					<p>
						Adding a documentation is not mandatory, but can be useful.
					</p>
					<p>
						If you choose to add one, you can enter a full physical address (URL) or just its MIRIAM ID (including the Pubmed ID).
					</p>
					<p>
						The second choice is recommended to avoid any problem about unreachable resources in the future (you only rely on MIRIAM).
					</p>
				</div>
				
				<p>
					<input type="reset" value="Previous values" onclick="window.location.reload()" />
					<input type="submit" value="Update the data-type!" />
				</p>
			
				<script type="text/javascript">
				<!--
					document.forms['submission_form'].reset();
					razEdit();
				//-->
				</script>
				
			</form>
			
			<%-- Compneur group menu --%>
			<div id="bottom_page">
				<c:import url="${initParam.www}compneur_bottom.html" charEncoding="UTF-8" />
			</div>   <!-- id="bottom_page" -->
		
		</div>   <!-- id="content" -->
	
	</body>
</html>
