package se.goodline.skrubba.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import se.goodline.skrubba.model.Logg;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.repository.LoggRepository;
import se.goodline.skrubba.repository.ParamRepository;

/*
** typ i loggen kan vara något av följande
** LOGIN,ADMIN,UPDATE,MAIL,INVOICE,BACKUP,DELETE,CREATE,BEKR
*/

@Service
public class LoggService 
{
	@Autowired
	private LoggRepository loggRepo;
	
	@Autowired
	ParamRepository paramRepo;
	
	public void add(String typ, String note)
	{
		Optional<Param> par = paramRepo.findByParamName("Loggning");
		Logg logg = new Logg(new Date(), getUser(), typ, note);
		if (par.isPresent() && par.get().getParamValue().contains(typ))
			loggRepo.save(logg);
	}
	
	public List<Logg> getAll()
	{
		return loggRepo.findAll(Sort.by("tidpunkt").descending());
	}
	
	public List<Logg> getByDates(Date dFrom, Date dTo)
	{
		System.out.println("getbydates: " + dFrom + " to: " + dTo);
		return loggRepo.findByDates(dFrom, dTo);
	}
	
	public List<Logg> getDate(Date date)
	{
		return loggRepo.findByDates(date, date);
	}
	
	public List<Logg> getByUser(String user)
	{
		return loggRepo.findByUser(user);
	}
	
	public List<Logg> getByAll(String user, Date dFrom, Date dTo)
	{
		return loggRepo.findByAll(user, dFrom, dTo);
	}
	private String getUser()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) 
        {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) 
            {
                return ((UserDetails) principal).getUsername();
            } 
            else 
            {
                return principal.toString();
            }
        }
        return "Deamon";
	}
	
}
