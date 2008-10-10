/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2008  Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */


package uk.ac.ebi.miriam.db;


/**
 * <p>
 * Object which stores all the information about a resource (= a physical location of a data-type).
 * <p>
 * Implements <code>Comparable</code> to be able to use the objects of this class inside a <code>TreeSet</code>
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2008 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 * <br />
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br />
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </dd>
 * </dl>
 * </p>
 *
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20070522
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
   * <p>Constructor by default (empty object).
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
     * Tests if two <code>Resource</code> objects are the same (only checks the ID).
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Resource res)
    {
        return (this.id.equals(res.id));
    }


    /**
     * Checks if two <code>Resource</code> objects have the same content (and same ID).
     * @param res the other <code>Resource</code> to compare to
     * @return
     */
    public boolean hasSameContent(Resource res)
    {
        return ((this.id.equals(res.id)) &&
                (this.url_prefix.equals(res.url_prefix)) &&
                (this.url_suffix.equals(res.url_suffix)) &&
                (this.url_root.equals(res.url_root)) &&
                (this.info.equals(res.info)) &&
                (this.institution.equals(res.institution)) &&
                (this.location.equals(res.location)) &&
                (this.obsolete == res.obsolete));
    }


    /**
     * Checks if two <code>Resource</code> are similar (based on a statistics studies).
     *
     * <p>
     * 7 attributes take into account (url_prefix, url_suffix, url_root, info, institution, location, obsolete).
     *
     * @param res the other <code>Resource</code> to compare to
     * @return 'true' if number of similarities >= 4 (7 attributes tested)
     */
    public boolean couldBeSimilar(Resource res)
    {
        int nb = 0;

        if (this.url_prefix.equals(res.url_prefix))
        {
            nb ++;
        }
        if (this.url_suffix.equals(res.url_suffix)){
            nb ++;
        }
        if (this.url_root.equals(res.url_root))
        {
            nb ++;
        }
        if (this.info.equals(res.info))
        {
            nb ++;
        }
        if (this.institution.equals(res.institution))
        {
            nb ++;
        }
        if (this.location.equals(res.location))
        {
            nb ++;
        }
        if (this.obsolete == res.obsolete)
        {
            nb ++;
        }

        return (nb >= 4);
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
