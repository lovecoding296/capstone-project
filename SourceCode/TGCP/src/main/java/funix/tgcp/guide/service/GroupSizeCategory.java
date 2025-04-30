package funix.tgcp.guide.service;

public enum GroupSizeCategory {
    UNDER_5("Under 5"),
    FROM_5_TO_10("From 5 to 10"),
    OVER_10("Over 10");

    private final String displayName;

    // Constructor để gán giá trị displayName cho từng hằng số
    GroupSizeCategory(String displayName) {
        this.displayName = displayName;
    }

    // Getter để lấy displayName
    public String getDisplayName() {
        return displayName;
    }

    public String toString() {
    	return displayName;
    }

}
