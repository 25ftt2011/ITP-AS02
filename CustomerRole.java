
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

public class CustomerRole {
    // Storage for user credentials
    private static Map<String, String> users = new HashMap<>();
    private static Map<String, ArrayList<String>> userBookings = new HashMap<>();
    private static String currentUser = null;
    private static Scanner input = new Scanner(System.in);
    
    // Showtimes data
    private static String[] movies = {
        "Avengers: Endgame",
        "Interstellar",
        "The Dark Knight",
        "Inception",
        "Titanic"
    };
    
    private static String[][] showtimes = {
        {"10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM"}, // Avengers
        {"11:00 AM", "2:00 PM", "5:00 PM", "8:00 PM"}, // Interstellar
        {"12:00 PM", "3:00 PM", "6:00 PM", "9:00 PM"}, // The Dark Knight
        {"10:30 AM", "1:30 PM", "4:30 PM", "7:30 PM"}, // Inception
        {"11:30 AM", "2:30 PM", "5:30 PM", "8:30 PM"}  // Titanic
    };
    
    private static double[] ticketPrices = {12.50, 14.00, 13.50, 12.00, 15.00};

    public static void main(String[] args) {
        // Add a sample user for testing
        users.put("admin", "admin123");
        userBookings.put("admin", new ArrayList<>());
        
        System.out.println("=== Grand Theatre Booking System ===");
        
        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                register();
                break;
            case 2:
                login();
                break;
            case 3:
                System.out.println("Thank you for using the Theatre Booking System!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void showUserMenu() {
        System.out.println("\n=== Welcome, " + currentUser + "! ===");
        System.out.println("1. View All Movies & Showtimes");
        System.out.println("2. Select Showtime & Book Ticket");
        System.out.println("3. View My Bookings");
        System.out.println("4. Cancel Booking");
        System.out.println("5. Logout");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                viewAllShowtimes();
                break;
            case 2:
                selectShowtime();
                break;
            case 3:
                viewMyBookings();
                break;
            case 4:
                cancelBooking();
                break;
            case 5:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void register() {
        System.out.println("\n--- Registration ---");
        System.out.print("Enter username: ");
        String username = input.nextLine().trim();
        
        if (users.containsKey(username)) {
            System.out.println("Username already exists! Please choose another username.");
            return;
        }
        
        System.out.print("Enter password: ");
        String password = input.nextLine();
        
        System.out.print("Confirm password: ");
        String confirmPassword = input.nextLine();
        
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match! Registration failed.");
            return;
        }
        
        if (password.length() < 4) {
            System.out.println("Password must be at least 4 characters long!");
            return;
        }
        
        users.put(username, password);
        userBookings.put(username, new ArrayList<>());
        System.out.println("Registration successful! You can now login.");
    }

    private static void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = input.nextLine().trim();
        
        System.out.print("Password: ");
        String password = input.nextLine();
        
        if (users.containsKey(username) && users.get(username).equals(password)) {
            currentUser = username;
            System.out.println("Login successful! Welcome back, " + username + "!");
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private static void logout() {
        System.out.println("Goodbye, " + currentUser + "!");
        currentUser = null;
    }
    
    private static void viewAllShowtimes() {
        System.out.println("\n========== ALL MOVIES & SHOWTIMES ==========");
        System.out.println("============================================");
        
        for (int i = 0; i < movies.length; i++) {
            System.out.println("\n[" + (i + 1) + "] " + movies[i].toUpperCase());
            System.out.println("    Price: $" + ticketPrices[i]);
            System.out.println("    Showtimes:");
            for (int j = 0; j < showtimes[i].length; j++) {
                System.out.println("      " + (j + 1) + ". " + showtimes[i][j]);
            }
            System.out.println("    ----------------------------------------");
        }
        
        System.out.println("\n============================================");
    }
    
    private static void selectShowtime() {
        System.out.println("\n--- SELECT SHOWTIME & BOOK TICKET ---");
        
        // Display movies
        System.out.println("\nAvailable Movies:");
        for (int i = 0; i < movies.length; i++) {
            System.out.println((i + 1) + ". " + movies[i] + " - $" + ticketPrices[i]);
        }
        
        System.out.print("\nSelect movie (1-" + movies.length + "): ");
        int movieChoice = getIntInput();
        
        if (movieChoice < 1 || movieChoice > movies.length) {
            System.out.println("Invalid movie selection!");
            return;
        }
        
        int movieIndex = movieChoice - 1;
        String selectedMovie = movies[movieIndex];
        
        // Display showtimes for selected movie
        System.out.println("\nShowtimes for " + selectedMovie + ":");
        for (int i = 0; i < showtimes[movieIndex].length; i++) {
            System.out.println((i + 1) + ". " + showtimes[movieIndex][i]);
        }
        
        System.out.print("\nSelect showtime (1-" + showtimes[movieIndex].length + "): ");
        int timeChoice = getIntInput();
        
        if (timeChoice < 1 || timeChoice > showtimes[movieIndex].length) {
            System.out.println("Invalid showtime selection!");
            return;
        }
        
        String selectedTime = showtimes[movieIndex][timeChoice - 1];
        double ticketPrice = ticketPrices[movieIndex];
        
        // Ask for number of tickets
        System.out.print("\nNumber of tickets: ");
        int numTickets = getIntInput();
        
        if (numTickets < 1) {
            System.out.println("Invalid number of tickets!");
            return;
        }
        
        double totalCost = ticketPrice * numTickets;
        
        // Confirm booking
        System.out.println("\n--- Booking Summary ---");
        System.out.println("Movie: " + selectedMovie);
        System.out.println("Showtime: " + selectedTime);
        System.out.println("Tickets: " + numTickets);
        System.out.println("Total Cost: $" + String.format("%.2f", totalCost));
        
        System.out.print("\nConfirm booking? (yes/no): ");
        String confirmation = input.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            // Create booking record
            String booking = selectedMovie + " | " + selectedTime + " | " + numTickets + " tickets | $" + String.format("%.2f", totalCost);
            userBookings.get(currentUser).add(booking);
            
            System.out.println("\n✓ Booking successful!");
            System.out.println("Booking ID: " + (userBookings.get(currentUser).size()));
            System.out.println("Enjoy the show, " + currentUser + "!");
        } else {
            System.out.println("Booking cancelled.");
        }
    }
    
    private static void viewMyBookings() {
        ArrayList<String> myBookings = userBookings.get(currentUser);
        
        if (myBookings.isEmpty()) {
            System.out.println("\nYou have no bookings yet.");
            System.out.println("Use option 2 to book tickets!");
            return;
        }
        
        System.out.println("\n========== MY BOOKINGS ==========");
        System.out.println("User: " + currentUser);
        System.out.println("=================================");
        
        for (int i = 0; i < myBookings.size(); i++) {
            System.out.println((i + 1) + ". " + myBookings.get(i));
        }
        
        System.out.println("=================================");
        System.out.println("Total Bookings: " + myBookings.size());
    }
    
    private static void cancelBooking() {
        ArrayList<String> myBookings = userBookings.get(currentUser);
        
        if (myBookings.isEmpty()) {
            System.out.println("\nYou have no bookings to cancel.");
            return;
        }
        
        System.out.println("\n--- CANCEL BOOKING ---");
        viewMyBookings();
        
        System.out.print("\nEnter booking number to cancel (1-" + myBookings.size() + "): ");
        int bookingNum = getIntInput();
        
        if (bookingNum < 1 || bookingNum > myBookings.size()) {
            System.out.println("Invalid booking number!");
            return;
        }
        
        String cancelledBooking = myBookings.get(bookingNum - 1);
        
        System.out.print("Are you sure you want to cancel this booking? (yes/no): ");
        String confirmation = input.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            myBookings.remove(bookingNum - 1);
            System.out.println("\n✓ Booking cancelled successfully!");
            System.out.println("Cancelled: " + cancelledBooking);
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    private static int getIntInput() {
        while (true) {
            try {
                int intInput = Integer.parseInt(input.nextLine());
                return intInput;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}