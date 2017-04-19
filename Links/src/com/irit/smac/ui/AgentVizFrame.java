package com.irit.smac.ui;

import java.awt.BorderLayout;
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
import javax.swing.tree.TreePath;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

import com.irit.smac.attributes.AVTAttribute;
import com.irit.smac.attributes.DrawableAttribute;
import com.irit.smac.model.Attribute;
import com.irit.smac.model.Attribute.AttributeStyle;
import com.irit.smac.model.Entity;
import com.irit.smac.model.Relation;
import com.irit.smac.model.SnapshotsCollection;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;

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

		setTitle(a.getName() + " Vizualization tool");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 592, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);

		splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);

		attributeTree = new JTree();
		attributeTree.setRootVisible(false);
		attributeTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				if (arg0.getSource().equals(attributeTree)) {
					updateLookAndDraw(attributeTree.getSelectionPaths());
				}
			}
		});

		splitPane.setLeftComponent(attributeTree);

		attributeViewerPanel = new JPanel();
		splitPane.setRightComponent(attributeViewerPanel);
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
						isDrawing = !isDrawing;
						draw();
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
						for (String s : r.getAttributes().keySet()) {
							for (Attribute t : r.getAttributes().get(s)) {
								this.toLook
										.add(new DrawableAttribute(DrawableAttribute.Type.RELATION, r.getName(), s, t));
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
							this.toLook.add(new DrawableAttribute(DrawableAttribute.Type.RELATION, r.getName(), s, t));
						}

					}

					if (path[i].getPath()[1].toString().contains("Entity")) {
						String s = path[i].getPath()[2].toString();
						String tmp = path[i].getPath()[3].toString();
						tmp = tmp.substring(tmp.indexOf("[") + 1, tmp.indexOf("]"));
						if (entity != null) {
							Attribute t = entity.getAttributesWithName(tmp);
							this.toLook
									.add(new DrawableAttribute(DrawableAttribute.Type.ENTITY, entity.getName(), s, t));
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

	private void updateTreeList() {
		DefaultTreeModel tree;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		tree = new DefaultTreeModel(root);

		DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode("Entity");
		root.add(entityNode);

		for (String carac : this.entity.getAttributes().keySet()) {

			DefaultMutableTreeNode newCarac = new DefaultMutableTreeNode(carac);
			entityNode.add(newCarac);

			for (Attribute t : this.entity.getAttributes().get(carac)) {
				newCarac.add(new DefaultMutableTreeNode(t.toString()));
			}

		}

		DefaultMutableTreeNode relNode = new DefaultMutableTreeNode("Relations");
		root.add(relNode);

		for (Relation r : this.relations) {

			DefaultMutableTreeNode newCarac = new DefaultMutableTreeNode(r.getName());
			relNode.add(newCarac);
			for (String s : r.getAttributes().keySet()) {
				for (Attribute t : r.getAttributes().get(s)) {
					newCarac.add(new DefaultMutableTreeNode(t.toString()));
				}
			}

		}

		attributeTree.setModel(tree);
		attributeTree.setRootVisible(false);
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
			s = "Entity is dead or not alive yet";
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
		}
	}

	public void setlblBotTxt(String txt, long num) {
		currentFrameNum = num;
		lblBotTxt.setText(txt);
	}
}