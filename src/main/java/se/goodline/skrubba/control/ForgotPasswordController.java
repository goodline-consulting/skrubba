package se.goodline.skrubba.control;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.weaver.bcel.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import net.bytebuddy.utility.RandomString;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.service.AspirantService;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.SkrubbaUserDetailsService;

@Controller
public class ForgotPasswordController 
{
	
	@Autowired
    private EmailService mailService;
	
	@Autowired
	private AspirantService aspirantService;
	
    @Autowired
    private SkrubbaUserDetailsService userService;
     
    @GetMapping("/forgot_password")
    public String showForgotPasswordForm() 
    {
    	return "/forgot_password";	
    }
 
    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request, Model model) 
    {
    	String email = request.getParameter("email");
    	String token = RandomString.make(30);
    	//EmailService mailService = new EmailService();
    	
    	try 
    	{
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = getSiteURL(request) + "/reset_password?token=" + token;
            mailService.sendPasswordLink(email, resetPasswordLink);
            model.addAttribute("message", "Vi har skickat ett mail med en länk för att återställa ditt passord till " + email);
             
        } 
    	catch (UsernameNotFoundException ex) 
    	{
            model.addAttribute("error", ex.getMessage());
        } 
    	catch (MessagingException | UnsupportedEncodingException e) 
    	{
            model.addAttribute("error", "Ett fel uppstod när e-post skulle skickas");
        }
                	
    	return "/logout.html";
    }
    
    private static String getSiteURL(HttpServletRequest request) 
    {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    } 
     
    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model)
    {
    	Optional<User> user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);
         
        if (user.isEmpty()) 
        {
            model.addAttribute("error", "Ogiltigt länk");
            return "/message";
        }         
    	return "/reset_password";
    }
    
    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
         
        Optional<User> user = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Återställning av lösenord");
         
        if (user.isEmpty()) 
        {
            model.addAttribute("error", "Ogiltigt användarsigill");
            return "/message";
        } 
        else 
        {           
            userService.updatePassword(user.get(), password);
             
            model.addAttribute("message", "Ditt lösenord har ändrats.");
        }
         
        return "/message";
    }
    @PostMapping("/send_reset_link")
    public String processSendPasswordLink(HttpServletRequest request, Model model) 
    {
        /*
    	String token = request.getParameter("token");
        String password = request.getParameter("password");
        */ 
    	int id = Integer.parseInt(request.getParameter("id"));
    	User user = userService.getUser(id);
    	String token = RandomString.make(30);
    	userService.updateResetPasswordToken(token, user.getUserName());
        String resetPasswordLink = getSiteURL(request) + "/reset_password?token=" + token;
    	model.addAttribute("Role", "ROLE_ADMIN");
		model.addAttribute("aspirant", aspirantService.getAspirantById(id));	
		model.addAttribute("ActionUrl", "/aspirant/edit/" + id);
		
        try 
        {
			mailService.sendPasswordLink(user.getUserName(), resetPasswordLink);
			//mailService.sendPasswordLink("robert@goodline.se", resetPasswordLink);
		} 
        catch (UnsupportedEncodingException e) 
        {
			e.printStackTrace();
		} 
        catch (MessagingException e) 
        {			
			e.printStackTrace();
		}
        model.addAttribute("message", "Vi har skickat ett mail med en länk för att återställa lösenordet till " + user.getUserName());
        //return request.getContextPath() + request.getRequestURI() + "/" + request.getParameter("id");
        return "/edit_aspirant"; // + request.getParameter("id");
    }
}
