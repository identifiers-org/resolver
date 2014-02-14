<%--
  Page displaying (in xHTML) the response of a resolving query: profile used, so loads the preferred resource in a frame
  Camille Laibe
  20111011
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <%--<meta name="layout" content="resolve" />   declared in the controller--%>

        <title>Identifiers.org [${record.requestedUri} | ${profile.shortname}]</title>
    </head>
    <body>
        <div id="top_bar">
            <div class="info">
%{--
                Access to <span class="entity">${record.entityId}</span> (from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>) using the preferred resource of the profile <acronym title="Description of the profile: ${profile.name}" class="help_tooltip">${profile.shortname}</acronym>.
                <br />
                Entity available from <b>${record.dataCollection.resources.size()}</b> providers, for more information please refer to: <a href="${record.officialUri.encodeAsHTML()}" title="Access to all resources...">${record.officialUri.encodeAsHTML()}</a>.
--}%            Alternatives to access information for <span class="entity">${record.entityId}</span> (from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>) can be found at <a href="${record.infoUri.encodeAsHTML()}" title="Access to other available resources...">${record.infoUri.encodeAsHTML()}</a>
            </div>
            <div class="close_button">
                <a href="${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}" title="Close this top bar">Close</a> <a href="${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}" title="Close this top bar"><img src="${resource(dir: 'images', file: 'red_cross.gif', base: 'http://static.identifiers.org/')}" alt="Closing icon" title="Close this top bar" style="vertical-align: middle;" /></a>
            </div>
           %{-- <div>
                <table class="foot">
                    <tr>
                        <td class="redirect_errors">
                            <g:each in="${record.messages}" var="message">
                               <span style="padding-right: 2em;">[<acronym title="${message.description}">${message.summary}</acronym>]</span>
                            </g:each>
                        </td>
                    </tr>
                </table>
            </div>--}%
        </div>

        <div style="margin-top:32px; height:100%;">
            <iframe src="${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}" frameborder="0" width="100%" height="800px" scrolling="yes">
                If your browser was supporting frames, you would see here the content provided by: <a href="${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}">${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}</a>.
            </iframe>
        </div>
    </body>
</html>
