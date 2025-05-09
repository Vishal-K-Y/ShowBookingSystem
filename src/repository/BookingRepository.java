package repository;

import pojo.BookingStatus;
import pojo.ShowSchedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BookingRepository {
    private final Map<ShowSchedule, List<BookingStatus>> bookings = new ConcurrentHashMap<>();

    public void addBooking(ShowSchedule schedule, BookingStatus status) {
        bookings.compute(schedule, (key, existingList) -> {
            List<BookingStatus> newList = existingList != null ? new ArrayList<>(existingList) : new ArrayList<>();
            newList.add(status);
            return newList;
        });
    }

    public void updateBookings(ShowSchedule schedule, List<BookingStatus> newBookings) {
        bookings.put(schedule, new ArrayList<>(newBookings));
    }

    public List<BookingStatus> getBookings(ShowSchedule schedule) {
        return new ArrayList<>(bookings.getOrDefault(schedule, new ArrayList<>()));
    }

    public List<BookingStatus> getBookingsOrEmpty(ShowSchedule schedule) {
        return new ArrayList<>(bookings.getOrDefault(schedule, new ArrayList<>()));
    }

    public List<ShowSchedule> getAllSchedules() {
        return new ArrayList<>(bookings.keySet());
    }
}