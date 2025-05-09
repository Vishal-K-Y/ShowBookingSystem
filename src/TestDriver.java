import pojo.ShowSchedule;
import service.TicketBookingSystem;

import java.util.List;
import java.util.UUID;

public class TestDriver {
    public static void main(String[] args) {
        TicketBookingSystem bookingSystem = TicketBookingSystem.getInstance();
        System.out.println("=== Starting Test Cases ===\n");

        // Test Case 1: Register Shows
        System.out.println("Test Case 1: Register Shows");
        System.out.println("------------------------");
        bookingSystem.registerShow("TMKOC", "Comedy");
        bookingSystem.registerShow("Breaking Bad", "Drama");
        bookingSystem.registerShow("Friends", "Comedy");
        System.out.println();

        // Test Case 2: Onboard Show Slots
        System.out.println("Test Case 2: Onboard Show Slots");
        System.out.println("-----------------------------");
        bookingSystem.onboardShowSlots("TMKOC 09:00-10:00 1");
        bookingSystem.onboardShowSlots("TMKOC 10:00-11:00 2");
        bookingSystem.onboardShowSlots("Breaking Bad 11:00-12:00 1");
        bookingSystem.onboardShowSlots("Friends 12:00-13:00 3");
        System.out.println();

        // Test Case 3: View Shows by Genre
        System.out.println("Test Case 3: View Shows by Genre");
        System.out.println("------------------------------");
        System.out.println("Comedy Shows:");
        List<ShowSchedule> comedyShows = bookingSystem.getAvailableShowsByGenre("Comedy");
        comedyShows.forEach(System.out::println);
        System.out.println("\nDrama Shows:");
        List<ShowSchedule> dramaShows = bookingSystem.getAvailableShowsByGenre("Drama");
        dramaShows.forEach(System.out::println);
        System.out.println();

        // Test Case 4: Book Tickets
        System.out.println("Test Case 4: Book Tickets");
        System.out.println("-----------------------");
        // Book for first user
        bookingSystem.bookTickets("John", "TMKOC", "09:00", 2);
        bookingSystem.bookTickets("John", "Breaking Bad", "11:00", 1);
        
        // Book for second user
        bookingSystem.bookTickets("Alice", "TMKOC", "09:00", 3);
        bookingSystem.bookTickets("Alice", "Friends", "12:00", 2);
        
        // Try to book same slot for first user (should fail)
        bookingSystem.bookTickets("John", "TMKOC", "09:00", 1);
        System.out.println();

        // Test Case 5: View Bookings
        System.out.println("Test Case 5: View Bookings");
        System.out.println("------------------------");
        System.out.println("TMKOC 09:00 Bookings:");
        bookingSystem.viewShowBookings("TMKOC", "09:00");
        System.out.println("\nBreaking Bad 11:00 Bookings:");
        bookingSystem.viewShowBookings("Breaking Bad", "11:00");
        System.out.println();

        // Test Case 6: Cancel Booking
        System.out.println("Test Case 6: Cancel Booking");
        System.out.println("-------------------------");
        // Get a booking ID from the view
        System.out.println("Viewing TMKOC bookings to get a booking ID:");
        bookingSystem.viewShowBookings("TMKOC", "09:00");
        
        // Cancel a booking (replace with actual booking ID from above)
        System.out.println("\nCancelling a booking:");
        bookingSystem.cancelTicket(UUID.fromString("b3dfc6ea-0593-4ca2-9a04-a7c37005b4d2"));
        
        // View bookings after cancellation
        System.out.println("\nViewing bookings after cancellation:");
        bookingSystem.viewShowBookings("TMKOC", "09:00");
        System.out.println();

        // Test Case 7: Waitlist Functionality
        System.out.println("Test Case 7: Waitlist Functionality");
        System.out.println("--------------------------------");
        // Try to book more tickets than available
        bookingSystem.bookTickets("Bob", "TMKOC", "09:00", 45);
        bookingSystem.bookTickets("Charlie", "TMKOC", "09:00", 5);
        
        // View bookings to see waitlist
        System.out.println("\nViewing bookings with waitlist:");
        bookingSystem.viewShowBookings("TMKOC", "09:00");
        System.out.println();

        // Test Case 8: Edge Cases
        System.out.println("Test Case 8: Edge Cases");
        System.out.println("---------------------");
        // Try to book non-existent show
        bookingSystem.bookTickets("John", "NonExistentShow", "09:00", 1);
        
        // Try to book non-existent time slot
        bookingSystem.bookTickets("John", "TMKOC", "15:00", 1);
        
        // Try to book with invalid time format
        bookingSystem.onboardShowSlots("TMKOC 9:00-10:00 4");
        System.out.println();

        System.out.println("=== Test Cases Completed ===");
    }
} 