<%--
  Template for most of the pages
  Camille Laibe
  20111125
  Modified: Sarala Wimalaratne
--%>

<!doctype html>
<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"><!--<![endif]-->
<head>
    <meta charset="utf-8">

    <title>Identifiers.org &lt; EMBL-EBI</title>
    <meta name="description" content="EMBL-EBI"><!-- Describe what this page is about -->
    <meta name="keywords" content="bioinformatics, europe, institute"><!-- A few keywords that relate to the content of THIS PAGE (not the whol project) -->
    <meta name="author" content="EMBL-EBI"><!-- Your [project-name] here -->
    <meta name="ebi:localmasthead-image" content="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/images/backgrounds/embl-ebi-background.jpg" />
    %{--<meta name="ebi:localmasthead-color" content="#000000" />--}%

<!-- Mobile viewport optimized: j.mp/bplateviewport -->
    <meta name="viewport" content="width=device-width,initial-scale=1">

    <!-- Place favicon.ico and apple-touch-icon.png in the root directory: mathiasbynens.be/notes/touch-icons -->


    %{-- <link rel="stylesheet" href="${resource(dir: 'css', file: 'static.css')}" type="text/css" />--}%
%{--
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'static.css', base: '//static.identifiers.org/')}" type="text/css"/>
--}%

    <style type="text/css">
    /* You have the option of setting a maximum width for your page, and making sure everything is centered */
    /* body { max-width: 1600px; margin: 0 auto; } */
    </style>

    <!-- end CSS-->

    <!-- custom build (lacks most of the "advanced" HTML5 support -->
    <script src="//www.ebi.ac.uk/web_guidelines/js/libs/modernizr.custom.49274.js"></script>
    <link rel='shortcut icon' href='//static.identifiers.org/images/favicon.ico' type='image/x-icon'/>


    <script type="text/javascript" charset="utf-8" async="" src="https://platform.twitter.com/js/timeline.f8bf188a26c0fb191f8cdd1eb88ad3c7.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

    <link rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/libraries/foundation-6/css/foundation.css" type="text/css">
    <link rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/css/ebi-global.css" type="text/css">
    <link rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/EBI-Icon-fonts/v1.1/fonts.css" type="text/css">
    <link rel="stylesheet" href="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/css/theme-ebi-services-about.css" type="text/css">

    <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/js/cookiebanner.js"></script>
    <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/js/foot.js"></script>
    <script defer="defer" src="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/js/script.js"></script>
    <script src="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/libraries/foundation-6/js/foundation.js"></script>
    <script src="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/js/foundationExtendEBI.js"></script>

</head>

<body class="level2"><!-- add any of your classes or IDs -->
<div id="skip-to">
    <ul>
        <li><a href="#content">Skip to main content</a></li>
        <li><a href="#local-nav">Skip to local navigation</a></li>
        <li><a href="#global-nav">Skip to EBI global navigation menu</a></li>
        <li><a href="#global-nav-expanded">Skip to expanded EBI global navigation menu (includes all sub-sections)</a>
        </li>
    </ul>
</div>



        <div data-sticky-container="">
            <div id="local-masthead" class="meta-background-image" data-sticky="true"
                                           data-sticky-on="large" data-top-anchor="180" data-btm-anchor="300000">
                <header>
                <div id="global-masthead" class="clearfix">

                <!--This has to be one line and no newline characters-->
                <a href="//www.ebi.ac.uk/" title="Go to the EMBL-EBI homepage"><span class="ebi-logo"></span></a>

                <nav>
                    <div class="row">
                        <ul id="global-nav" class="menu">
                            <!-- set active class as appropriate -->
                            <li id="home-mobile" class=""><a href="//www.ebi.ac.uk"></a></li>
                            <li id="home" class="active"><a href="//www.ebi.ac.uk"><i class="icon icon-generic" data-icon="H"></i> EMBL-EBI</a></li>
                            <li id="services"><a href="//www.ebi.ac.uk/services"><i class="icon icon-generic" data-icon="("></i> Services</a></li>
                            <li id="research"><a href="//www.ebi.ac.uk/research"><i class="icon icon-generic" data-icon=")"></i> Research</a></li>
                            <li id="training"><a href="//www.ebi.ac.uk/training"><i class="icon icon-generic" data-icon="t"></i> Training</a></li>
                            <li id="about"><a href="//www.ebi.ac.uk/about"><i class="icon icon-generic" data-icon="i"></i> About us</a></li>
                            <li id="search">
                                <a href="#" data-toggle="search-global-dropdown"><i class="icon icon-functional" data-icon="1"></i>
                                    <span class="show-for-small-only">Search</span></a>
                                <div id="search-global-dropdown" class="dropdown-pane" data-dropdown="" data-options="closeOnClick:true;">
                                    <form id="global-search" name="global-search" action="/ebisearch/search.ebi" method="GET">
                                        <fieldset>
                                            <div class="input-group">
                                                <input type="text" name="query" id="global-searchbox" class="input-group-field" placeholder="Search all of EMBL-EBI">

                                                <div class="input-group-button">
                                                    <input type="submit" name="submit" value="Search" class="button">
                                                    <input type="hidden" name="db" value="allebi" checked="checked">
                                                    <input type="hidden" name="requestFrom" value="global-masthead" checked="checked">
                                                </div>
                                            </div>
                                        </fieldset>
                                    </form>
                                </div>
                            </li>
                            <li class="float-right show-for-medium embl-selector">
                                <button class="button" type="button" data-toggle="embl-dropdown">Hinxton</button>

                                <div id="embl-dropdown" class="dropdown-pane" data-dropdown="" data-options="closeOnClick:true;">
                                    to come.
                                </div>
                            </li>
                        </ul>
                    </div>
                </nav>

            </div>



            <!-- local-title -->
            <!-- NB: for additional title style patterns, see http://frontier.ebi.ac.uk/web/style/patterns -->

            <div class="masthead row"><div class="columns medium-6" id="local-title">
                <h1><a href="//identifiers.org/"><img
                        src="${resource(dir: 'images', file: 'identifiers-org_logo-new.png', base: '//static.identifiers.org/')}"
                        alt="Identifiers.org logo" height="80" width="80" style="padding-right: 12px;"/>Identifiers.org
                </a></h1>

            </div>

                <div class="columns medium-6  last">
                    <g:form id="local-search" name="local-search" controller="registry" action="index"
                          method="get">
                        <fieldset>
                            <div class="input-group">
                                <label>
                                    <g:if test="${registry!=null}">
                                        <g:textField name="query" value="${registry.query}"/>
                                    </g:if>
                                    <g:else>
                                        <g:textField name="query"/>
                                    </g:else>

                                    <p class="examples">Examples:
                                            <g:link controller="registry" action="index" params="[query:'ontology']" >ontology</g:link>,
                                            <g:link controller="registry" action="index" params="[query:'enzyme']" >enzyme</g:link>,
                                            <g:link controller="registry" action="index" params="[query:'EMBL']" >EMBL</g:link>,
                                            <g:link controller="registry" action="index" params="[query:'Japan']" >Japan</g:link>
                                </p>
                                </label>

                                <div class="input-group-button"><input type="submit" value="Search"
                                                                       class="submit button secondary">

                                </div>
                            </div>
                        </fieldset>
                    </g:form>

                    %{--<form id="local-search" name="local-search" action="//www.ebi.ac.uk/miriam/main/search"
                          method="get">
                        <fieldset>
                            <div class="input-group">
                                <label>
                                    <input type="text" name="query" id="local-searchbox">

                                    <p class="examples">Examples: <a
                                            href="//www.ebi.ac.uk/miriam/main/search?query=ontology"
                                            title="Search for 'ontology'">ontology</a>, <a
                                            href="//www.ebi.ac.uk/miriam/main/search?query=enzyme"
                                            title="Search for 'enzyme'">enzyme</a>, <a
                                            href="//www.ebi.ac.uk/miriam/main/search?query=Japan"
                                            title="Search for 'Japan'">Japan</a>, <a
                                            href="//www.ebi.ac.uk/miriam/main/search?query=EMBL"
                                            title="Search for 'EMBL'">EMBL</a></p>
                                </label>

                                <div class="input-group-button"><input type="submit" value="Search"
                                                                       class="submit button secondary">

                                    <div class="small">
                                        <!-- name="submit"  -->
                                        <!-- some example of search terms -->
                                        <span class="adv"><a href="//www.ebi.ac.uk/miriam/main/tags/" id="adv-search"
                                                             title="Search using types of data">Categories&nbsp;&amp;&nbsp;tags</a>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                    </form>--}%
                </div>

                <nav>
                    %{--<ul class="grid_24 dropdown menu float-left columns medium-12" id="local-nav" data-dropdown-menu="true">--}%
                <ul class="grid_24 menu float-left columns medium-12" id="local-nav" data-dropdown-menu="true">
                    <li class="first"><g:link controller="general" action="intro" title="Identifiers.org">Home</g:link></li>
                    <li><g:link controller="general" action="documentation" title="Documentation">Documentation</g:link>
                    <li><g:link controller="service" action="index" title="Services">Services</g:link>
                    <li class="last"><g:link controller="general" action="about" title="About">About</g:link></li>
                    <!-- If you need to include functional (as opposed to purely navigational) links in your local menu,
               add them here, and give them a class of "functional". Remember: you'll need a class of "last" for
               whichever one will show up last...
               For example: -->
                    %{--          <li class="functional last"><a href="#" class="icon icon-functional" data-icon="l">Login</a></li>
                              <li class="functional"><a href="#" class="icon icon-generic" data-icon="\">Feedback</a></li>
                              <li class="functional"><a href="#" class="icon icon-functional" data-icon="r">Share</a></li>--}%
                    <li class="menu-412 menu-feedback functional float-right" id="feedback"><a href="//www.ebi.ac.uk/support/identifiers.org" target="_blank" class="icon icon-generic" data-icon="\">Feedback</a></li>
                    <li class="float-right"><a href="//identifiers.org/request/prefix" target="_blank" class="icon icon-functional" data-icon="D">Request Prefix</a></li>

                </ul>

            </nav>
            </div>



    </header>
        </div>
        </div>
    <div id="content" role="main" class="row">

        <g:layoutBody/>
        %{--<g:javascript library="application" />--}%

    </div>


    <footer>
        <!-- Optional local footer (insert citation / project-specific copyright / etc here -->
        <!--
      <div id="local-footer">
        <div class="row">
          <span class="reference">How to reference this page: ...</span>
        </div>
      </div>
 -->
        <!-- End optional local footer -->

        <div id="global-footer">

            <nav id="global-nav-expanded" class="row">
                <!-- Footer will be automatically inserted by footer.js -->
            </nav>

            <section id="ebi-footer-meta" class="row">
                <!-- Footer meta will be automatically inserted by footer.js -->
            </section>

        </div>

    </footer>


<!-- JavaScript at the bottom for fast page loading -->

<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if offline -->
<!--
  <script src="//ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
  <script>window.jQuery || document.write('<script src="../js/libs/jquery-1.6.2.min.js"><\/script>')</script>
  -->

<!-- JavaScript at the bottom for fast page loading -->
<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if offline -->

<script defer="defer" src="//www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/js/cookiebanner.js"></script>
<script defer="defer" src="https://www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/js/foot.js"></script>
<script defer="defer" src="https://www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/js/script.js"></script>

<!-- The Foundation theme JavaScript -->
<script src="https://www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/libraries/foundation-6/js/foundation.js"></script>
<script src="https://www.ebi.ac.uk/web_guidelines/EBI-Framework/v1.1/js/foundationExtendEBI.js"></script>
<script type="text/JavaScript">$(document).foundation();</script>
<script type="text/JavaScript">$(document).foundationExtendEBI();</script>



<!-- Google Analytics details... -->
<!-- Change UA-XXXXX-X to be your site's ID -->
<!--
  <script>
    window._gaq = [['_setAccount','UAXXXXXXXX1'],['_trackPageview'],['_trackPageLoadTime']];
    Modernizr.load({
      load: ('https:' == location.protocol ? '//ssl' : '//www') + '.google-analytics.com/ga.js'
    });
  </script>
  -->


<!-- Prompt IE 6 users to install Chrome Frame. Remove this if you want to support IE 6.
       chromium.org/developers/how-tos/chrome-frame-getting-started -->
<!--[if lt IE 7 ]>
    <script src="//ajax.googleapis.com/ajax/libs/chrome-frame/1.0.3/CFInstall.min.js"></script>
    <script>window.attachEvent('onload',function(){CFInstall.check({mode:'overlay'})})</script>
  <![endif]-->

</body>
</html>


</body>
</html>
