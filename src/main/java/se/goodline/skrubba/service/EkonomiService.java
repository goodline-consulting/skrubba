package se.goodline.skrubba.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Betalning;
import se.goodline.skrubba.model.Brevmall;
import se.goodline.skrubba.model.EmailForm;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.BetalningRepository;
import se.goodline.skrubba.repository.BrevmallRepository;
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
	
	@Autowired
	private BrevmallRepository mallRepo;
	
	@Autowired
	private EmailService mailService;
	
	@Autowired
	EmailParser emp;
	
	@Async
	public void fakturera()
	{
		List<Aspirant> aspLista = aspirantRepo.findAll(Sort.by("koPlats"));		
		Optional<Param> paramOpt = paramRepo.findByParamName("Fakturaprocess");
		Param par = null;		
		if (paramOpt.isEmpty())
		{
			par = new Param("Fakturaprocess", "");
		}
		else par = paramOpt.get();
		par.setParamValue("Startad " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		paramRepo.save(par);
		
		Brevmall mallAktiv = mallRepo.findByNamn("köavgift");
		Brevmall mallPassiv =  mallRepo.findByNamn("köavgift_passiva");
		EmailForm em = null;
		int antal = 0;
		int maxAntal = 20;
		int totAntal = 0;
		int antBet = 0;
		for (Aspirant asp : aspLista)
		{	
			Optional<Betalning> bet = betRepo.findThisYear(asp.getId(), Year.now().getValue());
			
			try 
			{
				if (bet.isEmpty())
				{	
					antBet++;
					createBetalning(asp.getId(), Year.now().getValue());
					bet = betRepo.findThisYear(asp.getId(), Year.now().getValue());	
					asp.setBetalat(null);
				}	
				if (bet.get().getMailat() == null)
				{
					antal++;
					totAntal ++;
					// Vi får bara skicka max 20 mail i taget så vi måte vänta här i fem minuter					
					if (antal > maxAntal)
					{
						par.setParamValue("Skapat " + antBet + " betalposter, skickat " + (totAntal - 1) + " brev, Pausar");
						paramRepo.save(par);						
						Thread.sleep(5 * 60 * 1000);
						antal = 1;
						//return;
					}
					
					if (asp.getKoStatus().equals("Aktiv"))						
						em = new EmailForm(mallAktiv);
					    
					else
						em = new EmailForm(mallPassiv);
					em = emp.parseAspEmailForm(em, asp);
					
					System.out.println("Skickar mail till " + asp.getEmail() + " på köplats " + asp.getKoPlats() + " med status " + asp.getKoStatus());
					mailService.sendEmail(asp.getEmail(), em);
					bet.get().setMailat(new Date());
					betRepo.save(bet.get());
					
				}
            } 
			catch (DataAccessException e) 
			{            
				System.err.println("Betalning finns redan för " + asp.getFnamn() + " " + asp.getEnamn() + " med id: " + asp.getId());
				
			} 
			catch (MessagingException e) 
			{
				System.err.println(e.getMessage());				
			} 
			catch (IOException e) 
			{			
				System.err.println(e.getMessage());
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
         }
		par.setParamValue("Skapat " + antBet + " betalposter, skickat " + totAntal + " brev, klart " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		paramRepo.save(par);
	}
	
	@Async
	public void omFakturera()
	{
		List<Aspirant> aspLista = aspirantRepo.findByNotPaid();		
		Optional<Param> paramOpt = paramRepo.findByParamName("Betalningspåminnelse");
		Param par = null;		
		if (paramOpt.isEmpty())
		{
			par = new Param("Betalningspåminnelse", "");
		}
		else par = paramOpt.get();
		par.setParamValue("Startad " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		paramRepo.save(par);
		Brevmall mall = mallRepo.findByNamn("Obetald administrationsavgift");
		
		EmailForm em = null;
		int antal = 0;
		int maxAntal = 20;
		int totAntal = 0;
		int antBet = 0;
		for (Aspirant asp : aspLista)
		{	
			Optional<Betalning> bet = betRepo.findThisYear(asp.getId(), Year.now().getValue());
			
			try 
			{
				if (bet.isEmpty())
				{	
					antBet++;
					createBetalning(asp.getId(), Year.now().getValue());
					bet = betRepo.findThisYear(asp.getId(), Year.now().getValue());	
					
				}	
				
				if (bet.get().getMailat() == null || isOlderThanTwoWeeks(bet.get().getMailat()))
				{
					antal++;
					totAntal ++;
					// Vi får bara skicka max 20 mail i taget så vi måte vänta här i fem minuter					
					if (antal > maxAntal)
					{
						par.setParamValue("Skapat " + antBet + " betalposter, skickat " + (totAntal - 1) + " brev, Pausar");
						paramRepo.save(par);						
						Thread.sleep(5 * 60 * 1000);
						antal = 1;
						//return;
					}
					
					
					em = new EmailForm(mall);
					em = emp.parseAspEmailForm(em, asp);
					
					System.out.println("Skickar mail till " + asp.getEmail() + " på köplats " + asp.getKoPlats() + " med status " + asp.getKoStatus());
					mailService.sendEmail(asp.getEmail(), em);
					bet.get().setMailat(new Date());
					betRepo.save(bet.get());
					
				}
            } 
			catch (DataAccessException e) 
			{            
				System.err.println("Betalning finns redan för " + asp.getFnamn() + " " + asp.getEnamn() + " med id: " + asp.getId());
				
			} 
			catch (MessagingException e) 
			{
				System.err.println(e.getMessage());				
			} 
			catch (IOException e) 
			{			
				System.err.println(e.getMessage());
			} 
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
         }
		par.setParamValue("Skapat " + antBet + " betalposter, skickat " + totAntal + " betalningspåminnelser, Klart " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
		paramRepo.save(par);
		
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
    
    // Metod för att kolla om ett java.util.Date är äldre än två veckor
    private boolean isOlderThanTwoWeeks(Date mailatDate) {
        if (mailatDate == null) {
            return false;  // Hantera null-värden om det behövs
        }

        // Konvertera java.util.Date till java.time.LocalDate
        LocalDate mailat = mailatDate.toInstant()
                                     .atZone(ZoneId.systemDefault())
                                     .toLocalDate();

        // Beräkna datumet för två veckor sedan
        LocalDate twoWeeksAgo = LocalDate.now().minus(2, ChronoUnit.WEEKS);
        
        // Kontrollera om mailat är äldre än två veckor
        return mailat.isBefore(twoWeeksAgo);
    }
}
