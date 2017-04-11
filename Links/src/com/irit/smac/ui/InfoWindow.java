package com.irit.smac.ui;

import java.awt.BorderLayout;
import static org.graphstream.algorithm.Toolkit.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import org.graphstream.graph.Graph;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InfoWindow extends JFrame {

	private JPanel contentPane;

	private LinksWindows links;
	
	private JTextPane textPane;

	/**
	 * Create the frame.
	 * 
	 * @param linksWindows The reference to the links window.
	 */
	public InfoWindow(LinksWindows linksWindows) {
		setTitle("Statistic Window");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				links.inforInfoWindowsClosing();
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		links = linksWindows;
		setBounds(100, 100, 220, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		textPane = new JTextPane();
		contentPane.add(textPane, BorderLayout.CENTER);
		this.buildText();
		this.setVisible(true);
	}

	public void buildText() {
		Graph g = links.getDisplayedGraph().getGraph();
		String s = "";
		s = s + "Number of nodes : " + g.getNodeCount() + "\n";
		s = s + "Number of relations : " + g.getEdgeCount() + "\n";
		s = s + "Density : " + density(g) + "\n";
		s = s + "Diameter : " + diameter(g) + "\n";
		textPane.setText(s);
	}

}
