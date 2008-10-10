/*
 * Author: Camille Laibe
 * Copyright: EMBL-EBI, Computational Neurobiology Group
 * Project: Miriam
 */


package uk.ac.ebi.miriam.web;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.DbPoolConnect;


/**
 * <p>
 * Useful methods for the session tracking, handling ResultSet, and other cool stuff...
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>EMBL-EBI, Computational Neurobiology Group</dd>
 * </dl>
 * 
 * @author Camille Laibe
 * @version 20061214
 */
public class MiriamUtilities
{
	private static Logger logger = Logger.getLogger(MiriamUtilities.class);   // static logger in a web app: not good?
	// user who can add and modify data-types
	public static final String USER_AUTHORIZED = "miriam";
	// god
	public static final String USER_ROOT = "admi";
	
	
	/**
	 * Test if a session is valid (equals the user is logged)
	 * @param HttpSession current session
	 * @return boolean true is a user is logged, false if not
	 */
	public static boolean isSessionValid(HttpSession session)
	{
		if ((session != null) && (session.getAttribute("login") != null) && (session.getAttribute("role") != null))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	/**
	 * Test if a user is logged in, and if true, if he is an authorized member 
	 * @param HttpSession current session
	 * @return boolean answer to the question: is the user an authorized member?
	 */
	public static boolean isUserAuthorized(HttpSession session)
	{
		boolean result = false;
		String role = new String();
		
		if (isSessionValid(session))
		{
			role = (String) session.getAttribute("role");
			if (role.equalsIgnoreCase(USER_AUTHORIZED))
			{
				result = true;
			}
		}
		
		return result;
	}
	
	
	/**
	 * Test if a user is logged in, and if true, if he is an administrator
	 * @param HttpSEssion current session
	 * @return boolean answer to the question: is the user an administrator?
	 */
	public static boolean isUserAdministator(HttpSession session)
	{
		boolean result = false;
		String role = new String();
		
		if (isSessionValid(session))
		{
			role = (String) session.getAttribute("role");
			if (role.equalsIgnoreCase(USER_ROOT))
			{
				result = true;
			}
		}
		
		return result;
	}
	
	
	/**
	 * Count the number of rows in a ResultSet (like 'getColumnCount()' for the columns)
	 * @param ResultSet object already created
	 * @return int number of rows in the ResultSet object
	 */
	public static int getRowCount(ResultSet data)
	{
		int result = -1;
		
		try
		{
			data.last();
			result = data.getRow();
			data.first();
		}
		catch (SQLException e)
		{
			// nothing to do here
		}
		
		return result;
	}
	
	
	/**
	 * Converts a ResultSet (from a SQL query) to an ArrayList
	 * @param result result of a SQL query
	 * @return an ArrayList Object with all the elements in the first field (column) of the ResultSet
	 */
	public static ArrayList ArrayConvert(ResultSet result)
	{
		ArrayList conv = new ArrayList();
		
		int nbLines = DbPoolConnect.getRowCount(result);
		
		for (int i=1; i<=nbLines; ++i)
		{
			try
			{
				conv.add(result.getString(1));
				result.next();
			}
			catch (SQLException e)
			{
				logger.warn("An exception occured during the conversion of a ResultSet to an ArrayList!");
				logger.warn("SQL Exception raised: " + e.getMessage());
			}
			
		}
		
		return conv;
	}
	
	/**
	 * Converts a ResultSet (from a SQL query) to an ArrayList, with a transformation of the elements
	 * (for example,if they are designed to be included in a physical URL, for valid XHTML links) 
	 * @param result result of a SQL query
	 * @return an ArrayList Object with all the elements in the first field (column) of the ResultSet
	 */
	public static ArrayList ArrayConvert(ResultSet result, boolean URL)
	{
		ArrayList conv = new ArrayList();
		
		int nbLines = DbPoolConnect.getRowCount(result);
		for (int i=1; i<=nbLines; ++i)
		{
			try
			{
				if (URL)
				{
					conv.add(transURL(result.getString(1), '&', "&amp;"));
				}
				else
				{
					conv.add(result.getString(1));	
				}
				result.next();
			}
			catch (SQLException e)
			{
				logger.warn("An exception occured during the conversion of a ResultSet to an ArrayList!");
				logger.warn("SQL Exception raised: " + e.getMessage());
			}
			
		}
		
		return conv;
	}
	
	
	/**
	 * Converts a ResultSet (from a SQL query) to an ArrayList
	 * @param result result of a SQL query
	 * @param nbCol number of columns in the ResultSet to convert
	 * @return an ArrayList Object with all the elements in the 'nbCols' first fields (columns) of the ResultSet
	 */
	public static ArrayList ArrayConvert(ResultSet result, int nbCol)
	{
		ArrayList conv = new ArrayList();
		ArrayList[] temp = new ArrayList[nbCol];
		
		for (int i=0; i<nbCol; ++i)
		{
			temp[i] = new ArrayList();
		}
		
		int nbLines = DbPoolConnect.getRowCount(result);
		for (int i=0; i<nbLines; ++i)
		{
			try
			{
				for (int j=0; j<nbCol; ++j)
				{
					temp[j].add(result.getString(j+1));
				}
				result.next();
			}
			catch (SQLException e)
			{
				logger.warn("An exception occured during the conversion of a ResultSet to an ArrayList!");
				logger.warn("SQL Exception raised: " + e.getMessage());
			}
		}
		
		for (int i=0; i<nbCol; ++i)
		{
			conv.add(temp[i]);
		}
		
		return conv;
	}
	
	/**
	 * Converts a ResultSet (from a SQL query) to an ArrayList, with a transformation of the elements
	 * (for example,if they are designed to be included in a physical URL, for valid XHTML links) 
	 * @param result result of a SQL query
	 * @param nbCol number of columns in the ResultSet to convert
	 * @param URL indicates if the String values need or not to be converted into a XHTML valid format (cf. '&')
	 * @return an ArrayList Object with all the elements in the 'nbCols' first fields (columns) of the ResultSet
	 */
	public static ArrayList ArrayConvert(ResultSet result, int nbCol, boolean URL)
	{
		ArrayList conv = new ArrayList();
		ArrayList[] temp = new ArrayList[nbCol];
		
		for (int i=0; i<nbCol; ++i)
		{
			temp[i] = new ArrayList();
		}
		
		int nbLines = DbPoolConnect.getRowCount(result);
		for (int i=0; i<nbLines; ++i)
		{
			try
			{
				for (int j=0; j<nbCol; ++j)
				{
					if (URL)
					{
						temp[j].add(transURL(result.getString(j+1), '&', "&amp;"));
					}
					else
					{
						temp[j].add(result.getString(j+1));
					}
				}
				result.next();
			}
			catch (SQLException e)
			{
				logger.warn("An exception occured during the conversion of a ResultSet to an ArrayList!");
				logger.warn("SQL Exception raised: " + e.getMessage());
			}
		}
		
		for (int i=0; i<nbCol; ++i)
		{
			conv.add(temp[i]);
		}
		
		return conv;
	}
	
	
	/**
	 * Converts a ResultSet from aSQL query (only the first element) to a String
	 * @param result result of a SQL query
	 * @return a String Object with the first element (first column, first row) of the ResultSet
	 */
	public static String StringConvert(ResultSet result)
	{
		String conv = new String();
		
		try
		{
			if (result.first())
			{
				conv = result.getString(1);
			}
		}
		catch (SQLException e)
		{
			logger.warn("An exception occured during the conversion of a ResultSet to a String!");
			logger.warn("SQL Exception raised: " + e.getMessage());
		}
		
		return conv;
	}
	
	
	/**
	 * Replaces a Substrings in a String
	 * @param str original String
	 * @param pattern
	 * @param replace
	 * @return modified character string
	 */
	public static String replace(String base, String pattern, String replace)
	{
        int begin = 0;
        int end = 0;
        StringBuffer result = new StringBuffer();
        
        while ((end = base.indexOf(pattern, begin)) >= 0)
        {
            result.append(base.substring(begin, end));
            result.append(replace);
            begin = end + pattern.length();
        }
        result.append(base.substring(begin));
        
        return result.toString();
    }
	
	/**
	 * Converts a URL into a (X)HTML valid way: replace '&' by '&amp;'
	 * @param url physical URL
	 * @return the same URL, but W3C valid 
	 */
	public static String urlConvert(String url)
	{
		String valid = new String();
		valid = replace(url, "&", "&amp;");
		
		return valid;
	}
	
	/**
	 * Returns the String passed in parameter 
	 * (used for example in URLs to have valid XHTML links),
	 * replacing the 'pattern' character by the "replace" string
	 * @param original character string
	 * @param pattern
	 * @param replace
	 * @return modified character string
	 */
	public static String transURL(String str, char pattern, String replace)
	{
		String newStr = "";
		
		for (int j=0; j<str.length(); ++j)
		{
			if (str.charAt(j) == pattern)
			{
				newStr += replace;
			}
			else
			{
				newStr += str.charAt(j);
			}
		}
		
		return newStr;
	}
	
	
	/**
	 * Tests if a string is composed only of space(s) or empty or null
	 * @param str character string
	 * @return response to the question: "is this character string only composed of space(s)?"
	 */
	public static boolean isEmpty(String str)
	{
		boolean space = true;
		
		if ((str == null) || (str.equalsIgnoreCase("")))
		{
			return space;   // true
		}
		else
		{
			for (int i=0; i<str.length(); ++i)
			{
				if (str.charAt(i) != ' ')
				{
					space = false;
				}
			}
		}
			
		return space;
	}
	
	
	/**
	 * Returns a new String equivalent to the string in parameter,
	 * but with all the spaces replaced by '%20' (to have valid XHTML links)
	 * @param oldStr a string (usually a name with space)
	 * @return the string in parameter without any space but "%20" instead
	 */
	public static String nameTrans(String oldStr)
	{
		String newStr = "";
		
		for (int j=0; j<oldStr.length(); ++j)
		{
			if (oldStr.charAt(j) == ' ')
			{
				newStr += "%20";
			}
			else
			{
				newStr += oldStr.charAt(j);
			}
		}
		
		return newStr;
	}
	
	
	/**
	 * Returns the data-type part of a URI (everything before "#")
	 * @param uri a URI (example: "http://www.pubmed.gov/#10812475")
	 * @return the data-type part of a URI (everything before "#")
	 */
	public static String getDataPart(String uri)
	{
		int index = uri.lastIndexOf("#");
		
		if (index != -1)
		{
			return uri.substring(0, index);
		}
		// "#" not found
		else
		{
			return uri;
		}
	}
	
	
	/**
	 * Returns the identifier part of a URI (everything before "#")
	 * @param uri a URI (example: "http://www.pubmed.gov/#10812475")
	 * @return the identifier part of a URI (everything before "#")
	 */
	public static String getElementPart(String uri)
	{
		int index = uri.lastIndexOf("#");
		
		if (index != -1)
		{
			return uri.substring(index+1, uri.length());
		}
		// "#" not found
		else
		{
			return uri;
		}
	}
}
