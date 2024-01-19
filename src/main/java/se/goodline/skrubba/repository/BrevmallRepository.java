package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Brevmall;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrevmallRepository extends JpaRepository<Brevmall, Integer> 
{      
    @Query(value = "SELECT * from brevmall m where m.namn = ?1", nativeQuery = true)
    public Brevmall findByNamn(String namn);    
 }



