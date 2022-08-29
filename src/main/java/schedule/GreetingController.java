package schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Controller
public class GreetingController {

    @Autowired
    private EmployeeRepository eRepo;
    @Autowired
    private scheduleDataRepository sdRepo;

	@GetMapping("/weeklyschedule")
	public String getAll(Model model) {

        //Gets todays date, the date with which to start displaying the schedule
        LocalDate localDate = LocalDate.now();

        //Stores all the dates to be displayed on schedule
        List<LocalDate> dates = new ArrayList<>();
        
        //Creates a list of all employees in the database
        List<Employee> emps = eRepo.findAll();

        //2d array, the table to be displayed on webpage. Each row is an employee, 
        //each column a day of the week
        String[][] schedule = new String[emps.size()][7];

        //Period between start date, and date 7 days from now inclusive of both
        for(int period=0; period<Period.between(localDate, localDate.plus(Period.ofDays(7))).getDays(); period++) {
            LocalDate currentDate = localDate.plus(Period.ofDays(period));

            //Gets all the shifts for the day
            List<scheduleDatabase> allShifts = sdRepo.findByDate(currentDate);

            //Goes through each employee and each shift, and fills in the table with shift info
            for(int i=0; i<emps.size(); i++) {
                for(scheduleDatabase shift : allShifts) {
                    if(shift.getEmpId() == emps.get(i).getId()) {
                        schedule[emps.get(i).getId()-1][period] = shift.getShift();
                    }
                }
                //If the employee wasn't scheduled, then they are off for the day
                if(schedule[emps.get(i).getId()-1][period] == null) {
                    schedule[emps.get(i).getId()-1][period] = "Off";
                }
            }

            dates.add(currentDate);

        }

        //Makes this data available to html template
        model.addAttribute("dates", dates);
        model.addAttribute("emps", emps);
        model.addAttribute("schedule", schedule);
        
        return "weeklyschedule";
    }

}