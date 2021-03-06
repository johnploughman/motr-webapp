package uk.gov.dvsa.motr.web.cookie;

import java.io.Serializable;

abstract class SubscriptionParams implements Serializable {

    private String email;
    private String expiryDate;
    private String registration;

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getExpiryDate() {

        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {

        this.expiryDate = expiryDate;
    }

    public String getRegistration() {

        return registration;
    }

    public void setRegistration(String registration) {

        this.registration = registration;
    }
}
