/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.6
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2019 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 13/nov/07 21:30:42
 |  License Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is ExploreJTable.java, part of "jCathy"
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

import java.io.File;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableModel;

import jdro.cathy.resources.Messages;

/**
 * TODO scrivere introduzione
 * @author Simone Rossetto
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class ExploreJTable extends JTable
{
	/**
	 * Creates a new <code>ExploreJTable</code> setting the default <code>ExploreTableModel</code>
	 * as the model
	 */
	public ExploreJTable()
	{
		super(new ExploreTableModel());
	}

	/**
	 * It checks if the argument <code>dataModel</code> is an instance of
	 * <code>ExploreTableModel</code>. If so it sets this model to the <code>ExploreJTable</code>
	 * and setup the correct dimensions of the columns, if not it throws an
	 * <code>IllegalArgumentException</code>
	 * @param dataModel the model to be setup in this Table
	 * @exception IllegalArgumentException will be thrown if the model passed isn't an instance of
	 * <code>ExploreTableModel</code>
	 */
	@Override
	public void setModel(TableModel dataModel)
	{
		if(dataModel instanceof ExploreTableModel)
		{
			super.setModel(dataModel);
    		int icoW = JTreeObserver.DIR_ICON.getIconWidth() + 6;
    		int w = getWidth() - icoW;
    		getColumn(getColumnName(0)).setMaxWidth(icoW);
    		getColumn(getColumnName(1)).setPreferredWidth(w/3);
    		getColumn(getColumnName(2)).setPreferredWidth(w/12);
    		getColumn(getColumnName(3)).setPreferredWidth(w/12);
    		getColumn(getColumnName(4)).setPreferredWidth(w/300);
    		getColumn(getColumnName(5)).setPreferredWidth(w/300);
		}
		else
		{
			throw new IllegalArgumentException(Messages.getString("ExploreJTable.illegalArgumentExceptionMsg")); //$NON-NLS-1$
		}
	}
	
	
	
	/**
	 * When the selection changes this method add the name of the selected file (or directory)
	 * to the <code>Cathy.GUI.expPath</code>
	 * @param event
	 */
	@Override
	public void valueChanged(ListSelectionEvent event)
	{
		super.valueChanged(event);
		
		Cathy.GUI.expPath.setText(Cathy.GUI.expTabStartPath + File.separator + Cathy.GUI.expTable.getModel().getValueAt(Cathy.GUI.expTable.getSelectedRow(), 1));
	}

	/**
	 * This method creates a new <code>ExploreTableModel</code> and set this model to the
	 * <code>ExploreJTable</code>. The new model is created starting with the Id number
	 * of the selected directory
	 * @param dirId the id of the selected-directory
	 */
	public void setData(int dirId)
	{
		setModel(new ExploreTableModel(dirId));
	}

	
	/**
	 * Used to clear the visualization of an <code>ExploreJTable</code> when
	 * the user selects a new volume
	 */
	public void clear()
	{
		setModel(new ExploreTableModel());
	}
	
	/**
	 * This subclass is used as default <code>TableModel</code> for <code>ExploreJTable</code>.<br>
	 * It contains everything needed to setup a new model according to the functionality
	 * of the "Explore" tab: query, classes, header-name, coversion script and printing
	 * functions.
	 */
	public static class ExploreTableModel extends CathyTableModel
	{	
		private Vector<TableTreeEntryNode> data;
		
		private static final String[] expHeader = new String[]{"","Name", "Size", "Date", "Sub Dir", "Files"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		private static final Class[] expClasses = new Class[]
		                                                  {
			javax.swing.ImageIcon.class,
			java.lang.String.class,
			java.lang.Number.class,
	        java.util.Date.class,
	        java.lang.Number.class,
	        java.lang.Number.class
		                                                  };
		
		
		/**
		 * This defult construtor set an empty <code>Vector< TableTreeEntryNode ></code>
		 * as <code>data</code> attribute.
		 * The method <code>getValueAt</code> will return always null and the table will be empty
		 * with only header section
		 */
		public ExploreTableModel()
		{
			super(expHeader, expClasses);
			data = new Vector<TableTreeEntryNode>();
		}
		
		
		/**
		 * Constructs a new <code>ExploreTableModel</code> executing the query in the database
		 * that retrives the content of the directory indexed by <code>dirId</code>
		 * @param dirId the id of the selected directory
		 */
		public ExploreTableModel(int dirId)
		{
			super(expHeader, expClasses);
			data = Cathy.DB.updateExploreTab(Cathy.lastSelectedVolume.getVolumeName(),dirId);
			if(dirId!=0)
				data.add(0, new NullEntry(dirId,Cathy.lastSelectedVolume.getVolumeName())); 
		}

		
		/**
		 * Get the dimension of the <code>data</code>
		 * @return number of rows in current <code>JTable</code>
		 * @see javax.swing.table.TableModel#getRowCount()
		 */
		public int getRowCount()
		{
			return data.size();
		}


		/**
		 * This method is used to contruct a <code>JTable</code> for this program.
		 * @param rowIndex the number of the row
		 * @param columnIndex the number of the column
		 * @return the value of the cell indexed by (rowIndex,columnIndex), null if an
		 * exception is thrown
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{	
			Object retObj;
			try
			{
				switch(columnIndex)
				{
					case 0:
						retObj = data.elementAt(rowIndex).getIcon();
						break;
					case 1:
						retObj = data.elementAt(rowIndex).getName();
						break;
					case 2:
						retObj = roundSize(data.elementAt(rowIndex).getSize());
						break;
					case 3:
						retObj =data.elementAt(rowIndex).getDate();
						break;
					case 4:
						retObj = data.elementAt(rowIndex).getDirs();
						break;
					case 5:
						retObj =data.elementAt(rowIndex).getFiles();
						break;
					default:
						retObj = null;
				}
			}
			catch(Exception e)
			{
				retObj = null;
			}
				
			return retObj;
		}
		
		/**
		 * Returns the id associated with the element at <code>rowIndex</code>
		 * @param rowIndex the number of the row
		 * @returnthe id associated with the element at <code>rowIndex</code>
		 */
		public Integer getId(int rowIndex)
		{
			return data.elementAt(rowIndex).getId();
		}
	}

}
