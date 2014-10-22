package be.UAntwerpen.DS.NameServer;

import java.io.*;
import java.net.*;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class NameServer extends UnicastRemoteObject implements NameServerInterface{
	
	private static final long serialVersionUID = 1L;
	
	static Register NSregister;
	
    public static void main(String[] argv) throws NotBoundException, IOException {
    	NameServer server = new NameServer();
    	String bindLocation = "//localhost/FileServer";
    	
    	try { 
    		InetAddress groupIP = InetAddress.getByName("225.100.100.100");
    		Registry reg = LocateRegistry.createRegistry(1099);
    		Naming.bind(bindLocation, server);	        
    		System.out.println("FileServer Server is ready at:" + bindLocation);
            System.out.println("java RMI registry created.\n");
    		
            server.getFileNodePing("test");
            
    		String data = NameServer.receiveMulticast(groupIP);
    		String[] parts = data.split(",");
			String name = parts[0]; 
			String ipaddress = parts[1];
			NSregister.clearRegister();										// enkel test
			System.out.println("Size: " + NSregister.getSizeRegister());		// enkel test
			
			sendData(InetAddress.getByName(ipaddress));
			
			NSregister.addNode(name, ipaddress);
			System.out.println("Size: " + NSregister.getSizeRegister());		// enkel test
			System.out.println("Ip van node: " + NSregister.getNode(name));	// enkel test
			
    		//NameServerInterface service = (NameServerInterface) UnicastRemoteObject.exportObject(server,0);
        } catch (MalformedURLException | AlreadyBoundException e) {
            System.out.println("java RMI registry already exists.");
        }
    }

	public NameServer() throws RemoteException	{
		NSregister = new Register();
	}
    
	public void addNode(String naam, String ipadres)	{
		NSregister.addNode(naam, ipadres);
	}
	
	public String getNode(String naam)	{
		return NSregister.getNode(naam);
	}
	
	public void removeNode(String naam)	{
		NSregister.removeNode(naam);
	}
	
	public int getSizeRegister()	{
		return NSregister.getSizeRegister();
	}
	
	public void clearRegister()	{
		NSregister.clearRegister();
	}
	
	public String getFileNode(String filename)	{
		return NSregister.getFileNode(filename);
	}
	
	public int convHash(String name) {
		return NSregister.convHash(name);
	}
	
	
	
	
	public boolean ping(String ipaddress) throws IOException, UnknownHostException	{
		try {
			String ipaddressFile = ipaddress;
			InetAddress inet = InetAddress.getByName(ipaddressFile);
			return inet.isReachable(5000);
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
			return false;
		}
	}
	
	public void getFileNodePing(String filename) throws UnknownHostException, IOException	{
		try {
			String ipaddress = getFileNode(filename);
			boolean result = ping(ipaddress);
			if (result == true) {
				System.out.println("Pingen naar " + ipaddress + " is gelukt.");
			}else if(result == false) {
				System.out.println("Pingen naar " + ipaddress + " is mislukt. Destination host is NOT reachable");
			}
		}catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}
	}
	
	public static String receiveMulticast(InetAddress group) {
		    MulticastSocket s = null;														//create MulticastSocket
			try 
			{
				//InetAddress group = InetAddress.getByName(group);					//get group IP from arguments
				s = new MulticastSocket(6789);												//assign socket
				s.joinGroup(group);															//join group by given IP
				byte[] buffer = new byte[1000];												//create buffer
		
				// get messages from others in group
				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);		//create incoming DatagramPacket
				s.receive(messageIn);														//receive message
				InetAddress ipaddress = messageIn.getAddress();								//get sender IP
				System.out.println("Received: " + new String(messageIn.getData()) + "from " + ipaddress);
				s.leaveGroup(group);														//leave the group
				String ipaddressSender = ipaddress.toString();								//make string from IP
				String name = new String(messageIn.getData());								//get node name
				String data = name + "," + ipaddressSender;
				return data;
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());		//SocketException
			return null;
			}catch (IOException e){System.out.println("IO: " + e.getMessage());				//IOException
			return null;
			}finally {if(s != null) s.close();}	
			}
	
	public static void sendData(InetAddress ipaddress) throws IOException {
		ServerSocket serversocket = new ServerSocket(8765);
		Socket clientsocket = serversocket.accept();											 // Server listens to (and accepts) a connection
		
		int size = NSregister.getSizeRegister();
		String data = "" + size;
		byte[] message = data.getBytes();
		DataOutputStream dOut = new DataOutputStream(clientsocket.getOutputStream());
		dOut.writeInt(message.length); 															 // write length of the message
		dOut.write(message);         														 	 // write the message

	}
}
