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

    String deprecated

//    static hasOne = [mimetype: MimeType]
//    MimeType mimetype

    static belongsTo = [resource:Resource, mimetype:MimeType]

    static mapping = {
        table "mir_res_formats"
        id column:"id"
        urlPrefix column:"url_prefix"
        urlSuffix column:"url_suffix"
        deprecated column:"deprecated"

        resource column:"ptr_resource", type:"string"   // foreign key
        mimetype column:"ptr_mimetype", type:"string"
 //       mimetype column:"ptr_mimetype"
        mimetype fetch:"join"

        version false  // no version column
    }

}
