package se.goodline.skrubba.control;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.goodline.skrubba.model.Andring;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.repository.AndringRepository;
import se.goodline.skrubba.service.LoggService;

@Controller
public class AndringController 
{
	@Autowired
	LoggService loggService;

	@Autowired
	AndringRepository andringRepo;
		
	@GetMapping("/andringar")
	public String listaAndringar(Model model) 
	{
		List<Andring> andringLista = andringRepo.findAll();		
		model.addAttribute("andringLista", andringLista);
	  	return "/andringar.html";      
	}  
	
	@PostMapping("/andring/delete/{id}")
	public ResponseEntity<String> deleteParam(@PathVariable int id)
	{
		Andring andring = andringRepo.getById(id);
		andringRepo.deleteById(id);
		
		loggService.add("DELETE", "Tar bort ändringspost för lott " + andring.getLottnr());
		return ResponseEntity.ok().body("{}");
	}
	     
}
