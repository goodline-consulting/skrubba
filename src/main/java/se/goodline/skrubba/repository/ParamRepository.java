package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Param;
import se.goodline.skrubba.model.Visning;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Repository
public interface ParamRepository extends JpaRepository<Param, Integer> 
{
    
	@Query(value = "Select * from param p where p.param_name = ?1", nativeQuery = true)
	Optional<Param> findByParamName(String paramName);
           
        
    @Transactional
    @Modifying
    @Query(value = "DELETE from param p where p.param_name = ?1", nativeQuery = true)
    public void deleteByParamName(String paramName); 
}



