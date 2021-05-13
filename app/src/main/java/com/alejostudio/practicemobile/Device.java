package com.alejostudio.practicemobile;

public class Device {

    private String name;
    private String id;
    private Boolean status;
    private double lastActivity;

    public Device(String name, String id, Boolean status, double lastActivity){
        this.id = id;
        this.name = name;
        this.status = status;
        this.lastActivity = lastActivity;
    }

    public String getName() {
        return name;
    }

    public Boolean getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public double getLastActivity() {
        return lastActivity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setLastActivity(double lastActivity) {
        this.lastActivity = lastActivity;
    }
}
