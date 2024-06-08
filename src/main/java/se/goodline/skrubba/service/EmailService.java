package se.goodline.skrubba.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.EmailForm;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.ParamRepository;

@Service
public class EmailService 
{
	@Autowired
	JavaMailSender mailSender;
	
	@Autowired
	EmailParser emp;
	
	@Autowired
	ParamRepository paramRepo;
	
	@Autowired
	KoloniLottRepository lottRepo;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	LoggService loggService;
	
	public void sendPasswordLink(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException
    {
		String mailMottagare = paramRepo.findByParamName("Mailmottagare").isPresent() ? paramRepo.findByParamName("Mailmottagare").get().getParamValue() : null;
    	MimeMessage message = mailSender.createMimeMessage();              
        MimeMessageHelper helper = new MimeMessageHelper(message);
         
        helper.setTo(mailMottagare == null ? recipientEmail : mailMottagare);       
        helper.setFrom("styrelsen@skrubba.se", "Skrubba koloniträdgårdsförening");
       
         
        String subject = "Länk för att återställa lösenord";
         
        String content = "<p>Hej,</p>"
                + "<p>Du har begärt en länk för att återställa ditt lösenord.</p>"
                + "<p>Klicka på länken nedan för att välja ett nytt:</p>"
                + "<p><a href=\"" + link + "\">Byt lösenord</a></p>"
                + "<br>"
                + "<p>Ignorera detta email ifall du kommer ihåg ditt lösenord, "
                + "eller om du inte ligger bakom denna begäran.</p>";
         
        helper.setSubject(subject);         
        helper.setText(content, true);        
        mailSender.send(message);
    }  
	public void sendWelcomeLink(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException
    {
		String mailMottagare = paramRepo.findByParamName("Mailmottagare").isPresent() ? paramRepo.findByParamName("Mailmottagare").get().getParamValue() : null;
    	MimeMessage message = mailSender.createMimeMessage();              
        MimeMessageHelper helper = new MimeMessageHelper(message);
       
        helper.setFrom("styrelsen@skrubba.se", "Skrubba koloniträdgårdsförening");
        helper.setTo(mailMottagare == null ? recipientEmail : mailMottagare);        
         
        String subject = "Välkommen till Stugkön";
         
        String content = "<p>Hej,</p>"
                + "<p>Du står nu i Skrubba koloniträdgårdsförenings stugkö.</p>"
                + "<p>Klicka på länken nedan för att välja ett lösenord för inloggning på vår hemsida för stugkön</p>"
                + "<p><a href=\"" + link + "\">Välj lösenord</a></p>"
                + "<br>"
                + "<p>Ignorera detta email ifall du redan har ett lösenord, "
                + "eller om du inte ligger bakom denna begäran.</p>";
         
        helper.setSubject(subject);         
        helper.setText(content, true);    
        mailSender.send(message);
       
    }  
	public void sendEmail(String recipientEmail, EmailForm em) throws MessagingException, IOException
	{
		String folder = null;
		if (recipientEmail.contains("noemail.se") || recipientEmail.startsWith("bytutmej"))
		{	
			System.out.println("Saknar riktig emailadress:" + recipientEmail);
			return;			
		}	
		String mailMottagare = paramRepo.findByParamName("Mailmottagare").isPresent() ? paramRepo.findByParamName("Mailmottagare").get().getParamValue() : recipientEmail;
		MimeMessage message = mailSender.createMimeMessage();              
        MimeMessageHelper helper = new MimeMessageHelper(message, true);                       
        
        helper.setFrom("styrelsen@skrubba.se", "Skrubba koloniträdgårdsförening");
        helper.setTo(mailMottagare);
        helper.setSubject(em.getSubject());         
        helper.setText(em.getBody(), false); 
        for (String attachment : em.getBilageLista()) 
        {        
        	if (attachment.startsWith("_"))
        		folder = paramRepo.findByParamName("Bilagor").get().getParamValue() + "/" + attachment.substring(1);
        	else
        		folder = paramRepo.findByParamName("Spool").get().getParamValue() + "/" + attachment;
        	
        	Resource resource = new ClassPathResource(folder);
        	        	
        	File attachmentFile = resource.getFile();
        	FileSystemResource fileSystemResource = new FileSystemResource(attachmentFile);            
            helper.addAttachment(attachmentFile.getName(), fileSystemResource);
        }
        //System.out.println(em.getBody());
        mailSender.send(message);
	}
	
	@Async
	@Transactional
	public void sendToAspList(List<String> valda, EmailForm em) throws  Exception
	{
		//String mailMottagare = paramRepo.findByParamName("Mailmottagare").isPresent() ? paramRepo.findByParamName("Mailmottagare").get().getParamValue() : null;
		int tot   = 0;
		int antal = 0;
		for (String vald: valda)
		{		
			Aspirant asp = aspRepo.findById(Integer.parseInt(vald));
			antal++;
			tot++;
			if (antal > 19)
			{				
				System.out.println("Skickat " + tot + " mail, Pausar fem minuter.");
				loggService.add("MAIL", "Skickat " + tot + " mail, Pausar fem minuter.");
				Thread.sleep(5 * 60 * 1000);
				antal = 0;			
			}
			EmailForm emCopy = em.clone();
			emCopy = emp.parseAspEmailForm(emCopy, asp);
			sendEmail(asp.getEmail(), emCopy);	
		}
		loggService.add("MAIL", "Email med rubrik " + em.getSubject() + " skickat till " + tot + " i kön");
	}	
	
	@Async
	@Transactional
	public void sendToLottList(List<String> valda, EmailForm em) throws  Exception
	{
		int antal = 0;
		int tot   = 0;
		for (String vald : valda)	
		{
			Kolonilott lott = lottRepo.getById(Integer.parseInt(vald));
			
			antal++;
			if (antal > 19)
			{				
				System.out.println("Skickat " + tot + " mail, Pausar fem minuter.");
				loggService.add("MAIL", "Skickat " + tot + " mail, Pausar fem minuter.");						
				Thread.sleep(5 * 60 * 1000);
				antal = 0;			
			}
			EmailForm emCopy = em.clone();
			emCopy = emp.parseLottEmailForm(emCopy, lott);
			sendEmail(lott.getEmail(), emCopy);	
		}
		loggService.add("MAIL", "Email med rubrik " + em.getSubject() + " skickat till " + tot + " kolonister");
	}
	
	public void sendBetalningsBekraftelse(EmailForm em, Aspirant asp) throws  Exception
	{
		EmailForm emNy = emp.parseAspEmailForm(em, asp);
		emNy.setBilageLista(em.getBilageLista());
		sendEmail(asp.getEmail(), emNy);
		loggService.add("BEKR", "Skickar betalningsbekrätelse till " + asp.getEmail());
	}		
}
