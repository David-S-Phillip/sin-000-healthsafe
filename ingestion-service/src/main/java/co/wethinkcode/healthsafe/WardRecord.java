package co.wethinkcode.healthsafe;

public class WardRecord {
    String wardId;
    String wing;
    String department;
    Integer bedsAvailable;
    String notes;

    public WardRecord(String wardId, String wing, String department, Integer bedsAvailable, String notes){
        this.wardId = wardId;
        this.wing = wing;
        this.department = department;
        this.bedsAvailable = bedsAvailable;
        this.notes = notes;
    }

    public String wardId(){
        return this.wardId;
    }

    public String wing(){
        return this.wing;
    }

    public String getDepartment(){
        return this.department;
    }

    public Integer bedsAvailable(){
        return this.bedsAvailable;
    }

    public String notes(){
        return this.notes;
    }
}
