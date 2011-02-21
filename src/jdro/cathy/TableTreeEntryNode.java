/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.3+svn
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 12/nov/07 14:31:29
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is TableTreeEntryNode.java, part of "jCathy"
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

import java.sql.Date;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * TODO scrivere introduzione
 * @author Simone Rossetto
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public abstract class TableTreeEntryNode extends DefaultMutableTreeNode
{
	private String name;
	private Long size;
	private Date date;
	private String pathStr;
	
	
	/**
	 * @param name
	 * @param size
	 * @param date
	 * @param sourcePath
	 */
	public TableTreeEntryNode(String name, Long size, Date date, String sourcePath)
	{
		super();
		this.name = name;
		this.size = size;
		this.date = date;
		this.pathStr = sourcePath;
	}


	/**
	 * Returns the name of this element.<br>
	 * In <code>JTree</code> and in <code>JTable</code> this method is called
	 * to show an element
	 */
	@Override
	public String toString()
	{
		return name;
	}


	/** @return the name of this entry-node */
	public String getName() { return name; }
	
	/** @return the total size of this entry-node in byte */
	public Long getSize() { return size; }

	/** @return the date and time of creation of this entry-node*/
	public Date getDate() { return date; }

	/** @return the path of this entry-node */
	public String getPathStr() { return pathStr; }
	
	
	public abstract Icon getIcon();
	public abstract Integer getId();
	public abstract String getVolume();
	public abstract Integer getParentId();
	public abstract Object getDirs();
	public abstract Object getFiles();
	
}
