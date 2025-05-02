package funix.tgcp.booking.payment;

public enum PaymentOption {
    FULL_PAYMENT("Full Payment"),       // Thanh toán 100% trước
    PAY_LATER("Pay Later");             // Thanh toán sau

    private final String displayName;    // Mô tả chi tiết của PaymentOption

    PaymentOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String toString() {
    	return displayName;
    }
}