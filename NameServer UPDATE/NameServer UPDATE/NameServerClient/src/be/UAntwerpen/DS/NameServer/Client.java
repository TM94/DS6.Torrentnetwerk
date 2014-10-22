package be.UAntwerpen.DS.NameServer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client{
	int hashVorige;
	int hashVolgende;
	int hashNode;
	   public Client() {
		   int hashVorige;
		   int hashVolgende;
		   int hashNode;
	   }
	
	   public static void main(String argv[]) throws UnknownHostException {
		   Client client = new Client();
		   String name = "//localhost/FileServer";
		  
	      try {
	         NameServerInterface server = (NameServerInterface) Naming.lookup(name);
	         InetAddress group = InetAddress.getByName("225.100.100.100");				// Multicast IP-address 
	         String hostname = InetAddress.getLocalHost().getHostName();				// Get your hostname
	         client.hashNode = server.convHash(hostname);								// Set your own hash
	         System.out.println("Node name " + hostname + " with hash: " + client.hashNode); //Print node name and hash
	         multicastSend(group);														// Send multicast
	         String numberOfNodes = receiveData(InetAddress.getByName("localhost"));	// Receive data from server
	         int count = Integer.parseInt(numberOfNodes);
	         System.out.println("Data: " + numberOfNodes);								// Print number of nodes
	         if (count < 1) {															// This is the first node
	        	 client.hashVorige = client.hashNode;
	        	 client.hashVolgende = client.hashNode;
	         }
	         else {
	        	 client.hashVorige = ;													// Get hashcode previous node from server-CODE JAN IN NAMESERVER EN HIER AANROEPEN !!!
	        	 client.hashVolgende = ;												// Get hashcode next node from server
	         }
	         while(true) {
	        	 String senderIP = multicastReceive("225.100.100.100");					// Get the IP from the multicastsender
	        	 int hashSender = server.convHash(senderIP);							// Calculate hash code sender IP
	        	 if (hashSender < client.hashVolgende && hashSender < client.hashNode) {
	        		 client.hashVolgende = hashSender;									// Set as next node
	        	 }
	        	 else if (hashSender > client.hashVorige && hashSender < client.hashNode) {
	        		 client.hashVorige = hashSender;									// Set as previous node
	        	 }
	         }
	         
	      } catch(Exception e) {
	         System.err.println("Server exception: "+ e.getMessage());
	         e.printStackTrace();
	      }
	   }
	   
	   public static void multicastSend(InetAddress group) throws UnknownHostException
	   {
		   InetAddress address = InetAddress.getLocalHost();
		   String hostIP = address.getHostAddress();
		   String hostname = address.getHostName();
		   String message = hostname + "," + hostIP;
	   	   MulticastSocket s = null;														//create MulticastSocket
			try 
			{
				s = new MulticastSocket(6789);												//assign socket
				s.joinGroup(group);															//join group by given IP
				DatagramPacket messageOut = new DatagramPacket(message.getBytes(), message.length(), group, 6789);	//create outgoing DatagramPacket
				s.send(messageOut);															//send message
				s.leaveGroup(group);														//leave the group
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());		//SocketException
			}catch (IOException e){System.out.println("IO: " + e.getMessage());				//IOException
			}finally {if(s != null) s.close();}												//close the socket
		}	
	   // VOORLOPIG NIET GEBRUIKT !!!
	   public static String multicastReceive(String ipaddressGroup)
			{ 
			MulticastSocket d = null;														//create MulticastSocket
			String ipaddressSender = null;													//create string for IP
			try 
			{
				InetAddress group = InetAddress.getByName(ipaddressGroup);					//get group IP from arguments
				d = new MulticastSocket(6789);												//assign socket
				d.joinGroup(group);															//join group by given IP
				byte[] buffer = new byte[1000];												//create buffer
		
				// get messages from others in group
				DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);		//create incoming DatagramPacket
				d.receive(messageIn);														//receive message
				InetAddress ipaddress = messageIn.getAddress();								//get sender IP
				System.out.println("Received: " + new String(messageIn.getData()) + "from " + ipaddress);
				d.leaveGroup(group);														//leave the group
				ipaddressSender = ipaddress.toString();
			}catch (SocketException e){System.out.println("Socket: " + e.getMessage());		//SocketException
			}catch (IOException e){System.out.println("IO: " + e.getMessage());				//IOException
			}finally {if(d != null) d.close();}												//close the socket
			
			return ipaddressSender;
		}
	   
	   public static String receiveData(InetAddress ipaddress) throws IOException {
		   Socket socket = new Socket(ipaddress, 8765);
		   
		   DataInputStream dIn = new DataInputStream(socket.getInputStream());

		   int length = dIn.readInt();                    									// read length of incoming message
		   if(length > 0) {
		       byte[] message = new byte[length];
		       dIn.readFully(message, 0, message.length); 									// read the message
		       String data = new String(message, "UTF-8");									// convert bytes to string
		       socket.close();
		       return data;
		   } else {
			   socket.close();
			   return null;
			 }		   
	   }
	}
