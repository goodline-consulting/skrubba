package se.goodline.skrubba.control;

import java.security.Principal;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.repository.AndringRepository;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.service.LoggService;



@Controller
public class HomeController {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	ParamRepository paramRepo;
		
	@Autowired
	LoggService loggService;
	
	@Autowired
	AndringRepository andringRepo;
		
	@GetMapping("/login")
	public String login()
	{
		return "/login";
	}
	
	
	@GetMapping("/userpage/{id}")
	public String userpage(@PathVariable int id, Model model) 
	{		
		model.addAttribute("aspirant", aspRepo.getById(id));
        return "/userpage.html";
	}
    
	@GetMapping("/")
    public String home(Principal loggedInUser, Model model) 
    {   
    	
    	Optional<User> user = userRepo.findByUserName(loggedInUser.getName());
    	loggService.add("LOGIN", "Login by " + user.get().getUserName() + " with role " + user.get().getRoles());
    	
    	if (user.get().getRoles().compareTo("ROLE_ADMIN") == 0)
    	{
    		if (andringRepo.andringExists())
    			model.addAttribute("message", "Det finns ohanterade ändringar i registret för kolonilotter. Klicka på Lotter->Uppdateringar!");
    		model.addAttribute("admin", user.get());
    		return ("/index.html");
    	}	
    	else 
    	{	
    		model.addAttribute("aspirant", aspRepo.getById(user.get().getId()));
    		return "/userpage.html";
    	}	
    }
	@GetMapping("/params")
	public String userpage(Model model) 
	{		
		model.addAttribute("params", paramRepo.findAll());
        model.addAttribute("ny_param", new Param());
		return "/parametrar.html";
	}
	
	@PostMapping("/param/ny")
	public String saveParam(@ModelAttribute("ny_param") Param param_ny, Model model) 
	{					
		paramRepo.save(param_ny);
		loggService.add("CREATE", "Skapar ny parameter " + param_ny.getParamName());
		return "redirect:/params";		
	}	
	
	@PostMapping("/param/delete/{id}")
	public ResponseEntity<String> deleteParam(@PathVariable String id)
	{
		Optional<Param> par = paramRepo.findByParamName(id);
		paramRepo.deleteByParamName(id);
		loggService.add("DELETE", "Tar bort parameter " + par.get().getParamName());
		return ResponseEntity.ok().body("{}");
	}
}		

