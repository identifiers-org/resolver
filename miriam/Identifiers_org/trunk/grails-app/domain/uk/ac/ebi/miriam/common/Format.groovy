package uk.ac.ebi.miriam.common

/**
 * Created by IntelliJ IDEA.
 * @author Sarala Wimalaratne
 * Date: 26/03/2014
 * Time: 11:55
 */
class Format {

    String id
    /* prefix part of the physical location (URL) */
    String urlPrefix
    /* suffix part of the physical location (URL) */
    String urlSuffix

/*    static hasOne = [mimetype: MimeType]*/
    MimeType mimeType

    static belongsTo = [resource:Resource]

    static mapping = {
        table "mir_res_formats"
        id column:"id"
        urlPrefix column:"url_prefix"
        urlSuffix column:"url_suffix"

        resource column:"ptr_resource", type:"string"   // foreign key
        mimeType column:"ptr_mimetype"

        version false  // no version column
    }

}
