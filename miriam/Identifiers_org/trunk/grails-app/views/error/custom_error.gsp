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
        <h2>${error.title}</h2>
        
        <p>Sorry, you requested <i>${error.request}</i>, but the server running Identifiers.org generated the following status code: <b>${error.code}</b>.</p>
        
        <br />
        <p style="font-style: italic; padding-left:1em;">${error.message.encodeAsHTML()}</p>

        <h3>Report bug</h3>
        <p>If you think this is the result of an error on our side, please report it to:<a href="mailto:biomodels-net-support@lists.sf.net" target="_top">biomodels-net-support</a> (including all information displayed on this page).</p>
        <br />
    </body>
</html>