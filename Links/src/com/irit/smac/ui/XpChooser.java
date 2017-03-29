package com.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.bson.Document;

import com.irit.smac.core.Links;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class XpChooser extends JFrame {

	private JPanel contentPane;
	private JList<String> list;
	private Links linksRef;
	private NewXpWindows xpWindows;

	/**
	 * Create the frame.
	 * 
	 * @param links
	 */
	public XpChooser(Links links) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				Links.mongoClient.close();
			}
		});
		this.linksRef = links;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 190, 522);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JToolBar toolBar = new JToolBar();
		contentPane.add(toolBar, BorderLayout.NORTH);

		JLabel lblAdd = new JLabel("");
		XpChooser ref = this;
		lblAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				xpWindows = new NewXpWindows(ref);
			}
		});
		lblAdd.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/plus.png")));
		toolBar.add(lblAdd);

		JLabel lblRemove = new JLabel("");
		lblRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (arg0.getSource().equals(lblRemove)) {
					String xpName = (String) list.getSelectedValue();
					if (xpName != null) {
						destroyExperiment(xpName);
					}
				}
			}
		});
		lblRemove.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/minus.png")));
		toolBar.add(lblRemove);

		JLabel lblPlay = new JLabel("");
		lblPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (list.getSelectedValue() != null) {
					String xpName = (String) list.getSelectedValue();
					if (xpName != null) {
						String linkToCss = Links.getCssFilePathFromXpName(xpName);
						links.createNewLinksWindows(xpName, linkToCss);
					}
				}
			}

		});

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource().equals(lblNewLabel)) {
					if (list.getSelectedValue() != null) {
						String xpName = (String) list.getSelectedValue();
						if (xpName != null) {
							xpWindows = new NewXpWindows(XpChooser.this,xpName);
						}
					}
				}
			}
		});
		lblNewLabel.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/edit.png")));
		toolBar.add(lblNewLabel);
		lblPlay.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/play.png")));
		toolBar.add(lblPlay);

		list = new JList();
		init();
		this.setVisible(true);
	}

	protected void destroyExperiment(String xpName) {
		Links.database.getCollection(xpName).drop();
		Links.database.getCollection(Links.collectionNameExperimentList).findOneAndDelete(Filters.eq("xpName", xpName));
	}

	

	private void init() {
		Vector<String> v = new Vector<String>();
		MongoCollection<Document> maCollection = Links.database.getCollection(Links.collectionNameExperimentList);

		for (Document document : maCollection.find()) {
			Iterator<Entry<String, Object>> it = document.entrySet().iterator();
			String id = (String) it.next().getValue().toString();
			String xpName = (String) it.next().getValue();
			v.addElement(xpName);
		}

		list = new JList<String>(v);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contentPane.add(list, BorderLayout.CENTER);

		JLabel lblTxt = new JLabel("Select or create your experiment");
		lblTxt.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblTxt, BorderLayout.SOUTH);
	}

	public void redrawList() {
		xpWindows = null;

		DefaultListModel<String> v = new DefaultListModel<String>();
		MongoCollection<Document> maCollection = Links.database.getCollection(Links.collectionNameExperimentList);

		for (Document document : maCollection.find()) {
			Iterator<Entry<String, Object>> it = document.entrySet().iterator();
			String id = (String) it.next().getValue().toString();
			String xpName = (String) it.next().getValue();
			System.out.println(xpName);
			v.addElement(xpName);
		}

		list.setModel(v);
	}

}
