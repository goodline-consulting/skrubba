package se.goodline.skrubba.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.EmailForm;
import se.goodline.skrubba.model.Kolonilott;
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
	private void sendEmail(String recipientEmail, EmailForm em) throws UnsupportedEncodingException, MessagingException
	{
		String mailMottagare = paramRepo.findByParamName("Mailmottagare").isPresent() ? paramRepo.findByParamName("Mailmottagare").get().getParamValue() : recipientEmail;
		MimeMessage message = mailSender.createMimeMessage();              
        MimeMessageHelper helper = new MimeMessageHelper(message, true);                       
        
        helper.setFrom("styrelsen@skrubba.se", "Skrubba koloniträdgårdsförening");
        helper.setTo(mailMottagare);
        helper.setSubject(em.getSubject());         
        helper.setText(em.getBody(), false); 
        for (String attachment : em.getBilageLista()) 
        {
        	File attachmentFile = new File(attachment.startsWith("_") ? paramRepo.findByParamName("Bilagor").get().getParamValue() + "/" + attachment.substring(1) : paramRepo.findByParamName("Spool").get().getParamValue() + "/" + attachment);
            FileSystemResource fileSystemResource = new FileSystemResource(attachmentFile);
            helper.addAttachment(attachmentFile.getName(), fileSystemResource);
        }
        System.out.println("Skickar mail till:" + mailMottagare);
        mailSender.send(message);
	}
	
	
	public void sendToAspList(List<Aspirant> aspList, EmailForm em) throws  Exception
	{
		//String mailMottagare = paramRepo.findByParamName("Mailmottagare").isPresent() ? paramRepo.findByParamName("Mailmottagare").get().getParamValue() : null;
		for (Aspirant asp : aspList)
		{
			EmailForm emNy = emp.parseAspEmailForm(em, asp);
			emNy.setBilageLista(em.getBilageLista());
			sendEmail(asp.getEmail(), emNy);			
		}
	}
	public void sendToLottList(List<Kolonilott> lottList, EmailForm em) throws  Exception
	{
		//String mailMottagare = paramRepo.findByParamName("Mailmottagare").isPresent() ? paramRepo.findByParamName("Mailmottagare").get().getParamValue() : null;
		for (Kolonilott lott : lottList)
		{
			EmailForm emNy = emp.parseLottEmailForm(em, lott);
			emNy.setBilageLista(em.getBilageLista());
			sendEmail(lott.getEmail(), emNy);
		}
	}
	
	public void sendBetalningsBekraftelse(EmailForm em, Aspirant asp) throws  Exception
	{
		EmailForm emNy = emp.parseAspEmailForm(em, asp);
		emNy.setBilageLista(em.getBilageLista());
		sendEmail(asp.getEmail(), emNy);
	}
	
	
	
	
	
	
		
}
