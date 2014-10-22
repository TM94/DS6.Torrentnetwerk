package be.UAntwerpen.DS.NameServer;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameServerInterface extends Remote	{
	public void addNode(String naam, String ipadres) throws RemoteException;
	public String getNode(String naam) throws RemoteException;
	public void removeNode(String naam) throws RemoteException;
	public int getSizeRegister() throws RemoteException;
	public void clearRegister() throws RemoteException;
	public String getFileNode(String filename) throws RemoteException;
	public int convHash(String name) throws RemoteException;

	public boolean ping(String ipaddress) throws RemoteException, IOException;
	public void getFileNodePing(String filename) throws RemoteException, IOException;
}
