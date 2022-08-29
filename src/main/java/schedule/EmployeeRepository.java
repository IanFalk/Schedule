package schedule;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{

  //Return a list of all employees with lastname "lastName"
  List<Employee> findByLastName(String lastName);
  //Return a list of all employees with firstname "firstName"
  List<Employee> findByFirstName(String firstName);
  //Return the employee with specified ID
  Employee findById(int id);

}