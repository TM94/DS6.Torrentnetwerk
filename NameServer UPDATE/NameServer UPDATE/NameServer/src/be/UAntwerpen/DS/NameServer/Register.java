package be.UAntwerpen.DS.NameServer;

import java.io.*;
import java.util.*;

public class Register implements Serializable	{

	private static final long serialVersionUID = 1L;
	
	Map<Integer, String> register;
	private String naamBestand = "DNSregister.ser";
	
	// Constructor
	public Register()	{
		register = new TreeMap<Integer, String>();
		System.out.println("$ Make register: done");
		loadRegister();
	}
	
	// Node toevoegen aan het register
	public void addNode(String naam, String adres)	{
		loadRegister();
		int sortcode = convHash(naam);
		if(register.containsKey(sortcode))	{
			System.out.println("$ Add node: Deze naam bestaat al, operatie afgebroken...");
		} else {
			register.put(sortcode, adres);
			System.out.println("$ Add node: " + naam + "(" + sortcode + "): " + adres);
			saveRegister();
		}
	}
	
	// Node opvragen van het register, niet bestaan = return null
	public String getNode(String naam)	{
		loadRegister();
		int sortcode = convHash(naam);
		if(register.containsKey(sortcode))	{
			String adres = register.get(sortcode);
			System.out.println("$ Get node: " + naam + "(" + sortcode + ") --> " + adres);
			return adres;
		} else {
			System.out.println("$ Get node: De naam " + naam + " komt niet voor in het register!");
			return null;
		}
		
	}
	
	// Node verwijderen uit het register
	public void removeNode(String naam)	{
		loadRegister();
		int sortcode = convHash(naam);
		if(register.containsKey(sortcode))	{
			register.remove(sortcode);
			System.out.println("$ Remove node: verwijder " + naam + "(" + sortcode + ")");
			saveRegister();
		} else {
			System.out.println("$ Remove node: " + naam + " komt niet voor in het register!");
		}
	}
	
	// Aantal nodes die in het register zitten
	public int getSizeRegister()	{
		loadRegister();
		int size = register.size();
		System.out.println("$ Size register: " + size);
		return size;
	}
	
	// Het register leeg maken
	public void clearRegister()	{
		register.clear();
		System.out.println("$ Clear register: done");
		saveRegister();
	}
	
	// Node-ID teruggeven die onder de hashcode van de file ligt, geen node = return null
	public String getFileNode(String filename)	{
		int hashcodefile = convHash(filename);
		String ipaddress = null;
		int i;
		int run = 1;
		for(i = hashcodefile - 1; run == 1; i--)	{
			if(i == hashcodefile)	{
				if(register.containsKey(i))	{
					ipaddress = register.get(i);
					System.out.println("$ Get file node: " + filename + "(" + hashcodefile + ") op " + ipaddress + "(" + i + ")");
					run = 0;
				} else {
					System.out.println("$ Get file node: register bevat geen nodes!");
					run = 0;
				}
			} else if(i == 0)	{
				if(register.containsKey(i))	{
					ipaddress = register.get(i);
					System.out.println("$ Get file node: " + filename + "(" + hashcodefile + ") op " + ipaddress + "(" + i + ")");
					run = 0;
				} else {
					i = 32768;
				}
			} else {
				if(register.containsKey(i))	{
					ipaddress = register.get(i);
					System.out.println("$ Get file node: " + filename + "(" + hashcodefile + ") op " + ipaddress + "(" + i + ")");
					run = 0;
				}
			}
		}
		return ipaddress;
	}
	
	// Converteert String naar hashcode (0 - 32768)
	public int convHash(String name)	{
		int hashcode = name.hashCode();
		int res = Math.abs(hashcode);
		int res2 = res % 32768;
		return res2;		
	}
	
	// Opslaan van het register naar harde schijf
	public void saveRegister()	{
		try	{
			FileOutputStream savefile = new FileOutputStream(naamBestand);
			ObjectOutputStream save = new ObjectOutputStream(savefile);
			save.writeObject(register);
			save.close();
		} catch(FileNotFoundException fnfe) {
			System.err.println(naamBestand + " niet gevonden!");
			//fnfe.printStackTrace();
		} catch(IOException ioe)	{
			System.err.println("Fout met IO bij wegschrijven van register!");
			//ioe.printStackTrace();
		}
	}
	
	// Laden van het register van harde schijf
	public void loadRegister()	{
		try	{
			FileInputStream loadfile = new FileInputStream(naamBestand);
			ObjectInputStream load = new ObjectInputStream(loadfile);
			register = (TreeMap<Integer, String>) load.readObject();
			load.close();
		} catch(FileNotFoundException fnfe) {
			System.err.println(naamBestand + " niet gevonden!");
			//fnfe.printStackTrace();
		} catch(IOException ioe)	{
			System.err.println("Fout met IO bij wegschrijven van register!");
			//ioe.printStackTrace();			
		} catch(ClassNotFoundException cnfe)	{
			System.err.println("De klasse is niet gevonden!");
			//cnfe.printStackTrace();
		}
	}
}
