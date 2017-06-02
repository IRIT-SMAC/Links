package fr.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import fr.irit.smac.attributes.AVTAttribute;
import fr.irit.smac.attributes.DrawableAttribute;
import fr.irit.smac.attributes.DrawableAttribute.Type;
import fr.irit.smac.core.AutoPlayThread;
import fr.irit.smac.core.DisplayedGraph;
import fr.irit.smac.core.Links;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.model.Attribute;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.model.SnapshotsCollection;
import fr.irit.smac.model.Attribute.AttributeStyle;

/**
 * LinksWindows: This class
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * @version 1.0
 * @since 10/03/2017
 *
 */
public class LinksWindows implements Serializable {

	public String xpName;

	private JFrame frame;

	private ArrayList<AgentVizFrame> listAgent = new ArrayList<AgentVizFrame>();

	private ArrayList<AgentVizFrame> toRemove = new ArrayList<AgentVizFrame>();

	private ArrayList<AgentVizFrame> toAdd = new ArrayList<AgentVizFrame>();

	private DisplayedGraph graph;

	private JPanel graphPanel;

	private View view;

	private Viewer viewer;

	private JLabel snapNumber;

	private ClicksPipe clicksPipe;

	private JLabel lblPlay;

	private long currentSnap;

	private boolean isSynch = true;

	private JLabel lblSynch;

	private final String linkToCss;

	private boolean isInfoWindowsOpened = false;

	private final AutoPlayThread autoPlayThread;

	private boolean moving;

	private JLabel lblStop;
	private JTextField txtSpeed;
	private JTextField txtFramerate;
	private Links linksRef;
	private JFileChooser fc = new JFileChooser();
	private JLabel lblInfo;

	private InfoWindow info;
	private JLabel lblMoving;
	private List<Map<Entity,List<String>>> charts;
	private Map<Attribute,AttributeStyle> typeChart;
	private long lastSnapNumDrawn = 0;

	/**
	 * Creates a new JFrame and start to display the experiment in parameter.
	 * 
	 * @param xpName
	 *            The name of the experiment to display.
	 * @param linkToCss
	 *            The path to the CSS file.
	 * @param links
	 *            A reference to the main application.
	 */
	public LinksWindows(String xpName, String linkToCss, Links links) {
		charts = new ArrayList<Map<Entity,List<String>>>();
		typeChart = new HashMap<Attribute,AttributeStyle>();
		linksRef = links;
		this.xpName = xpName;
		this.linkToCss = linkToCss;
		autoPlayThread = new AutoPlayThread(this);
		this.moving = false;
		SnapshotsCollection snapCol = null;
		if (links != null) {
			snapCol = new SnapshotsCollection(this);
			snapCol.setLinksWindows(this);
		}
		graph = new DisplayedGraph(snapCol, linkToCss);

		initialize();
		this.frame.setVisible(true);
		if (snapCol != null) {
			if (snapCol.getSnaptshot(1) != null) {
				switchToSnap(1);
			}
		}

		//TODO
		/**
		 * Thread used to understand the user's input
		 */
		Thread t = new Thread(){
			public void run(){
				System.out.println("Enter : 'NBSNAP for the number of snapshot'");
				System.out.println("Enter : 'SHOW <nameEntity> <Attribute1> <Attribute2> <AttributeN> <NOSYNCHR>(synchronization) <BAR/LIN/AVRT/AVT>' to show the graph (in case of blank put the name between simple quote");
				Scanner sc = new Scanner(System.in);
				while(true){
					int option = 0;
					String ans = sc.nextLine();
					if(ans.equals("NBSNAP")){
						System.out.println("The number of snapshot is : " + getSnapCol().getMaxNum());
					}
					if(ans.contains("SHOW ")){
						if(ans.contains("NOSYNCHR"))
							option++;
						Map<Entity,List<String>> tmpMap = new HashMap<Entity,List<String>>();
						ArrayList<String> tmpList = new ArrayList<String>();
						String[] spl = ans.split(" (?=(?:[^\']*\'[^\']*\')*[^\']*$)");
						for(int i =0; i<spl.length;i++){
							if(spl[i].contains("'"))
								spl[i] = spl[i].split("'")[1];
						}
						AttributeStyle type = null;
						if(ans.contains("BAR")){
							type = AttributeStyle.BAR;
							option++;
						}
						if(ans.contains("LIN")){
							type = AttributeStyle.LINEAR;
							option++;
						}
						if(ans.contains("AVRT")){
							type = AttributeStyle.AVRT;
							option++;
						}
						if(ans.contains("AVT")){
							type = AttributeStyle.AVT;
							option++;
						}
						Entity e = getSnapCol().getEntity(spl[1], getCurrentSnapNumber());
						if(e == null){
							System.out.println("Snapshot not found");
						}
						else{
							ArrayList<DrawableAttribute> atts = new ArrayList<DrawableAttribute>();
							for(int i = 2; i < spl.length-option; i++){
								String s = spl[i];
								if(e.getAttributes().get(s) == null){
									System.out.println("Attribut "+s+" not found");
								}
								else
								{
									for (Attribute t : e.getAttributes().get(s)) {
										DrawableAttribute datt = new DrawableAttribute(DrawableAttribute.Type.ENTITY, e.getName(), s, t);
										if(type==null)
											type = t.getTypeToDraw();
											typeChart.put(t, type);
										atts.add(datt);
									}
									if(!ans.contains("NOSYNCHR")){
										tmpList.add(s);
									}
								}
								if(!ans.contains("NOSYNCHR")){
									tmpMap.put(e, tmpList);
									charts.add(tmpMap);
								}
								else
									draw(e,100,atts,type);
							}

						}
					}
				}
			}
		};
		t.start();

	}

	/**
	 * Get the snapshot collection.
	 * 
	 * @return The collection of snapshots.
	 */
	public SnapshotsCollection getSnapshotsCollection() {
		return graph.getSnapCol();
	}

	/**
	 * Initializes the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				linksRef.informClose();
			}
		});
		frame.getContentPane().setBackground(SystemColor.inactiveCaption);
		frame.setAutoRequestFocus(false);
		frame.setBounds(200, 200, 900, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Links : Vizualizing agents' life");
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		JToolBar toolBar_1 = new JToolBar();
		toolBar_1.setBackground(SystemColor.inactiveCaption);
		panel.add(toolBar_1);

		lblPlay = new JLabel("");
		lblPlay.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent arg0) {
				if (arg0.getSource().equals(lblPlay))
					onPlayClick();
			}
		});

		lblSynch = new JLabel("");
		lblSynch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (isSynch) {
					lblSynch.setEnabled(false);
				} else {
					lblSynch.setEnabled(true);
				}
				isSynch = !isSynch;
			}
		});

		lblInfo = new JLabel("");
		LinksWindows myWindow = this;
		lblInfo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (!isInfoWindowsOpened) {
					isInfoWindowsOpened = true;
					info = new InfoWindow(myWindow);
				}
			}
		});
		lblInfo.setIcon(new ImageIcon(LinksWindows.class.getResource("/icons/question.png")));
		toolBar_1.add(lblInfo);
		lblSynch.setIcon(new ImageIcon(LinksWindows.class.getResource("/icons/synchronization.png")));
		toolBar_1.add(lblSynch);
		toolBar_1.add(lblPlay);
		lblPlay.setIcon(new ImageIcon(LinksWindows.class.getResource("/icons/play.png")));

		lblStop = new JLabel("");
		lblStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				onPauseClick();
			}
		});
		lblStop.setEnabled(false);
		lblStop.setIcon(new ImageIcon(LinksWindows.class.getResource("/icons/stop.png")));
		toolBar_1.add(lblStop);

		JLabel lblPrev = new JLabel("");
		lblPrev.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				switchToSnap(Math.max(1, currentSnap - Integer.valueOf(txtFramerate.getText())));
				notifyJump(Math.max(Math.min(currentSnap + Integer.valueOf(txtFramerate.getText()),
						graph.getSnapCol().getMaxNum() - 1), 1));
			}
		});
		lblPrev.setIcon(new ImageIcon(LinksWindows.class.getResource("/icons/backL.png")));
		toolBar_1.add(lblPrev);

		JLabel lblNext = new JLabel("");
		lblNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				switchToSnap(Math.max(Math.min(currentSnap + Integer.valueOf(txtFramerate.getText()),
						graph.getSnapCol().getMaxNum() - 1), 1));
			}
		});
		lblNext.setIcon(new ImageIcon(LinksWindows.class.getResource("/icons/nextR.png")));
		toolBar_1.add(lblNext);

		lblMoving = new JLabel("Moving   : NO ");
		lblMoving.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				moving = !moving;
				if(moving)
					lblMoving.setText("Moving : OK ");
				else
					lblMoving.setText("Moving : NO ");
			}
		});
		toolBar_1.add(lblMoving);

		JLabel lblSpeed = new JLabel("Speed:");
		toolBar_1.add(lblSpeed);

		txtSpeed = new JTextField();
		txtSpeed.setHorizontalAlignment(SwingConstants.LEFT);
		txtSpeed.setText("250");
		toolBar_1.add(txtSpeed);
		txtSpeed.setColumns(10);

		JLabel lblFrameRate = new JLabel("Frame Rate:");
		toolBar_1.add(lblFrameRate);

		txtFramerate = new JTextField();
		txtFramerate.setText("1");
		toolBar_1.add(txtFramerate);
		txtFramerate.setColumns(10);

		snapNumber = new JLabel("");
		toolBar_1.add(snapNumber);
		setSnapNumber(0);

		graphPanel = new JPanel();
		frame.getContentPane().add(graphPanel, BorderLayout.CENTER);
		graphPanel.setLayout(new BorderLayout(0, 0));
		generateGraph();
	}

	/**
	 * This method is called on a click on the play icon.
	 */
	public void onPlayClick() {
		autoPlayThread.setActivated(true);
		autoPlayThread.setFrameRateAndSpeed(Integer.valueOf(this.txtFramerate.getText()),
				Integer.valueOf(this.txtSpeed.getText()));
		this.lblPlay.setEnabled(false);
		this.lblStop.setEnabled(true);
	}

	/**
	 * This method is called on a click on the pause icon.
	 */
	public void onPauseClick() {
		autoPlayThread.setActivated(false);
		this.lblPlay.setEnabled(true);
		this.lblStop.setEnabled(false);
	}

	/**
	 * Change the displayed graph to display the snapshot which number is passed
	 * as parameter.
	 * 
	 * @param number
	 *            The number of the snapshot to be displayed.
	 */
	public void switchToSnap(long number) {
		graph.loadGraph(number);
		setSnapNumber(number);
		notifyJump(number);
		notifyDraw();
		this.currentSnap = number;
	}

	/**
	 * Set the Snapshot number label of the displayed graph.
	 * 
	 * @param text
	 *            The text to display.
	 */
	public void setSnapNumber(long text) {
		this.snapNumber.setText("Current Snap: " + text);
	}

	private void generateGraph() {
		viewer = new Viewer(graph.getGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		view = viewer.addDefaultView(false); // false indicates "no JFrame".
		graphPanel.add((Component) view, BorderLayout.CENTER);
		clicksPipe = new ClicksPipe(graph.getGraph(), viewer, this);
	}

	/**
	 * Get the current snapshot number.
	 * 
	 * @return The current snapshot number.
	 */
	public long getCurrentSnapNumber() {
		return this.currentSnap;
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/**
	 * Get the snapshots collection.
	 * 
	 * @return The snapshots collection.
	 */
	public SnapshotsCollection getSnapCol() {
		return graph.getSnapCol();
	}

	/**
	 * Get the displayed graph.
	 * 
	 * @return The displayed graph.
	 */
	public DisplayedGraph getDisplayedGraph() {
		return graph;
	}

	/**
	 * This method is called to inform Links that a new snapshot is availabe.
	 * 
	 * @param maxNum
	 *            The number of the new snapshot.
	 */
	public void newSnap(long maxNum) {
		if (isSynch) {
			switchToSnap(maxNum);
		}
		notifyJump(maxNum);

	}

	private synchronized void notifyJump(long number) {
		for (AgentVizFrame a : toAdd) {
			listAgent.add(a);
		}

		toAdd = new ArrayList<AgentVizFrame>();

		for (AgentVizFrame a : listAgent) {
			if (a != null) {
				a.notifyJump(number);
			}
		}
		for (AgentVizFrame a : toRemove) {
			listAgent.remove(a);
			a.getDefaultCloseOperation();
		}
		toRemove = new ArrayList<AgentVizFrame>();

		if (isInfoWindowsOpened) {
			if (info != null) {
				info.buildText();
			}
		}
	}

	/**
	 * Add a new observer to the Frame.
	 * 
	 * @param aviz
	 *            The reference to the new observer.
	 */
	public void registerObserver(AgentVizFrame aviz) {
		toAdd.add(aviz);
	}

	/**
	 * Remove an observer.
	 * 
	 * @param me
	 *            The reference of the observer to remove.
	 */
	public void unregisterObserver(AgentVizFrame me) {
		toRemove.add(me);
	}

	/**
	 * Add a new snapshot to the collection.
	 * 
	 * @param s
	 *            The snapshot to be added.
	 */
	public void addSnapshot(Snapshot s) {
		for(AgentVizFrame a : this.listAgent){
			boolean alive = false;
			for(Entity e : s.getEntityList()){
				if(a.getName().equals(e.getName()))
					alive = true;
			}
			if(!alive)
				a.dispose();
		}
		ArrayList<Map<Entity,List<String>>> removeList = new ArrayList<Map<Entity,List<String>>>();
		for(Map<Entity,List<String>> l : this.charts){
			for(Entity chart : l.keySet()){
				boolean alive = false;
				for(Entity e : s.getEntityList()){
					if(e.getName().equals(chart.getName()))
						alive = true;
				}
				//TODO
				if(!alive){
					removeList.add(l);
				}
			}
		}
		for(Map<Entity,List<String>> l :removeList)
			this.charts.remove(l);
		graph.getSnapCol().addSnapshot(s);
	}

	/**
	 * Get the highest snapshot num.
	 * 
	 * @return The highest snapshot num.
	 */
	public long getMaxSnapNumber() {
		return graph.getSnapCol().getMaxNum();
	}

	/**
	 * Get the frame speed.
	 * 
	 * @return The frame speed.
	 */
	public int getFrameSpeed() {
		return Integer.valueOf(this.txtFramerate.getText());
	}

	/**
	 * 
	 */
	public void inforInfoWindowsClosing() {
		this.isInfoWindowsOpened = false;
	}

	public Viewer getViewer() {
		return viewer;
	}

	public String getXpName() {
		return xpName;
	}

	public boolean getMoving(){
		return this.moving;
	}

	/**
	 * Draw a chart which was asked by a command line
	 * @param a
	 * 		The entity which correspond
	 * @param drawSizeLong
	 * 			The size
	 * @param atts
	 * 		All the attribute to represent
	 */
	public void draw(Entity a,long drawSizeLong,  ArrayList<DrawableAttribute> atts,AttributeStyle style) {
		long max = this.getCurrentSnapNumber();
		long u;
		if (this.getFrameSpeed() > 0) {
			u = Math.max(this.lastSnapNumDrawn, Math.max(1, this.getCurrentSnapNumber() - drawSizeLong));
		} else {
			u = Math.min(this.lastSnapNumDrawn, Math.max(1, this.getCurrentSnapNumber() - drawSizeLong));
		}

		for (long i = u; i <= max; i++) {
			long timei = i;
			if (drawSizeLong != 0) {
				timei = i % drawSizeLong;
			}
			if (a != null) {
				for (DrawableAttribute t : atts) {
					String s = t.getAttribute().getName();
					Attribute theAttribute = t.getAttribute();
					if(style == null)
						style = theAttribute.getTypeToDraw();
					if (style == AttributeStyle.LINEAR || style == null) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " linear",
								ChartType.LINE).add(s, timei, (Double) theAttribute.getValue());
					}
					if (style == AttributeStyle.BAR) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " bar",
								ChartType.BAR).add(s, timei, (Double) theAttribute.getValue());
					}
					if (style == AttributeStyle.AVRT) {
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
					if (style == AttributeStyle.AVT) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVT",
								ChartType.LINE).add(s + "Value", timei, (Double) theAttribute.getValue());
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVT",
								ChartType.LINE).add(s + "Delta", timei, ((AVTAttribute) theAttribute).getDelta());
					}
				}
			}
		}
	}

	//TODO
	/**
	 * Method use to refresh all chats who are synchr
	 */
	public void notifyDraw(){
		for(Map<Entity,List<String>> h : this.charts){
			for(Entity e : h.keySet()){
				ArrayList<DrawableAttribute> atts = new ArrayList<DrawableAttribute>();
				AttributeStyle style = AttributeStyle.LINEAR;
				for(String s : h.get(e)){
					for (Attribute t : e.getAttributes().get(s)) {
						DrawableAttribute datt = new DrawableAttribute(DrawableAttribute.Type.ENTITY, e.getName(), s, t);
						style = typeChart.get(t);
						atts.add(datt);
					}

				}
				draw(e,100,atts,style);
			}
		}
	}

}
