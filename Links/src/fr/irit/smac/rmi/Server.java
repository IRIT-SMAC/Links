package fr.irit.smac.rmi;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import fr.irit.smac.core.Links;

public class Server {
	 public static void main(String[] args) {
		    try {
		      LocateRegistry.createRegistry(1099);

		      System.out.println("Set of the Security Manager ...");
		     /* if (System.getSecurityManager() == null) {
		        System.setSecurityManager(new SecurityManager());
		      }*/
		      LinksImpl linksImpl = new LinksImpl();

		      String url = "rmi://" + InetAddress.getLocalHost().getHostAddress() + "/Links";
		      
		      Naming.rebind(url, linksImpl);

		      System.out.println("Server running");
		    } catch (RemoteException e) {
		      e.printStackTrace();
		    } catch (MalformedURLException e) {
		      e.printStackTrace();
		    } catch (java.net.UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
}
