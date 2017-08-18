package fr.irit.smac.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.tree.DefaultMutableTreeNode;

import fr.irit.smac.attributes.DoubleAttribute;
import fr.irit.smac.attributes.DrawableAttribute;
import fr.irit.smac.attributes.StringAttribute;
import fr.irit.smac.attributes.DrawableAttribute.Type;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Relation;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.model.Attribute;
import fr.irit.smac.model.Attribute.AttributeStyle;

public class Example {

	/**
	 * Launch the application.
	 * 
	 * @param args
	 *            The parameters of the application.
	 */
	public static void main(String[] args) {

		Links links = new Links("test");

		Snapshot s = new Snapshot();

		Entity a = s.addEntity("Toto", "Humain");

		a.addOneAttribute("Charct", "Age", 24);
		a.addOneAttribute("Charct", "Bonbons", 2., AttributeStyle.BAR);
		a.addOneAttribute("Charct", "Nom", "Toto");

		// a.addOneAttribute("Domain", new AVRT("VRange1", new AVT("Up", 1, 0),
		// new AVT("Down", 1, -5), 10, -10));

		Entity b = s.addEntity("Rufus", "Dog");
		Relation r = s.addRelation("Toto", "Rufus", "TotoPossedeRufus", false, "possede");

		s.addEntity("Toto", "Humain");

		links.addSnapshot(s);

		/* ************************ */

		Snapshot s2 = new Snapshot();

		a = s2.addEntity("Toto", "Humain");

		a.addOneAttribute("Charct", "Age", 24);
		a.addOneAttribute("Charct", "Bonbons", 2., AttributeStyle.BAR);
		a.addOneAttribute("Charct", "Nom", "Toto");

		// a.addOneAttribute("Domain", new AVRT("VRange1", new AVT("Up", 1, 0),
		// new AVT("Down", 1, -5), 10, -10));

		s2.addEntity("entityPost1", "Positioned", 10, 10);
		s2.addEntity("entityPost2", "Positioned", 10, 15);
		s2.addEntity("entityPost3", "Positioned", 15, 15);
		s2.addEntity("entityPost4", "Positioned", 15, 10);
		b = s2.addEntity("Rufus", "Dog");
		s2.addRelation("Toto", "Rufus", "TotoPossedeRufus", false, "possede");

		Entity c = s2.addEntity("Luna", "Cat");
		((DoubleAttribute) s2.getEntity("Toto").getAttributesWithName("Age")).setValue(32);
		((DoubleAttribute) s2.getEntity("Toto").getAttributesWithName("Bonbons")).setValue(32);

		s2.addRelation("Luna", "Rufus", "LR", true, "seBattre");
		s2.addRelation("Toto", "Luna", "LU", true, "seBattre");

		boolean continu = true;
		int i = 0;
		//s2.addEntity(""+i, "Test");
		DrawableAttribute datt = new DrawableAttribute(Type.ENTITY,"0","DrawRanger",new DoubleAttribute("DoubleDraw : ", 75));
		s2.addEntity("0", "Bip");
		s2.getEntity("0").addOneAttribute("Attribut1", "Attribut 1", "Boop");
		Scanner scan = new Scanner(System.in);
		s2.getEntity("0").addOneAttribute("Attribut double", "Double :", 15);
		while(continu){
			i++;
			s2.addEntity(""+i, "Test");
			Entity e = s2.getEntity("0");
			
			
			
			s2.addRelation("0",""+i,"0Test"+i,true,"RelTest");
			if (i == 4){
				e.addOneAttribute("Attribut 2", "Attribut 2 : "+i, "Blabla");
				for (String str : e.getAttributes().keySet()) {
					/*for (Attribute t : e.getAttributes().get(str)) {
						System.out.println(t.toString());
					}*/
				}
			}
			
			if(i == 6){
				ArrayList<Attribute> list = new ArrayList<Attribute>();
				e.addOneAttribute("Liste d'attribut","Item 1","Je");
				e.addOneAttribute("Liste d'attribut","Item 2","Tu");
				e.addOneAttribute("Liste d'attribut","Item 3","Il");
			}
			
			if(i == 8){
				Attribute att = e.getAttributesWithName("Double :");
				att.setValue(50);
				System.out.println("LA : " +  e.getAttributesWithName("Double :"));
			}
			
			/*if(i == 10){
				DrawableAttribute datt1 = new DrawableAttribute(Type.ENTITY,"TestDraw","DrawRanger",new DoubleAttribute("DoubleDraw : ", 75));
			}*/
			
			
			
			links.addSnapshot(s2);
			String rep = scan.nextLine();
			
			if(rep == "n")
				continu = false;
			
		}
	}
}
