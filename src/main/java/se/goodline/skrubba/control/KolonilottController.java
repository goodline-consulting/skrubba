package se.goodline.skrubba.control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
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
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.TillSaluRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.repository.VisningRepository;
import se.goodline.skrubba.service.AspirantService;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.KoloniLoader;
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
		model.addAttribute("lott", lottRepo.getById(lottnr));	
		//model.addAttribute("ActionUrl", "/kolonilott/edit/");
		Optional<Tillsalu> salu = saluRepo.findPagaende(lottnr);
		
		if (salu.isPresent())
			model.addAttribute("saluid", salu.get().getId());
		return "/edit_kolonilott.html";
	}
	
	@PostMapping("kolonilott/edit/")
	public String updateKoloniForm(@ModelAttribute("lott") Kolonilott lott,	Model model) 
	{	
		Optional<Tillsalu> salu = null;
		
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
		
			Optional <Kolonilott> orgLott = lottRepo.findById(lott.getLottnr());
			if (orgLott.isPresent())
			{
				orgLott.get().setAgare(lott.getAgare());
				orgLott.get().setEmail(lott.getEmail());
				orgLott.get().setHemadress(lott.getHemadress());
				orgLott.get().setLgh(lott.getLgh());
				orgLott.get().setPostnr(lott.getPostnr());
				orgLott.get().setPostort(lott.getPostort());
				orgLott.get().setTelefon(lott.getTelefon());
				lottRepo.save(orgLott.get());		
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
	
	/*
	@GetMapping("/tillsalu/new/{lottnr}")
	public String newTillSaluForm(@PathVariable int lottnr, Model model) 
	{
		//Tillsalu tillSalu = null;
		Kolonilott lott = lottRepo.getById(lottnr);
		if (lott.isTillsalu())
			tillSalu = saluRepo.findByLottNr(lottnr);
		else
		{
			tillSalu = new Tillsalu();
			tillSalu.setLottnr(lottnr);
		}
	  	model.addAttribute("tillsalu", tillSalu);
		model.addAttribute("ActionUrl", "/tillsalu/save");	
		model.addAttribute("tillbaka", "/kolonilott/edit/" + lottnr);
		return "/edit_tillsalu.html";      
	}  
	*/
	@GetMapping("/tillsalu/edit/{id}/{urval}")
	public String editTillSaluForm(@PathVariable int id, @PathVariable int urval, Model model) 
	{
	  	Tillsalu tillSalu = saluRepo.getById(id);	  	
	  	model.addAttribute("tillsalu", tillSalu);
		model.addAttribute("ActionUrl", "/tillsalu/save");
		model.addAttribute("tillbaka", "/tillsalulista/" + urval);
		return "/edit_tillsalu.html";      
	}  
	
	@PostMapping("/tillsalu/save")
	public String saveTillSalu(@ModelAttribute("tillsalu") Tillsalu tillSalu, @Param("sparurlrl") String sparurl, Model model) 
	{		
		if (tillSalu.getSaljdatum() != null)
		{
			Kolonilott lott = lottRepo.getByLottnr(tillSalu.getLottnr());
			lott.setTillsalu(false);
			lottRepo.save(lott);
		}
		System.out.println("sparurlrl:" + sparurl);
		saluRepo.save(tillSalu);		
		return "redirect:" + sparurl;		
	}
	
	@PostMapping("/tillsalu/remove")
	public String removeTillSalu(@ModelAttribute("tillsalu") Tillsalu tillsalu, Model model) 
	{		    
		    
			return "/tillsalulista.html";      		    	
	}	
	
	private static String getSiteURL(HttpServletRequest request) 
    {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }     
}
