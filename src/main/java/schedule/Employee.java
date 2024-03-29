package schedule;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*  Example of database structure
 *  ________________________________
 * | 1 | Jack  | Bauer  | Manager  |
 * | 2 | David | Palmer | Employee |
 * |____________________|__________|
 */
@Entity
@Table(name="Employees")
public class Employee {
    
    protected static final List<String> ROLES_LIST = Arrays.asList("Employee", "Manager", "Owner");
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    private String firstName;
    private String lastName;
    private String role;
    private String password;

    protected Employee() {}

    public Employee(String firstName, String lastName, String role, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName+" "+lastName;
    }

    public String getRole() {
        return role;
    }

    public void setFirstName(String fname) {
        this.firstName = fname;
    }

    public void setLastName(String lname) {
        this.lastName = lname;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
            "Employee[id=%d, firstName='%s', lastName='%s', role='%s']",
            id, firstName, lastName, role);
    }
}
