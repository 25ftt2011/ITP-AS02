import java.util.*;
import java.io.*;
import org.json.*;

public class TheaterRoleNewest {

    private static Scanner input = new Scanner(System.in);
    private static final String FILE_NAME = "theater_data.json";

    // ===== CLEAN OOP STRUCTURE =====
    static class Movie {
        String title;
        double price;
        ArrayList<String> showtimes;
        ArrayList<Integer> ticketsSold;

        public Movie(String title, double price,
                     ArrayList<String> showtimes,
                     ArrayList<Integer> ticketsSold) {
            this.title = title;
            this.price = price;
            this.showtimes = showtimes;
            this.ticketsSold = ticketsSold;
        }
    }

    private static ArrayList<Movie> movies = new ArrayList<>();
    private static ArrayList<String> promotions = new ArrayList<>();

    public static void main(String[] args) {
        loadFromJSON();
        showAdminDashboard();
    }

    // ================= MENU =================
    public static void showAdminDashboard() {
        while (true) {
            System.out.println("\n===== THEATER MANAGEMENT PANEL =====");
            System.out.println("1. Add Movie and Showtimes");
            System.out.println("2. Update/Reschedule Showtime");
            System.out.println("3. Remove/Cancel Movie");
            System.out.println("4. Manage Promotions Deals");
            System.out.println("5. Record Ticket Sales");
            System.out.println("6. View Sales Report");
            System.out.println("7. Save & Exit");
            System.out.print("Select Task: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> addMovie();
                case 2 -> rescheduleShowtime();
                case 3 -> cancelMovie();
                case 4 -> managePromotions();
                case 5 -> recordTicketSales();
                case 6 -> viewSalesReport();
                case 7 -> {
                    saveToJSON();
                    System.out.println("Saved successfully. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= ADD MOVIE =================
    private static void addMovie() {
        System.out.print("Movie Title: ");
        String title = input.nextLine();

        System.out.print("Ticket Price: ");
        double price = Double.parseDouble(input.nextLine());

        ArrayList<String> times = new ArrayList<>();
        ArrayList<Integer> sales = new ArrayList<>();

        while (true) {
            System.out.print("Showtime (type 'done'): ");
            String t = input.nextLine();
            if (t.equalsIgnoreCase("done")) break;
            times.add(t);
            sales.add(0);
        }

        movies.add(new Movie(title, price, times, sales));
        System.out.println("Movie added successfully.");
    }

    // ================= RESCHEDULE =================
    private static void rescheduleShowtime() {
        displayMovies();
        System.out.print("Select movie: ");
        int m = getIntInput() - 1;

        if (validMovie(m)) {
            Movie movie = movies.get(m);

            for (int i = 0; i < movie.showtimes.size(); i++) {
                System.out.println((i + 1) + ". " + movie.showtimes.get(i));
            }

            System.out.print("Select showtime: ");
            int s = getIntInput() - 1;

            if (s >= 0 && s < movie.showtimes.size()) {
                System.out.print("New time: ");
                movie.showtimes.set(s, input.nextLine());
                System.out.println("Updated successfully.");
            }
        }
    }

    // ================= CANCEL MOVIE =================
    private static void cancelMovie() {
        displayMovies();
        System.out.print("Select movie: ");
        int i = getIntInput() - 1;

        if (validMovie(i)) {
            movies.remove(i);
            System.out.println("Movie removed.");
        }
    }

    // ================= PROMOTIONS =================
    private static void managePromotions() {
        System.out.println("1.View 2.Add 3.Clear");
        int c = getIntInput();

        if (c == 1) System.out.println(promotions);
        else if (c == 2) {
            System.out.print("Enter promo: ");
            promotions.add(input.nextLine());
        } else if (c == 3) {
            promotions.clear();
        }
    }

    // ================= SALES =================
    private static void recordTicketSales() {
        displayMovies();
        System.out.print("Select movie: ");
        int m = getIntInput() - 1;

        if (validMovie(m)) {
            Movie movie = movies.get(m);

            for (int i = 0; i < movie.showtimes.size(); i++) {
                System.out.println((i + 1) + ". " + movie.showtimes.get(i));
            }

            System.out.print("Select showtime: ");
            int s = getIntInput() - 1;

            System.out.print("Tickets sold: ");
            int sold = getIntInput();

            if (s >= 0 && s < movie.ticketsSold.size()) {
                movie.ticketsSold.set(s, movie.ticketsSold.get(s) + sold);
                System.out.println("Sales updated.");
            }
        }
    }

    // ================= REPORT =================
    private static void viewSalesReport() {

        int totalTickets = 0;
        double totalRevenue = 0;

        System.out.println("\n===== SALES REPORT =====");

        for (Movie m : movies) {

            int movieTickets = 0;

            for (int s : m.ticketsSold) {
                movieTickets += s;
            }

            double revenue = movieTickets * m.price;

            totalTickets += movieTickets;
            totalRevenue += revenue;

            System.out.println(m.title +
                    " | Tickets: " + movieTickets +
                    " | Revenue: $" + revenue);
        }

        System.out.println("\nTOTAL TICKETS: " + totalTickets);
        System.out.println("TOTAL REVENUE: $" + totalRevenue);
    }

    // ================= SAVE JSON =================
    private static void saveToJSON() {
        try {
            JSONObject root = new JSONObject();

            JSONArray movieArr = new JSONArray();

            for (Movie m : movies) {
                JSONObject obj = new JSONObject();
                obj.put("title", m.title);
                obj.put("price", m.price);
                obj.put("showtimes", m.showtimes);
                obj.put("ticketsSold", m.ticketsSold);
                movieArr.put(obj);
            }

            root.put("movies", movieArr);
            root.put("promotions", promotions);

            FileWriter file = new FileWriter(FILE_NAME);
            file.write(root.toString(2));
            file.close();

        } catch (Exception e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    // ================= LOAD JSON =================
    private static void loadFromJSON() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            JSONObject root = new JSONObject(content);

            movies = new ArrayList<>();
            promotions = new ArrayList<>();

            JSONArray movieArr = root.getJSONArray("movies");

            for (int i = 0; i < movieArr.length(); i++) {

                JSONObject obj = movieArr.getJSONObject(i);

                String title = obj.getString("title");
                double price = obj.getDouble("price");

                ArrayList<String> times = new ArrayList<>();
                JSONArray st = obj.getJSONArray("showtimes");
                for (int j = 0; j < st.length(); j++) {
                    times.add(st.getString(j));
                }

                ArrayList<Integer> sales = new ArrayList<>();
                JSONArray ts = obj.getJSONArray("ticketsSold");
                for (int j = 0; j < ts.length(); j++) {
                    sales.add(ts.getInt(j));
                }

                movies.add(new Movie(title, price, times, sales));
            }

            JSONArray promoArr = root.getJSONArray("promotions");
            for (int i = 0; i < promoArr.length(); i++) {
                promotions.add(promoArr.getString(i));
            }

        } catch (Exception e) {
            System.out.println("Load error: " + e.getMessage());
        }
    }

    // ================= HELPERS =================
    private static void displayMovies() {
        for (int i = 0; i < movies.size(); i++) {
            System.out.println((i + 1) + ". " + movies.get(i).title);
        }
    }

    private static boolean validMovie(int i) {
        return i >= 0 && i < movies.size();
    }

    private static int getIntInput() {
        try {
            return Integer.parseInt(input.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }
}