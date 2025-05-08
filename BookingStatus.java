import java.util.UUID;

public class BookingStatus {
    private UUID bookingId;
    private boolean confirmed;
    private User user;
    private int ticketCount;

    public BookingStatus(UUID bookingId, boolean confirmed, User user, int ticketCount) {
        this.bookingId = bookingId;
        this.confirmed = confirmed;
        this.user=user;
        this.ticketCount=ticketCount;
    }


    public UUID getBookingId() {
        return bookingId;
    }

    public void setConfirmed(boolean confirmed){
        this.confirmed=confirmed;
    }
    public boolean isConfirmed() {
        return this.confirmed;
    }

    public User getUser() {
        return this.user;
    }
    
    public int getTicketCount() {
        return ticketCount;
    }
}