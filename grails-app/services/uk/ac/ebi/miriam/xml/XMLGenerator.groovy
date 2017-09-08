package uk.ac.ebi.miriam.xml

import uk.ac.ebi.miriam.common.*

import java.text.DateFormat

/**
 * Created by sarala on 16/01/2017.
 */
class XMLGenerator {

    public static StringWriter getRegistryXML(DateFormat df, Date lastModified){
        def sw = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(sw)

        xml.setOmitEmptyAttributes(true)
        xml.setOmitNullAttributes(true)

        xml.miriam(date:df.format(new Date()),last_modified:df.format(lastModified)){
            DataCollection.list(sort:"id").each { DataCollection dc ->
                collection(id: dc.id, obsolete: dc.obsolete) {
                    name(dc.name)
                    if (dc.synonyms) {
                        synonyms() {
                            dc.synonyms.each { Synonym s ->
                                synonym(s.name)
                            }
                        }
                    }
                    namespace(dc.namespace())
                    if (dc.aliasPrefixes) {
                        aliases() {
                            dc.aliasPrefixes.each { PrefixAlias pa ->
                                alias(pa.name)
                            }
                        }
                    }
                    pattern(dc.regexp)
                    definition(dc.definition)
                    prefixed(dc.prefixed_id)

                    urischemes() {
                        dc.uris.each { Identifier id ->
                            urischeme(type: id.type, deprecated: id.deprecated, id.uriPrefix)
                        }
                    }

                    if (dc.taglink) {
                        tags() {
                            dc.taglink.each { TagLink tl ->
                                tag(tl.tag.name)
                            }
                        }
                    }

                    resources() {
                        dc.resources.each { Resource r ->
                            resource(id: r.id, primary: r.primary, obsolete: r.obsolete) {
                                if (r.prefix)
                                    provider_code(r.prefix)
                                title(r.info)
                                redirect(r.urlPrefix + '$id' + r.urlSuffix)
                                test(r.exampleId)
                                homepage(r.urlRoot)
                                institution(r.institution)
                                location(r.location)

                                Reliability rb = r.reliability
                                if (rb) {
                                    keyword(rb.keyword)
                                    status(state: rb.getHumanState(rb.state), reliability: rb.uptimePercent())
                                }
                            }
                        }
                    }

                }
            }
        }
        return sw
    }



}
