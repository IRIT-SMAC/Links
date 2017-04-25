package fr.irit.smac.core;

import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.UIManager;

import org.bson.Document;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import fr.irit.smac.model.Snapshot;
import fr.irit.smac.ui.LinksWindows;
import fr.irit.smac.ui.XpChooser;

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
	
	private XpChooser xpChooser;

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

		xpChooser = new XpChooser(this);
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

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
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

		xpChooser = new XpChooser(this);

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
		xpChooser = new XpChooser(this);
	}
	
	/**
	 * Get the displayed graph (to access advanced graphstream options).
	 * @return The currently displayed graph.
	 */
	public Graph getGraph(){
		if(linksWindow!=null){
			return linksWindow.getDisplayedGraph().getGraph();
		}else{
			return null;
		}
	}
	
	/**
	 * Get the graph view (to access advanced graphstream options).
	 * @return The currently displayed graph.
	 */
	public Viewer getGraphView(){
		if(linksWindow!=null){
			return linksWindow.getViewer();
		}else{
			return null;
		}
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
	 * Update the current graph to visualize the snapshot.
	 * @param s The snapshot to view.
	 */
	public void viewSnapshot(Snapshot s){
		if (linksWindow != null) {
			linksWindow.getDisplayedGraph().viewSnapshot(s);
		}
	}

	/**
	 * Create a new experiment with the given name. Drop if any other experiment
	 * with the same name already exists.
	 * 
	 * @param xpName
	 *            The name of the experiment
	 */
	public void createExperiment(String xpName) {
		xpChooser.create(xpName);
	}

	/**
	 * Delete an experiment with the given name.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 */
	public void deleteExperiment(String xpName) {
		xpChooser.delete(xpName);
	}

	/**
	 * Test if an experiment with the given name has been created.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 * @return True if the experiment exists, false otherwise.
	 */
	public boolean existsExperiment(String xpName) {
		MongoCollection<Document> maCollection = Links.database.getCollection(Links.collectionNameExperimentList);
		Document myXP = maCollection.find(Filters.eq("xpName", xpName)).first();
		if (myXP != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Drop the experiment with the given name and reset the current snapNumber at 0.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 */
	public void dropExperiment(String xpName) {
		xpChooser.drop(xpName);
		this.linksWindow.getDisplayedGraph().resetSnapNumber();
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