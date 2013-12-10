package uk.ac.ebi.miriam.resolver

/**
 * Controller handling the access to media files (such as slides or posters).
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
 * @version 20131210
 */
class MediaController
{
    private static final String PATH_TO_MEDIA_FILES = "/nfs/production/biomodels/WWW/identifiers-org/media/";


    /**
     * Allows the download of a given file.
     */
    def download = {
        def file = null;
        try
        {
            file = new File(PATH_TO_MEDIA_FILES + params.file)
        }
        catch (FileNotFoundException e)
        {
            file = null;
        }

        if (null != file)
        {
            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment;filename=${file.getName()}")
            response.outputStream << file.newInputStream() // Performing a binary stream copy
        }
        else
        {
            forward(controller: "error", action: "notFound")   // file not found
        }
    }
}
