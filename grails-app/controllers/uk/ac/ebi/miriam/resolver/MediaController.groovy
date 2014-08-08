package uk.ac.ebi.miriam.resolver

import grails.util.Holders

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
//    private static final String PATH_TO_MEDIA_FILES = Holders.getGrailsApplication().config.getProperty('staticpages')+"media/";


    /**
     * Allows the download of a given file.
     */
    def download = {
        String mediaFile = Holders.getGrailsApplication().config.getProperty('staticpages')+"media/" +params.file
        if(exists(mediaFile)){
            redirect(url:mediaFile)
        }else {
            forward(controller: "error", action: "notFound")   // file not found
        }
    }

    public boolean exists(String url){
        try {
          HttpURLConnection.setFollowRedirects(false);
          HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
          con.setRequestMethod("HEAD");
          return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        catch (Exception e) {
           e.printStackTrace();
           return false;
        }
      }
}
