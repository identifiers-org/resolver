// author:    Camille Laibe <camille.laibe@ebi.ac.uk>
// version:   20080711
// copyright: EMBL-EBI, Computational Neurobiology Group
//
// Functions for dynamic HTML forms (submission of new elements and edition of old ones)


// Resets the different values of the counters of the page
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

// Resets the different values of the counters of the page (in edit mode)
function razEdit()
{
	document.getElementById("synonymsCounter").value = document.getElementById("synonymsCounterReal").value;
	document.getElementById("deprecatedCounter").value = document.getElementById("deprecatedCounterReal").value;
	document.getElementById("resourcesCounter").value = document.getElementById("resourcesCounterReal").value;
	document.getElementById("docCounter").value = document.getElementById("docCounterReal").value;
}

// Creates a div with a field for a new synonym
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

// Removes a div with a field for a new synonym
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

// Changes the legend of the button to add a new synonym (already one synonym in the form)
function changeSynonymLink()
{
	var d = document.getElementById('add_synonym_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add another synonym\" onclick=\"addSynonym();\">[Add another synonym]</a>";
}

// Changes the legend of the button to add a new synonym (no synonym in the form)
function reChangeSynonymLink()
{
	var d = document.getElementById('add_synonym_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add a synonym\" onclick=\"addSynonym();\">[Add a synonym]</a>";
}

// Creates a div with a field for a deprecated URI
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

// Removes a div with a field for a deprecated URI
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

// Changes the legend of the button to add a deprecated URI (already one deprecated URI in the form)
function changeDeprecatedLink()
{
	var d = document.getElementById('add_deprecated_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add another deprecated URI\" onclick=\"addDeprecated();\">[Add another deprecated URI]</a>";
}

// Changes the legend of the button to add a deprecated URI (no deprecated URI in the form)
function reChangeDeprecatedLink()
{
	var d = document.getElementById('add_deprecated_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add a deprecated URI\" onclick=\"addDeprecated();\">[Add a deprecated URI]</a>";
}

// Creates a div with a field for a new resource
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
	newdiv.innerHTML = "\n<input type=\"hidden\" value=\"null\" name=\"resourceId" + num + "\" id=\"resourceId" + num + "\" />\n<table class=\"resources\"><tr><td rowspan=\"2\" valign=\"bottom\">&nbsp;</td><td>Data entry:&nbsp;</td><td><input type=\"text\" name=\"dataEntryPrefix" + num + "\" size=\"34\" id=\"depId" + num + "\" /></td><td><b>$id</b></td><td><input type=\"text\" name=\"dataEntrySuffix" + num +"\" size=\"15\" id=\"desId" + num + "\" /></td></tr><tr><td>Example of identifier:&nbsp;</td><td><input type=\"text\" name=\"dataExample" + num + "\" size=\"30\" id=\"xplId" + num + "\" /></td></tr><tr><td><b>#" + num + ":&nbsp;</b></td><td>Data resource:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"dataResource" + num + "\" size=\"45\" id=\"drId" + num + "\" /></td></tr><tr><td rowspan=\"3\" valign=\"top\"><a href=\"javascript:;\" title=\"Remove this resource\" onclick=\"removeResource(\'" + IdName + "\')\"><img src=\"http://www.ebi.ac.uk/compneur-srv/miriam/demo/img/Delete.gif\" alt=\"Delete logo\" height=\"16\" width=\"16\" /></a></td><td>Information:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"information" + num + "\" size=\"55\" id=\"infoId" + num + "\" /></td></tr><tr><td>Institution:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"institution" + num + "\" size=\"45\" id=\"instituteId" + num + "\" /></td></tr><tr><td>Country:&nbsp;</td><td colspan=\"3\"><input type=\"text\" name=\"country" + num + "\" size=\"30\" id=\"countryId" + num + "\" /></td></tr></table>\n";
	var ni = document.getElementById('resources_id');
	ni.appendChild(newdiv);
	// change the link, if necessary (no more  URI in the form)
	if (numReal == 2)
	{
		changeResourcesLink();
	}
}

// Removes a div with a field for a resource
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

// Changes the legend of the button to add a resource (already one resource in the form)
function changeResourcesLink()
{
	var d = document.getElementById('add_resources_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add another resource\" onclick=\"addResource();\">[Add another resource]</a>";
}

// Changes the legend of the button to add a resource (no resource in the form)
function reChangeResourcesLink()
{
	var d = document.getElementById('add_resources_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add a resource\" onclick=\"addResource();\">[Add a resource]</a>";
}

// Creates a div with a field for a new documentation
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
	newdiv.innerHTML = "\n<div><a href=\"javascript:;\" title=\"Remove this piece of documentation\" onclick=\"removeDoc(\'" + IdName + "\')\"><img src=\"http://www.ebi.ac.uk/compneur-srv/miriam/demo/img/Delete.gif\" alt=\"Delete logo\" height=\"16\" width=\"16\" /></a>&nbsp;<span id=\"docTypeBox" + num + "\">Type: PMID: <input type=\"radio\" name=\"docType" + num + "\" value=\"PMID\" onclick=\" return displayPubMedForm('docForm" + num + "', " + num + ");\" />&nbsp;DOI: <input type=\"radio\" name=\"docType" + num + "\" value=\"DOI\" onclick=\" return displayDoiForm('docForm" + num + "', " + num + ");\" />&nbsp;Physical Location: <input type=\"radio\" name=\"docType" + num + "\" value=\"URL\" onclick=\"return displayUrlForm('docForm" + num + "', " + num + ");\" /></span></div><div id=\"docForm" + num + "\" class=\"indentDiv\"><em>First, select a type of documentation above...</em></div>\n";
	var ni = document.getElementById('doc_id');
	ni.appendChild(newdiv);
	// change the link, if necessary (no more  URI in the form)
	if (numReal == 1)
	{
		changeDocLink();
	}
}

// Removes a div with a field for a documentation
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

// Changes the legend of the button to add a documentation (already one documentation in the form)
function changeDocLink()
{
	var d = document.getElementById('add_doc_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add another piece of documentation\" onclick=\"addDoc();\">[Add another piece of documentation]</a>";
}

// Changes the legend of the button to add a documentation (no documentation in the form)
function reChangeDocLink()
{
	var d = document.getElementById('add_doc_id');
	d.innerHTML = "<a href=\"javascript:;\" title=\"Add a piece of documentation\" onclick=\"addDoc();\">[Add a piece of documentation]</a>";
}


// Validates the whole form
function validate_submission()
{
	var valid = true;
	var message = "The following elements must be (properly) filled:\n";
	
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
    if ((document.forms['submission_form'].elements['urn'].value == "") || (isSpace(document.forms['submission_form'].elements['urn'].value)))
    {
        message += "+ official URI\n";
        document.forms['submission_form'].elements['urn'].style.backgroundColor = 'red';
        valid = false;
    }
    else
    {
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
		
		// test of the documentation references (PMID, DOI or URL)
		if (eval(query_uri))   // element exists
		{
			var query_uri_str = query_uri + ".value == ''";
			if (eval(query_uri_str) || isSpace(eval(query_uri + ".value")))   // element is empty
			{
				message += "+ the URI for the documentation #" + i + "\n";
				var cmd = "document.forms['submission_form'].elements['docUri" + i + "'].style.backgroundColor = 'red'";
				eval(cmd);
				valid = false;
			}
			else   // element not empty
			{
				var pmidType = query_type + "[0].checked == true";
				if ((eval(pmidType)) && (!(checkPmid(id))))   // element is a PMID and not valid
				{
					message += "+ invalid PubMed ID for the documentation #" + i + "\n";
					var cmd = "document.forms['submission_form'].elements['docUri" + i + "'].style.backgroundColor = 'red'";
					eval(cmd);
					valid = false;
				}
				else
				{
					var doiType = query_type + "[1].checked == true";
					if ((eval(doiType)) && (!(checkDoi(id))))   // element is a DOI and not valid
					{
						message += "+ invalid DOI for the documentation #" + i + "\n";
						var cmd = "document.forms['submission_form'].elements['docUri" + i + "'].style.backgroundColor = 'red'";
						eval(cmd);
						valid = false;
					}
					else
					{
						var urlType = query_type + "[2].checked == true";
						if ((eval(urlType)) && ((eval(query_uri + ".value")) == "http://www."))   // element is a physical location
						{
							message += "+ URL for the documentation #" + i + "\n";
							var cmd = "document.forms['submission_form'].elements['docUri" + i + "'].style.backgroundColor = 'red'";
							eval(cmd);
							valid = false;
						}
						else   // everything seems ok
						{
							var cmd = "document.forms['submission_form'].elements['docUri" + i + "'].style.backgroundColor = 'white'";
							eval(cmd);
						}
					}
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

// Displays the form to add a documentation, based on a PubMed Identifier
function displayPubMedForm(section, num)
{
	var str = "document.getElementById('" + section + "')";
	(eval(str)).innerHTML = "PMID: <input type=\"text\" name=\"docUri" + num + "\" size=\"20\" id=\"docUri" + num + "\" /><div style=\"color: grey;\">Example: <span style=\"font-style: italic;\">18078503</span></div>";
	return true;
}

// Displays the form to edit a documentation, based on a PubMed Identifier, giving a PMID as a parameter
function displayPubMedForm2(section, num, pmid)
{
	var str = "document.getElementById('" + section + "')";
	(eval(str)).innerHTML = "PMID: <input type=\"text\" name=\"docUri" + num + "\" size=\"20\" id=\"docUri" + num + "\" value=\"" + pmid + "\" /><div style=\"color: grey;\">Example: <span style=\"font-style: italic;\">18078503</span></div>";
	return true;
}

// Displays the form to add a documentation, based on an URL
function displayUrlForm(section, num)
{
	var str = "document.getElementById('" + section + "')";
	(eval(str)).innerHTML = "Location: <input type=\"text\" name=\"docUri" + num + "\" size=\"45\" id=\"docUri" + num + "\" value=\"http://www.\"  onfocus=\"javascript:this.select();\"/><div style=\"color: grey;\">Example: <span style=\"font-style: italic;\">http://www.ebi.ac.uk/compneur-srv/miriam-main/mdb?section=faq</span></div>";
	return true;
}

// Displays the form to edit a documentation, based on an URL, giving the URL as a parameter
function displayUrlForm2(section, num, url)
{
	var str = "document.getElementById('" + section + "')";
	(eval(str)).innerHTML = "Location: <input type=\"text\" name=\"docUri" + num + "\" size=\"45\" id=\"docUri" + num + "\" value=\"" + url + "\"  onfocus=\"javascript:this.select();\"/><div style=\"color: grey;\">Example: <span style=\"font-style: italic;\">http://www.ebi.ac.uk/compneur-srv/miriam-main/mdb?section=faq</span></div>";
	return true;
}

// Displays the form to add a documentation, based on a DOI
function displayDoiForm(section, num)
{
	var str = "document.getElementById('" + section + "')";
	(eval(str)).innerHTML = "DOI: <input type=\"text\" name=\"docUri" + num + "\" size=\"30\" id=\"docUri" + num + "\" value=\"\" /><div style=\"color: grey;\">Example: <span style=\"font-style: italic;\">10.1186/1752-0509-1-58</span></div>";
	return true;
}

// Displays the form to edit a documentation, based on a DOI, giving the DOI as a parameter
function displayDoiForm2(section, num, doi)
{
	var str = "document.getElementById('" + section + "')";
	(eval(str)).innerHTML = "DOI: <input type=\"text\" name=\"docUri" + num + "\" size=\"30\" id=\"docUri" + num + "\" value=\"" + doi + "\" /><div style=\"color: grey;\">Example: <span style=\"font-style: italic;\">10.1186/1752-0509-1-58</span></div>";
	return true;
}

// Checks the validity of an element against the regular expresion of a PMID
function checkPmid(elt)
{
	var regexp = /^\d+$/;   // regular expression followed by PMID
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

// Checks the validity of an element against the regular expresion of a DOI
function checkDoi(elt)
{
	var regexp = /^\d{2}\.\d{4}\/.*$/;   // regular expression followed by DOI
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

// Validates that an anonymous user who want to edit the tags of a data type has entered some personnal info first.
function validate_edit_tag(form)
{
    var valid = true;
    
    if ((document.getElementById("userId").value == "") || isSpace(document.getElementById("userId").value))
    {
        var message = "You must fill the 'user information' field!";
        document.getElementById("userId").style.backgroundColor = 'red';
        valid = false;
    }
    else
    {
        document.getElementById("userId").style.backgroundColor = 'white';
    }
    
    if (!valid)
    {
        alert(message);
    }
    
    return valid;
}

// Validates that an anonymous user who want to edit the tags of a data type has entered some personnal info first.
function validate_edit_tag(form)
{
    var valid = true;
    
    if ((document.getElementById("userId").value == "") || isSpace(document.getElementById("userId").value))
    {
        var message = "You must fill the 'user information' field!";
        document.getElementById("userId").style.backgroundColor = 'red';
        valid = false;
    }
    else
    {
        document.getElementById("userId").style.backgroundColor = 'white';
    }
    
    if (!valid)
    {
        alert(message);
    }
    
    return valid;
}

// Removes an connection between a tag and a data type
function deleteTagLink(element)
{
    if ($(element) != null)
    {
        // checks if was the last element from the list
        var form = $('tag_edition_2');
        var dump = form.getInputs('text');
        if (dump.length == 1)
        {
            var str = '\n<li id="li_link_0"><em>No data type associated</em></li>\n';
            new Insertion.Before('li_'+dump[0].id, str);
            $('counter_id').value = 0;
        }
        // removes the element
        Element.remove(element);
    }
}

// Adds a new connection between a tag and a data type
function addTagLink(element)
{
    var counterElt = $('counter_id');
    var counterValue = counterElt.value;
    var adr;
    var notLast = false;
    var insertLoc;
    
    // generates the list of data types (and their HTML id) associated with the current tag
    var listData = [];   // this size can be too much, if some links have been deleted
    var listId = [];   // this size can be too much, if some links have been deleted
    var form = $('tag_edition_2');
    var dump = form.getInputs('text');
    for (var i=0; i<dump.length; ++i)
    {
        listData[i] = dump[i].value;
        listId[i] = dump[i].id;
    }
    
    // search if the data type is not already in the list of links
    var found = false;
    for (var index=0; index<listData.length; ++index)
    {
        if (element.capitalize() == (listData[index]).capitalize())
        {
            found = true;
            break;
        }
    }
    
    // the data type is already there
    if (found)
    {
        alert("'" + element + "' is already associated with this tag!");
        return false;   // the end.
    }
    else
    {
        // increments the counter of connections (actually number max)
        var newCounterElt = parseInt(counterValue) + 1;
        counterElt.value = newCounterElt;
    }
    
    var str = '\n<li id="li_link_' + newCounterElt + '"><a href="#" onclick="deleteTagLink(\'li_link_' + newCounterElt + '\');return false;" title="Removes this connection"><img src="http://www.ebi.ac.uk/compneur-srv/miriam/demo/img/Delete.gif" alt="delete icon" title="Deletes this connection" height="16" width="16" /></a>&nbsp;<input name="n_link_' + newCounterElt + '" id="link_' + newCounterElt + '" type="text" size="40" style="border:none;" readonly="readonly" value="' + element + '" /></li>\n';
    
    // first data type to add
    if (newCounterElt == 1)
    {
        new Insertion.After('li_link_0', str);
        Element.remove('li_link_0');
        return false;   // the end.
    }
    
    // search for the right place to put the new data type (let's try to keep the alphabetical order)
    insertLoc = listData.length - 1;
    for (var index=0; index<listData.length; ++index)
    {
        if (element.capitalize() < (listData[index]).capitalize())
        {
            notLast = true;
            insertLoc = index;
            break;
        }
        //alert("current test: " + element + " < " + listData[index] + " -> " + (element.capitalize() < (listData[index]).capitalize()));
    }
    
    if (notLast)
    {
        new Insertion.Before('li_'+listId[insertLoc], str);
    }
    else
    {
        new Insertion.After('li_'+listId[insertLoc], str);
    }
}

// Validates the tag edition form (1)
function validate_tag_edition()
{
    var valid = true;
    var message = "The following elements must be (properly) filled:\n";
    
    if (isSpace(Form.Element.getValue('tagName_id')))
    {
        message += '+ the name of the tag must not be empty\n';
        valid = false;
    }
    
    // informs the user, if necessary
    if (! valid)
    {
        alert(message);
    }
    
    return valid;
}

// Generates a link using the prefix, suffix and example of identifier
function generateExampleLink(prefix, suffix, identifier)
{
    var url = Form.Element.getValue(prefix) + Form.Element.getValue(identifier) + Form.Element.getValue(suffix);
    window.open(url, 'open_window', 'scrollbars, resizable, dependent, width=640, height=480, left=0, top=0');
}
