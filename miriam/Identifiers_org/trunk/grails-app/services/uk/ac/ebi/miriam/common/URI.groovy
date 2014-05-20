package uk.ac.ebi.miriam.common

class URI
{
    /* type of URIs: URLs can be directly used in a Web browser, URNs need to be dereferenced for a usage on the Web */
    private static enum TypeURI {URN, URL}
    /* reference stored as a URI (can be a URN or a URL) */
    private String uri
    /* type of URI (cf. TypeURI) */
    private TypeURI type


    /**
     * Default constructor.
     */
    public URI()
    {
        this.uri = new String();
        this.type = TypeURI.URL;   // default value: URL
    }


    /**
     * Constructor allowing to set a type of URI.
     */
    public URI(String uri)
    {
        this.uri = new String();
        this.uri = uri.trim();
        this.type = getURIType(uri);
    }


    /**
     * Constructor per copy.
     * @param uri
     */
    public URI(URI uri)
    {
        this(uri.getURI());
    }


    /**
     * Constructor allowing to set a type of URI.
     * Warning: if the uri is updated and not of the given type, its type will be updated too!
     */
    public URI(TypeURI type)
    {
        this.uri = new String();
        this.type = type;
    }


    /**
     * Compares to objects and determine whether they are equivalent or not.
     * Mandatory method for the class to be able to implement 'Comparable'
     *
     * @param obj the reference object with which to compare
     * @return 0 if the two objects are the same, -1 otherwise
     */
    public int compareTo(Object obj)
    {
        if (this.equals(obj))
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }


    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj the reference object with which to compare
     * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        URI ref = (URI) obj;

        if (null != ref)
        {
            return (((this.getURI()).compareToIgnoreCase(ref.getURI()) == 0) && (this.getType().equals(ref.getType())));
        }
        else   // the object is null
        {
            return false;
        }
    }


    /**
     * Overrides the 'toString()' method for the 'Reference' object
     * @return a string which contains all the information about the reference
     */
    public String toString()
    {
        return this.getURI();
    }


    /**
     * Getter
     * @return the uri
     */
    public String getURI()
    {
        return this.uri;
    }


    /**
     * Setter (also updates the type of URI)
     * @param uri the uri to set
     */
    public void setURI(String uri)
    {
        this.uri = uri.trim();
        this.type = getURIType(uri);
    }


    /**
     * Getter
     * @return the type of URI
     */
    public String getType()
    {
        return this.type.name();
    }


    /**
     * Getter of the URN type of URI
     * @return URN type
     */
    public static TypeURI getTypeURN()
    {
        return TypeURI.URN;
    }


    /**
     * Getter of the URL type of URI
     * @return URL type
     */
    public static TypeURI getTypeURL()
    {
        return TypeURI.URL;
    }


    /*
     * Returns the type of a URI: URL or URN?
     * @param uri
     * @return
     */
    private TypeURI getURIType(String uri)
    {
        uri = uri.trim();

        if (uri.startsWith("urn:"))
        {
            return TypeURI.URN;
        }
        else
        {
            return  TypeURI.URL;
        }
    }

}
