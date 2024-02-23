package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Logg;

@Repository
public interface LoggRepository extends JpaRepository<Logg, Integer> 
{
    // You can add custom query methods here if needed
}