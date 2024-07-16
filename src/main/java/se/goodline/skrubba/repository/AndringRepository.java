package se.goodline.skrubba.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Andring;
import se.goodline.skrubba.model.Logg;

@Repository
public interface AndringRepository extends JpaRepository<Andring, Integer> 
{
	 

}



