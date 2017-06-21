package fr.irit.smac.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
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
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bson.Document;

import com.mongodb.BasicDBObject;
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
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class XpChooser extends JFrame {

	private JPanel contentPane;
	private JList<String> list;
	private Links linksRef;
	private NewXpWindows xpWindows;
	private JTextField textField;

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
		setContentPane(new JScrollPane(contentPane));

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
		toolBar.addSeparator(new Dimension(5,25));

		JLabel lblRemove = new JLabel("");
		lblRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (arg0.getSource().equals(lblRemove)) {
					int dialogButton = JOptionPane.YES_NO_OPTION;

					List<String> xps = list.getSelectedValuesList();
					String choice = "Would You Like to Completly Delete the Experiment Entitled : \n";
					for(String xpName: xps){
						choice += xpName + "\n";
					}
					choice += " ?";
					int dialogResult = JOptionPane.showConfirmDialog(null,choice, "Warning",dialogButton);
					if (dialogResult == JOptionPane.YES_OPTION) {
						for(String xpName : xps)
							destroyExperiment(xpName);
					}
				}
			}
		});
		lblRemove.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/minus.png")));
		toolBar.add(lblRemove);
		toolBar.addSeparator(new Dimension(5,25));

		JLabel lblPlay = new JLabel("");
		lblPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (list.getSelectedValue() != null) {
					String xpName = (String) list.getSelectedValue();
					if (xpName != null) {
						String linkToCss = Links.getCssFilePathFromXpName(xpName);
						links.createNewLinksWindows(xpName, linkToCss,true);
					}
				}
			}

		});

		JLabel lblEdit = new JLabel("");
		lblEdit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getSource().equals(lblEdit)) {
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
				List<String> xps = list.getSelectedValuesList();
				String choice = "Would You Like to Drop the Experiment Entitled : \n";
				for(String xpName: xps){
					choice += xpName + "\n";
				}
				choice += " ?";
				int dialogResult = JOptionPane.showConfirmDialog(null,choice, "Warning",dialogButton);

				if (dialogResult == JOptionPane.YES_OPTION) {
					//String xpName = (String) list.getSelectedValue();
					linksRef.deleteWindow();
					for(String xpName : xps)
						drop(xpName);
				}

			}
		});
		lblErase.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/eraser.png")));
		toolBar.add(lblErase);
		toolBar.addSeparator(new Dimension(5,25));
		lblEdit.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/edit.png")));
		toolBar.add(lblEdit);
		toolBar.addSeparator(new Dimension(5,25));

		ImageIcon iErase = new ImageIcon(LinksWindows.class.getResource("/icons/eraser.png"));;

		JLabel lblSave = new JLabel();
		lblSave.setIcon(new ImageIcon(new ImageIcon(LinksWindows.class.getResource("/icons/save.png")).getImage().getScaledInstance(iErase.getIconWidth(), iErase.getIconHeight(), Image.SCALE_DEFAULT)));
		lblSave.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				save((String) list.getSelectedValue());
			}
		});
		toolBar.add(lblSave);
		toolBar.addSeparator(new Dimension(5,25));

		JLabel lblLoad = new JLabel();
		lblLoad.setIcon(new ImageIcon(new ImageIcon(LinksWindows.class.getResource("/icons/file.png")).getImage().getScaledInstance(iErase.getIconWidth(), iErase.getIconHeight(), Image.SCALE_DEFAULT)));
		lblLoad.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e){
				loadExperience();
			}
		});
		toolBar.add(lblLoad);
		toolBar.addSeparator(new Dimension(5,25));
		lblPlay.setIcon(new ImageIcon(XpChooser.class.getResource("/icons/play.png")));
		toolBar.add(lblPlay);
		toolBar.addSeparator(new Dimension(5,25));

		JLabel lblSaveDesc = new JLabel("Save desc");
		lblSaveDesc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e){
				saveDesc();
			}
		});
		toolBar.add(lblSaveDesc);
		toolBar.addSeparator(new Dimension(5,25));

		list = new JList();
		init();
		this.setVisible(true);
	}

	//TODO
	protected void saveDesc() {
		String s = textField.getText();
		MongoCollection<Document> collection = Links.database.getCollection(list.getSelectedValue());
		Document doc = collection.find(Filters.eq("LinksDescriptionXP","The description")).first();
		Document newDocument = new Document("LinksDescriptionXP", "The description").append("Desc : ", "DescriptionOfXP "+s);
		if (doc != null){
			collection.findOneAndReplace(new BasicDBObject().append("LinksDescriptionXP","The description"), newDocument);
		}
		else
			collection.insertOne(newDocument);

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
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			//TODO
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(list.getSelectedValue() != null){
					MongoCollection<Document> collection = Links.database.getCollection(list.getSelectedValue());
					Document doc = collection.find(Filters.eq("LinksDescriptionXP","The description")).first();
					if (doc == null){
						textField.setText("NULL");
					}
					else{
						Iterator<Entry<String, Object>> it = doc.entrySet().iterator();
						//We need to iterate 3 times
						it.next();
						it.next();
						textField.setText(it.next().getValue().toString());
					}

				}
			}
		});

		JLabel lblTxt = new JLabel("Select or create your experiment");
		lblTxt.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblTxt, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		contentPane.add(splitPane, BorderLayout.CENTER);

		splitPane.setLeftComponent(list);

		textField = new JTextField();
		splitPane.setRightComponent(textField);

		list.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount() == 2){
					if (list.getSelectedValue() != null) {
						String xpName = (String) list.getSelectedValue();
						if (xpName != null) {
							String linkToCss = Links.getCssFilePathFromXpName(xpName);
							linksRef.createNewLinksWindows(xpName, linkToCss,true);
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

		if(chooser.getSelectedFile() != null){
			savePath = chooser.getSelectedFile().toString()+File.separator+xpName+".csv";

			//We get the fields of the experience
			while(curs.hasNext()){
				String line = curs.next().toString();
				if(line.contains("LinksDescriptionXP")){
					fields.add("Desc : ");
				}
				//The first line is not interesting
				if(!first && !line.contains("LinksDescriptionXP")){
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
	}


	/**
	 * Load an experience and put it in mongoDB
	 */
	public void loadExperience(){
		JFileChooser chooser = new JFileChooser();
		chooser.showOpenDialog(null);
		String loadPath = null;
		if (chooser.getSelectedFile() != null){
		loadPath = chooser.getSelectedFile().toString();

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
		linksRef.createNewLinksWindows(xpName, linkToCss,true);
		try
		{
			BufferedReader sourceFile = new BufferedReader(new FileReader(loadPath));
			String line;
			int i = 0;
			Map<String,String> things = new HashMap<String,String>();
			//For each line
			while((line = sourceFile.readLine())!= null)
			{
				if(line.contains("DescriptionOfXP")){
					String[] lineSplit = line.split(",|=|\\}");
					ArrayList<String> tmp = this.eraseEmpty(lineSplit);
					String s = tmp.get(tmp.size()-1);
					MongoCollection<Document> collection = Links.database.getCollection(xpName);
					Document doc = collection.find(Filters.eq("LinksDescriptionXP","The description")).first();
					Document newDocument = new Document("LinksDescriptionXP", "The description").append("Desc : ", "DescriptionOfXP "+s);
					if (doc != null){
						collection.findOneAndReplace(new BasicDBObject().append("LinksDescriptionXP","The description"), newDocument);
					}
					else
						collection.insertOne(newDocument);
				}
				else{
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

		j++;
		//We get the coorX
		spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		double coorx = Double.parseDouble(spl.get(spl.size()-1));

		j++;
		//We get the coorY
		spl = eraseEmpty(lineSplit[j].split(":|\"|\\[|\\]|\\{|\\}|="));
		double coory = Double.parseDouble(spl.get(spl.size()-1));

		//If the entity does not exist we add it
		if(things.get(name)==null){
			things.put(name, name);
		}
		nbAcc += matchBraces(lineSplit[j]);
		Entity entity ;
		if(coorx == -10000 && coory == -10000)
			entity = s.addEntity(name, eclass);
		else
			entity = s.addEntity(name, eclass,coorx,coory);

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





