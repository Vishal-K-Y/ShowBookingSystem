import java.time.LocalTime;
import java.util.Arrays;


public enum BookingSlot {
    SLOT_09_10(LocalTime.of(9, 0), LocalTime.of(10, 0)),
    SLOT_10_11(LocalTime.of(10, 0), LocalTime.of(11, 0)),
    SLOT_11_12(LocalTime.of(11, 0), LocalTime.of(12, 0)),
    SLOT_12_13(LocalTime.of(12, 0), LocalTime.of(13, 0)),
    SLOT_13_14(LocalTime.of(13, 0), LocalTime.of(14, 0)),
    SLOT_14_15(LocalTime.of(14, 0), LocalTime.of(15, 0)),
    SLOT_15_16(LocalTime.of(15, 0), LocalTime.of(16, 0)),
    SLOT_16_17(LocalTime.of(16, 0), LocalTime.of(17, 0)),
    SLOT_17_18(LocalTime.of(17, 0), LocalTime.of(18, 0)),
    SLOT_18_19(LocalTime.of(18, 0), LocalTime.of(19, 0)),
    SLOT_19_20(LocalTime.of(19, 0), LocalTime.of(20, 0)),
    SLOT_20_21(LocalTime.of(20, 0), LocalTime.of(21, 0));

    private final LocalTime start;
    private final LocalTime end;

    BookingSlot(LocalTime start, LocalTime end) {
        
        if (start.isBefore(LocalTime.of(9, 0)) || start.isAfter(LocalTime.of(20, 0))) {
            throw new IllegalArgumentException("Start time must be between 09:00 and 20:00.");
        }
        if (!end.equals(start.plusHours(1))) {
            throw new IllegalArgumentException("Each slot must be exactly 1 hour long.");
        }
        if (start.getMinute() != 0) {
            throw new IllegalArgumentException("Start time must be exactly on the hour (e.g., 9:00, 10:00, etc.).");
        }    
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public static BookingSlot fromString(String timeRange) {
        return Arrays.stream(values())
                    .filter(slot -> slot.toString().equals(timeRange.trim()))
                    .findFirst()
                    .orElse(null);
    }


    public static BookingSlot getSlotFromStartTime(String timeStart) {
        LocalTime startTime = LocalTime.parse(timeStart);
        LocalTime endTime = startTime.plusHours(1);
        String fullRange = startTime + "-" + endTime;

        return BookingSlot.fromString(fullRange); 
    }

    @Override
    public String toString() {
        return start + "-" + end;
    }
}
