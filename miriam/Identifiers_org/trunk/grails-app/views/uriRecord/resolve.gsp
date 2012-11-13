<%--
  Page displaying (in xHTML) the response of a resolving query.
  Camille Laibe
  20120217
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <%--<meta name="layout" content="resolve" />   declared in the controller--%>

        <title>Identifiers.org [${record.requestedUri}]</title>
    </head>
    <body>
        <div id="header">
            <h1>${record.requestedUri}</h1>
            <div class="info">
                <g:if test="${record.dataCollection.resources.size() > 1}">
                    <b>${record.dataCollection.resources.size()}</b> physical locations (or resources) are available for accessing <span class="entity">${record.entityId}</span> (from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>):
                </g:if>
                <g:else>
                    <b>${record.dataCollection.resources.size()}</b> physical location (or resource) is available for accessing <span class="entity">${record.entityId}</span> (from <a href="http://www.ebi.ac.uk/miriam/main/datatypes/${record.dataCollection.id}" title="Access information about: ${record.dataCollection.name}">${record.dataCollection.name}</a>):
                </g:else>
            </div>
        </div>

        <div id="content">

            <%-- TODO: remove the following div once resolver stable
            <div id="beta">beta</div>
            --%>
            
            <g:each in="${record.messages}" var="message">
                <div class="feedback_user">
                    <div class="feedback_summary">${message.summary}</div><div class="feedback_description">${message.description}</div>
                </div>
            </g:each>

            <table class="resources">
                <g:each in="${0..record.dataCollection.resources.size()-1}" var="counter">
                    <g:if test="${(counter % 2) == 0}">
                       <tr>
                    </g:if>

                    <td align="center">
                      <div class="resource">
                        <div class="resource_overview"></div>
                        <div class="resource_info">
                          <a href="${record.dataCollection.resources[counter].urls[0].link.encodeAsHTML()}" title="Access to '${record.entityId}' via this resource (${record.dataCollection.resources[counter].id})">
                            <span class="desc">${record.dataCollection.resources[counter].description.encodeAsHTML()}</span>
                            <span class="institution">${record.dataCollection.resources[counter].institution.encodeAsHTML()}</span>
                            <span class="country">${record.dataCollection.resources[counter].location.encodeAsHTML()}</span>
                            <g:if test="${record.dataCollection.resources[counter].reliability == 0}">
                                <span class="status">(Uptime: <i>unknown</i>)</span>
                            </g:if>
                            <g:else>
                                <span class="status">(Uptime: ${record.dataCollection.resources[counter].reliability}%)</span>
                            </g:else>
                          </a>
                        </div>
                      </div>
                    </td>

                    <g:if test="${(counter % 2) || (counter == record.dataCollection.resources.size()-1)}">
                        </tr>
                    </g:if>
                </g:each>
            </table>
          </div>


          <div id="footer">
            <div class="powered">Powered by <a href="http://identifiers.org/registry/" title="Go to: MIRIAM Registry">MIRIAM Registry</a></div>
            <div class="formats">Information also available in: <a href="${record.requestedUri}?format=rdfxml" title="Information in RDF">RDF</a></div>
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
