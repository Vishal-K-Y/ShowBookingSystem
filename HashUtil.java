

public class HashUtil {
    public static String generateUniqueKey(BookingSlot slot, int screenNumber) {
        return slot.toString() + "-" + screenNumber; // Always unique per slot-screen pair
    }
}
