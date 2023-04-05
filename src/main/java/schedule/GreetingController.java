package schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    protected String[] OPERATING_HOURS = {"09:00", "24"};
    
    @Autowired
    private EmployeeRepository eRepo;
    @Autowired
    private scheduleDataRepository sdRepo;
    
	@GetMapping({"/schedule/weekly","/"})
	public String showWeeklySchedule(Model model, Principal principal) {

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
            dates.add(currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + formatter.format(currentDate));

        }


        //Makes this data available to html template
        model.addAttribute("dates", dates);
        model.addAttribute("emps", emps);
        model.addAttribute("schedule", schedule);
        model.addAttribute("user", principal.getName());
        
        return "weeklySchedule";
    }

    //Get the weekly schedule for only this employee
    @GetMapping("/schedule/employee")
    public String showEmployeeSchedule(Model model, Principal principal) {
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

        log.info(targetEmployee.getFirstName());
        //TODO: Add error handling if feteched employee is null
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

        return "employeeSchedule";
        
    }

    @GetMapping("/index")
    public String showIndex(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("user", auth.getPrincipal());
        return "index";
    }

    @GetMapping("manager")
    public String showManagerFeatures() {
        return "managerFeatures";
    }

    @GetMapping("manager/employee/add")
    public String showAddEmployee(Model model) {
        List<String> ROLES_LIST = Employee.ROLES_LIST;
        model.addAttribute("roles", ROLES_LIST);
        return "addEmployee";
    }

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;
    @PostMapping("manager/employee/add")
    public String getAddEmployee(Model model, @RequestParam String fname, @RequestParam String lname, @RequestParam String pass, @RequestParam String role) {
        
        //Create new employee in database
        Employee emp = new Employee(fname, lname, role, passwordEncoder.encode(pass));
        eRepo.save(emp);

        //Create new login for employee
        ArrayList<GrantedAuthority> grantedAuthoritiesList= new ArrayList<>();
		grantedAuthoritiesList.add(new SimpleGrantedAuthority("ROLE_"+role));
        UserDetails user = new User(fname.charAt(0)+lname, passwordEncoder.encode(pass), grantedAuthoritiesList);
        inMemoryUserDetailsManager.createUser(user);
        
        return showAddEmployee(model);
    }

    @PostMapping("manager/employee/delete")
    public String getDeleteEmployee(@RequestParam int deleteEmp, Model model) {
        Employee emp = eRepo.findById(deleteEmp);
        eRepo.deleteById(deleteEmp);
        inMemoryUserDetailsManager.deleteUser(emp.getFirstName().charAt(0)+emp.getLastName());
        return showDeleteEmployee(model);
    }

    @GetMapping("manager/employee/delete")
    public String showDeleteEmployee(Model model) {
        List<Employee> employees = eRepo.findAll();
        model.addAttribute("employees", employees);
        return "deleteEmployee";
    }

    @GetMapping("manager/employee/edit")
    public String showEditEmployee(Model model) {
        List<Employee> employees = eRepo.findAll();
        model.addAttribute("employees", employees);
        return "editEmployee";
    }

    @GetMapping("manager/employee/edit/select")
    public String showEditSelectEmployee(Model model, int editEmp) {
        Employee emp = eRepo.findById(editEmp);
        model.addAttribute("employee", emp);
        
        List<String> ROLES_LIST = Employee.ROLES_LIST;
        model.addAttribute("roles", ROLES_LIST);
        return "editSelectEmployee";
    }

    @PostMapping("manager/employee/edit")
    public String getEditEmployee(Model model, @RequestParam String fname, @RequestParam String lname, @RequestParam int id, @RequestParam String role) {
        Employee emp = eRepo.findById(id);
        emp.setFirstName(fname);
        emp.setLastName(lname);
        emp.setRole(role);
        eRepo.save(emp);

        //TODO: Add prompt that data was changed sucessfully
        return showEditEmployee(model);
    }

}