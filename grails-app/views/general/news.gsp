<%--
  News page.
  Camille Laibe
  20111011
  Modified: Sarala Wimalaratne
--%>

<%@ page import="grails.util.Holders; uk.ac.ebi.miriam.common.Constants" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
    <head>
        <title>Identifiers.org [news]</title>
    </head>
    <body>
        <c:import url="${Holders.getGrailsApplication().config.getProperty('staticpages') + 'news.html'}" charEncoding="UTF-8" />
    </body>
</html>