package fr.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import fr.irit.smac.core.Links;

import java.awt.Font;

public class XpChooser extends JFrame {

	private JPanel contentPane;
	private JList<String> list;
	private Links linksRef;
	private NewXpWindows xpWindows;

	/**
	 * Create the frame.
	 * 
	 * @param links
	 *            The reference to the links window.
	 */
	public XpChooser(Links links) {
		setTitle("Links: Xp Chooser");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				Links.mongoClient.close();
			}
		});
		this.linksRef = links;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 250);
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
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(null,
							"Would You Like to Completly Delete the Experiment Entitled : " + list.getSelectedValue()
									+ " ?",
							"Warning", dialogButton);
					if (dialogResult == JOptionPane.YES_OPTION) {
						String xpName = (String) list.getSelectedValue();
						if (xpName != null) {
							destroyExperiment(xpName);
						}
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
							xpWindows = new NewXpWindows(XpChooser.this, xpName);
						}
					}
				}
			}
		});

		JLabel lblErase = new JLabel("");
		lblErase.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				int dialogButton = JOptionPane.YES_NO_OPTION;
				int dialogResult = JOptionPane.showConfirmDialog(null,
						"Would You Like to Drop the Experiment Entitled : " + list.getSelectedValue() + " ?", "Warning",
						dialogButton);

				if (dialogResult == JOptionPane.YES_OPTION) {
					String xpName = (String) list.getSelectedValue();
					drop(xpName);
				}

			}
		});
		lblErase.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/eraser.png")));
		toolBar.add(lblErase);
		lblNewLabel.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/edit.png")));
		toolBar.add(lblNewLabel);
		lblPlay.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/play.png")));
		toolBar.add(lblPlay);

		list = new JList();
		init();
		this.setVisible(true);
	}

	public static void delete(String xpName) {
		Links.database.getCollection(xpName).drop();
		Links.database.getCollection(Links.collectionNameExperimentList).findOneAndDelete(Filters.eq("xpName", xpName));
	}

	public static void create(String xpName) {
		MongoCollection<Document> collection = Links.database.getCollection(Links.collectionNameExperimentList);
		collection.deleteMany(Filters.eq("xpName", xpName));
		String cssLink = "graphStream.css";
//		if (Links.existsExperiment(xpName)) {
			cssLink = Links.getCssFilePathFromXpName(xpName);
//		} else {
			System.out.println("coucou2");
			collection.insertOne(new Document("xpName", xpName).append("cssFile", cssLink));
//		}

		MongoCollection<Document> collection2 = Links.database.getCollection(xpName);
		collection2.deleteMany(Filters.eq("xpName", xpName));
		collection2.insertOne(new Document("xpName", xpName).append("maxNum", 0));
	}
	
	public static void create(String xpName,String cssPath) {
		MongoCollection<Document> collection = Links.database.getCollection(Links.collectionNameExperimentList);
		collection.deleteMany(Filters.eq("xpName", xpName));
		String cssLink = "graphStream.css";
		collection.insertOne(new Document("xpName", xpName).append("cssFile", cssPath));

		MongoCollection<Document> collection2 = Links.database.getCollection(xpName);
		collection2.deleteMany(Filters.eq("xpName", xpName));
		collection2.insertOne(new Document("xpName", xpName).append("maxNum", 0));
	}

	public static void drop(String xpName) {
		MongoCollection<Document> collection = Links.database.getCollection(Links.collectionNameExperimentList);
		MongoCollection<Document> collection2 = Links.database.getCollection(xpName);
		collection2.drop();
		collection2.insertOne(new Document("xpName", xpName).append("maxNum", 0));
	}

	protected void destroyExperiment(String xpName) {
		delete(xpName);
		this.redrawList();
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
		list.setFont(new Font("Tahoma", Font.PLAIN, 14));
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
			v.addElement(xpName);
		}

		list.setModel(v);
	}

}
