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
    <div class="panel-pane pane-custom pane-4 clearfix" >
        <a class="twitter-timeline" href="https://twitter.com/IdentifiersOrg">Tweets by IdentifiersOrg</a> <script async src="//platform.twitter.com/widgets.js" charset="utf-8"></script>
    </div>
</div>
</body>
</html>
