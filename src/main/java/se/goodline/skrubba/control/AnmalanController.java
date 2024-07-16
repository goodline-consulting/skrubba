package se.goodline.skrubba.control;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import se.goodline.skrubba.model.Anmalan;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.userExistsException;
import se.goodline.skrubba.repository.AnmalanRepository;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.service.AspirantService;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.LoggService;



@Controller
public class AnmalanController {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	ParamRepository paramRepo;
	
	@Autowired
	AnmalanRepository anmRepo;
	
	@Autowired
	LoggService loggService;
	
	@Autowired
	AspirantService aspService;
	
	@Autowired
	EmailService mailService;
	    
	@GetMapping("/anmalan")
    public String anmalan(Model model) 
    {   
    	Anmalan anm = new Anmalan();
    	model.addAttribute("anmalan", anm);
    	return "/anmalan.html";
    		
    }
	@PostMapping("/anmalan")
	public String nyAnmalan(@ModelAttribute("anmalan") Anmalan anm, Model model) 
	{	
		// kolla att det inte redan finns en med samma email i kön eller bland anmälningarna.
		
		if (aspRepo.findExistByEmail(anm.getEmail()))
		{
			model.addAttribute("errmess", "Det finns redan en person i kön med mailadress " + anm.getEmail() + ". Använd en annan mailadress eller klicka på Avbryt");
			model.addAttribute("anmalan", anm);
			return "/anmalan.html";
		}
		else if (anmRepo.findExistByEmail(anm.getEmail()))
		{
			model.addAttribute("errmess", "Det finns redan en anmälan till vår stugkö från en person med mailadress " + anm.getEmail() + ". <br>Om den anmälan är gjord av dig, klicka på avbryt. Använd annars en annan mailadress.");
			model.addAttribute("anmalan", anm);
			return "/anmalan.html";
		}
		anmRepo.save(anm);
		model.addAttribute("message", "Tack för din anmälan " + anm.getFnamn() + "!<br>" + 
		                              "Du är nu anmäld till kön. När vi kontrollerat din anmälan kommer du att få ett bekräftelsemail med betalningsinformation.<br>" +
				                      "När din betalning kommit oss tillhanda så kommer du att få ett email som bekräftar att vi tagit emot din betalning.<br>" + 
		                              "Mailet innehåller även en länk till vårt kösystem där du kan ändra dina kontaktuppgifer, se din köplats, din senaste betalning mm.<br><br>" +
				                      "Med vänlig hälsing, Styrelsen Skrubba koloniträdgårdsföreing");
		
		// Skicka mail till styrelsen om att de inkommit en anmälan till stugkön
		try 
		{
			mailService.sendEmail("styrelsen@skrubba.se", 
					              "Anmälan till stugkön",
					              "Det har kommit en anmälan till stugkön ifrån " + anm.getFnamn() + " " + anm.getEnamn() + " (" + anm.getEmail() + ")");
		} 
		catch (MessagingException | IOException e) 
		{			
			System.err.println(e.getMessage());	
			e.printStackTrace();
		}
		return "/anmalan.html";		
	}	
	
	@GetMapping("/anmalningar")
	public String anmalningar(Model model) 
	{		
		model.addAttribute("Anmalningar", anmRepo.findAll());        
		return "/anmalningar.html";
	}
	
	@GetMapping("/anmalningar/add/{id}")
	public String addAnmalan(@PathVariable int id, Model model)
	{
		// skapa user och aspirant, lägg in kön
		Anmalan anm = anmRepo.getById(id);
		try 
		{
			Aspirant asp = aspService.newAspirant(anm);			
			model.addAttribute("message", asp.getFnamn() + " " + asp.getEnamn() + " har lagts in i kön på plats " + asp.getKoPlats());
			anmRepo.deleteById(id);
		} 	
		catch (Exception e) 
		{			
			model.addAttribute("message", "Det uppstod ett fel när email skulle skickas till " + anm.getEmail());
		}			
		model.addAttribute("Anmalningar", anmRepo.findAll());    
		return "/anmalningar.html";
	}
	
	@GetMapping("/anmalningar/confirm/{id}")
	public String confirmAnmalan(@PathVariable int id, Model model)
	{
		// skapa user och aspirant, lägg in kön
		Anmalan anm = anmRepo.getById(id);
		try 
		{
			mailService.sendEmail(anm.getEmail(), 
		              "Bekräftelse på Anmälan till Skrubbas stugkö",
		              "Hej " + anm.getFnamn() + ",\nVi har godkänt din anmälan till stugkön i Skrubba koloniträdgårdsförening.\n" +
		              "Du hamnar i kön när du betalat in 250:- till vårt bankgiro 5287-7172. Använd " + 1000 + anm.getId() + " som betalningsreferens.\n" + 
		              "När pengarna kommit oss tillhanda så får du ett bekräftelsemail på betalningen.\n" + 
		              "Mailet innehåller även inloggnigsuppgifter till vårt kösystem där du kan följa din köplats, se dina betalningar, erbjudanden om visningar mm.\n\n" + 
		              "Med vänlig hälsning, Styrelsen Skrubba koloniträdgårdsförening");			
			model.addAttribute("message", "Bekräftelsemail har skickats till " + anm.getFnamn() + " " + anm.getEnamn() + " (" + anm.getEmail() + ")");
			// loggas i mailService ssssssloggService.add("MAIL", "Bekräftele på anmälan till kön har skickats till " +  anm.getEmail());
			anm.setMailat(new Date());
			anmRepo.save(anm);
		} 	
		catch (Exception e) 
		{			
			model.addAttribute("message", "Det uppstod ett fel när email skulle skickas till " + anm.getEmail());
		}
		
		
		model.addAttribute("Anmalningar", anmRepo.findAll());    
		return "/anmalningar.html";
	}
	
	@GetMapping("/anmalningar/delete/{id}")
	public String deleteAnm(@PathVariable int id, Model model)
	{
		Anmalan anm = anmRepo.getById(id);
		model.addAttribute("message", "Anmälan ifrån " + anm.getFnamn() + " " + anm.getEnamn() + " har raderats!");
		loggService.add("DELETE,", "Anmälan ifrån " + anm.getFnamn() + " " + anm.getEnamn() + " har raderats.");
		anmRepo.deleteById(id);
		model.addAttribute("Anmalningar", anmRepo.findAll());    
		return "/anmalningar.html";
	}
}		

