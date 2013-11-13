import uk.ac.ebi.miriam.common.Constants

class InfoFilters {

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                if(params.entity == null && request.serverName.contains("info.") ) {
                    response.setStatus(301)
                    response.setHeader("Location", Constants.RESOLVER_URL_ROOT+request.forwardURI)
                    response.flushBuffer()
                    return false
                }else if(params.entity != null && !request.serverName.contains("info.") && !request.forwardURI.endsWith(".rdf")){
                    response.setStatus(301)
                    response.setHeader("Location", Constants.RESOLVER_SUBDOM+request.forwardURI)
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
