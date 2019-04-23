/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.6
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2019 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 29/nov/07 18:59:18
 |  License Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is NullEntry.java, part of "jCathy"
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

import javax.swing.Icon;

/**
 * TODO write introduction
 * @author Simone Rossetto
 */
@SuppressWarnings("serial")
public class NullEntry extends TableTreeEntryNode
{
	private int id;
	private String volume;
	
	/**
	 * @param id
	 * @param volume
	 */
	public NullEntry(int id, String volume)
	{
		super("..",null,null,null);
		this.id = id;
		this.volume = volume;
	}
	
	
	/** @return the <code>JTreeObserver.UP_ICON</code> always */
	public Icon getIcon() { return JTreeObserver.UP_ICON; }

	/** @return the id of the parent directory */
	public Integer getId() { return id; }

	/** @return the volume which this <code>NullNode</code> belongs to */
	public String getVolume() { return volume; }

	/** @return null always (this method is not used for Null) */
	public Integer getParentId() { return null; }

	/** @return null always (this method is not used for Null) */
	public Object getDirs() { return null; }

	/** @return null always (this method is not used for Null) */
	public Object getFiles() { return null; }

}
