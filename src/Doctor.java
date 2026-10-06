public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String availableTime;

    public Doctor(int id, String name, String specialization, String availableTime) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.availableTime = availableTime;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getAvailableTime() {
        return availableTime;
    }
}
