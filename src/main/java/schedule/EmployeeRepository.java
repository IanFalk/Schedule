package schedule;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Integer>{

  //Return a list of all employees with lastname "lastName"
  List<Employee> findByLastName(String lastName);
  //Return a list of all employees with firstname "firstName"
  List<Employee> findByFirstName(String firstName);
  //Return the employee with specified ID
  Employee findById(int id);

}