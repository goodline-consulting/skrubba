package se.goodline.skrubba.control;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Histpers;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.model.Urval;
import se.goodline.skrubba.model.Visning;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.HistPersRepository;
import se.goodline.skrubba.repository.TillSaluRepository;
import se.goodline.skrubba.repository.VisningRepository;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.VisningsService;


@Controller
public class VisningsController 
{
	
	@Autowired
	private VisningRepository visRepo;
	
	@Autowired
	private HistPersRepository histRepo;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	TillSaluRepository saluRepo;
	
	@Autowired
	VisningsService visService;;
	
	@GetMapping("/visning/valj/{id}/{lottnr}")
	public String editVisningUrval(@PathVariable int id, @PathVariable int lottnr, Model model) 
	{
		List<String> aspList = new ArrayList<String>();
		List<Aspirant> aspirantLista = new ArrayList<Aspirant>();
		Map<Integer, String> svarLista = new HashMap<>();
		Urval urval = new Urval();
		
		Tillsalu salu = saluRepo.getById(id);
		for (Visning visning : visRepo.findBySaluId(id))
		{	
			aspList.add("" + visning.getAsp());
			if (salu.getSaljdatum() == null)
				aspirantLista.add(aspRepo.getById(visning.getAsp()));
			else {
				Histpers histpers = histRepo.getById(visning.getAsp());
				Aspirant aspirant = new Aspirant();
				aspirant.setId(histpers.getId());
				aspirant.setFnamn(histpers.getFnamn());
				aspirant.setEnamn(histpers.getEnamn());
				aspirant.setInskriven(histpers.getInskriven());
				aspirant.setKoPlats(visning.getKo_plats());
				aspirantLista.add(aspirant);
				svarLista.put(aspirant.getId(), visning.getSvar());
			}			
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
		{
			model.addAttribute("aspirantlista", aspirantLista);
			model.addAttribute("svarlista", svarLista);
		}	
	  	return "/visning_valj.html";      
	}  
	
	@GetMapping("/visning/svar/{id}/{lottnr}")
	public String editVisningSvar(@PathVariable int id, @PathVariable int lottnr, Model model) 
	{
		
		List<Aspirant> aspirantLista = new ArrayList<Aspirant>();
		Map<Integer, String> svarLista = new HashMap<>();
		List<String> svarOptions = Arrays.asList("", "Ja", "Nej");
		Tillsalu salu = saluRepo.getById(id);
		for (Visning visning : visRepo.findBySaluId(id))
		{	
			Histpers histpers = histRepo.getById(visning.getAsp());
			Aspirant aspirant = new Aspirant();
			aspirant.setId(histpers.getId());
			aspirant.setFnamn(histpers.getFnamn());
			aspirant.setEnamn(histpers.getEnamn());
			aspirant.setInskriven(histpers.getInskriven());
			aspirant.setKoPlats(visning.getKo_plats());
			aspirantLista.add(aspirant);
			svarLista.put(aspirant.getId(), visning.getSvar());					
		}			
		aspirantLista.sort(Comparator.comparing(Aspirant::getEnamn));
		model.addAttribute("lott", salu.getLottnr());
		model.addAttribute("datum", salu.getVisdatum());
		model.addAttribute("sald", salu.getSaljdatum());
		model.addAttribute("lottnr", lottnr);			
		model.addAttribute("actionUrl", "/visning/savesvar/" + id);
		model.addAttribute("tillbakaUrl", "/tillsalulista/" + lottnr);		
		model.addAttribute("aspirantlista", aspirantLista);
		model.addAttribute("svarlista", svarLista);
		model.addAttribute("svarOptions", svarOptions);	
	  	return "/visning_svar.html";      
	}  
	
	@PostMapping("/visning/save/{id}")
	public String saveVisning(@PathVariable int id, Urval valda, @Param("lottnr") int lottnr, Model model) 
	{		
		visService.uppdateraVisningar(id, valda.getValda());
		return "redirect:/tillsalulista/" + lottnr;		
	}
	
	@PostMapping("/visning/savesvar/{id}")
	public String saveSvar(@PathVariable int id, @RequestParam Map<String, String> svarlista, Model model) 
	{				
        for (Map.Entry<String, String> entry : svarlista.entrySet()) 
        {
        	String key = entry.getKey();
        	if (key.startsWith("svar")) 
        	{
        		int aspirantId = Integer.valueOf(key.substring(4));        		
        		Optional<Visning> visning = visRepo.findById(id, aspirantId);
        		if (visning.isPresent())
        		{
        			visning.get().setSvar(entry.getValue().equals("") ? null : entry.getValue());
        			visRepo.save(visning.get());       			
        		}
        	}	
		}		
		return "redirect:/tillsalulista/0";		
	}
	     
}
