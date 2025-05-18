public class Prescription {
    private String medicineName, whenToTake, beforeAfter, quantity, timesPerDay, note;

    public Prescription(String medicineName, String whenToTake, String beforeAfter, String quantity, String timesPerDay, String note) {
        this.medicineName = medicineName;
        this.whenToTake = whenToTake;
        this.beforeAfter = beforeAfter;
        this.quantity = quantity;
        this.timesPerDay = timesPerDay;
        this.note = note;
    }

    public String getMedicineName() { return medicineName; }
    public String getWhenToTake() { return whenToTake; }
    public String getBeforeAfter() { return beforeAfter; }
    public String getQuantity() { return quantity; }
    public String getTimesPerDay() { return timesPerDay; }
    public String getNote() { return note; }
}
