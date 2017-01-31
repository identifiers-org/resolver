package uk.ac.ebi.miriam.resolver

import grails.util.Holders
import groovy.json.JsonSlurper
import uk.ac.ebi.miriam.common.DataCollection
import uk.ac.ebi.miriam.common.Identifier
import uk.ac.ebi.miriam.common.Registry
import uk.ac.ebi.miriam.common.RegistryResult
import uk.ac.ebi.miriam.common.Resource
import uk.ac.ebi.miriam.common.Synonym

class RegistryController {

    static layout = 'main'

    def index() {
        Registry registry = new Registry();

        URL url = new URL(constructSearchURL())
        try {
            def result = new JsonSlurper().parseText(url.text)
            Map jsonResult = (Map) result
            populateResults(registry,jsonResult)
        }catch (IOException e){
            registry.message = "Invalid search query."
        }

        render(view: "registry" , model: [registry: registry ])
    }

    private void populateResults(Registry registry, Map jsonResult){
        Integer hitCount = (Integer) jsonResult.get("hitCount");
        registry.hitcount = hitCount

        List entries = (List) jsonResult.get("entries");

        entries.each {Map entry ->
            RegistryResult registryResult = new RegistryResult();
            String id = (String) entry.get("id");
            DataCollection dataCollection = DataCollection.findById(id)
            if(dataCollection!=null) {
                registryResult.name = dataCollection.name
                registryResult.type = RegistryResult.ResourceType.COLLECTION
                registryResult.description = dataCollection.definition
                registryResult.prefix = getNameSpace(dataCollection)
                registryResult.pattern = dataCollection.regexp
                registryResult.link = Holders.getGrailsApplication().config.grails.serverURL+"/"+ registryResult.prefix
                registryResult.synonyms = getSynonyms(dataCollection)
                registry.results.add(registryResult)
            }
            else {
                Resource resource = Resource.findById(id)
                if (resource != null) {
                    registryResult.name = resource.info
                   // System.out.println(registryResult.name + " "+ resource.dataCollection.definition);
                    registryResult.description = resource.dataCollection.definition
                    registryResult.type = RegistryResult.ResourceType.RESOURCE
                    registryResult.homepage = resource.urlRoot
                    registryResult.prefix = resource.prefix
                    registryResult.link = Holders.getGrailsApplication().config.grails.serverURL + "/miriam.resource/" + resource.id
                    registryResult.upTime = resource.reliability.uptimePercent()
                    registryResult.primary = resource.primary
                    registryResult.institute = resource.institution
                    registryResult.location = resource.location
                    registryResult.idorglink = Holders.getGrailsApplication().config.grails.serverURL+"/"+ getNameSpace(resource.dataCollection)+"/"+resource.exampleId
                    registry.results.add(registryResult)
                }
            }


        };


        if(hitCount!=0){
            registry.message = hitCount + " results found."
        }
        else{
            registry.message = "No results found."
        }

        if(params.query != null)
            registry.query = params.query;
    }

    private String getSynonyms(DataCollection dataCollection){
        if(dataCollection.synonyms.empty){
            return "";
        }

        String synonyms = "(";
        dataCollection.synonyms.each {Synonym synonym ->
            synonyms += synonym.name + ", "
        }
        synonyms = synonyms.substring(0,synonyms.length()-2) + ")"

    }

    def filter(){

        render(view: "registry")
    }

    private String constructSearchURL(){
        String searchUrl = Holders.getGrailsApplication().config.getProperty('searchURL')

        if(params.query==null || params.query.empty){
            searchUrl +="?query=domain_source:identifiers_registry"
        }else{

            searchUrl +="?query="+URLEncoder.encode(params.query, "UTF-8").replace("+", "%20")
        }

        searchUrl += "&format=json"

        if(params.offset!=null) {
            String limit = "&start=" + params.offset + "&size=" + params.max
            return searchUrl+limit;
        }
        return searchUrl;
    }

    private String getNameSpace(DataCollection dataCollection){
        List<Identifier> uris = Identifier.findAllByDataCollection(dataCollection);

        String namespace = null

        uris.each {
            if ((!it.deprecated) && (it.type == "URN"))
            {
                String uri = it.uriPrefix
                namespace = uri.substring(uri.lastIndexOf(":")+1)
            }
        }

        return namespace
    }
}
