<%--
  Error page: if something went wrong on the server side.
  Allows the user to report the issue.
  Camille Laibe
  20110630
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <%--<meta name="layout" content="main" />   declared in the controller--%>

        <title>MIRIAM Resolver</title>
    </head>
    <body>
        <h1>Oups...</h1>
        <p>Something went <b>horribly wrong</b> on our side when trying to deal with your request. We are therefore not able to fulfil your request and apologise about that.</p>

        <h2>Report this issue</h2>
        <p>If you wish to report this issue, please just fill the form below and we will endeavour to sort the problem as soon as possible.</p>
        <p>All fields are <i>optional</i>, but if you provide us with some contact information, we will be able to keep you informed about any following actions.</p>
        <p>Thank you.</p>

        <g:form name="issueReportForm" action="report">   <%-- url="[controller:'error',action:'report']" --%>
            <h3>Contact information</h3>
            <p style="margin-left:1em;">Name:&nbsp;  <g:textField name="name" size="97" /></p>
            <p style="margin-left:1em;">Email: <g:textField name="email" size="98" /></p>
            <h3>Comment(s)</h3>
            <g:textArea name="comment" rows="5" cols="80" style="margin-left:1em;" />
            <h3>Failed request details</h3>
            <g:textArea name="request" value="URL: ${url}\nParameters: ${parameters}" rows="2" cols="80" readonly="readonly" style="margin-left:1em;" />
            <h3>Error code</h3>
            <g:textArea name="code" value="${code}" rows="1" cols="80" readonly="readonly" style="margin-left:1em;" />
            <h3>Configuration</h3>
            <g:textArea name="configuration" value="${request.getHeader('User-Agent')}" rows="1" cols="80" readonly="readonly" style="margin-left:1em;" />
            <br />
            <g:actionSubmit value="Send report" />
        </g:form>

        <br />
  </body>
</html>