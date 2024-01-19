package se.goodline.skrubba.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import se.goodline.skrubba.model.SkrubbaUserDetails;
import se.goodline.skrubba.model.User;
import se.goodline.skrubba.repository.UserRepository;

import java.util.Optional;

@Service
public class SkrubbaUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
                
        Optional<User> user = userRepo.findByUserName(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + userName));
        return user.map(SkrubbaUserDetails::new).get();
    }
    public boolean userExists(String userName)
    {
    	Optional<User> user = userRepo.findByUserName(userName);
    	 return user.isPresent();
    }
    public User getUser(int id)
    {
    	return userRepo.getById(id);
    }
    
    public void saveUser(User user)
    {
    	userRepo.save(user);    	
    }
    
    public void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException
    {
    	Optional<User> user = userRepo.findByUserName(email);
        if (user.isPresent()) 
        {
            user.get().setResetPasswordToken(token);
            userRepo.save(user.get());
        } 
        else throw new UsernameNotFoundException("Kunde inte hitta någon registrerad användare med e-postadress " + email);
        	
    }
    
    
    public Optional<User> getByResetPasswordToken(String token) 
    {
        return userRepo.findByResetPasswordToken(token);
    }
     
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
         
        user.setResetPasswordToken(null);
        userRepo.save(user);
    }
    public void updatePassword(int id, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = userRepo.getById(id);
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepo.save(user);
    }
}