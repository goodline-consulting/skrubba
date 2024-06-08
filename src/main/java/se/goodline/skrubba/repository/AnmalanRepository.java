package se.goodline.skrubba.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Anmalan;

@Repository
public interface AnmalanRepository extends JpaRepository<Anmalan, Integer> 
{

}



