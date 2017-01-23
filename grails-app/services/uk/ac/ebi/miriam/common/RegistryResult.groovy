package uk.ac.ebi.miriam.common

/**
 * Created by sarala on 20/01/2017.
 */
class RegistryResult {

    public enum ResourceType {COLLECTION, RESOURCE}

    String name;
    ResourceType type;
    String description;
    String prefix;
    String link;
    String homepage;
    Integer upTime;
    Boolean primary;

}
