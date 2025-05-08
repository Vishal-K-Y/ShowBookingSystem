import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TicketBookingSystem {
    private List<User> registeredUser;
    private final int SEAT_CAPACITY=50;
    private static TicketBookingSystem instance;
    private List<Show> registeredShows;
    private List<ShowSchedule> showSchedules;
    private Map<ShowSchedule, List<BookingStatus>> bookings; 
    private Queue<BookingStatus> waitingQueue;

    private TicketBookingSystem() {
            showSchedules = new ArrayList<>();
            bookings = new ConcurrentHashMap<>();
            registeredShows=new ArrayList<>();
            registeredUser=new ArrayList<>();
            waitingQueue=new LinkedList<>();
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
        registeredShows.add(show);
        System.out.println(showName+" show is registered !!");
    }

    //onboard slots
    public void onboardShowSlots(String input) {
        // Use regex to locate where the first time slot starts
        Pattern timePattern = Pattern.compile("\\d{1,2}:\\d{2}-\\d{1,2}:\\d{2}");
        Matcher matcher = timePattern.matcher(input);
    
        if (!matcher.find()) {
            System.out.println("Invalid input format. No time slots detected.");
            return;
        }
    
        int slotStartIndex = matcher.start();
        String showName = input.substring(0, slotStartIndex).trim();
        String slotsInfo = input.substring(slotStartIndex).trim();
        if(slotsInfo.startsWith("9:")) formatTime(slotsInfo);
        String[] slotPairs = slotsInfo.split(", ");
        for (String pair : slotPairs) {
            String[] details = pair.split(" ");
            if (details.length < 1) continue;
    
            String timeSlot = details[0];
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
            bookings.put(showSchedule, bookings.getOrDefault(showSchedule, new ArrayList<>()));
            showSchedules.add(showSchedule);
    
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
        Optional<ShowSchedule> schedule = showSchedules.stream()
                                            .filter(x -> x.getSlot().equals(slot) && x.getShow().getShowName().equals(showName))
                                            .findAny();
                    
        User user = checkIfUserExists(userName)
                        .orElseGet(() -> {
                                    User newUser = new User(userName);
                                    registeredUser.add(newUser);
                                    return newUser;});
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
        List<BookingStatus> bookingList = bookings.get(scheduleEntry);
        if (bookingList.size() + numberOfTickets <= scheduleEntry.getScreen().capacity) {
            for (int i = 0; i < numberOfTickets; i++) { 
                bookingList.add(new BookingStatus(bookingId, true, user, numberOfTickets));
            }
            bookings.put(scheduleEntry, bookingList);
            System.out.println("Booking for "+numberOfTickets+" confirmed for " + userName + " at " + time_start + " on screen " + scheduleEntry.getScreen());
        } else {
            waitingQueue.offer(new BookingStatus(bookingId, false, user, numberOfTickets));
            System.out.println("Slot full. Added " + userName + " to waitlist.");
        }
    }

    //USER cancels their ticket
    public void cancelTicket(UUID bookingId) {
        ShowSchedule matchedSchedule = null;
        BookingStatus targetBooking = null;
    
        // Step 1: Find the booking and associated schedule
        for (var entry : bookings.entrySet()) {
            for (BookingStatus bs : entry.getValue()) {
                if (bs.getBookingId().equals(bookingId)) {
                    if (bs.isConfirmed()) {
                        matchedSchedule = entry.getKey();
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
    
        int seatsToFree = targetBooking.getTicketCount();
    
        // Step 2: Remove all matching booking statuses with that booking ID
        List<BookingStatus> bookingList = bookings.get(matchedSchedule);
        bookingList.removeIf(bs -> bs.getBookingId().equals(bookingId));
        bookings.put(matchedSchedule, bookingList);
        System.out.println("Cancelled booking ID: " + bookingId+" Tickets impacted: "+seatsToFree);
    
        // Step 3: Promote waitlisted users
        while (seatsToFree > 0 && !waitingQueue.isEmpty()) {
            BookingStatus next = waitingQueue.peek();
            int neededSeats = next.getTicketCount();
    
            if (neededSeats <= seatsToFree) {
                List<BookingStatus> promotedList = new ArrayList<>();
                for (BookingStatus b : waitingQueue) {
                    if (b.getUser().equals(next.getUser())) {
                        b.setConfirmed(true);
                        promotedList.add(b);
                    }
                }
                bookingList.addAll(promotedList);
                waitingQueue.removeAll(promotedList);
                bookings.put(matchedSchedule, bookingList);
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
        String hashKey = HashUtil.generateUniqueKey(slot, screenNumber); // Generate unique key
        
        System.out.println(hashKey);
        // Check if any existing ShowSchedule has the same slot-screen combo
        return bookings.keySet().stream()
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
    
    
}
