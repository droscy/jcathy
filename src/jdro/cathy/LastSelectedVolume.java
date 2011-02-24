/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.4
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 11/nov/07 13:30:39
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is LastSelectedVolume.java, part of "jCathy"
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

import java.awt.Cursor;
import java.util.Observable;

/**
 * Class that contains the name of the last-selected volume.
 * Can be observed: if the user select a new volume any observer can be updated
 * to show the new volume
 * @author Simone Rossetto
 */
public class LastSelectedVolume extends Observable
{
	/**<the name of the last-selected volume */
	private String volumeName;

	
	/**
	 * Constructs a default <code>LastSelectedVolume</code> setting null to the <code>volume</code>
	 */
	public LastSelectedVolume()
	{
		super();
		volumeName = null;
	}


	/**
	 * Returns the name of the last-selected volume
	 * @return the name of the last-selected volume
	 */
	public String getVolumeName()
	{
		return volumeName;
	}


	/**
	 * This method sets the new name to <code>volume</code> and calls:
	 * <ul>
	 * <li><code>setChanged()</code></li>
	 * <li><code>notifyObservers()</code></li>
	 * </ul>
	 * in order to notify all the observers.
	 * If the name is null or equal to the <code>volumeName</code> already setted, this method
	 * does nothing
	 * @param name the name of the new-selected volume
	 * @exception IllegalArgumentException is thrown if null is passed as the name of the volume
	 */
	public void setVolumeName(String name)
	{
		if(name!=null)
		{
			/** if the name contains html tag, this <code>replaceAll</code> removes them */
			name = name.replaceAll("<(html|/html|b|/b)>", "");
		}
		
		if((volumeName==null && name!=null) || (volumeName!=null && !volumeName.equals(name)))
			setChanged();
		
		volumeName = name;
		Cathy.GUI.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		notifyObservers(name);
		Cathy.GUI.setCursor(null);
	}
}
