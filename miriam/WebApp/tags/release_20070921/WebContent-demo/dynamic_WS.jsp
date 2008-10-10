<%--
  @author Camille Laibe
  @version 20070523
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: requests page using Web Services
--%><?xml version="1.0" encoding="utf-8"?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://ajaxtags.org/tags/ajax" prefix="ajax" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <meta name="description" content="European Bioinformatics Institute - Computational Neurobiology" />
  <meta name="author" content="Nicolas Le Novère and Camille Laibe" />
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
  
  <meta name="keywords" content="Nicolas Le Novere, EBI, EMBL, bioinformatics, software, databases, genomics, computational neurobiology, neuroinformatics, systems biology" />
	<link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/MIRIAM.css" />
	
  <script type="text/javascript" src="${initParam.www}js/prototype-1.4.0.js"></script>
  <script type="text/javascript" src="${initParam.www}js/scriptaculous.js"></script>
  <script type="text/javascript" src="${initParam.www}js/overlibmws.js"></script>
  <script type="text/javascript" src="${initParam.www}js/ajaxtags-1.2-beta2.js"></script>
  
  <script type="text/javascript">
  	// display (nicely) the waiting message
	  function MsgWait()
		{
			Effect.BlindDown('getRequestResultMsg');
 		}
 		// clear (nicely) the waiting message
 		function MsgWaitEnd()
		{
			setTimeout("Effect.DropOut('getRequestResultMsg');", 800);
		}
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
					
					<c:import url="${initParam.www}request_intro.html" charEncoding="UTF-8" />
			
					<h2>Web Services Demo</h2>
					
					<table class="simple" summary="list of all the available methods">
						<tr>
							<td>
								Choose one action in the list:&nbsp;
							</td>
							<td>
								<form method="get" action="mdb">
									<div>
										<input type="hidden" name="section" value="request" />
										<select name="request">
							  	    <option value="">Select one</option>
							  	    <option value="getDataTypeURI">+ &nbsp; get the official URI of a data-type</option>
							    	  <option value="getDataTypeURN">- &nbsp;  get the official URN of a data-type</option>
								      <option value="getDataTypeURL">- &nbsp; get the official URL of a data-type</option>
								      <option value="getDataTypeURIs" class="line-above">+ &nbsp; get all the URNs or URLs of a data-type</option>
							      	<option value="getDataTypeURNs">- &nbsp; get all the URNs of a data-type</option>
								      <option value="getDataTypeURLs">- &nbsp; get all the URLs of a data-type</option>
								      <option value="getDataTypeAllURIs" class="line-above">+ &nbsp; get all the URIs of a data-type</option>
		 						      <option value="getURI" class="line-above">+ &nbsp; get the official URI of an element</option>
								      <option value="getURN">- &nbsp; get the official URN of an element</option>
								      <option value="getURL">- &nbsp; get the official URL of an element</option>
								      <option value="getDataTypeDef" class="line-above">+ &nbsp; get the definition of a data-type</option>
								      <option value="getDataTypePattern">+ &nbsp; get the regular expression of a data-type</option>
								      <option value="getDataEntries" class="line-above">+ &nbsp; get links to an element</option>
								      <option value="getDataResources">+ &nbsp; get links to a data-type</option>
								      <option value="isDeprecated" class="line-above">+ &nbsp; test if a URI is deprecated</option>
								      <option value="getOfficialURI">+ &nbsp; get the official URI of a data-type</option>
								      <option value="getDataTypeSynonyms" class="line-above">+ &nbsp; get the synonyms of a data-type</option>
								      <option value="getName">+ &nbsp; get the official name of a data-type</option>
								      <option value="getNames">+ &nbsp; get all the names of a data-type</option>
						  	 		</select>
						  	 		<input type="submit" value="Go!" class="submit_button" />
					  	 		</div>
					  	 	</form>
						  </td>
				  	</tr>
			  	</table>
			  	
			  	
					<h2>Query</h2>
					
					<div id="requestForm" class="request">
						<c:choose>
							<c:when test="${request == 'getDataTypeURN'}">
								<p>
									Get the official URN of a data-type:
								</p>
								<form id="getDataTypeURNForm" action="" onsubmit="getDataTypeURNButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeURN</legend>
										<label>data-type name</label>
										<input type="text" id="getDataTypeURNParam" size="30" />
										<input type="button" value="Search" id="getDataTypeURNButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeURN.do" source="getDataTypeURNButton" target="getRequestResult" parameters="getDataTypeURNParam={getDataTypeURNParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypeURNs'}">
								<p>
									Get all the URNs of a data-type:
								</p>
								<form id="getDataTypeURNsForm" action="" onsubmit="getDataTypeURNsButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeURNs</legend>
										<label>data-type name</label>
										<input type="text" id="getDataTypeURNsParam" size="30" />
										<input type="button" value="Search" id="getDataTypeURNsButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeURNs.do" source="getDataTypeURNsButton" target="getRequestResult" parameters="getDataTypeURNsParam={getDataTypeURNsParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypeURL'}">
								<p>
									Get the official URL of a data-type:
								</p>
								<form id="getDataTypeURLForm" action="" onsubmit="getDataTypeURLButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeURL</legend>
										<label>data-type name</label>
										<input type="text" id="getDataTypeURLParam" size="30" />
										<input type="button" value="Search" id="getDataTypeURLButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeURL.do" source="getDataTypeURLButton" target="getRequestResult" parameters="getDataTypeURLParam={getDataTypeURLParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypeURLs'}">
								<p>
									Get all the URLs of a data-type:
								</p>
								<form id="getDataTypeURLsForm" action="" onsubmit="getDataTypeURLsButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeURLs</legend>
										<label>data-type name</label>
										<input type="text" id="getDataTypeURLsParam" size="30" />
										<input type="button" value="Search" id="getDataTypeURLsButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeURLs.do" source="getDataTypeURLsButton" target="getRequestResult" parameters="getDataTypeURLsParam={getDataTypeURLsParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypeURI'}">
								<p>
									get the official URI of a data-type:
								</p>
								<form id="getDataTypeURIForm" action="" onsubmit="getDataTypeURIButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeURI</legend>
										<label>data-type name</label>
										<input type="text" id="getDataTypeURIParam1" size="30" />
										<br />
										<label>type of URI (URL, URN or empty)</label>
										<input type="text" id="getDataTypeURIParam2" size="30" value="URL" />
										<input type="button" value="Search" id="getDataTypeURIButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeURI.do" source="getDataTypeURIButton" target="getRequestResult" parameters="getDataTypeURIParam1={getDataTypeURIParam1},getDataTypeURIParam2={getDataTypeURIParam2}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypeURIs'}">
								<p>
									Get all the URIs of a data-type:
								</p>
								<form id="getDataTypeURIsForm" action="" onsubmit="getDataTypeURIsButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeURIs</legend>
										<label>data-type name</label>
										<input type="text" id="getDataTypeURIsParam1" size="30" />
										<br />
										<label>type of URI (URL, URN or empty)</label>
										<input type="text" id="getDataTypeURIsParam2" size="30" value="URL" />
										<input type="button" value="Search" id="getDataTypeURIsButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeURIs.do" source="getDataTypeURIsButton" target="getRequestResult" parameters="getDataTypeURIsParam1={getDataTypeURIsParam1},getDataTypeURIsParam2={getDataTypeURIsParam2}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypeAllURIs'}">
								<p>
									Get all the URIs of a data-type:
								</p>
								<form id="getDataTypeAllURIsForm" action="" onsubmit="getDataTypeAllURIsButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeAllURIs</legend>
										<label>data-type name</label>
										<input type="text" id="getDataTypeAllURIsParam" size="30" />
										<input type="button" value="Search" id="getDataTypeAllURIsButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeAllURIs.do" source="getDataTypeAllURIsButton" target="getRequestResult" parameters="getDataTypeAllURIsParam={getDataTypeAllURIsParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypeDef'}">
								<p>
									Get the definition of a data-type:
								</p>
								<form id="getDataTypeDefForm" action="" onsubmit="getDataTypeDefButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeDef</legend>
										<label>data-type name or URI</label>
										<input type="text" id="getDataTypeDefParam" size="30" />
										<input type="button" value="Search" id="getDataTypeDefButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeDef.do" source="getDataTypeDefButton" target="getRequestResult" parameters="getDataTypeDefParam={getDataTypeDefParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getURN'}">
								<p>
									Get the official URN of an element:
								</p>
								<form id="getURNForm" action="" onsubmit="getURNButton.click(); return false;">
									<fieldset class="request">
										<legend>getURN</legend>
										<label>data-type name</label>
										<input type="text" id="getURNParam1" size="30" />
										<br />
										<label>element id</label>
										<input type="text" id="getURNParam2" size="30" />
										<input type="button" value="Search" id="getURNButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getURN.do" source="getURNButton" target="getRequestResult" parameters="getURNParam1={getURNParam1},getURNParam2={getURNParam2}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getURL'}">
								<p>
									Get the official URL of an element:
								</p>
								<form id="getURLForm" action="" onsubmit="getURLButton.click(); return false;">
									<fieldset class="request">
										<legend>getURL</legend>
										<label>data-type name</label>
										<input type="text" id="getURLParam1" size="30" />
										<br />
										<label>element id</label>
										<input type="text" id="getURLParam2" size="30" />
										<input type="button" value="Search" id="getURLButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getURL.do" source="getURLButton" target="getRequestResult" parameters="getURLParam1={getURLParam1},getURLParam2={getURLParam2}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getURI'}">
								<p>
									Get the official URI of an element:
								</p>
								<form id="getURIForm" action="" onsubmit="getURIButton.click(); return false;">
									<fieldset class="request">
										<legend>getURI</legend>
										<label>data-type name</label>
										<input type="text" id="getURIParam1" size="30" />
										<br />
										<label>element id</label>
										<input type="text" id="getURIParam2" size="30" />
										<br />
										<label>type of URI (URL, URN or empty)</label>
										<input type="text" id="getURIParam3" size="30" value="URL" />
										<input type="button" value="Search" id="getURIButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getURI.do" source="getURIButton" target="getRequestResult" parameters="getURIParam1={getURIParam1},getURIParam2={getURIParam2},getURIParam3={getURIParam3}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataEntries'}">
								<p>
									Get links to an element:
								</p>
								<form id="getDataEntriesForm" action="" onsubmit="getDataEntriesButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataEntries</legend>
										<label>data-type name or URI</label>
										<input type="text" id="getDataEntriesParam1" size="30" />
										<br />
										<label>element id</label>
										<input type="text" id="getDataEntriesParam2" size="30" />
										<input type="button" value="Search" id="getDataEntriesButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataEntries.do" source="getDataEntriesButton" target="getRequestResult" parameters="getDataEntriesParam1={getDataEntriesParam1},getDataEntriesParam2={getDataEntriesParam2}}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataResources'}">
								<p>
									Get links to a data-type:
								</p>
								<form id="getDataResourcesForm" action="" onsubmit="getDataResourcesButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataResources</legend>
										<label>data-type name or URI</label>
										<input type="text" id="getDataResourcesParam1" size="30" />
										<input type="button" value="Search" id="getDataResourcesButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataResources.do" source="getDataResourcesButton" target="getRequestResult" parameters="getDataResourcesParam1={getDataResourcesParam1}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'isDeprecated'}">
								<p>
									Test if a URI is deprecated:
								</p>
								<form id="isDeprecatedForm" action="" onsubmit="isDeprecatedButton.click(); return false;">
									<fieldset class="request">
										<legend>isDeprecated</legend>
										<label>data-type URI</label>
										<input type="text" id="isDeprecatedParam" size="30" />
										<input type="button" value="Search" id="isDeprecatedButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="isDeprecated.do" source="isDeprecatedButton" target="getRequestResult" parameters="isDeprecatedParam={isDeprecatedParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getOfficialURI'}">
								<p>
									Get the official URI of a data-type:
								</p>
								<form id="getOfficialURIForm" action="" onsubmit="getOfficialURIButton.click(); return false;">
									<fieldset class="request">
										<legend>getOfficialURI</legend>
										<label>deprecated data-type URI</label>
										<input type="text" id="getOfficialURIParam" size="30" />
										<input type="button" value="Search" id="getOfficialURIButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getOfficialURI.do" source="getOfficialURIButton" target="getRequestResult" parameters="getOfficialURIParam={getOfficialURIParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypePattern'}">
								<p>
									Get the regular expression of a data-type:
								</p>
								<form id="getDataTypePatternForm" action="" onsubmit="getDataTypePatternButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypePattern</legend>
										<label>data-type name or URI</label>
										<input type="text" id="getDataTypePatternParam" size="30" />
										<input type="button" value="Search" id="getDataTypePatternButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypePattern.do" source="getDataTypePatternButton" target="getRequestResult" parameters="getDataTypePatternParam={getDataTypePatternParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getDataTypeSynonyms'}">
								<p>
									Get the synonyms of a data-type:
								</p>
								<form id="getDataTypeSynonymsForm" action="" onsubmit="getDataTypeSynonymsButton.click(); return false;">
									<fieldset class="request">
										<legend>getDataTypeSynonyms</legend>
										<label>data-type name</label>
										<input type="text" id="getDataTypeSynonymsParam" size="30" />
										<input type="button" value="Search" id="getDataTypeSynonymsButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getDataTypeSynonyms.do" source="getDataTypeSynonymsButton" target="getRequestResult" parameters="getDataTypeSynonymsParam={getDataTypeSynonymsParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getName'}">
								<p>
									Get the official name of a data-type:
								</p>
								<form id="getNameForm" action="" onsubmit="getNameButton.click(); return false;">
									<fieldset class="request">
										<legend>getName</legend>
										<label>data-type URI</label>
										<input type="text" id="getNameParam" size="30" />
										<input type="button" value="Search" id="getNameButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getName.do" source="getNameButton" target="getRequestResult" parameters="getNameParam={getNameParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:when test="${request == 'getNames'}">
								<p>
									Get all the names of a data-type:
								</p>
								<form id="getNamesForm" action="" onsubmit="getNamesButton.click(); return false;">
									<fieldset class="request">
										<legend>getNames</legend>
										<label>data-type URI</label>
										<input type="text" id="getNamesParam" size="30" />
										<input type="button" value="Search" id="getNamesButton" class="submit_button" />
									</fieldset>
								</form>
								<ajax:htmlContent baseUrl="getNames.do" source="getNamesButton" target="getRequestResult" parameters="getNamesParam={getNamesParam}" preFunction="MsgWait" postFunction="MsgWaitEnd" />
							</c:when>
							
							<c:otherwise>
								<p>Choose one of the methods in the list above...</p>
							</c:otherwise>
						</c:choose>
					</div>
					
					<!-- displays the result of the request -->
					<h2>Answer</h2>
					
					<div class="right">
						<div id="getRequestResult" class="answer"></div>
						<div id="getRequestResultMsg" class="answerStatus" style="display:none;">
							<p>
								<img alt="Indicator" src="${initParam.www}img/Throbber.gif" /> Searching...
							</p>
						</div>
					</div>
					
					<!-- displays an help message -->
					<h2>Help</h2>
					<div id="request_help">
						<p>
							Here is a sample summarising all the parameters and the expected result:
						</p>
						
						<br />
						
						<div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
						
						<c:choose>
							<c:when test="${request == 'getDataTypeURN'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">urn:lsid:uniprot.org</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataTypeURNs'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element" rowspan="2">Result:</td><td class="help_result">urn:lsid:uniprot.org</td>
									</tr>
									<tr>
										<td class="help_result">...</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataTypeURL'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">EC code</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">http://www.ec-code.org/</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataTypeURLs'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">EC code</td>
									</tr>
									<tr>
										<td class="help_element" rowspan="2">Result:</td><td class="help_result">http://www.ec-code.org/</td>
									</tr>
									<tr>
										<td class="help_result">http://www.ebi.ac.uk/IntEnz/</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataTypeURI'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element">Type of URI:</td><td class="help_value">URL</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">http://www.uniprot.org/</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataTypeURIs'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">EC code</td>
									</tr>
									<tr>
										<td class="help_element">Type of URI:</td><td class="help_value"></td>
									</tr>
									<tr>
										<td class="help_element" rowspan="2">Result:</td><td class="help_result">http://www.ec-code.org/</td>
									</tr>
									<tr>
										<td class="help_result">http://www.ebi.ac.uk/IntEnz/</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataTypeAllURIs'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element" rowspan="2">Result:</td><td class="help_result">http://www.uniprot.org/</td>
									</tr>
									<tr>
										<td class="help_result">urn:lsid:uniprot.org</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataTypeDef'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name or URI:</td><td class="help_value">biomodels</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">BioModels Database is a data resource that allows biologists to store[...]</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getURN'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element">Element id:</td><td class="help_value">P47757</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">urn:lsid:uniprot.org:P47757</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getURL'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element">Element id:</td><td class="help_value">P47757</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">http://www.uniprot.org/#P47757</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getURI'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element">Element id:</td><td class="help_value">P47757</td>
									</tr>
									<tr>
										<td class="help_element">Type of URI:</td><td class="help_value">URL</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">http://www.uniprot.org/#P47757</td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataEntries'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name or URI:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element">Element id:</td><td class="help_value">P47757</td>
									</tr>
									<tr>
										<td class="help_element" rowspan="3">Result:</td><td class="help_result"><a href="http://www.ebi.uniprot.org/entry/CAPZB_MOUSE" title="More information about this data-entry">http://www.ebi.uniprot.org/entry/P47757</a></td>
									</tr>
									<tr>
										<td class="help_result"><a href="http://us.expasy.org/uniprot/CAPZB_MOUSE" title="More information about this data-entry">http://us.expasy.org/uniprot/P47757</a></td>
									</tr>
									<tr>
										<td class="help_result"><a href="http://www.pir.uniprot.org/cgi-bin/upEntry?id=CAPZB_MOUSE" title="More information about this data-entry">http://www.pir.uniprot.org/cgi-bin/upEntry?id=P47757</a></td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'getDataResources'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name or URI:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element" rowspan="3">Result:</td><td class="help_result"><a href="http://www.ebi.uniprot.org/" title="More information about this data-data">http://www.ebi.uniprot.org/</a></td>
									</tr>
									<tr>
										<td class="help_result"><a href="http://www.expasy.uniprot.org/" title="More information about this data-data">http://www.expasy.uniprot.org/</a></td>
									</tr>
									<tr>
										<td class="help_result"><a href="http://www.pir.uniprot.org/" title="More information about this data-data">http://www.pir.uniprot.org/</a></td>
									</tr>
								</table>
							</c:when>
							
							<c:when test="${request == 'isDeprecated'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type URI:</td><td class="help_value">http://www.ncbi.nlm.nih.gov/Taxonomy/</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">Warning: this URI is deprecated!</td>
									</tr>
								</table>
							</c:when>
						
							<c:when test="${request == 'getOfficialURI'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Deprecated data-type URI:</td><td class="help_value">http://www.ncbi.nlm.nih.gov/Taxonomy/</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">http://www.taxonomy.org/</td>
									</tr>
								</table>
							</c:when>
						
							<c:when test="${request == 'getDataTypePattern'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name or URI:</td><td class="help_value">uniprot</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">^([OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9](_[\dA-Z]{1,5})?)$</td>
									</tr>
								</table>
							</c:when>
						
							<c:when test="${request == 'getDataTypeSynonyms'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type name:</td><td class="help_value">go</td>
									</tr>
									<tr>
										<td class="help_element" rowspan="2">Result:</td><td class="help_result">Gene Ontology</td>
									</tr>
									<tr>
										<td class="help_result">GO</td>
									</tr>
								</table>
							</c:when>
						
							<c:when test="${request == 'getName'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type URI:</td><td class="help_value">http://www.uniprot.org/</td>
									</tr>
									<tr>
										<td class="help_element">Result:</td><td class="help_result">UniProt</td>
									</tr>
								</table>
							</c:when>
						
							<c:when test="${request == 'getNames'}">
								<table class="table_help" summary="usage example">
									<tr>
										<td class="help_element">Data-type URI:</td><td class="help_value">http://www.geneontology.org/</td>
									</tr>
									<tr>
										<td class="help_element" rowspan="2">Result:</td><td class="help_result">Gene Ontology</td>
									</tr>
									<tr>
										<td class="help_result">GO</td>
									</tr>
								</table>
							</c:when>
						
							<c:otherwise>
								<p>
									<i>the help information will be displayed after choosing one of the methods in the list above...</i>
								</p>
							</c:otherwise>
						</c:choose>
						
						</div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
						
					</div>
				
					<!-- focus on the input field of the first parameter (if necessary) -->
					<script type="text/javascript">
						if (document.forms[1])
						{
							if (document.forms[1][1])
							{
								document.forms[1][1].focus();
							}
						}
					</script>
			
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
