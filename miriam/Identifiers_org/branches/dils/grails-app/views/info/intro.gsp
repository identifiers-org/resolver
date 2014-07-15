<%--
  Front page.
  Camille Laibe
  20111011
  Modified: Sarala Wimalaratne
--%>

<%@ page import="grails.util.Holders; uk.ac.ebi.miriam.common.Constants" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Identifiers.org</title>
</head>
<body>
<!-- main content (3/4 of total width) -->
<div id="home_main_content" class="grid_18 alpha">
    <c:import url="${Holders.getGrailsApplication().config.getProperty('staticpages')+ 'intro.html'}" charEncoding="UTF-8" />

</div>

<!-- right panel (1/4 of total width) -->
<!-- latest news -->
<div class="grid_6 alpha">
    <div id="news_side_panel">
        <div class="news_heading">
            <div class="news_heading_left">
                <a href="news" title="Access all news" class="icon icon-generic" data-icon="N">News</a>
            </div>
            <div class="news_heading_right">
                <a href="https://twitter.com/biomodels" title="@biomodels" class="icon icon-socialmedia" data-icon="T" style="">&nbsp;</a>
            </div>
        </div>

        <c:import url="${Holders.getGrailsApplication().config.getProperty('staticpages')+ 'home_news.html'}" charEncoding="UTF-8" />

    </div>
</div>
</body>
</html>
