package se.goodline.skrubba.control;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.weaver.loadtime.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
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
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Logg;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.model.Urval;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.model.Visning;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.TillSaluRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.repository.VisningRepository;
import se.goodline.skrubba.service.AspirantService;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.LoggService;
import se.goodline.skrubba.service.SkrubbaUserDetailsService;

@Controller
public class LoggController 
{
	
	
	@Autowired
	LoggService loggService;
		
	@GetMapping("/logg")
	public String loggUrval(Model model) 
	{
		List<Logg> logg = loggService.getAll();		
		model.addAttribute("logg", logg);
	  	return "/logg.html";      
	}  
	
	@PostMapping("/logg/filter")
	public String filterLogg(@RequestParam("user") String userName, @RequestParam(value = "dFrom", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date dFrom, @RequestParam(value = "dTo", required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date dTo, Model model) 
	{		
		List<Logg> logg = null;
		//System.out.println("user: " + userName + " dFrom: " + (dFrom == null? "null": dFrom) + " dTo: " + (dTo == null? "null" : dTo));
		if (dTo == null && dFrom != null)
		{				
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(dFrom);	        
	        calendar.set(Calendar.HOUR_OF_DAY, 23);
	        calendar.set(Calendar.MINUTE, 59);
	        calendar.set(Calendar.SECOND, 59);
	        calendar.set(Calendar.MILLISECOND, 999);	
	        dTo = calendar.getTime();
		}	
		if (userName.equals("") && dFrom == null)
			 logg = loggService.getAll();
		else if (!userName.equals("") && dFrom == null)
			logg = loggService.getByUser("%" + userName + "%");
		else if (dFrom != null && userName.equals(""))
			logg = loggService.getByDates(dFrom, dTo);
		else
			logg = loggService.getByAll(userName, dFrom, dTo);
		model.addAttribute("logg", logg);
	  	return "/logg.html";	}
	
	     
}
