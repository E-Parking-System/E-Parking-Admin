package com.km.eparkingadmin;

/**
 * Created by Mirza Ahmed Baig on 2019-09-26.
 * Avantari Technologies
 * mirza@avantari.org
 */
class PaymentModel {
    private String licenceNumber;
    private long checkInTime;

    PaymentModel(String licenceNumber, long checkInTime) {
        this.licenceNumber = licenceNumber;
        this.checkInTime = checkInTime;
    }

    long getCheckInTime() {
        return checkInTime;
    }

    void setCheckInTime(long checkInTime) {
        this.checkInTime = checkInTime;
    }

    String getLicenceNumber() {
        return licenceNumber;
    }

    void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }
}
