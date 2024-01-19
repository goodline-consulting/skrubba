package se.goodline.skrubba.control;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.weaver.loadtime.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
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
import se.goodline.skrubba.service.SkrubbaUserDetailsService;

@Controller
public class VisningsController 
{
	
	@Autowired
	private VisningRepository visRepo;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	TillSaluRepository saluRepo;
	
	
	
	@GetMapping("/visning/valj/{id}/{lottnr}")
	public String editVisningUrval(@PathVariable int id, @PathVariable int lottnr, Model model) 
	{
		List<String> aspList = new ArrayList<String>();
		List<Aspirant> aspirantLista = new ArrayList<Aspirant>();
		
		Urval urval = new Urval();
		
		Tillsalu salu = saluRepo.getById(id);
		for (Visning visning : visRepo.findBySaluId(id))
		{	
			aspList.add("" + visning.getAsp());
			aspirantLista.add(aspRepo.getById(visning.getAsp()));
		}	
		urval.setValda(aspList);	
		model.addAttribute("lott", salu.getLottnr());
		model.addAttribute("datum", salu.getVisdatum());
		model.addAttribute("sald", salu.getSaljdatum());
		model.addAttribute("lottnr", lottnr);	
		model.addAttribute("urval", urval);
		model.addAttribute("actionUrl", "/visning/save/" + id);
		model.addAttribute("tillbakaUrl", "/tillsalulista/" + lottnr);
		if (salu.getSaljdatum() == null)
			model.addAttribute("aspirantlista", aspRepo.findByActive());
		else
			model.addAttribute("aspirantlista", aspirantLista);
	  	return "/visning_valj.html";      
	}  
	
	@PostMapping("/visning/save/{id}")
	public String saveVisning(@PathVariable int id, Urval valda, @Param("lottnr") int lottnr, Model model) 
	{		
		visRepo.deleteBySaluId(id);
		
		for (String vald: valda.getValda())
		{
			//System.out.println(visa.toString());
			visRepo.save(new Visning(id, Integer.parseInt(vald)));
		};
		
		return "redirect:/tillsalulista/" + lottnr;		
	}
	
	     
}
