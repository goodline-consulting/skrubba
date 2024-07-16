package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Aspirant;

import java.util.List;
import java.util.Optional;

@Repository
public interface AspirantRepository extends JpaRepository<Aspirant, Integer> {
    Aspirant findById(int id);
    
    @Query(value = "SELECT max(ko_plats) FROM aspirant", nativeQuery=true)
    public int getMaxTransactionId();
   
    @Query(value = "SELECT COUNT(id) FROM aspirant p WHERE email = ?1", nativeQuery=true)
    public int countByEmail(String email);
   
    default boolean findExistByEmail(String email) 
    {
        return (countByEmail(email) == 1);
    }
    @Query(value = "SELECT * FROM aspirant p where betalat is null order by ko_plats", nativeQuery=true)
    public List<Aspirant> findByNotPaid();
    
    @Query(value = "SELECT * FROM aspirant p where ko_status = 'Aktiv' order by ko_plats", nativeQuery=true)
    public List<Aspirant> findByActive();
    
    @Query(value = "SELECT * FROM aspirant p where ko_status = 'Passiv' order by ko_plats", nativeQuery=true)
    public List<Aspirant> findByPassive();
    
    @Query(value = "SELECT * FROM aspirant p where ko_status = 'Vilande' order by ko_plats", nativeQuery=true)
    public List<Aspirant> findByVilande();
    
    @Query(value = "SELECT * FROM aspirant p where ko_status != 'Aktiv' order by ko_plats", nativeQuery=true)
    public List<Aspirant> findByEjAktiva();
    
    @Query(value = "SELECT * FROM aspirant JOIN visning ON aspirant.id = visning.asp JOIN tillsalu  ON visning.id = tillsalu.id and tillsalu.saljdatum is null WHERE tillsalu.lottnr = ?1 order by aspirant.ko_plats", nativeQuery=true)
    public List<Aspirant> findByInvitedToSail(int lottnr);
    
    @Query(value = "SELECT COUNT(a.id) FROM aspirant a WHERE a.ko_status = ?1", nativeQuery=true)
    int getAmountWithStatus(String atatus);
    
    @Query(value = "SELECT COUNT(a.id) FROM aspirant a WHERE a.betalat is not null", nativeQuery=true)
    int getAmountPaid();
    
    @Query(value = "SELECT COUNT(a.id) FROM aspirant a WHERE a.betalat is null", nativeQuery=true)
    int getAmountNotPaid();
    
}
