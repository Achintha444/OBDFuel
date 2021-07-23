package com.mtdaps.obdfuel.activities.home.model;

import android.location.Location;

import com.mtdaps.obdfuel.util.UiUtil;

import java.util.HashMap;

/**
 * This is the class that used to create the object that will send the data to couchbase server
 */
public class OBDData {
    private static String vehicleName;
    private String currentLocation;
    // TODO: here we need to the rest of OBD realted data

    public OBDData(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public static String getVehicleName() {
        return vehicleName;
    }

    public static void setVehicleName(String vehicleName) {
        OBDData.vehicleName = vehicleName;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    /**
     * This method will use to create the HashMap that will send to Couchbase database
     *
     * @return OBDData Document
     */
    public HashMap<String, Object> getDocument(Location location) {
        HashMap<String, Object> obdDataDocument = new HashMap<>();
        obdDataDocument.put("timeStamp", UiUtil.getCurrentTimeMillis());
        obdDataDocument.put("vehicleName", vehicleName);
        obdDataDocument.put("vehicleLocation", getLocationHashMap(location));
        obdDataDocument.put("obdData", getRealOBDDataDocument());
        return obdDataDocument;
    }

    private HashMap<String, Double> getLocationHashMap(Location location) {
        HashMap<String, Double> locationDocument = new HashMap<>();
        locationDocument.put("latitude", location.getLatitude());
        locationDocument.put("longitude", location.getLongitude());
        return locationDocument;
    }

    // TODO: Implement this method
    private HashMap<String, Object> getRealOBDDataDocument() {
        HashMap<String, Object> realOBDDataDocument = new HashMap<>();
        return realOBDDataDocument;
    }
}
