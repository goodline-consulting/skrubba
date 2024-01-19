package se.goodline.skrubba.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


//@Controller
public class LoginController 
{
	@GetMapping("/login")
	public String login()
	{
		System.out.println("*******LOGGOUT");
		return "/login";
	}
	/*
	@GetMapping("/logout")  
    public String logoutPage(HttpServletRequest request, HttpServletResponse response, Model model) {  
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();  
        if (auth != null){      
           new SecurityContextLogoutHandler().logout(request, response, auth); 
           model.addAttribute("message", "Du har blivit uloggad!");
           return "/logout"; 
        }  
        else 
        	return ("redirect:/userpage");
     }
      */
}
