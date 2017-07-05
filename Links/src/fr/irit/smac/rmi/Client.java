package fr.irit.smac.rmi;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import fr.irit.smac.core.Links;
import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.model.Attribute.AttributeStyle;


public class Client {

	private Remote remote;
	public Client(){
		try{
			String url = "rmi://" + InetAddress.getLocalHost().getHostAddress() + "/Links";
			remote = Naming.lookup(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Remote getRemote(){
		return remote;
	}
	public static void main(String[] args) {
		System.out.println("Lancement du client");
		try {
			String url = "rmi://" + InetAddress.getLocalHost().getHostAddress() + "/Links";
			Remote r = Naming.lookup(url);
			System.out.println(r);
			if (r instanceof LinksRemote) {
				((LinksRemote) r).buildExperiment("test");

				//links.dropExperiment("test");
				System.out.println();
				Snapshot s2 = new Snapshot();
				Entity a = s2.addEntity("Toto", "Humain");
				a = s2.addEntity("Toto", "Humain");

				a.addOneAttribute("Charct", "Age", 40);
				a.addOneAttribute("Charct", "Bonbons", 6.0, AttributeStyle.BAR);
				a.addOneAttribute("Charct", "Nom", "Toto");

				// a.addOneAttribute("Domain", new AVRT("VRange1", new AVT("Up", 1, 0),
				// new AVT("Down", 1, -5), 10, -10));
				Entity b = s2.addEntity("Rufus", "Dog");
				b = s2.addEntity("Rufus", "Dog");
				s2.addRelation("Toto", "Rufus", "TotoPossedeRufus", false, "possede");

				Entity c = s2.addEntity("Luna", "Cat");

				s2.addRelation("Luna", "Rufus", "LR", true, "seBattre");
				s2.addRelation("Toto", "Luna", "LU", true, "seBattre");
				((LinksRemote) r).addSnapshot(s2,"test");

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Fin du client");
		while(true);
	}

}
