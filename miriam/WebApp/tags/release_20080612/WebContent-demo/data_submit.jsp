<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20080611
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: template to diplay static pages
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
	<link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/contents.css" type="text/css" />
	<link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/userstyles.css" type="text/css" />
	<script src="http://www.ebi.ac.uk/inc/js/contents.js" type="text/javascript"></script>
	<link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/sidebars.css" type="text/css" />
	<link rel="SHORTCUT ICON" href="http://www.ebi.ac.uk/bookmark.ico" />
	<!-- ===================================== -->
	<!-- TemplateBeginEditable name="head" -->
	<!-- ===================================== -->
	<!-- start meta tags, css , javascript here -->
	<!-- ===================================== -->
	
	<meta name="keywords" content="Nicolas Le Novère, Camille Laibe, EBI, EMBL, bioinformatics, software, databases, genomics, computational neurobiology, neuroinformatics, systems biology" />
	<link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/MIRIAM.css" />
	<link rel="alternate" type="application/rss+xml" title="MIRIAM News Feed" href="${initParam.www}rss/MiriamNews.xml" />
	
	<%-- javascript functions for dynamic forms --%>
	<script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>
	
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
		
		<h2>Add a data type</h2>

		<p>
			Here is the form to submit a new data type to MIRIAM Database.
		</p>
		
		<p>
			<c:choose>
				<%-- the user is logged in --%>
				<c:when test="${sessionScope.login != null}">
					You are an authenticated user: the new data type will be added directly in MIRIAM Database after you pressed the <b>Submit</b> button.
				</c:when>
				
				<%-- anonymous user  --%>
				<c:otherwise>
					You are an anonymous user: the new data type will not be added directly in MIRIAM Database after you pressed the <b>Submit</b> button, there will be a checking process before.
				</c:otherwise>
			</c:choose>
		</p>
		
		<h2>Help</h2>
		<p>
			You can display all help bubbles by clicking on: <a href="javascript:;" title="Help" onclick="displayHelps()">Displays all the help messages</a>, or hide them: <a href="javascript:;" title="Help" onclick="hideHelps()">Hides all the help messages</a>.
		</p>
		<p>
			Moreover, you can display the individual help by clicking on the button: <img src="${initParam.www}img/Help.gif" alt="Interrogation mark" height="16" width="16" /> located in the title of each section.
		</p>
		
		<h2>Add a new data type</h2>
		
		<form method="post" id="submission_form" action="addDataType" onsubmit="return validate_submission();">
		
			<p>
				First you need to enter the name of the data type you want to add to the database. After you can add as much synonyms as you want.
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
				<div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
					<p>
						The field "Primary name" is mandatory and must be the official name of the data type (for example: "Gene Ontology" or "UniProt").
					</p>
					<p>
						You can add several synonyms to the official name. That can be useful if the name is an acronym or can be summarised with an acronym (for example: "Gene Ontology" and "GO").
					</p>
				</div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
			</div>
			
			<p>
				Here is some information about the data type: definition and regular expression (<i>i.e.</i> pattern for identifiers of elements, following the PERL style).
			</p>
			
			<fieldset id="form_def_pattern">
				<legend>Definition and pattern&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpDef')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
				<table summary="Definition and pattern information">
					<tr>
						<td>
							Definition:&nbsp;
						</td>
						<td>
							<textarea cols="70" rows="3" name="def" id="defId" onfocus="javascript:this.select();">Enter definition here...</textarea>
						</td>
					</tr>
					<tr>
						<td>
							Identifier pattern:&nbsp;
						</td>
						<td>
							<textarea cols="70" rows="2" name="pattern" id="patternId" onfocus="javascript:this.select();">Enter Identifier pattern here...</textarea>
						</td>
					</tr>
				</table>
			</fieldset>
			
			<div class="help_submission" id="HelpDef" style="display: none;">
				<div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
					<p>
						The field "Definition" must be filled with one or two of sentences explaining what kind of elements are stored in the data type.
					</p>
					<p>
						The field "Identifier pattern" is the pattern (or PERL-style regular expression) of the identifiers used by the data type (for example: "^GO:\d{7}$" for Gene Ontology).
					</p>
				</div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
			</div>
			
			<p>
				In this part, you need to add (at least one) URIs for this data type. The official URI takes a URN form. If you know an older version of the URI which is still in use: you can add it as a deprecated one.
			</p>
			
			<fieldset id="form_uris">
				<legend>URIs&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpURIs')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
				<table summary="URIs information">
					<%--
					<tr>
						<td>
							Official URL:&nbsp;
						</td>
						<td>
							<input type="text" name="url" size="45" id="urlId" />
						</td>
					</tr>
					--%>
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
				<div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
					<p>
						The URI is a unique string of characters used to unambiguously identify the data type (for example: "urn:miriam:uniprot" for UniProt). The only official URI takes a URN form.
					</p>
					<p>
						You can add several deprecated versions of the URIs, which can be URLs or URNs. If they are URLs, keep in mind that they don't necessarily need to be valid physical addresses. They are only used as identifiers not as physical locations on Internet!
					</p>
				</div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
			</div>
			
			<p>
				Here, you need to add all the physical addresses of the resources where we can access the information about the data type.
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
									Example of identifier:&nbsp;
								</td>
								<td>
									<input type="text" name="dataExample1" size="30" id="xplId1" />
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
								<td rowspan="3" valign="top">
									&nbsp;
								</td>
								<td>
									Information:&nbsp;
								</td>
								<td colspan="3">
									<input type="text" name="information1" size="55" id="infoId1" />
									<!-- <textarea cols="55" rows="2" name="def" id="def_id"></textarea> -->
								</td>
							</tr>
							<tr>
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
				<div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
					<p>
						To access (via a web browser or a piece of software) a data type, you need to enter, at least, one resource.
					</p>
					<p>
						The "Data entry" field is divided into three parts (one of them cannot be edited): this is the physical address used to access a precise element stored by the data type. The field in the middle ($id) stands for the identifier of an element (it complies with the pattern entered previously).
					</p>
					<p>
						The "Example of identifier" field should contain an identifier used by this resource, in order to display an example of usage to the user.
					</p>
					<p>
						The "Data resource" field must contains a physical link to the main page of the resource.
					</p>
					<p>
						The three other fields give more information about the institution managing the resource (one sentence of information, the name of the institution, and the country).
					</p>
				</div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
			</div>
			
			<p>
				Finally, if you know some pieces of documentation about the data type (publication, paper, chapter, web sites, ...) you can put them here.
			</p>
			
			<fieldset id="form_docs">
				<legend>Documentation&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpDocumentation')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
				<input type="hidden" value="0" name ="docCounter" id="docCounter" />
				<input type="hidden" value="0" name ="docCounterReal" id="docCounterReal" />
				
				<div id="doc_id"></div>
				<div id="add_doc_id">
					<a href="javascript:;" title="Add a piece of documentation" onclick="addDoc();">[Add a piece of documentation]</a>
				</div>
			</fieldset>
			
			<div class="help_submission" id="HelpDocumentation" style="display: none;">
				<div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
					<p>
						Adding a piece of documentation is not mandatory, but can be useful.
					</p>
					<p>
						If you choose to add one, you can enter either its physical address (the URL you can put in the address bar of a Web browser), its PubMed Identifier (PMID) or its Digital Object Identifier (DOI).
					</p>
					<p>
						The second and third choices are recommended to avoid any problem about unreachable resources in the future (that only rely on MIRIAM Resources).
					</p>
				</div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
			</div>
			
			<%-- Spam trap: this field is not visible (cf. Miriam.css) and its value should stay empty --%>
			<fieldset class="SpicedHam">
				<legend>This field must keep its initial value (that means stay empty)!</legend>
				<table summary="Some more useless information">
					<tr>
						<td>
							Information:&nbsp;
						</td>
						<td>
							<input type="text" size="15" value="" name="pourriel" id="pourriel" />
						</td>
					</tr>
				</table>
			</fieldset>
			
			<c:if test="${sessionScope.login == null}">
				<p>
					As you are not authenticated, use the following field to give some more information.
				</p>
				<fieldset id="form_user">
					<legend>User information&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpUser')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
					<div id="user_form">
						<textarea cols="80" rows="4" name="user" id="userId"></textarea>
					</div>
				</fieldset>
				
				<div class="help_submission" id="HelpUser" style="display: none;">
					<div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
						<p>
							You can enter in this field your name, group, institution, and any other information in order to explain why you would like to add this data type to MIRIAM Resources.
						</p>
						<p>
							You can as well give some suggested tags for this data type (cf. the <a href="mdb?section=metadata" title="List of tags">list of tags</a> currently used).
						</p>
					</div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
				</div>
			</c:if>
			
			<p>
				<input type="submit" value="Submit!" class="submit_button" />
			</p>
		
			<script type="text/javascript">
			<!--
				document.forms['submission_form'].reset();
				raz();
			//-->
			</script>
			
		</form>
		
		<h2>Warning</h2>
		
		<p>
			You need to enable <em>Javascript</em> in your web browser in order to use this form.
		</p>

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
