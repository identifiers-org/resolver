/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


/*
 * TODO:
 * 
 * - create a Documentation object
 * 
 * - change DataType and DataTypeHybernate to add the support of the new fields of 'mir_resource' (info, institution, location and obsolete)
 * -
 * 
 * 
 * - add new attributes to this class
 * - add comments
 * -
 */

package uk.ac.ebi.miriam.db;


import java.util.ArrayList;

import uk.ac.ebi.miriam.web.MiriamUtilities;


/**
 * <p>
 * Object which stores all the information about a data-type.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20070123
 */
public class DataType
{
	/* stable identifier of the data-type (something starting by 'MIR:000' and followed by 5 digits) */
	private String id = new String();
	/* official name of the data-type */ 
	private String name = new String();
	/* name of the data-type for HTML links (with "%20" instead of spaces)*/ 
	private String nameURL = new String();
	/* synonyms of the name of the data-type */
	private ArrayList synonyms = new ArrayList();
	/* official URL of the data-type */
	private String URL = new String();
	/* official URN of the data-type */
	private String URN = new String();
	/* deprecated URIs */
	private ArrayList deprecatedURIs = new ArrayList();
	/* definition of the data-type */
	private String definition = new String();
	/* regular expression of the data-type */
	private String regexp = new String();
	/* resources (= physical locations) */
	private ArrayList resources = new ArrayList();
	/* list of physical locations of pieces of documentation of the data-type */
	private ArrayList documentationURLs = new ArrayList();
	/* list of identifiers of pieces of documentation of the data-type */
	private ArrayList documentationIDs = new ArrayList();
	/* list of the physical locations of the pieces of documentation (in fact: 'documentationURLs' AND transformed 'documentationIDs') */
	private ArrayList docHtmlURLs = new ArrayList();
	
	
	/**
	 * Construtor by default
	 */
	public DataType()
	{
		// nothing here, for the moment.
	}
	
	
	/**
	 * Destroys the object (free the memory)
	 */
	public void destroy()
	{
		this.id = "";
		this.name = "";
		this.nameURL = "";
		(this.synonyms).clear();
		this.URL = "";
		this.URN = "";
		(this.deprecatedURIs).clear();
		this.definition = "";
		this.regexp = "";
		(this.resources).clear();
		(this.documentationURLs).clear();
		(this.documentationIDs).clear();
		(this.docHtmlURLs).clear();
	}
	
	
	/**
	 * Overrides the 'toString()' method for the 'DataType' object
	 * @return a string which contains all the information about the data-type
	 */
	public String toString()
	{
		String tmp = new String();
		
		tmp += "\n";
		tmp += "+ Internal ID:        " + getId() + "\n";
		tmp += "+ Name:               " + getName() + "\n";
		tmp += "+ Synonyms:           " + getSynonyms().toString() + "\n";
		tmp += "+ Definition:         " + getDefinition() + "\n";
		tmp += "+ Regular Expression: " + getRegexp() + "\n";
		tmp += "+ Official URL:       " + getURL() + "\n";
		tmp += "+ Official URN:       " + getURN() + "\n";
		tmp += "+ Deprecated URI(s):  " + getDeprecatedURIs().toString() + "\n";
		tmp += "+ Data Resource(s): " + "\n";
		for (int i=0; i<getResources().size(); ++i)
		{
			tmp += "    * Resource #" + i + ":\n";
			tmp += getResource(i).toString() + "\n";
		}
		tmp += "+ Documentation ID(s): " + "\n";
		for (int i=0; i<getDocumentationIDs().size(); ++i)
		{
			tmp += "       - " + getDocumentationID(i) + "\n";
		}
		tmp += "+ Documentation URL(s): " + "\n";
		for (int i=0; i<getDocumentationURLs().size(); ++i)
		{
			tmp += "       - " + getDocumentationURL(i) + "\n";
		}
		
		return tmp;
	}
	
	
	/**
	 * Searches the type of the URI in parameter (URL or URN?)
	 * <p>
	 * WARNING: doesn't check if the parameter is a valid URI!
	 * @param uri a Uniform Request Identifier (can be a URL or a URN)
	 * @return a boolean with the answer to the question above
	 */
	public String getURIType(String uri)
	{
		// 'urn:' n'a pas été trouvé dans l'URI
		if ((uri.indexOf("urn:")) == -1)
		{
			return "URL";
		}
		else
		{
			return "URN";
		}
	}
	
	
	/**
	 * Returns the answer to the question: is this URI a URL?
	 * @param uri a Uniform Request Identifier
	 * @return a boolean with the answer to the question above
	 */
	public boolean isURL(String uri)
	{
		if (getURIType(uri).equalsIgnoreCase("URL"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * Returns the answer to the question: is this URI a URN?
	 * @param uri a Uniform Request Identifier
	 * @return a boolean with the answer to the question above
	 */
	public boolean isURN(String uri)
	{
		if (getURIType(uri).equalsIgnoreCase("URN"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * Returns the answer to the question: is the deprecated URI, identified by the index, a URN?
	 * @param i index of a deprecated URI
	 * @return a boolean with the answer to the question above
	 */
	public boolean isDeprecatedURN(int i)
	{
		if (getURIType(getDeprecatedURI(i)).equalsIgnoreCase("URN"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * Returns the answer to the question: is the deprecated URI, identified by the index, a URL?
	 * @param i index of a deprecated URI
	 * @return a boolean with the answer to the question above
	 */
	public boolean isDeprecatedURL(int i)
	{
		if (getURIType(getDeprecatedURI(i)).equalsIgnoreCase("URL"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * Getter of the definition of the data-type
	 * @return definition of the data-type
	 */
	public String getDefinition()
	{
		return definition;
	}
	
	
	/**
	 * Setter of the definition of the data-type
	 * @param definition definition of the data-type
	 */
	public void setDefinition(String definition)
	{
		this.definition = removeSpace(definition);
	}
	
	
	/**
	 * Returns all the deprecated URIs of the data-type
	 * @return all the deprecated URIs of the data-type
	 */
	public ArrayList getDeprecatedURIs()
	{
		return deprecatedURIs;
	}
	
	
	/**
	 * Returns one precise deprecated URI of the data-type
	 * @param i index of the deprecated URI
	 * @return one precise deprecated URI of the data-type
	 */
	public String getDeprecatedURI(int i)
	{
		return (String) deprecatedURIs.get(i);
	}
	
	/**
	 * Setter of the deprecated forms of the URI (URN or URL) of the data-type
	 * @param deprecatedURI list of all the deprecated URI of the data-type
	 */
	public void setDeprecatedURIs(ArrayList deprecatedURIs)
	{
		for (int i=0; i<deprecatedURIs.size(); ++i)
		{
			this.deprecatedURIs.add(removeSpace((String) deprecatedURIs.get(i)));
		}
	}
	
	
	/**
	 * Getter of the stable ID (in the database) of the data-type
	 * @return the internal ID of the data-type
	 */
	public String getId()
	{
		return id;
	}
	
	
	/**
	 * Setter of the internal ID (in the database) of the data-type
	 * @param internalId internal ID of the data-type
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
	
	/**
	 * Getter of the official name (not a synonym) of the data-type
	 * @return name of the data-type
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * Setter of the official name of the data-type
	 * @param name name of the data-type
	 */
	public void setName(String name)
	{
		this.name = removeSpace(name);
	}
	
	
	/**
	 * Getter of the HTML name (without any space)
	 * @return
	 */
	public String getNameURL()
	{
		return nameURL;
	}
	
	
	/**
	 * Setter of the HTML name (without any space)
	 * @param nameURL
	 */
	public void setNameURL(String nameURL)
	{
		this.nameURL = removeSpace(nameURL);
	}
	
	
	/**
	 * Getter of the regular expression of the data-type
	 * @return regular expression of the data-type
	 */
	public String getRegexp()
	{
		return regexp;
	}
	
	
	/**
	 * Setter of the regular expression of the data-type
	 * @param regexp regular expression of the data-type
	 */
	public void setRegexp(String regexp)
	{
		this.regexp = removeSpace(regexp);
	}
	
	
	/**
	 * Getter of the synonyms of the name of the data-type
	 * @return list of all the synonyms of the name of the data-type
	 */
	public ArrayList getSynonyms()
	{
		return synonyms;
	}
	
	
	/**
	 * Getter of one of the synonyms of the name of the data-type
	 * @param i index of the synonym
	 * @return one precise synonym of the name of the data-type
	 */
	public String getSynonym(int i)
	{
		return (String) synonyms.get(i);
	}
	
	
	/**
	 * Setter of the synonyms of the data-type
	 * @param synonyms list of all the synonyms of the data-type
	 */
	public void setSynonyms(ArrayList synonyms)
	{
		for (int i=0; i<synonyms.size(); ++i)
		{
			this.synonyms.add(removeSpace((String) synonyms.get(i)));	
		}
	}
	
	
	/**
	 * Getter of the official URL of the data-type
	 * @return URL of the data-type
	 */
	public String getURL()
	{
		return URL;
	}
	
	
	/**
	 * Setter of the official URL of the data-type
	 * @param url URL of the data-type
	 */
	public void setURL(String url)
	{
		URL = removeSpace(url);
	}
	
	
	/**
	 * Getter of the official URN of the data-type
	 * @return URN of the data-type
	 */
	public String getURN()
	{
		return URN;
	}
	
	
	/**
	 * Setter of the official URN of the data-type
	 * @param urn URN of the data-type
	 */
	public void setURN(String urn)
	{
		URN = removeSpace(urn);
	}
	
	
	/**
	 * Getter of the resources (physical locations) of the data-type
	 * @return the resources of the data-type
	 */
	public ArrayList getResources()
	{
		return resources;
	}
	
	
	/**
	 * Getter of a precise resource (physical location) of the data-type
	 * @return a precise resource of the data-type
	 */
	public Resource getResource(int index)
	{
		return (Resource) resources.get(index);
	}
	
	
/*
	 * Getter of a precise physical location of the data-type
	 * @param column number between 0 and 3 (excluded); 3 columns: one prefix, one suffix and one for the banner page
	 * @param row number corresponding to a precise data-type location in the whole list 
	 * @return the physical location wanted of the data-type
	public String getResources(int column, int row)
	{
		// TODO:
		// - try this method: OBVIOUSLY IT DOESN'T WORK!!!
		return (String) ((ArrayList) locations.get(column)).get(row);
	}
*/
	
	
	/**
	 * Setter of the resources (physical locations) of the data-type
	 * @param locations list of the resources of the data-type
	 */
	public void setResources(ArrayList resources)
	{
		this.resources = resources;
		

/* OLD VERSION
		ArrayList tempPrefix = new ArrayList();
		for (int i=0; i<((ArrayList) locations.get(0)).size(); ++i)
		{
			tempPrefix.add(removeSpace((String) ((ArrayList) locations.get(0)).get(i)));
		}
		
		ArrayList tempSuffix = new ArrayList();
		for (int i=0; i<((ArrayList) locations.get(1)).size(); ++i)
		{
			tempSuffix.add(removeSpace((String) ((ArrayList) locations.get(1)).get(i)));
		}
		
		ArrayList tempFront = new ArrayList();
		for (int i=0; i<((ArrayList) locations.get(2)).size(); ++i)
		{
			tempFront.add(removeSpace((String) ((ArrayList) locations.get(2)).get(i)));
		}
		
		this.locations.add(tempPrefix);
		this.locations.add(tempSuffix);
		this.locations.add(tempFront);
*/
	}
	
	
/*
	 * Setter of the physical locations of the data-type
	 * @param url list of the prefix part of the urls ($id between prefix and suffix)
	 * @param suffix_url list of the suffix part of the urls ($id between prefix and suffix)
	 * @param front_url lis of the urls to access to the front page of the web site
	public void setResources(ArrayList url, ArrayList suffix_url, ArrayList front_url)
	{
		ArrayList temp1 = new ArrayList();
		for (int i=0; i<url.size(); ++i)
		{
			temp1.add(removeSpace((String) url.get(i)));
		}
		this.locations.add(temp1);
		
		ArrayList temp2 = new ArrayList();
		for (int i=0; i<suffix_url.size(); ++i)
		{
			temp2.add(removeSpace((String) suffix_url.get(i)));
		}
		this.locations.add(temp2);
		
		ArrayList temp3 = new ArrayList();
		for (int i=0; i<front_url.size(); ++i)
		{
			temp3.add(removeSpace((String) front_url.get(i)));
		}
		this.locations.add(temp3);
	}
*/
	
	/**
	 * Adds another resource to the data-type
	 * @param res the new resource to add to the data-type
	 */
	public void addResource(Resource res)
	{
		this.resources.add(res);
	}
	
	
	/**
	 * Getter of the prefix of the physical location of all the data entries (one precise element)
	 * @return the prefix of the physical location of all the data entries 
	 */
	public ArrayList getDataEntriesPrefix()
	{
		ArrayList result = new ArrayList();
		
		for (int i=0; i<resources.size(); ++i)
		{
			result.add(((Resource) resources.get(i)).getUrl_prefix());
		}
		
		return result;
	}
	
	
	/**
	 * Getter of the prefix of the physical location of one data entry (one precise element)
	 * <p>
	 * WARNING: no check of the validity of the parameter ('out of range' possible...)
	 * @param index index of the resource
	 * @return the prefix of the physical location of one precise the data entry 
	 */
	public String getDataEntryPrefix(int index)
	{
		return (String) (((Resource) resources.get(index)).getUrl_prefix());
	}
	
	
	/**
	 * Getter of the suffix of the physical location of all the data entries (one precise element)
	 * @return the suffix of the physical location of all the data entries 
	 */
	public ArrayList getDataEntriesSuffix()
	{
		ArrayList result = new ArrayList();
		
		for (int i=0; i<resources.size(); ++i)
		{
			result.add(((Resource) resources.get(i)).getUrl_suffix());
		}
		
		return result;
	}
	
	
	/**
	 * Getter of the suffix of the physical location of one data entry (one precise element)
	 * @param index index of the resource
	 * @return the suffix of the physical location of one precise the data entry 
	 */
	public String getDataEntrySuffix(int index)
	{
		return (String) (((Resource) resources.get(index)).getUrl_suffix());
	}
	
	
	/**
	 * Getter of the physical locations of all the resources (information page)
	 * @return the physical locations of all the resources
	 */
	public ArrayList getDataResources()
	{
		ArrayList result = new ArrayList();
		
		for (int i=0; i<resources.size(); ++i)
		{
			result.add(((Resource) resources.get(i)).getUrl_root());
		}
		
		return result;
	}
	
	
	/**
	 * Getter of the physical location of one precise resource (information page)
	 * @param index index of the resource
	 * @return the physical location of one precise resource
	 */
	public String getDataResource(int index)
	{
		return (String) (((Resource) resources.get(index)).getUrl_root());
	}
	
	
	/**
	 * Getter of the physical locations (URLs) of all the pieces of documentation of the data-type
	 * @return physical locations of all the pieces of documentation of the data-type
	 */
	public ArrayList getDocumentationURLs()
	{
		return documentationURLs;
	}
	
	
	/**
	 * Getter of the physical location (URL) of one piece of documentation
	 * @param index index of one documentation
	 * @return physical location of one piece of documentation of the data-type
	 */
	public String getDocumentationURL(int index)
	{
		return (String) documentationURLs.get(index);
	}
	
	
	/**
	 * Setter of physical locations (URLs) of pieces of documentation of the data-type
	 * @param docs_url list physical locations (URLs)
	 */
	public void setDocumentationURLs(ArrayList docs_url)
	{
		for (int i=0; i<docs_url.size(); ++i)
		{
			this.documentationURLs.add(removeSpace((String) docs_url.get(i)));
		}
	}
	
	
	/**
	 * Getter of the identifier of all the pieces of documentation of the data-type
	 * @return identifiers of all the pieces of documentation of the data-type
	 */
	public ArrayList getDocumentationIDs()
	{
		return documentationIDs;
	}
	
	
	/**
	 * Getter of the identifier of one piece of documentation of the data-type
	 * @param index index of one documentation
	 * @return identifier of one piece of documentation of the data-type
	 */
	public String getDocumentationID(int index)
	{
		return (String) documentationIDs.get(index);
	}
	
	
	/**
	 * Setter of identifiers of pieces of documentation of the data-type
	 * @param docs_id list of identifiers (that can be managed/handled by Miriam Resources)
	 */
	public void setDocumentationIDs(ArrayList docs_id)
	{
		for (int i=0; i<docs_id.size(); ++i)
		{
			this.documentationIDs.add(removeSpace((String) docs_id.get(i)));
		}
	}
	
	
	/**
	 * Setter of the identifier of one piece of documentation of the data-type
	 * @param index index of one documentation
	 * @param id identifier of one documentation
	 */
	public void setDocumentationID(int index, String id)
	{
		documentationIDs.set(index, id);
	}
	
	
	/**
	 * Getter of the physical locations of ALL the pieces of documentation (even the documentations identified by an ID)
	 * @return list of the physical locations of ALL the pieces of documentation
	 */
	public ArrayList getDocHtmlURLs()
	{
		return docHtmlURLs;
	}
	
	
	/**
	 * Getter of one precise physical location of a piece of documentation
	 * @param index index of the physical location wanted
	 * @return one precise physical location of a piece of documentation
	 */
	public String getDocHtmlURL(int index)
	{
		return (String) docHtmlURLs.get(index);
	}
	
	
	/**
	 * Setter of the physical locations of ALL the pieces of documentation (even the documentations identified by an ID)
	 * @param docHtmlURLs list of the physical locations of ALL the pieces of documentation
	 */
	public void setDocHtmlURLs(ArrayList docHtmlURLs)
	{
		this.docHtmlURLs = docHtmlURLs;
	}
	
	
	/**
	 * Check all the mandatory parameters of the data-type, 
	 *  if something is missing or wrong, the method will return 'false'
	 * @return a boolean saying if the data-type is valid or not
	 */
	public boolean isValid()
	{
		return !(MiriamUtilities.isEmpty(getName()) || MiriamUtilities.isEmpty(getDefinition()) || MiriamUtilities.isEmpty(getRegexp()) || (MiriamUtilities.isEmpty(getURL()) && MiriamUtilities.isEmpty(getURN())) || ((getDataEntriesPrefix().isEmpty()) || (getDataResources().isEmpty())));
	}
	
	
	/**
	 * Checks if the data-type has (at least) one resource is official
	 * (there is at least one resource and the resources are not all deprecated)
	 * @return a boolean which says if the data-type has (at least) one official resource
	 */
	public boolean hasOfficialResource()
	{
		boolean result = false;
		
		for (int i=0; i<(getResources()).size(); ++i)
		{
			// one resource (at least) is not obsolete
			if (! (getResource(i)).isObsolete())
			{
				result = true;
			}
		}
		
		return result; 
	}
	
	/*
	 * Removes the space at the beginning and at the end of the chain of characters
	 * @param original a string that usually comes from a HTML from
	 * @return the same string without any space at the beginning and at the end
	 */
	private String removeSpace(String original)
	{
		String spaceFree = new String();
		int index;
		int begin = 0;
		int end = 0;
		
		if (MiriamUtilities.isEmpty(original))
		{
			spaceFree = "";
		}
		else
		{
			// checks the beginning of the string
			index = 0;
			while ((index<original.length()) && (original.charAt(index) == ' '))
			{
				index ++;
			}
			begin = index;
			
			// check the end of the string
			index = original.length() - 1;
			while ((index>0) && (original.charAt(index) == ' '))
			{
				index --;
			}
			end = index;
			
			// creation of the substring
			spaceFree = original.substring(begin, end+1);
		}
		
		return spaceFree;
	}
	
}
