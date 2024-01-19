package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import se.goodline.skrubba.model.Visning;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Repository
public interface VisningRepository extends JpaRepository<Visning, Integer> 
{
    Optional<Visning> findById(int id);
           
    @Query(value = "SELECT * from visning v where v.asp = ?1", nativeQuery = true)
    public List<Visning> findByAspId(int aspId);    
    
    @Query(value = "SELECT count(asp) from visning v where v.asp = ?1", nativeQuery = true)
    public int findNrByAspId(int aspId);  
    
    @Query(value = "SELECT * from visning v where v.id = ?1", nativeQuery = true)
    public List<Visning> findBySaluId(int saluId);
    
    @Transactional
    @Modifying
    @Query(value = "DELETE from visning v where v.id = ?1", nativeQuery = true)
    public void deleteBySaluId(int saluId); 
    
    @Transactional
    @Modifying
    @Query(value = "DELETE from visning v where v.asp = ?1", nativeQuery = true)
    public void deleteByAspId(int aspId); 
}



