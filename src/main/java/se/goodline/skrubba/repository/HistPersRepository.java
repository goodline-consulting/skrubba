package se.goodline.skrubba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se.goodline.skrubba.model.Histpers;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistPersRepository extends JpaRepository<Histpers, Integer> {
    Histpers findById(int id);
    
}
