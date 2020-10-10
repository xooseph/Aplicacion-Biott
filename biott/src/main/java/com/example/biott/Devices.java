package com.example.biott;

import java.util.ArrayList;
import java.util.List;

public class Devices {
    protected static List<Device> deviceVector = demoDevice();

    public Devices(){
        deviceVector = demoDevice();
    }

    static Device element(int id) {
        return deviceVector.get(id);
    }

    static void add(Device device) {
        deviceVector.add(device);
    }

    static int newDevice() {
        Device device = new Device();
        deviceVector.add(device);
        return deviceVector.size() - 1;
    }

    public static void erase(int id) {
        deviceVector.remove(id);
    }

    public static int size() {
        return deviceVector.size();
    }

    public static ArrayList<Device> demoDevice() {
        ArrayList<Device> devices = new ArrayList<Device>();

        //devices.add(new Device("Biott-Turmix", DeviceType.INDUCTIONSTOVE));
        return devices;
    }

}
