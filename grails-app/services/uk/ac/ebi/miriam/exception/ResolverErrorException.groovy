package uk.ac.ebi.miriam.exception

/**
 * Custom exception for handling cases when an error occurred on the server side.
 * Camille Laibe
 * 20110930
 */
class ResolverErrorException extends Exception
{
    String dataCollection


    def ResolverErrorException()
    {
        super()
    }

    def ResolverErrorException(String message)
    {
        super(message)
    }

    def ResolverErrorException(GString message, String dataCollection)
    {
        super(message)
        this.dataCollection = dataCollection
    }
}
