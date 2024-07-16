package se.goodline.skrubba.control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import net.bytebuddy.utility.RandomString;
import se.goodline.skrubba.model.Andring;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.repository.AndringRepository;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.LoggRepository;
import se.goodline.skrubba.repository.TillSaluRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.repository.VisningRepository;
import se.goodline.skrubba.service.AspirantService;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.KoloniLoader;
import se.goodline.skrubba.service.LoggService;
import se.goodline.skrubba.service.SkrubbaUserDetailsService;


@Controller
public class KolonilottController 
{
	@Autowired
	private AspirantService aspirantService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	KoloniLottRepository lottRepo;
	
	@Autowired
	TillSaluRepository saluRepo;
	
	@Autowired
	VisningRepository visRepo;
	
	@Autowired
	private LoggService loggService;
	
	@Autowired
	AndringRepository andringRepo;
	
	@GetMapping("/kolonilista")
	public String koloniLista(Model model) 
	{		
		List<Kolonilott> lottLista = lottRepo.findAll();
	  	//Aspirant aspirant = new Aspirant();
		model.addAttribute("lottLista", lottLista);
		return "/kolonilista.html";      
	}

	@GetMapping("/kolonilott/edit/{lottnr}")
	public String showKoloniForm(@PathVariable int lottnr, Model model) 
	{		
		Kolonilott lott = lottRepo.getById(lottnr);	
		/*
		Optional<Tillsalu> salu = saluRepo.findPagaende(lottnr);
		
		if (salu.isPresent())
			//model.addAttribute("saluid", salu.get().getId());
			lott.setTillsalu(true);
		*/	
		model.addAttribute("lott", lott);
		return "/edit_kolonilott.html";
	}
	
	@PostMapping("kolonilott/edit/")
	public String updateKoloniForm(@ModelAttribute("lott") Kolonilott lott,	Model model) 
	{	
		Optional<Tillsalu> salu = null;
		/*
		if (lott.isTillsalu())
		{
			salu = saluRepo.findPagaende(lott.getLottnr());
			if (salu.isEmpty())
			{
				Tillsalu ts = new Tillsalu();
				ts.setLottnr(lott.getLottnr());
				saluRepo.save(ts);
			}
		}
		else
		{
			salu = saluRepo.findPagaende(lott.getLottnr());
			if (salu.isPresent())
			{
				visRepo.deleteBySaluId(salu.get().getId());
				saluRepo.delete(salu.get());				
			}
		}
		Kolonilott orgLott = lottRepo.getByLottnr(lott.getLottnr());
		orgLott.setTillsalu(lott.isTillsalu());
		lottRepo.save(orgLott);
		*/
		return "redirect:/kolonilista";		
	}
	
	@PostMapping("kolonilott/upload/")
	public String uploadFile(@RequestParam("skrubbafil") MultipartFile file, Model model)
	{
		KoloniLoader koloniLoader = new KoloniLoader();
		try 
		{
			if (!koloniLoader.loadKolonier(file.getInputStream()))
			{
				model.addAttribute("message", "Fel vid läsning av filen från medlemsregistret, Undersök files!");
				return koloniLista(model);
			}
		} 
		catch (IOException e) 
		{
			model.addAttribute("message", "Fel vid uppladdning av filen, försök igen" + e.getLocalizedMessage());
			return koloniLista(model);
		}
		
		ArrayList<Kolonilott> lottList = koloniLoader.getLotter();		
		for (Kolonilott lott : lottList)
		{			
		
			Optional <Kolonilott> orgLottOpt = lottRepo.findById(lott.getLottnr());
			if (orgLottOpt.isPresent())
			{
				// Kolla om något av namn, hemadress, lgh, postnr eller postor är ändrat.
				Kolonilott orgLott = orgLottOpt.get();
			
				if ((orgLott.getAgare().compareToIgnoreCase(lott.getAgare()) != 0) ||
				    (orgLott.getHemadress().compareToIgnoreCase(lott.getHemadress()) != 0) ||
				    (orgLott.getPostnr().compareToIgnoreCase(lott.getPostnr()) != 0) ||
				    (orgLott.getPostort().compareToIgnoreCase(lott.getPostort()) != 0)) 	
				{
				    	Andring andring = new Andring(new Date(), loggService.getUser(), lott.getLottnr(), "");
				    	andring.setNote(lott.getAgare() + "\n" +
				    					lott.getHemadress() + "\n" +
				    					lott.getPostnr() + " " +
				    					lott.getPostort());
				    	andringRepo.save(andring);
				    	
				}
				orgLott.setAgare(lott.getAgare());
				orgLott.setEmail(lott.getEmail());
				orgLott.setHemadress(lott.getHemadress());
				orgLott.setLgh(lott.getLgh());
				orgLott.setPostnr(lott.getPostnr());
				orgLott.setPostort(lott.getPostort());
				orgLott.setTelefon(lott.getTelefon());
				lottRepo.save(orgLott);		
			}
			else		
				lottRepo.save(lott);
			//System.out.println(orgLott.toString());			
		}
		model.addAttribute("message", "Registret är uppdaterat!");
			
		return koloniLista(model);
	}
		
	@GetMapping("/tillsalulista/{lottnr}")
	public String tillSaluLista(@PathVariable int lottnr, Model model) 
	{		
		List<Tillsalu> saluLista = null;
		if (lottnr == 0)
			saluLista = saluRepo.findAll();
		else
			saluLista = saluRepo.findByLottNr(lottnr);
	  	model.addAttribute("salulista", saluLista);
	  	model.addAttribute("urval", lottnr);
		return "/tillsalulista.html";      
	}
	
	
	@GetMapping("/tillsalu/new/{lottnr}")
	public String newTillSaluForm(@PathVariable int lottnr, Model model) 
	{
		/*
		Tillsalu tillSalu = new Tillsalu();
		
		Kolonilott lott = lottRepo.getById(lottnr);
		if (lott.isTillsalu())
			tillSalu = saluRepo.findByLottNr(lottnr);
		else
		{
			tillSalu = new Tillsalu();
			tillSalu.setLottnr(lottnr);			
		}
		*/
		Tillsalu tillSalu = new Tillsalu();		
		tillSalu.setLottnr(lottnr);
	  	model.addAttribute("tillsalu", tillSalu);
		model.addAttribute("ActionUrl", "/tillsalu/save");	
		model.addAttribute("tillbakaUrl", "/tillsalulista/" + lottnr);
		return "/edit_tillsalu.html";      
	}  
	
	@GetMapping("/tillsalu/edit/{id}/{urval}")
	public String editTillSaluForm(@PathVariable int id, @PathVariable int urval, Model model) 
	{
	  	Tillsalu tillSalu = saluRepo.getById(id);	  	
	  	model.addAttribute("tillsalu", tillSalu);
		model.addAttribute("ActionUrl", "/tillsalu/save");
		model.addAttribute("tillbakaUrl", "/tillsalulista/" + urval);
		return "/edit_tillsalu.html";      
	}  
	
	@PostMapping("/tillsalu/save")
	public String saveTillSalu(@ModelAttribute("tillsalu") Tillsalu tillSalu,  Model model) 
	{		
		Kolonilott lott = lottRepo.getByLottnr(tillSalu.getLottnr());
		if (lott == null)
		{
			model.addAttribute("message", "Det finns ingen lott med nummer " + tillSalu.getLottnr());
			model.addAttribute("tillsalu", tillSalu);
			model.addAttribute("ActionUrl", "/tillsalu/save");
			model.addAttribute("tillbakaUrl", "/tillsalulista/0");
			return "/edit_tillsalu.html";      
		}
		Optional<Tillsalu> salu = saluRepo.findPagaende(lott.getLottnr());
		if (salu.isPresent() && salu.get().getId() != tillSalu.getId())
		{
			model.addAttribute("message", "Det finns redan en pågående försäljning på lott nummer " + tillSalu.getLottnr());
			model.addAttribute("tillsalu", tillSalu);
			model.addAttribute("ActionUrl", "/tillsalu/save");
			model.addAttribute("tillbakaUrl", "/tillsalulista/0");
			return "/edit_tillsalu.html";      
		}	
		lott.setTillsalu(tillSalu.getSaljdatum() != null ? false : true);
		lottRepo.save(lott);
		saluRepo.save(tillSalu);		
		return "redirect:/tillsalulista/0";	
	}
	
	@GetMapping("/tillsalu/remove/{id}")
	public String removeTillSalu(@PathVariable int id, Model model) 
	{		    
		    Tillsalu tillsalu = saluRepo.getById(id);
		    if (tillsalu.getSaljdatum() != null)
		    {
		    	model.addAttribute("message", "Du kan inte ta bort en avslutad försäljning");
		    }
		    visRepo.deleteBySaluId(id);
		    saluRepo.deleteById(id);
		    return "redirect:/tillsalulista/0";	      		    	
	}	
	
	private static String getSiteURL(HttpServletRequest request) 
    {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }     
}
