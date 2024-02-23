
 package se.goodline.skrubba.repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import se.goodline.skrubba.model.Aspirant;

public class AspirantLoader 
{
	private ArrayList<Aspirant> aspirantLista;
	
	public static final int POS_KOPLATS = 1;
	public static final int POS_NAMN = 2;
	public static final int POS_EMAIL = 3;
	public static final int POS_ADRESS = 4;
	public static final int POS_POSTNR = 5;
	public static final int POS_POSTADRESS = 6;
	public static final int POS_TELEFON = 7;
	public static final int POS_TELHEM = 8;
	public static final int POS_AKTIV = 9;
	public static final int POS_INSKRIVEN = 10;
	
	
	public boolean loadAspirants(String fileName)
	{		
		BufferedReader reader;
		String lineCols[];
		
		try 
		{
			reader = new BufferedReader(new FileReader(fileName));				
			String line  = reader.readLine();
						
			aspirantLista = new ArrayList<Aspirant>();
			
			
				while ((line = reader.readLine()) != null)
				{										
					lineCols = line.split(";");
					if (lineCols[POS_EMAIL].equals(""))
						continue;
					System.out.println(line);				
					Aspirant aspirant   = new Aspirant();
					
					aspirant.setKoPlats(Integer.parseInt(lineCols[POS_KOPLATS]));
					aspirant.setFnamn(lineCols[POS_NAMN].split(" ")[0]);
					aspirant.setEnamn(lineCols[POS_NAMN].split(" ")[1]);
					aspirant.setEmail(lineCols[POS_EMAIL]);	
					aspirant.setAdress(lineCols[POS_ADRESS]);
					aspirant.setPostnr(lineCols[POS_POSTNR]);
					aspirant.setPostAdress(lineCols[POS_POSTADRESS]);
					if (lineCols.length == POS_TELEFON)
						continue;
					aspirant.setTelefon(lineCols[POS_TELEFON].equals("") ? lineCols[POS_TELHEM] : lineCols[POS_TELEFON]);
					aspirant.setKoStatus(lineCols[POS_AKTIV].equals("1") ? "Aktiv" : "Vilande");
					if (aspirant.getKoStatus().equals("Aktiv"))
						aspirant.setUtbildad(new SimpleDateFormat("yyyy-MM-dd").parse("2023-06-06"));
					// aspirant.setInskriven(new Date(lineCols[POS_INSKRIVEN]));
					
					aspirantLista.add(aspirant);
					
				}
			
			reader.close();				
			return true;
		} 
		catch (NumberFormatException | IOException e) 
		{
			System.out.println(e.getMessage());
			return false;
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			return false;
		}	
	} 
	
	

	public ArrayList<Aspirant> getAsprianter()
	{
		return aspirantLista;
	}	
}

