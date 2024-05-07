import Mailer.SendMailSSL;
import Models.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class Main {
    public static void main(String[] args) {
        SystemUpc sysUpc = new SystemUpc("src/employees.json");
        Employee emp =new Employee("Perez", "E001", "Juan", "30000", "harukakasugano31@gmail.com", 1, "2024-01-01");
       String configString = sysUpc.getJsonDataString("src/config.json");
       try{
           JSONObject configJson = new JSONObject(configString);
           double uit = configJson.getDouble("uit");
           JSONArray segmentsJson = configJson.getJSONArray("segments");
           Segment[] segments = new Segment[segmentsJson.length()];
              for (int i = 0; i < segmentsJson.length(); i++) {
                JSONObject segmentJson = segmentsJson.getJSONObject(i);
                int id = segmentJson.getInt("id");
                String name = segmentJson.getString("name");
                double inf = segmentJson.getDouble("inf");
                double sup = segmentJson.getDouble("sup");
                double ret = segmentJson.getDouble("ret");
                segments[i] = new Segment(id, name, inf, sup, ret);
              }
                JSONArray affiliationsJson = configJson.getJSONArray("affiliations");
                Affiliation[] affiliations = new Affiliation[affiliationsJson.length()];
                for (int i = 0; i < affiliationsJson.length(); i++) {
                    JSONObject affiliationJson = affiliationsJson.getJSONObject(i);
                    int id = affiliationJson.getInt("id");
                    String name = affiliationJson.getString("name");
                    double capture = affiliationJson.getDouble("capture");
                    double flow = affiliationJson.getDouble("flow");
                    affiliations[i] = new Affiliation(id, name, capture, flow);
                }
                double essalud = configJson.getDouble("essalud");
                JSONArray gratificationMonthsJson = configJson.getJSONArray("gratificationMonths");
                int[] gratificationMonths = new int[gratificationMonthsJson.length()];
                for (int i = 0; i < gratificationMonthsJson.length(); i++) {
                    gratificationMonths[i] = gratificationMonthsJson.getInt(i);
                }
                JSONArray ctsMonthsJson = configJson.getJSONArray("ctsMonths");
                int[] ctsMonths = new int[ctsMonthsJson.length()];
                for (int i = 0; i < ctsMonthsJson.length(); i++) {
                    ctsMonths[i] = ctsMonthsJson.getInt(i);
                }
                Config config = new Config(uit, segments, affiliations, essalud, gratificationMonths, ctsMonths);
                String SystemDate = "2024-07-01";
                Payroll payroll = new Payroll(emp, config, SystemDate);
                payroll.generatePayrollJson();
       }catch (Exception e) {
           System.out.println("Error: " + e.getMessage());
       }
        //Comentario 2
        /*
        Method to send Email
        SendMailSSL mail = new SendMailSSL();
        mail.sendMail("harukakasugano32@gmail.com", "Prueba", "Prueba de correo");*/
    }
}