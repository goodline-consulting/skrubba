package se.goodline.skrubba.control;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

import org.aspectj.weaver.loadtime.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import net.bytebuddy.utility.RandomString;
import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Brevmall;
import se.goodline.skrubba.model.EmailForm;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Tillsalu;
import se.goodline.skrubba.model.Urval;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.model.Visning;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.BrevmallRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.repository.TillSaluRepository;
import se.goodline.skrubba.repository.UserRepository;
import se.goodline.skrubba.repository.VisningRepository;
import se.goodline.skrubba.service.AspirantService;
import se.goodline.skrubba.service.EmailService;
import se.goodline.skrubba.service.SkrubbaUserDetailsService;
import se.goodline.skrubba.utils.FileUtils;

@Controller
@SessionAttributes("emailForm")

public class MailController 
{
	
	@Autowired
	private VisningRepository visRepo;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	TillSaluRepository saluRepo;
	
	@Autowired
	KoloniLottRepository lottRepo;
	
	@Autowired
	BrevmallRepository mallRepo;
	
	@Autowired
	EmailService eService;
	
	@Autowired
	ParamRepository paramRepo;
	
	@Autowired
	ServletContext servletContext;
	
	@ModelAttribute("emailForm")
	public EmailForm createEmailForm() {
		EmailForm emailForm = new EmailForm(); 
		emailForm.setBilageLista(new ArrayList<>()); // Initialize your object
	    return emailForm;     
	}
	
	FileUtils fu = new FileUtils();
	
	@GetMapping("/mail")
	public String mainMailPage(@ModelAttribute("emailForm") EmailForm emailForm, Model model) 
	{		
		Urval urval = new Urval();
		model.addAttribute("urval", urval);		
		model.addAttribute("emailForm", emailForm);
		model.addAttribute("mallar", mallRepo.findAll());
		model.addAttribute("visningar",saluRepo.findPagaende());
		model.addAttribute("_urval", new String());
		System.out.println("emailForm:" + emailForm.getSubject());
	  	return "/mainmail.html";      
	}  
	
	@PostMapping("/mailurval")
	public String mailUrval(@Param("_urval") String _urval, @Param("_action") String _action, @ModelAttribute("emailForm") EmailForm emailForm, Urval valda, Model model) 
	{	
		
		boolean aspOrKollo = true;
		List<Aspirant> aspLista;
		List<Kolonilott> kolloLista;
		
		if (_action.equals("submit"))
		{
			switch (_urval) 
			{
		        case "queue":	        			        	
		        case "unpaid":	        	        	
		        case "aktiva":
		        case "vilande":	
		        case "passiva":
		        case "ejaktiva":	
		        	aspOrKollo = true;
		        	break;
		        case "members":			        	
		        case "area1":
		        case "area2":	            
		        case "area3":
		        case "area4":
		        case "area5":
		        case "area6":	
		        case "area7":	
		        case "area8":
		        	aspOrKollo = false;		        	
		        	break;	        
		        default:	           
		            break;
			}
			
			if (valda.getValda() != null)
			{	
				if (aspOrKollo)
				{
					aspLista = new ArrayList<Aspirant>();
					for (String vald: valda.getValda())
					{
					
						Aspirant asp = aspRepo.findById(Integer.parseInt(vald));
						aspLista.add(asp);
						
					}
					try 
					{
						
						eService.sendToAspList(aspLista, emailForm);
					} 
					catch (Exception e) 
					{
						
						e.printStackTrace();
					}
				}	
				else
				{
					kolloLista = new ArrayList<Kolonilott>();
					for (String vald: valda.getValda())
					{
						Kolonilott lott = lottRepo.getById(Integer.parseInt(vald));
						kolloLista.add(lott);
					}
					
					try 
					{
						eService.sendToLottList(kolloLista, emailForm);
					} 
					catch (Exception e) 
					{
						e.printStackTrace();				
					}
				}
				emailForm.setAttachments(null);
				emailForm.setBilageLista(new ArrayList<>());
				emailForm.setBody(null);
				emailForm.setSubject(null);
				model.addAttribute("emailForm", emailForm);
				return mainMailPage(emailForm, model); 				
			}
		}
		if (_action.startsWith("mall"))
		{
			Brevmall bm = mallRepo.findByNamn(_action.substring(4));
        	emailForm.setSubject(bm.getRubrik());
        	emailForm.setBody(bm.getKropp());
        	emailForm.setFooter(bm.getFot());
        	/*
        	** Hämta eventuella bilagor ifrån mallen, lägg till ett _ i filnamnet för att särskilja att de inte behöver ladda från klienten
            */
        	for (String bilaga : bm.getBilagor())
        	{
        		emailForm.getBilageLista().add("_" + bilaga);
        	}
		}
		else if (_action.equals("urval"))
			valda = new Urval();
		else if (_action.equals("bilaga"))
		{
			try 
			{
				emailForm.getBilageLista().add(storeFile(emailForm.getAttachments(), "Spool"));
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
					
		else if (_action.startsWith("remove"))
			emailForm.getBilageLista().remove(Integer.parseInt(_action.substring(6)));
		switch (_urval) 
		{
	        case "queue":		        	
	        	model.addAttribute("aspirantlista", aspRepo.findAll(Sort.by("koPlats")));
	            break;
	        case "unpaid":	        	
	        	model.addAttribute("aspirantlista", aspRepo.findByNotPaid());
	            break;
	        case "members":
	        	aspOrKollo = false;
	        	model.addAttribute("kolonistlista", lottRepo.findAll());
	            break;
	        case "area1":
	        case "area2":	            
	        case "area3":
	        case "area4":
	        case "area5":
	        case "area6":	
	        case "area7":	
	        case "area8":	
	        	aspOrKollo = false;
	        	model.addAttribute("kolonistlista", lottRepo.getByOmrade(Integer.parseInt(_urval.substring(4))));
	        	break;
	        case "aktiva":
	        	model.addAttribute("aspirantlista", aspRepo.findByActive());
	        	break;
	        case "passiva":
	        	model.addAttribute("aspirantlista", aspRepo.findByPassive());
	        	break;	
	        case "vilande":
	        	model.addAttribute("aspirantlista", aspRepo.findByVilande());
	        	break;	
	        case "ejaktiva":
	        	model.addAttribute("aspirantlista", aspRepo.findByEjAktiva());
	        	break;		
	        default:
	        	if (_urval.startsWith("salu"))
	        	{
	        		model.addAttribute("aspirantlista", aspRepo.findByInvitedToSail(Integer.parseInt(_urval.substring(4))));
	        	}
	            break;
		}
		model.addAttribute("aspReq", aspOrKollo ? true: false);
		model.addAttribute("kolloReq", aspOrKollo ? false: true);
		model.addAttribute("visningar",saluRepo.findPagaende());
		model.addAttribute("mallar", mallRepo.findAll());
		model.addAttribute("urval", valda);
		if (valda.getValda() != null)
		for (String vald: valda.getValda())
		{
			System.out.println(vald);
			
		};
		System.out.println("_urval:" + _urval + " _action: " + _action + " emailForm.rubrik" + emailForm.getSubject());
		System.out.println("attachments:" + emailForm.getAttachments().getOriginalFilename());
		model.addAttribute("emailForm", emailForm);
		model.addAttribute("_urval", _urval);
		return "/mainmail.html"; 	
	}
	
	@GetMapping("/mail/mallar")
	public String mainMallPage(@ModelAttribute("brevmall") Brevmall brevmall, Model model) 
	{				
		String[] bilageLista = fu.scanBilagorFolder(paramRepo.findByParamName("Bilagor").get().getParamValue());
		model.addAttribute("bilageLista", bilageLista);
		model.addAttribute("valda", new boolean[bilageLista.length]);
		model.addAttribute("mallar", mallRepo.findAll());
		model.addAttribute("brevmall", new Brevmall());
		model.addAttribute("mallkatalog", paramRepo.findByParamName("Bilagor").get().getParamValue());
		
		
	  	return "/mallar.html";      
	} 
	
	@GetMapping("/mail/edit/mall/{id}")
	public String editMall(@PathVariable("id") int id, Model model) 
	{		
		Brevmall brevmall = mallRepo.findById(id).get();
	
		String[] bilageLista = fu.scanBilagorFolder(paramRepo.findByParamName("Bilagor").get().getParamValue());
		model.addAttribute("bilageLista", bilageLista);
		boolean[] valda = getValdaBilagor(bilageLista, brevmall.getBilagor());
		model.addAttribute("valda", valda);
		model.addAttribute("mallar", mallRepo.findAll());
		model.addAttribute("brevmall", brevmall);
		model.addAttribute("mallkatalog", paramRepo.findByParamName("Bilagor").get().getParamValue());		
	  	return "/mallar.html";      
	} 
	
	@GetMapping("/mail/radera/mall/{id}")
	public String removeMall(@PathVariable("id") int id, @ModelAttribute("brevmall") Brevmall brevmall,  Model model) 
	{		
		mallRepo.deleteById(id);
		model.addAttribute("mallar", mallRepo.findAll());
		model.addAttribute("mallkatalog", paramRepo.findByParamName("Bilagor").get().getParamValue());
		model.addAttribute("brevmall", new Brevmall());
		model.addAttribute("mallkatalog", paramRepo.findByParamName("Bilagor").get().getParamValue());
	  	return "/mallar.html";      
	} 
	
	@PostMapping("/mail/save/mall")
	public String saveMall(@RequestParam int id, @RequestParam(name = "bilageval", required = false) String[] valdaBilagor, @ModelAttribute("brevmall") Brevmall brevmall, Model model) 
	{		
		if (valdaBilagor != null)
			brevmall.setBilagor(valdaBilagor);
		mallRepo.save(brevmall);		
	  	return "redirect:/mail/edit/mall/" + brevmall.getId();      
	} 
	
	@GetMapping("/bilagor")
	public String bilagor(Model model) 
	{		
		String[] bilageLista = fu.scanBilagorFolder(paramRepo.findByParamName("Bilagor").get().getParamValue());
		model.addAttribute("bilagor", bilageLista);
	  	return "/bilagor.html";      
	} 
	
	@PostMapping("/mail/delete/bilaga")
	public String raderaBilagor(@RequestParam(name = "bilageval", required = false) String[] valdaBilagor, Model model)
	{
		//String bilagefolder = paramRepo.findByParamName("Bilagor").get().getParamValue();
		Resource resource = new ClassPathResource(paramRepo.findByParamName("Bilagor").get().getParamValue());
		
		
		
		if (valdaBilagor != null)
			for (String bilaga : valdaBilagor)
			{
				try 
				{	             
					String bilagefolder  = resource.getURI().getPath();
	                Path filePath = Paths.get(bilagefolder, bilaga);
	                Files.delete(filePath);
	            } 
				catch (IOException e) 
				{
					model.addAttribute("message", "Ett fel inträffade vid radering av " + bilaga + ": " + e.getMessage());
	            }
			}
		String[] bilageLista = fu.scanBilagorFolder(paramRepo.findByParamName("Bilagor").get().getParamValue());
		model.addAttribute("bilagor", bilageLista);
		return "/bilagor.html"; 
	}
	
	@PostMapping("/mail/upload/bilaga")
	public String upload(@RequestParam("nybilaga") MultipartFile file, Model model)
	{
		String fileName = file.getOriginalFilename();
		try 
		{
			//file.transferTo(new File(paramRepo.findByParamName("Bilagor").get().getParamValue() + "/" + fileName));
			
			//model.addAttribute("bilagor", bilageLista);      
			model.addAttribute("message", "Bilaga " + storeFile(file, "bilagor") + " är uppladdad");
			
		} 
		catch (Exception e) 
		{			
			model.addAttribute("message", "Ett fel inträffade vid uppladdning av " + fileName + ": " + e.getMessage());			
		} 		
		//String[] bilageLista = fu.scanBilagorFolder(servletContext.getRealPath("/WEB-INF/classes/" + paramRepo.findByParamName("Bilagor").get().getParamValue()));
		
		String[] bilageLista = fu.scanBilagorFolder(paramRepo.findByParamName("Bilagor").get().getParamValue());
		model.addAttribute("bilagor", bilageLista); 
		return "/bilagor.html";
	}
	
	private boolean[] getValdaBilagor(String[] bilagor, String[] valdaBilagor)
	{
		boolean[] valda = new boolean[bilagor.length];
		
		for (String bilaga : valdaBilagor)
		{
			
			for (int i = 0; i < bilagor.length; i++)
			{
				
				if (bilaga.equals(bilagor[i]))
					valda[i] = true;
			}
		}		
		return valda;
	}
	private String storeFile(MultipartFile file, String folder) throws IOException 
	{				 
		 Resource resource = new ClassPathResource(paramRepo.findByParamName(folder).get().getParamValue());
		 //String basePath = servletContext.getRealPath("/WEB-INF/classes/" + paramRepo.findByParamName(folder).get().getParamValue());
         File uploadDir = resource.getFile();
//        
//         if (!uploadDir.exists()) {
//             uploadDir.mkdirs();
//         }
         
         File uploadFile = new File(uploadDir, file.getOriginalFilename());
         file.transferTo(uploadFile);
	     return file.getOriginalFilename();
    }
	
}
