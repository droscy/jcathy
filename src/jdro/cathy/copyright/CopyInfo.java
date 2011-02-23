/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.3+svn
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 14/nov/07 14:16:06
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is CopyInfo.java, part of "jCathy"
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

/**
 * Created on 20/ott/06 21:53:05
 */
package jdro.cathy.copyright;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import jdro.cathy.Cathy;

/**
 * Contains the license under which I release my programs
 * @author Simone Rossetto
 */
@SuppressWarnings("serial")
public class CopyInfo extends JDialog
{
	// CopyInfo for singleton pattern
	private static CopyInfo ci = null;
	
	// Header
	private JLabel logoLabel;
	private JLabel copyLabel[] = new JLabel[3];
	private JPanel copyPanel;
	private JPanel headerPanel;
	
	// ClassLoader header icon
	private ClassLoader cldr = this.getClass().getClassLoader();
	protected URL imageURL;
	protected ImageIcon logoImage;
	
	// Licenses section
	private JTextArea licenseArea;
	private JScrollPane licenseJsp;
	private JPanel licensePanel;
	private JLabel hsqldbLabel;
	
	// Footer
	private JButton closeButton;
	private JPanel closePanel = new JPanel();
	
	// Internationalized messages
	private static final String MESSAGES_FILE = "jdro.cathy.copyright.copyinfo";
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle(MESSAGES_FILE);


	/**
	 * Gets the internationalized strings form .properites files
	 * @param key the key-string of the wanted messages
	 * @return the wanted string if found, "!key!" if not found
	 */
	private static String getMessage(String key)
	{
		try
		{
			return MESSAGES.getString(key);
		}
		catch(MissingResourceException e)
		{
			return '!' + key + '!';
		}
	}
	
	
	/**
	 * Hides the window after clicking <code>closeButton</code>
	 */
	private class CloseButtonActionListener implements ActionListener
	{
		/**
		 * Hides the window
		 */
		public void actionPerformed(ActionEvent e)
		{
			setVisible(false);
		}
	}
	
	/**
	 * Contructs a new dialog
	 * @param programName title to show in the titlebar and for copyright information
	 * @param year the year of the copyright
	 */
	private CopyInfo(JFrame owner, String programName, String year, String programImage)
	{
		super(owner, getMessage("ABOUT") + programName,true);
		
		headerPanel = new JPanel();
		headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		headerPanel.setLayout(new BorderLayout());
		{
			imageURL = cldr.getResource(programImage);
			logoImage = new ImageIcon(imageURL);
			logoLabel = new JLabel(logoImage);
			logoLabel.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
			
			copyPanel = new JPanel();
			copyPanel.setLayout(new GridLayout(3,1,0,0));
			{
				copyLabel[0] = new JLabel(programName);
				copyLabel[1] = new JLabel("Copyright \u00A9 " + year + " Simone Rossetto <simros85@gmail.com>");
				copyLabel[2] = new JLabel("GNU General Public License v3");
			}
			for(int i=0;i<3;i++)
				copyPanel.add(copyLabel[i]);
		}
		headerPanel.add(BorderLayout.WEST, logoLabel);
		headerPanel.add(BorderLayout.CENTER, copyPanel);
		
		String txt = "";
		try
		{
			File copyingFile = new File(Cathy.LOCAL_PATH + "COPYING");
			if(!copyingFile.exists())
				copyingFile = new File(Cathy.LOCAL_PATH + "copying");
			FileReader copyingFileReader = new FileReader(copyingFile);
			BufferedReader copying = new BufferedReader(copyingFileReader);
			while(copying.ready())
				txt += copying.readLine() + "\n";
			copying.close();
			copyingFileReader.close();
		}
		catch(Exception fnfExcep)
		{
			txt = getMessage("ERROR_LOADING_COPYING_FILE");
		}
		
		licensePanel = new JPanel(new BorderLayout(0,10));
		licensePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		{
			licenseArea = new JTextArea(txt);
			licenseArea.setFont(Font.decode("Courier New 12"));
			licenseArea.setEditable(false);
			licenseArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
			licenseJsp = new JScrollPane(licenseArea);

			hsqldbLabel = new JLabel(getMessage("HSQLDB_LABEL"));
			hsqldbLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		licensePanel.add(licenseJsp,BorderLayout.CENTER);
		licensePanel.add(hsqldbLabel,BorderLayout.SOUTH);
		
		closeButton = new JButton(getMessage("CLOSE"));
		closeButton.addActionListener(new CloseButtonActionListener());
		closeButton.setSelected(true);
		getRootPane().setDefaultButton(closeButton);
		closePanel.add(closeButton);
		closePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		
		add(BorderLayout.NORTH,headerPanel);
		add(BorderLayout.CENTER, licensePanel);
		add(BorderLayout.SOUTH, closePanel);
		
		setSize(600, 600);
		setLocationRelativeTo(owner);
		
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}
	
	/**
	 * Returns a reference to a <code>CopyInfo</code>, if it doesn't exist yet, it creates it
	 * @param programName title to show in the titlebar and for copyright information
	 * @param year the year of the copyright
	 * @return Reference to a <code>CopyInfo</code>
	 */
	public static CopyInfo getInstance(JFrame owner, String programName, String year, String programImage)
	{
		if(ci == null)
			ci = new CopyInfo(owner, programName, year, programImage);
		return ci;
	}

}
