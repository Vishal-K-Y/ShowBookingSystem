package service;

import pojo.BookingSlot;

import java.time.LocalTime;
import java.util.Arrays;

public class BookingSlotService {

    public BookingSlot fromString(String timeRange) {
        return Arrays.stream(BookingSlot.values())
                .filter(slot -> slot.toString().equals(timeRange.trim()))
                .findFirst()
                .orElse(null);
    }

    public BookingSlot getSlotFromStartTime(String timeStart) {
        LocalTime startTime = LocalTime.parse(timeStart);
        return Arrays.stream(BookingSlot.values())
                .filter(slot -> slot.getStart().equals(startTime))
                .findFirst()
                .orElse(null);
    }
}
