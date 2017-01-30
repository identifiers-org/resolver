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
%{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'registry.css', base: '//static.identifiers.org/')}" type="text/css" />--}%
<link rel="stylesheet" href="${resource(dir: 'css', file: 'registry.css')}" type="text/css" />

<section>

    <div id="main-content-area" class="row">
        <div class="small-12 columns">
            <h2 class='icon icon-generic' data-icon='D'>&nbsp;&nbsp;Registry</h2>
            <p>${registry.message}</p>
        </div>

        %{--to work with facets--}%
      %{--  <div class="small-12 medium-9 medium-push-3 columns">--}%



        <div class="small-12 medium-12 columns">

            <table class="hover">
                <tbody>
                <g:each in="${registry.results}">
                <tr>
                    <td>

                        <g:if test="${it.type == uk.ac.ebi.miriam.common.RegistryResult.ResourceType.COLLECTION}">
                            <h4>
                                <b><a href="${it.link}" >${it.name}</a></b>&nbsp;&nbsp;${it.synonyms}
                            </h4>

                            ${it.description}</br>
                            <b>Namespace:</b> ${it.prefix}</br>
                            <b>URI:</b> <a href="${it.link}" >${it.link}</a>
                        </g:if>
                        <g:else>
                            <h4 class="icon icon-generic" data-icon="R">
                                <b><a href="${it.link}" >${it.name}</a></b>
                            </h4>
                            <g:if test="${it.prefix!=null && !it.prefix.empty}">
                                <b>Provider_code:</b> ${it.prefix}</br>
                            </g:if>
                            <g:if test="${it.primary}">
                                <b>Primary:</b> ${it.primary}</br>
                            </g:if>
                            <b>Home:</b> <a href="${it.homepage}">${it.homepage}</a></br>
                            <b>Institution:</b> ${it.institute}</br>
                            <b>Location:</b> ${it.location}</br>
                            <b>Example:</b> <a href="${it.idorglink}" >${it.idorglink}</a></br>
                            <b>Uptime:</b> ${it.upTime}%
                        </g:else>

                    </td>
                </tr>
                </g:each>

                </tbody>
            </table>

        <g:paginate controller="registry" params="${[query:registry.query]}" action="index" total="${registry.hitcount}"/>

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