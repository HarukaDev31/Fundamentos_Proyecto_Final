package Models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class PayrollDetail extends Payroll {
    private PayrollDetailConcept[] payrollDetailConcepts;

    public PayrollDetail(Employee employee, Config config, String systemDate, PayrollDetailConcept[] payrollDetailConcepts) {
        super(employee, config, systemDate);
        this.payrollDetailConcepts = payrollDetailConcepts;
    }

    public void generatePayrollDetailsJson() throws FileNotFoundException {
        JSONArray payrollDetailsJson = getPayrollDetailsJson();
        try (FileWriter file = new FileWriter("src/payrollDetails.json")) { // Abrir el archivo en modo de agregar
            JSONObject payrollDetailJson = new JSONObject();
            JSONObject payrollEmployeeJson = new JSONObject();
            payrollDetailJson.put("employeeId", employee.getEmpCode());
            payrollDetailJson.put("employeeName", employee.getEmpName()+" "+employee.getEmpLastName());
            payrollDetailJson.put("systemDate", systemDate);
            JSONArray payrollDetailConceptsJson = new JSONArray();
            for (PayrollDetailConcept payrollDetailConcept : payrollDetailConcepts) {
                JSONObject payrollDetailConceptJson = new JSONObject();
                payrollDetailConceptJson.put("conceptName", payrollDetailConcept.getConceptName());
                payrollDetailConceptJson.put("conceptAmount", payrollDetailConcept.getConceptAmount());
                payrollDetailConceptJson.put("conceptType", payrollDetailConcept.getConceptType());
                payrollDetailConceptsJson.put(payrollDetailConceptJson);
            }
            payrollDetailJson.put("payrollDetailConcepts", payrollDetailConceptsJson);
            if (payrollDetailsJson.length() == 0) {
                payrollDetailsJson.put(payrollDetailJson);
                file.write(payrollDetailsJson.toString());
            } else {
                //if not empty, add new payroll detail to the array of payroll details
                payrollDetailsJson.put(payrollDetailJson);
                file.write(payrollDetailsJson.toString());

            }

            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getPayrollDetailsJson() throws FileNotFoundException {
        JSONArray payrollDetailsJson = new JSONArray();
        try (FileReader reader = new FileReader("src/payrollDetails.json")) {
            int character;
            String payrollDetailsString = "";
            while ((character = reader.read()) != -1) {
                payrollDetailsString += (char) character;
            }
            payrollDetailsJson = new JSONArray(payrollDetailsString);
        } catch (IOException e) {
            //RETURN EMPTY JSON ARRAY
            return payrollDetailsJson;
        } catch (Exception e) {
            return payrollDetailsJson;
        }
        return payrollDetailsJson;
    }

    public void initFile(String filePath) {
        File file = new File(filePath);

        // Check if the file exists
        if (!file.exists()) {
            try {
                // Create the file
                boolean fileCreated = file.createNewFile();
                if (fileCreated) {
                    System.out.println("File created successfully: " + filePath);
                } else {
                    System.out.println("Failed to create the file: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File already exists: " + filePath);
        }
    }
}
