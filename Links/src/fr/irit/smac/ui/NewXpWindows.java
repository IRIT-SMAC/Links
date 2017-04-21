package fr.irit.smac.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import fr.irit.smac.core.Links;

public class NewXpWindows extends JFrame {

	private JPanel contentPane;
	private JTextField txtLinkToCss;
	private JTextField txtXpName;

	private XpChooser xpRef;
	private JButton btnCreate;
	private final JFileChooser fc = new JFileChooser();

	public void init() {
		setTitle("Create a new experiment");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 700, 150);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtLinkToCss = new JTextField();
		txtLinkToCss.setBounds(273, 42, 304, 20);
		contentPane.add(txtLinkToCss);
		txtLinkToCss.setColumns(10);

		JButton btnNewButton = new JButton("Load");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getSource().equals(btnNewButton)) {
					int returnVal = fc.showOpenDialog(NewXpWindows.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						txtLinkToCss.setText(file.getAbsolutePath());
					}
				}
			}
		});
		btnNewButton.setBounds(590, 41, 70, 23);
		contentPane.add(btnNewButton);

		txtXpName = new JTextField();
		txtXpName.setBounds(273, 11, 304, 20);
		contentPane.add(txtXpName);
		txtXpName.setColumns(10);

		JLabel lblExperimentName = new JLabel("Experiment Name :");
		lblExperimentName.setBounds(10, 14, 256, 14);
		contentPane.add(lblExperimentName);

		JLabel lblPathToCss = new JLabel("Path to css file      :");
		lblPathToCss.setBounds(10, 45, 100, 14);
		contentPane.add(lblPathToCss);

		btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(btnCreate)) {
					if (btnCreate.getText().equals("Create")) {
						create();
					} else {
						update();
					}
				}
			}

		});
		btnCreate.setBounds(283, 77, 89, 23);
		contentPane.add(btnCreate);
		this.setVisible(true);
	}

	private void update() {
		MongoCollection<Document> collection = Links.database.getCollection(Links.collectionNameExperimentList);
		String xpName = this.txtXpName.getText();
		collection.deleteMany(Filters.eq("xpName", xpName));
		collection.insertOne(new Document("xpName", xpName).append("cssFile", this.txtLinkToCss.getText()));
		xpRef.redrawList();
		this.setVisible(false);
	}

	public NewXpWindows(XpChooser ref, String xpName) {
		this.xpRef = ref;
		init();
		this.txtXpName.setText(xpName);
		this.txtLinkToCss.setText(Links.getCssFilePathFromXpName(xpName));
		this.btnCreate.setText("Update");
	}

	/**
	 * Create the frame.
	 * 
	 * @param ref
	 *            The reference to the XpChooser window.
	 */
	public NewXpWindows(XpChooser ref) {
		this.xpRef = ref;
		init();
	}

	private void create() {
		String xpName = this.txtXpName.getText();
		String cssPath = this.txtLinkToCss.getText();
		XpChooser.create(xpName, cssPath);

		xpRef.redrawList();
		this.setVisible(false);
	}
}
