package uk.ac.ebi.miriam.resolver

import uk.ac.ebi.miriam.common.Constants

/**
 * Controller handling the various redirections.
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
 * @version 20110919
 */
class RedirectController
{

    // Redirects to external URL
    def external = {
        def fullUri = request.forwardURI
        //def endUri = fullUri.substring(fullUri.lastIndexOf("/") + 1, fullUri.length())

        //if (endUri == "registry")
        if (fullUri.contains("/registry"))
        {
            redirect(url: Constants.MIRIAM_URL_ROOT);   // 302 HTTP status code will be issued by default
        }
        else   // default: front page
        {
            forward(controller: "info", action: "intro")   // "request" does not work
        }
    }
}
