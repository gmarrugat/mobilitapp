package com.example.gerardmarrugat.accelerometer;


/**
 * Created by Gerard Marrugat on 23/10/2015.
 */


// We create a class for accelerometers data, with methods that allows us to get and set the distinct values
public class SensorData {
    private String timestamp;
    private double x;
    private double y;
    private double z;

    public SensorData(String timestamp, double x, double y, double z){
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

}
