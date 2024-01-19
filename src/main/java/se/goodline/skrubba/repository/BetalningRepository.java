package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import se.goodline.skrubba.model.Betalning;
import java.util.List;
import java.util.Optional;

@Repository
public interface BetalningRepository extends JpaRepository<Betalning, Integer> 
{
    //Optional<Visning> findById(int id);
           
    @Query(value = "SELECT * from betalning v where v.asp = ?1", nativeQuery = true)
    public List<Betalning> findByAspId(int aspId);    
    
    @Query(value = "SELECT * from betalning v where v.asp = ?1 and betdatum is null and ar = ?2", nativeQuery = true)
    public Optional<Betalning> findUnPaidThisYear(int aspId, int year); 
    
    @Query(value = "SELECT * from betalning v where v.asp = ?1 and ar = ?2", nativeQuery = true)
    public Optional<Betalning> findThisYear(int aspId, int year); 
    
    @Transactional
    @Modifying
    @Query(value = "DELETE from betalning v where v.asp = ?1", nativeQuery = true)
    public void delBetalningar(int aspId); 
    
}



