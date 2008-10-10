MIRIAM Web Application


PROBLEMS:

- change name of the methods in the list (request part) ("get links to data", "get links to resource", for example)
- reorder the name of the methods (with indentation for subclasses)

- add an error message in the WSI if the web services doesn't answer

- validation of the ID in the request interface
- 
- use MiriamLib in the package ebi.ac.uk.wsi
- generate the list of methods (request part) automatically
- access to web services via "http://web62-node2:9951/compneur-srv/miriamws-alpha" without the 'compneur-srv'
- in the request form, validation of the ID
- 


TODO:

- think about the "getDataEntries()" method with only one String in parameter,
		and a method to cut the URI in two part (data-type and element identifier)
- add a search engine to the web application
- fix the bugs
- use Spring
- use ZK for the AJAX part (http://www.zkoss.org/)
- take some holiday
-

ISSUES:
- links to images not fully hard coded, EXCEPT in the static pages (javascript code and html)
- 

		
THINGS TO CHANGE BETWEEN THE DIFFERENT VERSIONS:

* CSS style sheets
		the one for the the SID version contains a "WARNING" image on the top of the page
* each .jsp
		change the version name in the "<h1>" tage (no version for the MAIN version)
* static_html
		- compneur_menu.html
		- web_services.html
* MiriamDynamicForms.js
		address of the images (Delete.gif)


FILES:

- template_*.jspf: 


SOLVED:

- UTF-8 between the submit form and the display/email/database
		NOW you can enter special characters
- password reminder
		NOW added
- uk.ac.ebi.miriam.wsi.MiriamLink: endpoint of the WebServices
		NOW changed: constructor with the address in parameter, and endPointAddress in the 'web.xml' file
- uk.ac.ebi.miriam.web.ServletDataTypeAdd: when sending the email, change to body with the version of the app
		NOW changed: "email" and "version" are in the web.xml file
