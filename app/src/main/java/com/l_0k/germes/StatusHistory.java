package com.l_0k.germes;

/**
 * Created by knyazev_o on 21.12.2014.
 * Status of task class for StatusHistoryAdapter
 */
public class StatusHistory {
    private String StatusTimeStamp;
    private String Status;
    private String Latitude;
    private String Longitude;
    private String Address;

    StatusHistory(String StatusTimeStamp, String Status, String Latitude, String Longitude, String Address) {
        this.StatusTimeStamp = StatusTimeStamp;
        this.Status = Status;
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.Address = Address;
    }

    public String getStatusTimeStamp() {
        return StatusTimeStamp;
    }

    public void setStatusTimeStamp(String statusTimeStamp) {
        StatusTimeStamp = statusTimeStamp;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

}
