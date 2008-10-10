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


package uk.ac.ebi.miriam.db;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * <p>Servlet which handles the database access for examples of annotation.
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
 * @version 20080609
 */
public class AnnotationDao extends Dao
{
    private Logger logger = Logger.getLogger(AnnotationDao.class);
    
    
    /**
     * Constructor.
     * @param pool database pool
     */
    public AnnotationDao(String pool)
    {
        super(pool);
        // setup env
        setupEnv();
    }
    
    
    /**
     * Cleans the mess (like releasing the database connection)
     */
    @Override
    public void clean()
    {
        cleanEnv();
    }
    
    
    /**
     * Retrieves the tags belonging to a given format.
     * @param format such as SBML, BioPAX or CellML
     * @return list of tags (for XML formats) with their identifier
     */
    public List<AnnotationTag> getTagsByFormat(String format)
    {
        List<AnnotationTag> result = new ArrayList<AnnotationTag>();
        
        PreparedStatement stmt = this.pool.getPreparedStatement("SELECT id, name, information, format FROM mir_annotation WHERE (format=?) ORDER BY name");
        try
        {
            stmt.setString(1, format);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                AnnotationTag temp = new AnnotationTag();
                temp.setId(rs.getString("id"));
                temp.setName(rs.getString("name"));
                temp.setInfo(rs.getString("information"));
                temp.setFormat(rs.getString("format"));
                result.add(temp);
                
                notEmpty = rs.next();
            }
            
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the tags from the format: " + format);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the examples of annotation for a given data type.
     * @param dataTypeId identifier of a data type (for example: 'MIR:00000008')
     * @return
     */
    public List<Annotation> getAnnotationFromDataId(String dataTypeId)
    {
        List<Annotation> result = new ArrayList<Annotation>();
        
        String sql = "SELECT anno.id, anno.format, anno.name, anno.information FROM mir_annotation anno, mir_anno_link link WHERE ((link.ptr_datatype = '" + dataTypeId + "') AND (link.ptr_annotation = anno.id)) ORDER BY anno.format, anno.name";
        ResultSet rs = pool.request(pool.getStatement(), sql);
        Annotation anno = null;
        
        boolean notEmpty;
        try
        {
            String format = new String();
            notEmpty = rs.next();
            
            while (notEmpty)
            {
                
                Tag temp = new Tag();
                format = rs.getString("anno.format");
                temp.setId(rs.getString("anno.id"));
                temp.setName(rs.getString("anno.name"));
                temp.setInfo(rs.getString("anno.information"));
                
                // an annotation with the same format already exists
                if ((anno != null) && (anno.getFormat().equals(format)))
                {
                    // adds the new tag
                    anno.addTag(temp);
                }
                else
                {
                    // no annotation created so far
                    if (anno == null)
                    {
                        // creation of an Annotation
                        anno = new Annotation(format);
                        
                        // adds the new tag
                        anno.addTag(temp);
                    }
                    else   // an annotation has already been created 
                    {
                        // adds the previous annotation to list
                        result.add(anno);
                        
                        // creation of a new Annotation (new format)
                        anno = new Annotation(format);
                        
                        // adds the new tag
                        anno.addTag(temp);
                    }
                }
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while searching the annotation!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        // adds the last annotation to the list
        if (anno != null)
        {
            result.add(anno);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the available formats for examples of annotations.
     * @return list of formats (SBML, CellML, BioPAX, ...)
     */
    public List<String> getAvailableFormats()
    {
        List<String> result = new ArrayList<String>();
        
        String sql = "SELECT DISTINCT format FROM mir_annotation ORDER BY format DESC";
        ResultSet rs = pool.request(pool.getStatement(), sql);
        
        boolean notEmpty;
        try
        {
            notEmpty = rs.next();
            
            while (notEmpty)
            {
                result.add(rs.getString("format"));
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retreiving the available formats for examples of annotation!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        return result;
    }


    /**
     * Checks if a tag (example of annotation) exists in the database.
     * @param tagId identifier of a tag (for example: 'MIR:00500009')
     * @return True or False
     */
    public boolean exists(String tagId)
    {
        boolean result = false;   // default value
        
        PreparedStatement stmt = this.pool.getPreparedStatement("SELECT name FROM mir_annotation WHERE (id=?)");
        try
        {
            stmt.setString(1, tagId);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                result = true;
            }
            
            stmt.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of the following annotation: " + tagId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        
        return result;
    }
    
    
    /**
     * Checks if an association between a data type and a annotation tag exists.
     * @param dataId identifier of a data type (for example: 'MIR:00000022')
     * @param tagId identifier of a annotation tag (for example: 'MIR:00500009')
     * @return
     */
    public boolean annotationExists(String dataId, String tagId)
    {
        boolean existing = false;
        
        PreparedStatement stmt1 = this.pool.getPreparedStatement("SELECT id FROM mir_anno_link WHERE ((ptr_annotation=?) AND (ptr_datatype=?))");
        try
        {
            stmt1.setString(1, tagId);
            stmt1.setString(2, dataId);
            logger.debug("SQL prepared query: " + stmt1.toString());
            ResultSet rs = stmt1.executeQuery();
            
            if (rs.first())
            {
                existing = true;
            }
            
            stmt1.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of an association between an example of annotation (" + tagId + ") and a data type (" + dataId + ")!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        
        return existing;
    }
    
    
    /**
     * Adds a new association between a data type and a tag (example of annotation)
     * @param dataId identifier of a data type (for example: 'MIR:00000022')
     * @param tagId identifier of a annotation tag (for example: 'MIR:00500009')
     * @return
     */
    public boolean addAnnotation(String dataId, String tagId)
    {
        boolean state = false;
        boolean existing = false;
        
        // checks if the association doesn't exist already
        existing = annotationExists(dataId, tagId);
        
        // creates the new association
        if (! existing)
        {
            PreparedStatement stmt2 = this.pool.getPreparedStatement("INSERT INTO mir_anno_link (ptr_annotation, ptr_datatype) VALUES (?, ?)");
            try
            {
                stmt2.setString(1, tagId);
                stmt2.setString(2, dataId);
                logger.debug("SQL prepared query: " + stmt2.toString());
                stmt2.executeUpdate();
                stmt2.close();
                state = true;
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the creation an association between an example of annotation (" + tagId + ") and a data type (" + dataId + ")!");
                logger.warn("SQLException raised: " + e.getMessage());
            }
        }
        
        return state;
    }


    /**
     * Retrieves an example of annotation given its identifier.
     * @param tagId identifier of an example of annotation (for example: 'MIR:00500009')
     * @return can be null
     */
    public AnnotationTag getAnnoFromId(String tagId)
    {
        AnnotationTag result = null;
        
        String sql = "SELECT id, name, information, format FROM mir_annotation WHERE (id='" + tagId + "')";
        ResultSet rs = pool.request(pool.getStatement(), sql);
        try
        {
            if (rs.first())            
            {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String info = rs.getString("information");
                String format = rs.getString("format");
                result = new AnnotationTag(id, name, info, format);
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving an example of annotation: " + tagId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        return result;
    }
}
