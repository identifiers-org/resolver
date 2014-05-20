package uk.ac.ebi.miriam.common

import javax.xml.bind.annotation.XmlAttribute


class URIextended extends URI
{
    @XmlAttribute(name="deprecated", required=false)
    private Boolean obsolete;


    /**
     * Default constructor.
     */
    public URIextended()
    {
        super();
        this.obsolete = false;
    }


    /**
     * Constructor with parameters (URI).
     * @param obsolete
     */
    public URIextended(URI uri, Boolean obsolete)
    {
        super(uri);
        this.obsolete = obsolete;
    }


    /**
     * COnstructor with parameters (String).
     * @param uri
     * @param obsolete
     */
    public URIextended(String uri, Boolean obsolete)
    {
        super(uri);
        this.obsolete = obsolete;
    }


    /**
     * COnstructor with parameters (String).
     * @param uri
     * @param obsolete
     */
    public URIextended(String uri, int obsolete)
    {
        super(uri);
        if (obsolete == 0)
        {
            this.obsolete = false;
        }
        else
        {
            this.obsolete = true;
        }
    }


    /**
     * Getter
     * @return the obsolete
     */
    public Boolean isObsolete()
    {
        return this.obsolete;
    }


    /**
     * Setter
     * @param obsolete the obsolete to set
     */
    public void setObsolete(Boolean obsolete)
    {
        this.obsolete = obsolete;
    }
}
