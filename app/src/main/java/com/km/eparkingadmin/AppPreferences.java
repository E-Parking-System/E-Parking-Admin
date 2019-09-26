package com.km.eparkingadmin;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Mirza Ahmed Baig on 2019-09-25.
 * Avantari Technologies
 * mirza@avantari.org
 */
class AppPreferences {
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    private String APP_SHARED_PREFS = "com.km.eparkinguser.preferences";
    private String USER_DETAILS = APP_SHARED_PREFS + ".userdetails";
    private String DEVICE_LIST = APP_SHARED_PREFS + ".addeddevicelist";

    AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(
                APP_SHARED_PREFS,
                Activity.MODE_PRIVATE
        );
        this._prefsEditor = _sharedPrefs.edit();
        this._prefsEditor.apply();
    }

    String getUserDetails() {
        return _sharedPrefs.getString(USER_DETAILS, null);
    }

    /**
     * User details input format should follow username,email,bikename,licencenumber
     */
    void setUserDetails(String userDetails) {
        _prefsEditor.putString(USER_DETAILS, userDetails);
        _prefsEditor.commit();
    }

    ArrayList<PaymentModel> getParkedVehicleList() {
        String devicesList = _sharedPrefs.getString(DEVICE_LIST, null);
        if (devicesList == null) {
            return new ArrayList<>();
        } else {
            return new Gson().fromJson(devicesList, new TypeToken<ArrayList<PaymentModel>>() {
            }.getType());
        }
    }

    private void setParkedVehicleList(ArrayList<PaymentModel> list) {
        _prefsEditor.putString(DEVICE_LIST, new Gson().toJson(list));
        _prefsEditor.commit();
    }

    PaymentModel getVehicleInfo(String licenceNumber) {
        ArrayList<PaymentModel> list = getParkedVehicleList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLicenceNumber().equalsIgnoreCase(licenceNumber)) {
                return list.get(i);
            }
        }
        return null;
    }

    void addNewParkingVehicle(String licenceNumber) {
        ArrayList<PaymentModel> list = getParkedVehicleList();
        boolean duplicateLicence = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLicenceNumber().equalsIgnoreCase(licenceNumber)) {
                duplicateLicence = true;
                break;
            }
        }
        if (!duplicateLicence) {
            list.add(new PaymentModel(licenceNumber, System.currentTimeMillis()));
            setParkedVehicleList(list);
        }
    }

    void removeVehicelFromList(String licenceNumber) {
        ArrayList<PaymentModel> list = getParkedVehicleList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLicenceNumber().equalsIgnoreCase(licenceNumber)) {
                list.remove(i);
                break;
            }
        }
        setParkedVehicleList(list);
    }


}
