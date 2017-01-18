package uk.ac.ebi.miriam.resolver

import uk.ac.ebi.miriam.common.DataCollection
import uk.ac.ebi.miriam.xml.XMLGenerator

import java.text.DateFormat
import java.text.SimpleDateFormat

class ServiceController {

    static layout = 'main'

    def index() {
        render(view: "service")
    }

    def info() {
        render (view: "info")
    }

    def restws() {
        render (view: "restws")
    }

    def registryxml(){
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        def lastModified = DataCollection.find("FROM DataCollection ORDER BY dateModification desc").dateModification

        response.setHeader("Last-Modified",df.format(lastModified));

        render(text: XMLGenerator.getRegistryXML(df,lastModified).toString(), contentType: "text/xml", encoding: "UTF-8")
    }
}
