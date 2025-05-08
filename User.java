import java.util.HashMap;
import java.util.Map;

public class User {
        // private String userId;
        private String name;
        private Map<BookingSlot, Show> bookedShows;

        public User(String name) {
            this.name = name;
            bookedShows=new HashMap<>();
        }
        
        public String getName() {
            return name;
        }
    
        public boolean canBookSlot(BookingSlot slot) {
            return !bookedShows.containsKey(slot);
        }
    
        public void addBookingSlot(BookingSlot slot, Show show) {
            bookedShows.put(slot, show);
        }
        
        public void removeBookingSlot(BookingSlot slot) {
            bookedShows.remove(slot);
        }
        
        public Show getShow(BookingSlot slot) {
            return bookedShows.get(slot);
        }
        
        @Override
        public String toString() {
            return "User{" +
                   "name='" + name + '\'' +
                   '}';
        }
}
