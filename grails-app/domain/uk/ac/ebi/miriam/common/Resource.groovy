package uk.ac.ebi.miriam.common


/**
 * Contains all the information about a MIRIAM resource.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140520
 */
class Resource
{
    /* stable identifier of the resource (something starting by 'MIR:001' and followed by 5 digits) */
    String id
    /* prefix part of the physical location (URL) */
    String urlPrefix
    /* suffix part of the physical location (URL) */
    String urlSuffix
    /* address of the front page of the resource */
    String urlRoot
    /* some useful information about the resource */
    String info
    /* institution which manage the resource */
    String institution
    /* country of the institution */
    String location   // optional
    /* example of an identifier used by this resource */
    String exampleId
    /* is the resource obsolete or not? */
    Boolean obsolete
    /* is the resource allowing loading of its content from a frame? */
    Boolean deniesFrame
    /* whether the resource is the primary/official one of the data collection (optional) */
    Boolean primary
    /* Internet media type returned */
    //String mime = "application/xhtml+xml"   // TODO: this should not be hard coded!
    /* URI prefix for conversion purposes */
    String convertPrefix

    String resource_prefix

    // GORM: unidirectional many-to-one relationship
    static belongsTo = [dataCollection:DataCollection]
    // GORM: unidirectional one-to-one relationship
    static hasOne = [reliability:Reliability]   // percentage of reliability (uptime)

    static hasMany = [format:Format]


    static mapping = {
        table "mir_resource"
        id generator:"assigned", column:"resource_id", type:"string"  // name:"id"
        urlPrefix column:"url_element_prefix"
        urlSuffix column:"url_element_suffix"
        urlRoot column:"url_resource"
        info column:"info"
        institution column:"institution"
        location column:"location"
        exampleId column:"example"
        obsolete column:"obsolete"
        deniesFrame column:"frame_deny"
        primary column:"official"
//        isAResource column:"resource"
//        isAUriScheme column:"urischeme"

        dataCollection column:"ptr_datatype", type:"string"   // foreign key
        reliability fetch:"join"  // lazy:false
        format fetch:"join"
        
        version false   // no version column
//        uriScheme column:"urischeme"
        convertPrefix column:"convertPrefix"
        resource_prefix column:"resource_prefix"
    }
}
