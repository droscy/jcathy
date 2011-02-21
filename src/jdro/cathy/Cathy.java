/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.3+svn
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 04/nov/07 16:51:26
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is Cathy.java, part of "jCathy"
 |
 |	This program is free software: you can redistribute it and/or modify
 |	it under the terms of the GNU General Public License as published by
 |	the Free Software Foundation, either version 3 of the License, or
 |	(at your option) any later version with the additional exemption that
 |	compiling, linking, and/or using OpenSSL is allowed.
 |
 |	This program is distributed in the hope that it will be useful,
 |	but WITHOUT ANY WARRANTY; without even the implied warranty of
 |	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |	GNU General Public License for more details.
 |
 |	You should have received a copy of the GNU General Public License
 |	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 +-----------------------------------------------------------------------------
 */
package jdro.cathy;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import jdro.cathy.resources.Messages;


/**
 * @mainpage TODO deve essere scritta la mainpage del programma
 * TODO inserire ordinamento delle colonne
 * TODO creare import/export del database in formato XML
 * TODO trovare icona per il pulsante Rename
 * TODO Fare in modo che solo la ricerca sia IgnoreCase, ma non la memorizzazione sul database!
 * TODO Scrivere l'Usage nel README
 * @author Simone Rossetto
 * @version 0.7.3+svn
 */
public class Cathy
{
	public static final String NAME = "jCathy"; //$NON-NLS-1$
	public static final String VERSION = "v0.7.3+svn"; //$NON-NLS-1$
	public static final String YEAR = "2007-2011"; //$NON-NLS-1$
	public static final String ICON_PATH = "jdro/cathy/images/";
	public static final String ICON_FILENAME = ICON_PATH + "jcathy.png"; //$NON-NLS-1$
	
	/** the absolute path to the jar file containing this program */
	public static final String JAR_PATH;
	static
	{
		String startPath = Cathy.class.getResource("").getPath().replaceFirst("^.*:", "").replaceFirst("!.*$", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		String finalPath;
		try
		{
			finalPath = new URI(startPath).getPath();
		}
		catch(URISyntaxException e)
		{
			finalPath = startPath;
		}
		JAR_PATH = finalPath;
	}
	
	/** the absolute path to the directory that contains the jar of this program */
	public static final String LOCAL_PATH = JAR_PATH.substring(0, JAR_PATH.lastIndexOf(File.separatorChar) + 1);
	
	
	protected static GUI GUI;
	
	protected static final String TABLEvolume = "volumes"; //$NON-NLS-1$
	protected static final String TABLEdirectories = "directories"; //$NON-NLS-1$
	protected static final String TABLEfiles = "files"; //$NON-NLS-1$
	protected static final String TABLEignore = "ignore"; //$NON-NLS-1$
	protected static final String TABLEsettings = "settings"; //$NON-NLS-1$
	
	protected static final Database DB = new Database(LOCAL_PATH + "jcathy.db"); //$NON-NLS-1$
	
	/** the observable object that contains the name of the last-selected volume */
	protected static LastSelectedVolume lastSelectedVolume;
	
	
	/**
	 * Executes the connection with the database, if it succedes the GUI is created otherwise
	 * an error dialog is show
	 */
	public static void main(String[] args)
	{			
		if(DB.connect())
		{
			GUI = new GUI();
			GUI.volumeTable.refresh();
			GUI.searchField.requestFocusInWindow();
			GUI.setVisible(true);
			Runtime.getRuntime().addShutdownHook(new KillingCathy());
		}
		else
		{
			//TODO errore di connessione al database
			MessageDialog.show(Messages.getString("Cathy.MSG.connectionErrorTitle"), Messages.getString("Cathy.MSG.connectionErrorMsg"), GUI.errorIcon); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * This thread is executed just before Cathy's Shutdown
	 * in order to correctly disconnect from the database
	 */
	private static class KillingCathy extends Thread
	{
		/**
		 * If Cathy is still connected to the database
		 * the connection will be closed
		 */
		public void run()
		{
			if(DB.isConnected())
				DB.disconnect();
		}
	}
}
