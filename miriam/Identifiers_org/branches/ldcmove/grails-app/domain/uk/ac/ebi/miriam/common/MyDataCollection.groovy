package uk.ac.ebi.miriam.common

/*
 * Contains all the custom information about a data collection linked to a myMIRIAM profile.
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
class MyDataCollection
{
    Integer id   // internal identifier
    String dataCollectionId
    String preferredResourceId
    Date dateAdded
    Date dateModif

    // GORM: unidirectional many-to-one relationship
    static belongsTo = [profile:Profile]

    static mapping = {
        table "mir_my_miriam"
        id generator:"assigned", column:"id", type:"integer"    //, insert:"false", update:"false"
        dataCollectionId column:"ptr_datatype", type:"string"
        preferredResourceId column:"ptr_preferred_resource", type:"string"
        dateAdded column:"date_added"
        dateModif column:"date_modif"

        profile column:"ptr_my_project", type:"integer"   // foreign key

        version false   // no version column
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder()

        str << "MyDataCollection: " << id
        str << "  data collection:     " << dataCollectionId
        str << " preferred resourceId: " << preferredResourceId
        str << " date added: " << dateAdded
        str << " date modif: " << dateModif

        return str.toString()
    }
}
