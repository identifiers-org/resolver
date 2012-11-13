package uk.ac.ebi.miriam.common


/*
 * Type of URI (either URN or URL).
 * This was created to properly map to the existing database schema (specially table 'mir_uri') using GORM.
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
 * @version 20110729
 */
public enum IdentifierType
{
    URN('Uniform Resource Name'),
    URL('Uniform Resource Locator')

    
    private final String name

    IdentifierType(String name)
    {
        this.name = name
    }

    /*
    String getName()
    {
        name()
    }
    */

    static list()
    {
        [URN, URL]
    }

    /*
    String toString()
    {
        this.name
    }
    */
}
