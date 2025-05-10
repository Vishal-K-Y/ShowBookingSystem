## Main.java contains main method to interact with the application

## TestDriver.java tests working of all the functions.

# Commands:
```
- registerShow: <Show Name> -> <Genre> - Add a new show.
- onboardShowSlots: <Show Name> <Time Slot> <Screen Number>, ... - Add times/screens (1-hour slots).
- bookTicket: (User Name, Show Name, Time, Number of Tickets) - Book tickets.
- cancelBookingId: <Booking ID> - Cancel a booking.
- showAvailByGenre: <Genre> - See shows by genre.
```

# Notes:
- Show times are fixed 1-hour slots, available from 09:00 to 21:00.
- Screen numbers are unique for each time slot.
- Bookings can be cancelled using the booking ID.
- Users can only have one active booking for any given time slot. To book a different show at the same time, cancel the existing booking first.
- Shows can be filtered by genre.
- The system maintains availability in real-time.
