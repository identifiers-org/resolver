package uk.ac.ebi.miriam.common

/**
 * Created by IntelliJ IDEA.
 * @author Sarala Wimalaratne
 * Date: 26/03/2014
 * Time: 13:04
 */
class MimeType {
    String id
    String mimetype
    String displaytext
    //static belongsTo = [format:Format]
    static hasMany = [format:Format]

    static mapping = {
        table "mir_mimetype"
        id column:"id"
        mimetype column:"mimetype"
        displaytext column:"displaytext"

        version false  // no version column
    }

}
