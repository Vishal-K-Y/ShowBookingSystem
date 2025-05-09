package service;

import pojo.*;
import repository.*;
import util.HashUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TicketBookingSystem {

        private final int SEAT_CAPACITY=50;
        private static TicketBookingSystem instance;
        private final UserRepository registeredUser = new UserRepository();
        private final ShowRepository registeredShows = new ShowRepository();
        private final ShowScheduleRepository showSchedules = new ShowScheduleRepository();
        private final BookingRepository bookings = new BookingRepository();
        private final WaitingQueueRepository waitingQueue = new WaitingQueueRepository();
        private TicketBookingSystem() {

        }

        public static synchronized TicketBookingSystem getInstance() {
            if (instance == null) {
                instance = new TicketBookingSystem();
            }
            return instance;
        }

        //register show
        public void registerShow(String showName, String genre){
            Show show=new Show(showName, genre);
            registeredShows.addShow(show);
            System.out.println(showName+" show is registered !!");
        }

        //onboard slots
        public void onboardShowSlots(String input) {

            Pattern timePattern = Pattern.compile("\\d{1,2}:\\d{2}-\\d{1,2}:\\d{2}");
            Matcher matcher = timePattern.matcher(input);

            if (!matcher.find()) {
                System.out.println("Invalid input format. No time slots detected.");
                return;
            }

            int slotStartIndex = matcher.start();
            String showName = input.substring(0, slotStartIndex).trim();
            String slotsInfo = input.substring(slotStartIndex).trim();
            String[] slotPairs = slotsInfo.split(", ");
            for (String pair : slotPairs) {
                String[] details = pair.split(" ");
                if (details.length < 1) continue;

                String timeSlot = details[0];
                // Format the time slot if it starts with 9:
                if (timeSlot.startsWith("9:")) {
                    timeSlot = "09:" + timeSlot.substring(2);
                }
                int screenNumber = details.length > 1 ? Integer.parseInt(details[1]) : 0;

                Show show = showExistsWithName(showName)
                        .orElseThrow(() -> new IllegalArgumentException("Show does not exist: " + showName));

                BookingSlot bs = BookingSlot.fromString(timeSlot);
                if (bs == null) {
                    System.out.println("Invalid time slot format: " + timeSlot);
                    continue;
                }

                if (!isSlotAvailable(bs, screenNumber)) {
                    System.out.println("Slot " + bs + " on screen " + screenNumber + " is already occupied.");
                    continue;
                }

                ShowSchedule showSchedule = new ShowSchedule(show, bs, new Screen(screenNumber, SEAT_CAPACITY));
                bookings.addBooking(showSchedule, new BookingStatus(UUID.randomUUID(), true, null, 0));
                showSchedules.addSchedule(showSchedule);

                System.out.println("Slot " + bs + " onboarded for show " + showName + " on screen " + screenNumber + ".");
            }
        }

        public void bookTickets(String userName, String showName, String time_start, int numberOfTickets) {
            bookTickets( userName, showName, time_start, numberOfTickets, 1);
        }


        //user books ticket
        public void bookTickets(String userName, String showName, String time_start, int numberOfTickets, double ignoreThis) {
            BookingSlot slot=BookingSlot.getSlotFromStartTime(time_start);
            UUID bookingId = UUID.randomUUID();
            Optional<ShowSchedule> schedule = showSchedules.getAllSchedules().stream()
                    .filter(x -> x.getSlot().equals(slot) && x.getShow().getShowName().equals(showName))
                    .findAny();

            User user = registeredUser.findByName(userName)
                    .orElseGet(() -> {
                        User newUser = new User(userName);
                        registeredUser.addUser(newUser);
                        return newUser;
                    });

            System.out.println(schedule);

            if (!schedule.isPresent()) {
                System.out.println(showName + "is not scueduled at " + time_start);
                return;
            }
            if(!user.canBookSlot(slot)){
                System.out.println("Booking unsuccessful: '" + user.getShow(slot).getShowName() + "' is already scheduled at " + slot + ". Please choose a different time slot.");
                return;
            }
            ShowSchedule scheduleEntry = schedule.get();
            user.addBookingSlot(slot, scheduleEntry.getShow());
            List<BookingStatus> bookingList = bookings.getBookings(scheduleEntry);
            if (bookingList.size() + numberOfTickets <= scheduleEntry.getScreen().getCapacity()) {
                List<BookingStatus> newBookings = new ArrayList<>(bookingList);
                for (int i = 0; i < numberOfTickets; i++) {
                    newBookings.add(new BookingStatus(bookingId, true, user, numberOfTickets));
                }
                for (BookingStatus status : newBookings) {
                    bookings.addBooking(scheduleEntry, status);
                }
                System.out.println("Booking for "+numberOfTickets+" confirmed for " + userName + " at " + time_start + " on screen " + scheduleEntry.getScreen());
            } else {
                waitingQueue.enqueue(new BookingStatus(bookingId, false, user, numberOfTickets));
                System.out.println("Slot full. Added " + userName + " to waitlist.");
            }
        }

        //USER cancels their ticket
        public void cancelTicket(UUID bookingId) {
            ShowSchedule matchedSchedule = null;
            BookingStatus targetBooking = null;

            // Step 1: Find the booking and associated schedule
            for (ShowSchedule schedule : bookings.getAllSchedules()) {
                List<BookingStatus> statuses = bookings.getBookings(schedule);
                for (BookingStatus bs : statuses) {
                    if (bs.getBookingId().equals(bookingId)) {
                        if (bs.isConfirmed()) {
                            matchedSchedule = schedule;
                            targetBooking = bs;
                        }
                        break;
                    }
                }
                if (targetBooking != null) break;
            }

            if (matchedSchedule == null || targetBooking == null) {
                System.out.println("No confirmed booking found for this ID.");
                return;
            }

            User user = targetBooking.getUser();
            if (user != null) {
                user.removeBookingSlot(matchedSchedule.getSlot());
            }

            int seatsToFree = targetBooking.getTicketCount();

            List<BookingStatus> bookingList = bookings.getBookings(matchedSchedule);
            bookingList.removeIf(bs -> bs.getBookingId().equals(bookingId));
            bookings.updateBookings(matchedSchedule, bookingList);
            System.out.println("Cancelled booking ID: " + bookingId + " Tickets impacted: " + seatsToFree);

            // Step 3: Promote waitlisted users
            while (seatsToFree > 0 && !waitingQueue.isEmpty()) {
                BookingStatus next = waitingQueue.dequeue();
                int neededSeats = next.getTicketCount();

                if (neededSeats <= seatsToFree) {
                    next.setConfirmed(true);
                    bookingList.add(next);
                    bookings.updateBookings(matchedSchedule, bookingList);
                    seatsToFree -= neededSeats;
                    System.out.println("Promoted user: " + next.getUser().getName());
                } else {
                    break;
                }
            }
        }

        //show tickets available by genre
        public List<ShowSchedule> getAvailableShowsByGenre(String genre) {
            return showSchedules.stream()
                    .filter(s -> genre.equals(s.getShow().getGenre()))
                    .collect(Collectors.toList());
        }

        public Optional<Show> showExistsWithName(String showName) {
            return registeredShows.stream()
                    .filter(show -> showName.equals(show.getShowName()))
                    .findFirst();
        }

        public boolean isSlotAvailable(BookingSlot slot, int screenNumber) {
            String hashKey = HashUtil.generateUniqueKey(slot, screenNumber);

            System.out.println(hashKey);
            return bookings.getAllSchedules().stream()
                    .noneMatch(schedule -> HashUtil.generateUniqueKey(schedule.getSlot(), schedule.getScreen().getScreenNumber()).equals(hashKey));
        }

        private Optional<User> checkIfUserExists(String name) {
            return registeredUser.stream()
                    .filter(x -> x.getName().equals(name))
                    .findFirst();
        }

        public String formatTime(String time) {
            return time.startsWith("9:") ? "09:" + time.substring(2) : time;
        }

        public void viewShowBookings(String showName, String timeStart) {
            BookingSlot slot = BookingSlot.getSlotFromStartTime(timeStart);
            Optional<ShowSchedule> schedule = showSchedules.getAllSchedules().stream()
                    .filter(x -> x.getSlot().equals(slot) && x.getShow().getShowName().equals(showName))
                    .findAny();

            if (!schedule.isPresent()) {
                System.out.println(showName + " is not scheduled at " + timeStart);
                return;
            }

            ShowSchedule scheduleEntry = schedule.get();
            List<BookingStatus> bookings = this.bookings.getBookings(scheduleEntry);
            
            // Filter out the initial null booking
            List<BookingStatus> actualBookings = bookings.stream()
                    .filter(booking -> booking.getUser() != null)
                    .toList();
            
            System.out.println("\nBookings for " + showName + " at " + timeStart + ":");
            System.out.println("Total bookings: " + actualBookings.size());
            System.out.println("Screen: " + scheduleEntry.getScreen().getScreenNumber());
            System.out.println("Capacity: " + scheduleEntry.getScreen().getCapacity());
            System.out.println("Available seats: " + (scheduleEntry.getScreen().getCapacity() - actualBookings.size()));
            
            if (!actualBookings.isEmpty()) {
                System.out.println("\nBooking details:");
                actualBookings.forEach(booking -> 
                    System.out.println("Booking ID: " + booking.getBookingId() + 
                                     ", User: " + booking.getUser().getName() + 
                                     ", Tickets: " + booking.getTicketCount() +
                                     ", Status: " + (booking.isConfirmed() ? "Confirmed" : "Waitlisted"))
                );
            }
        }
    }

