<%--
  Page displaying (in xHTML) the response of a resolving query: profile used, so loads the preferred resource in a frame
  Camille Laibe
  20111011
  Modified: Sarala Wimalaratne
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Identifiers.org [${record.requestedUri} | ${profile.shortname}]</title>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-1.8.0.min.js', base: 'http://static.identifiers.org/')}"></script>
</head>

<body>
<div id="top_bar">
    <div class="info">
        Alternative ways to access information for <span class="entity">${record.entityId}</span> (from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>) can be found at <a href="${record.infoUri.encodeAsHTML()}" title="Access to other available resources...">${record.infoUri.encodeAsHTML()}</a>.
    </div>
    <div class="close_button">
        <a href="${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}" title="Close this top bar">Close</a> <a href="${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}" title="Close this top bar"><img src="${resource(dir: 'images', file: 'red_cross.gif', base: 'http://static.identifiers.org/')}" alt="Closing icon" title="Close this top bar" style="vertical-align: middle;" /></a>
    </div>
</div>

<div class="frame" id="frame">
    <div class="update">
        <p id="loadertext">Display of <span class="entity">${record.entityId}</span>: please wait, loading in process...</p>
    </div>
    <iframe id="externalContent" src="${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}" frameborder="0" width="100%" height="1000px" scrolling="yes" onload="hideProgress()">
        If your browser was supporting frames, you would see here the content provided by: <a href="${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}">${preferredResource.urlPrefix.encodeAsHTML()}${entity}${preferredResource.urlSuffix.encodeAsHTML()}</a>.
    </iframe>
</div>

<script>
    $(function(){
        var overiFrame = 1;
        $('iframe').hover( function() {
            overiFrame = 1;
        }, function() {
            overiFrame = -1
        });

        $(window).blur( function() {
            if( overiFrame == 1 )
                window.location.href=parent.document.getElementById('externalContent').src; /* example, do your stats here */
        });
    });

</script>

</body>

</html>
