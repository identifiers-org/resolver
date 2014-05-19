package uk.ac.ebi.miriam.common

/*
 * Contains all the necessary information about a resource for generating a response to the request for a MIRIAM URI.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140519
 */
class ResourceRecord
{
    String id
    String description
    String homepage
    String institution
    String location
    Boolean deprecated
    String state
    Integer reliability
    Boolean deniesFrame
    Boolean primary
    /* link towards a license or some terms of use */
    String license
    UrlRecord[] urls


    /* Returns a human readable value for the state (up, down, unknown, ...) of a resource */
    String stateStr()
    {
        String str = null

        if (null != state)
        {
            str = Constants.states.get(state);
            if (null == str)
            {
                str = "unknown"
            }
        }
        else
        {
            str = "unknown"
        }

        return str
    }
}
