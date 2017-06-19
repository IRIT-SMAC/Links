package fr.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.JButton;
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
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import fr.irit.smac.attributes.AVTAttribute;
import fr.irit.smac.attributes.DoubleAttribute;
import fr.irit.smac.attributes.DrawableAttribute;
import fr.irit.smac.attributes.DrawableAttribute.Type;
import fr.irit.smac.attributes.StringAttribute;
import fr.irit.smac.core.AutoPlayThread;
import fr.irit.smac.core.DisplayedGraph;
import fr.irit.smac.core.Links;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.interfaces.ILxPlotChart;
import fr.irit.smac.lxplot.server.LxPlotChart;
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

	private boolean drawing = false;

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
	private JLabel lblDraw;

	private Map<String,ILxPlotChart> listLxPlot;

	private List<DrawableAttribute> tolook;

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
	public LinksWindows(String xpName, String linkToCss, Links links, boolean visible) {
		charts = new ArrayList<Map<Entity,List<String>>>();
		typeChart = new HashMap<Attribute,AttributeStyle>();
		this.listLxPlot = new HashMap<String,ILxPlotChart>();
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
		this.frame.setVisible(visible);
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
				System.out.println("Enter : 'SHOW <nameEntity> <Attribute1> <Attribute2> <AttributeN> <NOSYNCHR> <BAR/LIN/AVRT/AVT> <SIZE=N>' to show the graph (in case of blank put the name between simple quote)");
				System.out.println("Example : SHOW 'entity 1' attr1 'attr 2' BAR SIZE=300");
				System.out.println("NOSYNCHR is an option if you don't want that the synchronisation of the chart");
				System.out.println("DEFAULT : LIN (or the style of the Attribute if it set) and Size=100");
				Scanner sc = new Scanner(System.in);
				while(true){
					int option = 0;
					long size = 100;
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
						if(ans.contains("PIE")){
							type = AttributeStyle.PIE;
							option++;
						}
						if(ans.contains("SIZE")){
							size = Long.parseLong(spl[spl.length-1].split("=")[1]);
							option++;
						}
						Entity e = getSnapCol().getEntity(spl[1], getCurrentSnapNumber());
						if(e == null){
							System.out.println("Entity not found");
						}
						else{
							ArrayList<DrawableAttribute> atts = new ArrayList<DrawableAttribute>();
							if(spl.length-option == 2){
								constructDraw(e,type,size,!ans.contains("NOSYNCHR"));
							}
							else{
								for(int i = 2; i < spl.length-option; i++){
									String s = spl[i];
									if(e.getAttributes().get(s) == null){
										System.out.println("Attribut "+s+" not found");
									}
									else
									{
										for (Attribute t : e.getAttributes().get(s)) {
											AttributeStyle style = null;
											DrawableAttribute datt = new DrawableAttribute(DrawableAttribute.Type.ENTITY, e.getName(), s, t);
											if(type==null){
												style = t.getTypeToDraw();
											}
											else{
												style = type;
												((DoubleAttribute) t).setTypeToDraw(type);
											}
											typeChart.put(t, style);
											atts.add(datt);
											listLxPlot.put(datt.getName()+datt.getAttribute().getName(),null);
										}
										if(!ans.contains("NOSYNCHR")){
											tmpList.add(s);
										}
									}
									if(!ans.contains("NOSYNCHR")){
										tmpMap.put(getSnapCol().getEntity(spl[1], getCurrentSnapNumber()), tmpList);
										charts.add(tmpMap);
									}
									draw(e,size,atts,type);
								}
							}

						}
					}
				}
			}
		};
		t.start();

	}

	//TODO
	/**
	 * Method used to draw a charts with a click
	 * @param e
	 * @param type
	 * @param size
	 */
	public void constructDraw(Entity e, AttributeStyle type,long size,boolean synchr){
		Map<Entity,List<String>> tmpMap = new HashMap<Entity,List<String>>();
		ArrayList<String> tmpList = new ArrayList<String>();
		ArrayList<DrawableAttribute> atts = new ArrayList<DrawableAttribute>();
		for(String s : e.getAttributes().keySet()){
			for (Attribute t : e.getAttributes().get(s)) {
				if(!(t.getValue() instanceof String)){
					AttributeStyle style = t.getTypeToDraw();
					DrawableAttribute datt = new DrawableAttribute(DrawableAttribute.Type.ENTITY, e.getName(), s, t);
					if(type !=null)
						style = type;
					atts.add(datt);
					typeChart.put(t, style);
				}
				tmpList.add(s);
			}
		}
		if(isSynch && synchr){
			tmpMap.put(e, tmpList);
		}
		charts.add(tmpMap);
		draw(e,100,atts,null);
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
					if(info != null)
						info.setVisible(true);
					else
						info = new InfoWindow(myWindow);
					isInfoWindowsOpened = true;
				}
				else{
					info.setVisible(false);
					isInfoWindowsOpened = false;
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
		ImageIcon iNext = new ImageIcon(LinksWindows.class.getResource("/icons/nextR.png"));;
		lblNext.setIcon(iNext);
		toolBar_1.add(lblNext);

		lblMoving = new JLabel("");

		lblMoving.setIcon(new ImageIcon(new ImageIcon(LinksWindows.class.getResource("/icons/moving.png")).getImage().getScaledInstance(iNext.getIconWidth(), iNext.getIconHeight(), Image.SCALE_DEFAULT)));
		lblMoving.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				moving = !moving;
				if(moving){
					frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
					drawing = false;
				}
				else
					frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		toolBar_1.add(lblMoving);

		lblDraw = new JLabel("");
		lblDraw.setIcon(new ImageIcon(LinksWindows.class.getResource("/icons/draw.png")));
		lblDraw.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				drawing = !drawing;
				if(drawing){
					frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
					moving = false;
				}
				else
					frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		toolBar_1.add(lblDraw);

		JButton lblSpeed = new JButton("Speed:");

		lblSpeed.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				txtSpeed.setEnabled(!txtSpeed.isEnabled());
			}
		});

		toolBar_1.add(lblSpeed);

		txtSpeed = new JTextField();
		txtSpeed.setHorizontalAlignment(SwingConstants.LEFT);
		txtSpeed.setText("250");
		txtSpeed.setEnabled(false);
		toolBar_1.add(txtSpeed);
		txtSpeed.setColumns(10);

		JButton lblFrameRate = new JButton("Frame Rate:");

		lblFrameRate.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				txtFramerate.setEnabled(!txtFramerate.isEnabled());
			}
		});

		toolBar_1.add(lblFrameRate);

		txtFramerate = new JTextField();
		txtFramerate.setText("1");
		txtFramerate.setEnabled(false);
		toolBar_1.add(txtFramerate);
		txtFramerate.setColumns(10);

		snapNumber = new JLabel("");
		toolBar_1.add(snapNumber);
		setSnapNumber(0);

		graphPanel = new JPanel();
		frame.getContentPane().add(graphPanel, BorderLayout.CENTER);
		graphPanel.setLayout(new BorderLayout(0, 0));

		//Give the shortcut
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
		.addKeyEventDispatcher(new KeyEventDispatcher(){
			public boolean dispatchKeyEvent(KeyEvent e){
				if(e.getID() == KeyEvent.KEY_PRESSED)
				{
					switch(e.getKeyCode()){
					case KeyEvent.VK_P:
						if(lblPlay.isEnabled())
							onPlayClick();
						break;
					case KeyEvent.VK_S:
						if(lblStop.isEnabled())
							onPauseClick();
						break;
					case KeyEvent.VK_D:
						drawing = !drawing;
						if(drawing){
							frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
						}
						else
							frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						break;
					case KeyEvent.VK_M:
						moving = !moving;
						if(moving){
							frame.setCursor(new Cursor(Cursor.HAND_CURSOR));
							drawing = false;
						}
						else
							frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						break;
					case KeyEvent.VK_N:
						switchToSnap(Math.max(Math.min(currentSnap + Integer.valueOf(txtFramerate.getText()),
								graph.getSnapCol().getMaxNum() - 1), 1));
						break;
					case KeyEvent.VK_B:
						switchToSnap(Math.max(1, currentSnap - Integer.valueOf(txtFramerate.getText())));
						notifyJump(Math.max(Math.min(currentSnap + Integer.valueOf(txtFramerate.getText()),
								graph.getSnapCol().getMaxNum() - 1), 1));
						break;
					case KeyEvent.VK_C:
						isSynch = !isSynch;
						lblSynch.setEnabled(isSynch);
						break;
					case KeyEvent.VK_I:
						if (!isInfoWindowsOpened) {
							if(info != null)
								info.setVisible(true);
							else
								info = new InfoWindow(myWindow);
							isInfoWindowsOpened = true;
						}
						else{
							info.setVisible(false);
							isInfoWindowsOpened = false;
						}
						break;
					default:
						break;
					}
				}
				return false;}});

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
		updateCharts(number);
		notifyDraw();
		this.currentSnap = number;
	}

	private void updateCharts(long number) {
		List<Map<Entity,List<String>>> tmpList = new ArrayList<Map<Entity,List<String>>>();
		for(Map<Entity,List<String>> h : this.charts){
			Map<Entity,List<String>> map = new HashMap<Entity,List<String>>();
			for(Entity e : h.keySet()){
				for(String s : e.getAttributes().keySet()){
					for(Attribute t : e.getAttributes().get(s)){
						
					}
				}
				Entity e1 = getSnapCol().getEntity(e.getName(), number);
				List<String> list = h.get(e);
				map.put(e1, list);
			}
			tmpList.add(map);
		}
		this.charts = tmpList;
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
	public synchronized void addSnapshot(Snapshot s) {
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

	public boolean getDrawing(){
		return this.drawing;
	}

	public void isDraw(){
		if(this.lblPlay.isEnabled())
			notifyDraw();
	}

	public void close(){
		this.frame.dispose();
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
	public synchronized void draw(Entity a,long drawSizeLong,  List<DrawableAttribute> atts,AttributeStyle type) {
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
					AttributeStyle style = null;
					String s = t.getAttribute().getName();
					Attribute theAttribute = t.getAttribute();
					if(typeChart.get(theAttribute) == null)
						style = theAttribute.getTypeToDraw();
					else{
						style = typeChart.get(theAttribute);
					}
					if (style == AttributeStyle.LINEAR || style == null) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " linear",
								ChartType.LINE).add(s, timei, (Double) theAttribute.getValue());
						this.listLxPlot.put(t.getName()+t.getAttribute().getName(),LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " linear",
								ChartType.LINE));
					}
					if (style == AttributeStyle.PIE) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " linear",
								ChartType.LINE).add(s, timei, (Double) theAttribute.getValue());
						this.listLxPlot.put(t.getName()+t.getAttribute().getName(),LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " linear",
								ChartType.LINE));
					}
					if (style == AttributeStyle.BAR) {
						if(theAttribute.getValue().getClass() != String.class)
							LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " bar",
									ChartType.BAR).add(s, timei, (Double) theAttribute.getValue());
						this.listLxPlot.put(t.getName()+t.getAttribute().getName(),LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " bar",
								ChartType.BAR));
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
							this.listLxPlot.put(t.getName()+t.getAttribute().getName(),LxPlot.getChart(
									t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVRT : " + s,
									ChartType.LINE));
						}
					}
					if (style == AttributeStyle.AVT) {
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVT",
								ChartType.LINE).add(s + "Value", timei, (Double) theAttribute.getValue());
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVT",
								ChartType.LINE).add(s + "Delta", timei, ((AVTAttribute) theAttribute).getDelta());
						this.listLxPlot.put(t.getName()+t.getAttribute().getName(),LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " AVT",
								ChartType.LINE));
					}
				}
			}
		}
		this.lastSnapNumDrawn = this.getCurrentSnapNumber();
	}

	//TODO
	/**
	 * Method use to refresh all charts who are synchr
	 */
	public void notifyDraw(){
		for(Map<Entity,List<String>> h : this.charts){
			for(Entity e : h.keySet()){
				tolook = new ArrayList<DrawableAttribute>();
				AttributeStyle style = null;
				for(String s : h.get(e)){
					ArrayList<Attribute> listTmp = new ArrayList<Attribute>();
					for (Attribute t : e.getAttributes().get(s)) {
						if(!(t instanceof StringAttribute)){
							DrawableAttribute datt = new DrawableAttribute(DrawableAttribute.Type.ENTITY, e.getName(), s, t);
							boolean alive = false;
								if(LxPlot.getCharts().containsValue(this.listLxPlot.get(datt.getName()+datt.getAttribute().getName()))){
										alive = true;
								}
							if(alive)
								tolook.add(datt);
							else
								listTmp.add(t);
						}
					}
					for(Attribute t : listTmp)
						e.getAttributes().get(s).remove(t);
				}
				draw(e,100,tolook,style);
			}
		}
	}

}
