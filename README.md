# Links

Links: A tool to visualize agents and their relations over time.

contact : contact@verstaevel.fr

/* Requirement * /

	- A running MongoDB server (https://www.mongodb.com/). Installation procedure at : https://docs.mongodb.com/manual/installation/
	- JDK 1.5 or over.
	
/* Dependencies */
	
	- MongoDB Java Driver - https://mongodb.github.io/mongo-java-driver/
	- GraphStream - http://graphstream-project.org/
	- LxPlot - https://bitbucket.org/perlesa/lx-plot
	
/* Usage */

	Add Links.jar file to your project - https://github.com/Eldey/Links/blob/master/Links/Links.jar

		1 - Create a Links instance.
			Links links = new Links();
	
		2 - Create a new Snapshot.
			Snapshot s = new Snapshot();
		
		3 - Add agents to the snapshot.
			Agent a = s.addAgent("Toto", "Human");
				where Toto is the agent name and Human is its css style.
			
		4 - Add attributes to your agent.
			a.addOneAttribute("Caract", new DoubleAttribute("Age", 24));
				where Caract is the name of the attribute list. There are curently four types of attributes: Double, String, AVT and AVRT.
			
		5 - Add relations to your snaphot.
			Relation r = s.addRelation("Toto", "Rufus", "RelationName", false, "relationType");
				where Toto is the name of the first agent, Rufus is the name of the second agent, RelationName is the name of the relation (must be unique), false is a boolean which determines if the relation is directional, and relationType is the css style.

		6 - Add attributes to your snaphot (see step 4);
	
		7 - Load your snaphot to your application.
			Links.addSnapshot(s);
			
/* Options */

	- You can precise the visibility of links in the constructor with a boolean, by default it is true.
	
	- You can precise the coordinates of the entities, if you do so you have to do it for every entities
	
	- The colors of nodes can be dynamically change if in your css file you precise :
		1 - fill-color: color1, color2  (more if you want but at least 2 )
		2 - fill-mode: dyn-plain
		
/* Exportation of an experience */

	- You can export in format .csv an experience with the button save of xpChooser
	- You can import a .csv file previously exported by links 
