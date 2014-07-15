grails create-app resolver
cd resolver

grails create-resource UriRecord


Content negotiation:
  http://grails.org/doc/latest/guide/single.html#6.8%20Content%20Negotiation

How to ask for a specific type:
  curl -D - -L -H   "Accept: application/rdf+xml" "http://localhost:8080/resolver/uniprot/P12345"
  curl URL
  curl --verbose URL

Interactive command
  grails interactive

Run the app
  grails run-app

Run test app with local database
   grails dev run-app

Testing
  grails test-app
  grails test-app -clean

Upgrade to newer Grails version
  grails clean
  grails upgrade

Generate war for use in production (at the EBI)
  grails prod war

Generate war for use in London Data Centres
  grails -Dgrails.env=ldc war

Grails docs
  http://grails.org/doc/2.1.0/


Functional Testing
  http://grails.org/plugin/functional-test
  grails test-app -functional


It seems there are some issue with installing the JQuery plugin with 1.4M1, so I manually added the JS file and included in the template


TESTS for:
http://localhost:8080/resolver/ec-code/1.1.1.1
http://localhost:8080/resolver/obo.chebi/CHEBI%3A36927
http://localhost:8080/resolver/obo.chebi/CHEBI:36927

Known issues:
- previously most of the pages are not displayed in IE8
  - this came from the fact that a 303 See Other HTTP code was used
  - now uses "203 Non-Authoritative Information" for namespaces (no full URL)
  - now uses "300 Multiple Choices" for displaying the list of possible resources
