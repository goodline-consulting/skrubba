package se.goodline.skrubba.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Betalning;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.BetalningRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.repository.VisningRepository;



@Service
public class EkonomiService 
{ 
	private String firstRow;
	
	@Autowired
	private AspirantRepository aspirantRepo;
	
	@Autowired
	private BetalningRepository betRepo;
	
	@Autowired
	private ParamRepository paramRepo;
	
	@Autowired
	private KoloniLottRepository lottRepo;
	
	
	public void fakturera()
	{
		List<Aspirant> aspLista = aspirantRepo.findAll();
		
		
		for (Aspirant asp : aspLista)
		{	
			try 
			{
				createBetalning(asp.getId(), Year.now().getValue());
            } 
			catch (DataAccessException e) 
			{            
				System.err.println("Betalning finns redan för " + asp.getFnamn() + " " + asp.getEnamn() + " med id: " + asp.getId());
				//e.printStackTrace();
			}	
         }
	}
    public void createBetalning(int asp, int ar)
    {
    	 int avgift;
		 Optional<Param> par = paramRepo.findByParamName("Köavgift");
		 if (par.isPresent())
			avgift = Integer.parseInt(par.get().getParamValue());
		 else
			avgift = 250;
    	 Betalning bet = new Betalning();
         bet.setAsp(asp);
         bet.setAr(ar);  
         bet.setSumma(avgift);
         betRepo.save(bet);
    }
    public void createBetaldBetalning(int asp, int ar)
    {
    	 int avgift;
		 Optional<Param> par = paramRepo.findByParamName("Köavgift");
		 if (par.isPresent())
			avgift = Integer.parseInt(par.get().getParamValue());
		 else
			avgift = 250;
    	 Betalning bet = new Betalning();
         bet.setAsp(asp);
         bet.setAr(ar);  
         bet.setSumma(avgift);
         bet.setBetdatum(new Date());
         betRepo.save(bet);
    }
    public void createDummyBetalning(int asp, int ar)
    {
    	 Betalning bet = new Betalning();
         bet.setAsp(asp);
         bet.setAr(ar);  
         bet.setSumma(0);
         bet.setBetdatum(new Date());
         betRepo.save(bet);
    }
    
    public File CreateMergedFile(InputStream vismaStream) throws IOException
    {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(vismaStream));				
		
    	ArrayList<String[]> vismaListan  = readVisma(reader);
    	
    	for (String[] vismaRad: vismaListan)
		{
    		int lottnr = Integer.parseInt(vismaRad[1]);
    		if (lottnr > 197)
    			continue;
			Kolonilott lott = lottRepo.getByLottnr(lottnr);
			vismaRad[2]  = lott.getAgare();  	
			vismaRad[5]  = lott.getHemadress() + " " + lott.getLgh();  		// Adress + lägenhetanummer
			vismaRad[5]  = vismaRad[5].trim();
			vismaRad[7]  = lott.getPostnr();   // Postnummer
			vismaRad[8]  = lott.getPostort();  // Postort
			vismaRad[13] = lott.getTelefon();  // Telefon 1
			// vismaRad[14] = rad[13];         // Telefon 2
			vismaRad[15] = lott.getEmail();    // Email 1
			// vismaRad[16] = rad[11];  	   // Email 2
		}
    	return writeVisma(vismaListan);		
    }
    
    private ArrayList<String[]> readVisma(BufferedReader reader) throws IOException 
	{
		
		ArrayList<String[]> listan = new ArrayList<String[]>();
			
		firstRow = reader.readLine();
		String line = null;
		
		while ((line = reader.readLine()) != null) 
		{				
			listan.add(line.split(";"));			
		}
		reader.close();
		
		return listan;
	}
    
    private File writeVisma(ArrayList<String[]> vismaLista) throws IOException
	{
				
		File mergedFile = File.createTempFile("visma_merged", ".csv");
		PrintWriter outFile = new PrintWriter(mergedFile);
		outFile.println(firstRow);
		for (String[] rad : vismaLista)
		{
			int i = 0;
			for (String kol : rad)			
				outFile.print(kol + (++i == rad.length ? "" : ";"));				
			outFile.println();
		}	
		outFile.flush();
		return mergedFile;
	}
}
