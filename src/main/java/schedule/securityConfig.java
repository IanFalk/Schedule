package schedule;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class securityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                .antMatchers("/schedule/**").hasAnyRole("MANAGER", "ADMIN", "EMPLOYEE")
                .anyRequest().authenticated() )
            .formLogin((form) -> form
                .loginPage("/login")
                .permitAll() )
            .logout((logout) -> logout.permitAll());

            return http.build();
    }

    @Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
    
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        List<UserDetails> users = new ArrayList<>();
        users.add(User.withUsername("admin").password(passwordEncoder().encode("password"))
			.roles("ADMIN").build());
        users.add(User.withUsername("guest").password(passwordEncoder().encode("password"))
			.roles("GUEST").build());        
		return new InMemoryUserDetailsManager(users);
	}
    
}
