package se.goodline.skrubba.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Betalning;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.BetalningRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.repository.VisningRepository;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

@Service
public class AspirantService 
{
	@Autowired
	private AspirantRepository aspirantRepo;
	
	@Autowired
	private VisningRepository visRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	BetalningRepository betRepo;
	
	@Autowired
	ParamRepository paramRepo;
	
	@Autowired
	EkonomiService ekoService;
	
	
	public Aspirant getAspirantById(int id)
	{
		return aspirantRepo.getById(id);			
	}
	public void newAspirant(Aspirant asp)
	{
		
		Optional <User> user = userRepo.findByUserName(asp.getEmail());
		asp.setId(user.get().getId());		
		if (LocalDate.now().getMonthValue() == 12)
		{	
			ekoService.createDummyBetalning(asp.getId(), Year.now().getValue());
			ekoService.createBetaldBetalning(asp.getId(), Year.now().getValue() + 1);
		}	
		else 
			ekoService.createBetaldBetalning(asp.getId(), Year.now().getValue());
		asp.setBetalat(new Date());
		aspirantRepo.save(asp);
		stuvaOmListan();
	}	
	public void removeAspirant(int id)
	{
		 /* 
		 ** rensa bor alla poster i databasen som är relaterade till aspiranten
		 */
		aspirantRepo.deleteById(id);
		userRepo.deleteById(id);
		betRepo.delBetalningar(id); 
		visRepo.deleteByAspId(id);
		
	}
	public void saveAspirant(Aspirant aspirant)
	{
		aspirantRepo.save(aspirant);
	}
    public int getNextKoPlats()
    {
    	return aspirantRepo.getMaxTransactionId();
    }
    
    public boolean aspirantExists(String email)
    {
      	return aspirantRepo.findExistByEmail(email) == 1; 
    }
    
    public int visningar(int aspid)
    {
    	return visRepo.findNrByAspId(aspid);
    }
    
    public List<Aspirant>  stuvaOmListan()
    {
    	int cnt = 1, cntAkt = 1;
    	
    	List<Aspirant> aspLista = aspirantRepo.findAll(Sort.by("koPlats").ascending());
	   
		for(Aspirant asp: aspLista)
		{				
			asp.setKoPlats(cnt);
			asp.setKoPlatsAktiv(cntAkt);
			cnt++;
			if (asp.getKoStatus().equals("Aktiv"))
				cntAkt++;
			
			aspirantRepo.save(asp);
		}
		return aspLista;
    }
}
