/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.4
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 14/nov/07 14:16:06
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is CatalogJTable.java, part of "jCathy"
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import jdro.cathy.resources.Messages;

/**
 * TODO inserire introduzione
 * @author Simone Rossetto
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class CatalogJTable extends JTable implements MouseListener
{
	/**
	 * Creates a standard <code>CatalogJTable</code> with the volumes already cataloged
	 * in the database. It creats a new default <code>CatalogTableModel</code> and use
	 * this as the model for the new table
	 */
	public CatalogJTable()
	{
		super(new CatalogTableModel(false));
		addMouseListener(this);
	}

	/**
	 * It checks if the argument <code>dataModel</code> is an instance of
	 * <code>CatalogTableModel</code>. If so it sets this model to the <code>CatalogJTable</code>
	 * and setup the correct dimensions of the columns, if not it throws an
	 * <code>IllegalArgumentException</code>
	 * @param dataModel the model to be setup in this Table
	 * @exception IllegalArgumentException will be thrown if the model passed isn't an instance of
	 * <code>CatalogTableModel</code>
	 */
	@Override
	public void setModel(TableModel dataModel)
	{
		if(dataModel instanceof CatalogTableModel)
		{
			super.setModel(dataModel);
			int w = getWidth();
			getColumn(getColumnName(0)).setPreferredWidth(w/5);
			getColumn(getColumnName(1)).setPreferredWidth(w/15);
			getColumn(getColumnName(2)).setPreferredWidth(w/300);
			getColumn(getColumnName(3)).setPreferredWidth(w/300);
			getColumn(getColumnName(4)).setPreferredWidth(w/15);
			getColumn(getColumnName(5)).setPreferredWidth(w/4);
		}
		else
		{
			throw new IllegalArgumentException(Messages.getString("CatalogJTable.illegalArgumentExceptionMsg")); //$NON-NLS-1$
		}
	}
	
	
	/**
	 * When the user clicks this method set the new volume name on
	 * <code>Cathy.lastSelectedVolume</code>
	 * @param event
	 */
	public void mouseClicked(MouseEvent event)
	{
		String nuova = ((String)getModel().getValueAt(getSelectedRow(), 0));
		Cathy.lastSelectedVolume.setVolumeName(nuova);
	}
	
	public void mousePressed(MouseEvent ignore) { }
	public void mouseEntered(MouseEvent ignore) { }
	public void mouseExited(MouseEvent ignore) { }
	public void mouseReleased(MouseEvent ignore) { }
	

	/**
	 * Creates a new model ad set it up to the table.<br>
	 * This method is used when a new volume is added to the catalog so this method
	 * also executes the <code>update<code> method of <code>menuVolume</code>
	 * in order to disable the "Delete" button
	 */
	public void refresh()
	{
		setModel(new CatalogTableModel(true));
		Cathy.GUI.menuVolume.update(null, null);
	}
	
	/**
	 * This subclass is used as default <code>TableModel</code> for <code>CatalogJTable</code>.<br>
	 * It contains everything needed to setup a new model according to the functionality
	 * of the "Catalog" tab: query, classes, header-name, coversion script and printing
	 * functions.<br>
	 * The "Catalog" tab doesn't need any interaction from the user, so the model is standard:
	 * a final <code>QUERY</code> is executed and the results are show in the table
	 */
	private static class CatalogTableModel extends CathyTableModel
	{
		private Vector<VolumeEntry> data;
		
		private static final String[] catalogHeader = new String[]{"Name", "Size", "Dirs", "Files", "Date", "Source"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		private static final Class[] catalogClasses = new Class[]
		{
	        java.lang.String.class,
	        java.lang.Number.class,
	        java.lang.Number.class,
	        java.lang.Number.class,
	        java.util.Date.class,
	        java.lang.String.class
		 };
		
		
		/**
		 * Executes the <code>QUERY</code> and constructs a new <code>CatalogTableModel</code>
		 * setting returned data as <code>data</code> attribute
		 * @param select true if the data must be getted from the database, false to create an
		 * empty model
		 */
		public CatalogTableModel(boolean select)
		{
			super(catalogHeader,catalogClasses);
			if(select)
				data = Cathy.DB.getVolumes();
			else
				data = new Vector<VolumeEntry>();
		}
		
		/**
		 * Get the dimension of the <code>Vector</code>
		 * @return number of rows in current <code>JTable</code>
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount()
		{
			return data.size();
		}

		/**
		 * Simply return the <code>String</code> indexed by (rowIndex,columnIndex).
		 * If <code>columnIndex==4</code> it converts the text in a <code>Date</code>
		 * so the real date can be shown in the format of the current Locale,
		 * if <code>columnIndex==0</code> it returns the name of the volume in a bold style
		 * @param rowIndex the number of the row
		 * @param columnIndex the numbero of the column
		 * @return the value of the cell indexed by (rowIndex,columnIndex)
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			Object obj;
			
			try
			{
				switch(columnIndex)
				{
					case 0:
						obj = "<html><b>"+data.elementAt(rowIndex).getVolume()+ "</b></html>"; //$NON-NLS-1$ //$NON-NLS-2$
						break;
					case 1:
						obj = roundSize(data.elementAt(rowIndex).getSize());
						break;
					case 2:
						obj = data.elementAt(rowIndex).getDirs();
						break;
					case 3:
						obj = data.elementAt(rowIndex).getFiles();
						break;
					case 4:
						obj = data.elementAt(rowIndex).getDate();
						break;
					case 5:
						obj = data.elementAt(rowIndex).getPathStr();
						break;
					default:
						obj = null;
				}
			}
			catch(Exception e)
			{
				obj = null;
			}
			return obj;
		}
	}
}
