package uk.ac.ebi.miriam.resolver

import grails.converters.JSON
import groovy.json.JsonSlurper
import uk.ac.ebi.miriam.common.DataCollection
import uk.ac.ebi.miriam.common.Registry
import uk.ac.ebi.miriam.common.RegistryResult
import uk.ac.ebi.miriam.common.Resource

class RegistryController {

    static layout = 'main'

    def index() {
        Registry registry = new Registry();

        URL url = new URL("http://wwwdev.ebi.ac.uk/ebisearch/ws/rest/identifiers_registry?query=domain_source:identifiers_registry&format=json")
        def result = new JsonSlurper().parseText(url.text)

        Map jsonResult = (Map) result;

        List entries = (List) jsonResult.get("entries");

        entries.each {Map entry ->
            RegistryResult registryResult = new RegistryResult();
            String id = (String) entry.get("id");
            DataCollection dataCollection = DataCollection.findById(id)
            if(dataCollection!=null) {
                registryResult.name = dataCollection.name
                registryResult.type = RegistryResult.ResourceType.COLLECTION
                registryResult.description = dataCollection.definition
                registryResult.prefix = dataCollection.namespace()
                registryResult.link = "http://identifiers.org/miriam.collection/"+dataCollection.id

            }
            else {
                Resource resource = Resource.findById(id)
                if (resource != null) {
                    registryResult.name = resource.info
                    registryResult.type = RegistryResult.ResourceType.RESOURCE
                    registryResult.homepage = resource.urlRoot
                    registryResult.prefix = resource.prefix
                    registryResult.link = "http://identifiers.org/miriam.resource/" + resource.id
                    registryResult.upTime = resource.reliability.uptimePercent()
                    registryResult.primary = resource.primary
                }
            }
            registry.results.add(registryResult)
        };

        render(view: "registry" , model: [registry: registry ])
    }

    def filter(){

        render(view: "registry")
    }
}
