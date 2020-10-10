package com.example.biott;

public class Device {
    private String IPAddres;
    private String MAC;
    private String deviceModel;
    private DeviceType type;

    public Device(){

    }

    public Device(String IPAddress, String MAC, String deviceModel, DeviceType type){
        this.IPAddres = IPAddres;
        this.MAC = MAC;
        this.deviceModel = deviceModel;
        this.type = type;
    }

    public String getIPAddress(){
        return IPAddres;
    }

    public String getMAC(){
        return MAC;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public DeviceType getType() {
        return type;
    }

    public void setIPAddres(String IPAddress){
        this.IPAddres = IPAddress;
    }

    public void setMAC(String MAC){
        this.MAC = MAC;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setType(DeviceType type){
        this.type = type;
    }
}
