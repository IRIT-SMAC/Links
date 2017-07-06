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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import fr.irit.smac.attributes.AVTAttribute;
import fr.irit.smac.attributes.DoubleAttribute;
import fr.irit.smac.attributes.DrawableAttribute;
import fr.irit.smac.attributes.StringAttribute;
import fr.irit.smac.core.AutoPlayThread;
import fr.irit.smac.core.DisplayedGraph;
import fr.irit.smac.core.Links;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.interfaces.ILxPlotChart;
import fr.irit.smac.model.Attribute;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.model.SnapshotsCollection;
import fr.irit.smac.model.Attribute.AttributeStyle;
import javax.swing.JSlider;
import javax.swing.JSeparator;

/**
 * LinksWindows: This class
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * @version 1.0
 * @since 10/03/2017
 *
 */
public class LinksWindows implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -562874670782175594L;

	/**
	 * The name of the experience.
	 */
	public String xpName;

	/**
	 * The main frame.
	 */
	private JFrame frame;

	/**
	 * A list of all AgentVizFrame alive.
	 */
	private ArrayList<AgentVizFrame> listAgent = new ArrayList<AgentVizFrame>();

	/**
	 * A list of all AgentVizFrame to remove.
	 */
	private ArrayList<AgentVizFrame> toRemove = new ArrayList<AgentVizFrame>();

	/**
	 * A list of all AgentVizFrame to add.
	 */
	private ArrayList<AgentVizFrame> toAdd = new ArrayList<AgentVizFrame>();

	/**
	 * The graph UI.
	 */
	private DisplayedGraph graph;

	/**
	 * The panel for the graph.
	 */
	private JPanel graphPanel;

	/**
	 * The view to interact with the user.
	 */
	private View view;

	/**
	 * The viewer from the graph.
	 */
	private Viewer viewer;

	/**
	 * This label will show the current snapnumber.
	 */
	private JLabel snapNumber;

	/**
	 * 
	 * The CliksPipe to understand the input.
	 */
	@SuppressWarnings("unused")
	private ClicksPipe clicksPipe;

	/**
	 * The label play.
	 */
	private JLabel lblPlay;

	/**
	 * The current snapshot number.
	 */
	private long currentSnap;

	/**
	 * If we are in synchronization or not.
	 */
	private boolean isSynch = true;

	/**
	 * The label to set the synchronization.
	 */
	private JLabel lblSynch;

	/**
	 * The link to the CSS for the graph.
	 */
	@SuppressWarnings("unused")
	private final String linkToCss;

	/**
	 * Boolean to know if the infoWindows is open.
	 */
	private boolean isInfoWindowsOpened = false;

	/**
	 * Boolean to know if the mouse move or not.
	 */
	private boolean mouseMove = false;

	/**
	 * Boolean to know if we are in loop.
	 */
	private boolean loop = false;

	/**
	 * The AutoPlayThread to update the window.
	 */
	private final AutoPlayThread autoPlayThread;

	/**
	 * Boolean to know if we are in state Moving.
	 */
	private boolean moving;

	/**
	 * Boolean to know if we are in state Drawing.
	 */
	private boolean drawing = false;

	/**
	 * The focus for the zoom, 1.0 is the max.
	 */
	private Double zoomFocus = 1.0;

	/**
	 * The window with all the relation.
	 */
	private RelationsVizFrame relationsWindow;

	private JLabel lblStop;
	private JTextField txtSpeed;
	private JTextField txtFramerate;
	private Links linksRef;
	private JLabel lblInfo;

	/**
	 * The InfoWindow.
	 */
	private InfoWindow info;
	private JLabel lblMoving;
	private JLabel lblDraw;

	/**
	 * The map of LxPlot to know which are still alive.
	 */
	private Map<String,ILxPlotChart> listLxPlot;
	
	/**
	 * Stock all the Entity with their attribute in a list.
	 */
	private List<Map<Entity,List<String>>> charts;
	
	/**
	 * The Map which correspond an Attribute and his style.
	 */
	private Map<Attribute,AttributeStyle> typeChart;
	
	/**
	 * The lastSnapshot where we draw.
	 */
	private long lastSnapNumDrawn = 0;

	private JLabel lblLinks;
	private JLabel lblZoomPlus;
	private JLabel lblZoomMinus;
	private JLabel lblResetZoom;
	private JLabel lblResetSnap;
	private JSlider slider;
	private JSeparator separator;
	private JButton btnLoop;

	/**
	 * A queue for the thread which load the graph.
	 */
	private Queue<Long> numberQueue = new LinkedList<Long>();

	/**
	 * Creates a new JFrame and start to display the experiment in parameter.
	 * 
	 * @param xpName
	 *            The name of the experiment to display.
	 * @param linkToCss
	 *            The path to the CSS file.
	 * @param links
	 *            A reference to the main application.
	 * @param visible
	 * 			  If the linksWindows will be visible.
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

		/**
		 * Thread use to load the graph
		 */
		/*Thread loadGraphThread = new Thread(){
			public void run(){
				while(true){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(!numberQueue.isEmpty()){
						Long numb = numberQueue.poll();
						if(numb != null){
							synchronized(graph){
								graph.loadGraph(numb);
							}
						}
					}
				}
			}
		};

		loadGraphThread.start();*/

		/**
		 * Thread used to understand the user's input
		 */
		Thread t = new Thread(){
			public void run(){
				System.out.println("Enter : 'NBSNAP for the number of snapshot'");
				System.out.println("Enter : 'SHOW <nameEntity> <Attribute1> <Attribute2> <AttributeN> <NOSYNCHR> <BAR/LIN/AVRT/AVT> <SIZE=N>' to show the graph (in case of blank put the name between simple quote)");
				System.out.println("Example : SHOW 'entity 1' attr1 'attr 2' BAR SIZE=300");
				System.out.println("NOSYNCHR is an option if you don't want the synchronisation of the chart");
				System.out.println("DEFAULT : LIN (or the style of the Attribute if it set) and Size=100");
				@SuppressWarnings("resource")
				Scanner sc = new Scanner(System.in);
				while(true){
					
					// The number of option ask by the users
					int option = 0;
					
					//The default size
					long size = 100;
					
					String ans = sc.nextLine();
					
					// Print the number of snapshot
					if(ans.equals("NBSNAP")){
						System.out.println("The number of snapshot is : " + getSnapCol().getMaxNum());
					}
					
					// If the user want to draw a chart
					if(ans.contains("SHOW ")){
						
						// If he asks for no synchronization we increment option
						if(ans.contains("NOSYNCHR"))
							option++;
						
						Map<Entity,List<String>> tmpMap = new HashMap<Entity,List<String>>();
						ArrayList<String> tmpList = new ArrayList<String>();
						
						// we split the answer 
						String[] spl = ans.split(" (?=(?:[^\']*\'[^\']*\')*[^\']*$)");
						
						// We remove the quotes
						for(int i =0; i<spl.length;i++){
							if(spl[i].contains("'"))
								spl[i] = spl[i].split("'")[1];
						}
						
						// We look for the style
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
						
						// We look for the entity
						Entity e = getSnapCol().getEntity(spl[1], getCurrentSnapNumber());
						if(e == null){
							System.out.println("Entity not found");
						}
						else{
							ArrayList<DrawableAttribute> atts = new ArrayList<DrawableAttribute>();
							// If no option we draw directly 
							if(spl.length-option == 2){
								constructDraw(e,type,size,!ans.contains("NOSYNCHR"));
							}
							else{
								// We look for the attributes
								for(int i = 2; i < spl.length-option; i++){
									String s = spl[i];
									if(e.getAttributes().get(s) == null){
										System.out.println("Attribut "+s+" not found");
									}
									else
									{
										// For all the attributes of the attribute we get the style 
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
											// We save the type of the chart
											typeChart.put(t, style);
											
											// add to the list to draw
											atts.add(datt);
											
											// Put the name in the list
											listLxPlot.put(datt.getName()+datt.getAttribute().getName(),null);
										}
										
										// We keep it for synchronization
										if(!ans.contains("NOSYNCHR")){
											tmpList.add(s);
										}
									}
									// Save the tmpList in the chart
									if(!ans.contains("NOSYNCHR")){
										tmpMap.put(getSnapCol().getEntity(spl[1], getCurrentSnapNumber()), tmpList);
										charts.add(tmpMap);
									}
									// Drawing of all attributes of atts
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

	/**
	 * Method used to draw a charts with a click when isDraw is true
	 * 
	 * @param e
	 * 		The Entity to observ
	 * 
	 * @param type
	 * 			The style of the chart
	 * 
	 * @param size
	 * 			The size of the chart
	 * 
	 * @param synchr
	 * 			If the chart will be update
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
		toolBar_1.addSeparator();

		lblLinks = new JLabel("");


		lblLinks.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				if(relationsWindow == null)
					relationsWindow = new RelationsVizFrame(myWindow);
				relationsWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
		toolBar_1.add(lblLinks);
		toolBar_1.addSeparator();

		lblSynch.setIcon(new ImageIcon(LinksWindows.class.getResource("/icons/synchronization.png")));
		toolBar_1.add(lblSynch);
		toolBar_1.addSeparator();
		toolBar_1.add(lblPlay);
		toolBar_1.addSeparator();
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
		toolBar_1.addSeparator();

		// Go to the previous snapshot if possible
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
		toolBar_1.addSeparator();

		// Go to the next snapshot if possible
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
		toolBar_1.addSeparator();

		// When trigger the user can move a node without open an AgentVizFrame
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

		btnLoop = new JButton("Loop ");
		btnLoop.setForeground(Color.BLACK);
		btnLoop.setFont(btnLoop.getFont().deriveFont(Font.BOLD));
		btnLoop.setBackground(Color.RED);
		btnLoop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				loop = !loop;
				if(loop){
					btnLoop.setBackground(Color.GREEN);
				}
				else
					btnLoop.setBackground(Color.RED);
			}

		});
		toolBar_1.add(btnLoop);

		separator = new JSeparator();
		toolBar_1.add(separator);
		toolBar_1.add(lblMoving);
		toolBar_1.addSeparator();

		// When trigger the cursor will change and the users can draw a chart on a click
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
		toolBar_1.addSeparator();

		JButton lblSpeed = new JButton("Speed:");

		lblSpeed.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				txtSpeed.setEnabled(!txtSpeed.isEnabled());
			}
		});

		//Use the method zoomPlus
		lblZoomPlus = new JLabel("Zoom+ ");
		lblZoomPlus.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				zoomPlus();
			}
		});
		toolBar_1.add(lblZoomPlus);
		toolBar_1.addSeparator();

		// Use the method zoomMinus
		lblZoomMinus = new JLabel("Zoom - ");
		lblZoomMinus.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				zoomMinus();
			}
		});
		toolBar_1.add(lblZoomMinus);
		toolBar_1.addSeparator();

		// Reset the zoom to be on the center and 100%
		lblResetZoom = new JLabel("ResetZoom");
		lblResetZoom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e){
				zoomFocus = 1.0;
				view.getCamera().setViewPercent(zoomFocus);
				view.getCamera().setViewCenter(1, 1, 0);
				slider.setValue(100);
			}
		});
		toolBar_1.add(lblResetZoom);
		toolBar_1.addSeparator();

		// Set the snapnumber to 1
		lblResetSnap = new JLabel("ResetSnap");
		lblResetSnap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e){
				for(int i =0; i < 5;i++)
					switchToSnap(1);
			}
		});
		toolBar_1.add(lblResetSnap);
		toolBar_1.addSeparator();

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
		lblLinks.setIcon(new ImageIcon(new ImageIcon(LinksWindows.class.getResource("/icons/line.png")).getImage().getScaledInstance(iNext.getIconWidth(), iNext.getIconHeight(), Image.SCALE_DEFAULT)));

		txtFramerate = new JTextField();
		txtFramerate.setText("1");
		txtFramerate.setEnabled(false);
		toolBar_1.add(txtFramerate);
		txtFramerate.setColumns(10);

		snapNumber = new JLabel("");
		toolBar_1.add(snapNumber);
		setSnapNumber(0);

		graphPanel = new JPanel();
		graphPanel.addMouseListener(new MouseAdapter() {
		});
		frame.getContentPane().add(graphPanel, BorderLayout.CENTER);
		graphPanel.setLayout(new BorderLayout(0, 0));

		// Add a slider to zoom
		slider = new JSlider();
		slider.setValue(100);
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				zoomFocus = slider.getValue()/100.0;
				view.getCamera().setViewPercent(zoomFocus);
			}
		});
		graphPanel.add(slider, BorderLayout.SOUTH);

		//Give a shortcut for the label
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
					case KeyEvent.VK_L:
						if(relationsWindow != null)
							relationsWindow.notifyJump();
						break;
					case KeyEvent.VK_Z:
						zoomPlus();
						break;
					case KeyEvent.VK_A:
						zoomMinus();
						break;
					default:
						break;
					}
				}
				return false;}});
		generateGraph();
		view.addMouseListener(new ViewMouseListener());
		graphPanel.addMouseWheelListener(new MouseWheelListener(){

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation() < 0 ){
					zoomPlus();
				}
				else{
					zoomMinus();
				}
			}

		});
	}
	private class ViewMouseListener extends MouseAdapter{
		Point3 point = null;
		@Override
		public void mousePressed(MouseEvent e){
			point = view.getCamera().transformPxToGu(e.getX(), e.getY());
		}

		@Override
		public void mouseReleased(MouseEvent e){
			Point3 orx = view.getCamera().transformPxToGu(e.getX(), e.getY());
			if((point.x != orx.x || point.y !=orx.y) && !moving && !mouseMove){
				double newx =(point.x+orx.x)/2;
				double newy =(point.y+orx.y)/2;
				view.getCamera().setViewCenter(newx,newy, 0);
				zoomPlus();
			}
		}
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
		if(this.currentSnap == this.getMaxSnapNumber()-1 && loop)
			number = 1;
		//TODO
		numberQueue.offer(number);
		boolean res = graph.loadGraph(number);
		if(!res)
			this.viewer.disableAutoLayout();
		setSnapNumber(number);
		notifyJump(number);
		updateCharts(number);
		notifyDraw();
		this.currentSnap = number;

	}

	/**
	 * Update the list of chart to update
	 * 
	 * @param number
	 * 		The Snapshot number
	 */
	private void updateCharts(long number) {
		List<Map<Entity,List<String>>> tmpList = new ArrayList<Map<Entity,List<String>>>();
		for(Map<Entity,List<String>> h : this.charts){
			Map<Entity,List<String>> map = new HashMap<Entity,List<String>>();
			for(Entity e : h.keySet()){
				for(String s : e.getAttributes().keySet()){
					for(@SuppressWarnings("unused") Attribute t : e.getAttributes().get(s)){

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

	/**
	 * Create the Graph
	 * Create the viewer and the view 
	 * Create the CliksPipe
	 */
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

	/**
	 * Get the value of loop
	 * 
	 * @return loop
	 */
	public boolean getLoop(){
		return this.loop;
	}

	@SuppressWarnings("unused")
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

	/**
	 *  Add All the new AgentVizFrame to listAgent
	 *  Notify all AgentVizFrame
	 *  Remove all dead AgentVizFrame from listAgent and close them
	 *  Notify the RelationWindow
	 *  
	 * @param number
	 * 			The Snapshot number
	 */
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
		if(this.relationsWindow != null)
			this.relationsWindow.notifyJump();
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

	@SuppressWarnings("unused")
	private void removeEntities(Snapshot s){
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
	 * set isInfoWindowsOpened to false
	 */
	public void inforInfoWindowsClosing() {
		this.isInfoWindowsOpened = false;
	}

	/**
	 * Return the viewer
	 * 
	 * @return viewer
	 */
	public Viewer getViewer() {
		return viewer;
	}

	/**
	 * Return the xp name
	 * 
	 * @return xpName
	 */
	public String getXpName() {
		return xpName;
	}

	/**
	 * Return true if we are in state Moving
	 * 
	 * @return moving
	 */
	public boolean getMoving(){
		return this.moving;
	}

	/**
	 * Return true if we are in state Drawing
	 * 
	 * @return drawing
	 */
	public boolean getDrawing(){
		return this.drawing;
	}


	/**
	 * Dispose the frame
	 */
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
	 * @param type
	 * 		The type of the chart
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
						LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " pie",
								ChartType.PIE).add(s, timei, (Double) theAttribute.getValue());
						this.listLxPlot.put(t.getName()+t.getAttribute().getName(),LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " linear",
								ChartType.PIE));
					}
					if (style == AttributeStyle.BAR) {
						if(theAttribute.getValue().getClass() != String.class){
							Entity b = this.getSnapshotsCollection().getSnaptshot(this.currentSnap-1).getEntity(a.getName());
							LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " bar",
									ChartType.BAR).add(s+1, 0, (Double) b.getAttributesWithName(theAttribute.getName()).getValue());
							LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " bar",
									ChartType.BAR).add(s+2, 1, (Double) theAttribute.getValue());
							this.listLxPlot.put(t.getName()+t.getAttribute().getName(),LxPlot.getChart(t.getType() + ">" + t.getName() + ":" + t.getCaracList() + ":" + " bar",
									ChartType.BAR));
						}
					}
					if (style == AttributeStyle.AVRT) {
						Double tab[] = (Double[]) theAttribute.getValue();
						for (@SuppressWarnings("unused") Double val : tab) {
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

	/**
	 * Method use to refresh all charts who are synchr
	 */
	public void notifyDraw(){
		for(Map<Entity,List<String>> h : this.charts){
			for(Entity e : h.keySet()){
				ArrayList<DrawableAttribute> tolook = new ArrayList<DrawableAttribute>();
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

	/**
	 * Method used to zoom forward
	 */
	public void zoomPlus() {
		if(zoomFocus !=0.1)
			zoomFocus -= 0.05;
		view.getCamera().setViewPercent(zoomFocus);
		Double sl = zoomFocus*100;
		this.slider.setValue(sl.intValue());

	}

	/**
	 * Method used to zoom backward
	 */
	public void zoomMinus() {
		if(zoomFocus !=1)
			zoomFocus += 0.05;
		view.getCamera().setViewPercent(zoomFocus);
		Double sl = zoomFocus*100;
		slider.setValue(sl.intValue());

	}

	/**
	 * Set mouseMove
	 * 
	 * @param mouseMove
	 * 			boolean
	 */
	public void setMouseMove(boolean mouseMove){
		this.mouseMove = mouseMove;
	}
	
}
