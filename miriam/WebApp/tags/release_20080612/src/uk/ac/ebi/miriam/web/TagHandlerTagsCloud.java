/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2008  Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.miriam.db.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
//import org.apache.log4j.Logger;


/**
 * <p>Custom tag handler for displaying the list of tags in a table.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2008 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
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
 * @version 20080611
 */
public class TagHandlerTagsCloud extends SimpleTagSupport
{
    //private Logger logger = Logger.getLogger(TagHandlerTagsCloud.class);
    private List<Tag> data;
    private HashMap<Integer, Integer> mapping = new HashMap<Integer, Integer>();
    
    /**
     * Setter of 'data'
     * 
     * @param list of <code>SimpleDataType</code>
     */
    public void setData(List<Tag> data)
    {
        this.data = data;
    }
    
    
    /**
     * This method contains all the business part of the tag handler.
     */
    public void doTag() throws JspException, IOException
    {
        JspContext context = getJspContext();
        int i = 1;
        
        for (Tag tag: data)
        {
            StringBuffer prefix = new StringBuffer();
            StringBuffer suffix = new StringBuffer();
            
            // 4 tags per row (line)
            if ((i % 4) == 0)
            {
                suffix.append("</tr>\n");
                if (i != data.size())
                {
                    suffix.append("<tr>\n");
                }
                //prefix.append("<tr>\n");
            }
            else
            {
                // first element
                if (i == 1)
                {
                    prefix.append("<tr>\n");
                }
                if (i == data.size())
                {
                    suffix.append("</tr>\n");
                }
            }
            
            context.setAttribute("prefix", prefix.toString());
            context.setAttribute("suffix", suffix.toString());
            //context.setAttribute("nbOccurr", getProportionalTagSize(tag.getNbOccurrence(), getMaxTags(data)));
            context.setAttribute("nbOccurr", getProportionalTagSizeImproved(tag.getNbOccurrence(), getNbDiffTags(data)));
            context.setAttribute("tag", tag);
            
            i++;
            getJspBody().invoke(null);   // processes the body of the tag and print it to the response
        }
    }
    
    
    /**
     * Retrieves the number maximum of occurrences in the list of tags
     * 
     * @param tags list of tags
     * @return
     */
    private int getMaxTags(List<Tag> tags)
    {
        int max = 0;
        
        for (Tag tag: tags)
        {
            if (tag.getNbOccurrence() > max)
            {
                max = tag.getNbOccurrence();
            }
        }
        
        return max;
    }
    
    
    /**
     * Retrieves the number of (different) occurrences of the tags.
     * @param tags
     * @return
     */
    private int getNbDiffTags(List<Tag> tags)
    {
        int nb = 0;
        TreeSet<Integer> occurrences = new TreeSet<Integer>();   // ordered list
        
        for (Tag tag: tags)
        {
            boolean added = occurrences.add(tag.getNbOccurrence());
            if (added)
            {
                nb ++;
            }
        }
        
        // creates a mapping between the number of occurrences and the proportional size they should be displayed with
        int cpt = 1;
        for (Integer i: occurrences)
        {
            this.mapping.put(i, cpt);
            cpt++;
        }
        
        return nb;
    }
    
    
    /**
     * Computes the proportional size of the tag in the tag cloud, regarding its number of occurrences.
     * The resulting size is between 80% and 200%.
     * 
     * @param nbOccurrence number of data types linked to the current tag
     * @param occurrenceMax number max of data types linked to a tag
     * @return
     */
    private int getProportionalTagSize(int nbOccurrence, int occurrenceMax)
    {
        int maxSize = 200;   // CSS font-size property (in percent)
        int minSize = 80;   // CSS font-size property (in percent)
        float unitSize;
        int result;
        
        unitSize = (maxSize - minSize) / (occurrenceMax - 1);
        result = (int) unitSize * (nbOccurrence - 1);   // 1 -> 80% and not 110% 
        result += minSize;
        
        return result;
    }
    
    
    /**
     * Computes the proportional size of the tag in the tag cloud, regarding its number of occurrences.
     * The resulting size is between 80% and 200%.
     * <p>This method is based on the number of different nb of occurrence rather than simply on the number of occurrence.
     * <p>Better display when one tag has a lot more number of occurrences than the others.
     * 
     * @param nbOccurrence
     * @param nbDiffOccurrence
     * @return
     */
    private int getProportionalTagSizeImproved(int nbOccurrence, int nbDiffOccurrence)
    {
        int maxSize = 200;   // CSS font-size property (in percent)
        int minSize = 80;   // CSS font-size property (in percent)
        float unitSize;
        int result;
        
        unitSize = (maxSize - minSize) / (nbDiffOccurrence - 1);
        result = (int) unitSize * (this.mapping.get(nbOccurrence) - 1);   // 1 -> 80% and not 110% 
        result += minSize;
        
        return result;
    }
}
