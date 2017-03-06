package com.irit.smac.core;

import java.util.ArrayList;

import com.irit.smac.attributes.AVRT;
import com.irit.smac.attributes.AVT;
import com.irit.smac.attributes.DoubleAttribute;
import com.irit.smac.attributes.StringAttribute;
import com.irit.smac.model.Attribute;
import com.irit.smac.model.Snapshot;
import com.irit.smac.model.SnapshotsCollection;
import com.irit.smac.ui.LinksMainWindow;

public class Links {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		SnapshotsCollection snapCol = new SnapshotsCollection();
		
		DisplayedGraph graph = new DisplayedGraph(snapCol);
		
		LinksMainWindow window = new LinksMainWindow(graph);
		snapCol.setLinksWindows(window);
		
	
		Snapshot s = new Snapshot();
		
		s.addAgent("Toto", "Humain");
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add(new DoubleAttribute("Age",24));
		attributes.add(new StringAttribute("Nom","Toto"));
		s.getAgent("Toto").addAttribute("Charct", attributes);
		
		attributes = new ArrayList<Attribute>();
		attributes.add(new AVRT("VRange1", new AVT("Up", 1, 0 ), new AVT("Down", 1,-5), 10, -10));
		s.getAgent("Toto").addAttribute("Domain",attributes);
		 
		s.addAgent("Rufus", "Dog");
		s.addRelation("Toto", "Rufus", "TotoPossedeRufus", false, "possede");
		snapCol.addSnapshot(s);
		
		
		Snapshot s2 = new Snapshot();
		
		s2.addAgent("Toto", "Humain");
		
		attributes = new ArrayList<Attribute>();
		attributes.add(new DoubleAttribute("Age",24));
		attributes.add(new StringAttribute("Nom","Toto"));
		s2.getAgent("Toto").addAttribute("Charct", attributes);
		
		attributes = new ArrayList<Attribute>();
		attributes.add(new AVRT("VRange1", new AVT("Up", 1, 0 ), new AVT("Down", 1,-5), 10, -10));
		s2.getAgent("Toto").addAttribute("Domain",attributes);
		 
		s2.addAgent("Rufus", "Dog");
		s2.addRelation("Toto", "Rufus", "TotoPossedeRufus", false, "possede");

		s2.addAgent("Luna", "Cat");
		((DoubleAttribute) s2.getAgent("Toto").getAttributesWithName("Age")).value= 32;
		s2.addRelation("Luna", "Rufus", "LR", true, "seBattre");
		s2.addRelation("Toto", "Luna", "LU", true, "seBattre");
		snapCol.addSnapshot(s2);
	}
}
