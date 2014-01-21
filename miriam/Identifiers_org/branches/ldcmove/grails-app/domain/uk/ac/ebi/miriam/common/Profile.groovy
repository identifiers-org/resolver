package uk.ac.ebi.miriam.common

import uk.ac.ebi.miriam.resolver.Resolver

/*
 * Contains all the information about a myMIRIAM profile.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2012 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20120823
 */
class Profile
{
    /* internal identifier of the myMIRIAM profile */
    Integer id
    /* full name of the myMIRIAM profile */
    String name
    /* shortname of a profile (no space, no special character) */
    String shortname
    /* description of the profile */
    String description
    /* whether the myMIRIAM profile is public or not */
    Boolean open
    /* encrypted version of the key/password */
    String key
    /* contact email address */
    String contact
    /* date of creation */
    Date dateCreated

    // GORM one-to-many relationships
    static hasMany = [dataCollections: MyDataCollection]


    static mapping = {
        table "mir_profiles"
        id generator:"assigned", column:"id", type:"integer"  // name:"id"
        name column:"name"
        shortname column:"shortname"
        description column:"description", type:'text'
        open column:"public"
        key column:"key"
        contact column:"contact_email"
        dateCreated column:"date_created"

        dataCollections fetch:"select"

        version false   // no version column
    }

    /**
     * Retrieve the preferred resource for the data collection which identifier is provided in parameter.
     * Returns null if no such data collection
     * @return
     */
    String getPreferredResource(String id)
    {
        String preferredResourceId

        if (this.shortname == "most_reliable")
        {
            preferredResourceId = Resolver.getMostReliableResourceId(id)
        }
        else
        {
            preferredResourceId = getOrderedDataCollections()?.get(id)?.preferredResourceId
        }
        
        /*
        if (null != dataCollections)
        {
            dataCollections.each {
                if (it.dataCollectionId == id)
                {
                    return it.preferredResourceId
                }
            }
        }
        else
        {
            return null
        }
        */
        
        return preferredResourceId
    }

    /**
     * Retrieves a map of all data collection for easy access
     * @return
     */
    Map<String, MyDataCollection> getOrderedDataCollections()
    {
        Map<String, MyDataCollection> map = null

        if (null != dataCollections)
        {
            map = new HashMap<String, MyDataCollection>()
            dataCollections.each {
                map.put(it.dataCollectionId, it)
            }
        }

        return map
    }
}
