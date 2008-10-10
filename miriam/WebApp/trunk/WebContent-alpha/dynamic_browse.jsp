<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20080703
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
  	'view' part of the application: displays the overview of the content of the database (browse)
--%><?xml version="1.0" encoding="UTF-8"?>

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
					
					<h2>Browse the data types
					    <c:if test="${(preferences != null) && (!empty preferences)}">
                            <span style="color:rgb(94, 158, 158);">&nbsp;(${preferences})</span>
                        </c:if>
					</h2>
					
					<p>
						Brief overview of the different data types stored in <em>MIRIAM Database</em>.
					</p>
					
					<br />
					
					<table width="100%" summary="generic browse options and commands (top) table">
					   <tr>
					       <td>
						       <c:if test="${hasPrevious}">
						           <a href="mdb?section=browse&amp;offset=${previousOffset}&amp;nb=${number}${option}" title="Display the previous page"><img src="${initParam.www}img/prev.png" alt="Previous arrow icon" align="top" /> Previous page</a>&nbsp;
						       </c:if>
						       <c:if test="${hasNext}">
						           &nbsp;<a href="mdb?section=browse&amp;offset=${nextOffset}&amp;nb=${number}${option}" title="Display the next page">Next page <img src="${initParam.www}img/next.png" alt="Next arrow icon" align="top" /></a>
						       </c:if>
						   </td>
					       <td align="right">Display: <a href="mdb?section=browse&amp;offset=0&amp;nb=10${option}" title="Display 10 data types per page">10</a> | <a href="mdb?section=browse&amp;offset=0&amp;nb=20${option}" title="Display 20 data types per page">20</a> | <a href="mdb?section=browse&amp;offset=0&amp;nb=30${option}" title="Display 30 data types per page">30</a> | <a href="mdb?section=browse&amp;offset=0&amp;nb=${max}${option}" title="Display all the data types">All</a></td>
					   </tr>
					</table>
					
					<form method="post" action="deleteDataType">
						<table class="sqlresult" summary="data types overview">
							<tr>
								<c:if test="${sessionScope.role == 'admin'}">
									<th class="action"><img src="${initParam.www}img/Delete.gif" alt="delete icon" title="Deletes data type(s)" height="16" width="16" /></th>
								</c:if>
								<th>Name</th><th>URI</th><th>Definition</th>
							</tr>
							
							<mir:arrayBrowse data="${data}">
								<tr class="${class}">
									<c:if test="${sessionScope.role == 'admin'}">
										<td class="${class}">
											<input name="datatype2remove" type="checkbox" value="${id}" />
										</td>
									</c:if>
									<td class="${class}"><a href="mdb?section=browse&amp;data=${id}" title="Access to the complete information about the data type: ${name}">${name}</a></td><td class="${class}">${uri}</td><td class="${class}">${definition}</td>
								</tr>
							</mir:arrayBrowse>
						</table>
						
						<c:if test="${hasNext or hasPrevious}">
						    <table width="100%" summary="generic browse options and commands (bottom) table">
                                <tr>
                                    <c:if test="${hasPrevious}">
                                        <td align="left"><a href="mdb?section=browse&amp;offset=${previousOffset}&amp;nb=${number}${option}" title="Display the previous page"><img src="${initParam.www}img/prev.png" alt="Previous arrow icon" align="top" /> Previous page</a></td>
                                    </c:if>
                                    <c:if test="${hasNext}">
                                        <td align="right"><a href="mdb?section=browse&amp;offset=${nextOffset}&amp;nb=${number}${option}" title="Display the next page">Next page <img src="${initParam.www}img/next.png" alt="Next arrow icon" align="top" /></a></td>
                                    </c:if>
                                </tr>
                            </table>
						</c:if>
						
						<c:if test="${sessionScope.role == 'admin'}">
							<p>
								<input type="submit" value="Delete" class="submit_button" />
								<input type="reset" value="Reset" class="reset_button" />
							</p>
						</c:if>
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
