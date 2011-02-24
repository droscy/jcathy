/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.4
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 16/ott/07 14:19:14
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is MessageDialog.java, part of "jCathy"
 |
 |  This program is free software: you can redistribute it and/or modify
 |  it under the terms of the GNU General Public License as published by
 |  the Free Software Foundation, either version 3 of the License, or
 |  (at your option) any later version with the additional exemption that
 |  compiling, linking, and/or using OpenSSL is allowed.
 |
 |  This program is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU General Public License
 |  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 +-----------------------------------------------------------------------------
 */
package jdro.cathy;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A <code>JDialog</code> to show messages
 * @author Simone Rossetto
 */
@SuppressWarnings("serial")
public class MessageDialog extends JDialog
{
	
	private static MessageDialog msgDialog = null;
	private static Window owner = null;
	
	private static final String START_HTML = "<html><center>";
	private static final String END_HTML = "</center></html>";
	
	private JLabel imageLabel;
	private JLabel msgLabel;
	private JButton okButton;
	private JPanel okPanel;

	/**
	 * Private constructor to use with Singleton Pattern
	 * 
	 * @param owner the owner of this Dialog
	 * @param title the title of the dialog
	 * @param msg the message that must be shown
	 * @param image the icon to be shown
	 */
	private MessageDialog(Window owner, String title, String msg, ImageIcon image)
	{
		super(owner,title);
		setModal(true);

		imageLabel = new JLabel(image);
		imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		msgLabel = new JLabel(msg);
		msgLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

		okButton = new JButton((owner==null?"Exit":"OK"));
		okButton.addActionListener(actionHide);
		getRootPane().setDefaultButton(okButton);
		
		okPanel = new JPanel();
		okPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		okPanel.add(okButton);

		add(BorderLayout.WEST, imageLabel);
		add(BorderLayout.CENTER, msgLabel);
		add(BorderLayout.SOUTH, okPanel);

		setResizable(false);
		
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}

	/**
	 * Action that hide the dialog window
	 */
	private AbstractAction actionHide = new AbstractAction()
	{
		/**
		 * If <code>owner==null</code> this method close the program with status-code 1,
		 * otherwise it hides the dialog window.
		 * <code>owner==null</code> if some errors occured during database connection!
		 * @param actionEvent
		 */
		public void actionPerformed(ActionEvent actionEvent)
		{
			if(getOwner() == null)
				System.exit(1);
			else
				setVisible(false);
		}
	};


	/**
	 * Static function that return a reference to the dialog. If it wasn't
	 * instantiated creates a new one
	 * @param title the title of the window
	 * @param msg the message that must be shown
	 * @param image the <code>ImageIcon</code> to be shown
	 * @return reference to a <code>MessageDialog</code>
	 */
	public static MessageDialog getInstance(String title, String msg, ImageIcon image)
	{
		if(msgDialog == null)
			msgDialog = new MessageDialog(owner, title, START_HTML+msg+END_HTML, image);
		else
		{
			msgDialog.imageLabel.setIcon(image);
			msgDialog.msgLabel.setText(START_HTML+msg+END_HTML);
			msgDialog.setTitle(title);
		}
		msgDialog.pack();
		msgDialog.setLocationRelativeTo(owner);
		return msgDialog;
	}
	
	/**
	 * Gets an instace of <code>MessageDialog</code> and then shows it
	 * @param title the title of the window
	 * @param msg the message that must be shown
	 * @param image the <code>ImageIcon</code> to be shown
	 */
	public static void show(String title, String msg, ImageIcon image)
	{
		msgDialog = getInstance(title, msg, image);
		msgDialog.setVisible(true);
	}
	

	/**
	 * @param owner the owner to be setted for this dialog
	 */
	public static void setOwner(Window owner)
	{
		MessageDialog.owner = owner;
		if(msgDialog!=null)
			msgDialog.okButton.setText("Ok");
	}
}
