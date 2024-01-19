package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.Tillsalu;

import java.util.List;
import java.util.Optional;

@Repository
public interface TillSaluRepository extends JpaRepository<Tillsalu, Integer> {
    Optional<Tillsalu> findById(int id);
           
    @Query(value = "SELECT * from tillsalu t where t.lottnr = ?1", nativeQuery = true)
    public List<Tillsalu> findByLottNr(int lottnr);
    
    @Query(value = "SELECT * from tillsalu t where t.lottnr = ?1 and t.saljdatum is null", nativeQuery = true)
    public Optional<Tillsalu> findPagaende(int lottnr);
    
    @Query(value = "SELECT * from tillsalu t where t.saljdatum is null", nativeQuery = true)
    public List<Tillsalu> findPagaende();
   
    
}



