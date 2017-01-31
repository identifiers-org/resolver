package uk.ac.ebi.miriam.common

class TagLink {

    String id

    static belongsTo = [dataCollection:DataCollection, tag:Tag]


    static mapping = {
        table "mir_tag_link"
        id column:"id"
        dataCollection column:"ptr_datatype"
        tag column:"ptr_tag"
        dataCollection fetch: "join"
        tag fetch: "join"
        version false   // no version column
    }

    static constraints = {
    }
}
