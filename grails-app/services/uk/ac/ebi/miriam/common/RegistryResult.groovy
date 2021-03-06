package uk.ac.ebi.miriam.common

/**
 * Created by sarala on 20/01/2017.
 */
class RegistryResult {

    public enum ResourceType {COLLECTION, RESOURCE}

    String name
    ResourceType type
    String description
    String prefix
    String link
    String homepage
    Integer upTime
    Boolean primary
    String pattern

    String institute
    String location
    String synonyms
    String idorglink


    public String getShortDescription(){
        if(description.length()<150){
            return description
        }
        return description.substring(0,150) + "..."
    }
}
