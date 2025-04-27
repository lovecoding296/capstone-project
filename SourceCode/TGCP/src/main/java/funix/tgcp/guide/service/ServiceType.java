package funix.tgcp.guide.service;

public enum ServiceType {
    CITY_TOUR("City Tour"),
    NATURE_TOUR("Nature Tour"),
    TRANSLATOR("Translator"),
    PHOTOGRAPHER("Photographer");

    private final String displayName;

    // Constructor
    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    // Getter for displayName
    public String getDisplayName() {
        return displayName;
    }
    
    public String toString() {
    	return displayName;
    }
}
