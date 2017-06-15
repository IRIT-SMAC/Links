package fr.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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

import com.mongodb.Mongo;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import fr.irit.smac.core.Links;
import fr.irit.smac.model.Attribute.AttributeStyle;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Relation;
import fr.irit.smac.model.Snapshot;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;

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
					linksRef.deleteWindow();
					drop(xpName);
				}

			}
		});
		lblErase.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/eraser.png")));
		toolBar.add(lblErase);
		lblNewLabel.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/edit.png")));
		toolBar.add(lblNewLabel);

		JLabel lblSave = new JLabel("Save ");
		lblSave.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				save((String) list.getSelectedValue());
			}
		});
		toolBar.add(lblSave);

		JLabel lblLoad = new JLabel("Load ");
		lblLoad.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				loadExperience();
			}
		});
		toolBar.add(lblLoad);
		lblPlay.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/play.png")));
		toolBar.add(lblPlay);

		list = new JList();
		init();
		this.setVisible(true);
	}

	public void delete(String xpName) {
		Links.database.getCollection(xpName).drop();
		Links.database.getCollection(Links.collectionNameExperimentList).findOneAndDelete(Filters.eq("xpName", xpName));
	}

	public void create(String xpName) {
		MongoCollection<Document> collection = Links.database.getCollection(Links.collectionNameExperimentList);
		collection.deleteMany(Filters.eq("xpName", xpName));
		String cssLink = "";
		if (linksRef.existsExperiment(xpName)) {
			cssLink = Links.getCssFilePathFromXpName(xpName);
		} else {
			try{
				PrintWriter writer = new PrintWriter("linksAutoGeneratedStyleSheet.css", "UTF-8");
				writer.println("");
				writer.close();
			} catch (IOException e) {
				// do something
			}

			File file = new File("linksAutoGeneratedStyleSheet.css");
			String absolutePath = file.getAbsolutePath();
			String filePath = absolutePath;
			filePath = filePath.substring(0,absolutePath.lastIndexOf(File.separator))+"\\linksAutoGeneratedStyleSheet.css";
			collection.insertOne(new Document("xpName", xpName).append("cssFile", filePath));
		}

		MongoCollection<Document> collection2 = Links.database.getCollection(xpName);
		collection2.deleteMany(Filters.eq("xpName", xpName));
		collection2.insertOne(new Document("xpName", xpName).append("maxNum", 0));
	}

	public void create(String xpName, String cssPath) {
		MongoCollection<Document> collection = Links.database.getCollection(Links.collectionNameExperimentList);
		collection.deleteMany(Filters.eq("xpName", xpName));
		String cssLink = "graphStream.css";
		collection.insertOne(new Document("xpName", xpName).append("cssFile", cssPath));

		MongoCollection<Document> collection2 = Links.database.getCollection(xpName);
		collection2.deleteMany(Filters.eq("xpName", xpName));
		collection2.insertOne(new Document("xpName", xpName).append("maxNum", 0));
	}

	public void drop(String xpName) {
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

		list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2){
					if (list.getSelectedValue() != null) {
						String xpName = (String) list.getSelectedValue();
						if (xpName != null) {
							String linkToCss = Links.getCssFilePathFromXpName(xpName);
							linksRef.createNewLinksWindows(xpName, linkToCss);
						}
					}
				}
			}
		});
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

	/**
	 * Save the dataBase in csv format
	 * @param xpName
	 * 	the name of the experience to save
	 */
	public void save(String xpName){
		MongoCollection<Document> collection = Links.database.getCollection(xpName);
		FindIterable<Document> findIterable = collection.find();
		MongoCursor<Document> curs = findIterable.iterator();
		List<String> fields = new ArrayList<String>();
		boolean first = true;
		int iter = 0;
		String savePath = null;

		//The users choose where the save is done
		// creation of dialogue
		JFileChooser chooser = new JFileChooser("Where do you want to save the experience");
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);


		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
			System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
		} else {
			System.out.println("No Selection ");
		}


		savePath = chooser.getSelectedFile().toString()+File.separator+xpName+".csv";

		//We get the fields of the experience
		while(curs.hasNext()){
			String line = curs.next().toString();
			System.out.println(line);
			//The first line is not interesting
			if(!first){
				String[] lineSplit = line.split(" ");
				for(int i = 2; i< lineSplit.length; i++){
					int nbAcc = 0;
					String base = lineSplit[i];
					String field = base.split("=")[0];
					boolean exist = false;
					for(String s : fields){
						if(s.equals(field))
							exist = true;
					}
					if(!exist){
						fields.add(base.split("=")[0]);
					}
					if(base.contains("{{"))
						nbAcc=nbAcc+2;
					while(nbAcc >0){
						i++;
						String tmpF = lineSplit[i];
						int lengF = 0;
						while((lengF=tmpF.indexOf("{",lengF)) > 0){
							nbAcc++;
							lengF++;
						}
						String tmpB = lineSplit[i];
						int lengB = 0;
						while((lengB = tmpB.indexOf("}",lengB)) > 0){
							nbAcc--;
							lengB++;
						}
					}
				}
			}
			else
				first = !first;
		}
		String sFields = "_id,snapNum";
		for(String s : fields){
			sFields += ",\""+s+"\"";
		}
		//Execution of mongoexport
		Runtime runtime = Runtime.getRuntime();
		String[] mongoPath = linksRef.getMongoPath().split("mongod");
		String query="\""+mongoPath[0]+"mongoexport\" --host localhost --db LinksDataBase --collection " +xpName+
				" --type=csv --fields " + sFields +" --out \""+savePath+"\"";
		Process process = null;
		try{
			process = runtime.exec(query);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Error runtime");
		}
	}


	/**
	 * Load an experience and put it in mongoDB
	 */
	public void loadExperience(){
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(null);
		String loadPath = null;
		try {
			if (chooser.getSelectedFile() == null){
				System.exit(0);
			}
			loadPath = chooser.getSelectedFile().toString();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("BufferedWriter error");
		}
		String[] loadSplit = loadPath.split("\\\\");
		String xpName = loadSplit[loadSplit.length-1];
		xpName = xpName.split("\\.")[0];

		boolean unique = false;

		int iter = 1;
		while(!unique){
			unique = true;
			//Verify if the name is not already used
			MongoCollection<Document> maCollection = Links.database.getCollection(Links.collectionNameExperimentList);
			for (Document document : maCollection.find()) {
				Iterator<Entry<String, Object>> it = document.entrySet().iterator();
				String id = (String) it.next().getValue().toString();
				String xpNameb = (String) it.next().getValue();
				if(xpName.equals(xpNameb)){
					unique = false;
					xpName = xpName + "("+iter+")";
				}
			}
			iter++;
		}
		this.create(xpName);
		String linkToCss = Links.getCssFilePathFromXpName(xpName);
		linksRef.createNewLinksWindows(xpName, linkToCss);
		try
		{
			BufferedReader sourceFile = new BufferedReader(new FileReader(loadPath));
			String line;
			int i = 0;
			Map<String,String> things = new HashMap<String,String>();
			//For each line
			while((line = sourceFile.readLine())!= null)
			{
				//We split by a quote
				String[] lineSplit = line.split(",");
				if(i > 1)
				{
					//Creation of the new Snapshot
					Snapshot s = new Snapshot();
					//for each part splitted
					for(int j = 2; j < lineSplit.length-1;j++){
						if(!lineSplit[j].equals("")){
							int nbAcc = 1;
							String base = lineSplit[j];
							//We get the type
							String[] baseSplit = base.split(":|\"|\\{");
							String type = baseSplit[baseSplit.length-1];
							if(type.equals("Entity")){
								j = constructEntity(lineSplit, j, s, type, things);
							}
							else{
								j = constructRelation(lineSplit, j, s);
							}
						}
					}
					this.linksRef.addSnapshot(s);
				}
				i++;
			}
			sourceFile.close();
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Le fichier est introuvable !");
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*if(base.contains("{"))
							nbAcc=nbAcc++;
						while(nbAcc >0){
							j++;
							String tmpF = lineSplit[j];
							int lengF = 0;
							while((lengF=tmpF.indexOf("{",lengF)) > 0){
								nbAcc++;
								lengF++;
							}
							String tmpB = lineSplit[j];
							int lengB = 0;
							while((lengB = tmpB.indexOf("}",lengB)) > 0){
								nbAcc--;
								lengB++;
							}
						}

						if(type.equals("Entity")){

						}else{

						}
						if(!entitys.containsKey(entity)){
							fields.add(base.split("=")[0]);
						}
					}
				}
				i++;
			}
			sourceFile.close();                 
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Le fichier est introuvable !");
		} catch (IOException e) {
			e.printStackTrace();
		}
		String mongoPath = linksRef.getMongoPath().split("mongod")[0];
		String query = "\""+mongoPath+"mongoimport\" --db testImportJSCProg --collection "+xpName.split("\\.")[0]+" --type=csv --headerline --file \""+loadPath+"\"";
		System.out.println(query);

		Runtime runtime = Runtime.getRuntime();
		Process process = null;

		try{
			process = runtime.exec(query);
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Error runtime");
		}*/
		redrawList();
	}

	private int constructEntity(String[] lineSplit, int j, Snapshot s, String type, Map<String,String> things){

		int nbAcc = 1;
		j++;
		//We get the name
		ArrayList<String> spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		String name = spl.get(spl.size()-1);

		nbAcc += matchBraces(lineSplit[j]);

		j++;
		//We get the class
		spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		String eclass = spl.get(spl.size()-1);
		//If the entity does not exist we add it
		if(things.get(name)==null){
			things.put(name, name);
		}
		nbAcc += matchBraces(lineSplit[j]);

		Entity entity = s.addEntity(name, eclass);
		//we search for all the attribute
		if(!lineSplit[j].contains("}")){
			boolean eend = false;
			j++;
			while(!eend){

				//We get the attribute
				spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
				String att = spl.get(0);

				boolean aend = false;
				boolean fir = true;
				while(!aend){
					nbAcc += matchBraces(lineSplit[j]);
					spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
					//We get the name
					String aname = null;
					if(fir)
						aname = spl.get(1);
					else
						aname = spl.get(0);

					//We get the attributeStyle
					String style = null;
					if(fir)
						style = spl.get(3);
					else
						style = spl.get(2);
					AttributeStyle astyle =null;
					switch(style.trim()){
					case "BAR":
						astyle = AttributeStyle.BAR;
						break;
					case "AVRT":
						astyle = AttributeStyle.AVRT;
						break;
					case "AVT":
						astyle = AttributeStyle.AVT;
						break;
					case "STRING":
						astyle = AttributeStyle.STRING;
					default:
						astyle = AttributeStyle.LINEAR;
					}

					j++;

					//We get the type 
					spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
					String atype = spl.get(2);
					nbAcc += matchBraces(lineSplit[j]);

					//We get the value
					String avalue = spl.get(spl.size()-1);


					switch(atype.trim()){
					case "Double":
						entity.addOneAttribute(att, aname, Double.parseDouble(avalue), astyle);
						break;
					case "String":
						entity.addOneAttribute(att, aname, avalue);
						break;
					default:
						break;
					}
					aend = lineSplit[j].contains("}}");
					if(nbAcc!=0)
						j++;
					fir = false;
				}
				eend = (nbAcc == 0);

			}
		}
		return j;
	}

	private ArrayList<String> eraseEmpty(String[] split) {
		ArrayList<String> ret = new ArrayList<String>();
		for(int i =0; i < split.length;i++){
			if(!split[i].equals(""))
				ret.add(split[i]);
		}
		return ret;
	}

	//Relation can have attribute
	private int constructRelation(String[] lineSplit, int j, Snapshot s){
		int nbAcc = 1;
		j++;
		//We get the name
		ArrayList<String >spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		String name = spl.get(1);

		j++;
		//We get the A
		spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		String a = spl.get(1);

		j++;
		//We get the B
		spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		String b = spl.get(1);

		j++;
		//We get the direction
		spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		String dir = spl.get(1);
		boolean bdir = dir.equals("true");

		j++;
		//We get the class
		spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		String rclass = spl.get(1);
		
		Relation r = s.addRelation(a, b, name, bdir, rclass);
		

		//we search for all the attribute
		if(!lineSplit[j].contains("}")){
			
			boolean eend = false;
			j++;
			while(!eend){

				//We get the attribute
				spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
				String att = spl.get(0);

				boolean aend = false;
				boolean fir = true;
				while(!aend){
					nbAcc += matchBraces(lineSplit[j]);
					spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
					//We get the name
					String aname = null;
					if(fir)
						aname = spl.get(1);
					else
						aname = spl.get(0);

					//We get the attributeStyle
					String style = null;
					if(fir)
						style = spl.get(3);
					else
						style = spl.get(2);
					AttributeStyle astyle =null;
					switch(style.trim()){
					case "BAR":
						astyle = AttributeStyle.BAR;
						break;
					case "AVRT":
						astyle = AttributeStyle.AVRT;
						break;
					case "AVT":
						astyle = AttributeStyle.AVT;
						break;
					case "STRING":
						astyle = AttributeStyle.STRING;
					default:
						astyle = AttributeStyle.LINEAR;
					}

					j++;

					//We get the type 
					spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
					String atype = spl.get(2);
					nbAcc += matchBraces(lineSplit[j]);

					//We get the value
					String avalue = spl.get(spl.size()-1);


					switch(atype.trim()){
					case "Double":
						r.addOneAttribute(att, aname, Double.parseDouble(avalue), astyle);
						break;
					case "String":
						r.addOneAttribute(att, aname, avalue);
						break;
					default:
						break;
					}
					aend = lineSplit[j].contains("}}");
					if(nbAcc!=0)
						j++;
					fir = false;
				}
				eend = (nbAcc == 0);

			}
		}

		return j;
	}

	private int matchBraces(String s) {
		String tmpF = s;
		int nbAcc = 0;
		int lengF = 0;
		while((lengF=tmpF.indexOf("{",lengF)) > 0){
			nbAcc++;
			lengF++;
		}
		String tmpB = s;
		int lengB = 0;
		while((lengB = tmpB.indexOf("}",lengB)) > 0){
			nbAcc--;
			lengB++;
		}
		return nbAcc;
	}
}





