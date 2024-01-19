package se.goodline.skrubba.control;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import net.bytebuddy.utility.RandomString;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.SkrubbaUserDetailsService;

@Controller
public class AdminController 
{
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	SkrubbaUserDetailsService userService;
	
	@Autowired
	AspirantRepository aspRepo;
	
	
    /* 
	@GetMapping("/aspirantlista")
	public String aspirantLista(Model model) 
	{
		List<Aspirant> aspLista = aspRepo.findAll();
	  	//Aspirant aspirant = new Aspirant();
		model.addAttribute("aspLista", aspLista);
		return "/aspirantlista.html";      
	}
	*/	
	/*
	@GetMapping("/aspirant/new")
	public String newAspirantForm(Model model) 
	{
	  	Aspirant aspirant = new Aspirant();
		model.addAttribute("aspirant", aspirant);
		model.addAttribute("Role", "ROLE_ADMIN");
		return "/edit_aspirant.html";      
	}  
	@PostMapping("/aspirant/new")
	public String newAspirant(HttpServletRequest request, @ModelAttribute("aspirant") Aspirant aspirant, Model model) 
	{		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		EmailService emailService = new EmailService();
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
            model.addAttribute("error", ex.getMessage());
        } 
    	catch (MessagingException | UnsupportedEncodingException e) 
    	{
            model.addAttribute("error", "Ett fel uppstod när e-post skulle skickas");
        }
        Optional <User> user = userRepo.findByUserName(aspirant.getEmail());
        aspirant.setId(user.get().getId());
        aspRepo.save(aspirant);
		return "/index.html";      
	}  
    
    private static String getSiteURL(HttpServletRequest request) 
    {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }     
	*/
}
