import java.util.ArrayList;
import java.util.Scanner;

public class TheaterRoleUpdated {
    private static Scanner input = new Scanner(System.in);

    // Using ArrayLists so we can actually add/remove data
    private static ArrayList<String> movies = new ArrayList<>();
    private static ArrayList<ArrayList<String>> showtimes = new ArrayList<>();
    private static ArrayList<Double> ticketPrices = new ArrayList<>();
    private static ArrayList<String> promotions = new ArrayList<>();

    // Data structure to track tickets sold per showtime
    private static ArrayList<ArrayList<Integer>> ticketsSold = new ArrayList<>();

    public static void main(String[] args) {
        initializeAdminData();
        showAdminDashboard();
    }

    private static void initializeAdminData() {
        movies.add("Avengers: Endgame");
        ticketPrices.add(12.50);
        
        ArrayList<String> times = new ArrayList<>();
        times.add("10:00 AM");
        times.add("1:00 PM");
        showtimes.add(times);

        // Initializing sample sales data (50 tickets for 10am, 30 for 1pm)
        ArrayList<Integer> sales = new ArrayList<>();
        sales.add(50);
        sales.add(30);
        ticketsSold.add(sales);
    }

    public static void showAdminDashboard() {
        while (true) {
            System.out.println("\n===== THEATER MANAGEMENT PANEL =====");
            System.out.println("1. Add New Movie & Showtimes");
            System.out.println("2. Update/Reschedule Showtime");
            System.out.println("3. Cancel (Remove) Movie");
            System.out.println("4. Manage Promotion Deals");
            System.out.println("5. Record Ticket Sales");    // Added to give the report data
            System.out.println("6. View Daily Sales Report"); // Requested Feature
            System.out.println("7. Logout");
            System.out.print("Select Management Task: ");

            int choice = getIntInput();

            switch (choice) {
                case 1: addMovie(); break;
                case 2: rescheduleShowtime(); break;
                case 3: cancelMovie(); break;
                case 4: managePromotions(); break;
                case 5: recordTicketSales(); break;
                case 6: viewSalesReport(); break;
                case 7: 
                    System.out.println("Logout Successful");
                    return;
                default: 
                    System.out.println("Invalid choice.");
            }
        }
    }

    // 1. ADD NEW MOVIE
    private static void addMovie() {
        System.out.println("\n--- Add New Movie ---");
        System.out.print("Enter Movie Title: ");
        String title = input.nextLine();
        
        System.out.print("Enter Ticket Price: ");
        double price = Double.parseDouble(input.nextLine());

        ArrayList<String> newTimes = new ArrayList<>();
        ArrayList<Integer> newSales = new ArrayList<>(); // Initialize sales tracking
        
        while (true) {
            System.out.print("Enter Showtime (e.g. 2:30 PM) [Type 'done' to stop]: ");
            String time = input.nextLine();
            if (time.equalsIgnoreCase("done")) break;
            newTimes.add(time);
            newSales.add(0); // Initialize with 0 sales for new shows
        }

        movies.add(title);
        ticketPrices.add(price);
        showtimes.add(newTimes);
        ticketsSold.add(newSales);
        System.out.println("Movie successfully added to the system!");
    }

    // 2. RESCHEDULE SHOWTIME
    private static void rescheduleShowtime() {
        System.out.println("\n--- Reschedule Showtime ---");
        displayMovieList();
        System.out.print("Select Movie index: ");
        int mIdx = getIntInput() - 1;

        if (mIdx >= 0 && mIdx < movies.size()) {
            ArrayList<String> times = showtimes.get(mIdx);
            for (int i = 0; i < times.size(); i++) {
                System.out.println((i + 1) + ". " + times.get(i));
            }
            
            System.out.print("Select Showtime index: ");
            int tIdx = getIntInput() - 1;

            if (tIdx >= 0 && tIdx < times.size()) {
                System.out.print("Enter New Time: ");
                String newTime = input.nextLine();
                times.set(tIdx, newTime);
                System.out.println("Showtime rescheduled successfully!");
            }
        }
    }

    // 3. CANCEL/REMOVE MOVIE
    private static void cancelMovie() {
        System.out.println("\n--- Cancel Movie ---");
        displayMovieList();
        System.out.print("Select Movie index: ");
        int index = getIntInput() - 1;

        if (index >= 0 && index < movies.size()) {
            System.out.print("Confirm removal of " + movies.get(index) + "? (y/n): ");
            if (input.nextLine().equalsIgnoreCase("y")) {
                movies.remove(index);
                showtimes.remove(index);
                ticketPrices.remove(index);
                ticketsSold.remove(index); // Remove associated sales data
                System.out.println("Movie has been removed from schedule.");
            }
        }
    }

    // 4. PROMOTION DEALS
    private static void managePromotions() {
        System.out.println("\n--- Promotion Management ---");
        System.out.println("1. View Current Promotions");
        System.out.println("2. Add New Promotion");
        System.out.println("3. Clear All Promotions");
        System.out.print("Select Task: ");
        
        int pChoice = getIntInput();
        if (pChoice == 1) {
            System.out.println("Active Promos: " + (promotions.isEmpty() ? "None" : promotions));
        } else if (pChoice == 2) {
            System.out.print("Enter Promo Description: ");
            promotions.add(input.nextLine());
            System.out.println("Promo added.");
        } else if (pChoice == 3) {
            promotions.clear();
            System.out.println("Promotions cleared.");
        }
    }

    // METHOD TO RECORD SALES
    private static void recordTicketSales() {
        System.out.println("\n--- Record Ticket Sales ---");
        displayMovieList();
        System.out.print("Select Movie: ");
        int mIdx = getIntInput() - 1;

        if (mIdx >= 0 && mIdx < movies.size()) {
            ArrayList<String> times = showtimes.get(mIdx);
            for (int i = 0; i < times.size(); i++) {
                System.out.println((i + 1) + ". " + times.get(i));
            }
            System.out.print("Select Showtime: ");
            int tIdx = getIntInput() - 1;

            if (tIdx >= 0 && tIdx < times.size()) {
                System.out.print("Enter number of tickets sold for this session: ");
                int sold = getIntInput();
                if (sold >= 0) {
                    int current = ticketsSold.get(mIdx).get(tIdx);
                    ticketsSold.get(mIdx).set(tIdx, current + sold);
                    System.out.println("Sales updated!");
                }
            }
        }
    }

    // METHOD TO GENERATE THE REPORT
    private static void viewSalesReport() {
        int totalTickets = 0;
        double totalRevenue = 0;
        int totalShows = 0;

        System.out.println("\n===== DAILY SALES REPORT =====");
        System.out.printf("%-20s | %-10s | %-15s\n", "Movie", "Tickets", "Revenue");
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < movies.size(); i++) {
            String title = movies.get(i);
            double price = ticketPrices.get(i);
            ArrayList<Integer> movieSales = ticketsSold.get(i);
            int movieTotalTickets = 0;
            
            for (int sales : movieSales) {
                movieTotalTickets += sales;
                totalShows++;
            }
            
            double movieRevenue = movieTotalTickets * price;
            totalTickets += movieTotalTickets;
            totalRevenue += movieRevenue;

            System.out.printf("%-20s | %-10d | $%-14.2f\n", title, movieTotalTickets, movieRevenue);
        }

        // Calculation for average ticket per shows
        double average = (totalShows == 0) ? 0 : (double) totalTickets / totalShows;

        System.out.println("--------------------------------------------------");
        System.out.println("SUMMARY STATISTICS:");
        System.out.println("Total Tickets Sold:     " + totalTickets);
        System.out.printf("Total Revenue:          $%.2f\n", totalRevenue);
        System.out.println("Total Number of Shows:  " + totalShows);
        System.out.printf("Average Ticket Per Show: %.2f\n", average);
        System.out.println("==============================================");
    }

    // HELPERS
    private static void displayMovieList() {
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i));
        }
    }

    private static int getIntInput() {
        try {
            return Integer.parseInt(input.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}
