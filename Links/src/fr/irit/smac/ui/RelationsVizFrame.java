package fr.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import fr.irit.smac.attributes.DrawableAttribute;
import fr.irit.smac.core.Links;
import fr.irit.smac.model.Attribute;
import fr.irit.smac.model.Relation;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.model.SnapshotsCollection;

import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JLabel;

/**
 * This class show a frame with all the relations in a snapshot
 * @author Marcillaud Guilhem
 *
 */
public class RelationsVizFrame extends JFrame {

	private JPanel contentPane;

	private JTree relationsTree;

	private SnapshotsCollection snapCol;

	private LinksWindows linksWindow;

	private JPanel relationsViewerPanel;
	private JTextPane textPane;

	private ArrayList<DrawableAttribute> toLook = new ArrayList<DrawableAttribute>();
	private JButton btnSynch;

	private boolean isSynch = true;

	/**
	 * Create the frame.
	 */
	public RelationsVizFrame(LinksWindows links) {
		this.linksWindow = links;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		relationsTree = new JTree();
		relationsTree.setRootVisible(false);
		relationsTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
		relationsTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				if (arg0.getSource().equals(relationsTree)) {
					updateDesc(relationsTree.getSelectionPaths());
				}
			}
		});

		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);

		btnSynch = new JButton("Synch: ON");
		btnSynch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource().equals(btnSynch)) {
					isSynch = !isSynch;
					if (!isSynch) {
						btnSynch.setText("Synch: OFF");
					} else {
						btnSynch.setText("Synch: ON");
						notifyJump();
					}
				}
			}
		});
		btnSynch.setIcon(new ImageIcon(AgentVizFrame.class.getResource("/icons/synchronization.png")));
		toolBar.add(btnSynch);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(new JScrollPane(relationsTree));
		relationsViewerPanel = new JPanel();
		relationsViewerPanel.setLayout(new BorderLayout(0,0));
		splitPane.setRightComponent(new JScrollPane(relationsViewerPanel));

		textPane = new JTextPane();
		relationsViewerPanel.add(textPane,BorderLayout.CENTER);
		splitPane.setResizeWeight(0.5);
		contentPane.add(splitPane, BorderLayout.CENTER);
		this.updateTreeList();
		this.setVisible(true);
	}

	public void notifyJump(){
		if(isSynch){
			updateTreeList();
			updateDesc(relationsTree.getSelectionPaths());
			drawLook();
		}

	}

	/**
	 * Update the values of relationstree
	 */
	public void updateTreeList(){
		Snapshot s = linksWindow.getSnapshotsCollection().getSnaptshot(linksWindow.getCurrentSnapNumber());
		DefaultTreeModel model = (DefaultTreeModel) this.relationsTree.getModel();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
		boolean needReload = false;
		if(root.isLeaf()){
			model.insertNodeInto(new DefaultMutableTreeNode("RELATIONS"), root, 0);
			needReload = true;
		}
		DefaultMutableTreeNode relation = (DefaultMutableTreeNode) root.getFirstChild();
		for(Relation r : s.getRelations()){
			boolean exist = false;
			for(int i =0; i < relation.getChildCount(); i++){
				if(relation.getChildAt(i).toString().equals(r.getName())){
					exist = true;
				}
			}
			if(!exist){
				model.insertNodeInto(new DefaultMutableTreeNode(r.getName()), relation, relation.getChildCount());
			}
		}
		model.nodeChanged(root);
		model.nodeChanged(relation);
		if(needReload)
			model.reload();
	}

	/**
	 * Update the visualization of the relation selected
	 * @param treePaths
	 */
	public void updateDesc(TreePath[] treePaths){
		Snapshot s = linksWindow.getSnapshotsCollection().getSnaptshot(linksWindow.getCurrentSnapNumber());
		this.toLook = new ArrayList<DrawableAttribute>();
		if(treePaths != null){
			for(int i = 0; i < treePaths.length;i++){
				switch(treePaths[i].getPath().length){
				case 2:
					if (treePaths[i].getPath()[1].toString().contains("RELATIONS")) {
						for(Relation r : s.getRelations()){
							if(r.getAttributes() != null)
								for (String str : r.getAttributes().keySet()) {
									for (Attribute t : r.getAttributes().get(str)) {
										this.toLook.add(
												new DrawableAttribute(DrawableAttribute.Type.RELATION, r.getName(), str, t));
									}
								}
						}
					}
					break;
				case 3:
					if (treePaths[i].getPath()[1].toString().contains("RELATIONS")) {
						Relation r = this.linksWindow.getSnapshotsCollection().
								getRelation(treePaths[i].getPath()[2].toString(), this.linksWindow.getCurrentSnapNumber());
						if(r.getAttributes() != null)
							for(String str : r.getAttributes().keySet())
								for (Attribute t : r.getAttributes().get(str)) {
									this.toLook.add(new DrawableAttribute(DrawableAttribute.Type.RELATION, r.getName() , str, t));
								}
					}
					break;
				}
			}
		}
	}


	public void drawLook() {
		String s = "";
		Lock l = new ReentrantLock();
		l.lock();
		try{
			for (DrawableAttribute t : this.toLook) {
				s = s + " " + t.getType() + ":" + t.getName() + " : {" + t.getCaracList() + "} "
						+ t.getAttribute().toString() + "\n";
			}
			if (s == "") {
				if(toLook.isEmpty()){
					s = "Nothing is selected";
				}else{
					s = "Entity is dead or not alive yet";
				}
			}
			textPane.setText(s);
		}
		finally{
			l.unlock();
		}
	}

}
