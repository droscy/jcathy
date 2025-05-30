/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.7
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2025 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 29/nov/07 16:24:22
 |  License Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is VolumeEntry.java, part of "jCathy"
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
import java.sql.Date;

import javax.swing.Icon;

/**
 * TODO inserire introduzione
 * @author Simone Rossetto
 */
@SuppressWarnings("serial")
public class VolumeEntry extends TableTreeEntryNode
{
	private int dirs;
	private int files;
	
	/**
	 * @param name
	 * @param size
	 * @param dirs
	 * @param files
	 * @param date
	 * @param sourcePath
	 */
	public VolumeEntry(String name, long size, int dirs, int files, Date date, String sourcePath)
	{
		super(name,size,date,sourcePath);
		this.dirs = dirs;
		this.files = files;
	}


	/** @return the number of directories that exist in this volume */
	public Integer getDirs() { return dirs; }

	/** @return the number of files that exist in this volume */
	public Integer getFiles() { return files; }
	
	/** @return only the <code>File.separator</code> */
	public String getName() { return File.separator; }
	
	/** @return the name of this volume */
	public String getVolume() { return super.getName(); }

	
	
	/** @return null always (this method is not used for volume) */
	public Icon getIcon() { return null; }

	/** @return null always (this method is not used for volume) */
	public Integer getId() { return 0; }

	/** @return null always (this method is not used for volume) */
	public Integer getParentId() { return null; }
}
