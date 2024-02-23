

package se.goodline.skrubba;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import se.goodline.skrubba.model.Aspirant;
import se.goodline.skrubba.model.Kolonilott;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.repository.AspirantLoader;
import se.goodline.skrubba.repository.AspirantRepository;
import se.goodline.skrubba.service.KoloniLoader;
import se.goodline.skrubba.repository.KoloniLottRepository;
import se.goodline.skrubba.repository.UserRepository;

@SpringBootApplication
@EnableAsync
public class SkrubbaApplication extends SpringBootServletInitializer  
{

	@Autowired
    UserRepository userRepository;
	
	@Autowired
	AspirantRepository aspRepo;
	
	@Autowired
	KoloniLottRepository lottRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(SkrubbaApplication.class, args);
	}
	
	@PostConstruct
	public void init()
	{
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		if (userRepository.count() == 0)
        {
            userRepository.save(new User("admin", passwordEncoder.encode("alice00li"), true, "ROLE_ADMIN"));
            userRepository.save(new User("ingrid", passwordEncoder.encode("ingrid"), true, "ROLE_ADMIN"));
            userRepository.save(new User("jimmie", passwordEncoder.encode("jimmie"), true, "ROLE_ADMIN"));
            userRepository.save(new User("marit", passwordEncoder.encode("marit"), true, "ROLE_ADMIN"));
            userRepository.save(new User("johnny", passwordEncoder.encode("johnny"), true, "ROLE_ADMIN"));
            userRepository.save(new User("kerstin", passwordEncoder.encode("kerstin"), true, "ROLE_ADMIN"));
            userRepository.save(new User("renee", passwordEncoder.encode("renee"), true, "ROLE_ADMIN"));
            userRepository.save(new User("jeanette", passwordEncoder.encode("jeanette"), true, "ROLE_ADMIN"));
        }
		
		//loadAspirants();
		//loadLotter();
		
	}
	private void loadAspirants()
	{
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		System.out.println("*******laddar aspiranter********");
		AspirantLoader aspLoader = new AspirantLoader();
		aspLoader.loadAspirants("Dokument/intresselistan2024.csv");
		ArrayList<Aspirant> aspList = aspLoader.getAsprianter();
		for (Aspirant asp : aspList)
		{
			
			Optional<User> user = userRepository.findByUserName(asp.getEmail());
			if (user.isPresent())
				continue;
			userRepository.save(new User(asp.getEmail(), passwordEncoder.encode(asp.getEmail()), true, "ROLE_USER"));			
			user = userRepository.findByUserName(asp.getEmail());
			asp.setId(user.get().getId());
			aspRepo.save(asp);
			
			System.out.println("Skapar " + asp.getEmail() + " med köplats " + asp.getKoPlats() + "Telefon:" + asp.getTelefon()); // + " och med id " + asp.getId()); // + " userid:" + user.get().getId());			
		}
	}
	
	
}
