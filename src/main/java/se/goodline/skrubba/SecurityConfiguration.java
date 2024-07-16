package se.goodline.skrubba;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/forgot_password").permitAll()
                .antMatchers("/reset_password").permitAll() 
                .antMatchers("/change_password").permitAll()
                .antMatchers("/anmalan").permitAll()
                .antMatchers("/message").permitAll()
                .antMatchers("/admin").hasRole("ADMIN")
                .antMatchers("/**").hasAnyRole("ADMIN", "USER")
                
                //.antMatchers("/").permitAll()
                .and().formLogin().loginPage("/login").permitAll()
                //.loginPage("/login").permitAll()
                //.defaultSuccessUrl("/");
                //.and()
                //.and().logout();
                //.and().csrf().disable();
                .and().logout().permitAll();
    }
        
    @Bean    
    public PasswordEncoder getPasswordEncoder() 
    {
    	return new BCryptPasswordEncoder();
        //return NoOpPasswordEncoder.getInstance();
    }
}