package com.example.turkeyproject;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class BLEDevices {

    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<BluetoothDevice> address = new ArrayList<>();
    private HashMap<String, Boolean> addressToToConnectMap = new HashMap<>();

    public void addDevice(String name, BluetoothDevice address) {
        this.name.add(name);
        this.address.add(address);
        addressToToConnectMap.put(address.getAddress(), new Boolean(false));
    }

    public void clearDevices() {
        name.clear();
        address.clear();
        addressToToConnectMap.clear();
    }

    public void selectDevice(BluetoothDevice address) {
        boolean newValue = !addressToToConnectMap.get(address.getAddress()).booleanValue();
        addressToToConnectMap.put(address.getAddress(), new Boolean(newValue));
    }

    public void selectDevice(int position) {
        BluetoothDevice address = this.address.get(position);
        selectDevice(address);
    }

    public boolean isDeviceSelected(BluetoothDevice address) {
        return addressToToConnectMap.get(address.getAddress()).booleanValue();
    }

    public boolean isDeviceSelected(int index) {
        return isDeviceSelected(address.get(index));
    }

    public ArrayList<String> getNames() {
        return name;
    }

    public ArrayList<BluetoothDevice> getAddress() {
        return address;
    }

    public ArrayList<BluetoothDevice> getSelectedBluetoothDevices() {
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        for (int i = 0; i < address.size(); i++) {
            if (addressToToConnectMap.get(address.get(i).getAddress()).booleanValue()) {
                devices.add(address.get(i));
                Log.d("connect", address.get(i).getName());
            }
        }
        return devices;
    }
}