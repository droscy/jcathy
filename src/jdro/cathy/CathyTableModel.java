/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.5
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2014 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 08/nov/07 15:09:34
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is CathyTableModel.java, part of "jCathy"
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

import java.text.DecimalFormat;

import javax.swing.table.AbstractTableModel;

/**
 * This class represent the model needed to show in a JTable the result got from
 * a database-query.
 * 
 * @author Simone Rossetto
 */
public abstract class CathyTableModel extends AbstractTableModel
{
	private String[] header; /**< the name of the columns */
	private Class[] classes; /**< the type of the columns (needed to control the alignment) */
	//protected Vector<?> data; /**< the data-output from the query */

	/**
	 * Constructs a new <code>CathyTableModel</code> setting the attributes except <code>data</code>.
	 * Data will be added by calling <code>setData()</code> method
	 * @param header an array that contains the name of the columns
	 * @param classes types of content of the columns
	 */
	public CathyTableModel(String[] header, Class[] classes)
	{
		this.header = header;
		this.classes = classes;
	}
	
	
	/*
	 * Constructs a new <code>CathyTableModel</code> setting all the attributes
	 * @param header an array that contains the name of the columns
	 * @param classes types of content of the columns
	 * @param data contains the data-output from the query
	 */
	/*public CathyTableModel(String[] header, Class[] classes, Vector<?> data)
	{
		this.header = header;
		this.classes = classes;
		this.data = data;
	}*/


	/**
	 * @param column the index of the column
	 * @return the header-name of the column
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column)
	{
		return header[column];
	}


	/**
	 * Get the number of columns
	 * @return number of columns in current <code>JTable</code>
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return header.length;
	}

	/**
	 * Get the dimension of the <code>Vector</code>
	 * @return number of rows in current <code>JTable</code>
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public abstract int getRowCount();


	/**
	 * Subclasses must implements this method.
	 * This is used to construct the cell indexed by (rowIndex,columnIndex)
	 * @param rowIndex the number of the row
	 * @param columnIndex the numbero of the column
	 * @return the value of the cell indexed by (rowIndex,columnIndex)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public abstract Object getValueAt(int rowIndex, int columnIndex);



	/**
	 * Return always false because every <code>JTable</code> are needed to show data,
	 * not to add or modify data
	 * @return false
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}


	/**
	 * Returns the type of the value assigned by the array <code>classes</code>
	 * @param columnIndex the number of the column
	 * @return the type of the requested column
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class getColumnClass(int columnIndex)
	{
		return classes[columnIndex];
	}
	
	
	/**
	 * Converts a <code>Long</code> representing the size of a file expressed in bytes
	 * to a <code>String</code> expressed in power of bytes (Kb, Mb, Gb, Tb, x2^Pow).
	 * @param size the source <code>Long</code> with the size in bytes
	 * @return the new size correctly formatted in 0.00 Xb
	 */
	public static String roundSize(Long size)
	{
		String sizeOut;
		if(size==null)
		{
			sizeOut = null;
		}
		else
		{
			float fSize = (float)size;
			int exp = 0;
			while(fSize >= 1024)
			{
				fSize /= 1024;
				exp += 10;
			}
				
			String add;
			switch(exp)
			{
				case 0:
					add = " B";
					break;
				case 10:
					add = " Kb";
					break;
				case 20:
					add = " Mb";
					break;
				case 30:
					add = " Gb";
					break;
				case 40:
					add = " Tb";
					break;
				default:
					add = "x2^"+exp+" Byte";
			}
				
			DecimalFormat dFormat = new DecimalFormat("0.00");
			sizeOut = dFormat.format(fSize) + add;
		}

		return sizeOut;
	}
}
