/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.3+svn
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 15/nov/07 20:34:47
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is SearchJTable.java, part of "jCathy"
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import jdro.cathy.resources.Messages;

/**
 * TODO scrivere introduzione
 * @author Simone Rossetto
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class SearchJTable extends JTable implements Observer, MouseListener
{	
	/**
	 * Creates a standard <code>SearchJTable</code> with an empty model.
	 * This empty table is shown when the program starts
	 */
	public SearchJTable()
	{
		super(new SearchTableModel());
		addMouseListener(this);
	}

	/**
	 * It checks if the argument <code>dataModel</code> is an instance of
	 * <code>SearchTableModel</code>. If so it sets this model to the <code>SearchJTable</code>
	 * and setup the correct dimensions of the columns, if not it throws an
	 * <code>IllegalArgumentException</code>
	 * @param dataModel the model to be setup in this Table
	 * @exception IllegalArgumentException will be thrown if the model passed isn't an instance of
	 * <code>SearchTableModel</code>
	 */
	@Override
	public void setModel(TableModel dataModel)
	{
		if(dataModel instanceof SearchTableModel)
		{
			super.setModel(dataModel);
    		int icoW = JTreeObserver.DIR_ICON.getIconWidth() + 6;
    		int w = getWidth() - icoW;
    		getColumn(getColumnName(0)).setMaxWidth(icoW);
			getColumn(getColumnName(1)).setPreferredWidth(w/7);
			getColumn(getColumnName(2)).setPreferredWidth(w/17);
			getColumn(getColumnName(3)).setPreferredWidth(w/8);
			getColumn(getColumnName(4)).setPreferredWidth(w/3);
			getColumn(getColumnName(5)).setPreferredWidth(w/15);
			
		}
		else
		{
			throw new IllegalArgumentException(Messages.getString("SearchJTable.illegalArgumentExceptionMsg")); //$NON-NLS-1$
		}
	}
	
	/**
	 * When the user clicks this method set the new volume name on
	 * <code>Cathy.lastSelectedVolume</code>
	 * @param event
	 */
	public void mouseClicked(MouseEvent event)
	{
		String nuova = ((String)getModel().getValueAt(getSelectedRow(), 3));
		Cathy.lastSelectedVolume.setVolumeName(nuova);
	}
	
	public void mousePressed(MouseEvent ignore) { }
	public void mouseEntered(MouseEvent ignore) { }
	public void mouseExited(MouseEvent ignore) { }
	public void mouseReleased(MouseEvent ignore) { }
	
	
	/**
	 * This method creates a new <code>SearchTableModel</code> and set this model to the
	 * <code>SearchJTable</code>. The new model is created starting with the "pattern"
	 * manually inserted by the user.<br>
	 * If model is empty e dialog is show to alert the user that nothing has been found
	 * @param pattern the pattern to be searched
	 */
	public void setData(String pattern)
	{
		//clearSelection();
		SearchTableModel stmTmp = new SearchTableModel(pattern);
		setModel(stmTmp);
		if(stmTmp.getRowCount() == 0)
			MessageDialog.show(Messages.getString("SearchJTable.MSG.noResultFoundTitle"), Messages.getString("SearchJTable.MSG.noResultFoundMsg"), GUI.warningIcon); //$NON-NLS-1$ //$NON-NLS-2$
	}
	

	/**
	 * If the new selected volume is null, this means that a volume has been deleted,
	 * so the result presented in this table can now be invalid and must be hidden
	 * @param o the observable object that executed <code>notifyObservers()</code>
	 * @param arg an attribute passed to the <code>notifyObservers()</code> method:
	 *            in this case the name of the new selected volume
	 */
	public void update(Observable o, Object arg)
	{
		if(arg == null)
			setModel(new SearchTableModel());
	}
	
	
	/**
	 * This subclass is used as default <code>TableModel</code> for <code>SearchJTable</code>.<br>
	 * It contains everything needed to setup a new model according to the functionality
	 * of the "Search" tab: query, classes, header-name, coversion script and printing
	 * functions.
	 */
	private static class SearchTableModel extends CathyTableModel
	{
		private Vector<TableTreeEntryNode> data;
		
		private static final String[] catalogHeader = new String[]{"", "Name", "Size", "Volume", "Path", "Date"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		private static final Class[] catalogClasses = new Class[]
		                                                  {
			javax.swing.ImageIcon.class,
			java.lang.String.class,
	        java.lang.Number.class,
	        java.lang.String.class,
	        java.lang.String.class,
	        java.util.Date.class
		                                                  };
			
		
		/**
		 * Constructs an empty Model for an empy <code>SearchJTable</code>
		 */
		public SearchTableModel()
		{
			super(catalogHeader,catalogClasses);
			data = new Vector<TableTreeEntryNode>();
		}
		
		/**
		 * Constructs a new <code>SearchTableModel</code> executing the query in the database.
		 * The query is written here because the conversion from <code>Vector<String[]></code>
		 * to <code>Vector<TableTreeEntryNode></code> must be done in a specific way. The query
		 * is optimized for this conversion.
		 * 
		 * @param pattern the pattern to be searched
		 */
		public SearchTableModel(String pattern)
		{
			super(catalogHeader,catalogClasses);
			data = Cathy.DB.searchPattern("%" + pattern.replace('*', '%') + "%");
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
		 * Simply return the <code>String</code> indexed by (rowIndex,columnIndex).
		 * If <code>columnIndex==0</code> the corresponding <code>Icon</code> is returned,
		 * if <code>columnIndex==1</code> the corresponding name is returned,
		 * if <code>columnIndex==5</code> the corresponding <code>Date</code> is returned
		 * @param rowIndex the number of the row
		 * @param columnIndex the numbero of the column
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
						retObj = data.elementAt(rowIndex).getVolume();
						break;
					case 4:
						String tmpStr = data.elementAt(rowIndex).getPathStr().replace(GUI.SEPARATOR, File.separatorChar);
						if(tmpStr.endsWith(File.separator) && tmpStr.length()!=1)
							tmpStr = tmpStr.substring(0, tmpStr.length()-1);
						retObj = tmpStr;
						break;
					case 5:
						retObj = data.elementAt(rowIndex).getDate();
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
	}
}
