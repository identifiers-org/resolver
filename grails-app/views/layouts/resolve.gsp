<?xml version="1.0" encoding="UTF-8" ?>

<%--
  Template for the resolving page ("Transitional" as the page uses iframes)
  Camille Laibe
  20120823
  Modified: Sarala Wimalaratne
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico', base: 'http://static.identifiers.org/')}" type="image/x-icon" />
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'resolve.css')}" type="text/css" />
		%{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'resolve.css', base: 'http://static.identifiers.org/')}" type="text/css" />--}%
        <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-1.8.0.min.js', base: 'http://static.identifiers.org/')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'resolver.js', base: 'http://static.identifiers.org/')}"></script>

        <title><g:layoutTitle default="MIRIAM Resolver"/></title>

		<g:layoutHead/>
	</head>

	<body>
        <g:layoutBody />
	</body>

</html>