package uk.gov.dvsa.motr.web.component.subscription.model;

public class CancelledSubscription {
    private String unsubscribeId;

    private String vrm;

    private String email;

    private String motTestNumber;

    private String reasonForCancellation;

    public String getUnsubscribeId() {
        return unsubscribeId;
    }

    public CancelledSubscription setUnsubscribeId(String id) {
        this.unsubscribeId = id;
        return this;
    }

    public String getVrm() {
        return vrm;
    }

    public CancelledSubscription setVrm(String vrm) {
        this.vrm = vrm;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public String getReasonForCancellation() {
        return reasonForCancellation;
    }

    public CancelledSubscription setReasonForCancellation(String reasonForCancellation) {
        this.reasonForCancellation = reasonForCancellation;
        return this;
    }

    public CancelledSubscription setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getMotTestNumber() {
        return motTestNumber;
    }

    public CancelledSubscription setMotTestNumber(String motTestNumber) {
        this.motTestNumber = motTestNumber;
        return this;
    }
}
