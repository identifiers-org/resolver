package uk.ac.ebi.miriam.exception

/**
 * Custom exception for handling case when a requested data collection does not exist.
 * Camille Laibe
 * 20110919
 */

class NotExistingDataCollectionException extends Exception
{
    String dataCollection

    def NotExistingDataCollectionException()
    {
        super()
    }

    def NotExistingDataCollectionException(String message)
    {
        super(message)
    }

    def NotExistingDataCollectionException(GString message, String dataCollection)
    {
        super(dataCollection)
        this.dataCollection = dataCollection
    }
}
