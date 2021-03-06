package uk.ac.ebi.miriam.common

/*
 * Contains all the information of a response to the request for a MIRIAM URI.
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
 * @version 20130426
 */
class UriRecord
{
    String requestedEntityURI
    String requestedUri
    String requestedUriBase
    String officialUri
    String entityId
    String namespace
    DataCollectionRecord dataCollection
    String infoUri
    /* in case the response does not fully correspond to the request, we can inform the user via this way */
    List<FeedbackUser> messages = []   // to avoid null list


    public void addMessage(String summary, String description)
    {
        FeedbackUser feed = new FeedbackUser()
        feed.setSummary(summary)
        feed.setDescription(description)
        messages << feed
    }
}
