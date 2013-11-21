import grails.util.Holders
import uk.ac.ebi.miriam.common.Constants

class InfoFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                if(params.entity == null && request.serverName.contains("info.") ) {
                    response.setStatus(301)
                    response.setHeader("Location", Holders.getGrailsApplication().config.grails.serverURL+request.forwardURI)
                    response.flushBuffer()
                    return false
                }else if(params.entity != null && !request.serverName.contains("info.") && !request.forwardURI.endsWith(".rdf") && request.queryString ==null){
                    response.setStatus(301)
                    response.setHeader("Location", Holders.getGrailsApplication().config.getProperty('subdomain')+request.forwardURI)
                    response.flushBuffer()
                    return false
                }
            }
/*            after = {


            }
            afterView = { Exception e ->

            }*/
        }
    }
}
