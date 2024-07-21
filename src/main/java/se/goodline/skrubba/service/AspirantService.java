package se.goodline.skrubba.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import net.bytebuddy.utility.RandomString;
import se.goodline.skrubba.model.Anmalan;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Betalning;
import se.goodline.skrubba.model.Histpers;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.model.userExistsException;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.BetalningRepository;
import se.goodline.skrubba.repository.HistPersRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.repository.VisningRepository;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
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
	
	@Autowired
	LoggService loggService;
	
	@Autowired
	SkrubbaUserDetailsService userService; 
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	HistPersRepository histRepo;
	
	public Aspirant getAspirantById(int id)
	{
		return aspirantRepo.getById(id);			
	}
	
	public Aspirant newAspirant(Aspirant asp) throws userExistsException, UnsupportedEncodingException, MessagingException, UsernameNotFoundException
	{
		String egenUrl = "http://stugko.skrubba.se";
		if (userService.userExists(asp.getEmail()))	
			throw new userExistsException("Det finns redan en användare med användarnamn" + asp.getEmail());
		Optional<Param> param = paramRepo.findByParamName("EgenUrl");
		if (param.isPresent())
			egenUrl = param.get().getParamValue();
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		userRepo.save(new User(asp.getEmail(), passwordEncoder.encode(asp.getEmail()), true, "ROLE_USER"));
		Optional <User> user = userRepo.findByUserName(asp.getEmail());
		asp.setId(user.get().getId());	
		String token = RandomString.make(30); 	    	
		
		if (LocalDate.now().getMonthValue() == 12)
		{	
			ekoService.createDummyBetalning(asp.getId(), Year.now().getValue());
			ekoService.createBetaldBetalning(asp.getId(), Year.now().getValue() + 1);
		}	
		else 
			ekoService.createBetaldBetalning(asp.getId(), Year.now().getValue());
		asp.setBetalat(new Date());
		asp.setKoPlats(getNextKoPlats() + 1);
		userService.updateResetPasswordToken(token, asp.getEmail());
        String resetPasswordLink = egenUrl + "/reset_password?token=" + token;
        emailService.sendWelcomeLink(asp.getEmail(), resetPasswordLink);  
		aspirantRepo.save(asp);
		loggService.add("CREATE", "Ny person, " + asp.getFnamn() + " " + asp.getEnamn() + " inlagd i kön på plats " + asp.getKoPlats());
		stuvaOmListan();
		
		return asp;
	}	
		
	public Aspirant newAspirant(Anmalan anm) throws UsernameNotFoundException, UnsupportedEncodingException, userExistsException, MessagingException 
	{
		Aspirant asp = new Aspirant(anm);
		return newAspirant(asp);
	}
	
	public void removeAspirant(int id)
	{
		 /* 
		 ** rensa bor alla poster i databasen som är relaterade till aspiranten
		 */
		Aspirant asp = aspirantRepo.getById(id);
		aspirantRepo.deleteById(id);
		userRepo.deleteById(id);
		betRepo.delBetalningar(id); 
		//visRepo.deleteByAspId(id);
		loggService.add("DELETE", asp.getFnamn() + " " + asp.getEnamn() + " med plats " + asp.getKoPlats() + " borttagen ur kön");
	}
	public void saveAspirant(Aspirant asp)
	{
		aspirantRepo.save(asp);
		if (asp.getBetalat() != null)
		{
			Betalning bet = betRepo.findLastBet(asp.getId());
			bet.setBetdatum(asp.getBetalat());
			betRepo.save(bet);
		}
		// Vi måste också uppdater HISTPERS
		
		Histpers histpers = histRepo.getById(asp.getId());
		histpers.setFnamn(asp.getFnamn());
		histpers.setEnamn(asp.getEnamn());
		histpers.setInskriven(asp.getInskriven());
		loggService.add("UPDATE", asp.getFnamn() + " " + asp.getEnamn() + " har uppdaterats ");
	}
    public int getNextKoPlats()
    {
    	
    	return aspirantRepo.getMaxTransactionId();
    }
    
    public boolean aspirantExists(String email)
    {
    	
      	return aspirantRepo.findExistByEmail(email); 
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
		loggService.add("UPDATE", "Stugkön har uppdaterats och räknats om");
		return aspLista;
    }
}
