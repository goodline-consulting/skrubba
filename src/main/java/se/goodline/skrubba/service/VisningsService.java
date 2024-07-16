package se.goodline.skrubba.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Logg;
import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.model.Visning;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.repository.LoggRepository;
import se.goodline.skrubba.repository.ParamRepository;
import se.goodline.skrubba.repository.VisningRepository;


@Service
public class VisningsService 
{
	@Autowired
	private LoggRepository loggRepo;
	
	@Autowired
	private VisningRepository visRepo;
    
	@Autowired
	AspirantRepository aspRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	 
	@Transactional
	private Map<Integer, Visning> getCurrent(int id)
	{
		List<Visning> visLista = visRepo.findBySaluId(id);		
		Map<Integer, Visning> visMap = visLista.stream()
			        .collect(Collectors.toMap(Visning::getAsp, visning -> visning));
		entityManager.clear();
        return visMap;		
	}
	
	@Transactional
    public void taBortVisningar(int id) 
	{
        visRepo.deleteBySaluId(id);
    }

    @Transactional
    private void sparaNyaVisningar(int id, List<String> valda, Map<Integer, Visning> visMap) 
    {       
        for (String vald : valda) 
        {
            int valdInt = Integer.parseInt(vald);

            // Kontrollera om visMap innehåller valdInt
            Visning gammalVisning = visMap.get(valdInt);
                      
            // Skapa nytt Visning-objekt och återanvänd 'svar' om det finns
            Visning nyVisning;
            if (gammalVisning != null) 
                nyVisning = new Visning(id, valdInt, gammalVisning.getKo_plats(), gammalVisning.getSvar());
            else 
            {
            	// Hämta aspiranten så att vi får dit kötiden.
            	Aspirant asp = aspRepo.findById(valdInt);
                nyVisning = new Visning(id, valdInt, asp.getKoPlats(), null);
            }
            visRepo.save(nyVisning);            
        }
    }

    public void uppdateraVisningar(int id, List<String> valda) 
    {
        // Hämta nuvarande visningar
        Map<Integer, Visning> visMap = getCurrent(id);

        // Ta bort befintliga poster
        taBortVisningar(id);

        // Spara nya poster
        sparaNyaVisningar(id, valda, visMap);
    }

}
