package fr.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

import fr.irit.smac.attributes.AVTAttribute;
import fr.irit.smac.attributes.DrawableAttribute;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.model.Attribute;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Relation;
import fr.irit.smac.model.SnapshotsCollection;
import fr.irit.smac.model.Attribute.AttributeStyle;

public class AgentVizFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1736315532751908362L;

	private JPanel contentPane;

	private JTree attributeTree;

	private JPanel attributeViewerPanel;

	private JLabel lblBotTxt;

	private Entity entity;

	private SnapshotsCollection snapCol;

	private LinksWindows links;

	private JSplitPane splitPane;

	private boolean isSynch = true;

	private long snapNum;
	private JButton btnNewButton;

	private ClicksPipe clicksPipe;

	private boolean neigh = false;

	private long drawSizeLong = 100;

	private Graph g;

	private Viewer viewer;
	private JButton btnNewButton_1;
	private JButton btnDraw;

	private String treeListSlected;
	private JTextPane txtpnLook;

	private AgentVizFrame me;

	private ArrayList<DrawableAttribute> toLook = new ArrayList<DrawableAttribute>();

	private String aname;
	private JButton btnSynch;

	private boolean isDrawing = false;
	private JFormattedTextField drawSize;
	private JLabel lblDrawSize;

	private long currentFrameNum;

	private ArrayList<Relation> relations = new ArrayList<Relation>();

	private long lastSnapNumDrawn = 0;
	private JPanel panel;
	private JLabel lblNewLabel;


	/**
	 * Create the frame.
	 * @param a The entity to look at.
	 * @param snapCol The reference to the snapshot collection.
	 * @param links The reference to the links windows.
	 */
	public AgentVizFrame(Entity a, SnapshotsCollection snapCol, LinksWindows links) {
		me = this;
		aname = a.getName();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				links.unregisterObserver(me);
				entity.setTargeted(false);
			}
		});
		this.entity = a;
		this.snapCol = snapCol;
		this.links = links;
		snapNum = links.getCurrentSnapNumber();

		setTitle(a.getName() + " Vizualization tool"+ "   Type : "+ a.getType());

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 592, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);

		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		contentPane.add(splitPane, BorderLayout.CENTER);

		attributeTree = new JTree();
		attributeTree.setRootVisible(false);
		attributeTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
		attributeTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				if (arg0.getSource().equals(attributeTree)) {
					updateLookAndDraw(attributeTree.getSelectionPaths());
				}
			}
		});

		splitPane.setLeftComponent(new JScrollPane(attributeTree));

		attributeViewerPanel = new JPanel();
		splitPane.setRightComponent(new JScrollPane(attributeViewerPanel));
		attributeViewerPanel.setLayout(new BorderLayout(0, 0));

		txtpnLook = new JTextPane();
		attributeViewerPanel.add(txtpnLook, BorderLayout.CENTER);

		btnNewButton = new JButton("Neighbouring:OFF");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnNewButton.getText().equals("Neighbouring:OFF")) {
					isTargeted(true);
					updateTreeList();
					btnNewButton.setText("Neighbouring:ON");
				} else {
					isTargeted(false);
					updateTreeList();
					btnNewButton.setText("Neighbouring:OFF");
				}
			}
		});

		btnSynch = new JButton("Synch: ON");
		btnSynch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource().equals(btnSynch)) {
					isSynch = !isSynch;
					if (!isSynch) {
						btnSynch.setText("Synch: OFF");
					} else {
						btnSynch.setText("Synch: ON");
						update();
					}
				}
			}
		});

		lblNewLabel = new JLabel("");
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				updateTreeList();
			}
		});
		lblNewLabel.setIcon(new ImageIcon(AgentVizFrame.class.getResource("/icons/refresh.png")));
		toolBar.add(lblNewLabel);
		btnSynch.setIcon(new ImageIcon(AgentVizFrame.class.getResource("/icons/synchronization.png")));
		toolBar.add(btnSynch);
		btnNewButton.setIcon(new ImageIcon(AgentVizFrame.class.getResource("/icons/neighb.png")));
		toolBar.add(btnNewButton);

		btnNewButton_1 = new JButton("Look");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource().equals(btnNewButton_1)) {
					drawLook();
				}
			}
		});
		btnNewButton_1.setIcon(new ImageIcon(AgentVizFrame.class.getResource("/icons/look.png")));
		toolBar.add(btnNewButton_1);

		btnDraw = new JButton("Draw:OFF");
		btnDraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(btnDraw)) {
					if (isDrawing) {
						isDrawing = !isDrawing;
						btnDraw.setText("Draw:OFF");
					} else {
						draw();
						isDrawing = !isDrawing;
						btnDraw.setText("Draw:ON");
					}
				}
			}
		});
		btnDraw.setIcon(new ImageIcon(AgentVizFrame.class.getResource("/icons/draw.png")));
		toolBar.add(btnDraw);

		lblDrawSize = new JLabel("Draw Size: ");
		toolBar.add(lblDrawSize);

		drawSize = new JFormattedTextField();
		drawSize.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getSource().equals(drawSize)) {
					drawSizeLong = Long.valueOf(drawSize.getText());
				}
			}
		});
		drawSize.setText("100");
		toolBar.add(drawSize);

		panel = new JPanel();
		contentPane.add(panel, BorderLayout.WEST);

		lblBotTxt = new JLabel("Agent name : Toto ");
		lblBotTxt.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblBotTxt, BorderLayout.SOUTH);

		initFrame();
		relations = snapCol.getRelations(aname, snapNum);
		updateTreeList();
		for (int i = 0; i < attributeTree.getRowCount(); i++) {
			attributeTree.expandRow(i);
		}
	}

	private void updateLookAndDraw(TreePath[] path) {
		this.toLook = new ArrayList<DrawableAttribute>();
		relations = snapCol.getRelations(aname, currentFrameNum);
		if (path != null && entity != null) {
			for (int i = 0; i < path.length; i++) {
				switch (path[i].getPath().length) {
				case 1:
					// System.out.println(path[i].getPath()[0].toString());
					break;
				case 2:
					/* Whole agent or relations selected */
					if (path[i].getPath()[1].toString().contains("Relations")) {
						for (Relation r : this.relations) {
							for (String s : r.getAttributes().keySet()) {
								for (Attribute t : r.getAttributes().get(s)) {
									this.toLook.add(
											new DrawableAttribute(DrawableAttribute.Type.RELATION, r.getName(), s, t));
								}
							}
						}
					}
					if (path[i].getPath()[1].toString().contains("Entity")) {
						for (String s : entity.getAttributes().keySet()) {
							for (Attribute t : entity.getAttributes().get(s)) {
								this.toLook.add(
										new DrawableAttribute(DrawableAttribute.Type.ENTITY, entity.getName(), s, t));
							}
						}
					}
					break;
				case 3:
					/* Set of characteristics selected */

					if (path[i].getPath()[1].toString().contains("Relations")) {
						Relation r = snapCol.getRelation(path[i].getPath()[2].toString(), this.currentFrameNum);
						if(r != null && this.toLook != null){
							for (String s : r.getAttributes().keySet()) {
								for (Attribute t : r.getAttributes().get(s)) {
									this.toLook
									.add(new DrawableAttribute(DrawableAttribute.Type.RELATION, r.getName(), s, t));
								}
							}
						}
					}

					if (path[i].getPath()[1].toString().contains("Entity")) {

						String s = path[i].getPath()[2].toString();
						for (Attribute t : entity.getAttributes().get(s)) {
							this.toLook
							.add(new DrawableAttribute(DrawableAttribute.Type.ENTITY, entity.getName(), s, t));
						}
					}
					break;
				case 4:
					/* One characteristic selected */

					/* Set of characteristics selected */

					if (path[i].getPath()[1].toString().contains("Relations")) {
						Relation r = snapCol.getRelation(path[i].getPath()[2].toString(), this.currentFrameNum);
						String s = path[i].getPath()[3].toString();
						for (Attribute t : r.getAttributes().get(s)) {
							this.toLook.add(new DrawableAttribute(DrawableAttribute.Type.RELATION, r.getName() , s, t));
						}

					}

					if (path[i].getPath()[1].toString().contains("Entity")) {
						String s = path[i].getPath()[2].toString();
						String tmp = path[i].getPath()[3].toString();
						tmp = tmp.substring(tmp.indexOf("[") + 1, tmp.indexOf("]"));
						if (entity != null) {
							Attribute t = entity.getAttributesWithName(tmp);
							this.toLook
							.add(new DrawableAttribute(DrawableAttribute.Type.ENTITY, entity.getName() + entity.getType(), s, t));
						}

					}
					break;
				}
			}
		}
	}

	protected void isTargeted(boolean b) {
		entity.setTargeted(b);
		links.getDisplayedGraph().refreshNeighbouring(entity.getName(), entity.getType());
	}

	private void initFrame() {
		setlblBotTxt("Entity name : " + this.entity.getName() + " on snapshot number : " + links.getCurrentSnapNumber(),
				links.getCurrentSnapNumber());

		updateTreeList();

		setVisible(true);
	}

	/**
	 * Method uses by LinksWindows 
	 * Check if the synchronisation is on
	 * 	If it's on uses the method updateTreeList
	 */
	public void update(){
		if(isSynch)
			updateTreeList();
	}

	//TODO
	// Raffraichir la valeur de l'attribut
	private void updateTreeList() {
		if(entity != null){
			setTitle(entity.getName() + " Vizualization tool"+ "   Type : "+ entity.getType());

			//We use reload if we have to create entity or relation
			boolean needReload = false;
			DefaultTreeModel model = (DefaultTreeModel) this.attributeTree.getModel();
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
			if(root.isLeaf()){
				DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode("Entity : "+entity.getName());
				model.insertNodeInto(entityNode, root, 0);
				needReload = true;
			}
			DefaultMutableTreeNode entityNode = (DefaultMutableTreeNode) root.getFirstChild();

			//Verify if the tree already knows the attributes
			for (String carac : this.entity.getAttributes().keySet()) {
				boolean exist = false;
				int pos =0;
				for(int i = 0; i < entityNode.getChildCount(); i++){
					if(entityNode.getChildAt(i).toString().equals(carac)){
						exist = true;
						pos = i;
					}
				}
				//If no we create it
				if(!(exist)){
					DefaultMutableTreeNode newCarac = new DefaultMutableTreeNode(carac);
					model.insertNodeInto(newCarac, entityNode, entityNode.getChildCount());

					for (Attribute t : this.entity.getAttributes().get(carac)) {
						model.insertNodeInto(new DefaultMutableTreeNode(t.toString()), newCarac, newCarac.getChildCount());
					}
					model.nodeChanged(entityNode);
				}
				//If yes we verify if the values are the same
				else{
					boolean valExist = true;
					ArrayList<Attribute> addList = new ArrayList<Attribute>();
					for(Attribute att : this.entity.getAttributes().get(carac)){
						boolean find = false;
						for(int i = 0; i < entityNode.getChildAt(pos).getChildCount();i++){
							if(entityNode.getChildAt(pos).getChildAt(i).toString().equals(att.toString()))
								find = true;
						}
						if(!(find)){
							valExist = false;
							addList.add(att);
						}
					}
					//If no we replace the old value with the new
					if(!(valExist)){
						for(Attribute att : addList){
							for(int i =0; i < entityNode.getChildAt(pos).getChildCount(); i++){
								if(entityNode.getChildAt(pos).getChildAt(i).toString().contains(att.getName())){
									TreeNode tmp = entityNode.getChildAt(pos).getChildAt(i);
									model.insertNodeInto(new DefaultMutableTreeNode(att), (MutableTreeNode) entityNode.getChildAt(pos), 0);
									model.removeNodeFromParent((MutableTreeNode) tmp);
								}
							}
						}
						//TreeNode tmp = entityNode.getChildAt(pos).getChildAt(0);

					}
					model.nodeChanged(entityNode);
				}

			}


			/*for(String s : this.entity.getAttributes().keySet()){
			if(this.entity.getAttributes().keySet().size() != entityNode.getChildCount()){
				for(int i = entityNode.getChildCount()-1; i>= 0;i--){
					if(!(this.entity.getAttributes().keySet().contains(entityNode.getChildAt(i).toString())))
						model.removeNodeFromParent((MutableTreeNode) entityNode.getChildAt(i));	
				}
			}
		}*/

			//DefaultMutableTreeNode relNode = new DefaultMutableTreeNode("Relations");
			if(root.getChildCount()<2){
				DefaultMutableTreeNode relNode = new DefaultMutableTreeNode("Relations");
				//root.add(relNode);
				model.insertNodeInto(relNode, root, 1);
				needReload = true;
			}
			DefaultMutableTreeNode relNode = (DefaultMutableTreeNode) root.getChildAt(1);


			for(int i =relNode.getChildCount()-1; i>= 1;i--){
				if(!(this.relations.contains(((DefaultMutableTreeNode) relNode.getChildAt(i)).getUserObject())))
					model.removeNodeFromParent((MutableTreeNode) relNode.getChildAt(i));
			}

			for (Relation r : this.relations) {
				boolean exist = false;
				for(int i = 0; i < relNode.getChildCount(); i++){
					if(relNode.getChildAt(i).toString().equals(r.getName())){
						exist = true;
					}
				}
				if(!(exist)){
					DefaultMutableTreeNode newCarac = new DefaultMutableTreeNode(r.getName());
					//relNode.add(newCarac);
					model.insertNodeInto(newCarac, relNode, relNode.getChildCount());
					for (String s : r.getAttributes().keySet()) {
						for (Attribute t : r.getAttributes().get(s)) {
							newCarac.add(new DefaultMutableTreeNode(t.toString()));
							model.insertNodeInto(new DefaultMutableTreeNode(t), newCarac, newCarac.getChildCount());
						}
					}
				}
			}

			if(needReload)
				model.reload();
			else{
				model.nodeChanged(root);
				model.nodeChanged(relNode);
				model.nodeChanged(entityNode);
			}
		}
		//attributeTree.setModel(tree);
		//attributeTree.setRootVisible(false);
	}

	public void draw() {
		Entity a;
		long max = this.currentFrameNum;
		if (drawSizeLong == 0) {
			max = this.links.getMaxSnapNumber();
		}
		long u;
		if (links.getFrameSpeed() > 0) {
			u = Math.max(this.lastSnapNumDrawn, Math.max(1, this.currentFrameNum - drawSizeLong));
		} else {
			u = Math.min(this.lastSnapNumDrawn, Math.max(1, this.currentFrameNum - drawSizeLong));
		}

		for (long i = u; i <= max; i++) {
			long timei = i;
			if (drawSizeLong != 0) {
				timei = i % drawSizeLong;
			}
			a = snapCol.getEntity(this.aname, i);
			if (a != null) {
				for (DrawableAttribute t : this.toLook) {
					String s = t.getAttribute().getName();
					Attribute theAttribute = t.getAttribute();
					if (theAttribute.getTypeToDraw().equals(AttributeStyle.LINEAR)) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " linear",
								ChartType.LINE).add(s, timei, (Double) theAttribute.getValue());
					}
					if (theAttribute.getTypeToDraw().equals(AttributeStyle.BAR)) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " bar",
								ChartType.BAR).add(s, timei, (Double) theAttribute.getValue());
					}
					if (theAttribute.getTypeToDraw().equals(AttributeStyle.AVRT)) {
						Double tab[] = (Double[]) theAttribute.getValue();
						for (Double val : tab) {
							LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE).add(s + "LOWER", timei, tab[0]);
							LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE).add(s + "AVTDownLower", timei, tab[1] - tab[2]);
							LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE).add(s + "AVTDownValue", timei, tab[1]);
							LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE).add(s + "AVTDownUpper", timei, tab[1] + tab[2]);
							LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE).add(s + "AVTUpLower", timei, tab[3] - tab[4]);
							LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE).add(s + "AVTUpValue", timei, tab[3]);
							LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE).add(s + "AVTUpUpper", timei, tab[3] + tab[4]);
							LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE).add(s + "UPPER", timei, tab[5]);
						}
					}
					if (theAttribute.getTypeToDraw().equals(AttributeStyle.AVT)) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVT",
								ChartType.LINE).add(s + "Value", timei, (Double) theAttribute.getValue());
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVT",
								ChartType.LINE).add(s + "Delta", timei, ((AVTAttribute) theAttribute).getDelta());
					}
				}
			}
		}

		lastSnapNumDrawn = this.currentFrameNum;
	}

	public void drawLook() {
		String s = "";
		for (DrawableAttribute t : this.toLook) {
			if (t.getType().equals(DrawableAttribute.Type.ENTITY)) {
				s = s + "{" + t.getCaracList() + "} " + t.getAttribute().toString() + "\n";
			} else {
				s = s + " " + t.getType() + ":" + t.getName() + " : {" + t.getCaracList() + "} "
						+ t.getAttribute().toString() + "\n";
			}
		}
		if (s == "") {
			if(toLook.isEmpty()){
				s = "Nothing is selected";
			}else{
				s = "Entity is dead or not alive yet";
			}
		}
		txtpnLook.setText(s);
	}

	public void notifyJump(long num) {
		if (isSynch) {
			entity = snapCol.getEntity(aname, num);
			relations = snapCol.getRelations(aname, num);
			updateLookAndDraw(attributeTree.getSelectionPaths());
			drawLook();
			if (isDrawing) {
				draw();
			}
			setlblBotTxt("Entity name : " + aname + " on snapshot number : " + num, num);
			update();
		}
	}

	public void setlblBotTxt(String txt, long num) {
		currentFrameNum = num;
		lblBotTxt.setText(txt);
	}
}