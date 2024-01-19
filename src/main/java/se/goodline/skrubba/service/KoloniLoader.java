
 package se.goodline.skrubba.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Kolonilott;

public class KoloniLoader 
{
	private ArrayList<Kolonilott> koloniLista;
	
	public boolean loadKolonier(InputStream skrubba)
	{		
		
		BufferedReader reader;
		String lineCols[];
		int radnr = 0; 		
		try 
		{
			reader = new BufferedReader(new InputStreamReader(skrubba));				
			String line  = reader.readLine();
			
			
			koloniLista = new ArrayList<Kolonilott>();
			
			while ((line = reader.readLine()) != null)
			{		
				radnr++;
				
				line = line.replaceAll("\"", "");
				
				lineCols = line.split(";");
				
				if (lineCols[1].equals("adminjimmie"))
					continue;
				
				Kolonilott lott   = new Kolonilott();
				lott.setLottnr(Integer.parseInt(lineCols[1]));
				//System.out.println(line);
				//System.out.println("col: " + 1);
				lott.setAgare(lineCols[3] + " " + lineCols[4]);		
				//System.out.println("col: " + 3);
				lott.setAdress(lineCols[5]);
				//System.out.println("col: " + 5);
				lott.setHemadress(lineCols[6]);
				//System.out.println("col: " + 6);
				lott.setLgh(lineCols[7]);
				//System.out.println("col: " + 7);
				lott.setPostnr(lineCols[8].replaceAll(" ", ""));
				//System.out.println("col: " + 8);
				lott.setPostort(lineCols[9]);
				//System.out.println("col: " + 9);
				lott.setEmail(lineCols[10]);
				//System.out.println("col: " + 10);
				lott.setTelefon(lineCols[13]);
				//System.out.println("col: " + 13);
				lott.setOmrade(Integer.parseInt(lineCols[15].substring(7)));
				//System.out.println("col: " + 15);
				koloniLista.add(lott);
			}
			reader.close();				
			return true;
			} 
		catch (Exception e) 
		{		
			System.out.println("Exception på rad " + radnr + ": " + e.getMessage());
			
			return false;
		}
	}

	public ArrayList<Kolonilott> getLotter()
	{
		return koloniLista;
	}	
}

