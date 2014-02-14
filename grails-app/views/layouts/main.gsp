<%--
  Template for most of the pages
  Camille Laibe
  20111125
--%>

<!doctype html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<!--<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">-->
		<title><g:layoutTitle default="MIRIAM Resolver"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico', base: 'http://static.identifiers.org/')}" type="image/x-icon" />
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css', base: 'http://static.identifiers.org/')}" type="text/css" />
		<!--<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css', base: 'http://static.identifiers.org/')}" type="text/css">-->
		<g:layoutHead/>
	</head>
	<body>
        <div id="top_page" role="banner">
            <span style="position:absolute; top:13px; left:10px;">
                <a href="http://identifiers.org/"><img src="${resource(dir: 'images', file: 'identifiers-org_logo-small.png', base: 'http://static.identifiers.org/')}" alt="Identifiers.org logo"/></a>
            </span>
            <%-- menu --%>
            <div class="nav"  style="position:absolute; top:0px; right:10px;">
                <ul>
                    <li><span class="menuButton"><g:link controller="info" action="intro" title="Identifiers.org">Home</g:link></span></li>
                    <li><span class="menuButton"><g:link controller="info" action="news" title="News">News</g:link></span></li>
                    <li><span class="menuButton"><g:link controller="info" action="help" title="Help">Help</g:link></span></li>
                    <li><span class="menuButton"><g:link controller="info" action="examples" title="Examples">Examples</g:link></span></li>
                    <li><span class="menuButton"><a href="http://identifiers.org/registry/" title="MIRIAM Registry">Registry</a></span></li>
                    <li><span class="menuButton"><g:link controller="info" action="about" title="About">About</g:link></span></li>
                </ul>
            </div>
        </div>

		<g:layoutBody />

		<!--<div class="footer" role="contentinfo"></div>-->
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;" /></div>
		<g:javascript library="application" />
	</body>
</html>