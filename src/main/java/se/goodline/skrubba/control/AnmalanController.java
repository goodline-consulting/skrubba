package se.goodline.skrubba.control;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
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
		// kolla att det inte redan finns en med samma email
		anmRepo.save(anm);
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
	
	@GetMapping("/anmalningar/delete/{id}")
	public String deleteAnm(@PathVariable int id, Model model)
	{
		Anmalan anm = anmRepo.getById(id);
		model.addAttribute("message", "Anmälan ifrån " + anm.getFnamn() + " " + anm.getEnamn() + " har raderats!");
		anmRepo.deleteById(id);
		model.addAttribute("Anmalningar", anmRepo.findAll());    
		return "/anmalningar.html";
	}
}		

