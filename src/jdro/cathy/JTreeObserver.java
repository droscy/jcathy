/*
 +--------------------------------------------------------------------------------
 |	"jCathy" v0.00
 |	(DESCRIPTION)
 |	========================================
 |	by Simone Rossetto
 |	Copyright (C) 2007 Simone Rossetto
 |	E-Mail: simros85@gmail.com
 |	========================================
 |	File created on 11/nov/07 13:10:12
 |	Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +--------------------------------------------------------------------------------
 |	This file is JTreeObserver.java, part of "jCathy"
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

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/**
 * This class is used for the tree of the directories in the "Explore" tab and
 * observes the changing of <code>lastSelectedVolume</code>. Is identical to
 * <code>JTree</code> except for <code>update()</code> method needed to be
 * an <code>Observer</code>
 * 
 * @author Simone Rossetto
 */
@SuppressWarnings("serial")
public class JTreeObserver extends JTree implements Observer
{
	private static final DefaultTreeCellRenderer RENDERER = new DefaultTreeCellRenderer();
	public static final Icon DIR_ICON = RENDERER.getClosedIcon();
	public static final Icon FILE_ICON = RENDERER.getLeafIcon();
	public static final Icon UP_ICON = GUI.upIcon;

	private String lastVolume;

	/*
	 * This default construtor is neede during the first creation of the GUI
	 * in order to display a default <code>JTree</code>
	 * 
	 * XXX now is not needed, left here only for debug purpose
	 */
	/*public JTreeObserver()
	{
		super();
		lastVolume = null;
	}*/

	/**
	 * @param newModel
	 */
	public JTreeObserver(TreeModel newModel)
	{
		super(newModel);
		lastVolume = null;
	}
	

	/**
	 * Creates a new <code>JTree</code> to be shown in the "Explore" tab
	 * staring from the selected Volume. If <code>lastVolume==lastSelectedVolume</code>
	 * this method does nothig, otherwise it creates the new <code>JTree</code>
	 * 
	 * @param observableObj the observable object that executed <code>notifyObservers()</code>
	 * @param volumeName an attribute passed to the <code>notifyObservers()</code> method:
	 *                   in this case the name of the new selected volume
	 */
	public void update(Observable observableObj, Object volumeName)
	{
		if(volumeName == null)
			lastVolume = null;
		else //if(lastVolume == null || (lastVolume != null && !lastVolume.equals((String)volumeName)))
		{
			lastVolume = (String)volumeName;
			TableTreeEntryNode rootNode = new VolumeEntry(lastVolume,0,0,0,null,"");
			createSubTree(rootNode, 0);
			setModel(new DefaultTreeModel(rootNode));
			setSelectionRow(0);
			//System.gc();
		}
	}


	/**
	 * A recursive method that creates a new <code>JTree</code> starting from
	 * a <code>DefaultMutableTreeNode</code> used as rootNode
	 * 
	 * @param parentNode the node to be used as rootNode for this iteration
	 * @param parentId the ID number of the parentNode in the database
	 */
	private void createSubTree(TableTreeEntryNode parentNode, int parentId)
	{
		Vector<DirEntry> vectTmp = Cathy.DB.createSubTreeQuery(lastVolume, parentId);
		TableTreeEntryNode childNode;
		Iterator<DirEntry> iter = vectTmp.iterator();
		while(iter.hasNext())
		{
			childNode = iter.next();
			parentNode.add(childNode);
			createSubTree(childNode, childNode.getId());
		}
	}

	
	/**
	 * This method is needed to choose the correct directory in the <code>expTree</code>.
	 * When a user double-clicks on a directory in the <code>expTable</code> the program
	 * will search for that directory in the <code>expTree</code> and selects it
	 * @param dirId the id of the selected directory
	 * @param startingRow the row of the parent diretory (improve performance)
	 * @param bias this parameter indicates if the search must be done forward or backward
	 * @return the complete <code>TreePath</code> from the root to the selected directory
	 * @exception IllegalArgumentException if <code>(startingRow<0 || startingRow>=maxRow)</code>
	 */
	protected TreePath getDoubleClickedDirectoryPath(int dirId, int startingRow, Bias bias)
	{
		int maxRow = getRowCount();
		if(startingRow < 0 || startingRow >= maxRow)
			throw new IllegalArgumentException();
		
		int increment = (bias == Position.Bias.Forward) ? 1 : -1;
		int row = startingRow;
		do
		{
			TreePath path = getPathForRow(row);
			int idTmp = ((TableTreeEntryNode)path.getLastPathComponent()).getId();

			if(idTmp == dirId)
				return path;
			row = (row + increment + maxRow) % maxRow;
		} 
		while(row != startingRow);
		
		return null;
	}
}
