import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

public class TicketBookingSystemMain {

    public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		TicketBookingSystem bookingSystem = TicketBookingSystem.getInstance();
		
		while (true) {
			System.out.print("Enter command: ");
			String input = reader.readLine().trim();
			if (input.equalsIgnoreCase("exit")) break;
	
			String[] parts = input.split(": ");
			if (parts.length < 2) {
				System.out.println("Invalid command format.");
				continue;
			}
	
			String command = parts[0];
			String arguments = parts[1];
		
			switch (command) {
				case "registerShow":
					String[] cmd=arguments.split("->");
					bookingSystem.registerShow(cmd[0].trim(), cmd[1].trim());
					break;
				case "onboardShowSlots":
					bookingSystem.onboardShowSlots(arguments);
					break;
				case "showAvailByGenre":
					List<ShowSchedule> showsWithGenre= bookingSystem.getAvailableShowsByGenre(arguments);
					showsWithGenre.stream().forEach(System.out::println);
					break;
				case "bookTicket":
					String[] extTicketParam= parseBookingTicketArguments(arguments);
					//if no. of tickets is not mentioned it is 1 by default.
					//bookingSystem.bookTickets("UserB", "Kapil Sharma Show", "14:00", 10, 1.1);

					// if(extTicketParam.length==4)
					// {
						bookingSystem.bookTickets(extTicketParam[0], extTicketParam[1], extTicketParam[2], Integer.parseInt(extTicketParam[3]));
					// }
					// else{
					// 	bookingSystem.bookTickets(extTicketParam[0], extTicketParam[1], extTicketParam[2], Integer.parseInt(extTicketParam[3]), Integer.parseInt(extTicketParam[4]));
					// }
					break;
				case "cancelBookingId":
					UUID  bookingId= UUID.fromString(arguments);
					bookingSystem.cancelTicket(bookingId);
					break;
				default:
					System.out.println("Invalid command.");
				}
	}
	reader.close();
	}

	public static String[] parseBookingTicketArguments(String input) {
		return input.replace("(", "").replace(")", "").split(", ");
	}
	
}
	