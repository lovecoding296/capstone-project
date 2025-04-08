package funix.tgcp.util;

public record ApiResponse<T>(boolean success, String message, T booking) {
}
