package schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
public class ScheduleApplication {

	private static final Logger log = LoggerFactory.getLogger(ScheduleApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ScheduleApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(EmployeeRepository eRepo, scheduleDataRepository sdRepo) {
		return args -> {

		};
	}
    

}
