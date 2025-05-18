public class VitalSign {
    private String date;
    private String heartRate;
    private String oxygenLevel;
    private String temperature;
    private String bloodPressure;

    public VitalSign(String date, String heartRate, String oxygenLevel, String temperature, String bloodPressure) {
        this.date = date;
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.temperature = temperature;
        this.bloodPressure = bloodPressure;
    }

    public String getDate() {
        return date;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public String getOxygenLevel() {
        return oxygenLevel;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }
}
