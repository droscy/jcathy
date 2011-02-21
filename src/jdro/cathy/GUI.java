/*
 +-----------------------------------------------------------------------------
 |  "jCathy" v0.7.3+svn
 |  (simple cataloguer for removable devices)
 |  ========================================
 |  by Simone Rossetto
 |  Copyright (C) 2007-2011 Simone Rossetto
 |  E-Mail: simros85@gmail.com
 |  ========================================
 |  File created on 6/nov/07 16:42:01
 |  Licence Info: GNU GENERAL PUBLIC LICENSE (check file COPYING)
 +-----------------------------------------------------------------------------
 |  This file is GUI.java, part of "jCathy"
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jdro.cathy.copyright.CopyInfo;
import jdro.cathy.resources.Messages;

/**
 * TODO inserire introduzione, la ricerca va fatta senza * manuale e insensibile alle maiuscole
 * @author Simone Rossetto
 * @version 3.2.1 2010-08-15
 */
@SuppressWarnings("serial") //$NON-NLS-1$
public class GUI extends javax.swing.JFrame {
    
	private static CloseProgramAction cpAction;
	private static ExpTableDoubleClickedMouseListener rsMouseListener;
	private static SearchFunctionAction sfAction;
	private static AddNewVolumeAction anvAction;
	private static DeleteVolumeAction delvAction;
	private static RenameVolumeAction renvAction;
	
	private static CopyInfo copyInfoDialog;
	
	/** used as path separator while adding a new volume */
	protected static final char SEPARATOR = '/';
	
    /** needed to update the <code>expPath</code> during single selection in <code>expTable</code> */
	protected String expTabStartPath;
	
	// Icon image
	private ClassLoader cldr = this.getClass().getClassLoader();
	private static URL iconURL;
	protected static ImageIcon programIcon;
	protected static ImageIcon deleteIcon;
	protected static ImageIcon warningIcon;
	protected static ImageIcon errorIcon;
	protected static ImageIcon refreshIcon;
	protected static ImageIcon addIcon;
	protected static ImageIcon successIcon;
	protected static ImageIcon upIcon;
	
	/** this <code>JPopupMenu</code> is used to delete volume in the "Catalog" tab */
	protected CatalogPopupMenu popupMenu;
	
	
	
	/** Creates new form GUI */
    public GUI()
    {
    	iconURL = cldr.getResource(Cathy.ICON_FILENAME);
    	programIcon = new ImageIcon(iconURL);
    	iconURL = cldr.getResource(Cathy.ICON_PATH + "delete.png"); //$NON-NLS-1$
    	deleteIcon = new ImageIcon(iconURL);
    	iconURL = cldr.getResource(Cathy.ICON_PATH + "warning.png"); //$NON-NLS-1$
    	warningIcon = new ImageIcon(iconURL);
    	iconURL = cldr.getResource(Cathy.ICON_PATH + "error.png"); //$NON-NLS-1$
    	errorIcon = new ImageIcon(iconURL);
    	iconURL = cldr.getResource(Cathy.ICON_PATH + "refresh.png"); //$NON-NLS-1$
    	refreshIcon = new ImageIcon(iconURL);
    	iconURL = cldr.getResource(Cathy.ICON_PATH + "add.png"); //$NON-NLS-1$
    	addIcon = new ImageIcon(iconURL);
    	iconURL = cldr.getResource(Cathy.ICON_PATH + "success.png"); //$NON-NLS-1$
    	successIcon = new ImageIcon(iconURL);
    	iconURL = cldr.getResource(Cathy.ICON_PATH + "up.png"); //$NON-NLS-1$
    	upIcon = new ImageIcon(iconURL);
    	
    	cpAction = new CloseProgramAction();
    	rsMouseListener = new ExpTableDoubleClickedMouseListener();
    	sfAction = new SearchFunctionAction();
    	anvAction = new AddNewVolumeAction();
    	delvAction = new DeleteVolumeAction();
    	renvAction = new RenameVolumeAction();
    	
        initComponents();
        
        setTitle(Cathy.NAME + " " + Cathy.VERSION); //$NON-NLS-1$
        setIconImage(programIcon.getImage());
    	addWindowListener(cpAction);
    	MessageDialog.setOwner(this);
    	
        Cathy.lastSelectedVolume = new LastSelectedVolume();
        Cathy.lastSelectedVolume.addObserver(expVolumeName);
        Cathy.lastSelectedVolume.addObserver(expTree);
        Cathy.lastSelectedVolume.addObserver(expTab);
        Cathy.lastSelectedVolume.addObserver(searchTable); //se arg==null searchModel=null
        Cathy.lastSelectedVolume.addObserver(menuVolume);
        //Cathy.lastSelectedVolume.addObserver(volumeTable);
        
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuItemExit.addActionListener(new CloseProgramAction());
        menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        
        menuHelp.setMnemonic(KeyEvent.VK_H);
        menuItemAbout.addActionListener(new AboutClicked());
        menuItemAbout.setAccelerator(KeyStroke.getKeyStroke("F1")); //$NON-NLS-1$
        
        popupMenu = new CatalogPopupMenu();
        
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setToolTipTextAt(1, expTab.toolTipText);
        
		DefaultTreeCellRenderer renderer = 	new DefaultTreeCellRenderer();
		renderer.setLeafIcon(renderer.getClosedIcon());
		expTree.setCellRenderer(renderer);
		expTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		expTree.addTreeSelectionListener(new TreeNodeSelectionListener());
		expTree.setExpandsSelectedPaths(true);
		
		expTable.setGridColor(Color.LIGHT_GRAY);
		//expTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//expTable.setAutoCreateRowSorter(true);
		expTable.addMouseListener(rsMouseListener);
		
		expPath.setToolTipText(Messages.getString("GUI.expPathToolTip")); //$NON-NLS-1$
		
		volumeTable.setGridColor(Color.LIGHT_GRAY);
		//volumeTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        //volumeTable.setAutoCreateRowSorter(true);
        volumeTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        volumeTable.addMouseListener(popupMenu);
        
        //rootPath.addActionListener(anvAction);
        //getRootPane().setDefaultButton(volumeAddButton);
        
        browseButton.addActionListener(new BrowseFolders());
        volumeAddButton.addActionListener(anvAction);
        volumeAddButton.registerKeyboardAction(anvAction, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW); //$NON-NLS-1$
        volumeAddButton.setFocusable(true);
        
        //ignoreLabel.setEnabled(false);
        //ignoreTextField.setEnabled(false);
        //ignoreTextField.setToolTipText(Messages.getString("GUI.notImplemented")); //$NON-NLS-1$
        
        searchTable.setGridColor(Color.LIGHT_GRAY);
        //searchTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        //searchTable.setAutoCreateRowSorter(true);
        searchTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        searchButton.addActionListener(sfAction);
        
        searchField.addActionListener(sfAction);
        searchField.setToolTipText(Messages.getString("GUI.searchFieldToolTip")); //$NON-NLS-1$
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        tabbedPane = new JTabbedPaneListener();
        searchTab = new javax.swing.JPanel();
        searchLabel = new javax.swing.JLabel();
        searchField = new JTextField();
        searchButton = new javax.swing.JButton();
        searchTablePane = new javax.swing.JScrollPane();
        searchTable = new SearchJTable();
        expTab = new ExploreJPanelObserver();
        expVolumeNameLabel = new javax.swing.JLabel();
        expVolumeName = new JTextFieldObserver();
        expPathLabel = new javax.swing.JLabel();
        expPath = new javax.swing.JTextField();
        expSplitPane = new javax.swing.JSplitPane();
        expTreeScrollPane = new javax.swing.JScrollPane();
        expTree = new JTreeObserver(null);
        expScrollPane = new javax.swing.JScrollPane();
        expTable = new ExploreJTable();
        catalogTab = new javax.swing.JPanel();
        rootLabel = new javax.swing.JLabel();
        rootPath = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        volumeAddButton = new javax.swing.JButton();
        volumeNameLabel = new javax.swing.JLabel();
        volumeNameTextField = new javax.swing.JTextField();
        ignoreLabel = new javax.swing.JLabel();
        ignoreTextField = new javax.swing.JTextField();
        volumeScrollPane = new javax.swing.JScrollPane();
        volumeTable = new CatalogJTable();
        menu = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuVolume = new VolumeMenuObserver("Volume"); //$NON-NLS-1$
        menuItemExit = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setName("mainFrame"); //$NON-NLS-1$

        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        searchLabel.setText(Messages.getString("GUI.searchLabelText")); //$NON-NLS-1$

        searchField.setEditable(true);

        searchButton.setText(Messages.getString("search")); //$NON-NLS-1$

        searchTablePane.setViewportView(searchTable);

        javax.swing.GroupLayout searchTabLayout = new javax.swing.GroupLayout(searchTab);
        searchTab.setLayout(searchTabLayout);
        searchTabLayout.setHorizontalGroup(
            searchTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, searchTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(searchTablePane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE)
                    .addGroup(searchTabLayout.createSequentialGroup()
                        .addComponent(searchLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchField, 0, 670, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchButton)))
                .addContainerGap())
        );
        searchTabLayout.setVerticalGroup(
            searchTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(searchButton)
                    .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(searchTablePane, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(Messages.getString("search"), searchTab); //$NON-NLS-1$

        //XXX expTreeScrollPane.setViewportView(expTree);
        //XXX expScrollPane.setViewportView(expTable);


        expVolumeNameLabel.setText(Messages.getString("GUI.expVolumeNameText")); //$NON-NLS-1$

        expVolumeName.setEditable(false);

        expPathLabel.setText(Messages.getString("GUI.expPathText")); //$NON-NLS-1$

        expPath.setEditable(false);

        expSplitPane.setDividerLocation(200);

        expTreeScrollPane.setViewportView(expTree);

        expSplitPane.setLeftComponent(expTreeScrollPane);

        
        expScrollPane.setViewportView(expTable);

        expSplitPane.setRightComponent(expScrollPane);

        javax.swing.GroupLayout expTabLayout = new javax.swing.GroupLayout(expTab);
        expTab.setLayout(expTabLayout);
        expTabLayout.setHorizontalGroup(
            expTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(expTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(expTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(expSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE)
                    .addGroup(expTabLayout.createSequentialGroup()
                        .addComponent(expVolumeNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(expVolumeName, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(expPathLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(expPath, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)))
                .addContainerGap())
        );
        expTabLayout.setVerticalGroup(
            expTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, expTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(expTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expVolumeNameLabel)
                    .addComponent(expVolumeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(expPathLabel)
                    .addComponent(expPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(expSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(Messages.getString("GUI.expTabName"), expTab); //$NON-NLS-1$

        rootLabel.setText(Messages.getString("GUI.rootLabelText")); //$NON-NLS-1$

        rootPath.setToolTipText(Messages.getString("GUI.rootPathToolTip")); //$NON-NLS-1$

        browseButton.setText(Messages.getString("GUI.browseButtonText")); //$NON-NLS-1$

        volumeAddButton.setText(Messages.getString("GUI.volumeAddButtonText")); //$NON-NLS-1$
        volumeAddButton.setToolTipText(Messages.getString("GUI.volumeAddButtonToolTip")); //$NON-NLS-1$

        volumeNameLabel.setText(Messages.getString("GUI.volumeNameText")); //$NON-NLS-1$

        volumeNameTextField.setToolTipText(Messages.getString("GUI.volumeNameToolTip")); //$NON-NLS-1$

        ignoreLabel.setText(Messages.getString("GUI.ignoreLabelText")); //$NON-NLS-1$

        //ignoreTextField.setText("*.tmp,Thumbs.db,*.temp,*~"); //$NON-NLS-1$
        ignoreTextField.setText(Cathy.DB.getIgnorePattern());
        ignoreTextField.setToolTipText(Messages.getString("GUI.ignoreFieldToolTip")); //$NON-NLS-1$

       
        //volumeTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        volumeScrollPane.setViewportView(volumeTable);

        javax.swing.GroupLayout catalogTabLayout = new javax.swing.GroupLayout(catalogTab);
        catalogTab.setLayout(catalogTabLayout);
        catalogTabLayout.setHorizontalGroup(
            catalogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, catalogTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(catalogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(volumeScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE)
                    .addGroup(catalogTabLayout.createSequentialGroup()
                        .addGroup(catalogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rootLabel)
                            .addComponent(volumeNameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(catalogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(catalogTabLayout.createSequentialGroup()
                                .addComponent(rootPath, javax.swing.GroupLayout.DEFAULT_SIZE, 624, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(volumeAddButton))
                            .addGroup(catalogTabLayout.createSequentialGroup()
                                .addComponent(volumeNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ignoreLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ignoreTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        catalogTabLayout.setVerticalGroup(
            catalogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(catalogTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(catalogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rootLabel)
                    .addComponent(volumeAddButton)
                    .addComponent(browseButton)
                    .addComponent(rootPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(catalogTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(volumeNameLabel)
                    .addComponent(ignoreLabel)
                    .addComponent(ignoreTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(volumeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(volumeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabbedPane.addTab(Messages.getString("GUI.catalogTabName"), catalogTab); //$NON-NLS-1$

        menuFile.setText("File"); //$NON-NLS-1$
        menuItemExit.setText(Messages.getString("GUI.menuExitName")); //$NON-NLS-1$
        menuFile.add(menuItemExit);

        menu.add(menuFile);

        menu.add(menuVolume);

        menuHelp.setText("?"); //$NON-NLS-1$
        menuItemAbout.setText(Messages.getString("GUI.menuAboutName")); //$NON-NLS-1$
        menuHelp.add(menuItemAbout);

        menu.add(menuHelp);

        setJMenuBar(menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 835, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton volumeAddButton;
    private javax.swing.JButton browseButton;
    private javax.swing.JPanel catalogTab;
    protected javax.swing.JTextField expPath;
    private javax.swing.JLabel expPathLabel;
    private javax.swing.JScrollPane expScrollPane;
    private javax.swing.JSplitPane expSplitPane;
    private ExploreJPanelObserver expTab;
    protected ExploreJTable expTable;
    protected JTreeObserver expTree;
    private javax.swing.JScrollPane expTreeScrollPane;
    private JTextFieldObserver expVolumeName;
    private javax.swing.JLabel expVolumeNameLabel;
    private javax.swing.JLabel ignoreLabel;
    private javax.swing.JTextField ignoreTextField;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenu menuFile;
    protected VolumeMenuObserver menuVolume;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JLabel volumeNameLabel;
    private javax.swing.JTextField volumeNameTextField;
    private javax.swing.JLabel rootLabel;
    private javax.swing.JTextField rootPath;
    private javax.swing.JButton searchButton;
    protected javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JPanel searchTab;
    protected SearchJTable searchTable;
    private javax.swing.JScrollPane searchTablePane;
    protected JTabbedPaneListener tabbedPane;
    private javax.swing.JScrollPane volumeScrollPane;
    protected CatalogJTable volumeTable;
    // End of variables declaration//GEN-END:variables
    
    
    
    //-----------------------------------------------------------------------------------
    
    
	/**
	 * This class is used to show the name of the last-selected Volume in the "Expore" tab.
	 * This is identical to <code>JTextField</code> except for <code>update()</code> method
	 * needed to be an <code>Observer</code>
	 */
	private class JTextFieldObserver extends JTextField implements Observer
	{
		/**
		 * Update the text of this <code>JTextField</code>, the new text is the name of
		 * the last-selected volume
		 * @param observableObj the observable object that executed <code>notifyObservers()</code>
		 * @param arg an attribute passed to the <code>notifyObservers()</code> method:
		 *            in this case the name of the new selected volume
		 */
		public void update(Observable observableObj, Object arg)
		{
			setText((String)arg);
		}
		
	}
	
	/**
	 * TODO da scrivere
	 */
	private class JTabbedPaneListener extends JTabbedPane implements ChangeListener
	{
		public JTabbedPaneListener()
		{
			super();
			addChangeListener(this);
		}
		
		public void stateChanged(ChangeEvent e)
		{
			if(getSelectedComponent() == catalogTab)
			{
				menuVolume.setEnabled(true);
				if(volumeTable.getSelectedRow() != -1)
				{
					menuVolume.menuItemDelete.setEnabled(true);
					menuVolume.menuItemRename.setEnabled(true);
					/*
					 * TODO da aggiungere quando implementato il Refresh
					 * menuVolume.menuItemRefresh.setEnabled(true);
					 */
				}
				else
				{
					menuVolume.menuItemDelete.setEnabled(false);
					menuVolume.menuItemRename.setEnabled(false);
					/*
					 * TODO da aggiungere quando implementato il Refresh
					 * menuVolume.menuItemRefresh.setEnabled(false);
					 */
				}	
			}
			else
				menuVolume.setEnabled(false);
		}
	}
	
	
	/**
	 * This class is used to enable the "Explore" tab when the user selects a volume.
	 * Is identical to <code>JTextPanel</code> except for <code>update()</code> method
	 * needed to be an <code>Observer</code>
	 */
	private class ExploreJPanelObserver extends JPanel implements Observer
	{
        private String toolTipText = Messages.getString("GUI.exploreTabDisabledToolTip"); //$NON-NLS-1$
		
        /**
         * Default constructor
         */
        public ExploreJPanelObserver()
        {
        	super();
        }
        
		/**
		 * Enables the "Explore" tab when the user selects a volume in "Catalog" tab
		 * or a search-result in the "Search" tab.
		 * This method also removes the ToolTipText of the "Explore" tab that explains
		 * why the tab in deisabled when the program starts
		 * @param observableObj the observable object that executed <code>notifyObservers()</code>
		 * @param arg an attribute passed to the <code>notifyObservers()</code> method:
		 *            in this case the name of the new selected volume
		 */
		public void update(Observable observableObj, Object arg)
		{
			if(arg == null)
			{
				tabbedPane.setEnabledAt(1, false);
				tabbedPane.setToolTipTextAt(1, toolTipText);
				
				if(tabbedPane.getSelectedComponent() == expTab)
					tabbedPane.setSelectedComponent(catalogTab);
			}
			else
			{
				tabbedPane.setEnabledAt(1, true);
				tabbedPane.setToolTipTextAt(1, null);
			}
		}
	}

    
    /**
     * This class implements the methods that react to a double click
     * made over <code>expTable</code> to navigat in the tree
     */
	private class ExpTableDoubleClickedMouseListener implements MouseListener
    {
		/**
         * This method checks if the double click has made with left button and then
         * navigate in the correspondi file-system-tree
         * @param event the event that occurred
         */
		public void mouseClicked(MouseEvent event)
        {
            if(SwingUtilities.isLeftMouseButton(event) && event.getSource().equals(expTable) && event.getClickCount() == 2)
            {
				Icon icoTmp = (Icon)expTable.getModel().getValueAt(expTable.getSelectedRow(), 0);
				if(icoTmp==JTreeObserver.DIR_ICON)
				{
					expTree.expandPath(expTree.getSelectionPath());
					TreePath tpTmp = expTree.getDoubleClickedDirectoryPath(((ExploreJTable.ExploreTableModel)expTable.getModel()).getId(expTable.getSelectedRow()), expTable.getSelectedRow(), Position.Bias.Forward);
					expTree.expandPath(tpTmp);
					expTree.setSelectionPath(tpTmp);
				}
				else if(icoTmp==JTreeObserver.UP_ICON)
				{
					//int parentId = ((TableTreeEntryNode)expTree.getLastSelectedPathComponent()).getParentId();
					int parentId = ((TableTreeEntryNode)((TableTreeEntryNode)expTree.getLastSelectedPathComponent()).getParent()).getId();
					TreePath tpTmp = expTree.getDoubleClickedDirectoryPath(parentId, 0, Position.Bias.Backward);
					expTree.setSelectionPath(tpTmp);
				}
            }
        }
		

        public void mousePressed(MouseEvent ignore) { }
        public void mouseReleased(MouseEvent ignore) { }
        public void mouseEntered(MouseEvent ignore) { }
        public void mouseExited(MouseEvent ignore) { }
        
    }
	
	
	/**
     * This class is used as the <code>JPopupMenu</code> in the <code>volumeTable</code>,
     * every menuItem is contained inside
	 */
	protected class CatalogPopupMenu extends JPopupMenu implements MouseListener
	{		
		private JMenuItem menuItemRename;
		private JMenuItem menuItemDelete;
		private JMenuItem menuItemRefresh;
		
		
		/**
		 * Creates a new <code>CatalogPopupMenu</code> setting every <code>JMenuItem</code>
		 * and their relatives actions
		 */
		public CatalogPopupMenu()
		{
			super();
			menuItemRename = new JMenuItem(Messages.getString("GUI.menuRenameName")); //$NON-NLS-1$
			menuItemRename.addActionListener(renvAction);
			add(menuItemRename);
			
			menuItemDelete = new JMenuItem(Messages.getString("GUI.menuDeleteName"),deleteIcon); //$NON-NLS-1$
			menuItemDelete.addActionListener(delvAction);
			add(menuItemDelete);
			
			menuItemRefresh = new JMenuItem(Messages.getString("refresh"),refreshIcon); //$NON-NLS-1$
			/*
			 * TODO da aggiungere quando implementato il Refresh
			 * menuItemRefresh.addActionListener(new RefreshVolumeAction());
			 */
			add(menuItemRefresh);
			
			// TODO va tolto quando implementato il Refresh
			menuItemRefresh.setEnabled(false);
			menuItemRefresh.setToolTipText(Messages.getString("GUI.notImplemented")); //$NON-NLS-1$
		}
		
		/**
		 * When the user click the right button on the mouse, this method changes the
		 * selection in the <code>volumeTable</code> then shows the popup menu
		 * @param event
		 */
		public void mousePressed(MouseEvent event)
		{
			if(event.isPopupTrigger())
			{
				volumeTable.changeSelection(volumeTable.rowAtPoint(event.getPoint()), 0, false, false);
				show(event.getComponent(), event.getX(), event.getY());
				volumeTable.mouseClicked(event);
			}
		}
		
		public void mouseClicked(MouseEvent ignore) { }
	    public void mouseReleased(MouseEvent ignore) { }
	    public void mouseEntered(MouseEvent ignore) { }
	    public void mouseExited(MouseEvent ignore) { }
	}
	
	
	/**
	 * TODO da scrivere
	 */
	protected class VolumeMenuObserver extends JMenu implements Observer
	{
		private JMenuItem menuItemAdd;
		private JMenuItem menuItemRename;
		private JMenuItem menuItemDelete;
		private JMenuItem menuItemRefresh;
		
		
		public VolumeMenuObserver(String name)
		{
			super(name);
			
			menuItemAdd = new JMenuItem(Messages.getString("GUI.volumeAddButtonText"),addIcon); //$NON-NLS-1$
			menuItemAdd.addActionListener(anvAction);
			add(menuItemAdd);
			
			menuItemRename = new JMenuItem(Messages.getString("GUI.menuRenameName")); //$NON-NLS-1$
			menuItemRename.addActionListener(renvAction);
			add(menuItemRename);
			
			menuItemDelete = new JMenuItem(Messages.getString("GUI.menuDeleteName"),deleteIcon); //$NON-NLS-1$
			menuItemDelete.addActionListener(delvAction);
			add(menuItemDelete);
			
			menuItemRefresh = new JMenuItem(Messages.getString("refresh"),refreshIcon); //$NON-NLS-1$
			/*
			 * TODO da aggiungere quando implementato il Refresh
			 * menuItemRefresh.addActionListener(new RefreshVolumeAction());
			 */
			add(menuItemRefresh);
			
			// TODO togliere quando implementato il Refresh
			menuItemRefresh.setToolTipText(Messages.getString("GUI.notImplemented")); //$NON-NLS-1$
			
			menuItemDelete.setEnabled(false);
			menuItemRename.setEnabled(false);
			menuItemRefresh.setEnabled(false);
			
			setEnabled(false);
		}



		public void update(Observable o, Object arg)
		{
			if(arg != null)
			{
				menuItemDelete.setEnabled(true);
				menuItemRename.setEnabled(true);
				/*
				 * TODO da aggiungere quando implementato il Refresh
				 * menuItemRefresh.setEnabled(true);
				 */
			}
			else
			{
				menuItemDelete.setEnabled(false);
				menuItemRename.setEnabled(false);
				/*
				 * TODO da aggiungere quando implementato il Refresh
				 * menuItemRefresh.setEnabled(false);
				 */
			}
		}
		
	}
	
	
	/**
	 * This class contains the method needed to rename a volume already cataloged
	 */
	private class RenameVolumeAction implements ActionListener
	{
		private String selectedVolumeName;
		private String newVolumeName;
		
		/**
		 * When the user click over a "Rename volume" item this method shows a 
		 * confirmation dialog to ask the user to insert the new name
		 * @param ignore
		 */
		public void actionPerformed(ActionEvent ignore)
		{
			String volume = volumeTable.getModel().getValueAt(volumeTable.getSelectedRow(), 0).toString();
			new ConfirmDialog(volume);
		}
		
		/**
		 * Subclass that represent a confirmation dialog before deleting
		 */
		private class ConfirmDialog extends JDialog implements ActionListener
		{
			private JPanel namePanel;
			private JLabel msgLabel;
			private JTextField newNameText;
			private JPanel buttonPanel;
			private JButton renameButton;
			private JButton cancelButton;
			
			/**
			 * Creates a new <code>ConfirmDialog</code> setting the name of the volume
			 * to be deleted
			 * @param volumeName the name of the volume to be deleted
			 */
			public ConfirmDialog(String volumeName)
			{
				super(GUI.this, Messages.getString("GUI.renameConfirmDialog.Title")); //$NON-NLS-1$
				setModal(true);
				setResizable(false);
				
				selectedVolumeName = volumeName.replaceAll("<(html|/html|b|/b)>", ""); //$NON-NLS-1$ //$NON-NLS-2$
				
				namePanel = new JPanel();
				namePanel.setLayout(new FlowLayout());
				{
					msgLabel = new JLabel(Messages.getString("GUI.renameConfirmDialog.label_new_name")); //$NON-NLS-1$
					newNameText = new JTextField(selectedVolumeName,15);
				}
				namePanel.add(msgLabel);
				namePanel.add(newNameText);
				
				buttonPanel = new JPanel();				
				buttonPanel.setLayout(new FlowLayout());
				buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
				{
					renameButton = new JButton(Messages.getString("rename")); //$NON-NLS-1$
					renameButton.addActionListener(this);
					renameButton.registerKeyboardAction(this, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW); //$NON-NLS-1$
					cancelButton = new JButton(Messages.getString("cancel")); //$NON-NLS-1$
					cancelButton.addActionListener(this);
					cancelButton.registerKeyboardAction(this, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_FOCUSED); //$NON-NLS-1$
				}
				buttonPanel.add(renameButton);
				buttonPanel.add(cancelButton);
				
				add(namePanel,BorderLayout.NORTH);
				add(buttonPanel, BorderLayout.SOUTH);
				
				pack();
				setLocationRelativeTo(getOwner());
				//renameButton.requestFocusInWindow(); 
				setVisible(true);
			}

			
			/**
			 * If the user clicks on "Delete" button the volume will be deleted
			 * otherwise nothing is done
			 * @param event needed to get the source of the click
			 */
			public void actionPerformed(ActionEvent event)
			{
				if(event.getSource() == renameButton)
				{
					newVolumeName = newNameText.getText();
					try
					{
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						Cathy.DB.renameVolume(selectedVolumeName, newVolumeName);
						MessageDialog.show(Messages.getString("GUI.renameConfirmDialog.MSG.successTitle"), //$NON-NLS-1$
								Messages.getString("GUI.renameConfirmDialog.MSG.successMsg_pre") + selectedVolumeName + //$NON-NLS-1$
								Messages.getString("GUI.renameConfirmDialog.MSG.successMsg_mid") + newVolumeName + //$NON-NLS-1$
								Messages.getString("GUI.renameConfirmDialog.MSG.successMsg_post"), successIcon); //$NON-NLS-1$
						
						setCursor(null);
						
						Cathy.lastSelectedVolume.setVolumeName(null);
					}
					catch(SQLException e)
					{
						e.printStackTrace();
						
						int errorCode = e.getErrorCode();
						if(Cathy.DB.rollbackTransaction())
						{
							if(errorCode == -104)
								MessageDialog.show(Messages.getString("GUI.renameConfirmDialog.MSG.errorTitle"), //$NON-NLS-1$
										Messages.getString("GUI.renameConfirmDialog.MSG.errorNameAlreadyExists"), errorIcon); //$NON-NLS-1$
							else
								MessageDialog.show(Messages.getString("GUI.renameConfirmDialog.MSG.errorTitle"), //$NON-NLS-1$
										Messages.getString("GUI.renameConfirmDialog.MSG.errorRolledBack"), errorIcon); //$NON-NLS-1$
						}
						else
							MessageDialog.show(Messages.getString("GUI.renameConfirmDialog.MSG.errorTitle"), //$NON-NLS-1$
									Messages.getString("GUI.renameConfirmDialog.MSG.errorNotRolledBack"), errorIcon); //$NON-NLS-1$
					}
					volumeTable.refresh();
				}
				setVisible(false);
			}
		}
	}
	
	
	/**
	 * This class contains the method needed to delete a volume already cataloged
	 */
	private class DeleteVolumeAction implements ActionListener
	{
		private String selectedVolumeName;
		
		/**
		 * When the user click over a "Delete volume" item this method shows a 
		 * confirmation dialog to ask the user if he/she is sure to delete the volume
		 * @param ignore
		 */
		public void actionPerformed(ActionEvent ignore)
		{
			String volume = volumeTable.getModel().getValueAt(volumeTable.getSelectedRow(), 0).toString();
			new ConfirmDialog(volume);
			//new ConfirmDialog(Cathy.lastSelectedVolume.getVolumeName());
		}
		
		/**
		 * Subclass that represent a confirmation dialog before deleting
		 */
		private class ConfirmDialog extends JDialog implements ActionListener
		{
			private JLabel msgLabel;
			private JPanel buttonPanel;
			private JButton deleteButton;
			private JButton cancelButton;
			
			/**
			 * Creates a new <code>ConfirmDialog</code> setting the name of the volume
			 * to be deleted
			 * @param volumeName the name of the volume to be deleted
			 */
			public ConfirmDialog(String volumeName)
			{
				super(GUI.this, Messages.getString("GUI.deleteConfirmDialog.Title")); //$NON-NLS-1$
				setModal(true);
				setResizable(false);
				
				selectedVolumeName = volumeName.replaceAll("<(html|/html|b|/b)>", ""); //$NON-NLS-1$ //$NON-NLS-2$
				
				msgLabel = new JLabel(Messages.getString("GUI.deleteConfirmDialog.MsgLabel_pre")+selectedVolumeName+Messages.getString("GUI.deleteConfirmDialog.MsgLabel_post")); //$NON-NLS-1$ //$NON-NLS-2$
				msgLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
				
				buttonPanel = new JPanel();				
				buttonPanel.setLayout(new FlowLayout());
				buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
				{
					deleteButton = new JButton(Messages.getString("delete")); //$NON-NLS-1$
					deleteButton.addActionListener(this);
					deleteButton.registerKeyboardAction(this, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_FOCUSED); //$NON-NLS-1$
					cancelButton = new JButton(Messages.getString("cancel")); //$NON-NLS-1$
					cancelButton.addActionListener(this);
					cancelButton.registerKeyboardAction(this, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW); //$NON-NLS-1$
				}
				buttonPanel.add(deleteButton);
				buttonPanel.add(cancelButton);
				
				add(msgLabel,BorderLayout.NORTH);
				add(buttonPanel, BorderLayout.SOUTH);
				
				pack();
				setLocationRelativeTo(getOwner());
				cancelButton.requestFocusInWindow(); 
				setVisible(true);
			}

			
			/**
			 * If the user clicks on "Delete" button the volume will be deleted
			 * otherwise nothing is done
			 * @param event needed to get the source of the click
			 */
			public void actionPerformed(ActionEvent event)
			{
				if(event.getSource() == deleteButton)
				{
					try
					{
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						Cathy.DB.deleteVolume(selectedVolumeName);
						MessageDialog.show(Messages.getString("GUI.deleteConfirmDialog.MSG.successTitle"), Messages.getString("GUI.deleteConfirmDialog.MSG.successMsg_pre")+selectedVolumeName+Messages.getString("GUI.deleteConfirmDialog.MSG.successMsg_post"), successIcon); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						setCursor(null);
						Cathy.lastSelectedVolume.setVolumeName(null);
					}
					catch(SQLException e)
					{
						e.printStackTrace();
						if(Cathy.DB.rollbackTransaction())
							MessageDialog.show(Messages.getString("GUI.deleteConfirmDialog.MSG.errorTitle"), Messages.getString("GUI.deleteConfirmDialog.MSG.errorRolledBackMsg"), errorIcon); //$NON-NLS-1$ //$NON-NLS-2$
						else
							MessageDialog.show(Messages.getString("GUI.deleteConfirmDialog.MSG.errorTitle"), Messages.getString("GUI.deleteConfirmDialog.MSG.errorNoRolledBackMsg"), errorIcon); //$NON-NLS-1$ //$NON-NLS-2$
					}
					volumeTable.refresh();
				}
				setVisible(false);
			}
		}
	}
	
	
	/* TODO da implementare il Refresh
	private class RefreshVolumeAction implements ActionListener
	{
		private String selectedVolumeName;
		

		public void actionPerformed(ActionEvent e)
		{
			// Auto-generated method stub
			
		}
		
		/**
		 * Subclass that represent a confirmation dialog before deleting
		 *
		private class ConfirmDialog extends JDialog implements ActionListener
		{
			private JLabel msgLabel;
			private JPanel buttonPanel;
			private JButton refreshButton;
			private JButton cancelButton;
			
			/**
			 * Creates a new <code>ConfirmDialog</code> setting the name of the volume
			 * to be refreshed
			 * @param volumeName the name of the volume to be deleted
			 *
			public ConfirmDialog(String volumeName)
			{
				super(GUI.this, "Confirm refresh");
				setModal(true);
				setResizable(false);
				
				selectedVolumeName = volumeName.replaceAll("<(html|/html|b|/b)>", ""); //$NON-NLS-1$ //$NON-NLS-2$
				
				msgLabel = new JLabel("<html>Are you sure you want to refresh <i>\""+selectedVolumeName+"\"</i> volume?</html>");
				msgLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
				
				buttonPanel = new JPanel();				
				buttonPanel.setLayout(new FlowLayout());
				buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
				{
					refreshButton = new JButton(Messages.getString("refresh")); //$NON-NLS-1$
					refreshButton.addActionListener(this);
					refreshButton.registerKeyboardAction(this, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_FOCUSED); //$NON-NLS-1$
					cancelButton = new JButton(Messages.getString("cancel")); //$NON-NLS-1$
					cancelButton.addActionListener(this);
					cancelButton.registerKeyboardAction(this, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW); //$NON-NLS-1$
				}
				buttonPanel.add(refreshButton);
				buttonPanel.add(cancelButton);
				
				add(msgLabel,BorderLayout.NORTH);
				add(buttonPanel, BorderLayout.SOUTH);
				
				pack();
				setLocationRelativeTo(getOwner());
				cancelButton.requestFocusInWindow(); 
				setVisible(true);
			}

			
			/**
			 * If the user clicks on "Refresh" button the volume will be refreshed
			 * otherwise nothing is done
			 * @param event needed to get the source of the click
			 *
			public void actionPerformed(ActionEvent event)
			{
				if(event.getSource() == refreshButton)
				{
					try
					{
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						/*TODO funzione per aggiornare* Cathy.DB.deleteVolume(selectedVolumeName);
						MessageDialog.show("Volume successfully refreshed", "The volume <i>\""+selectedVolumeName+"\"</i> has been refreshed.", successIcon);
						setCursor(null);
						Cathy.lastSelectedVolume.setVolumeName(null);
						Cathy.lastSelectedVolume.setVolumeName(null);
					}
					catch(SQLException e)
					{
						e.printStackTrace();
						if(Cathy.DB.rollbackTransaction())
							MessageDialog.show("Refresh completed abnormally", "The volume cannot be refreshed!<br>The transaction ended without COMMIT!", errorIcon);
						else
							MessageDialog.show("Refresh completed abnormally", "The volume cannot be refreshed!<br>Some errors occured during database transaction.", errorIcon);
					}
					volumeTable.refresh();
				}
				setVisible(false);
			}
		}
		
	}*/
	
	
	/**
	 * This class contains the method that react to Node-Selection in a Tree
	 */
	private class TreeNodeSelectionListener implements TreeSelectionListener
	{
		/**
		 * When the value of a <code>JTree</code> changes this method set the new data to
		 * <code>expTable</code> and update the <code>expPath</code> label with the
		 * new selected path
    	 * @param event not used
		 */
		public void valueChanged(TreeSelectionEvent event)
		{
			TableTreeEntryNode selectedDir = (TableTreeEntryNode)expTree.getLastSelectedPathComponent();
			if(selectedDir!=null)
			{
				expTable.setData(selectedDir.getId());
				String tmpStr = selectedDir.getPathStr().replace(SEPARATOR, File.separatorChar) + selectedDir.getName();
				
				if(selectedDir.getParent()==null)
					expTabStartPath = ""; //$NON-NLS-1$
				else
					expTabStartPath = tmpStr;
				
				expPath.setText(tmpStr);
			}
		}
	}
    
    /**
     * This class implements the actions that must be done before closing the program.
     * Is used if the user clicks the "X", if the user clicks on File-->Exit
     */
    private class CloseProgramAction implements ActionListener, WindowListener
    {
        /**
         * Executes the disconnection from the database and closes the program
    	 * @param event not used
         */
    	public void actionPerformed(ActionEvent event)
        {
    		Cathy.DB.saveIgnorePattern(ignoreTextField.getText().split(",")); //$NON-NLS-1$
    		Cathy.DB.disconnect();
    		System.exit(0);
        }
    	
		/**
		 * Execute the disconnection from the database
    	 * @param event not used
		 */
    	public void windowClosing(WindowEvent event)
		{
    		Cathy.DB.saveIgnorePattern(ignoreTextField.getText().split(",")); //$NON-NLS-1$
    		Cathy.DB.disconnect();
		}

		public void windowActivated(WindowEvent ignore) { }
		public void windowDeactivated(WindowEvent ignore) { }
		public void windowDeiconified(WindowEvent ignore) { }
		public void windowIconified(WindowEvent ignore) { }
		public void windowOpened(WindowEvent ignore) { }
		public void windowClosed(WindowEvent ignore) { }
    }
    
    
    /**
     * This class implements the mothod that react to a search click
     */
    private class SearchFunctionAction implements ActionListener
    {
		/**
		 * When the user click the <code>searchButton</code> or press
		 * Enter in <code>searchField</code>, this method execute the query
		 * with the search pattern and report it in the <code>searchTable</code>.
		 * If the <code>searchField</code> is empty nothing is done
    	 * @param event not used
		 */
    	public void actionPerformed(ActionEvent event)
		{
    		if(!searchField.getText().isEmpty())
    			searchTable.setData(searchField.getText());
    		else
    			MessageDialog.show(Messages.getString("GUI.SearchFunction.MSG.searchFieldEmptyTitle"), Messages.getString("GUI.SearchFunction.MSG.searchFieldEmptyMsg"), warningIcon); //$NON-NLS-1$ //$NON-NLS-2$
		}
    }
    
    
    /**
     * This class implements the mothod that react to a click done over ?-->About menu
     */
    private class AboutClicked implements ActionListener
    {
    	/**
    	 * Creates a new <code>CopyInfo</code> dialog window if none is already created
    	 * and then makes it visible
    	 * @param event not used
    	 */
		public void actionPerformed(ActionEvent event)
		{
			copyInfoDialog = CopyInfo.getInstance(GUI.this, Cathy.NAME + " " + Cathy.VERSION, Cathy.YEAR, Cathy.ICON_FILENAME); //$NON-NLS-1$
			copyInfoDialog.setVisible(true);
		}
    	
    }
    
    /**
     * This class implements the mothod that react to a click done
     * over "Add" <code>JButton</code>
     */
    private class AddNewVolumeAction implements ActionListener
    {
    	private int dirId;
    	private Pattern pattern;
    	
    	/**
    	 * If the source of this event is <code>volumeAddButton</code> this method 
    	 * <ol>
    	 * <li>checks if both <code>rootPath</code> and <code>volumeNameTextField</code> are not empty</li>
    	 * <li>checks if the new name already exists</li>
    	 * <li>checks if the starting path is a directory</li>
    	 * <li>creates the new volume in the database</li>
    	 * <li>recurively reads the file system tree and inserts directory and file in the database</li>
    	 * <li>refresh the <code>volumeTable</code></li>
    	 * </ol>
    	 * @param event used to check weather the source of the click is <code>volumeAddButton</code>
    	 */
		public void actionPerformed(ActionEvent event)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if(rootPath.getText().isEmpty() || volumeNameTextField.getText().isEmpty())
			{
				MessageDialog.show(Messages.getString("GUI.AddNewVolume.MSG.fieldsEmptyTitle"), Messages.getString("GUI.AddNewVolume.MSG.fieldsEmptyMsg"), warningIcon); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if(Cathy.DB.volumeExists(volumeNameTextField.getText()))
			{
				MessageDialog.show(Messages.getString("GUI.AddNewVolume.MSG.volumeExistsTitle"), Messages.getString("GUI.AddNewVolume.MSG.volumeExistsMsg"), errorIcon); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else
			{
				File startDir = new File(rootPath.getText());
				if(startDir.isDirectory())
				{
					//setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					long currentTime = System.currentTimeMillis();
					dirId = 0;
					try
					{
						File[] content = startDir.listFiles();
						if(content.length != 0)
						{
				        	String prova = ignoreTextField.getText();
				        	String regex = "(" + prova.replaceAll("[.]", "[.]").replace(",","$)|(").replace("*", ".*") + "$)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
				        	pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
							
							Cathy.DB.startTransaction();
							{
								Cathy.DB.insertVolume(volumeNameTextField.getText(), 0, 0, 0, currentTime, rootPath.getText());
								long[] param = catalog(content, dirId, Character.toString(SEPARATOR));
								Cathy.DB.updateVolumeSizes(volumeNameTextField.getText(), param[0], (int)param[1], (int)param[2]);
								setCursor(null);
							}
							Cathy.DB.commitTransaction();
							MessageDialog.show(Messages.getString("GUI.AddNewVolume.MSG.successTitle"), Messages.getString("GUI.AddNewVolume.MSG.successMsg_pre")+volumeNameTextField.getText()+Messages.getString("GUI.AddNewVolume.MSG.successMsg_post"), successIcon); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						
							volumeNameTextField.setText(null);
							rootPath.setText(null);
						}
						else
							MessageDialog.show(Messages.getString("GUI.AddNewVolume.MSG.rootEmptyTitle"), Messages.getString("GUI.AddNewVolume.MSG.rootEmptyMsg"), warningIcon); //$NON-NLS-1$ //$NON-NLS-2$
					}
					catch(SQLException e)
					{
						e.printStackTrace();
						//System.out.println("messaggio: "+e.getMessage()+"; errore: "+e.getErrorCode()+"; stato: "+e.getSQLState());
						if(Cathy.DB.rollbackTransaction())
							MessageDialog.show(Messages.getString("GUI.AddNewVolume.MSG.errorTitle"), Messages.getString("GUI.AddNewVolume.MSG.errorRolledBackMsg"), errorIcon); //$NON-NLS-1$ //$NON-NLS-2$
						else
							MessageDialog.show(Messages.getString("GUI.AddNewVolume.MSG.errorTitle"), Messages.getString("GUI.AddNewVolume.MSG.errorNoRolledBackMsg"), errorIcon); //$NON-NLS-1$ //$NON-NLS-2$
					}
					setCursor(null);
					volumeTable.refresh();
				}
				else
				{
					MessageDialog.show(Messages.getString("GUI.AddNewVolume.MSG.errorNoDirTitle"),Messages.getString("GUI.AddNewVolume.MSG.errorNoDirMsg"), errorIcon); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			setCursor(null);
		}
		
		
		/**
		 * Inserts new files and new directories in the database and, if the current
		 * <code>File</code> is a directory, recursively, calls itself
		 * on the content of that directory
		 * @param content an array of <code>File</code> to be inserted in the database
		 * @param parent the id of the directory which this <code>content</code> belongs to
		 * @throws SQLException if some errors occur
		 */
		private long[] catalog(File[] content, int parent, String currentPath) throws SQLException
		{
			long param[] = new long[3];
			param[0] = 0; //size
			param[1] = 0; //dirs
			param[2] = 0; //files
			dirId++;
			
			for(File f : content)
			{
				if(f.isFile())
				{
					if(!pattern.matcher(f.getName()).matches())
					{
						Cathy.DB.insertFile(volumeNameTextField.getText(), parent, f.getName(), f.lastModified(), f.length(), currentPath);
						param[0] += f.length();
						param[2]++;
					}
				}
				else if(f.isDirectory())
				{
					int oldId = dirId;
					Cathy.DB.insertDir(oldId, volumeNameTextField.getText(), f.getName(), parent, 0, f.lastModified(), 0, 0, currentPath);
					long[] subParam = catalog(f.listFiles(),dirId,currentPath+f.getName()+SEPARATOR);
					Cathy.DB.updateDirSizes(oldId, volumeNameTextField.getText(), subParam[0], (int)subParam[1], (int)subParam[2]);
					param[0] += subParam[0];
					param[1] += subParam[1]+1;
					param[2] += subParam[2];
				}
			}
			return param;
		}
    	
    }
    
    /**
     * This class implements the mothod that react to a click done
     * over "Browse" <code>JButton</code>
     */
    private class BrowseFolders implements ActionListener
    {
		/**
		 * Shows a <code>JFileChooser</code> that permits the user to select the
		 * starting path for the catalogation
		 * @param event only if the source of this event is <code>browseButton</code> the 
		 * <code>JFileChooser</code> is shown
		 */
    	public void actionPerformed(ActionEvent event)
		{
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle(Messages.getString("GUI.BrowseFolders.dialogTitle")); //$NON-NLS-1$
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = jfc.showOpenDialog(GUI.this);
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File selectedDir = jfc.getSelectedFile();
				rootPath.setText(selectedDir.getAbsolutePath());
				if(volumeNameTextField.getText().isEmpty())
					volumeNameTextField.setText(selectedDir.getName());
			}
		}
    }
    
}
