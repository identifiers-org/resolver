package uk.ac.ebi.miriam.common

/**
 * Contains all the information about a MIRIAM Identifier.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20110930
 */
class Identifier
{
    // internal identifier
    Integer id
    // actual (beginning of -does not include the entity part-) Identifier
    String uriPrefix
    // URN or URL
    //IdentifierType type
    //UriType type
    String type
    // whether or not the Identifier is deprecated
    Boolean deprecated

    // GORM: unidirectional many-to-one relationship
    static belongsTo = [dataCollection:DataCollection]

    static mapping = {
		table "mir_uri"
		id name:"id", generator:"assigned", column:"uri_id", type:"integer"
        uriPrefix column:"uri"
        type column:"uri_type"//, sqlType:"enum", type:"enum"   //columnDefinition:"enum('URN','URL')"   //, type:"enum"   // Found: enum, expected: varchar(255)
        deprecated column:"deprecated"
        dataCollection column:"ptr_datatype", type:"string"  // foreign key
        version false   // no version column
	}

/*
    public static enum UriType
    {
	    urn, url
    }
*/
}
