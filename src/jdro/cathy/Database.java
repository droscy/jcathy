/*
 +--------------------------------------------------------------------------------
 |	"jCathy" v0.7.3
 |	(simple catalogator for removable devices)
 |	========================================
 |	by Simone Rossetto
 |	Copyright (C) 2007-2010 Simone Rossetto
 |	E-Mail: simros85@gmail.com
 |	========================================
 |	File created on 06/nov/07 19:44:09
 |	Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +--------------------------------------------------------------------------------
 |	This file is Database.java, part of "jCathy"
 |
 |	"jCathy" is free software; you can redistribute it and/or modify
 |	it under the terms of the GNU General Public License as published by
 |	the Free Software Foundation; either version 2 of the License, or
 |	(at your option) any later version.
 |	
 |	"jCathy" is distributed in the hope that it will be useful,
 |	but WITHOUT ANY WARRANTY; without even the implied warranty of
 |	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |	GNU General Public License for more details.
 |
 |	You should have received a copy of the GNU General Public License
 |	along with this program; if not, write to the Free Software
 |	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 +--------------------------------------------------------------------------------
 */
package jdro.cathy;

import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Connection;
import java.util.Vector;

/**
 * Standard class to manage the connection with a HSQL database.
 * This class is adapted for jCathy so that the result of a normal query
 * will return a model needed for the creation of a JTable
 * @author Simone Rossetto
 * @version 2.1.2
 */
public class Database
{
	private String dbFileName;
	private String lastError;
	private boolean connected;
	private boolean fk_created;
	
	private Connection connection;
	private Statement stmt;
	
	private PreparedStatement insertVolumeStatement;
	private PreparedStatement insertDirectoryStatement;
	private PreparedStatement insertFileStatement;
	
	private PreparedStatement renameVolumeStatement;
	private PreparedStatement updateVolumeStatement;
	private PreparedStatement updateDirectoryStatement;
	
	private PreparedStatement searchDirStatement;
	private PreparedStatement searchFileStatement;
	
	private PreparedStatement getVolumesStatement;
	private PreparedStatement checkVolumeExistence;
	
	private PreparedStatement selectDirStatement;
	private PreparedStatement selectFilesStatement;
	
	private PreparedStatement createSubTreeStatement;
	
	private PreparedStatement deleteVolumeStatement;
	//private PreparedStatement deleteDirStatement;
	//private PreparedStatement deleteFileStatement;
	
	private PreparedStatement ignoreInsertStatement;
	private PreparedStatement ignoreSelectStatement;
	private PreparedStatement ignoreDeleteStatement;


	/**
	 * Creates a new Database object that reads data from dbFileName file
	 * @param dbFileName Absolute path to the database-file
	 */
	public Database(String dbFileName)
	{
		this.dbFileName = dbFileName;
		connected = false;
		fk_created = false;
		lastError = "";
	}

	/**
	 * Open the connection with the database
	 * @return true if connected correctly, false otherwise
	 */
	public boolean connect()
	{
		try
		{	
			Class.forName("org.hsqldb.jdbcDriver");
			connection = DriverManager.getConnection("jdbc:hsqldb:file:"+dbFileName, "sa", "");
			connected = true;
			
			executeUpdate
			(
				"SET WRITE_DELAY FALSE;"+
				"SET LOGSIZE 10;"+
				"SET SCRIPTFORMAT COMPRESSED" // TEXT, COMPRESSED, BINARY
			);
			
			
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});

			Vector<String> vect = new Vector<String>();
			
			while (rs.next())
		    {
				vect.add(rs.getString("TABLE_NAME"));
		    }
			
			if(!vect.contains(Cathy.TABLEsettings.toUpperCase()))
			{
				executeUpdate
				(
					"SET IGNORECASE TRUE;" +
					"CREATE TABLE " + Cathy.TABLEsettings + "(fk_created BOOLEAN NOT NULL);" +
					"INSERT INTO " + Cathy.TABLEsettings + "(fk_created) VALUES (FALSE);"
				);
			}
			
			selectSettings();
			
			if(!vect.contains(Cathy.TABLEvolume.toUpperCase()))
			{
				executeUpdate
				(
					"SET IGNORECASE TRUE;" +
					"CREATE TABLE " + Cathy.TABLEvolume +
					"(" +
						"name VARCHAR NOT NULL PRIMARY KEY," +
						"size BIGINT NOT NULL," +
						"dirs INTEGER NOT NULL," +
						"files INTEGER NOT NULL," +
						"date DATE NOT NULL," +
						"source LONGVARCHAR NOT NULL" +
					");"
				);
			}
			
			if(!vect.contains(Cathy.TABLEdirectories.toUpperCase()))
			{
				executeUpdate
				(
					"SET IGNORECASE TRUE;"+
					"CREATE TABLE " + Cathy.TABLEdirectories +
					"(" +
						"id INTEGER NOT NULL," +
						"volume VARCHAR NOT NULL," +
						"name VARCHAR NOT NULL," +
						"parent INTEGER NOT NULL," +
						"size BIGINT NOT NULL,"+
						"date DATE NOT NULL," +
						"dirs INTEGER NOT NULL," +
						"files INTEGER NOT NULL," +
						"path LONGVARCHAR NOT NULL,"+
						"PRIMARY KEY(id,volume)," +
						"CONSTRAINT directories_fk_volume " +
							"FOREIGN KEY(volume) " +
							"REFERENCES " + Cathy.TABLEvolume + "(name) ON UPDATE CASCADE ON DELETE CASCADE" +
					");"
				);
			}
			else if(!fk_created)
			{
				executeUpdate
				(
					"ALTER TABLE " + Cathy.TABLEdirectories + " " +
					"ADD CONSTRAINT directories_fk_volume " + 
						"FOREIGN KEY(volume) " +
						"REFERENCES " + Cathy.TABLEvolume + "(name) ON UPDATE CASCADE ON DELETE CASCADE;"
				);
			}
			
			if(!vect.contains(Cathy.TABLEfiles.toUpperCase()))
			{
				executeUpdate
				(
					// FIXME come eseguire una ricerca IGNORECASE senza però avere anche l'ingresso IC!!!
					"SET IGNORECASE TRUE;" +
					"CREATE TABLE " + Cathy.TABLEfiles +
					"(" +
						"volume VARCHAR NOT NULL," +
						"directory INTEGER NOT NULL," +
						"name VARCHAR NOT NULL," +
						"date DATE NOT NULL," +
						"size BIGINT NOT NULL," +
						"path LONGVARCHAR NOT NULL," +
						"PRIMARY KEY(volume,directory,name)," +
						"CONSTRAINT files_fk_directories " +
							"FOREIGN KEY(directory,volume) " +
							"REFERENCES " + Cathy.TABLEdirectories + "(id,volume) ON UPDATE CASCADE ON DELETE CASCADE" +
					");"
				);
			}
			else if(!fk_created)
			{
				executeUpdate
				(
					"ALTER TABLE " + Cathy.TABLEfiles + " " +
					"ADD CONSTRAINT files_fk_directories " +
						"FOREIGN KEY(directory,volume) " +
						"REFERENCES " + Cathy.TABLEdirectories + "(id,volume) ON UPDATE CASCADE ON DELETE CASCADE;"
				);
			}
			
			if(!vect.contains(Cathy.TABLEignore.toUpperCase()))
			{
				executeUpdate
				(
					"SET IGNORECASE TRUE;"+
					"CREATE TABLE "+Cathy.TABLEignore+
					"(pattern VARCHAR NOT NULL);"
				);
			}
			
			executeUpdate("UPDATE " + Cathy.TABLEsettings + " SET fk_created=TRUE;");
			
			
			insertVolumeStatement = connection.prepareStatement("INSERT INTO "+Cathy.TABLEvolume+" VALUES(?,?,?,?,?,?)");
			insertDirectoryStatement = connection.prepareStatement("INSERT INTO "+Cathy.TABLEdirectories+" VALUES(?,?,?,?,?,?,?,?,?)");
			insertFileStatement = connection.prepareStatement("INSERT INTO "+Cathy.TABLEfiles+" VALUES(?,?,?,?,?,?)");
			
			renameVolumeStatement = connection.prepareStatement("UPDATE " + Cathy.TABLEvolume + " SET name = ? WHERE name LIKE ?");
			updateVolumeStatement = connection.prepareStatement("UPDATE " + Cathy.TABLEvolume + " SET size = ?, dirs = ?, files = ? WHERE name LIKE ?");
			updateDirectoryStatement = connection.prepareStatement("UPDATE " + Cathy.TABLEdirectories + " SET size = ?, dirs = ?, files = ? WHERE id = ? AND volume LIKE ?");
			
			searchDirStatement = connection.prepareStatement
			(
				"SELECT * " +
				"FROM "+Cathy.TABLEdirectories+" "+
				"WHERE name LIKE ? AND id!=0 "+
				"ORDER BY name ASC"
			);
			
			
			searchFileStatement = connection.prepareStatement
			(
				"SELECT * " +
				"FROM "+Cathy.TABLEfiles+" "+
				"WHERE name LIKE ? "+
				"ORDER BY name ASC"
			);
			
			
			getVolumesStatement = connection.prepareStatement
			(
				"SELECT * " +
				"FROM "+Cathy.TABLEvolume+" "+
				"ORDER BY name ASC"
			);
			
			checkVolumeExistence = connection.prepareStatement
			(
				"SELECT * "+
				"FROM "+Cathy.TABLEvolume+" "+
				"WHERE name = ?"
			);
			

			selectDirStatement = connection.prepareStatement
			(
				"SELECT * "+
				"FROM "+Cathy.TABLEdirectories+" "+
				"WHERE volume = ? AND id!=0 AND parent = ? "+
				"ORDER BY name ASC"
			);

		
			selectFilesStatement = connection.prepareStatement
			(
				"SELECT * " +
				"FROM "+Cathy.TABLEfiles+" " +
				"WHERE volume = ? AND directory = ? " +
				"ORDER BY name ASC"
			);
			
			
			createSubTreeStatement = connection.prepareStatement
			(
				"SELECT * " +
				"FROM " + Cathy.TABLEdirectories + " " +
				"WHERE id!=0 AND volume=? AND parent=? " +
				"ORDER BY name ASC"
			);
			
			
			deleteVolumeStatement = connection.prepareStatement
			(
				"DELETE FROM " + Cathy.TABLEvolume + " "+
				"WHERE name = ?"
			);
			
			/*deleteDirStatement = connection.prepareStatement // actually no more used
			(
				"DELETE FROM " + Cathy.TABLEdirectories + " "+
				"WHERE volume = ?"
			);
			
			deleteFileStatement = connection.prepareStatement // actually no more used
			(
				"DELETE FROM " + Cathy.TABLEfiles + " "+
				"WHERE volume = ?"
			);*/
			
			ignoreInsertStatement = connection.prepareStatement("INSERT INTO "+Cathy.TABLEignore+" VALUES(?)");
			ignoreSelectStatement = connection.prepareStatement("SELECT * FROM "+Cathy.TABLEignore);
			ignoreDeleteStatement = connection.prepareStatement("DELETE FROM "+Cathy.TABLEignore);

			
		}
		catch(SQLException sql_e)
		{
			sql_e.printStackTrace();
			lastError = sql_e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: " + lastError, GUI.errorIcon);
			connected = false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("Exception is thrown", "Exception: " + lastError, GUI.errorIcon);
			connected = false;
		}
		
		return connected;
	}
	
	/**
	 * Selects the settings form the database and stores them to local variables
	 */
	private void selectSettings() throws SQLException
	{
		stmt = connection.createStatement();
		ResultSet rs_fk = stmt.executeQuery("SELECT * FROM " + Cathy.TABLEsettings);
		if(rs_fk.next())
		{
			fk_created = rs_fk.getBoolean("fk_created");
			// add here other settings, if they exist
		}
		rs_fk.close();
		stmt.close();
	}
	
	/**
	 * Converts <code>ResultSet</code> data to a <code>Vector< String[] ></code>
	 * so data can be accessed even if the connection with the database will close
	 * @param rs the starting <code>ResultSet</code>
	 * @return every <code>String</code> of the <code>ResultSet</code> in a <code>Vector< String[] ></code>
	 * <br>Any element of the vector is a line of the <code>ResultSet</code>
	 * @throws SQLException if something goes wrong
	 */
	private static Vector<String[]> resSetToVectStrAr(ResultSet rs) throws SQLException
	{
		Vector<String[]> vectorOutput = new Vector<String[]>();
		int columns = rs.getMetaData().getColumnCount();
		String[] recordTmp;
		while(rs.next())
		{ 
			recordTmp = new String[columns];
			for(int i = 0; i < columns; i++)
				recordTmp[i] = rs.getString(i + 1);
			vectorOutput.add(recordTmp);
		}
		return vectorOutput;
	}


	/**
	 * Execute a normal query over the database and returns a Vector<String[]>
	 * that contains in each Vector element a row of the table and in each 
	 * String array the elements of the columns
	 * 
	 * @param query the query to be executed
	 * @return any row reported from the query. If this is null, a SQLException was thrown
	 */
	public Vector<String[]> executeQuery(String query)
	{
		Vector<String[]> vectorOutput = new Vector<String[]>();
		try
		{
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			vectorOutput.addAll(resSetToVectStrAr(rs));
			rs.close();
			stmt.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: " + lastError, GUI.errorIcon);
			vectorOutput.clear();
		}

		return vectorOutput;
	}

	/**
	 * Execute an updating query on the database and shows an error message if an SQLException
	 * is thrown.
	 * @param query the query to be executed
	 * @return true if the query succeded, false if a SQLException has been thrown
	 */
	public boolean executeUpdate(String query)
	{
		boolean result;
		try
		{
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			result = true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: " + lastError, GUI.errorIcon);
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Execute an updating query on the database
	 * @param query the query to be executed
	 * @exception SQLException if something goes wrong with the query
	 */
	public void executeUpdateWithoutErrMsg(String query) throws SQLException
	{
		stmt = connection.createStatement();
		stmt.executeUpdate(query);
		stmt.close();
	}

	/**
	 * Execute the SHUTDOWN query and closes the connection with the database
	 * @return true if the connection is closed correctly, false if an exception is thrown
	 */
	public boolean disconnect()
	{
		try
		{
			executeUpdate("SHUTDOWN");
			connection.close();
			connected = false;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: " + lastError, GUI.errorIcon);
		}
		return !connected;
	}

	/**
	 * Returns the current state
	 * @return true if the system is connected, false if the connection is out
	 */
	public boolean isConnected()
	{
		return connected;
	}
	

	/**
	 * Returns the error-message reported by the last thrown exception
	 * @return the last error-message
	 */
	public String getLastError()
	{
		return lastError;
	}
	
	
	/**
	 * Starts a transaction
	 * @throws SQLException if something goes wrong
	 */
	public void startTransaction() throws SQLException
	{
		connection.setAutoCommit(false);
	}
	
	/**
	 * Commits a transaction
	 * @throws SQLException if something goes wrong
	 */
	public void commitTransaction() throws SQLException
	{
		connection.commit();
		connection.setAutoCommit(true);
	}
	
	/**
	 * Rollbacks a transaction
	 * @return true if the rollbeck ended succesfully, false if some errors occured
	 */
	public boolean rollbackTransaction()
	{
		boolean result;
		
		try
		{
			connection.rollback();
			connection.setAutoCommit(true);
			result = true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
			result = false;
		}
		
		return result;
	}
	
	
	/**
	 * Inserts a new Volume in the database
	 * @param name the name of the new volume
	 * @param size total size of the volume in byte
	 * @param dirs how many directories are present in the volume
	 * @param files how manu files the volume contains
	 * @param date the time of the creation of this new volume
	 * @param path the starting path of the catalogation
	 * @throws SQLException if some error occured during query
	 */
	protected void insertVolume(String name, long size, int dirs, int files, long date, String path) throws SQLException
	{
		if(connection.getAutoCommit()==false)
		{
			insertVolumeStatement.setString(1, name);
			insertVolumeStatement.setLong(2, size);
			insertVolumeStatement.setInt(3, dirs);
			insertVolumeStatement.setInt(4, files);
			insertVolumeStatement.setDate(5, new Date(date));
			insertVolumeStatement.setString(6, path);
			insertVolumeStatement.executeUpdate();
		
			insertDir(0, name, name, 0, size, date, dirs, files, path);
		}
		else
			throw new SQLException("New volumes can be inserted only if AutoCommit mode is disabled.");
	}
	
	
	/**
	 * Inserts a new Directory in the database
	 * @param id the primary key of this directory
	 * @param volume the name of the volume to which this directory belongs
	 * @param name the name of this directory
	 * @param parent the id of the parent-directory (<code>null</code> if this directory is in the root)
	 * @param date the time of the last modification of this directory
	 * @throws SQLException if some error occured during query
	 */
	protected void insertDir(int id, String volume, String name, int parent, long size, long date,int dirs, int files, String path) throws SQLException
	{
		if(connection.getAutoCommit()==false)
		{
			insertDirectoryStatement.setInt(1, id);
			insertDirectoryStatement.setString(2, volume);
			insertDirectoryStatement.setString(3, name);
			insertDirectoryStatement.setInt(4, parent);
			insertDirectoryStatement.setLong(5, size);
			insertDirectoryStatement.setDate(6, new Date(date));
			insertDirectoryStatement.setInt(7, dirs);
			insertDirectoryStatement.setInt(8, files);
			insertDirectoryStatement.setString(9, path);
			insertDirectoryStatement.executeUpdate();
		}
		else
			throw new SQLException("New directories can be inserted only if AutoCommit mode is disabled.");

	}
	
	/**
	 * Inserts a new file in the "files" table of the database setting the correct parameters
	 * @param volume the name of the volume to which this file belongs
	 * @param dir the id of the directory that contains this file
	 * @param name the name of the file
	 * @param date the date&time of the last modification of this file
	 * @param size the dimension of this file in byte
	 * @throws SQLException if some error occured during query
	 */
	protected void insertFile(String volume, int dir, String name, long date, long size, String path) throws SQLException
	{
		if(connection.getAutoCommit()==false)
		{
			insertFileStatement.setString(1, volume);
			insertFileStatement.setInt(2,dir);
			insertFileStatement.setString(3, name);
			insertFileStatement.setDate(4, new Date(date));
			insertFileStatement.setLong(5,size);
			insertFileStatement.setString(6, path);
			insertFileStatement.executeUpdate();
		}
		else
			throw new SQLException("New files can be inserted only if AutoCommit mode is disabled.");
	}
	
	/**
	 * Rename one volume
	 * @param oldname the old name and primary key of the volume
	 * @param newname the new name to set for the volume
	 * @throws SQLException
	 */
	protected void renameVolume(String oldname, String newname) throws SQLException
	{
		boolean settedAutocommitOff = false;
		
		if(connection.getAutoCommit() == true)
		{
			startTransaction();
			settedAutocommitOff = true;
		}

		/*
		 * Only volumes are directly updated because related directories
		 * and files have foreign keys with "CASCADE" property setted.
		 */
		renameVolumeStatement.setString(1, newname);
		renameVolumeStatement.setString(2, oldname);
		renameVolumeStatement.executeUpdate();
		
		if(settedAutocommitOff)
			commitTransaction();
	}
	
	
	/**
	 * Updates the three size parameters for the volume with name <code>name</code>.
	 * @param name the name (primary key of the volume)
	 * @param size the new size (in bytes of the volume)
	 * @param dirs how many dirs are present in the volume
	 * @param files how many files are present in the volume
	 * @throws SQLException
	 */
	protected void updateVolumeSizes(String name, long size, int dirs, int files) throws SQLException
	{
		if(connection.getAutoCommit()==false)
		{
			updateVolumeStatement.setLong(1, size);
			updateVolumeStatement.setInt(2, dirs);
			updateVolumeStatement.setInt(3, files);
			updateVolumeStatement.setString(4, name);
			updateVolumeStatement.executeUpdate();
			
			updateDirSizes(0,name,size,dirs,files);
		}
		else
			throw new SQLException("New volumes can be inserted only if AutoCommit mode is disabled.");
	}
	
	
	/**
	 * Updates the size parameters of this directory
	 * @param id the primary key of the directory
	 * @param volume the name of the volume which this directory belongs to
	 * @param size the new size (in bytes of the directory)
	 * @param dirs how many dirs are present inside this directory
	 * @param files how many files are present inside this directory
	 * @throws SQLException
	 */
	protected void updateDirSizes(int id, String volume, long size, int dirs, int files) throws SQLException
	{
		if(connection.getAutoCommit()==false)
		{			
			updateDirectoryStatement.setLong(1, size);
			updateDirectoryStatement.setInt(2, dirs);
			updateDirectoryStatement.setInt(3, files);
			updateDirectoryStatement.setInt(4, id);
			updateDirectoryStatement.setString(5, volume);
			updateDirectoryStatement.executeUpdate();
		}
		else
			throw new SQLException("New directories can be inserted only if AutoCommit mode is disabled.");
	}

	
	
	/**
	 * Selects every entry in the database that is LIKE the <code>pattern</code> and
	 * constructs a <code>Vector< TableTreeEntryNode ></code> with the
	 * database-output
	 * @param pattern the pattern to be searched in the files table and directories table
	 * @return the data getted form the query. If an <code>SQLException</code> is thrown
	 * then en empy <code>Vector<String[]></code> is returned
	 */
	protected Vector<TableTreeEntryNode> searchPattern(String pattern)
	{
		Vector<TableTreeEntryNode> vectorOutput = new Vector<TableTreeEntryNode>();
		try
		{
			searchDirStatement.setString(1, pattern);
			ResultSet rsDir = searchDirStatement.executeQuery();
			while(rsDir.next())
			{
				vectorOutput.add(new DirEntry(rsDir.getInt("id"),rsDir.getString("name"),rsDir.getString("volume"),rsDir.getInt("parent"),rsDir.getLong("size"),rsDir.getInt("dirs"),rsDir.getInt("files"),rsDir.getDate("date"),rsDir.getString("path")));
			}
			rsDir.close();
			
			searchFileStatement.setString(1, pattern);
			ResultSet rsFile = searchFileStatement.executeQuery();
			while(rsFile.next())
			{
				vectorOutput.add(new FileEntry(JTreeObserver.FILE_ICON,rsFile.getString("name"),rsFile.getString("volume"),rsFile.getInt("directory"),rsFile.getLong("size"),rsFile.getDate("date"),rsFile.getString("path")));
			}
			rsFile.close();

		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
			vectorOutput.clear();
		}

		return vectorOutput;
	}
	
	
	/**
	 * Query the database in order to get the list of cataloged volumes 
	 * @return the list of volumes (empty if none is cataloged or if an exception is thrown)
	 */
	protected Vector<VolumeEntry> getVolumes()
	{
		Vector<VolumeEntry> vect = new Vector<VolumeEntry>();
		try
		{
			ResultSet rs = getVolumesStatement.executeQuery();
			while(rs.next())
			{
				vect.add(new VolumeEntry(rs.getString("name"),rs.getLong("size"),rs.getInt("dirs"),rs.getInt("files"),rs.getDate("date"),rs.getString("source")));
			}
			rs.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
			vect.clear();
		}
		return vect;
	}
	
	
	protected boolean volumeExists(String name)
	{
		boolean result;
		try
		{
			checkVolumeExistence.setString(1, name);
			ResultSet rs = checkVolumeExistence.executeQuery();
			if(rs.next())
				result = true;
			else
				result = false;
			rs.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
			System.out.println("prova");
			result = false;
		}
		return result;
	}
	
	
	/**
	 * Executes the query needed to update the <code>ExploreJTable</code> in the "Explore" tab.
	 * Gets all the subdirectories of the selected node from the database then executes
	 * <code>updateExploreTabFiles()</code> in order to retrive all the sub-files. Then
	 * returns the entire sub-content.
	 * @param volume the name of the current volume
	 * @param dirId the id of the selected directory
	 * @return a <code>Vector< TableTreeEntryNode ></code> with the output data getted
	 * from the database, the Vector is empty if an SQLException if thrown or if nothing
	 * is reported by the query
	 */
	protected Vector<TableTreeEntryNode> updateExploreTab(String volume, int dirId)
	{
		Vector<TableTreeEntryNode> vectOut = new Vector<TableTreeEntryNode>();
		try
		{
			selectDirStatement.setString(1, volume);
			selectDirStatement.setInt(2, dirId);
			ResultSet rs = selectDirStatement.executeQuery();
			while(rs.next())
			{
				vectOut.add(new DirEntry(rs.getInt("id"),rs.getString("name"),rs.getString("volume"),rs.getInt("parent"),rs.getLong("size"),rs.getInt("dirs"),rs.getInt("files"),rs.getDate("date"),rs.getString("path")));
			}
			rs.close();

			/*
			selectFilesStatement.setString(1, volume);
			selectFilesStatement.setInt(2, dirId);
			rs = selectFilesStatement.executeQuery();
			while(rs.next())
			{
				vectOut.add(new FileEntry(JTreeObserver.FILE_ICON,rs.getString("name"),rs.getString("volume"),rs.getInt("directory"),rs.getLong("size"),rs.getDate("date"),rs.getString("path")));
			}
			rs.close();*/
			vectOut.addAll(updateExploreTabFiles(volume, dirId));
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
			vectOut.clear();
		}
		
		return vectOut;
	}
	
	/**
	 * Gets all the subfiles of the selected node from the database
	 * @param volume the name of the volume which that node belongs to
	 * @param dirId the id number of the selected node
	 * @return every subfiles contained in the directory identified by
	 * <code>volume</code> and <code>dirId</code>
	 */
	protected Vector<TableTreeEntryNode> updateExploreTabFiles(String volume, int dirId)
	{
		Vector<TableTreeEntryNode> vectOut = new Vector<TableTreeEntryNode>();
		try
		{
			selectFilesStatement.setString(1, volume);
			selectFilesStatement.setInt(2, dirId);
			ResultSet rs = selectFilesStatement.executeQuery();
			while(rs.next())
			{
				vectOut.add(new FileEntry(JTreeObserver.FILE_ICON,rs.getString("name"),rs.getString("volume"),rs.getInt("directory"),rs.getLong("size"),rs.getDate("date"),rs.getString("path")));
			}
			rs.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
			vectOut.clear();
		}
		
		return vectOut;
	}

	
	
	/**
	 * Gets the data from the database needed to contructs a new <code>JTreeObserver</code>.<br>
	 * This method is called by the <code>createSubTree</code> method of the <code>JTreeObserver</code>
	 * class.
	 * @param volume the volume selected by the user
	 * @param parentId the id of the parent of the selected directory or file
	 * @return a <code>Vector< DirEntry ></code> with the output data getted from the database,
	 * the Vector is empty if an SQLException if thrown or if nothing is reported by the query
	 */
	protected Vector<DirEntry> createSubTreeQuery(String volume, int parentId)
	{
		Vector<DirEntry> vectOut = new Vector<DirEntry>();
		try
		{
			createSubTreeStatement.setString(1, volume);
			createSubTreeStatement.setInt(2, parentId);
			ResultSet rs = createSubTreeStatement.executeQuery();
			while(rs.next())
			{
				vectOut.add(new DirEntry(rs.getInt("id"),rs.getString("name"),volume,rs.getInt("parent"),rs.getLong("size"),rs.getInt("dirs"),rs.getInt("files"),rs.getDate("date"),rs.getString("path")));
			}
			rs.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
			vectOut.clear();
		}
		return vectOut;
	}
	
	
	/**
	 * Removes from the database the volume with name <code>name</code> ande deletes
	 * every directory and file that belong to that volume
	 * @param name the name of the volume to be deleted
	 * @throws SQLException if some error occured during query
	 */
	protected void deleteVolume(String name) throws SQLException
	{
		boolean settedAutocommitOff = false;
		
		if(connection.getAutoCommit() == true)
		{
			startTransaction();
			settedAutocommitOff = true;
		}

		/*
		 * Only volumes are directly deleted because related directories
		 * and files have foreign keys with "CASCADE" property setted.
		 */
		deleteVolumeStatement.setString(1, name);
		deleteVolumeStatement.executeUpdate();
		
		if(settedAutocommitOff)
			commitTransaction();
	}
	
	
	/**
	 * Saves the pattern inserted in the ignore field to the database
	 * @param patterns an array containing every pattern to be ignored during volume creation
	 */
	public void saveIgnorePattern(String[] patterns)
	{
		try
		{	startTransaction();
			ignoreDeleteStatement.executeUpdate();
			for(int i = 0; i<patterns.length; i++)
			{
				ignoreInsertStatement.setString(1, patterns[i]);
				ignoreInsertStatement.executeUpdate();
			}
			commitTransaction();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
		}
	}
	
	/**
	 * Retrives the saved ignore pattern from the database, if nothing is retrived a default
	 * pattern is returned
	 * @return the ignore pattern
	 */
	public String getIgnorePattern()
	{
		String pattern = "";
		try
		{
			ResultSet rs = ignoreSelectStatement.executeQuery();
			while(rs.next())
			{
				pattern += rs.getString(1) + ",";
			}
			rs.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			lastError = e.getMessage();
			MessageDialog.show("SQL Exception is thrown", "SQL Exception: "+lastError, GUI.errorIcon);
			pattern = null;
		}
		
		if(pattern==null || pattern.equals(""))
			pattern = "*.tmp,Thumbs.db,*.temp,*~,";
		
		return pattern.substring(0, pattern.length()-1);
	}
}
