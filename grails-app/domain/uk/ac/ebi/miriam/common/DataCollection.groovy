package uk.ac.ebi.miriam.common

/**
 * Contains all the information about a MIRIAM data collection.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2013 BioModels.net (EMBL - European Bioinformatics Institute)
 * <br />
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br />
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </dd>
 * </dl>
 * </p>
 *
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20130426
 */
class DataCollection implements Comparable
{
    /* stable identifier of the data collection (something starting by 'MIR:000' and followed by 5 digits) */
    String id
    /* official name of the data collection */
    String name
    /* synonyms of the name of the data collection */
    // TODO: List<String> synonyms
    /* official URN of the data collection */
    // TODO: String URN
    /* official URL of the data collection */
    // TODO: String URL
    /* deprecated URIs */
    // TODO: List<Identifier> deprecatedURIs
    // GORM one-to-many relationships
    static hasMany = [uris: Identifier, resources: Resource, synonyms: Synonym]
    /* definition of the data collection */
    String definition
    /* regular expression of the identifiers used by the data collection */
    String regexp
    /* resources (= physical locations) */
    //List<Resource> resources
    /* list of references towards pieces of documentation (can be MIRIAM URIs or URLs) */
    //List<Documentation> docs
    /* list of tags (with their definition) associated to the data collection */
    //Map<String, String> tags
    /* date of creation of the data collection (the Date and String versions are linked and are modified together) */
    Date dateCreation
    /* date of last modification of the data collection (the Date and String versions are linked and are modified together) */
    Date dateModification
    /* if the data collection is obsolete or not */
    Boolean obsolete;   // TODO: test that (int in the database)!
    /* why the data collection is obsolete */
    String obsoleteComment
    /* if the data collection is obsolete, this field must have a value */
    String replacedBy
    
    // database related configuration, for Hibernate
    static mapping = {
		table "mir_datatype"
		id generator:"assigned", column:"datatype_id", type:"string"  // name:"id", sqlType:"char", length:12
        name column:"name"
        synonyms fetch:"join"
        uris fetch:"join"   // foreign key in table 'mir_uri'   // column:"ptr_datatype", type:"string",
        resources fetch:"join"   // foreign key in table 'mir_resource'  column:"ptr_datatype", fetch:"select", lazy:false, type:"string",
        definition column:"definition", type:"text"   // longer than varchar(255)
        regexp column:"pattern", enumType:"string", sqlType:"varchar(255)"   // to be safer with the handling of special characters in regular expressions
        dateCreation column:"date_creation"
        dateModification column:"date_modif"
        obsolete column:"obsolete"
        obsoleteComment column:"obsolete_comment", type:"text"   // longer than varchar(255)
        replacedBy column:"replacement"
        version false   // no version column

        // synonyms
        //hasMany joinTable: [name: 'mir_synonyms', key: 'ptr_datatype', column: 'name', type: "text"]
	}

    
    int compareTo(obj)
    {
       id.compareTo(obj.id)
    }

    /**
     * Retrieves the official URN
     * @return
     */
    String officialUrn()
    {
        String urn = null
        
        uris.each {
            if ((!it.deprecated) && (it.type == "URN"))
            {
                urn = it.uriPrefix   // returns the first (and hopefully only) URI which is not deprecated and is a URN
            }
        }

        return urn
    }

    /**
     * Retrieves the official URL
     * @return
     */
    String officialUrl()
    {
        return Constants.RESOLVER_URL_ROOT + "/" + officialUrn().substring(11) + "/"  // we enforce the final '/'
    }
}
