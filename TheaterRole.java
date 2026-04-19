import java.util.ArrayList;
import java.util.Scanner;

public class TheaterRole {
    private static Scanner input = new Scanner(System.in);

    // Using ArrayLists so we can actually add/remove data
    private static ArrayList<String> movies = new ArrayList<>();
    private static ArrayList<ArrayList<String>> showtimes = new ArrayList<>();
    private static ArrayList<Double> ticketPrices = new ArrayList<>();
    private static ArrayList<String> promotions = new ArrayList<>();

    public static void main(String[] args) {
        // Sample data for testing the Admin functions
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
    }

    public static void showAdminDashboard() {
        while (true) {
            System.out.println("\n===== THEATER MANAGEMENT PANEL =====");
            System.out.println("1. Add New Movie & Showtimes");
            System.out.println("2. Update/Reschedule Showtime");
            System.out.println("3. Cancel (Remove) Movie");
            System.out.println("4. Manage Promotion Deals");
            System.out.println("5. Logout");
            System.out.print("Select Management Task: ");

            int choice = getIntInput();

            switch (choice) {
                case 1: addMovie(); break;
                case 2: rescheduleShowtime(); break;
                case 3: cancelMovie(); break;
                case 4: managePromotions(); break;
                case 5: 
                    System.out.println("Logging out from Admin Panel...");
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
        //System.out.println("Enter Showtimes (Enter 'done' when finished):");
        while (true) {
            System.out.print("Enter Showtime (e.g. 2:30 PM): ");
            String time = input.nextLine();
            if (time.equalsIgnoreCase("done")) break;
            newTimes.add(time);
        }

        movies.add(title);
        ticketPrices.add(price);
        showtimes.add(newTimes);
        System.out.println("Movie successfully added to the system!");
    }

    // 2. RESCHEDULE SHOWTIME
    private static void rescheduleShowtime() {
        System.out.println("\n--- Reschedule Showtime ---");
        displayMovieList();
        System.out.print("Select Movie to be updated: ");
        int mIdx = getIntInput() - 1;

        if (mIdx >= 0 && mIdx < movies.size()) {
            ArrayList<String> times = showtimes.get(mIdx);
            for (int i = 0; i < times.size(); i++) {
                System.out.println((i + 1) + ". " + times.get(i));
            }
            
            System.out.print("Select Showtime to be updated: ");
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
        System.out.print("Select Movie to be removed: ");
        int index = getIntInput() - 1;

        if (index >= 0 && index < movies.size()) {
            System.out.print("Confirm removal of " + movies.get(index) + "? (y/n): ");
            if (input.nextLine().equalsIgnoreCase("y")) {
                movies.remove(index);
                showtimes.remove(index);
                ticketPrices.remove(index);
                System.out.println("Movie hs been removed from schedule.");
            }
        }
    }

    // 4. PROMOTION DEALS
    private static void managePromotions() {
        System.out.println("\n--- Promotion Management ---");
        System.out.println("1. View Current Promotions");
        System.out.println("2. Add New Promotion");
        System.out.println("3. Clear All Promotions");
        
        int pChoice = getIntInput();
        if (pChoice == 1) {
            System.out.println("Active Promos: " + (promotions.isEmpty() ? "None" : promotions));
        } else if (pChoice == 2) {
            System.out.print("Enter Promo Description (e.g., '10% Off for Students'): ");
            promotions.add(input.nextLine());
            System.out.println("✓ Promo added.");
        } else if (pChoice == 3) {
            promotions.clear();
            System.out.println("✓ Promotions cleared.");
        }
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