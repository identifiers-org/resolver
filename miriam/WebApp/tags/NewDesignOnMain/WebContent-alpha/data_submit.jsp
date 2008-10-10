<%--
  @author Camille Laibe
  @version 20070109
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: template to diplay static pages
--%><?xml version="1.0" encoding="UTF-8" ?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


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
			
			<h2><span class="box_green">Add</span> data-type</h2>

			<p>
				Here is the form to submit a new data-type to MIRIAM database.
			</p>
			<p>
				If you are authenticated, the new data-type will be added directly in MIRIAM database after you pressed the <b>Submit</b> button. If not, the new data-type will be added after a checking process.
			</p>
			
			<h3><span class="h3_right">Help</span></h3>
			<p>
				You can display every help bubbles by clicking on: <a href="javascript:;" title="Help" onclick="displayHelps()">Displays all the help messages</a>, or hide them: <a href="javascript:;" title="Help" onclick="hideHelps()">Hides all the help messages</a>.
			</p>
			<p>
				Moreover, you can display the individual help by clincking on the button: <img src="${initParam.www}img/Help.gif" alt="Interrogation mark" height="16" width="16" /> located in the title of each section.
			</p>
			
			<h3><span class="h3_right">Add a new data-type</span></h3>
			
			<form method="post" id="submission_form" action="addDataType" onsubmit="return validate_submission();">
			
				<p>
					First you need to enter the name of the data-type you want to add to the database. After you can add as much as synonyms you want.
				</p>
				
				<fieldset id="form_name_synonyms">
					<legend>Name and synonyms&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpNames')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<table summary="Name and synonyms information">
						<tr>
							<td>
								Primary name:&nbsp;
							</td>
							<td>
								<input type="text" name="name" size="45" id="nameId" />
							</td>
						</tr>
					</table>
					<input type="hidden" value="0" name="synonymsCounter" id="synonymsCounter" />
					<input type="hidden" value="0" name="synonymsCountReal" id="synonymsCounterReal" />
					<div id="synonyms_id"></div>
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
				
				<p>
					Here are some information about the data-type: definition and regular expression that identifiers of elements from this data-type follow.
				</p>
				
				<fieldset id="form_def_pattern">
					<legend>Definition and pattern&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpDef')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<table summary="Definition and pattern information">
						<tr>
							<td>
								Definition:&nbsp;
							</td>
							<td>
								<textarea cols="50" rows="2" name="def" id="defId" onfocus="javascript:this.select();">Enter definition here...</textarea>
							</td>
						</tr>
						<tr>
							<td>
								Identifier pattern:&nbsp;
							</td>
							<td>
								<textarea cols="50" rows="2" name="pattern" id="patternId" onfocus="javascript:this.select();">Enter Identifier pattern here...</textarea>
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
				
				<p>
					In this part, you need to add (at least one) URIs for this data-type. The URIs can be URLs or URNs. If you know an older version of the URI which is still in use: you can add it as a deprecated one.
				</p>
				
				<fieldset id="form_uris">
					<legend>URIs&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpURIs')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<table summary="URIs information">
						<tr>
							<td>
								Official URL:&nbsp;
							</td>
							<td>
								<input type="text" name="url" size="45" id="urlId" />
							</td>
						</tr>
						<tr>
							<td>
								Official URN:&nbsp;
							</td>
							<td>
								<input type="text" name="urn" size="45" id="urn_id" />
							</td>
						</tr>
					</table>
					<input type="hidden" value="0" name ="deprecatedCounter" id="deprecatedCounter" />
					<input type="hidden" value="0" name ="deprecatedCountReal" id="deprecatedCounterReal" />
					<div id="deprecated_id"></div>
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
				
				<p>
					Here, you need to add all the physical adress of the resources where we can access to information about the data-type.
				</p>
				
				<fieldset id="form_resources">
					<legend>Resources&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpResources')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<input type="hidden" value="1" name ="resourcesCounter" id="resourcesCounter" />
					<input type="hidden" value="1" name ="resourcesCounterReal" id="resourcesCounterReal" />
					<div id="resources_id">
						<div id="resources1Div">
							<table summary="Resources information" class="resources">
								<tr>
									<td rowspan="3" valign="bottom">
										<b>#1:&nbsp;</b>
									</td>
									<td>
										Data entry:&nbsp;
									</td>
									<td>
										<input type="text" name="dataEntryPrefix1" size="34" id="depId1" />
									</td>
									<td>
										<b>$id</b>
									</td>
									<td>
										<input type="text" name="dataEntrySuffix1" size="15" id="desId1" />
									</td>
								</tr>
								<tr>
									<td>
										Data resource:&nbsp;
									</td>
									<td colspan="3">
										<input type="text" name="dataResource1" size="45" id="drId1" />
									</td>
								</tr>
								<tr>
									<td>
										Information:&nbsp;
									</td>
									<td colspan="3">
										<input type="text" name="information1" size="55" id="infoId1" />
										<!-- <textarea cols="55" rows="2" name="def" id="def_id"></textarea> -->
									</td>
								</tr>
								<tr>
									<td rowspan="2" valign="top">
										&nbsp;
									</td>
									<td>
										Institution:&nbsp;
									</td>
									<td colspan="3">
										<input type="text" name="institution1" size="45" id="instituteId1" />
									</td>
								</tr>
								<tr>
									<td>
										Country:&nbsp;
									</td>
									<td colspan="3">
										<input type="text" name="country1" size="30" id="countryId1" />
									</td>
								</tr>
							</table>
						</div>
					</div>
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
				
				<p>
					Finally, if you know some pieces of documentation about the data-type (publication, paper, chapter, web sites, ...) you can put them here.
				</p>
				
				<fieldset id="form_docs">
					<legend>Documentation&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpDocumentation')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<input type="hidden" value="0" name ="docCounter" id="docCounter" />
					<input type="hidden" value="0" name ="docCounterReal" id="docCounterReal" />
					
					<div id="doc_id"></div>
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
				
				<c:if test="${sessionScope.login == null}">
					<p>
						As you are not authenticated, use the following field to give some information about you.
					</p>
					<fieldset id="form_user">
						<legend>User information&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpUser')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
						<div id="user_form">
							<textarea cols="50" rows="2" name="user" id="userId"></textarea>
						</div>
					</fieldset>
					<div class="help_submission" id="HelpUser" style="display: none;">
						<p>
							You can enter in this field your name, group, institution and any other information in order to explain why you would like to add this data-type to MIRIAM.
						</p>
					</div>
				</c:if>
				
				<p>
					<input type="submit" value="Submit the new data-type!" class="action_button" />
				</p>
			
				<script type="text/javascript">
				<!--
					document.forms['submission_form'].reset();
					raz();
				//-->
				</script>
				
			</form>
			
			<h3><span class="h3_right">Warning</span></h3>
			
			<p>
				You need to enable <em>Javascript</em> in your web browser in order to use this form.
			</p>
			
			<%-- Compneur group menu --%>
			<div id="bottom_page">
				<c:import url="${initParam.www}compneur_bottom.html" charEncoding="UTF-8" />
			</div>   <!-- id="bottom_page" -->
		
		</div>   <!-- id="content" -->
	
	</body>
</html>