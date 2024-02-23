package se.goodline.skrubba.control;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

import net.bytebuddy.utility.RandomString;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.model.Visning;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.TillSaluRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.repository.VisningRepository;
import se.goodline.skrubba.service.AspirantService;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.SkrubbaUserDetailsService;


@Controller
public class AspirantController 
{
	@Autowired
	private AspirantService aspirantService;
	
	@Autowired
	private SkrubbaUserDetailsService userService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	VisningRepository visRepo;
	
	@Autowired
	TillSaluRepository saluRepo;
	
	
	@GetMapping("/aspirantlista")
	public String aspirantLista(Model model) 
	{		
		List<Aspirant> aspLista = aspRepo.findAll();
		
		Map<Integer, List<Tillsalu>> visLista = new HashMap<>(); 
		for (Aspirant asp: aspLista)
		{
			asp.setVisningar(aspirantService.visningar(asp.getId()));
			if (asp.getVisningar() > 0)
			{	
				List<Tillsalu> saluLista = new ArrayList<>();
				for (Visning vis: visRepo.findByAspId(asp.getId()))
					saluLista.add(saluRepo.findById(vis.getId()).get());
				visLista.put(asp.getId(), saluLista);
			}	
		}

		model.addAttribute("aspLista", aspLista);
		model.addAttribute("visLista", visLista);
		return "/aspirantlista.html";      
	}
	
	@GetMapping("/aspirant/new")
	public String newAspirantForm(Model model) 
	{
	  	Aspirant aspirant = new Aspirant();
	  	aspirant.setKoPlats(aspirantService.getNextKoPlats() + 1);
		model.addAttribute("aspirant", aspirant);
		model.addAttribute("Role", "ROLE_ADMIN");
		model.addAttribute("ActionUrl", "new");	
		return "/edit_aspirant.html";      
	}  
	
	@PostMapping("/aspirant/new")
	public String newAspirant(HttpServletRequest request, @ModelAttribute("aspirant") Aspirant aspirant, Model model) 
	{		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		// if (aspirantService.aspirantExists(aspirant.getEmail()))
		if (userService.userExists(aspirant.getEmail()))
		{
			model.addAttribute("message", "Det finns redan en person med email adress" + aspirant.getEmail() + "!");
			model.addAttribute("aspirant", aspirant);
			model.addAttribute("Role", "ROLE_ADMIN");
			model.addAttribute("ActionUrl", "new");	
			return "/edit_aspirant.html"; 
		}
		//EmailService emailService = new EmailService();
		String token = RandomString.make(30); 	    	
		userRepo.save(new User(aspirant.getEmail(), passwordEncoder.encode(aspirant.getEmail()), true, "ROLE_USER"));
		
    	try 
    	{
            userService.updateResetPasswordToken(token, aspirant.getEmail());
            String resetPasswordLink = getSiteURL(request) + "/reset_password?token=" + token;
            emailService.sendWelcomeLink(aspirant.getEmail(), resetPasswordLink);                        
        } 
    	catch (UsernameNotFoundException ex) 
    	{
            model.addAttribute("message", "Ett fel uppstod när personen skulle registreras\n" + ex.getMessage());
            model.addAttribute("aspirant", aspirant);
			model.addAttribute("Role", "ROLE_ADMIN");
			model.addAttribute("ActionUrl", "new");	
			return "/edit_aspirant.html"; 
        } 
    	catch (MessagingException | UnsupportedEncodingException e) 
    	{
            model.addAttribute("message", "Ett fel uppstod när e-post skulle skickas\n" + e.getMessage());
            model.addAttribute("aspirant", aspirant);
			model.addAttribute("Role", "ROLE_ADMIN");
			model.addAttribute("ActionUrl", "new");	
			return "/edit_aspirant.html"; 
        }
        
        aspirantService.newAspirant(aspirant);
		return "redirect:/aspirantlista";      
	}  
	
	
	@GetMapping("/aspirant/edit/{id}")
	public String editAspirantForm(@PathVariable int id, Principal loggedInUser, Model model) 
	{
		Optional<User> user = userRepo.findByUserName(loggedInUser.getName());
    	if (user.get().getRoles().compareTo("ROLE_ADMIN") == 0)
    		model.addAttribute("Role", "ROLE_ADMIN");
		model.addAttribute("aspirant", aspirantService.getAspirantById(id));	
		model.addAttribute("ActionUrl", "edit");
		return "/edit_aspirant";
	}
	
	@PostMapping("/aspirant/edit/{id}")
	public String editAspirantForm(@PathVariable int id, @ModelAttribute("aspirant") Aspirant aspirant,	@RequestParam("Role") String Role, Model model) 
	{
		String oldStatus;
		
		Aspirant oldAspirant = aspirantService.getAspirantById(id);
		
		oldStatus = oldAspirant.getKoStatus();
		
		oldAspirant.setAdress(aspirant.getAdress());
		oldAspirant.setEmail(aspirant.getEmail());
		oldAspirant.setEnamn(aspirant.getEnamn());
		oldAspirant.setFnamn(aspirant.getFnamn());
		if (Role.equals("ROLE_ADMIN"))
		{	
			oldAspirant.setBetalat(aspirant.getBetalat());
			oldAspirant.setInskriven(aspirant.getInskriven());
			oldAspirant.setKoPlats(aspirant.getKoPlats());
			oldAspirant.setKoStatus(aspirant.getKoStatus());
			oldAspirant.setUtbildad(aspirant.getUtbildad());
		}	
		oldAspirant.setPostAdress(aspirant.getPostAdress());
		oldAspirant.setPostnr(aspirant.getPostnr());
		oldAspirant.setTelefon(aspirant.getTelefon());
		
		aspirantService.saveAspirant(oldAspirant);
		System.out.print(oldStatus + " / " + aspirant.getKoStatus());
		// vi kollar om statusen är ändrad. I så fall skall vi räkna om köplatsen koPlatsAktiv
		if (oldStatus != oldAspirant.getKoStatus())
		{
			List<Aspirant> aspLista = aspirantService.stuvaOmListan();
		    System.out.println("Sorterar om listan");			
		}
		// vi uppdaterar User.email utan att kolla om den är ändrad. det är enklare hi hi..
		User user = userService.getUser(id);
		user.setUserName(aspirant.getEmail());
		userService.saveUser(user);
		
		if (Role.equals("ROLE_ADMIN"))
			return "redirect:/aspirantlista";
		else
		{	
			model.addAttribute("aspirant", aspirantService.getAspirantById(id));
			return "/userpage";
		}	
	}
	@GetMapping("/aspirant/change_password/{id}")
	public String showPasswordForm(@PathVariable int id, Model model) 
	{
		model.addAttribute("id", id);
		model.addAttribute("action", "/aspirant/change_password");
    	return "/change_password";	
	}
	
	@PostMapping("/aspirant/change_password")
	public String changePasswordForm(HttpServletRequest request, Model model) 
	{
		    int id = Integer.parseInt(request.getParameter("id"));
		    userService.updatePassword(id, request.getParameter("password"));
		    Aspirant aspirant = aspirantService.getAspirantById(id);
		    model.addAttribute("aspirant", aspirant);
		    
	    	return "/userpage";	
	}	
	
	@GetMapping("/aspirant/remove/{id}")
	public String removeAspirant(@PathVariable int id, Model model) 
	{			    
	    Aspirant aspirant = aspirantService.getAspirantById(id);
	    model.addAttribute("aspirant", aspirant);			
		model.addAttribute("id", id);
		model.addAttribute("cancelUrl", "/aspirant/edit/" + id);
    	return "/remove_aspirant";	
	} 
	
	@PostMapping("/aspirant/remove")
	public String removeAspirant(@ModelAttribute("aspirant") Aspirant aspirant, Model model) 
	{		    
		    int cnt = 1, cntAkt = 1;
		    System.out.println(aspirant.getEmail() + " " + aspirant.getId());
		    aspirantService.removeAspirant(aspirant.getId());
		    
			model.addAttribute("message", aspirant.getFnamn() + " " + aspirant.getEnamn() + " har plockats bort ifrån kön!");					
		    List<Aspirant> aspLista = aspirantService.stuvaOmListan();		    
			model.addAttribute("aspLista", aspLista);
			return aspirantLista(model);    		    	
	}	
	
	private static String getSiteURL(HttpServletRequest request) 
    {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }     
}
