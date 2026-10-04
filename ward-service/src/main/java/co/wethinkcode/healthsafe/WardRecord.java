package co.wethinkcode.healthsafe;

public class WardRecord {
    String wardId;
    String wing;
    String department;
    Integer bedsAvailable;
    String notes;

    public WardRecord(String wardId, String wing, String department, Integer bedsAvailable, String notes){
        if (wardId == null || wardId.trim().isEmpty()){
            throw new IllegalArgumentException("Ward If cannot be empty or null");
        }
        this.wardId = wardId;
        this.wing = wing;
        this.department = department;
        this.bedsAvailable = bedsAvailable;
        this.notes = notes;
    }
    // i think we need a empty constructor for jackson
    public WardRecord(){};

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
