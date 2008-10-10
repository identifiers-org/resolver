<%--
  @author Camille Laibe <camille.laibe@ebi.ac.uk>
  @version 20080821
  @copyright EMBL-EBI, Computational Neurobiology Group
  
  MIRIAM Web Interface,
    'view' part of the application: template to diplay static pages
--%><?xml version="1.0" encoding="utf-8"?>

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
                    
                    <%-- displays the message coming from the previous request (if any) --%>
                    <m:displayMessage message="${message}" />
                    
                    
                    <h2>Logout</h2>
                    
                    <p>
                        If you want to logout from the authenticated part of MIRIAM Resources, click on the button below.
                    </p>
                    
                    <br />
                    <form method="post" id="logout_form" action="signOut">
                        <input type="submit" value="Logout" class="submit_button" />
                    </form>
                    
                    
                    <h2>Personnal information</h2>
                    
                    <p>
                        Here are the personnal information we store about you. If you want to update it, change the content of the following form and click the "Update Info" button.
                    </p>
                    
                    <br />
                    <form method="post" id="update_info_form" action="updateUserInfo">
                        <table summary="User information table">
	                        <tr>
	                            <td>
	                                Login:&nbsp;
	                            </td>
	                            <td>
	                                ${data.login}
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>
	                                First name:&nbsp;
	                            </td>
	                            <td>
	                                <input type="text" name="firstName" size="45" id="first_name_id" value="${data.firstName}" />
	                            </td>
	                        </tr>
	                        <tr>
                                <td>
                                    Last name:&nbsp;
                                </td>
                                <td>
                                    <input type="text" name="lastName" size="45" id="last_name_id" value="${data.lastName}" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Email:&nbsp;
                                </td>
                                <td>
                                    <input type="text" name="email" size="45" id="email_id" value="${data.email}" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    Organisation:&nbsp;
                                </td>
                                <td>
                                    <input type="text" name="organisation" size="45" id="organisation_id" value="${data.organisation}" />
                                </td>
                            </tr>
	                    </table>
	                    <br />
	                    <input type="submit" value="Update" class="submit_button" />
                    </form>
                    
                    
                    <h2>Password</h2>
                    
                    <p>
                        If you want to change your password, fill the following form and click the "Change Pass" button.
                    </p>
                    
                    <form method="post" id="update_pass_form" action="passChange">
                        <table summary="password update table">
	                        <tr>
	                            <td>
	                                Current password:&nbsp;
	                            </td>
	                            <td>
	                                <input type="password" name="oldPass" size="20" id="old_pass_id" />
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>
	                                New password:&nbsp;
	                            </td>
	                            <td>
	                                <input type="password" name="newPass1" size="20" id="new_pass_one_id" />
	                            </td>
	                        </tr>
	                        <tr>
	                            <td>
	                                New Password (bis):&nbsp;
	                            </td>
	                            <td>
	                                <input type="password" name="newPass2" size="20" id="new_pass_two_id" />
	                            </td>
	                        </tr>
                        </table>
                        <br />
                        <input type="submit" value="Change" class="submit_button" />
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
