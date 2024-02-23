package se.goodline.skrubba.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Betalning;
import se.goodline.skrubba.model.Brevmall;
import se.goodline.skrubba.model.EmailForm;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.BetalningRepository;
import se.goodline.skrubba.repository.BrevmallRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.service.EmailService;


@Controller
public class BetalningsController 
{
	
	@Autowired
	private BetalningRepository betRepo;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired	
	ParamRepository paramRepo;
	
	@Autowired	
	BrevmallRepository mallRepo;
	
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/betalning/lista/{id}")
	public String editVisningUrval(@PathVariable int id, Model model) 
	{
		Aspirant asp = aspRepo.findById(id);
		List<Betalning> betalLista = betRepo.findByAspId(id);
		
		model.addAttribute("aspnamn", asp.getFnamn() + " " + asp.getEnamn());
		model.addAttribute("betallista", betalLista);
		model.addAttribute("ny_betalning", new Betalning(0, asp.getId(),Integer.parseInt(paramRepo.findByParamName("Köavgift").get().getParamValue()), Year.now().getValue(), new Date(), null));
		return "/aspbetalningar.html";      
	}  
	@PostMapping("/betalning/registrera/{id}")
	public ResponseEntity<String> regBetalning(@PathVariable int id, @RequestBody String datum, Model model) 
	{			
		Optional<Betalning> betOpt = betRepo.findById(id);
		if (betOpt.isEmpty())
		{
			HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/plain");
            String errorMessage = "Avgiftspost med avinummer " + id + " saknas.";
            return ResponseEntity.ok(errorMessage);
            
		}
		else if (betOpt.get().getBetdatum() != null)
		{
			HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/plain");
            Aspirant asp = aspRepo.findById(betOpt.get().getAsp());
            String errorMessage = "Avgiftspost för " + asp.getFnamn() + " " + asp.getEnamn() + " med avinummer " + betOpt.get().getId() + " är redan betald." ;
            return ResponseEntity.ok(errorMessage);
            
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try 
        {
			Date betDatum = dateFormat.parse(datum);
			System.out.println("avi: " + id + " Datum: " + betDatum);
			Betalning bet = betOpt.get();
			Aspirant asp = aspRepo.findById(bet.getAsp());
			bet.setBetdatum(betDatum);
			asp.setBetalat(betDatum);
			betRepo.save(bet);
			// Skicka betalningsbekräftelse till torparn
			Brevmall bm = mallRepo.findByNamn("Betalningsbekräftelse");
			EmailForm em = new EmailForm(bm);
			emailService.sendBetalningsBekraftelse(em, asp);
			return ResponseEntity.ok("Betalning avseende år " + bet.getAr() + " för " + asp.getFnamn() + " " + asp.getEnamn() + " registrerad.\n" +
			                         "Betalningsbekräftelse är skickad till " + asp.getEmail() + ".");	
		} 
        catch (ParseException e) 
        {		
        	HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/plain");
            String errorMessage = "Felaktigt datumformat!";
            return ResponseEntity.ok(errorMessage);
		} 
        catch (Exception e) 
        {
			String errorMessage = "Fel: " + e.getMessage();
            return ResponseEntity.ok(errorMessage);
		}					
	}
	
	@PostMapping("/betalning/ny")
	public String saveBetalning(@ModelAttribute("ny_betalning") Betalning betalning, @RequestParam int summa, Model model) 
	{			
		try
		{
			betalning.setSumma(summa);
			Aspirant asp = aspRepo.findById(betalning.getAsp());
			asp.setBetalat(betalning.getBetdatum());
			betRepo.save(betalning);
			Brevmall bm = mallRepo.findByNamn("Betalningsbekräftelse");
			EmailForm em = new EmailForm(bm);
			emailService.sendBetalningsBekraftelse(em, asp);
		}
		catch (Exception e) 
		{
			model.addAttribute("message", "Det finns redan en betalning registrerad för år " + betalning.getAr() + "!" + e.getMessage());
		}
		return editVisningUrval(betalning.getAsp(), model);
	}	
	
	@PostMapping("/betalning/delete/{id}")
	public ResponseEntity<String> deleteBetalning(@PathVariable int id, Model model) 
	{		
		System.out.println("tar bort betalning");
		Optional<Betalning> bet = betRepo.findById(id);
		betRepo.delete(bet.get());
		return ResponseEntity.ok("{\"message\": \"Betalningen borttagen\"}"); // "redirect:/betalning/lista/" + bet.get().getAsp();		
	}	
}
