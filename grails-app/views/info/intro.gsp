<%--
  Front page.
  Camille Laibe
  20111011
--%>

<%@ page import="grails.util.Holders; uk.ac.ebi.miriam.common.Constants" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <%--<meta name="layout" content="main"></meta>   declared in the controller--%>

        <title>Identifiers.org</title>
    </head>
    <body>
        <c:import url="${ Holders.getGrailsApplication().config.getProperty('staticpages')+ 'intro.html'}" charEncoding="UTF-8" />

    </body>
</html>
