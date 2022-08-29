package schedule;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ScheduleApplication {

	private static final Logger log = LoggerFactory.getLogger(ScheduleApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(ScheduleApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(EmployeeRepository eRepo, scheduleDataRepository sdRepo) {
		return (args) -> {
			// save Employees to the database
			eRepo.save(new Employee("Jack", "Bauer"));
			eRepo.save(new Employee("Chloe", "O'Brian"));
			eRepo.save(new Employee("Kim", "Bauer"));
			eRepo.save(new Employee("David", "Palmer"));
			eRepo.save(new Employee("Michelle", "Dessler"));

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

		};
	}

}
