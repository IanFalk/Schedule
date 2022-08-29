package schedule;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/*  Example of database structure
 *  _____________________
 * | 1 | Jack  | Bauer  |
 * | 2 | David | Palmer |
 * |____________________|
 */
@Entity
@Table(name="Employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String firstName;
    private String lastName;

    protected Employee() {}

    public Employee(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    @Override
    public String toString() {
        return String.format(
            "Employee[id=%d, firstName='%s', lastName='%s']",
            id, firstName, lastName);
    }
}
