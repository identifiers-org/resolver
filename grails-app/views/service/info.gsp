<%--
  Created by IntelliJ IDEA.
  User: sarala
  Date: 03/11/2016
  Time: 12:17
--%>

<%@ page import="grails.util.Holders; uk.ac.ebi.miriam.common.Constants" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Identifiers.org | Info </title>
</head>

<body>
    <c:import url="${Holders.getGrailsApplication().config.getProperty('staticpages') + 'info.html'}" charEncoding="UTF-8" />
</body>
</html>