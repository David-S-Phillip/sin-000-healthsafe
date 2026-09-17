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

    public String getWardId(){
        return this.wardId;
    }

    public String getWing(){
        return this.wing;
    }

    public String getDepartment(){
        return this.department;
    }

    public Integer getBedsAvailable(){
        return this.bedsAvailable;
    }

    public String getNotes(){
        return this.notes;
    }
}
