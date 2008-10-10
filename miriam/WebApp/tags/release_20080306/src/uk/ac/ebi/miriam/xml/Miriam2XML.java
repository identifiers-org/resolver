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


package uk.ac.ebi.miriam.xml;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.apache.log4j.Logger;

import uk.ac.ebi.miriam.db.DbPoolConnect;


/**
 * <p> Export of the whole MIRIAM DataBase in an XML file (like the old 'Resource.xml').
 *
 * <p>
 * Uses the database pool created by the Miriam Web App.
 *
 * <p>
 * TODO: add the annotations example in the XML file
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
 * @version 20070130
 */
public class Miriam2XML
{
  private Logger logger = Logger.getLogger(Miriam2XML.class);
  private PrintWriter file = null;
  private String fileName = null;
  private String poolName = null;
  private DbPoolConnect pool;


  /**
   * Constructor
   * @param String name of the output file
   */
  public Miriam2XML(String poolName, String fileName)
  {
    logger.debug("Creation of a 'Miriam2XML' object.");
    this.fileName = fileName;
    this.poolName = poolName;
  }


  /**
   * Official destructor
   */
  public void finalize()
  {
    // nothing here.
  }


  /**
   * Export of the database in the output file
   */
  public boolean export()
  {
    logger.debug("Request for an export of the MIRIAM database");

    pool = new DbPoolConnect(poolName);

    // open the output file
    if (openFile() == null)
    {
      logger.fatal("Impossible to proceed the MIRIAM export!");
      return false;
    }

    // connection to the database (via a pool)
    // test without 'newConnection()' before, let's see...
    pool.getConnection();

    // header of the file
    beginFile(file);

    // database dump
    if (dump() == false)
    {
      logger.fatal("An error occurred during the MIRIAM export!");
      return false;
    }

    // footer of the file
    endFile(file);

    // close the file
    file.close();

    // without closing the statement, let's see...
    pool.closeConnection();

    // logging message
    logger.info("The MIRIAM export is a success!");

    return true;
  }


  /*
   * Opens the file
   * @return the PrintWriter object
   */
  private PrintWriter openFile()
  {
    try
    {
      file = new PrintWriter(new FileWriter(fileName));
    }
    catch (IOException e)
    {
      logger.error("The output file cannot be opened!");
      logger.error("IO Exception raised: " + e.getMessage());
    }

    logger.info("Output file used: '" + fileName + "'");

    return file;
  }


  /*
   * Reads the content of the database and writes it in the output file
   * @return a boolean in order to know if there was a problem or not
   */
  private boolean dump()
  {
    String id = new String();
    String tmp = new String();
    ResultSet sqlResult = null;
    String originalQuery = "SELECT * FROM mir_datatype";

    // execute the query
    sqlResult = pool.request(pool.getStatement(), originalQuery);

      try
      {
      boolean notEmpty = sqlResult.next();

      // for each resources, search all the needed information
      while (notEmpty)
      {
        // new resource
        id = sqlResult.getString("datatype_id");
        file.println("\t<datatype id=\"" + id + "\" pattern=\"" + sqlResult.getString("pattern") + "\">");

        // primary name
        file.println("\t\t<name>" + sqlResult.getString("name") + "</name>");

        // searching the synonyms
        Statement stmt2 = null;
        ResultSet sqlResult2 = null;
        boolean notEmpty2;
        String query2 = "SELECT * FROM mir_synonym WHERE (ptr_datatype = '" + id + "')";

        // new statement
        stmt2 = pool.getStatement();

        // query
        sqlResult2 = pool.request(stmt2, query2);

        try
        {
          notEmpty2 = sqlResult2.next();
          while (notEmpty2)
          {
            file.println("\t\t<synonym>" + sqlResult2.getString("name") + "</synonym>");
            notEmpty2 = sqlResult2.next();
          }
        }
        catch (SQLException e1)
        {
            logger.error("Error while searching the synonyms!");
            logger.error("SQL Exception raised: " + e1.getMessage());
        }

        // searching the definition
        if (sqlResult.getString("definition") != null)
        {
          file.println("\t\t<definition>" + sqlResult.getString("definition") + "</definition>");
        }

        // searching the URIs
        String query3 = "SELECT * FROM mir_uri WHERE (ptr_datatype = '" + id + "')";
        sqlResult2 = pool.request(stmt2, query3);

        try
        {
          notEmpty2 = sqlResult2.next();
          while (notEmpty2)
          {
            file.print("\t\t<uri type=\"" + sqlResult2.getString("uri_type") + "\"");
            if ((sqlResult2.getString("deprecated")).equals("0"))
            {
              // nothing
            }
            else
            {
              file.print(" deprecated=\"true\"");
            }
            file.println(">" + sqlResult2.getString("uri") + "</uri>");
            notEmpty2 = sqlResult2.next();
          }
        }
        catch (SQLException e1)
        {
            logger.error("Error while computing the synonyms!");
            logger.error("SQL Exception raised: " + e1.getMessage());
        }

        // TODO: I am here...

        // searching the documentation(s) related to a datatype
        String query4 = "SELECT * FROM mir_doc WHERE (ptr_type = 'data' AND ptr_datatype = '" + id + "')";
        sqlResult2 = pool.request(stmt2, query4);

        try
        {
          notEmpty2 = sqlResult2.next();
          while (notEmpty2)
          {
            file.println("\t\t<documentation type=\"" + sqlResult2.getString("uri_type") + "\">" + sqlResult2.getString("uri") + "</documentation>");
            notEmpty2 = sqlResult2.next();
          }
        }
        catch (SQLException e1)
        {
            logger.error("Error while searching the documentations of the current resource!");
            logger.error("SQL Exception raised: " + e1.getMessage());
        }

          // searching the resources (physical locationS)
        String query5 = "SELECT * FROM mir_resource WHERE (ptr_datatype = '" + id + "')";
        sqlResult2 = pool.request(stmt2, query5);

        try
        {
          notEmpty2 = sqlResult2.next();
          while (notEmpty2)
          {
            // beginning of the current resource
            if ((sqlResult2.getString("obsolete")).equalsIgnoreCase("1"))   // resource obsolete
            {
              file.println("\t\t<resource id=\"" + sqlResult2.getString("resource_id") + "\" obsolete=\"true\">");
            }
            else   // resource not obsolete
            {
              file.println("\t\t<resource id=\"" + sqlResult2.getString("resource_id") + "\">");
            }
            file.println("\t\t\t<dataResource>" + (sqlResult2.getString("url_resource")).replaceAll("&", "&amp;") + "</dataResource>");
            tmp = (sqlResult2.getString("url_element_prefix")).replaceAll("&", "&amp;") + "$id" + (sqlResult2.getString("url_element_suffix")).replaceAll("&", "&amp;");   // for valid URLs
            file.println("\t\t\t<dataEntry>" + tmp + "</dataEntry>");
            file.println("\t\t\t<dataInfo>" + sqlResult2.getString("info") + "</dataInfo>");
            file.println("\t\t\t<dataInstitution>" + sqlResult2.getString("institution") + "</dataInstitution>");
            file.println("\t\t\t<dataLocation>" + sqlResult2.getString("location") + "</dataLocation>");

            // searching the documentation(s) related to the current resource
            Statement stmt3 = null;
            ResultSet sqlResult3 = null;
            boolean notEmpty3;
            String query6 = "SELECT * FROM mir_doc WHERE (ptr_type = 'loc' AND ptr_resource = '" + id + "')";

            stmt3 = pool.getStatement();
            sqlResult3 = pool.request(stmt3, query6);

            try
            {
              notEmpty3 = sqlResult3.next();
              while (notEmpty3)
              {
                file.println("\t\t\t<documentation type=\"" + sqlResult3.getString("uri_type") + "\">" + sqlResult3.getString("uri") + "</documentation>");
                notEmpty3 = sqlResult3.next();
              }
              sqlResult3.close();
              stmt3.close();
            }
            catch (SQLException e2)
            {
                logger.error("Error while searching the documentations of the current resource!");
                logger.error("SQL Exception raised: " + e2.getMessage());
            }

            // end of the current resource
            file.println("\t\t</resource>");
            notEmpty2 = sqlResult2.next();
          }
          sqlResult2.close();
          stmt2.close();
        }
        catch (SQLException e1)
        {
            logger.error("Error while searching the resources!");
            logger.error("SQL Exception raised: " + e1.getMessage());
        }

        // end of a resource
        file.println("\t</datatype>");
        file.println("");
        file.flush();
        notEmpty = sqlResult.next();
      }
      sqlResult.close();
      }
      catch (SQLException e)
      {
        logger.error("Error while computing the resources!");
        logger.error("SQL Exception raised: " + e.getMessage());
        return false;
      }

      // close all the statements
        pool.closeStatements();

        return true;
    }


  /*
   * Creates the header of the XML file
   */
  private void beginFile(PrintWriter file)
  {
    // recovery of the current date
    Calendar cal = new GregorianCalendar();

    // writing
    file.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    file.println("");   // empty line
    file.println("<!-- This file is an export of MIRIAM Database. -->");
    file.println("<!-- http://www.ebi.ac.uk/compneur-srv/miriam/  -->");
    file.println("<!-- Generated: " + cal.getTime() + ".   -->");
    file.println("");   // empty line
    file.println("<miriam xmlns=\"http://www.biomodels.net/MIRIAM/\">");
    file.flush();
  }


  /*
   * Creates the footer of the XML file
   */
  private void endFile(PrintWriter file)
  {
    file.println("</miriam>");
    file.flush();
  }

}

