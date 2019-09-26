package com.km.eparkingadmin;

/**
 * Created by Mirza Ahmed Baig on 2019-09-26.
 * Avantari Technologies
 * mirza@avantari.org
 */
class UserModel {
    private String userName;
    private String userEmail;
    private String organisationName;
    private double parkingFee;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getParkingFee() {
        return parkingFee;
    }

    public void setParkingFee(double parkingFee) {
        this.parkingFee = parkingFee;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }
}
