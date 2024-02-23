package se.goodline.skrubba.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import se.goodline.skrubba.model.Logg;
import se.goodline.skrubba.repository.LoggRepository;

/*
** typ i loggen kan vara något av följande
** LOGIN, LOGOUT, ADMIN, UPDATE, MAIL, INVOICE
*/

@Service
public class LoggService 
{
	@Autowired
	private LoggRepository loggRepo;
	
	public void add(String typ, String note)
	{
		Logg logg = new Logg(new Date(), getUser(), typ, note);
		loggRepo.save(logg);
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
        return null;
	}
	
}
