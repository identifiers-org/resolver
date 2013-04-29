<%--
  Custom error page: nicely display why something went wrong to the user.
  Camille Laibe
  20130426
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <%--<meta name="layout" content="main"></meta>   declared in the controller--%>

        <title>Identifiers.org [error]</title>
    </head>
    <body>
        <h1>${error.title}</h1>
        
        <p>Sorry, you requested <i>${error.request}</i>, but the server running Identifiers.org generated the following status code: <b>${error.code}</b>.</p>
        
        <br />
        <p style="font-style: italic; padding-left:1em;">${error.message.encodeAsHTML()}</p>

        <h2>Report bug</h2>
        <p>If you think this is the result of an error on our side, please report it to: <b>biomodels-net-support [AT] lists.sf.net</b> (including all information displayed on this page). Thank you.</p>
        <br />
    </body>
</html>