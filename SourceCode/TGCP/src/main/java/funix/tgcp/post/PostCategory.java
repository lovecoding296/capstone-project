package funix.tgcp.post;

public enum PostCategory {
    EXPERIENCE_SHARING("Experience Sharing"),
    TRAVEL_QNA("Travel Q&A"),
    HOT_DESTINATIONS("Hot Destinations"),
    FOOD_SPECIALTIES("Food Specialties"),
    CULTURE_AND_PEOPLE("Culture and People"),
    SERVICE_REVIEWS("Service Reviews"),
    TICKET_BOOKING_TIPS("Ticket Booking Tips"),
    TRAVEL_DIARY("Travel Diary");

    private final String displayName;

    PostCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
