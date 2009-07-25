/*
 +--------------------------------------------------------------------------------
 |	"jCathy" v0.00
 |	(DESCRIPTION)
 |	========================================
 |	by Simone Rossetto
 |	Copyright (C) 2007 Simone Rossetto
 |	E-Mail: simros85@gmail.com
 |	========================================
 |	File created on 29/nov/07 16:21:01
 |	Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +--------------------------------------------------------------------------------
 |	This file is DirEntry.java, part of "jCathy"
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

import java.sql.Date;

import javax.swing.Icon;

/**
 * TODO inserire introduzione
 * @author Simone Rossetto
 */
@SuppressWarnings("serial")
public class DirEntry extends TableTreeEntryNode
{
	private int id;
	private String volume;
	private int parentId;
	private int dirs;
	private int files;
	
	
	/**
	 * @param id
	 * @param name
	 * @param volume
	 * @param parentId
	 * @param size
	 * @param dirs
	 * @param files
	 * @param date
	 * @param pathStr
	 */
	public DirEntry(int id, String name, String volume, int parentId, long size, int dirs, int files, Date date, String pathStr)
	{
		super(name,size,date,pathStr);
		this.id = id;
		this.volume = volume;
		this.parentId = parentId;
		this.dirs = dirs;
		this.files = files;
	}


	/** @return the icon of this directory-node */
	public Icon getIcon() { return JTreeObserver.DIR_ICON; }

	/** @return the id of this directory */
	public Integer getId() { return id; }

	/** @return the volume which this directory belongs to */
	public String getVolume() { return volume; }

	/** @return the id of the parent directory */
	public Integer getParentId() { return parentId; }

	/** @return the number of directories that exist in this directory */
	public Integer getDirs() { return dirs; }

	/** @return the number of files that exist in this directory */
	public Integer getFiles() { return files; }
	
	
}
