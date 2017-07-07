package fr.irit.smac.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.bson.Document;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import fr.irit.smac.attributes.AVTAttribute;
import fr.irit.smac.attributes.DrawableAttribute;
import fr.irit.smac.attributes.DrawableAttribute.Type;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.server.LxPlotChart;
import fr.irit.smac.model.Attribute;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Relation;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.model.Attribute.AttributeStyle;
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
public class Links implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8051187441385020519L;

	/**
	 * Name of the MongoDB data base used by the application.
	 */
	public static final String dataBaseName = "LinksDataBase";

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
	 * The mongoPath if we want to execute it
	 */
	public String mongoPath;

	/**
	 * The path of the config
	 */
	public String mongoConfig;

	/**
	 * The file which will store the path of mongoDB
	 */
	private String resMong = "setMongo.txt";

	/**
	 * The main UI windows.
	 */
	private LinksWindows linksWindow;

	private XpChooser xpChooser;
	
	private String currentXP;
	
	private Map<String,LinksWindows> windows = new HashMap<String,LinksWindows>();



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
		lireMongoPath();
		initMongoConnection();
		xpChooser = new XpChooser(this);
		xpChooser.redrawList();
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
		lireMongoPath();
		initMongoConnection();

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			createExperiment(xpName);
		}

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName),true);
		xpChooser.redrawList();
		this.currentXP = xpName;
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
	 * @Param pathCss 
	 * 		  	  The path to the css file
	 * 
	 */
	public Links(String xpName, String pathCss) {
		setLookAndFeel();
		lireMongoPath();
		initMongoConnection();

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			createExperiment(xpName,pathCss);
		}

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName),true);
		xpChooser.redrawList();
		this.currentXP = xpName;
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
		lireMongoPath();
		initMongoConnection(addr);

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			createExperiment(xpName);
		}

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName),true);
		xpChooser.redrawList();
		this.currentXP = xpName;
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
	 * @Param pathCss
	 * 		  The path to the css file
	 */
	public Links(ServerAddress addr, String xpName, String pathCss) {
		setLookAndFeel();	
		lireMongoPath();
		initMongoConnection(addr);

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			createExperiment(xpName,pathCss);
		}

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName),true);
		xpChooser.redrawList();
		this.currentXP = xpName;
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
		lireMongoPath();
		initMongoConnection(addr);
		xpChooser = new XpChooser(this);
		xpChooser.redrawList();
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
	 * @param visible
	 * 			  The visibility of the experience's frame.
	 * 
	 */
	public Links(String xpName,boolean visible) {
		setLookAndFeel();
		lireMongoPath();
		initMongoConnection();

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			createExperiment(xpName);
		}

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName),visible);
		xpChooser.redrawList();
		this.currentXP = xpName;
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
	 * @param visible
	 * 			  The visibility of the experience's frame.
	 * 
	 * @Param pathCss
	 * 		  The path to the css file
	 */
	public Links(String xpName,boolean visible, String pathCss) {
		setLookAndFeel();
		lireMongoPath();
		initMongoConnection();

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			createExperiment(xpName,pathCss);
		}

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName),visible);
		xpChooser.redrawList();
		this.currentXP = xpName;
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
	 * @param visible
	 * 			  The visibility of the experience's frame.
	 * 
	 */
	public Links(ServerAddress addr, String xpName,boolean visible) {
		setLookAndFeel();	
		lireMongoPath();
		initMongoConnection();

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			createExperiment(xpName);
		}

		createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName),visible);
		xpChooser.redrawList();
	}

	/**
	 * Permet de recuperer le chemin d'acces a mongoDB si le fichier a ete rempli
	 */
	private void lireMongoPath() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(resMong));
			mongoPath = br.readLine();
			mongoConfig = br.readLine();

			br.close();
		} catch (Exception e) {
			try {
				BufferedWriter bw = new BufferedWriter (new FileWriter(resMong));
				bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Get the displayed graph (to access advanced graphstream options).
	 * 
	 * @return The currently displayed graph.
	 */
	public Graph getGraph() {
		if (linksWindow != null) {
			//return linksWindow.getDisplayedGraph().getGraph();
			return this.windows.get(currentXP).getDisplayedGraph().getGraph();
		} else {
			return null;
		}
	}

	/**
	 * Get the graph view (to access advanced graphstream options).
	 * 
	 * @return The currently displayed graph.
	 */
	public Viewer getGraphView() {
		if (linksWindow != null) {
			return linksWindow.getViewer();
		} else {
			return null;
		}
	}

	/**
	 * Connect Links to the MongoDB server.
	 */
	private void initMongoConnection(){
		checkMongo();
		try{
			mongoClient = new MongoClient();
			database = mongoClient.getDatabase(dataBaseName);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("It seems that you have not a running mongoDB server. If you whish not to use mongoDB, be sure to use only the method viewSnapshot.");
		}
		// in the case where mongoDB is not running, we run it

	}

	/**
	 * Connect Links to the MongoDB server.
	 * 
	 * @param addr
	 * 			The address of the server.
	 */
	private void initMongoConnection(ServerAddress addr) {
		checkMongo();
		try{
			mongoClient = new MongoClient(addr);
			database = mongoClient.getDatabase(dataBaseName);
		}catch(Exception e){

			e.printStackTrace();
			System.err.println("It seems that you have not a running mongoDB server. If you whish not to use mongoDB, be sure to use only the method viewSnapshot.");
		}
	}

	/**
	 * Check the OS to know how to execute mongoDB.
	 * A file will be created to save the path and the configuration.
	 */
	private void checkMongo(){
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win")){
			if(mongoPath == null){
				JOptionPane.showMessageDialog(xpChooser, "Can you give the path to mongod.exe ?");
				// creation
				JFileChooser dialogue = new JFileChooser("Give the path to mongod.exe");

				// showing
				dialogue.showOpenDialog(null);
				try {
					if (dialogue.getSelectedFile() == null){
						System.exit(0);
					}
					mongoPath = dialogue.getSelectedFile().toString();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Dialogue mongoPath error");
				}
			}
			if(mongoConfig == null){
				Object[] options = {"Yes","Use default"};
				int n = JOptionPane.showOptionDialog(xpChooser,
						"Can you give the path to the config of mongo or use default ",
						"Configuration",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[1]);
				if(n == 0){
					// creation
					JFileChooser dialogue = new JFileChooser("Give the path to the config of mongo ");

					// showing
					dialogue.showOpenDialog(null);
					try {
						if (dialogue.getSelectedFile() == null){
							System.exit(0);
						}
						mongoConfig = dialogue.getSelectedFile().toString();
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println("Dialogue mongoConfig error");
					}
				}
				else{
					mongoConfig = "DEFAULT";
				}
			}
			//Execute on Windows
			try{
				if(mongoConfig.equals("DEFAULT")){
					String[] commande = {"\""+mongoPath+"\""};
					ProcessBuilder pb = new ProcessBuilder(commande);
					Process p = pb.start();
				}
				else{
					String[] commande = {"\""+mongoPath+"\" --config \""+mongoConfig+"\""};
					ProcessBuilder pb = new ProcessBuilder(commande);
					Process p = pb.start();
				}
			}
			catch(Exception e){
				e.printStackTrace();
				System.err.println("Can't run mongod please check the path");

			}
		}
		else{
			//Execute on Linux
			String[] commande = {"mongod"};
			try {
				ProcessBuilder pb = new ProcessBuilder(commande);
				Process p = pb.start();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Runtime error");
			}
		}

		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(resMong));
			bw.write(mongoPath+"\n");
			bw.write(mongoConfig);
			bw.close();
		}
		catch(IOException e){
			e.printStackTrace();
			System.err.println("BufferedWriter error");
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		linksWindow = this.windows.get(currentXP);
		if (linksWindow != null && s != null) {
			try{
				linksWindow.addSnapshot(s);
				//this.windows.get(currentXP).addSnapshot(s);
			}
			catch(Exception e){
				e.printStackTrace();
				linksWindow.addSnapshot(s);
				//this.windows.get(currentXP).addSnapshot(s);
			}
		}
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Add a new Snapshot to the model. The number of this snapshot is
	 * automatically choose.
	 * 
	 * @param s
	 *            The snapshot to add.
	 */
	public void addSnapshot(Snapshot s, String xpName) {
		if(windows.get(xpName) == null){
			this.createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName), true);
		}
		linksWindow = windows.get(xpName);
		if (linksWindow != null && s != null) {
			try{
				linksWindow.addSnapshot(s);
			}
			catch(Exception e){
				linksWindow.addSnapshot(s);
			}
		}
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Update the current graph to visualize the snapshot.
	 * 
	 * @param s
	 *            The snapshot to view.
	 */
	public void viewSnapshot(Snapshot s) {
		if (linksWindow != null) {
			//linksWindow.getDisplayedGraph().viewSnapshot(s);
			this.windows.get(currentXP).getDisplayedGraph().viewSnapshot(s);
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
	 * Create a new experiment with the given name. Drop if any other experiment
	 * with the same name already exists.
	 * 
	 * @param xpName
	 *            The name of the experiment
	 * @Param pathCss
	 * 			  The path to the css
	 */
	public void createExperiment(String xpName, String pathCss) {
		xpChooser.create(xpName,pathCss);
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

	public void deleteWindow(){
		if(this.linksWindow != null)
			this.linksWindow.close();
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
	 * Drop the experiment with the given name and reset the current snapNumber
	 * at 0.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 */
	public void dropExperiment(String xpName) {
		xpChooser.drop(xpName);
		//linksWindow.getDisplayedGraph().resetSnapNumber();
		if(this.windows.get(xpName) != null)
		this.windows.get(xpName).getDisplayedGraph().resetSnapNumber();
	}

	/**
	 * Initialize the visualization windows on the specificied experiment using
	 * the specified CSS file.
	 * 
	 * @param xpName
	 *            The name of the experiment to visualize.
	 * @param linkToCss
	 *            The path to the CSS file.
	 */
	public void createNewLinksWindows(String xpName, String linkToCss,boolean visible) {
		this.currentXP = xpName;
		this.windows.put(xpName,new LinksWindows(xpName, linkToCss, this,visible));
		//this.linksWindow = new LinksWindows(xpName, linkToCss, this,visible);
	}

	/**
	 * Release memory when a vizualisation windows is closed.
	 */
	public void informClose() {
		this.windows.remove(currentXP);
		this.linksWindow = null;
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

	/**
	 * Return the mongoPath
	 * 
	 * @return mongoPath
	 */
	public String getMongoPath(){
		return mongoPath;
	}
	

}
