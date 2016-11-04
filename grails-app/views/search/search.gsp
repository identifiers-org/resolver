<%--
  Created by IntelliJ IDEA.
  User: sarala
  Date: 23/08/2016
  Time: 09:58
--%>

<%@ page import="grails.util.Holders; uk.ac.ebi.miriam.common.Constants" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Identifiers.org | Advanced Search</title>
</head>

<body>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.0/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'search.js', base: '//static.identifiers.org/')}"></script>
%{--<script type="text/javascript" src="${resource(dir: 'js', file: 'search.js')}"></script>--}%
<link rel="stylesheet" href="${resource(dir: 'css', file: 'search.css', base: '//static.identifiers.org/')}" type="text/css" />
%{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'search.css')}" type="text/css" />--}%

<h3 class='icon icon-functional' data-icon='1'>Advanced Search</h3>

<table>
    <tr>
        <td>
            <label for="resources">Find valid database resource names, eg: CHEBI </label>
        </td>
        <td>
            <div id="resources-widget-container">
                <input id="resources">
            </div>
        </td>
    </tr>

    <tr>
        <td>
            <label for="resIdent">Validate a given prefix:identifier, eg: CHEBI:36927 </label>
        </td>
        <td>
            <input id="resIdent" type="text">
            <button id="validate">Validate</button>
            <div id="progressbar"></div>
            <div id="validate-result"></div>
        </td>
    </tr>

    <tr>
        <td>
            <label for="databases">Alpha: Find resources for a given identifier, eg: CHEBI:36927 </label>
        </td>
        <td>
            <div id="databases-widget-container">
                <input id="databases">
            </div>
        </td>
    </tr>
</table>

<c:import url="${Holders.getGrailsApplication().config.getProperty('staticpages') + 'advSearch.html'}" charEncoding="UTF-8" />

</body>
</html>