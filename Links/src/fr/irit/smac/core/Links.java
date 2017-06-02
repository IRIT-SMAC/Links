package fr.irit.smac.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	 * The mongoPath if we want to execute it
	 */
	public String mongoPath;

	/**
	 * The file which will store the path of mongoDB
	 */
	private String resMong = "setMongo.txt";

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
		lireMongoPath();
		initMongoConnection();
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
		lireMongoPath();
		initMongoConnection();

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
		lireMongoPath();
		initMongoConnection();

		xpChooser = new XpChooser(this);

		if (!existsExperiment(xpName)) {
			createExperiment(xpName);
		}

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
		lireMongoPath();
		initMongoConnection(addr);
		xpChooser = new XpChooser(this);
	}

	/**
	 * Permet de recuperer le chemin d'acces a mongoDB si le fichier a ete rempli
	 */
	private void lireMongoPath() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(resMong));
			String line;
			while ((line = br.readLine()) != null) {
				mongoPath = line;
			}
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
			return linksWindow.getDisplayedGraph().getGraph();
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
	 * Check the OS to know how to execute mongoDB
	 */
	private void checkMongo(){
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win")){
			if(mongoPath == null){
				JOptionPane.showMessageDialog(xpChooser, "Can you give the path to mongod.exe ?");
				// cr�ation de la bo�te de dialogue
				JFileChooser dialogue = new JFileChooser("Give the path to mongod.exe");

				// affichage
				dialogue.showOpenDialog(null);
				try {
					if (dialogue.getSelectedFile() == null){
						System.exit(0);
					}
					mongoPath = dialogue.getSelectedFile().toString();
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("BufferedWriter error");
				}
			}
			//Execute on Windows
			try{
				String[] commande = {"cmd.exe","/c",mongoPath};
				ProcessBuilder pb = new ProcessBuilder(commande);
				Process p = pb.start();
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
			bw.write(mongoPath);
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
		if (linksWindow != null) {
			try{
				linksWindow.addSnapshot(s);
			}catch(NoSuchElementException exc){
				linksWindow.addSnapshot(s);
			}
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
	 * Drop the experiment with the given name and reset the current snapNumber
	 * at 0.
	 * 
	 * @param xpName
	 *            The name of the experiment.
	 */
	public void dropExperiment(String xpName) {
		xpChooser.drop(xpName);
		this.linksWindow.getDisplayedGraph().resetSnapNumber();
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
	public void createNewLinksWindows(String xpName, String linkToCss) {
		linksWindow = new LinksWindows(xpName, linkToCss, this);
		/*Thread t = new Thread(){
			public void run(){
				System.out.println("Enter : 'NBSNAP for the number of snapshot'");
				System.out.println("Enter : 'SHOW <nameEntity> <Attribute1> <Attribute2> <AttributeN> <YES/NO>(synchronization)' to show the graph (in case of blank put the name between simple quote");
				Scanner sc = new Scanner(System.in);
				while(true){
					String ans = sc.nextLine();
					if(ans.equals("NBSNAP")){
						System.out.println("The number of snapshot is : " + linksWindow.getSnapCol().getMaxNum());
					}
					if(ans.contains("SHOW ")){
						Map<Entity,List<String>> tmpMap = new HashMap<Entity,List<String>>();
						ArrayList<String> tmpList = new ArrayList<String>();
						String[] spl = ans.split(" (?=(?:[^\']*\'[^\']*\')*[^\']*$)");
						for(int i =0; i<spl.length;i++){
							if(spl[i].contains("'"))
								spl[i] = spl[i].split("'")[1];
						}
						Entity e = linksWindow.getSnapCol().getEntity(spl[1], linksWindow.getCurrentSnapNumber());
						if(e == null){
							System.out.println("Snapshot not found");
						}
						else{
							ArrayList<DrawableAttribute> atts = new ArrayList<DrawableAttribute>();
							for(int i = 2; i < spl.length-1; i++){
								String s = spl[i];
								//for (String s : e.getAttributes().keySet()) {
								System.out.println(s);
								for (Attribute t : e.getAttributes().get(s)) {
									System.out.println(t.toString());
									atts.add(
											new DrawableAttribute(DrawableAttribute.Type.ENTITY, e.getName(), s, t));
								}
								if(spl[spl.length-1].equals("YES")){
									tmpList.add(s);
								}
							}
							draw(e,100,atts);
							if(spl[spl.length-1].equals("YES")){
								tmpMap.put(e, tmpList);
								charts.add(tmpMap);
							}
						}
					}
				}
			}
		};
		t.start();*/

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
