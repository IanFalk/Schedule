package schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class GreetingController {

    @Autowired
    private EmployeeRepository eRepo;
    @Autowired
    private scheduleDataRepository sdRepo;

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    
	@GetMapping({"/schedule/weekly","/"}/* */)
	public String weeklySchedule(Model model, Principal principal) {

        //Gets todays date, the date with which to start displaying the schedule
        LocalDate localDate = LocalDate.now();

        //Stores all the dates to be displayed on schedule
        List<String> dates = new ArrayList<>();
        
        //Creates a list of all employees in the database
        List<Employee> emps = eRepo.findAll();

        //2d array, the table to be displayed on webpage. Each row is an employee, 
        //each column a day of the week
        String[][] schedule = new String[emps.size()][7];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");
        //Period between start date, and date 7 days from now inclusive of both
        for(int period=0; period<Period.between(localDate, localDate.plus(Period.ofDays(7))).getDays(); period++) {
            LocalDate currentDate = localDate.plus(Period.ofDays(period));

            //Gets all the shifts for the day
            List<scheduleDatabase> allShifts = sdRepo.findByDate(currentDate);

            //Goes through each employee and each shift, and fills in the table with shift info
            for(int i=0; i<emps.size(); i++) {
                for(scheduleDatabase shift : allShifts) {
                    if(shift.getEmpId() == emps.get(i).getId()) {
                        schedule[i][period] = shift.getShift();
                    }
                }
                //If the employee wasn't scheduled, then they are off for the day
                if(schedule[i][period] == null) {
                    schedule[i][period] = "Off";
                }
            }

            //Convert Date to string formatted like: "Sunday 12/18"
            dates.add(currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + formatter.format(currentDate));

        }


        //Makes this data available to html template
        model.addAttribute("dates", dates);
        model.addAttribute("emps", emps);
        model.addAttribute("schedule", schedule);
        model.addAttribute("user", principal.getName());
        
        return "weeklyschedule";
    }

    //Get the weekly schedule for only this employee
    @GetMapping("/schedule/employee")
    public String employeeSchedule(Model model, Principal principal) {
        //Get the name of the user accessing this page
        String name = principal.getName();
        //Find users with the last name present in the username
        List<Employee> employees = eRepo.findByLastName(name.substring(1));
        Employee targetEmployee = null;
        List<scheduleDatabase> shifts = new ArrayList<>();

        //Find the correct user (in case of duplicate last names)
        for(Employee emp : employees) {
            if(emp.getFirstName().charAt(0) == name.charAt(0)) {
                targetEmployee = emp;
            }
        }

        //Find the shifts for the target employee
        if(targetEmployee != null) {
            List<scheduleDatabase> allShifts = sdRepo.findByEmpId(targetEmployee.getId());
            LocalDate currentDate = LocalDate.now();
            for(scheduleDatabase data : allShifts) {
                LocalDate date = data.getDate();
                if(date.isAfter(currentDate.plusDays(-1)) && date.isBefore(currentDate.plusDays(7))) {
                    shifts.add(data);
                }
            }
        }

        model.addAttribute("shifts", shifts);
        model.addAttribute("employee", targetEmployee);

        return "employeeschedule";
        
    }

    @GetMapping("/index")
    public String showIndex(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", auth.getPrincipal());
        return "index";
    }

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;
    @GetMapping("manager/employee/add/data")
    public String getAddEmployee(Model model, String fname, String lname, String pass, String role) {
        Employee emp = new Employee(fname, lname, role);
        eRepo.save(emp);
        ArrayList<GrantedAuthority> grantedAuthoritiesList= new ArrayList<>();
		grantedAuthoritiesList.add(new SimpleGrantedAuthority("ROLE_"+role));
        inMemoryUserDetailsManager.createUser(new User(fname.charAt(0)+lname, pass, grantedAuthoritiesList));
        log.info(fname+lname+" user has been created");
        return "createEmployee";
    }

    @GetMapping("manager/employee/add")
    public String showAddEmployee() {
        return "createEmployee";
    }

    @GetMapping("manager/employee/delete/data")
    public String getDeleteEmployee(Model model, int deleteEmp) {
        log.info(eRepo.findById(deleteEmp).getFullName()+" has been deleted");
        eRepo.deleteById(deleteEmp);
        return "deleteEmployee";
    }

    @GetMapping("manager/employee/delete")
    public String showDeleteEmployee(Model model) {
        List<Employee> employees = eRepo.findAll();
        model.addAttribute("employees", employees);
        return "deleteEmployee";
    }

    @GetMapping("manager/employee/edit/select")
    public String showSelectEmployee(Model model, int editEmp) {
        Employee emp = eRepo.findById(editEmp);
        model.addAttribute("employee", emp);
        return "editSelectEmployee";
    }

    @GetMapping("manager/employee/edit")
    public String showEditEmployee(Model model) {
        List<Employee> employees = eRepo.findAll();
        model.addAttribute("employees", employees);
        return "editEmployee";
    }

    @GetMapping("manager/employee/edit/data")
    public String getEditEmployee(Model model, String fname, String lname, int id, String role) {
        Employee emp = eRepo.findById(id);
        emp.setFirstName(fname);
        emp.setLastName(lname);
        emp.setRole(role);
        eRepo.save(emp);
        return "editEmployee";
    }

}