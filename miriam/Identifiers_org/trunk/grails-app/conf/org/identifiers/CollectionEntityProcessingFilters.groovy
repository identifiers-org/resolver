/**
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2015 BioModels.net (EMBL - European Bioinformatics Institute)
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
 */

package org.identifiers

import grails.util.Holders

/**
 * Filter in charge of fixing the request params for collection entities.
 *
 * Requests to UriRecordController have incorrect entity and format params due to the way in
 * which Grails maps URIs with dots -- it will assume that the first dot in an entity
 * denotes the start of the request format. In the case of an entity with several dots, this
 * results in an eagerly-trimmed entity and an overweight format, both of which being invalid.
 *
 * This filter processes the request parameters and uses the mime type configuration settings
 * in grails.mime.types to test for possible expected formats. These entries are compared
 * with the concation of the request's entity and format and the parameters are updated
 * accordingly.
 *
 * @author Mihai Glonț <mihai.glont@ebi.ac.uk>
 * @date 20151217
 */
class CollectionEntityProcessingFilters {
    final Map mimeTypeCfg = (Holders.config.grails.mime.types ?: [:]).findAll { key, value ->
        key != 'all'
    }
    final Set<String> fmts = mimeTypeCfg.keySet()

    def filters = {
        fixEntityFormat(controller: 'uriRecord') {
            before = {
                String e = params.entity ?: ""
                String f = params.format ?: ""
                final StringBuilder sb = new StringBuilder(e.length() + f.length() + 1)
                sb.append(e).append('.').append(f)
                final String entityFormat = sb.toString()
                boolean found = false
                for (String fmt in fmts) {
                    if (entityFormat.endsWith(fmt)) {
                        e = entityFormat - ".$fmt"
                        f = fmt
                        found = true
                        break
                    }
                }
                if (!found) {
                    e = entityFormat
                    f = 'html'
                }
                params.entity = e
                params.format = f
            }
        }
    }
}
