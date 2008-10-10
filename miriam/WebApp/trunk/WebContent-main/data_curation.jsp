<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20080729
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
    'view' part of the application: displays the complete information about a precise data type
    Displays a specific data type from the curation pipeline
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
    
    <%-- javascript functions for dynamic forms --%>
    <script type="text/javascript" src="${initParam.www}js/MiriamDynamicForms.js"></script>
    
    <script type="text/javascript" src="${initParam.www}js/prototype.js"></script>
    
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
                    
                    <h1>MIRIAM</h1>
                    
                    <%-- displays the login box (if a user is logged) --%>
                    <m:loginBox login="${sessionScope.login}" />
                    
                    <%-- displays the message coming from the previous request (if any) --%>
                    <m:displayMessage message="${message}" />
                    
                    <h2>Curate data type: <em>${data.name}</em></h2>
                    
                    <br />
                    
                    <form method="post" id="submission_form" action="curaDataEdit" onsubmit="return validate_submission();">
                        
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
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    The field "Primary name" is mandatory and must be the official name of the data type (for example: "Gene Ontology" or "Uniprot").
                                </p>
                                <p>
                                    You can add several synonyms to the official name. That can be useful if the name is an acronym or can be summarised with an acronym (for example: "Gene Ontology" and "GO").
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
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
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    The field "Definition" must be filled with one or two of sentences explaining what kind of elements are stored in the data type.
                                </p>
                                <p>
                                    The field "Identifier pattern" is the pattern (or PERL-style regular expression) of the identifiers used by the data type (for example: "^GO:\d{7}$" for Gene Ontology).
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <fieldset id="form_uris">
                            <legend>URIs&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpURIs')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            <table summary="URIs information">
                                <%--
                                <tr>
                                    <td>
                                        Official URL:&nbsp;
                                    </td>
                                    <td>
                                        <input type="text" name="url" size="45" id="url_id" value="${data.URL}" />
                                    </td>
                                </tr>
                                --%>
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
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    The URI is a unique string of characters used to unambiguously identify the data type (for example: "urn:miriam:uniprot" for UniProt). The only official URI takes a URN form.
                                </p>
                                <p>
                                    You can add several deprecated versions of the URIs, which can be URLs or URNs. If they are URLs, keep in mind that they don't necessarily need to be valid physical addresses. They are only used as identifiers not as physical locations on Internet!
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <fieldset id="form_resources">
                            <legend>Resources&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpResources')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            <div id="resources_id">
                                <mir:resourcesBrowse data="${data.resources}">
                                    <div id="resources${id}Div">
                                        <input type="hidden" value="${resourceId}" name="resourceId${id}" id="resourceId${id}" />
                                        <input type="hidden" value="${obsolete}" name="obsolete${id}" id="obsolete${id}" />
                                        <table summary="Resources information" class="resources">
                                            <tr>
                                                <td rowspan="2" valign="bottom">
                                                    <c:choose>
                                                        <c:when test="${obsolete == '1'}">
                                                            <img src="${initParam.www}img/Warning.gif" title="WARNING: this resource is obsolete!" alt="Resource obsolete!" align="bottom" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            &nbsp;
                                                        </c:otherwise>
                                                    </c:choose>
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
                                                    Example of identifier:&nbsp;
                                                </td>
                                                <td>
                                                    <input type="text" name="dataExample${id}" size="30" id="xplId${id}" value="${example}" />
                                                </td>
                                                <c:if test="${!empty example}">
                                                <td>
                                                    &nbsp;[<a class="external" href="#" onclick="generateExampleLink('depId${id}', 'desId${id}', 'xplId${id}');return false;" title="Link generated using the provided information">Test link</a>]
                                                </td>
                                                </c:if>
                                            </tr>
                                            <tr>
                                                <td>
                                                    <b>#${id}:&nbsp;</b>
                                                </td>
                                                <td>
                                                    Data resource:&nbsp;
                                                </td>
                                                <td colspan="3">
                                                    <input type="text" name="dataResource${id}" size="45" id="drId${id}" value="${base}" />
                                                </td>
                                            </tr>
                                            <tr>
                                                <td rowspan="3" valign="top">
                                                    <a href="javascript:;" title="Remove this resource" onclick="removeResource('resources${id}Div')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>
                                                </td>
                                                <td>
                                                    Information:&nbsp;
                                                </td>
                                                <td colspan="3">
                                                    <input type="text" name="information${id}" size="55" id="infoId${id}" value="${info}" />
                                                </td>
                                            </tr>
                                            <tr>
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
                        
                        <fieldset id="form_docs">
                            <legend>Documentation&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpDocumentation')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            <%-- TODO: create a Tag Handler (and check if the documentationURLs contains also the transformed URIs -if this is the case: this a problem-) --%>
                            <c:set var="docsCounter" value="0" />
                            <div id="doc_id">
                                <c:forEach var="url" items="${data.documentationURLs}" varStatus="id">
                                    <div class="docSubmitForm" id="docSubmitForm${id.count}">
                                        <div>
                                            <a href="javascript:;" title="Remove this piece of documentation" onclick="removeDoc('docSubmitForm${id.count}')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;<span id="docTypeBox${id.count}">Type: PMID: <input type="radio" name="docType${id.count}" value="PMID" onclick="return displayPubMedForm('docForm${id.count}', ${id.count});" />&nbsp;DOI: <input type="radio" name="docType${id.count}" value="DOI" onclick="return displayDoiForm('docForm${id.count}', ${id.count});" />&nbsp;Physical Location: <input type="radio" name="docType${id.count}" value="URL" checked="checked" onclick="return displayUrlForm2('docForm${id.count}', ${id.count}, '${url}');" /></span>
                                        </div>
                                        <div id="docForm${id.count}" class="indentDiv">
                                            Location: <input name="docUri${id.count}" size="45" id="docUri${id.count}" value="${url}" type="text" />
                                        </div>
                                    </div>
                                    <c:set var="docsCounter" value="${id.count}" />
                                </c:forEach>
                                
                                <mir:resourcesEdit uris="${data.documentationIDs}" types="${data.documentationIDsType}" start="${docsCounter}">
                                    <c:choose>
                                        <c:when test="${uriType == 'DOI'}">
                                            <div class="docSubmitForm" id="docSubmitForm${id}">
                                                <div>
                                                    <a href="javascript:;" title="Remove this piece of documentation" onclick="removeDoc('docSubmitForm${id}')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;<span id="docTypeBox${id}">Type: PMID: <input type="radio" name="docType${id}" value="PMID" onclick="return displayPubMedForm('docForm${id}', ${id});" />&nbsp;DOI: <input type="radio" name="docType${id}" value="DOI" onclick="return displayDoiForm2('docForm${id}', ${id}, '${uri}');"  checked="checked" />&nbsp;Physical Location: <input type="radio" name="docType${id}" value="URL" onclick="return displayUrlForm('docForm${id}', ${id});" /></span>
                                                </div>
                                                <div id="docForm${id}" class="indentDiv">
                                                    DOI:&nbsp;<input type="text" name="docUri${id}" size="30" id="docUri${id}" value="${uri}" />
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="docSubmitForm" id="docSubmitForm${id}">
                                                <div>
                                                    <a href="javascript:;" title="Remove this piece of documentation" onclick="removeDoc('docSubmitForm${id}')"><img src="${initParam.www}img/Delete.gif" alt="Delete logo" height="16" width="16" /></a>&nbsp;<span id="docTypeBox${id}">Type: PMID: <input type="radio" name="docType${id}" value="PMID" onclick="return displayPubMedForm2('docForm${id}', ${id}, '${uri}');"  checked="checked" />&nbsp;DOI: <input type="radio" name="docType${id}" value="DOI" onclick="return displayDoiForm('docForm${id}', ${id});" />&nbsp;Physical Location: <input type="radio" name="docType${id}" value="URL" onclick="return displayUrlForm('docForm${id}', ${id});" /></span>
                                                </div>
                                                <div id="docForm${id}" class="indentDiv">
                                                    PMID:&nbsp;<input type="text" name="docUri${id}" size="20" id="docUri${id}" value="${uri}" />
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:set var="docsCounter" value="${id}" />
                                </mir:resourcesEdit>
                            </div>
                            <input type="hidden" value="${docsCounter}" name ="docCounter" id="docCounter" />
                            <input type="hidden" value="${docsCounter}" name ="docCounterReal" id="docCounterReal" />
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
                                    If you choose to add one (or several), you can either enter a full physical address (URL), a PubMed ID or a DOI.
                                </p>
                                <p>
                                    The second and third choices are recommended to avoid any problem about unreachable resources in the future (only relies on MIRIAM Resources).
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <fieldset id="form_user">
                            <legend>Submission's comment&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpSubmissionComment')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            <div id="user_form">
                                <textarea cols="80" rows="4" name="subComment" id="subCommentId" style="background-color: #C5CED5;" onfocus="this.blur();">${data.subInfo}</textarea>
                            </div>
                        </fieldset>
                        
                        <div class="help_submission" id="HelpSubmissionComment" style="display: none;">
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    Here is the comment left by the person who submitted the data type.
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <fieldset id="form_curator">
                            <legend>Curator's comment&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpCuratorComment')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            <div id="curator_form">
                                <textarea cols="80" rows="4" name="curator" id="curatorId">${data.comment}</textarea>
                            </div>
                        </fieldset>
                        
                        <div class="help_submission" id="HelpCuratorComment" style="display: none;">
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    You can enter in this field any comments you wish to make about this data type. This comment will not be made public but every curators of MIRIAM Resources will be able to see (and modify it) it.
                                </p>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <fieldset id="form_state">
                            <legend>State&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpState')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            <div id="state_form">
                                <select id="stateId" name="state">
                                    <option value="Submitted"<c:if test="${data.state == 'Submitted'}"> selected="selected"</c:if>>Submitted</option>
                                    <option value="Curation"<c:if test="${data.state == 'Curation'}"> selected="selected"</c:if>>Curation</option>
                                    <%--<option value="Published"<c:if test="${data.state == 'Published'}"> selected="selected"</c:if>>Published</option>--%>
                                    <option value="Pending"<c:if test="${data.state == 'Pending'}"> selected="selected"</c:if>>Pending</option>
                                    <option value="Canceled"<c:if test="${data.state == 'Canceled'}"> selected="selected"</c:if>>Canceled</option>
                                </select>
                                &nbsp;&nbsp;Current state: <em>${data.state}</em>
                            </div>
                        </fieldset>
                        
                        <div class="help_submission" id="HelpState" style="display: none;">
                            <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                                <p>
                                    State of the data type in the curation pipeline:
                                </p>
                                <ul>
                                    <li><b>Submitted</b>: an anonymous user (not logged) has submitted a new data type</li>
                                    <li><b>Curation</b>: a curator started to work on a previously submitted data type</li>
                                    <%--<li><b>Published</b>: the data type has been curated and has now moved to the public website</li>--%>
                                    <li><b>Pending</b>: the data type cannot be published as it is now for a specific reason (this reason should be given in comment)</li>
                                    <li><b>Canceled</b>: the data type has been canceled (will never be published) for a specific reason (explain in comment)</li>
                                </ul>
                            </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                        
                        <p>
                            <input type="submit" value="Update" class="submit_button" />
                            <input type="reset" value="Reset" onclick="window.location.reload()" class="reset_button" />
                        </p>
                        
                        <script type="text/javascript">
                        <!--
                            document.forms['submission_form'].reset();
                            razEdit();
                        //-->
                        </script>
                        
                    </form>
                    
                    
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