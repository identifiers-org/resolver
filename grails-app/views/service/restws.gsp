<%--
  Created by IntelliJ IDEA.
  User: sarala
  Date: 17/01/2017
  Time: 09:48
--%>

<%@ page import="grails.util.Holders; uk.ac.ebi.miriam.common.Constants" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Identifiers.org | REST Web Services </title>
</head>

<body>
<c:import url="${Holders.getGrailsApplication().config.getProperty('staticpages') + 'restws.html'}" charEncoding="UTF-8" />
</body>
</html>