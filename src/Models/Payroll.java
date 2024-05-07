package Models;
import java.time.LocalDate;
import java.util.Arrays;

public class Payroll {
    private Employee employee;
    private Config config;
    private String systemDate;
    public Payroll(Employee employee, Config config, String systemDate) {
        this.employee = employee;
        this.config = config;
        this.systemDate = systemDate;
    }
    private double getEssalud() {
        return config.getEssalud()*employee.getEmpSalary();
    }
    public double getGratification(int monthManual) {
        int[] gratificationMonths = config.getGratificationMonths();
        int getEmpStartYear = employee.getStartYear();
        int getEmpStartMonth = employee.getStartMonth();
        for (int month : gratificationMonths) {
            if (month == monthManual) {

                if(getEmpStartYear == getSystemYear()) {
                    if(getEmpStartMonth >= monthManual) {
                        return 0;
                    }
                    int semesterMonthWorked= Math.min(month - (getEmpStartMonth>6?getEmpStartMonth+1:getEmpStartMonth), 6);
                    return (employee.getEmpSalary() / 6) * semesterMonthWorked + getEssalud();
                }else{
                    return (employee.getEmpSalary() / 6)*6 + getEssalud();
                }
            }
            continue;
        }
        return 0;
    }
    public double getFifthCategory() {
        double remaining = Math.max(0, getSalaryForecast()+getGratificationForeCast() - (config.getUit() * 7));
        if (remaining == 0) {
            return 0;
        }
        double fifthCategory = 0;
        Segment[] segments = config.getSegments();
        for (Segment segment : segments) {
            if (segment.getSup() == 999999) {
                fifthCategory += remaining * segment.getRet();
                break;
            } else if (segment.getId()==segments.length) {
                fifthCategory += remaining * segment.getRet();
                break;
            } else {
                fifthCategory += (segment.getSup() - segment.getInf()) * config.getUit() * segment.getRet();
                remaining -= (segment.getSup() - segment.getInf()) * config.getUit();
            }
        }
        return fifthCategory;
    }


    public double getSalaryForecast() {
        return employee.getStartYear()==getSystemYear()?employee.getEmpSalary()*(13-employee.getStartMonth()):employee.getEmpSalary()*12;
    }
    public double getGratificationForeCast(){
        System.out.println(getGratification(7));
        System.out.println(getGratification(12));
        return getGratification(7)+getGratification(12);

    }
    private int getSystemYear() {
        return Integer.parseInt(systemDate.substring(0, 4));
    }
    private int getSystemMonth() {
        return Integer.parseInt(systemDate.substring(5, 7));
    }
}
