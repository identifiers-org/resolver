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
 * <p>Performs all the persistence features (link with the database) of a <code>User</code> object, such as "retrieve" or "save".
 * <p>It is necessary to use the method "setParameters()" before any of the other methods available!
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
 * @version 20080516
 */
public class UserDao extends User
{
    private Logger logger = Logger.getLogger(UserDao.class);
    private DbSimpleConnect connection;
    // database connection parameters
    private String dbDriver;
    private String dbType;
    private String dbServer;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPass;
    
    
    /**
     * Default constructor.
     */
    public UserDao()
    {
        super();
    }
    
    
    /**
     * Constructor with arguments: builds a full object.
     * 
     * @param login
     * @param firstName
     * @param lastName
     * @param email
     * @param organisation
     * @param password
     */
    public UserDao(String login, String firstName, String lastName, String email, String organisation, String password)
    {
        super(login, firstName,lastName, email, organisation, password);
    }
    
    
    /**
     * Sets the parameters to access the database.
     * 
     * @param dbDriver
     * @param dbType
     * @param dbServer
     * @param dbPort
     * @param dbName
     * @param dbUser
     * @param dbPass
     */
    public void setParameters(String dbDriver, String dbType, String dbServer, String dbPort, String dbName, String dbUser, String dbPass)
    {
        this.dbDriver = dbDriver;
        this.dbType = dbType;
        this.dbServer = dbServer;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }
    
    
    /**
     * Retrieves a <code>User</code> from the database. Returns the status of this retrieval. The retrieved <code>User</code> is stored in this object.
     * @param login
     * @return True if retrieval is a success, False otherwise
     */
    public boolean retrieve(String login)
    {
        boolean status = false;
        ResultSet rs = null;
        
        // database settings
        connection = new DbSimpleConnect();
        connection.setDriver(dbDriver);
        connection.setType(dbType);
        connection.setServer(dbServer);
        connection.setPort(dbPort);
        connection.setName(dbName);
        connection.setUser(dbUser);
        connection.setPass(dbPass);
        // database connection
        connection.getConnection();
        
        PreparedStatement stmt1 = connection.getPreparedStatement("SELECT firstname, lastname, email, password, organisation FROM auth_user WHERE login=?");
        try
        {
            stmt1.setString(1, login);
            rs = stmt1.executeQuery();
            
            int nbLines = DbPoolConnect.getRowCount(rs);
            if (nbLines == 1)
            {
                this.setLogin(login);
                this.setFirstName(rs.getString("firstname"));
                this.setLastName(rs.getString("lastname"));
                this.setEmail(rs.getString("email"));
                this.setPassword(rs.getString("password"));
                this.setOrganisation(rs.getString("organisation"));
                
                status = true;
            }
            else
            {
                if (nbLines > 1)
                {
                    logger.warn("The user '" + login + "' is recorded several times in the database!");
                }
                else
                {
                    logger.warn("The user '" + login + "' is not recoded in the database!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during a simple (without pooling) SQL query (with prepared statements): retrieving personnal info about the user (" + login + ")!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        connection.closeConnection();
        
        return status;
    }
    
    
    /**
     * Saves the user information in the database. This will only update the 'first name', 'last name', 'email' and 'organisation' fields, not the password or others!
     * @return True if the save is a success, False otherwise
     */
    public boolean updateUserInfo()
    {
        boolean status = false;
        
        // database settings
        connection = new DbSimpleConnect();
        connection.setDriver(dbDriver);
        connection.setType(dbType);
        connection.setServer(dbServer);
        connection.setPort(dbPort);
        connection.setName(dbName);
        connection.setUser(dbUser);
        connection.setPass(dbPass);
        // database connection
        connection.getConnection();
        
        PreparedStatement stmt = connection.getPreparedStatement("UPDATE auth_user SET firstname=?, lastname=?, email=?, organisation=? WHERE (login=?)");
        try
        {
            stmt.setString(1, getFirstName());
            stmt.setString(2, getLastName());
            stmt.setString(3, getEmail());
            stmt.setString(4, getOrganisation());
            stmt.setString(5, getLogin());
            logger.debug("SQL query (user information update): " + stmt.toString());
            int resultStatus = stmt.executeUpdate();
            stmt.close();
            
            if (resultStatus != 1)   // failure to perform the update
            {
                status = false;
            }
            else
            {
                status = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during a simple (without pooling) SQL query (with prepared statements): updating personnal info about the user (" + getLogin() + ")!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            connection.closeConnection();
        }
        
        
        return status;
    }
    
    
    /**
     * Saves the content this object in the database. If the <code>User</code> already exists in the database, it will be updated.
     * @return True if the save is a success, False otherwise
     */
    /*
    public boolean save()
    {
        // TODO: complete that!
        
        return false;
    }
    */
    
    
    public List<UserManageDetails> getUsers()
    {
        List<UserManageDetails> result = new ArrayList<UserManageDetails>();
        ResultSet rs = null;
        
        // database settings
        connection = new DbSimpleConnect();
        connection.setDriver(dbDriver);
        connection.setType(dbType);
        connection.setServer(dbServer);
        connection.setPort(dbPort);
        connection.setName(dbName);
        connection.setUser(dbUser);
        connection.setPass(dbPass);
        // database connection
        connection.getConnection();
        
        PreparedStatement stmt = connection.getPreparedStatement("SELECT u.login, u.firstname, u.lastname, r.role, l.last_login FROM auth_user u, auth_role r, auth_app a, auth_link l WHERE ((l.ptr_user = u.login) AND (l.ptr_app = a.id) AND (l.ptr_role = r.id))");
        try
        {
            rs = stmt.executeQuery();
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                UserManageDetails temp = new UserManageDetails();
                temp.setLogin(rs.getString("login"));
                temp.setName(rs.getString("firstname") + " " + rs.getString("lastname"));
                temp.setRole(rs.getString("role"));
                temp.setLastLogin(rs.getTimestamp("last_login"));   // the date *MUST* be different to '0000-00-00 00:00:00'!!!
                /*
                Error during a simple (without pooling) SQL query (with prepared statements): retrieving some information about the users!
                2008-05-15 17:14:53,699 ERROR db.UserDao        (            ?:?)     - SQL Exception raised: Value '0000-00-00 00:00:00' can not be represented as java.sql.Date
                */
                result.add(temp);
                
                notEmpty = rs.next();
            }
            
        }
        catch (SQLException e)
        {
            logger.error("Error during a simple (without pooling) SQL query (with prepared statements): retrieving some information about the users!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            connection.closeConnection();
        }
        
        return result;
    }
}
