/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.4
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 29/nov/07 16:45:59
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is FileEntry.java, part of "jCathy"
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

import java.sql.Date;

import javax.swing.Icon;

/**
 * TODO inserire introduzione
 * @author Simone Rossetto
 */
@SuppressWarnings("serial")
public class FileEntry extends TableTreeEntryNode
{
	private Icon icon;
	private String volume;
	private int parentId;
	
	
	/**
	 * @param icon
	 * @param name
	 * @param volume
	 * @param parentId
	 * @param size
	 * @param date
	 * @param pathStr
	 */
	public FileEntry(Icon icon, String name, String volume, int parentId, long size, Date date, String pathStr)
	{
		super(name,size,date,pathStr);
		this.icon = icon;
		this.volume = volume;
		this.parentId = parentId;
	}
	


	/** @return the icon of this file-node */
	public Icon getIcon() { return icon; }

	/** @return the volume which this file belongs to */
	public String getVolume() { return volume; }

	/** @return the id of the parent directory */
	public Integer getParentId() { return parentId; }

	/** @return null always (this method is not used for file */
	public Integer getId() { return null; }

	/** @return '-' always because files have no subdirs */
	public String getDirs() { return "-"; }

	/** @return '-' always because files have no subfiles */
	public String getFiles() {return "-"; }
	
	
}
