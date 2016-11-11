package uk.ac.ebi.miriam.common

class PrefixAlias {

    long id

    String name

    static belongsTo = [dataCollection:DataCollection]

    static mapping = {
        table "alias_prefix"
        id generator: "increment"
        name column: "name"
        dataCollection column:"datatype_id", type:"string"
        version false
    }
}
