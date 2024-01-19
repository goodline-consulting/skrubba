package se.goodline.skrubba.service;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Betalning;
import se.goodline.skrubba.model.EmailForm;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.repository.BetalningRepository;
import se.goodline.skrubba.repository.ParamRepository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailParser 
{
	@Autowired
	private BetalningRepository betRepo;
	
	@Autowired
	private ParamRepository paramRepo;
	
	
	private String replaceSysVars(String str)
	{
		String resStr = new String(str);
		Optional<Param> param;
		if (str.contains("#BG"))
		{	
			param = paramRepo.findByParamName("Bankgiro");
			if (param.isPresent())
				resStr = resStr.replace("#BG", param.get().getParamValue());
		}
		if (str.contains("#KOAVGIFT"))
		{	
			param = paramRepo.findByParamName("Köavgift");
			if (param.isPresent())
				resStr = resStr.replace("#KOAVGIFT", param.get().getParamValue());
		}
		if (str.contains("#BETALDATUM"))
		{	
			param = paramRepo.findByParamName("Betaldatum");
			if (param.isPresent())
				resStr = resStr.replace("#BETALDATUM", param.get().getParamValue());
		}
		return resStr;
	}
	
	private  String replaceTokens(String str, Aspirant asp)
	{
		/*
		#AVINR
		#KOPLATS
		#FORNAMN
		#EFTERNAMN
		#BG
		#KOAVGIFT
		#BETALDATUM
		*/
		if (!str.contains("#"))
			return str;
		Optional<Betalning> bet = betRepo.findUnPaidThisYear(asp.getId(), LocalDate.now().getYear());
		String resStr = new String(str);
		resStr = resStr.replace("#AVINR", "" + (bet.isPresent() ? bet.get().getId() : "#AVINR"));
		resStr = resStr.replace("#KOPLATS","" + asp.getKoPlats());
		resStr = resStr.replace("#FORNAMN", asp.getFnamn());
		resStr = resStr.replace("#EFTERNAMN", asp.getEnamn());
		resStr = resStr.replace("#HELTNAMN", asp.getFnamn() +  " " + asp.getEnamn());
		resStr = resStr.replace("#EMAIL", asp.getEmail() != null ? asp.getEmail() : "#EMAIL");
		resStr = resStr.replace("#TELEFON", asp.getTelefon() != null ? asp.getTelefon() : "#TELEFON");
		resStr = replaceSysVars(resStr);
		return resStr;
	}
	private  String replaceTokens(String str, Kolonilott lott)
	{
		/*
		#HELTNAMN		
		#FORNAMN
		#EFTERNAMN
		#LOTTNR
		#OMRADE
		*/
		if (!str.contains("#"))
			return str;
		String resStr = new String(str);
		
		resStr = resStr.replace("#FORNAMN", lott.getAgare().split(" ")[0]);
		resStr = resStr.replace("#EFTERNAMN", lott.getAgare().split(" ")[1]);
		resStr = resStr.replace("#HELTNAMN", lott.getAgare());
		resStr = resStr.replace("#EMAIL", lott.getEmail() != null ? lott.getEmail() : "#EMAIL");
		resStr = resStr.replace("#TELEFON", lott.getTelefon() != null ? lott.getTelefon() : "#TELEFON");
		resStr = resStr.replace("#LOTTNR", "" + lott.getLottnr());
		resStr = resStr.replace("#OMRADE", "" + lott.getOmrade());
		//resStr = replaceSysVars(resStr);
		return resStr;
	}
	EmailForm parseAspEmailForm(EmailForm em, Aspirant asp)
	{
		String newSubject = null;
		String newBody    = null;
		String newFooter  = null;
		
		if (em.getSubject() != null)
			newSubject = replaceTokens(em.getSubject(), asp);
		if (em.getBody() != null)
			newBody    = replaceTokens(em.getBody(), asp);
		if (em.getFooter() != null)
			newFooter  = replaceTokens(em.getFooter(), asp);
		
		return new EmailForm(newSubject, newBody, newFooter, em.getAttachments());
	}
	EmailForm parseLottEmailForm(EmailForm em, Kolonilott lott)
	{
		String newSubject = null;
		String newBody    = null;
		String newFooter  = null;
		
		if (em.getSubject() != null)
			newSubject = replaceTokens(em.getSubject(), lott);
		if (em.getBody() != null)
			newBody    = replaceTokens(em.getBody(), lott);
		if (em.getFooter() != null)
			newFooter  = replaceTokens(em.getFooter(), lott);
		
		return new EmailForm(newSubject, newBody, newFooter, em.getAttachments());
	}
}
