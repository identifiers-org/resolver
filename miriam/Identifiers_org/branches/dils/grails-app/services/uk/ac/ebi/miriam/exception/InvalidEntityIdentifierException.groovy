package uk.ac.ebi.miriam.exception

/**
 * Custom exception for handling cases when the entity identifier is invalid regarding the provided data collection.
 * Camille Laibe
 * 20110919
 */
class InvalidEntityIdentifierException extends Exception
{
    String dataCollection
    String identifier
    String regexp

    
    def InvalidEntityIdentifierException()
    {
        super()
    }

    def InvalidEntityIdentifierException(String message)
    {
        super(message)
    }

    def InvalidEntityIdentifierException(GString message, String dataCollection, String identifier, String regexp)
    {
        super(message)
        this.dataCollection = dataCollection
        this.identifier = identifier
        this.regexp = regexp
    }
}
