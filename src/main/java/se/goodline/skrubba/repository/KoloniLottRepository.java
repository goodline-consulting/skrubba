package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Visning;

import java.util.List;
import java.util.Optional;

@Repository
public interface KoloniLottRepository extends JpaRepository<Kolonilott, Integer> 
{
    Optional<Kolonilott> findById(int id);
    
    @Query(value = "SELECT * from kolonilott k where k.lottnr = ?1", nativeQuery = true)
    public Kolonilott getByLottnr(int lottnr);    
 
    @Query(value = "SELECT * from kolonilott k where k.omrade = ?1 order by lottnr", nativeQuery = true)
    public List<Kolonilott> getByOmrade(int omrade);    
}



