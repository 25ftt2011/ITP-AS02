import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

public class CustomerRoleUpdated {
    // Storage for user credentials
    private static Map<String, String> users = new HashMap<>();
    private static Map<String, ArrayList<String>> userBookings = new HashMap<>();
    private static Map<String, ArrayList<String>> userHistory = new HashMap<>();
    private static Map<String, ArrayList<String>> userWatchlist = new HashMap<>();
    private static String currentUser = null;
    private static Scanner input = new Scanner(System.in);
    
    // Movies data with categories and popularity
    private static String[] movies = {
        "Avengers: Endgame",
        "Interstellar",
        "The Dark Knight",
        "Inception",
        "Titanic"
    };
    
    private static String[] categories = {
        "Action",
        "Sci-Fi",
        "Action",
        "Thriller",
        "Romance"
    };
    
    private static double[] popularity = {   // rating out of 10
        9.5,
        8.8,
        9.0,
        8.5,
        7.9
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
        userHistory.put("admin", new ArrayList<>());
        userWatchlist.put("admin", new ArrayList<>());
        
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
        System.out.println("2. Search Movies (by title, category, popularity)");
        System.out.println("3. Select Showtime & Book Ticket");
        System.out.println("4. View My Active Bookings");
        System.out.println("5. Cancel Booking");
        System.out.println("6. View Booking History (all past bookings)");
        System.out.println("7. Manage Watchlist");
        System.out.println("8. Logout");
        System.out.print("Choose an option: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                viewAllShowtimes();
                break;
            case 2:
                searchMovies();
                break;
            case 3:
                selectShowtime();
                break;
            case 4:
                viewMyBookings();
                break;
            case 5:
                cancelBooking();
                break;
            case 6:
                viewBookingHistory();
                break;
            case 7:
                manageWatchlist();
                break;
            case 8:
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
        userHistory.put(username, new ArrayList<>());
        userWatchlist.put(username, new ArrayList<>());
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
            System.out.println("    Category: " + categories[i]);
            System.out.println("    Popularity: " + popularity[i] + "/10");
            System.out.println("    Price: $" + ticketPrices[i]);
            System.out.println("    Showtimes:");
            for (int j = 0; j < showtimes[i].length; j++) {
                System.out.println("      " + (j + 1) + ". " + showtimes[i][j]);
            }
            System.out.println("    ----------------------------------------");
        }
        
        System.out.println("\n============================================");
    }
    
    // ================== SEARCH MOVIES ==================
    private static void searchMovies() {
        System.out.println("\n--- SEARCH MOVIES ---");
        System.out.println("Search by:");
        System.out.println("1. Title (keyword)");
        System.out.println("2. Category");
        System.out.println("3. Minimum Popularity (0-10)");
        System.out.print("Choose search type: ");
        
        int searchType = getIntInput();
        boolean found = false;
        
        switch (searchType) {
            case 1:
                System.out.print("Enter title keyword: ");
                String keyword = input.nextLine().trim().toLowerCase();
                System.out.println("\n--- Movies matching '" + keyword + "' ---");
                for (int i = 0; i < movies.length; i++) {
                    if (movies[i].toLowerCase().contains(keyword)) {
                        printMovieInfo(i);
                        found = true;
                    }
                }
                break;
            case 2:
                System.out.print("Enter category (Action, Sci-Fi, Thriller, Romance, etc.): ");
                String cat = input.nextLine().trim();
                System.out.println("\n--- Movies in category '" + cat + "' ---");
                for (int i = 0; i < categories.length; i++) {
                    if (categories[i].equalsIgnoreCase(cat)) {
                        printMovieInfo(i);
                        found = true;
                    }
                }
                break;
            case 3:
                System.out.print("Enter minimum popularity (0-10): ");
                double minPop = getDoubleInput();
                System.out.println("\n--- Movies with popularity >= " + minPop + " ---");
                for (int i = 0; i < popularity.length; i++) {
                    if (popularity[i] >= minPop) {
                        printMovieInfo(i);
                        found = true;
                    }
                }
                break;
            default:
                System.out.println("Invalid search type.");
                return;
        }
        
        if (!found) {
            System.out.println("No movies found matching your criteria.");
        }
    }
    
    private static void printMovieInfo(int index) {
        System.out.println("\n[" + (index+1) + "] " + movies[index]);
        System.out.println("    Category: " + categories[index]);
        System.out.println("    Popularity: " + popularity[index] + "/10");
        System.out.println("    Price: $" + ticketPrices[index]);
        System.out.println("    Showtimes: " + String.join(", ", showtimes[index]));
    }
    // ================== END SEARCH ==================
    
    // ================== SELECT SHOWTIME WITH PAYMENT ==================
    private static void selectShowtime() {
        System.out.println("\n--- SELECT SHOWTIME & BOOK TICKET ---");
        
        System.out.println("\nAvailable Movies:");
        for (int i = 0; i < movies.length; i++) {
            System.out.println((i + 1) + ". " + movies[i] + " - $" + ticketPrices[i] + " (" + categories[i] + ", " + popularity[i] + "/10)");
        }
        
        System.out.print("\nSelect movie (1-" + movies.length + "): ");
        int movieChoice = getIntInput();
        
        if (movieChoice < 1 || movieChoice > movies.length) {
            System.out.println("Invalid movie selection!");
            return;
        }
        
        int movieIndex = movieChoice - 1;
        String selectedMovie = movies[movieIndex];
        
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
        
        System.out.print("\nNumber of tickets: ");
        int numTickets = getIntInput();
        
        if (numTickets < 1) {
            System.out.println("Invalid number of tickets!");
            return;
        }
        
        double totalCost = ticketPrice * numTickets;
        
        System.out.println("\n--- Booking Summary ---");
        System.out.println("Movie: " + selectedMovie);
        System.out.println("Showtime: " + selectedTime);
        System.out.println("Tickets: " + numTickets);
        System.out.println("Total Cost: $" + String.format("%.2f", totalCost));
        
        // ---------- PAYMENT STEP ----------
        boolean paymentSuccess = processPayment(totalCost);
        if (!paymentSuccess) {
            System.out.println("Payment failed. Booking cancelled.");
            return;
        }
        // ---------- END PAYMENT ----------
        
        // After successful payment, ask for final confirmation
        System.out.print("\nConfirm booking? (yes/no): ");
        String confirmation = input.nextLine().trim().toLowerCase();
        
        if (confirmation.equals("yes") || confirmation.equals("y")) {
            String booking = selectedMovie + " | " + selectedTime + " | " + numTickets + " tickets | $" + String.format("%.2f", totalCost);
            userBookings.get(currentUser).add(booking);
            userHistory.get(currentUser).add("[BOOKED] " + booking);
            System.out.println("\n✓ Booking successful!");
            System.out.println("Booking ID: " + (userBookings.get(currentUser).size()));
            System.out.println("Enjoy the show, " + currentUser + "!");
        } else {
            System.out.println("Booking cancelled after payment. Your payment will be refunded (simulated).");
        }
    }
    
    // Dummy payment processor
    private static boolean processPayment(double amount) {
        System.out.println("\n--- PAYMENT ---");
        System.out.println("Total to pay: $" + String.format("%.2f", amount));
        System.out.println("Please enter your payment details (simulated)");
        
        System.out.print("Card Number (16 digits): ");
        String cardNum = input.nextLine().trim();
        if (!cardNum.matches("\\d{16}")) {
            System.out.println("Invalid card number. Must be 16 digits.");
            return false;
        }
        
        System.out.print("Expiry (MM/YY): ");
        String expiry = input.nextLine().trim();
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            System.out.println("Invalid expiry format. Use MM/YY.");
            return false;
        }
        
        System.out.print("CVV (3 digits): ");
        String cvv = input.nextLine().trim();
        if (!cvv.matches("\\d{3}")) {
            System.out.println("Invalid CVV. Must be 3 digits.");
            return false;
        }
        
        // Simulate processing
        System.out.print("Processing payment");
        for (int i = 0; i < 3; i++) {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            System.out.print(".");
        }
        System.out.println();
        
        System.out.println("✓ Payment approved!");
        return true;
    }
    // ================== END PAYMENT ==================
    
    private static void viewMyBookings() {
        ArrayList<String> myBookings = userBookings.get(currentUser);
        
        if (myBookings.isEmpty()) {
            System.out.println("\nYou have no active bookings.");
            System.out.println("Use option 3 to book tickets!");
            return;
        }
        
        System.out.println("\n========== MY ACTIVE BOOKINGS ==========");
        System.out.println("User: " + currentUser);
        System.out.println("========================================");
        
        for (int i = 0; i < myBookings.size(); i++) {
            System.out.println((i + 1) + ". " + myBookings.get(i));
        }
        
        System.out.println("========================================");
        System.out.println("Total Active Bookings: " + myBookings.size());
    }
    
    private static void cancelBooking() {
        ArrayList<String> myBookings = userBookings.get(currentUser);
        
        if (myBookings.isEmpty()) {
            System.out.println("\nYou have no active bookings to cancel.");
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
            userHistory.get(currentUser).add("[CANCELLED] " + cancelledBooking);
            myBookings.remove(bookingNum - 1);
            System.out.println("\n✓ Booking cancelled successfully!");
            System.out.println("Cancelled: " + cancelledBooking);
        } else {
            System.out.println("Cancellation aborted.");
        }
    }
    
    private static void viewBookingHistory() {
        ArrayList<String> history = userHistory.get(currentUser);
        
        if (history.isEmpty()) {
            System.out.println("\nNo booking history found.");
            return;
        }
        
        System.out.println("\n========== COMPLETE BOOKING HISTORY ==========");
        System.out.println("User: " + currentUser);
        System.out.println("==============================================");
        
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
        
        System.out.println("==============================================");
        System.out.println("Total entries: " + history.size());
    }
    
    // ================== WATCHLIST ==================
    private static void manageWatchlist() {
        System.out.println("\n--- WATCHLIST MANAGEMENT ---");
        System.out.println("1. View my watchlist");
        System.out.println("2. Add movie to watchlist");
        System.out.println("3. Remove movie from watchlist");
        System.out.println("4. Back to main menu");
        System.out.print("Choose: ");
        
        int choice = getIntInput();
        
        switch (choice) {
            case 1:
                viewWatchlist();
                break;
            case 2:
                addToWatchlist();
                break;
            case 3:
                removeFromWatchlist();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid choice.");
        }
    }
    
    private static void viewWatchlist() {
        ArrayList<String> watchlist = userWatchlist.get(currentUser);
        
        if (watchlist.isEmpty()) {
            System.out.println("\nYour watchlist is empty.");
            System.out.println("Use 'Add to watchlist' to save movies you want to see later.");
            return;
        }
        
        System.out.println("\n========== MY WATCHLIST ==========");
        for (int i = 0; i < watchlist.size(); i++) {
            System.out.println((i + 1) + ". " + watchlist.get(i));
        }
        System.out.println("===================================");
    }
    
    private static void addToWatchlist() {
        System.out.println("\n--- Add Movie to Watchlist ---");
        System.out.println("Available movies:");
        for (int i = 0; i < movies.length; i++) {
            System.out.println((i + 1) + ". " + movies[i] + " (" + categories[i] + ", " + popularity[i] + "/10)");
        }
        
        System.out.print("Select movie number (1-" + movies.length + "): ");
        int movieNum = getIntInput();
        
        if (movieNum < 1 || movieNum > movies.length) {
            System.out.println("Invalid selection.");
            return;
        }
        
        String selectedMovie = movies[movieNum - 1];
        ArrayList<String> watchlist = userWatchlist.get(currentUser);
        
        if (watchlist.contains(selectedMovie)) {
            System.out.println(selectedMovie + " is already in your watchlist.");
        } else {
            watchlist.add(selectedMovie);
            System.out.println("✓ Added \"" + selectedMovie + "\" to your watchlist.");
        }
    }
    
    private static void removeFromWatchlist() {
        ArrayList<String> watchlist = userWatchlist.get(currentUser);
        
        if (watchlist.isEmpty()) {
            System.out.println("\nYour watchlist is empty. Nothing to remove.");
            return;
        }
        
        viewWatchlist();
        System.out.print("\nEnter the number of the movie to remove: ");
        int index = getIntInput();
        
        if (index < 1 || index > watchlist.size()) {
            System.out.println("Invalid number.");
            return;
        }
        
        String removed = watchlist.remove(index - 1);
        System.out.println("✓ Removed \"" + removed + "\" from your watchlist.");
    }
    // ================== END WATCHLIST ==================
    
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
    
    private static double getDoubleInput() {
        while (true) {
            try {
                double d = Double.parseDouble(input.nextLine());
                return d;
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number (e.g., 8.5): ");
            }
        }
    }
}
