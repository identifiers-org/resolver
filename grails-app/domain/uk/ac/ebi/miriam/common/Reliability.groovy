package uk.ac.ebi.miriam.common


/**
 * Contains all the information about the reliability of a resource.
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
class Reliability
{
    Integer id
    Integer uptime
    Integer downtime
    Integer unknown
    Boolean ajax
    Integer state   // O: failure; 1: resource working; 2: unknow (no keyword); 3: probably up (Ajax)*

    // GORM: unidirectional one-to-one relationship
    static belongsTo = [resource:Resource]

    static mapping = {
        table "mir_url_check"
        id generator:"assigned", column:"id"    // type:"string", insert:"false", update:"false"
        uptime column:"uptime"
        downtime column:"downtime"
        unknown column:"unknown"
        ajax column:"ajax"
        state column:"state"

        resource column:"resource_id", type:"string"   // foreign key

        version false   // no version column
    }

        /**
     * Retrieves the reliability of the resource (a percentage of uptime).
     * @return resource reliability
     */
    public Integer uptimePercent()
    {
        int totalDays = uptime + downtime;   // 'unknown' not part of the uptime percent computation
        if (totalDays > 0)
        {
            return Math.round(this.uptime * 100 / totalDays)
        }
        else
        {
            return 0
        }
    }

    /**
     * Convert the state of a resource (stored as an integer) into a human readable state.
     * @param state
     * @return
     */
    static String getHumanState(state)
    {
        def String humanState

        switch (state) {
            case 0:
                humanState = "Down"
                break
            case 1:
                humanState = "Up"
                break
            case 2:
                humanState = "Unknown"
                break
            case 3:
                humanState = "Probably up"
                break
            default:
                humanState = "Unknown"
        }

        return humanState
    }
}
