<%--
  Page displaying (in xHTML) the response of a partial query: only data collection part in the URL
  Camille Laibe
  20111011
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <%--<meta name="layout" content="resolve" />   declared in the controller--%>

        <title>Identifiers.org [${requestedUri}]</title>
    </head>
    <body>
    <div id="top_bar">
                <div class="info">
%{--        <div style="position:fixed; top:0px; left:0px; height:30px; width:100%; background-color:#EEF5F5; border-top: 1px solid #e33e3e; border-bottom: 1px solid #e33e3e; padding-left:5px;">--}%
%{--            <div style="text-align:center; color:#e33e3e; font-weight:bold;">
                ${requestedUri} <g:if test="${obsolete}"><span style="color:#feba12; font-weight:bold; padding-left:1em;">[<img src="${resource(dir: 'images', file: 'warning.gif', base: 'http://www.ebi.ac.uk/compneur-srv/identifiers-org/')}" alt="Warning icon" title="The requested URI is deprecated, please use the official one instead: ${record.officialUrl()}" style="vertical-align: top; margin-top: 2px; padding-right: 3px;" /> <a href="${record.officialUrl()}" title="The requested URI is deprecated, please use the official one instead: ${record.officialUrl()}" class="warning_link">Official URL</a>]</span></g:if>
            </div>--}%
            %{--<div style="font-size:80%; margin-top:5px;">--}%
                Preview of the data collection <b>${record.name}</b> (${record.id}) from the <a href="http://www.ebi.ac.uk/miriam/" title="Go to: MIRIAM Registry">MIRIAM Registry</a>.
            </div>
            %{--<div style="position:fixed; top:20px; right:10px; font-size:80%;">--}%
        <div class="close_button">
                <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.id}" title="Close this top bar">Close</a> <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.id}" title="Close this top bar"><img src="${resource(dir: 'images', file: 'red_cross.gif', base: 'http://www.ebi.ac.uk/compneur-srv/identifiers-org/')}" alt="Closing icon" title="Close this top bar" style="vertical-align: middle;" /></a>
            </div>
        </div>
        
        <div style="height:100%; margin-top:32px;">
            <iframe src="http://www.ebi.ac.uk/miriam/main/datatypes/${record.id}" frameborder="0" width="100%" height="800px" scrolling="yes">
            If your browser was supporting frames, you would see here the content provided by: <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.id}">http://www.ebi.ac.uk/miriam/main/datatypes/${record.id}</a>.
            </iframe>
        </div>
    </body>
</html>
