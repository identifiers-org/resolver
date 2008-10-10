<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20080610
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
    'view' part of the application: displays the extra information about a precise data-type
    (annotation using a specific format, like SBML, CellML or BioPAX)  
  
--%><?xml version="1.0" encoding="UTF-8" ?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ajax" uri="http://ajaxtags.org/tags/ajax" %>
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
    <link rel="stylesheet" href="http://www.ebi.ac.uk/inc/css/tabmenu.css" type="text/css" />
    <link rel="SHORTCUT ICON" href="http://www.ebi.ac.uk/bookmark.ico" />
    <!-- ===================================== -->
    <!-- TemplateBeginEditable name="head" -->
    <!-- ===================================== -->
    <!-- start meta tags, css , javascript here -->
    <!-- ===================================== -->
    
    <meta name="keywords" content="Nicolas Le Novère, Camille Laibe, EBI, EMBL, bioinformatics, software, databases, genomics, computational neurobiology, neuroinformatics, systems biology" />
    <link rel="stylesheet" type="text/css" media="screen" href="${initParam.www}style/MIRIAM.css" />
    <link rel="alternate" type="application/rss+xml" title="MIRIAM News Feed" href="${initParam.www}rss/MiriamNews.xml" />
    
    <script type="text/javascript" src="${initParam.www}js/prototype.js"></script>
    <script type="text/javascript" src="${initParam.www}js/scriptaculous.js"></script>
    <script type="text/javascript" src="${initParam.www}js/overlibmws.js"></script>
    <script type="text/javascript" src="${initParam.www}js/overlibmws_crossframe.js"></script>
    <script type="text/javascript" src="${initParam.www}js/overlibmws_iframe.js"></script>
    <script type="text/javascript" src="${initParam.www}js/overlibmws_hide.js"></script>
    <script type="text/javascript" src="${initParam.www}js/overlibmws_shadow.js"></script>
    <script type="text/javascript" src="${initParam.www}js/ajaxtags.js"></script>
    <script type="text/javascript" src="${initParam.www}js/ajaxtags_controls.js"></script>
    <script type="text/javascript" src="${initParam.www}js/ajaxtags_parser.js"></script>
    
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
        <iframe src="http://www.ebi.ac.uk/inc/head.html" name="head" id="head" marginwidth="0" marginheight="0" style="position: absolute; z-index: 1; height: 57px;" frameborder="0" scrolling="no" width="100%"></iframe>
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
                    
                    <h1>MIRIAM - Alpha</h1>
                    
                    <%-- displays the login box (if a user is logged) --%>
                    <m:loginBox login="${sessionScope.login}" />
                    
                    <%-- displays the message coming from the previous request (if any) --%>
                    <m:displayMessage message="${message}" />
                    
                    <h2>Edit examples of annotation: <em>${data.name}</em></h2>
                    
                    <form method="post" id="new_anno1" action="addExistingAnnotation" class="basicForm">
                        <fieldset>
                            <legend>Existing examples of annotation&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpAnnoExisting')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                        
                            <c:forEach var="anno" items="${annotation}">
                                <ul>
                                    <li>${anno.format}
                                        <ul>
		                                    <c:forEach var="tag" items="${anno.tags}">
		                                        <li>${tag.name}</li>
		                                    </c:forEach>
		                                </ul>
		                            </li>
                                </ul>
                            </c:forEach>
                        </fieldset>
                        
                        <div class="help_add_existing" id="HelpAnnoExisting" style="display: none;">
	                        <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
	                            <p>
	                                Here is the list of all the examples of annotation stored for this data type.
	                            </p>
	                        </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                        </div>
                    
                        
                        <fieldset>
                            <legend>Add existing annotation&nbsp;<a href="javascript:;" title="Help" onclick="displayHelp('HelpAddAnnoExisting')"><img src="${initParam.www}img/Help.gif" alt="Help logo" height="16" width="16" /></a></legend>
                            
                            <table summary="format select table">
                                <tr>
                                    <td>Format:&nbsp;</td>
                                    <td>
                                        <select id="format">
			                                <option value="">Select format</option>
			                                <c:forEach var="format" items="${formats}">
			                                    <option value="${format}">${format}</option>
		                                    </c:forEach>
			                            </select>
			                            &nbsp;<span id="progressMsg" style="display:none;"><img alt="Indicator" src="${initParam.www}img/Throbber.gif" /> Loading...</span>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Annotation tag:</td>
                                    <td>
                                        <select id="tag" name="tag" disabled="disabled">
			                                <option value="">Select format</option>
			                            </select>
                                    </td>
                                </tr>
                            </table>
                            
                            <br />
                            <input type="submit" value="Add" class="submit_button" />
                        </fieldset>
                        <div id="errorMsg" style="display:none;border:1px solid #e00;background-color:#fee;padding:2px;margin-top:8px;width:300px;font:normal 12px Arial;color:#900"></div>
                        
                        <input type="hidden" value="${data.id}" name="dataTypeId" id="dataTypeId" />
                    </form>

                    <div class="help_add_existing" id="HelpAddAnnoExisting" style="display: none;">
                        <div class="xround"><b class="xtop"><b class="xb1">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb4">&nbsp;</b></b><div class="xboxcontent">
                            <p>
                                Adds a new example of annotation, based on the ones already used by other data types.
                            </p>
                        </div><b class="xbottom"><b class="xb4">&nbsp;</b><b class="xb3">&nbsp;</b><b class="xb2">&nbsp;</b><b class="xb1">&nbsp;</b></b></div>
                    </div>
                    
                    <script type="text/javascript">
					    function initProgress()
					    {
                            Element.show('progressMsg');
                        }
                        
                        function resetProgress()
                        {
                            Effect.Fade('progressMsg');
                        }
                        
                        function reportError()
                        {
                            if ($('tag').options.length == 0)
                            {
                                $('errorMsg').innerHTML = "Sorry, an error occurred!";
                            }
                            Element.show('errorMsg');
                            setTimeout("Effect.DropOut('errorMsg')", 2500);
                        }
                    </script> 
                    <ajax:select baseUrl="ajaxGetAnnotationTags" 
                                 source="format" 
                                 target="tag" 
                                 parameters="format={format}" 
                                 preFunction="initProgress" 
                                 emptyOptionName="Select format" 
                                 postFunction="resetProgress" 
                                 errorFunction="reportError" />
                    
                    <br />
                    <div class="bottomSeparator"></div>
                    <table width="100%" summary="table bottom links">
                        <tr>
                            <td align="left">
                                <a href="mdb?section=browse&amp;data=${data.id}" title="Return to the data type"><img src="${initParam.www}img/prev.png" alt="Previous arrow icon" align="top" />&nbsp;Go back to the data type</a>
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
                        <iframe src="http://www.ebi.ac.uk/inc/foot.html" name="foot" frameborder="0" marginwidth="0" marginheight="0" scrolling="no"  height="22" width="100%"  style="z-index:2;">
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