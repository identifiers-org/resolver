/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.db;


/**
 * <p>
 * Object which stores all the information about a resource (= a physical location of a data-type).
 * <p>
 * Implements 'Comparable' to be able to use the objects of this class inside a 'TreeSet' 
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
public class Resource implements Comparable
{
	/* stable identifier of the resource (something starting by 'MIR:001' and followed by 5 digits) */
	private String id = new String();
	/* prefix part of the physical location (URL) */
	private String url_prefix = new String();
	/* suffix part of the physical location (URL) */
	private String url_suffix = new String();
	/* address of the front page of the resource */
	private String url_root = new String();
	/* some useful information about the resource */
	private String info = new String();
	/* institution which manage the resource */
	private String institution = new String();
	/* country of the institution */
	private String location = new String();
	/* is the resource obsolete or not? */
	private boolean obsolete;
	
	
	/**
	 * Construtor by default
	 */
	public Resource()
	{
		// default parameters
		this.obsolete = false;
	}
	
	
	/**
	 * Overrides the 'toString()' method for the 'Resource' object
	 * @return a string which contains all the information about the resource
	 */
	public String toString()
	{
		String tmp = new String();
		
		tmp += "       - ID:          " + getId() + "\n";
		tmp += "       - URL prefix:  " + getUrl_prefix() + "\n";
		tmp += "       - URL suffix:  " + getUrl_suffix() + "\n";
		tmp += "       - URL root:    " + getUrl_root() + "\n";
		tmp += "       - Information: " + getInfo() + "\n";
		tmp += "       - Institution: " + getInstitution() + "\n";
		tmp += "       - Location:    " + getLocation() + "\n";
		tmp += "       - Obsolete:    " + isObsolete() + "\n";
		
		return tmp;
	}
	
	
	/**
	 * Getter of the stable identifier of the resource
	 * @return the stable identifier of the resource
	 */
	public String getId()
	{
		return id;
	}
	
	
	/**
	 * Setter of the stable identifier of the resource
	 * @param id the stable identifier of the resource
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
	
	/**
	 * Getter of some general information about the resource
	 * @return some general information about the resource
	 */
	public String getInfo()
	{
		return info;
	}
	
	
	/**
	 * Setter of some general information about the resource
	 * @param info some general information about the resource
	 */
	public void setInfo(String info)
	{
		this.info = info;
	}
	
	/**
	 * Getter of the institution managing the resource
	 * @return the institution managing the resource
	 */
	public String getInstitution()
	{
		return institution;
	}
	
	
	/**
	 * Setter of the institution managing the resource
	 * @param institution the institution managing the resource
	 */
	public void setInstitution(String institution)
	{
		this.institution = institution;
	}
	
	
	/**
	 * Getter of the country of the institution
	 * @return the country of the institution
	 */
	public String getLocation()
	{
		return location;
	}
	
	
	/**
	 * Setter of the country of the institution
	 * @param location the country of the institution
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}
	
	
	/**
	 * Getter of the obsolete parameter
	 * @return if the resource is obsolete or not
	 */
	public boolean isObsolete()
	{
		return obsolete;
	}
	
	
	/**
	 * Setter of the obsolete parameter
	 * @param obsolete the resource is obsolete or not (that is the question)
	 */
	public void setObsolete(boolean obsolete)
	{
		this.obsolete = obsolete;
	}
	
	
	/**
	 * Getter of the prefix part of the address (link to an element)
	 * @return the prefix part of the address (link to an element)
	 */
	public String getUrl_prefix()
	{
		return url_prefix;
	}
	
	
	/**
	 * Setter of the prefix part of the address (link to an element)
	 * @param url_prefix the prefix part of the address (link to an element)
	 */
	public void setUrl_prefix(String url_prefix)
	{
		this.url_prefix = url_prefix;
	}
	
	
	/**
	 * Getter of the resource address (front page)
	 * @return the resource address (front page)
	 */
	public String getUrl_root()
	{
		return url_root;
	}
	
	
	/**
	 * Setter of the resource address (front page)
	 * @param url_root the resource address (front page)
	 */
	public void setUrl_root(String url_root)
	{
		this.url_root = url_root;
	}
	
	
	/**
	 * Getter of the suffix part of the address (link to an element)
	 * @return the suffix part of the address (link to an element)
	 */
	public String getUrl_suffix()
	{
		return url_suffix;
	}
	
	
	/**
	 * Setter of the suffix part of the address (link to an element)
	 * @param url_suffix the suffix part of the address (link to an element)
	 */
	public void setUrl_suffix(String url_suffix)
	{
		this.url_suffix = url_suffix;
	}
	
	
	/**
	 * Compares to objects and determine whether they are equicalent or not
	 * Mandatory method for the class to be able to implements 'Comparable'
	 * <p>
	 * WARNING: the test only uses the ID of the Resource object!
	 * @param an unknown object 
	 * @return 0 if the two objects are the same
	 */
	public int compareTo(Object obj)
	{
		Resource res = (Resource) obj;
		
		// different identifiers
		if ((this.getId()).compareToIgnoreCase(res.getId()) != 0)
		{
			return -1;
		}
		else   // same identifier
		{
			return 0;
		}
	}

}
