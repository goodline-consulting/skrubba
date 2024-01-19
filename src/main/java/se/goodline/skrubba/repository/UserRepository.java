package se.goodline.skrubba.repository;

 
import org.springframework.data.jpa.repository.JpaRepository;

import se.goodline.skrubba.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUserName(String userName);
    Optional<User> findByResetPasswordToken(String token);  
}