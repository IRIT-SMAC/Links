package com.irit.smac.core;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.bson.Document;

import com.irit.smac.model.Snapshot;
import com.irit.smac.ui.LinksWindows;
import com.irit.smac.ui.XpChooser;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

/**
 * Links: A tool to visualize agents and their relations over time.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * @version 1.0
 * @since 29/03/2017
 *
 */
/**
 * @author Bob
 *
 */
public class Links {

	/**
	 * Name of the MongoDB data base used by the application.
	 */
	private static final String dataBaseName = "LinksDataBase";

	/**
	 * Name of the MongoDB collection used by the application to list the
	 * experiments name and path to CSS files.
	 */
	public static final String collectionNameExperimentList = "xpList";

	/**
	 * The MongoClient. By default, its connects to the local host.
	 */
	public static MongoClient mongoClient;

	/**
	 * The MongoDataBase for the Links application.
	 */
	public static MongoDatabase database;

	/**
	 * The main UI windows.
	 */
	private LinksWindows linksWindow;

	/**
	 * Main Launch to start the standalone application.
	 * 
	 * @param args
	 *            A list of empty arguments. Not used.
	 */
	public static void main(String args[]) {
		Links links = new Links();
	}

	/**
	 * Creates a new Links instance connection to the localhost and default port
	 * of MongoDB. This constructor enables to start the application with the
	 * selection of the experiment name.
	 */
	public Links() {
		setLookAndFeel();
		mongoClient = new MongoClient();
		database = mongoClient.getDatabase(dataBaseName);

		XpChooser xpChooser = new XpChooser(this);
	}

	/**
	 * Creates a new Links instance connection to the localhost and default port
	 * of MongoDB. This constructor intializes the experiment to the name passed
	 * in parameter.
	 * 
	 * @param xpName
	 *            The name of the experiment to use. If an experiment with this
	 *            name already exists, the application restore the previously
	 *            loaded data.
	 * 
	 */
	public Links(String xpName) {
		setLookAndFeel();
		mongoClient = new MongoClient();
		database = mongoClient.getDatabase(dataBaseName);

		// XpChooser xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			System.out.println("TOTOTO");
			createExperiment(xpName);
		}

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName));
	}

	/**
	 * Creates a new Links instance connection to the specified address of
	 * MongoDB. This constructor intialise the experiment to the name passed in
	 * parameter.
	 * 
	 * @param addr
	 *            The ServerAddress of the MongoDB database.
	 * @param xpName
	 *            The name of the experiment to use. If an experiment with this
	 *            name already exists, the application restore the previously
	 *            loaded data.
	 * 
	 */
	public Links(ServerAddress addr, String xpName) {
		setLookAndFeel();
		mongoClient = new MongoClient(addr);
		database = mongoClient.getDatabase(dataBaseName);

		XpChooser xpChooser = new XpChooser(this);

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName));
	}

	/**
	 * Creates a new Links instance connection to the specified address of
	 * MongoDB. This constructor intializes the experiment to the name passed in
	 * parameter.
	 * 
	 * @param addr
	 *            The ServerAddress of the MongoDB database.
	 * 
	 */
	public Links(ServerAddress addr) {
		setLookAndFeel();
		mongoClient = new MongoClient(addr);
		database = mongoClient.getDatabase(dataBaseName);
		XpChooser xpChooser = new XpChooser(this);
	}

	/**
	 * Add a new Snapshot to the model. The number of this snapshot is
	 * automatically choose.
	 * 
	 * @param s
	 *            The snapshot to add.
	 */
	public void addSnapshot(Snapshot s) {
		if (linksWindow != null) {
			linksWindow.addSnapshot(s);
		}
	}

	/**
	 * Create a new experiment with the given name. Drop if any other experiment
	 * with the same name already exists.
	 * 
	 * @param xpName
	 *            The name of the experiment
	 */
	public static void createExperiment(String xpName) {
		XpChooser.create(xpName);
	}

	/**
	 * Delete an experiment with the given name.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 */
	public static void deleteExperiment(String xpName) {
		XpChooser.delete(xpName);
	}

	/**
	 * Test if an experiment with the given name has been created.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 * @return True if the experiment exists, false otherwise.
	 */
	public static boolean existsExperiment(String xpName) {
		MongoCollection<Document> maCollection = Links.database.getCollection(Links.collectionNameExperimentList);
		Document myXP = maCollection.find(Filters.eq("xpName", xpName)).first();
		if (myXP != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Drop the experiment with the givent name.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 */
	public void dropExperiment(String xpName) {
		XpChooser.drop(xpName);
	}

	/**
	 * Initialize the vizualization windows on the specficied experiment using
	 * the specified CSS file.
	 * 
	 * @param xpName
	 *            The name of the experiment to vizualize.
	 * @param linkToCss
	 *            The path to the CSS file.
	 */
	public void createNewLinksWindows(String xpName, String linkToCss) {
		linksWindow = new LinksWindows(xpName, linkToCss, this);
	}

	/**
	 * Release memory when a vizualisation windows is closed.
	 */
	public void informClose() {
		linksWindow = null;
	}

	/**
	 * Set look and feel according to the OS.
	 */
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Static method which gets the CSS path file associated to an experiment in
	 * the MongoDB database.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 * @return The path to the CSS file.
	 */
	public static String getCssFilePathFromXpName(String xpName) {
		MongoCollection<Document> maCollection = Links.database.getCollection(Links.collectionNameExperimentList);
		Document myXP = maCollection.find(Filters.eq("xpName", xpName)).first();
		Iterator<Entry<String, Object>> it = myXP.entrySet().iterator();
		it.next(); // Skip first
		it.next(); // Skip second
		return it.next().getValue().toString();
	}

}
