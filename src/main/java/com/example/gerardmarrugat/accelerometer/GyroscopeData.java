package com.example.gerardmarrugat.accelerometer;

/**
 * Created by Gerard Marrugat on 05/02/2016.
 */


// We create a class for gyroscope data, with methods that allows us to get and set the distinct values
public class GyroscopeData extends SensorData {
        private String timestamp;
        private double x;
        private double y;
        private double z;

        public GyroscopeData(String timestamp, double x, double y, double z){
            //Extend AccelerometerData class and use its constructor method to implements GyroscopeData constructor
            super(timestamp, x, y, z);

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
