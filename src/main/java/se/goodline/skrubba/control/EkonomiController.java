package se.goodline.skrubba.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.weaver.loadtime.Options;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import net.bytebuddy.utility.RandomString;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Betalning;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.model.Urval;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.model.Visning;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.BetalningRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.TillSaluRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.repository.VisningRepository;
import se.goodline.skrubba.service.AspirantService;
import se.goodline.skrubba.service.EkonomiService;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.KoloniLoader;
import se.goodline.skrubba.service.SkrubbaUserDetailsService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EkonomiController 
{
	
	@Autowired
	private BetalningRepository betRepo;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	EkonomiService ekoService;
	
	@Autowired 
	EmailService emailService;
	
	
	@GetMapping("/ekonomi")
	public String ekonomi(Model model) 
	{
		int antBet = aspRepo.getAmountPaid();
		model.addAttribute("antpers", aspRepo.getMaxTransactionId());
   		model.addAttribute("antpassiva", aspRepo.getAmountWithStatus("Passiv"));
   		model.addAttribute("antvilande", aspRepo.getAmountWithStatus("Vilande"));
   		model.addAttribute("antbet", antBet);
   		model.addAttribute("antobet", aspRepo.getAmountNotPaid());
   		model.addAttribute("kronor", (antBet * 250) + ":-");
		model.addAttribute("datum", new Date());
		return "/ekonomi.html";      
	}  
	
	
	@GetMapping("/ekonomi/fakturera")
	public String faktureraBetalning (RedirectAttributes redirectAttributes) 
	{	
		if (!emailService.mallExists("köavgift_passiva"))
		{
			redirectAttributes.addFlashAttribute("message", "Brevmallen 'köavgift_passiva' saknas. Fakturor kan inte skickas ut!");	
		}
		else if (!emailService.mallExists("köavgift"))
		{
			redirectAttributes.addFlashAttribute("message", "Brevmallen 'köavgift' saknas. Fakturor kan inte skickas ut!");	
		}
		else
		{
			ekoService.fakturera();
			
			redirectAttributes.addFlashAttribute("message", "Betalpost skapas och mail skickas till alla i kön i bakgrunden. Kontroller status på processen under Underhåll->Inställningar->Fakturaprocess");
		}	
		return "redirect:/aspirantlista";   		
	}	
	
	@GetMapping("/ekonomi/omfakturera")
	public String omfaktureraBetalning (RedirectAttributes redirectAttributes) 
	{	
		if (!emailService.mallExists("Obetald administrationsavgift"))
		{
			redirectAttributes.addFlashAttribute("message", "Brevmallen 'Obetald administrationsavgift' saknas. Påminnelser kan inte skickas ut!");	
		}
		else
		{	
			ekoService.omFakturera();		
			redirectAttributes.addFlashAttribute("message", "Betalningspåminnelser skickas i bakgrunden till alla i kön som inte betalat administrationsavgiften. Kontrollera status på processen under Underhåll->Inställningar->Betalningspåminnelse");
		}
		return "redirect:/aspirantlista";   		
	}	
	
	@PostMapping("ekonomi/updatevisma/")
	public ResponseEntity<InputStreamResource> updateVisma(@RequestParam("vismafil") MultipartFile file, Model model)
	{

		try 
		{
			 File mergedFile = ekoService.CreateMergedFile(file.getInputStream());
			 HttpHeaders headers = new HttpHeaders();
	         headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	         headers.setContentDispositionFormData("attachment", "Visma_merged.csv");
	         
	         InputStream mergedFileInputStream = new FileInputStream(mergedFile);
             InputStreamResource inputStreamResource = new InputStreamResource(mergedFileInputStream);
             model.addAttribute("message", mergedFile.getName() + " är skickad till din dator, updatera Visma med filen");
	         // Return a ResponseEntity with the merged file's InputStream and headers
             return ResponseEntity.ok()
                     .headers(headers)
                     .contentLength(mergedFile.length())
                     .body(inputStreamResource);
			
			
		} 
		catch (IOException e) 
		{
			model.addAttribute("message", "Fel vid uppladdning av filen, försök igen" + e.getLocalizedMessage());
			return ResponseEntity.status(500).body(null);
		}
		
	
//		model.addAttribute("message", mergedVismaFile + " är skickad till din dator, updatera Visma med filen");
//		//model.addAttribute("datum", new Date());	
//		return ekonomi(model);
	
	}
	/*
	@GetMapping("/ekonomi/rapport")
	public String ekoRapport(Model model)
	{
		
	}
	*/
}
