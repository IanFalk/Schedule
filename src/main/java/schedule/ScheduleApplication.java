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
	public List<String> listOfRoles;

	public static void main(String[] args) {
		SpringApplication.run(ScheduleApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(EmployeeRepository eRepo, scheduleDataRepository sdRepo) {
		return args -> {
			//Add Roles to the list
			listOfRoles.add("MANAGER");
			listOfRoles.add("EMPLOYEE");
			listOfRoles.add("ADMIN");
			// save Employees to the database
			eRepo.save(new Employee("Jack", "Bauer", "MANAGER"));
			eRepo.save(new Employee("Chloe", "O'Brian", "EMPLOYEE"));
			eRepo.save(new Employee("Kim", "Bauer", "EMPLOYEE"));
			eRepo.save(new Employee("David", "Palmer", "EMPLOYEE"));
			eRepo.save(new Employee("Michelle", "Dessler", "EMPLOYEE"));

			//Some test data, setting dates and times for employee shifts
			LocalDate date = LocalDate.now();
			LocalTime startTime = LocalTime.parse("08:00");
			LocalTime endTime = LocalTime.parse("14:00");

			sdRepo.save(new scheduleDatabase(date, 1, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(1), 1, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(2), 1, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(3), 1, startTime, endTime));

			startTime = LocalTime.parse("10:00");
			endTime = LocalTime.parse("14:00");

			sdRepo.save(new scheduleDatabase(date, 2, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(1), 2, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(2), 2, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(4), 2, startTime, endTime));

			startTime = LocalTime.parse("08:00");
			endTime = LocalTime.parse("23:00");

			sdRepo.save(new scheduleDatabase(date, 3, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(1), 3, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(4), 4, startTime, endTime));
			sdRepo.save(new scheduleDatabase(date.plusDays(3), 4, startTime, endTime));

			/*
			// Print all employees found in database to console. Debugging content
			log.info("Employees found with findAll():");
			log.info("-------------------------------");
			for (Employee employee : eRepo.findAll()) {
			log.info(employee.toString());
			}
			log.info("");
			*/

			createAllUsers(eRepo);

		};
	}

	@Autowired
	public InMemoryUserDetailsManager inMemoryUserDetailsManager;
    @Autowired
	public PasswordEncoder passwordEncoder;

    public String createAllUsers(EmployeeRepository eRepo) {
        List<Employee> employees = eRepo.findAll();
        for(Employee emp : employees) {
            if(emp.getRole().equals("EMPLOYEE")) {
                //Creates a Employee with username first initial last name. Ex: Jack Bauer = jbauer
                //User gets password "password" and the employee role
                log.info(createUser(emp.getFirstName().charAt(0) + emp.getLastName(), "password", "EMPLOYEE"));
            } else if(emp.getRole().equals("MANAGER")) {
                //Creates a Manager with username first initial last name. Ex: Jack Bauer = jbauer
                //User gets password "password" and the manager role
                log.info(createUser(emp.getFirstName().charAt(0) + emp.getLastName(), "password", "MANAGER"));
            }
        }
        return "index";               
    }

	public String createUser(String username, String password, String role)
	{
		ArrayList<GrantedAuthority> grantedAuthoritiesList= new ArrayList<>();
		grantedAuthoritiesList.add(new SimpleGrantedAuthority("ROLE_"+role));
        User test = new User(username, passwordEncoder.encode(password), grantedAuthoritiesList);
		inMemoryUserDetailsManager.createUser(test);
		return username + " user has been created";
	}

    

}
