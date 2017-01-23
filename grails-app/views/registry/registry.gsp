<%--
  Created by IntelliJ IDEA.
  User: sarala
  Date: 18/01/2017
  Time: 13:51
--%>

<%@ page import="uk.ac.ebi.miriam.common.Resource; uk.ac.ebi.miriam.common.DataCollection" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Identifiers.org | Registry</title>
</head>

<body>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.0/themes/base/jquery-ui.css">
<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
<script src="https://code.jquery.com/ui/1.12.0/jquery-ui.js"></script>
%{--<script type="text/javascript" src="${resource(dir: 'js', file: 'registry.js', base: '//static.identifiers.org/')}"></script>--}%
<script type="text/javascript" src="${resource(dir: 'js', file: 'registry.js')}"></script>

<section>

    <div id="main-content-area" class="row">
        <div class="small-12 columns">
            <h2 class='icon icon-generic' data-icon='D'>&nbsp;&nbsp;Registry</h2>
        </div>

        %{--to work with facets--}%
      %{--  <div class="small-12 medium-9 medium-push-3 columns">--}%

        <div class="small-12 medium-12 columns">

%{--            <ul class="pagination" role="navigation" aria-label="Pagination">
                <li class="pagination-previous disabled">Previous <span class="show-for-sr">page</span></li>
                <li class="current"><span class="show-for-sr">You're on page</span> 1</li>
                <li><a href="#" aria-label="Page 2">2</a></li>
                <li><a href="#" aria-label="Page 3">3</a></li>
                <li><a href="#" aria-label="Page 4">4</a></li>
                <li class="ellipsis" aria-hidden="true"></li>
                <li><a href="#" aria-label="Page 12">12</a></li>
                <li><a href="#" aria-label="Page 13">13</a></li>
                <li class="pagination-next"><a href="#" aria-label="Next page">Next <span class="show-for-sr">page</span></a></li>
            </ul>--}%

            <g:paginate controller="registry" action="index" total="${registry.results.size()}"/>

            <table class="hover">
                <tbody>
                <g:each in="${registry.results}">
                <tr>
                    <td>

                        <g:if test="${it.type == uk.ac.ebi.miriam.common.RegistryResult.ResourceType.COLLECTION}">
                            <h4>
                                <a href="${it.link}" >${it.name}</a>
                            </h4>
                            ${it.description}</br>
                            <b>namespace:</b> ${it.prefix}
                        </g:if>
                        <g:else>
                            <h4 class="icon icon-generic" data-icon="R">
                                <a href="${it.link}" >${it.name}</a>
                            </h4>
                            <g:if test="${!it.prefix.empty}">
                                <b>provider_code:</b> ${it.prefix}</br>
                            </g:if>
                            <g:if test="${it.primary}">
                                <b>primary:</b> ${it.primary}</br>
                            </g:if>
                            <b>home:</b> <a href="${it.homepage}">${it.homepage}</a></br>
                            <b>uptime:</b> ${it.upTime}%
                        </g:else>

                    </td>
                </tr>
                </g:each>

                </tbody>
            </table>


        </div> <!-- /medium-7 -->

       %{-- <div class="small-12 medium-3 medium-pull-9 columns">

            <section>
                <h5>Facets</h5>
                <div class="">
                    <g:form name="myForm" action="/registry/filter" id="1">
                        <g:checkBox name="myCheckbox" value="${true}" onChange="document.getElementById('myForm').submit();"/>
                    </g:form>
                    --}%%{--<ul class="menu vertical">
                        <li><a href="#">Facet One</a></li>
                        <li><a href="#">Facet Two</a></li>
                        <ul class="nested menu vertical">
                            <li><a href="#">Sub-facet One</a></li>
                            <li><a href="#">Sub-facet Two</a></li>
                            <li><a href="#">Sub-facet Three</a></li>
                            <li><a href="#">Sub-facet Four</a></li>
                        </ul>
                        <li><a href="#">Facet Three</a></li>
                        <li><a href="#">Facet Four</a></li>
                    </ul>--}%%{--
                </div>
            </section>

        </div> <!-- /medium-7 -->
--}%
    </div>

</section>
</body>
</html>