package se.goodline.skrubba.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.goodline.skrubba.model.Logg;

@Repository
public interface LoggRepository extends JpaRepository<Logg, Integer> 
{
	 @Query(value = "SELECT * from logg l where l.tidpunkt >= ?1 and l.tidpunkt <= ?2", nativeQuery = true)
	    public List<Logg> findByDates(Date from, Date to);
	 
	 @Query(value = "SELECT * from logg l where l.user like ?1", nativeQuery = true)
	    public List<Logg> findByUser(String user);    
     
	 @Query(value = "SELECT * from logg l where l.user like ?1 and l.tidpunkt >= ?2 and l.tidpunkt <= ?3", nativeQuery = true)
	 public List<Logg> findByAll(String user, Date dFrom, Date dTo);

}



