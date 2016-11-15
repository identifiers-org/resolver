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
<div id="home_main_content" class="small-12 medium-9 column">
    <c:import url="${Holders.getGrailsApplication().config.getProperty('staticpages')+ 'intro.html'}" charEncoding="UTF-8" />

</div>

<div class="small-12 medium-3 column hide-for-small-only">

    <div class="shortcuts transparent">
        <div class="panel-pane pane-custom pane-2 clearfix">

            <h3 class="pane-title">Connect with us</h3>

            <ul class="columns small-6 no-bullet">
                <li><a href="//twitter.com/IdentifiersOrg" class='icon icon-socialmedia' data-icon='T'>Twitter</a></li>
            </ul>
            <ul class="columns small-6 no-bullet">
                <li><a href="//github.com/identifiers-org/" class='icon icon-socialmedia' data-icon='g'>GitHub</a></li>
            </ul>
        </div>

        <div class="panel-separator"></div>

        <div class="panel-pane pane-custom pane-4 clearfix">
                <a class="twitter-timeline" data-tweet-limit="3"
                   href="https://twitter.com/IdentifiersOrg" >Tweets by IdentifiersOrg</a> <script async
                                                                                                  src="//platform.twitter.com/widgets.js"
                                                                                                  charset="utf-8"></script>
        </div>
    </div>
</div>

</body>
</html>
