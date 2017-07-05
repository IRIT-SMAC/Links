package fr.irit.smac.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JFrame;

import fr.irit.smac.core.Links;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.ui.LinksWindows;

public class LinksImpl extends UnicastRemoteObject implements LinksRemote, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 564199253962112174L;
	
	private Links links;
	protected LinksImpl() throws RemoteException {
		super();
	}
	
	public Links getLinks() throws RemoteException{
		return this.links;
	}
	
	public void createNewLinksWindow(String xpName) throws RemoteException{
		this.links.createNewLinksWindows(xpName, Links.getCssFilePathFromXpName(xpName), true);
	}

	@Override
	public void runLinks() throws RemoteException {
		this.links = new Links();
		
	}

	@Override
	public void buildExperiment(String xpName) throws RemoteException {
		this.links = new Links(xpName);
		
	}
	
	public void addSnapshot(Snapshot s, String xpName){
		this.links.addSnapshot(s, xpName);
	}

	@Override
	public void dropExperiment(String xpName) throws RemoteException {
		this.links.dropExperiment(xpName);
		
	}

	

}
