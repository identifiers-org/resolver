<%--
  Page displaying (in xHTML) the response of a resolving query.
  Camille Laibe
  20130426
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <%--<meta name="layout" content="resolve" />   declared in the controller--%>

        <title>Identifiers.org [${record.requestedEntityURI}]</title>
    </head>
    <body>
        <div id="header">
            <h1>${record.requestedEntityURI}</h1>
            <div class="info">
            Identifiers.org URI for identifying <span class="entity">${record.entityId}</span> from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>.
            </div>
%{--            <div class="info">
                <g:if test="${record.dataCollection.resources.size() > 1}">
                    <b>${record.dataCollection.resources.size()}</b> physical locations (or resources) are available for accessing <span class="entity">${record.entityId}</span> (from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>):
                </g:if>
                <g:else>
                    <b>${record.dataCollection.resources.size()}</b> physical location (or resource) is available for accessing <span class="entity">${record.entityId}</span> (from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>):
                </g:else>--}%%{--
            </div>--}%
        </div>

        <div id="content">

            <g:each in="${record.messages}" var="message">
                <div class="feedback_user">
                    <div class="feedback_summary">${message.summary}</div><div class="feedback_description">${message.description}</div>
                </div>
            </g:each>

            <div class="info">
            Information for entry <span class="entity">${record.entityId}</span> in the <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a> data collection can be accessed from any of the following locations:
            </div>

            <table class="resources">
                <%-- primary/official resource (if any) --%>
                <g:if test="${primaryResource != null}">
                    <tr>
                        <td colspan="2" align="center">
                            <div style="text-align:right; font-size:70%; color:grey;">Primary location</div>
                            <div class="resource">
                                <div class="resource_overview" style="background-color:#A2C7C7; background-image:none;"></div>
                                <div class="resource_info">
                                    <a href="${primaryResource.urls[0].link.encodeAsHTML()}" title="Access to '${record.entityId}' via this resource (${primaryResource.id})">
                                        <span class="desc">${primaryResource.description.encodeAsHTML()}</span>
                                        <span class="institution">${primaryResource.institution.encodeAsHTML()}</span>
                                        <span class="country">${primaryResource.location.encodeAsHTML()}</span>
                                        <g:if test="${primaryResource.reliability == 0}">
                                            <span class="status">(Uptime: <i>unknown</i>)</span>
                                        </g:if>
                                        <g:else>
                                            <span class="status">(Uptime: ${primaryResource.reliability}%)</span>
                                        </g:else>
                                    </a>
                                </div>
                            </div>
                        </td>
                    </tr>
                </g:if>
                <%-- all other resource(s) --%>
                <g:each in="${0..allResources.size()-1}" var="counter">
                    <g:if test="${(counter % 2) == 0}">
                       <tr>
                    </g:if>

                    <td align="center">
                      <div class="resource">
                        <div class="resource_overview"></div>
                        <div class="resource_info">
                          <a href="${allResources[counter].urls[0].link.encodeAsHTML()}" title="Access to '${record.entityId}' via this resource (${allResources[counter].id})">
                            <span class="desc">${allResources[counter].description.encodeAsHTML()}</span>
                            <span class="institution">${allResources[counter].institution.encodeAsHTML()}</span>
                            <span class="country">${allResources[counter].location.encodeAsHTML()}</span>
                            <g:if test="${allResources[counter].reliability == 0}">
                                <span class="status">(Uptime: <i>unknown</i>)</span>
                            </g:if>
                            <g:else>
                                <span class="status">(Uptime: ${allResources[counter].reliability}%)</span>
                            </g:else>
                          </a>
                        </div>
                      </div>
                    </td>

                    <g:if test="${(counter % 2) || (counter == allResources.size()-1)}">
                        </tr>
                    </g:if>
                </g:each>
            </table>
          </div>


          <div id="footer">
            <div class="powered">Powered by <a href="http://identifiers.org/registry/" title="Go to: MIRIAM Registry">MIRIAM Registry</a></div>
            <div class="formats">Information also available in: <a href="${record.requestedUri}.rdf" title="Information in RDF">RDF/XML</a></div>
            <div class="end"></div>
          </div>

        <script type="text/javascript">
            // the mouse moves over the overview of a resource
            $('.resource').mouseover(function()
            {
              $(this).children('div').children('a').children('span').css('opacity','1').css('color','#e33e3e').css('font-size', '130%');   // .css('z-index','3')
              $(this).children('div').children('a').css('text-decoration','none');
              $(this).children('div.resource_overview').css('opacity','0.3').css('color','#000');   // .css('z-index','2')
            });
            // the mouse leaves the overview of a resource
            $('.resource').mouseleave(function()
            {
              $(this).children('div.resource_overview').css('opacity','1').css('color','#000').css('z-index','2');
              $(this).children('div').children('a').children('.desc').css('color','inherit').css('font-size', '100%');
              $(this).children('div').children('a').children('.institution').css('color','inherit').css('font-size', '100%');
              $(this).children('div').children('a').children('.country').css('color','inherit').css('font-size', '100%');
              $(this).children('div').children('a').children('.status').css('color','inherit').css('font-size', '90%');
              $(this).children('div').children('a').css('text-decoration','underline');
            });
      </script>
    </body>
</html>
