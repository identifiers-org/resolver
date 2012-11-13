<%--
  Page displaying (in xHTML) the response of a resolving query with only one recorded resource: so loads it in a frame
  Camille Laibe
  20111125
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <%--<meta name="layout" content="resolve" />   declared in the controller--%>

        <title>Identifiers.org [${record.requestedUri}</title>
    </head>
    <body>
        <div id="top_bar">
            <div class="title">
                ${record.requestedUri}
            </div>
            <div class="info">
                Access to <span class="entity">${record.entityId}</span> (from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>) using the only recorded resource: <acronym title="Description of the resource: ${record.dataCollection.resources[0].description} (${record.dataCollection.resources[0].institution}, ${record.dataCollection.resources[0].location})" class="help_tooltip">${record.dataCollection.resources[0].id}</acronym>.
            </div>
            <div class="close_button">
                <a href="${record.dataCollection.resources[0].urls[0].link.encodeAsHTML()}" title="Close this top bar">Close</a> <a href="${record.dataCollection.resources[0].urls[0].link.encodeAsHTML()}" title="Close this top bar"><img src="${resource(dir: 'images', file: 'red_cross.gif', base: 'http://www.ebi.ac.uk/compneur-srv/identifiers-org/')}" alt="Closing icon" title="Close this top bar" style="vertical-align: middle;" /></a>
            </div>
            <div style="margin-top:10px;">
                <table class="foot">
                    <tr>
                        <td class="redirect_errors">
                            <g:each in="${record.messages}" var="message">
                               <span style="padding-right: 2em;">${message.description}</span>
                            </g:each>
                        </td>
                        <td class="powered_by">Powered by: <a href="http://identifiers.org/" title="Identifiers.org">Identifiers.org</a> &amp; <a href="http://identifiers.org/registry/" title="Go to: MIRIAM Registry">MIRIAM Registry</a></td>
                    </tr>
                </table>
            </div>
        </div>

        <div style="margin-top:82px; height:100%;">
            <iframe src="${record.dataCollection.resources[0].urls[0].link.encodeAsHTML()}" frameborder="0" width="100%" height="800px" scrolling="yes">
                If your browser was supporting frames, you would see here the content provided by: <a href="${record.dataCollection.resources[0].urls[0].link.encodeAsHTML()}">${record.dataCollection.resources[0].urls[0].link.encodeAsHTML()}</a>.
            </iframe>
        </div>
    </body>
</html>
