package com.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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

import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import com.irit.smac.core.AutoPlayThread;
import com.irit.smac.core.DisplayedGraph;
import com.irit.smac.model.Snapshot;
import com.irit.smac.model.SnapshotsCollection;

/**
 * Links: A tool to visualize agents and their relations over time.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * @version 1.0
 * @since 10/03/2017
 *
 */
public class LinksApplication implements Serializable {

	private JFrame frame;

	private ArrayList<AgentVizFrame> listAgent = new ArrayList<AgentVizFrame>();

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

	private final AutoPlayThread autoPlayThread;

	private JLabel lblStop;
	private JTextField txtSpeed;
	private JTextField txtFramerate;
	private JFileChooser fc = new JFileChooser();

	/**
	 * Creates the application and displays the associated JFrame.
	 */
	public LinksApplication(String linkToCss) {
		this.linkToCss = linkToCss;
		autoPlayThread = new AutoPlayThread(this);
		SnapshotsCollection snapCol = new SnapshotsCollection();
		graph = new DisplayedGraph(snapCol, linkToCss);
		snapCol.setLinksWindows(this);
		initialize();
		this.frame.setVisible(true);
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
		frame.getContentPane().setBackground(SystemColor.inactiveCaption);
		frame.setAutoRequestFocus(false);
		frame.setBounds(200, 200, 900, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Links : Vizualizing agents' life");
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.WHITE);
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.setBackground(Color.WHITE);
		menuBar.add(mnFile);

		JMenuItem mntmLoad = new JMenuItem("Load");
		mntmLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource() == mntmLoad) {
					int returnVal = fc.showSaveDialog(fc);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						// This is where a real application would save the file.
						// System.err.println("Saving: " + file.getName() +
						// ".");
						try {
							FileInputStream fin = new FileInputStream(file.getAbsolutePath());
							ObjectInputStream ois = new ObjectInputStream(fin);
							graph.getSnapCol().setCollection((HashMap<Long, Snapshot>) ois.readObject());
							switchToSnap(1);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						// System.err.println("Save command cancelled by
						// user.");
					}
				}
			}
		});
		mnFile.add(mntmLoad);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == mntmSave) {
					int returnVal = fc.showSaveDialog(fc);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						// This is where a real application would save the file.
						// System.err.println("Saving: " + file.getName() +
						// ".");
						try {
							FileOutputStream fout = new FileOutputStream(file.getAbsolutePath());
							ObjectOutputStream oos = new ObjectOutputStream(fout);
							oos.writeObject(graph.getSnapCol().getCollection());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else {
						// System.err.println("Save command cancelled by
						// user.");
					}
				}
			}
		});
		mnFile.add(mntmSave);

		JMenu mnAbout = new JMenu("?");
		mnAbout.setBackground(Color.WHITE);
		menuBar.add(mnAbout);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(SystemColor.inactiveCaption);
		toolBar.setRollover(true);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

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
		lblSynch.setIcon(new ImageIcon(LinksApplication.class.getResource("/icons/synchronization.png")));
		toolBar_1.add(lblSynch);
		toolBar_1.add(lblPlay);
		lblPlay.setIcon(new ImageIcon(LinksApplication.class.getResource("/icons/play.png")));

		lblStop = new JLabel("");
		lblStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				onPauseClick();
			}
		});
		lblStop.setEnabled(false);
		lblStop.setIcon(new ImageIcon(LinksApplication.class.getResource("/icons/stop.png")));
		toolBar_1.add(lblStop);

		JLabel lblPrev = new JLabel("");
		lblPrev.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				switchToSnap(Math.max(1, currentSnap - Integer.valueOf(txtSpeed.getText())));
			}
		});
		lblPrev.setIcon(new ImageIcon(LinksApplication.class.getResource("/icons/backL.png")));
		toolBar_1.add(lblPrev);

		JLabel lblNext = new JLabel("");
		lblNext.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				switchToSnap(Math.min(currentSnap + Integer.valueOf(txtSpeed.getText()),
						graph.getCurrentSnap().getMaxNum() - 1));
			}
		});
		lblNext.setIcon(new ImageIcon(LinksApplication.class.getResource("/icons/nextR.png")));
		toolBar_1.add(lblNext);

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

		snapNumber = new JLabel("New label");
		toolBar_1.add(snapNumber);
		setSnapNumber(0);

		graphPanel = new JPanel();
		frame.getContentPane().add(graphPanel, BorderLayout.CENTER);
		graphPanel.setLayout(new BorderLayout(0, 0));
		generateGraph();
	}

	/**
	 * Not implemented.
	 */
	public void onPlayClick() {
		autoPlayThread.setActivated(true);
		autoPlayThread.setFrameRateAndSpeed(Integer.valueOf(this.txtFramerate.getText()),
				Integer.valueOf(this.txtSpeed.getText()));
		this.lblPlay.setEnabled(false);
		this.lblStop.setEnabled(true);
	}

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
		return graph.getCurrentSnap();
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
	 * This methode is called to inform Links that a new snapshot is availabe.
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

	private void notifyJump(long number) {
		for (AgentVizFrame a : listAgent) {
			a.notifyJump(number);
		}
	}

	/**
	 * Add a new observer to the Frame.
	 * 
	 * @param aviz
	 *            The reference to the new observer.
	 */
	public void registerObserver(AgentVizFrame aviz) {
		this.listAgent.add(aviz);
	}

	/**
	 * Remove an observer.
	 * 
	 * @param me
	 *            The reference of the observer to remove.
	 */
	public void unregisterObserver(AgentVizFrame me) {
		listAgent.remove(me);
	}

	/**
	 * Add a new snapshot to the collection.
	 * 
	 * @param s
	 *            The snapshot to be added.
	 */
	public void addSnapshot(Snapshot s) {
		graph.getSnapCol().addSnapshot(s);
	}

	public long getMaxSnapNumber() {
		return graph.getSnapCol().getMaxNum();
	}
}
