// author: Camille Laibe
// version: 20070112
// copyright: EMBL-EBI, Computational Neurobiology Group
//
// functions for dynamic html forms (submission of new elements)


// resets the differnt values of the counters of the page
function raz()
{
	document.getElementById("synonymsCounter").value = 0;
	document.getElementById("synonymsCounterReal").value = 0;
	document.getElementById("deprecatedCounter").value = 0;
	document.getElementById("deprecatedCounterReal").value = 0;
	document.getElementById("resourcesCounter").value = 1;
	document.getElementById("resourcesCounterReal").value = 1;
	document.getElementById("docCounter").value = 0;
	document.getElementById("docCounterReal").value = 0;
}

// resets the differnt values of the counters of the page (in edit mode)
function razEdit()
{
	document.getElementById("synonymsCounter").value = document.getElementById("synonymsCounterReal").value;
	document.getElementById("deprecatedCounter").value = document.getElementById("deprecatedCounterReal").value;
	document.getElementById("resourcesCounter").value = document.getElementById("resourcesCounterReal").value;
	document.getElementById("docCounter").value = document.getElementById("docCounterReal").value;
}

// creates a div with a field for a new synonym
function addSynonym()
{
	// updates the normal counter (the one which never decreases)
	var numi = document.getElementById('synonymsCounter');
	var num = (document.getElementById("synonymsCounter").value -1)+ 2;
	numi.value = num;
	// updates the real counter (the one which represents the real number of (existing) items
	var numi = document.getElementById('synonymsCounterReal');
	var numReal = (document.getElementById("synonymsCounterReal").value -1)+ 2;
	numi.value = numReal;
	// creation of a new div
	var IdName = "synonym"+num+"Div";
	var newdiv = document.createElement('div');
	newdiv.setAttribute("id",IdName);
	newdiv.innerHTML = "\nSynonym:&nbsp;<input type=\"text\" name=\"synonym" + num + "\" size=\"45\" /> &nbsp; <a href=\"javascript:;\" title=\"Remove this synonym\" onclick=\"removeSynonym(\'" + IdName + "')\"><img src=\"http://www.ebi.ac.uk/compneur-srv/miriam/demo/img/Delete.gif\" alt=\"Delete logo\" height=\"16\" width=\"16\" /></a>\n";
	var ni = document.getElementById('synonyms_id');
	ni.appendChild(newdiv);
	// change the link, if necessary (already one synonym in the form)
	if (numReal == 1)
	{
		changeSynonymLink();
	}
}

// removes a div with a field for a new synonym
function removeSynonym(IdName)
{
	// removes the div
	var d = document.getElementById('synonyms_id');
	var olddiv = document.getElementById(IdName);
	d.removeChild(olddiv);
	// update the real counter (the one which represents the real number of (existing) items
	var numi = document.getElementById('synonymsCounterReal');
	var numReal = (document.getElementById("synonymsCounterReal").value -2)+ 1;
	numi.value = numReal;
	// change the link, if necessary (no more synonym in the form)
	if (numReal == 0)
	{
		reChangeSynonymLink();
	}
}

// changes the legend of the button to add a new synonym (already one synonym in the form)
function changeSynonymLink()
{
	var d = document.getElementById('add_synonym_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add another synonym\" onclick=\"addSynonym();\">[Add another synonym]</a>";
}

// changes the legend of the button to add a new synonym (no synonym in the form)
function reChangeSynonymLink()
{
	var d = document.getElementById('add_synonym_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add a synonym\" onclick=\"addSynonym();\">[Add a synonym]</a>";
}

// creates a div with a field for a deprecated URI
function addDeprecated()
{
	// updates the normal counter (the one which never decreases)
	var numi = document.getElementById('deprecatedCounter');
	var num = (document.getElementById("deprecatedCounter").value -1)+ 2;
	numi.value = num;
	// updates the real counter (the one which represents the real number of (existing) items
	var numi = document.getElementById('deprecatedCounterReal');
	var numReal = (document.getElementById("deprecatedCounterReal").value -1)+ 2;
	numi.value = numReal;
	// creation of a new div
	var IdName = "deprecated"+num+"Div";
	var newdiv = document.createElement('div');
	newdiv.setAttribute("id",IdName);
	newdiv.innerHTML = "\nDeprecated URI:&nbsp;<input type=\"text\" name=\"deprecated" + num + "\" size=\"45\" /> &nbsp; <a href=\"javascript:;\" title=\"Remove this deprecated URI\" onclick=\"removeDeprecated(\'" + IdName + "\')\"><img src=\"http://www.ebi.ac.uk/compneur-srv/miriam/demo/img/Delete.gif\" alt=\"Delete logo\" height=\"16\" width=\"16\" /></a>\n";
	var ni = document.getElementById('deprecated_id');
	ni.appendChild(newdiv);
	// change the link, if necessary (already one deprectaed URI in the form)
	if (numReal == 1)
	{
		changeDeprecatedLink();
	}
}

// removes a div with a field for a deprecated URI
function removeDeprecated(IdName)
{
	// removes the div
	var d = document.getElementById('deprecated_id');
	var olddiv = document.getElementById(IdName);
	d.removeChild(olddiv);
	// update the real counter (the one which represents the real number of (existing) items
	var numi = document.getElementById('deprecatedCounterReal');
	var numReal = (document.getElementById("deprecatedCounterReal").value -2)+ 1;
	numi.value = numReal;
	// change the link, if necessary (no more deprectaed URI in the form)
	if (numReal == 0)
	{
		reChangeDeprecatedLink();
	}
}

// changes the legend of the button to add a deprecated URI (already one deprecated URI in the form)
function changeDeprecatedLink()
{
	var d = document.getElementById('add_deprecated_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add another deprecated URI\" onclick=\"addDeprecated();\">[Add another deprecated URI]</a>";
}

// changes the legend of the button to add a deprecated URI (no deprecated URI in the form)
function reChangeDeprecatedLink()
{
	var d = document.getElementById('add_deprecated_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add a deprecated URI\" onclick=\"addDeprecated();\">[Add a deprecated URI]</a>";
}

// creates a div with a field for a new resource
function addResource()
{
	// updates the normal counter (the one which never decreases)
	var numi = document.getElementById('resourcesCounter');
	var num = (document.getElementById("resourcesCounter").value -1)+ 2;
	numi.value = num;
	// updates the real counter (the one which represents the real number of (existing) items
	var numi = document.getElementById('resourcesCounterReal');
	var numReal = (document.getElementById("resourcesCounterReal").value -1)+ 2;
	numi.value = numReal;
	// creation of a new div
	var IdName = "resources"+num+"Div";
	var newdiv = document.createElement('div');
	newdiv.setAttribute("id",IdName);
	//newdiv.innerHTML = "\n<input type=\"hidden\" value=\"null\" name=\"resourceId" + num + "\" id=\"resourceId" + num + "\" />\n<table><tr><td rowspan=\"2\"><b>#" + num + ":&nbsp;</b></td><td>Data entry:&nbsp;</td><td><input type=\"text\" name=\"dataEntryPrefix" + num + "\" size=\"34\" id=\"depId" + num + "\" /></td><td><b>$id</b></td><td><input type=\"text\" name=\"dataEntrySuffix" + num +"\" size=\"15\" id=\"desId" + num + "\" /></td><td rowspan=\"2\">&nbsp;<a href=\"javascript:;\" title=\"Remove this resource\" onclick=\"removeResource(\'" + IdName + "\')\"><img src=\"http://www.ebi.ac.uk/~laibe/miriam/sid/img/Delete.gif\" alt=\"Delete logo\" height=\"16\" width=\"16\" /></a></td></tr><tr><td>Data resource:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"dataResource" + num + "\" size=\"45\" id=\"drId" + num + "\" /></td></tr></table>\n";
	newdiv.innerHTML = "\n<input type=\"hidden\" value=\"null\" name=\"resourceId" + num + "\" id=\"resourceId" + num + "\" />\n<table class=\"resources\"><tr><td rowspan=\"3\" valign=\"bottom\"><b>#" + num + ":&nbsp;</b></td><td>Data entry:&nbsp;</td><td><input type=\"text\" name=\"dataEntryPrefix" + num + "\" size=\"34\" id=\"depId" + num + "\" /></td><td><b>$id</b></td><td><input type=\"text\" name=\"dataEntrySuffix" + num +"\" size=\"15\" id=\"desId" + num + "\" /></td></tr><tr><td>Data resource:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"dataResource" + num + "\" size=\"45\" id=\"drId" + num + "\" /></td></tr><tr><td>Information:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"information" + num + "\" size=\"55\" id=\"infoId" + num + "\" /></td></tr><tr><td rowspan=\"2\" valign=\"top\"><a href=\"javascript:;\" title=\"Remove this resource\" onclick=\"removeResource(\'" + IdName + "\')\"><img src=\"http://www.ebi.ac.uk/compneur-srv/miriam/demo/img/Delete.gif\" alt=\"Delete logo\" height=\"16\" width=\"16\" /></a></td><td>Institution:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"institution" + num + "\" size=\"45\" id=\"instituteId" + num + "\" /></td></tr><tr><td>Country:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"country" + num + "\" size=\"30\" id=\"countryId" + num + "\" /></td></tr></table>\n";
	var ni = document.getElementById('resources_id');
	ni.appendChild(newdiv);
	// change the link, if necessary (no more  URI in the form)
	if (numReal == 2)
	{
		changeResourcesLink();
	}
}

// removes a div with a field for a resource
function removeResource(IdName)
{
	// removes the div
	var d = document.getElementById('resources_id');
	var olddiv = document.getElementById(IdName);
	d.removeChild(olddiv);
	// update the real counter (the one which represents the real number of (existing) items
	var numi = document.getElementById('resourcesCounterReal');
	var numReal = (document.getElementById("resourcesCounterReal").value -2)+ 1;
	numi.value = numReal;
	// change the link, if necessary (no more deprectaed URI in the form)
	if (numReal == 1)
	{
		reChangeResourcesLink();
	}
}

// changes the legend of the button to add a resource (already one resource in the form)
function changeResourcesLink()
{
	var d = document.getElementById('add_resources_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add another resource\" onclick=\"addResource();\">[Add another resource]</a>";
}

// changes the legend of the button to add a resource (no resource in the form)
function reChangeResourcesLink()
{
	var d = document.getElementById('add_resources_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add a resource\" onclick=\"addResource();\">[Add a resource]</a>";
}

// creates a div with a field for a new documentation
function addDoc()
{
	// updates the normal counter (the one which never decreases)
	var numi = document.getElementById('docCounter');
	var num = (document.getElementById("docCounter").value -1)+ 2;
	numi.value = num;
	// updates the real counter (the one which represents the real number of (existing) items
	var numi = document.getElementById('docCounterReal');
	var numReal = (document.getElementById("docCounterReal").value -1)+ 2;
	numi.value = numReal;
	// creation of a new div
	var IdName = "docSubmitForm" + num;
	var newdiv = document.createElement('div');
	newdiv.setAttribute("id", IdName);
	newdiv.setAttribute("class", "docSubmitForm");
	newdiv.innerHTML = "\n<div><a href=\"javascript:;\" title=\"Remove this documentation\" onclick=\"removeDoc(\'" + IdName + "\')\"><img src=\"http://www.ebi.ac.uk/compneur-srv/miriam/demo/img/Delete.gif\" alt=\"Delete logo\" height=\"16\" width=\"16\" /></a>&nbsp;<span id=\"docTypeBox" + num + "\">Type: MIRIAM ID: <input type=\"radio\" name=\"docType" + num + "\" value=\"MIRIAM\" onclick=\" return displayMiriamForm('docForm" + num + "', " + num + ");\" />Physical URL: <input type=\"radio\" name=\"docType" + num + "\" value=\"URL\" onclick=\"return displayUrlForm('docForm" + num + "', " + num + ");\" /></span></div><div id=\"docForm" + num + "\" class=\"indentDiv\"><em>First, select a type of documentation...</em></div>\n";
	var ni = document.getElementById('doc_id');
	ni.appendChild(newdiv);
	// change the link, if necessary (no more  URI in the form)
	if (numReal == 1)
	{
		changeDocLink();
	}
}

// removes a div with a field for a documentation
function removeDoc(IdName)
{
	// removes the div
	var d = document.getElementById('doc_id');
	var olddiv = document.getElementById(IdName);
	d.removeChild(olddiv);
	// update the real counter (the one which represents the real number of (existing) items
	var numi = document.getElementById('docCounterReal');
	var numReal = (document.getElementById("docCounterReal").value -2)+ 1;
	numi.value = numReal;
	// change the link, if necessary (no more deprectaed URI in the form)
	if (numReal == 0)
	{
		reChangeDocLink();
	}
}

// changes the legend of the button to add a documentation (already one documentation in the form)
function changeDocLink()
{
	var d = document.getElementById('add_doc_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add another documentation\" onclick=\"addDoc();\">[Add another documentation]</a>";
}

// changes the legend of the button to add a documentation (no documentation in the form)
function reChangeDocLink()
{
	var d = document.getElementById('add_doc_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add a documentation\" onclick=\"addDoc();\">[Add a documentation]</a>";
}


// validates the whole form
function validate_submission()
{
	var valid = true;
	var message = "The following elements must be filled:\n";
	
	// name
	if ((document.forms['submission_form'].elements['name'].value == "") || isSpace(document.forms['submission_form'].elements['name'].value))
	{
		message += "+ primary name\n";
		document.forms['submission_form'].elements['name'].style.backgroundColor = 'red';
		valid = false;
	}
	else
	{
		document.forms['submission_form'].elements['name'].style.backgroundColor = 'white';
	}
	
	// synonyms
	// ... we don't care!
	
	// definition
	if ((document.forms['submission_form'].elements['def'].value == "Enter definition here...") || (document.forms['submission_form'].elements['def'].value == "") || (isSpace(document.forms['submission_form'].elements['def'].value)))
	{
		message += "+ definition\n";
		document.forms['submission_form'].elements['def'].style.backgroundColor = 'red';
		valid = false;
	}
	else
	{
		document.forms['submission_form'].elements['def'].style.backgroundColor = 'white';
	}
	
	// regular expression (pattern)
	if ((document.forms['submission_form'].elements['pattern'].value == "Enter Identifier pattern here...") || (document.forms['submission_form'].elements['pattern'].value == "") || (isSpace(document.forms['submission_form'].elements['pattern'].value)))
	{
		message += "+ pattern\n";
		document.forms['submission_form'].elements['pattern'].style.backgroundColor = 'red';
		valid = false;
	}
	else
	{
		document.forms['submission_form'].elements['pattern'].style.backgroundColor = 'white';
	}
	
	// URI(s)
	if (((document.forms['submission_form'].elements['url'].value == "") || (isSpace(document.forms['submission_form'].elements['url'].value))) && ((document.forms['submission_form'].elements['urn'].value == "") || (isSpace(document.forms['submission_form'].elements['urn'].value))))
	{
		message += "+ official URL or URN\n";
		document.forms['submission_form'].elements['url'].style.backgroundColor = 'red';
		document.forms['submission_form'].elements['urn'].style.backgroundColor = 'red';
		valid = false;
	}
	else
	{
		document.forms['submission_form'].elements['url'].style.backgroundColor = 'white';
		document.forms['submission_form'].elements['urn'].style.backgroundColor = 'white';
	}
	
	// deprectaed URIs
	// ... we don't care!
	
	// deprecated URI(s)
	var num = document.getElementById('deprecatedCounter').value;
	for (var i=1; i<=num; i++)
	{
		var query = "document.forms['submission_form'].elements['deprecated" + i + "']";
		if (eval(query))
		{
			var query_str = query + ".value";
			if ((eval(query_str) == '') || (isSpace(eval(query_str))))
			{
				message += "+ deprecated URI #" + i +"\n";
				eval(query).style.backgroundColor = 'red';
				valid = false;
			}
			else
			{
				eval(query).style.backgroundColor = 'white';
			}
		}
	}
	
	// Resources
	var num = document.getElementById('resourcesCounter').value;
	for (var i=1; i<=num; i++)
	{
		var query_dep = "document.forms['submission_form'].elements['depId" + i + "']";
		var query_des = "document.forms['submission_form'].elements['desId" + i + "']";
		var query_dr = "document.forms['submission_form'].elements['drId" + i + "']";
		if (eval(query_dep) && eval(query_des) && eval(query_dr))
		{
			// Data entry
			if ((eval(query_dep).value == '') || (isSpace(eval(query_dep).value)))
			{
				message += "+ data entry #" + i + "\n";
				eval(query_dep).style.backgroundColor = 'red';
				valid = false;
			}
			else
			{
				eval(query_dep).style.backgroundColor = 'white';
			}
			// Data resource
			if ((eval(query_dr).value == '') || (isSpace(eval(query_dr).value)))
			{
				message += "+ data resource #" + i + "\n";
				eval(query_dr).style.backgroundColor = 'red';
				valid = false;
			}
			else
			{
				eval(query_dr).style.backgroundColor = 'white';
			}
		}
	}
	
	// URI(s) and type of documentation
	var num = document.getElementById('docCounter').value;
	for (var i=1; i<=num; i++)
	{
		var query_type = "document.forms['submission_form'].elements['docType" + i + "']";
		var query_uri = "document.forms['submission_form'].elements['docUri" + i + "']";
		var id = "docUri" + i;
		
		// test of the type of documentation
		if (eval(query_type))
		{
			var query_type_str = "((" + query_type + "[0].checked == false) && (" + query_type + "[1].checked == false))";
			if (eval(query_type_str))
			{
				message += "+ the type of the URI for the documentation #" + i + "\n";
				var eltStr = "document.getElementById('docTypeBox" + i + "')";
				eval(eltStr).style.backgroundColor = 'red';
				valid = false;
			}
			else
			{
				var eltStr = "document.getElementById('docTypeBox" + i + "')";
				eval(eltStr).style.backgroundColor = 'white';
			}
		}
		// test of the IDs or URLs
		if (eval(query_uri))
		{
			var query_uri_str = query_uri + ".value == ''";
			if (eval(query_uri_str) || isSpace(eval(query_uri + ".value")) || ((eval(query_uri + ".value")) == "http://www."))
			{
				message += "+ the URI for the documentation #" + i + "\n";
				var cmd = "document.forms['submission_form'].elements['docUri" + i + "'].style.backgroundColor = 'red'";
				eval(cmd);
				valid = false;
			}
			else
			{
				// test, if the URI is an ID, that it follows the regexp
				if (((eval(query_type))[0].checked == true) && (!(checkRegExp(id))))
				{
					message += "+ Please enter a valid Pubmed ID (containing only digit numbers) for the documentation #" + i + "\n";
					var cmd = "document.forms['submission_form'].elements['docUri" + i + "'].style.backgroundColor = 'red'";
					eval(cmd);
					valid = false;
				}
				else
				{
					var cmd = "document.forms['submission_form'].elements['docUri" + i + "'].style.backgroundColor = 'white'";
					eval(cmd);
				}
			}
		}
	}
	
	// test of the information about the user (if not authenticated)
	if (document.forms['submission_form'].elements['user'])
	{
		if ((document.forms['submission_form'].elements['user'].value == "") || isSpace(document.forms['submission_form'].elements['user'].value))
		{
			message += "+ User information\n";
			document.forms['submission_form'].elements['user'].style.backgroundColor = 'red';
			valid = false;
		}
		else
		{
			document.forms['submission_form'].elements['user'].style.backgroundColor = 'white';
		}
	}
	
	if (!valid)
	{
		alert(message);
	}
	
	return valid;
}

// Shows one precise help bubble
function displayHelp(elt)
{
  var str = "document.getElementById(\"" + elt + "\")";
  if ((eval(str)).style.display == "none")
  {
 	  (eval(str)).style.display = "block";
  }
  else
  {
    (eval(str)).style.display = "none";
  }
}

// Shows all the help bubbles
function displayHelps()
{
	for (i=0; i<document.getElementsByTagName('div').length; i++)
  {
    if (document.getElementsByTagName('div')[i].className == "help_submission")
    {
      document.getElementsByTagName('div')[i].style.display = "block";
    }
  }
}

// Hides all the help bubbles
function hideHelps()
{
	for (i=0; i<document.getElementsByTagName('div').length; i++)
  {
    if (document.getElementsByTagName('div')[i].className == "help_submission")
    {
    	document.getElementsByTagName('div')[i].style.display = "none";
    }
  }
}

// Displays the form to add a documentation, based on a MIRIAM ID
function displayMiriamForm(section, num)
{
	var str = "document.getElementById('" + section + "')";
	(eval(str)).innerHTML = "ID: <input type=\"text\" name=\"docSuffixId" + num + "\" size=\"18\" id=\"docSuffixId" + num + "\" value=\"http://www.pubmed.gov/\" onfocus=\"this.blur();\" style=\"background-color: rgb(197, 206, 213);\" />#<input type=\"text\" name=\"docUri" + num + "\" size=\"8\" id=\"docUri" + num + "\" />";
	return true;
}

// Displays the form to add a documentation, based on an URL
function displayUrlForm(section, num)
{
	var str = "document.getElementById('" + section + "')";
	(eval(str)).innerHTML = "Address: <input type=\"text\" name=\"docUri" + num + "\" size=\"45\" id=\"docUri" + num + "\" value=\"http://www.\" />";
	return true;
}
  
// Checks the fields containing a PubMedId
function checkRegExp(elt)
{
	var regexp = /^\d+$/;   //regular expression defining only digit numbers
	var str = "document.getElementById('" + elt + "')";
	// var value = (eval(str)).value;
	if ((eval(str)).value.search(regexp) == -1)   // match failed
	{
		return false;
	}
	else
	{
		return true;
	}
}

// Tests if a string is only made of spaces
function isSpace(string)
{
	var space = true;
	for (var i=0; i<string.length; i++)
	{
		if (string[i] != " ")
		{
			space = false;
		}
	}
	
	return space;
}
