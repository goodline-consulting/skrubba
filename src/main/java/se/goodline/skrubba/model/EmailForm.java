package se.goodline.skrubba.model;

import java.util.ArrayList;
import java.util.List;
import se.goodline.skrubba.model.Brevmall;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class EmailForm 
{
	private String subject;
	private String body;
	private String footer;
	private MultipartFile attachments;
	private List<String> bilageLista;
	
	public EmailForm() 
	{
	        // Default constructor needed for Spring to instantiate the bean
	}
	public EmailForm(Brevmall mall)
	{
		this.subject = mall.getRubrik();
		this.body    = mall.getKropp();
		this.bilageLista = new ArrayList<String>();
		for (String bilaga : mall.getBilagor())
    	{
    		this.getBilageLista().add("_" + bilaga);
    	}		
	}
	public MultipartFile getAttachments() {
		return attachments;
	}
	public void setAttachments(MultipartFile attachments) {
		this.attachments = attachments;
		//attachments.getOriginalFilename()
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getFooter() {
		return footer;
	}
	public void setFooter(String footer) {
		this.footer = footer;
	}
	
	public List<String> getBilageLista() {
		return bilageLista;
	}
	public void setBilageLista(List<String> bilageLista) {
		this.bilageLista = bilageLista;
	}
	
	public EmailForm(String subject, String body, String footer, MultipartFile attachments) {
		super();
		this.subject = subject;
		this.body = body;
		this.footer = footer;
		this.attachments = attachments;
	}
	@Override
	public String toString() {
		return "EmailForm [subject=" + subject + ", body=" + body + ", footer=" + footer + "]";
	}
	
}
