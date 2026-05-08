import java.util.*;
import java.text.*;
import java.util.regex.*;

// =============================================================================
//  AdminRole.java
//  Combines: Login, Customer Management, System Announcements,
//            Ticket & Seat Management, System Log
// =============================================================================


// ─────────────────────────────────────────────────────────────────────────────
// DATA MODELS
// ─────────────────────────────────────────────────────────────────────────────

class Announcement {
    private String title;
    private String message;
    private Date   startDate;
    private Date   endDate;

    public Announcement(String title, String message, Date startDate, Date endDate) {
        this.title     = title;
        this.message   = message;
        this.startDate = startDate;
        this.endDate   = endDate;
    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "\n--- Announcement ---" +
               "\nTitle   : " + title +
               "\nMessage : " + message +
               "\nStart   : " + sdf.format(startDate) +
               "\nEnd     : " + sdf.format(endDate);
    }
}

// ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ──

class Showtime {
    private int    showtimeId;
    private String movieTitle;
    private Date   dateTime;
    private String hall;

    public Showtime(int showtimeId, String movieTitle, Date dateTime, String hall) {
        this.showtimeId = showtimeId;
        this.movieTitle = movieTitle;
        this.dateTime   = dateTime;
        this.hall       = hall;
    }

    public int    getShowtimeId() { return showtimeId; }
    public String getMovieTitle() { return movieTitle; }
    public Date   getDateTime()   { return dateTime; }
    public String getHall()       { return hall; }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return String.format("[Showtime #%d] %-25s | %s | Hall: %s",
                showtimeId, movieTitle, sdf.format(dateTime), hall);
    }
}

// ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ──

class Ticket {
    private static int idCounter = 1;

    private int     ticketId;
    private int     showtimeId;
    private String  customerId;
    private String  seatNumber;
    private double  price;
    private boolean voided;

    public Ticket(int showtimeId, String customerId, String seatNumber, double price) {
        this.ticketId   = idCounter++;
        this.showtimeId = showtimeId;
        this.customerId = customerId;
        this.seatNumber = seatNumber;
        this.price      = price;
        this.voided     = false;
    }

    public int     getTicketId()          { return ticketId; }
    public int     getShowtimeId()        { return showtimeId; }
    public String  getCustomerId()        { return customerId; }
    public String  getSeatNumber()        { return seatNumber; }
    public double  getPrice()             { return price; }
    public boolean isVoided()             { return voided; }
    public void    setSeatNumber(String s){ this.seatNumber = s; }
    public void    voidTicket()           { this.voided = true; }

    public String toString() {
        return String.format("[Ticket #%d] Showtime #%d | Customer: %-10s | Seat: %-4s | Price: $%.2f | %s",
                ticketId, showtimeId, customerId, seatNumber, price,
                voided ? "VOIDED" : "ACTIVE");
    }
}

// ── ── ── ── ── ── ── ── ── ── ── ── ── ── ── ──

class LogEntry {
    private static int idCounter = 1;

    private int    entryId;
    private Date   timestamp;
    private String userId;
    private String action;

    public LogEntry(String userId, String action) {
        this.entryId   = idCounter++;
        this.timestamp = new Date();
        this.userId    = userId;
        this.action    = action;
    }

    // Constructor with explicit timestamp — used for seeding
    LogEntry(String userId, String action, Date timestamp) {
        this.entryId   = idCounter++;
        this.timestamp = timestamp;
        this.userId    = userId;
        this.action    = action;
    }

    public Date   getTimestamp() { return timestamp; }
    public String getUserId()    { return userId; }
    public String getAction()    { return action; }
    public int    getEntryId()   { return entryId; }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return String.format("[#%03d] %s | User: %-10s | %s",
                entryId, sdf.format(timestamp), userId, action);
    }
}


// =============================================================================
//  AdminRole  — entry point
// =============================================================================
public class AdminRole {

    static final Scanner sc = new Scanner(System.in);

    // ── Admin credentials (hardcoded; new accounts can be added here) ──
    static final Map<String, String> ADMIN_CREDENTIALS = new HashMap<>();
    static {
        ADMIN_CREDENTIALS.put("admin",  "admin123");
        ADMIN_CREDENTIALS.put("sadmin", "super456");
    }

    static String currentAdmin = null; // set after successful login

    // ── Shared data ──────────────────────────────────────────────────────────
    // Customer Management
    static Map<String, String>              customers     = new HashMap<>();
    static Map<String, String>              customerEmails= new HashMap<>();
    static Map<String, ArrayList<String>>   customerBookings = new HashMap<>();

    // Announcements
    static ArrayList<Announcement>          announcements = new ArrayList<>();
    static final SimpleDateFormat           ANN_SDF;
    static {
        ANN_SDF = new SimpleDateFormat("dd/MM/yyyy");
        ANN_SDF.setLenient(false);
    }

    // Ticket & Seat Management
    static ArrayList<Showtime>              showtimes     = new ArrayList<>();
    static ArrayList<Ticket>               tickets       = new ArrayList<>();
    static Map<String, Double>             basePriceRules= new HashMap<>();
    static final SimpleDateFormat           ST_SDF;
    static final Pattern                    SEAT_PATTERN  = Pattern.compile("^[A-Z]\\d{1,2}$");
    static {
        ST_SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        ST_SDF.setLenient(false);
    }

    // System Log
    static final ArrayList<LogEntry>        LOG           = new ArrayList<>();
    static final SimpleDateFormat           LOG_SDF;
    static final Pattern                    USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    static {
        LOG_SDF = new SimpleDateFormat("dd/MM/yyyy");
        LOG_SDF.setLenient(false);
    }

    // Validation patterns (Customer Management)
    static final String NAME_PATTERN  = "^[a-zA-Z0-9 ]+$";
    static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";


    // =========================================================================
    //  MAIN
    // =========================================================================
    public static void main(String[] args) {
        seedAll();
        System.out.println("=== Grand Theatre Admin System ===");

        while (true) {
            if (currentAdmin == null) {
                showLoginMenu();
            } else {
                showAdminMenu();
            }
        }
    }


    // =========================================================================
    //  LOGIN / LOGOUT
    // =========================================================================

    static void showLoginMenu() {
        System.out.println("\n--- Admin Login ---");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Choice: ");

        int choice = readInt();
        switch (choice) {
            case 1: login();  break;
            case 2:
                System.out.println("Goodbye.");
                System.exit(0);
            default:
                System.out.println("Invalid option.");
        }
    }

    static void login() {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine();

        if (ADMIN_CREDENTIALS.containsKey(username) &&
            ADMIN_CREDENTIALS.get(username).equals(password)) {
            currentAdmin = username;
            log(currentAdmin, "Admin login");
            System.out.println("Login successful! Welcome, " + currentAdmin + ".");
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    static void logout() {
        log(currentAdmin, "Admin logout");
        System.out.println("Goodbye, " + currentAdmin + "!");
        currentAdmin = null;
    }


    // =========================================================================
    //  MAIN ADMIN MENU  — shown after login
    // =========================================================================

    static void showAdminMenu() {
        System.out.println("\n=== Admin Menu — Logged in as: " + currentAdmin + " ===");
        System.out.println("1. Customer Management");
        System.out.println("2. System Announcements");
        System.out.println("3. Ticket & Seat Management");
        System.out.println("4. System Log");
        System.out.println("0. Logout");
        System.out.print("Choice: ");

        int choice = readInt();
        switch (choice) {
            case 1: customerManagementMenu(); break;
            case 2: announcementsMenu();      break;
            case 3: ticketSeatMenu();         break;
            case 4: systemLogMenu();          break;
            case 0: logout();                 break;
            default: System.out.println("Invalid option.");
        }
    }


    // =========================================================================
    //  FEATURE 1 — CUSTOMER MANAGEMENT
    // =========================================================================

    static void customerManagementMenu() {
        int choice;
        do {
            System.out.println("\n--- Admin: Customer Management ---");
            System.out.println("1. Search Customer (by Name / Email / ID)");
            System.out.println("2. Update Customer Info");
            System.out.println("3. Delete Customer");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            choice = readInt();
            switch (choice) {
                case 1: searchCustomer(); break;
                case 2: updateCustomer(); break;
                case 3: deleteCustomer(); break;
                case 0: break;
                default: System.out.println("Invalid option.");
            }
        } while (choice != 0);
    }

    // Search by name / email / ID — query must not be empty
    static void searchCustomer() {
        System.out.print("Enter search query (ID, Name, or Email): ");
        String query = sc.nextLine().trim();

        if (query.isEmpty()) {
            System.out.println("Invalid search: Query cannot be empty.");
            return;
        }

        boolean found = false;
        for (String id : customers.keySet()) {
            String email = customerEmails.getOrDefault(id, "N/A");
            if (id.contains(query) || email.contains(query)) {
                System.out.println("ID: " + id +
                                   " | Email: " + email +
                                   " | Bookings: " + customerBookings.getOrDefault(id, new ArrayList<>()).size());
                found = true;
            }
        }
        if (!found) System.out.println("No customer found matching: " + query);
    }

    // Update name (no special chars) and email (valid format)
    static void updateCustomer() {
        System.out.print("Enter Customer ID to update: ");
        String id = sc.nextLine().trim();

        if (!customers.containsKey(id)) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("Enter new Name: ");
        String newName = sc.nextLine();
        if (!Pattern.matches(NAME_PATTERN, newName)) {
            System.out.println("Error: Name contains invalid special characters.");
            return;
        }

        System.out.print("Enter new Email: ");
        String newEmail = sc.nextLine();
        if (!Pattern.matches(EMAIL_PATTERN, newEmail)) {
            System.out.println("Error: Invalid email format.");
            return;
        }

        customerEmails.put(id, newEmail);
        log(currentAdmin, "Updated customer info: " + id);
        System.out.println("Customer info updated successfully.");
    }

    // Delete — blocked if customer has active bookings
    static void deleteCustomer() {
        System.out.print("Enter Customer ID to delete: ");
        String id = sc.nextLine().trim();

        if (!customers.containsKey(id)) {
            System.out.println("Customer not found.");
            return;
        }

        ArrayList<String> bookings = customerBookings.get(id);
        if (bookings != null && !bookings.isEmpty()) {
            System.out.println("Error: Cannot delete customer. Active bookings found (" + bookings.size() + ").");
        } else {
            customers.remove(id);
            customerEmails.remove(id);
            customerBookings.remove(id);
            log(currentAdmin, "Deleted customer: " + id);
            System.out.println("Customer deleted successfully.");
        }
    }


    // =========================================================================
    //  FEATURE 2 — SYSTEM ANNOUNCEMENTS
    // =========================================================================

    static void announcementsMenu() {
        int choice;
        do {
            System.out.println("\n--- Admin: System Announcements ---");
            System.out.println("1. Create Announcement");
            System.out.println("2. View Announcements");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            choice = readInt();
            switch (choice) {
                case 1: createAnnouncement(); break;
                case 2: viewAnnouncements();  break;
                case 0: break;
                default: System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    static void createAnnouncement() {
        String title, message;
        Date startDate = null, endDate = null;

        // Title: non-empty, max 100 chars
        while (true) {
            System.out.print("Enter Title (max 100 chars): ");
            title = sc.nextLine();
            if (title.isEmpty())        { System.out.println("Title cannot be empty!"); continue; }
            if (title.length() > 100)   { System.out.println("Title exceeds 100 characters!"); continue; }
            break;
        }

        // Message: non-empty, max 1500 chars
        while (true) {
            System.out.print("Enter Message (max 1500 chars): ");
            message = sc.nextLine();
            if (message.isEmpty())      { System.out.println("Message cannot be empty!"); continue; }
            if (message.length() > 1500){ System.out.println("Message exceeds 1500 characters!"); continue; }
            break;
        }

        // Dates: valid format, start <= end
        while (true) {
            try {
                System.out.print("Enter Start Date (dd/MM/yyyy): ");
                startDate = ANN_SDF.parse(sc.nextLine());
                System.out.print("Enter End Date   (dd/MM/yyyy): ");
                endDate   = ANN_SDF.parse(sc.nextLine());
                if (startDate.after(endDate)) {
                    System.out.println("Start date must be BEFORE or EQUAL to end date!");
                } else {
                    break;
                }
            } catch (ParseException e) {
                System.out.println("Invalid date format! Use dd/MM/yyyy");
            }
        }

        announcements.add(new Announcement(title, message, startDate, endDate));
        log(currentAdmin, "Created announcement: \"" + title + "\"");
        System.out.println("Announcement created successfully!");
    }

    static void viewAnnouncements() {
        if (announcements.isEmpty()) {
            System.out.println("No announcements available.");
            return;
        }
        for (Announcement a : announcements) System.out.println(a);
    }


    // =========================================================================
    //  FEATURE 3 — TICKET & SEAT MANAGEMENT
    // =========================================================================

    static void ticketSeatMenu() {
        int choice;
        do {
            System.out.println("\n--- Admin: Ticket & Seat Management ---");
            System.out.println("1. View All Booked Tickets");
            System.out.println("2. View Tickets by Showtime");
            System.out.println("3. View Seat Availability for Showtime");
            System.out.println("4. Void Ticket (Refund / Free Seat)");
            System.out.println("5. Update Ticket Seat");
            System.out.println("6. Set Base Ticket Price Rule");
            System.out.println("7. View Base Price Rules");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            choice = readInt();
            switch (choice) {
                case 1: viewAllTickets();        break;
                case 2: viewTicketsByShowtime(); break;
                case 3: viewSeatAvailability();  break;
                case 4: voidTicket();            break;
                case 5: updateTicketSeat();      break;
                case 6: setBasePriceRule();      break;
                case 7: viewBasePriceRules();    break;
                case 0: break;
                default: System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    // 1. View all booked tickets (global view)
    static void viewAllTickets() {
        if (tickets.isEmpty()) { System.out.println("No tickets found."); return; }
        System.out.println("\n--- All Booked Tickets ---");
        for (Ticket t : tickets) System.out.println(t);
        System.out.println("Total: " + tickets.size() + " ticket(s).");
    }

    // 2. View tickets filtered to one showtime
    static void viewTicketsByShowtime() {
        listShowtimes();
        System.out.print("Enter Showtime ID: ");
        int sid = readInt();

        if (findShowtime(sid) == null) { System.out.println("Showtime not found."); return; }

        boolean found = false;
        System.out.println("\n--- Tickets for Showtime #" + sid + " ---");
        for (Ticket t : tickets) {
            if (t.getShowtimeId() == sid) { System.out.println(t); found = true; }
        }
        if (!found) System.out.println("No tickets booked for this showtime.");
    }

    // 3. Seat availability — occupied vs free
    static void viewSeatAvailability() {
        listShowtimes();
        System.out.print("Enter Showtime ID: ");
        int sid = readInt();

        Showtime st = findShowtime(sid);
        if (st == null) { System.out.println("Showtime not found."); return; }

        List<Ticket> stTickets = new ArrayList<>();
        for (Ticket t : tickets) if (t.getShowtimeId() == sid) stTickets.add(t);

        System.out.println("\n--- Seat Status: Showtime #" + sid +
                           " (" + st.getMovieTitle() + ", " + st.getHall() + ") ---");

        if (stTickets.isEmpty()) { System.out.println("No seats booked for this showtime."); return; }

        System.out.printf("  %-6s %-12s %-16s%n", "Seat", "Customer", "Status");
        System.out.println("  " + "-".repeat(36));
        for (Ticket t : stTickets) {
            System.out.printf("  %-6s %-12s %-16s%n",
                    t.getSeatNumber(), t.getCustomerId(),
                    t.isVoided() ? "FREE (voided)" : "OCCUPIED");
        }
    }

    // 4. Void ticket — refund + free seat
    //    Constraint: must exist and not already be voided
    static void voidTicket() {
        viewAllTickets();
        System.out.print("\nEnter Ticket ID to void: ");
        int tid = readInt();

        Ticket ticket = findTicket(tid);
        if (ticket == null)    { System.out.println("Ticket not found."); return; }
        if (ticket.isVoided()) { System.out.println("Ticket #" + tid + " is already voided."); return; }

        ticket.voidTicket();
        System.out.println("Ticket #" + tid + " voided. Seat " + ticket.getSeatNumber() +
                           " is now free. Refund issued to: " + ticket.getCustomerId());
        log(currentAdmin, "Voided ticket #" + tid +
                          " (Showtime #" + ticket.getShowtimeId() +
                          ", Seat: " + ticket.getSeatNumber() +
                          ", Customer: " + ticket.getCustomerId() + ")");
    }

    // 5. Update ticket seat — reassign for active ticket
    //    Validation: seat format A1-Z99, not already occupied in same showtime
    static void updateTicketSeat() {
        viewAllTickets();
        System.out.print("\nEnter Ticket ID to update seat: ");
        int tid = readInt();

        Ticket ticket = findTicket(tid);
        if (ticket == null)    { System.out.println("Ticket not found."); return; }
        if (ticket.isVoided()) { System.out.println("Cannot update a voided ticket."); return; }

        System.out.println("Current seat: " + ticket.getSeatNumber());
        String newSeat = promptSeatNumber(ticket.getShowtimeId(), ticket.getTicketId());
        String oldSeat = ticket.getSeatNumber();
        ticket.setSeatNumber(newSeat);

        System.out.println("Seat updated: " + oldSeat + " -> " + newSeat + " for Ticket #" + tid);
        log(currentAdmin, "Updated seat on ticket #" + tid + ": " + oldSeat + " -> " + newSeat +
                          " (Showtime #" + ticket.getShowtimeId() + ")");
    }

    // 6. Set base ticket price rule (per showtime / per hall)
    //    Validation: price must be numeric and positive
    static void setBasePriceRule() {
        listShowtimes();
        System.out.print("Enter Showtime ID to set price for: ");
        int sid = readInt();

        Showtime st = findShowtime(sid);
        if (st == null) { System.out.println("Showtime not found."); return; }

        double price = promptTicketPrice();
        String key   = sid + "-" + st.getHall();
        basePriceRules.put(key, price);

        System.out.println("Base price rule set: Showtime #" + sid +
                           " (" + st.getHall() + ") => $" + String.format("%.2f", price));
        log(currentAdmin, "Set base price rule [" + key + "] = $" + price);
    }

    // 7. View all base price rules
    static void viewBasePriceRules() {
        if (basePriceRules.isEmpty()) { System.out.println("No base price rules set."); return; }
        System.out.println("\n--- Base Ticket Price Rules ---");
        for (Map.Entry<String, Double> e : basePriceRules.entrySet())
            System.out.printf("  %-20s => $%.2f%n", e.getKey(), e.getValue());
    }


    // =========================================================================
    //  FEATURE 4 — SYSTEM LOG
    // =========================================================================

    static void systemLogMenu() {
        int choice;
        do {
            System.out.println("\n--- Admin: System Log ---");
            System.out.println("1. View All Logs");
            System.out.println("2. Filter by Date Range");
            System.out.println("3. Filter by User ID");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            choice = readInt();
            switch (choice) {
                case 1: viewAllLogs();       break;
                case 2: filterByDateRange(); break;
                case 3: filterByUserId();    break;
                case 0: break;
                default: System.out.println("Invalid choice!");
            }
        } while (choice != 0);
    }

    // Public logger — call from any feature method
    static void log(String userId, String action) {
        LOG.add(new LogEntry(userId, action));
    }

    // View all logs — read-only, no edit/delete exposed
    static void viewAllLogs() {
        if (LOG.isEmpty()) { System.out.println("No log entries found."); return; }
        System.out.println("\n--- System Log (All Entries) ---");
        System.out.println("  [Read-only. Logs cannot be edited or deleted.]");
        for (LogEntry e : LOG) System.out.println(e);
        System.out.println("Total entries: " + LOG.size());
    }

    // Filter by date range — start <= end validation
    static void filterByDateRange() {
        Date startDate = null, endDate = null;

        while (true) {
            try {
                System.out.print("Enter Start Date (dd/MM/yyyy): ");
                startDate = LOG_SDF.parse(sc.nextLine().trim());
                System.out.print("Enter End Date   (dd/MM/yyyy): ");
                endDate   = LOG_SDF.parse(sc.nextLine().trim());
                if (startDate.after(endDate)) {
                    System.out.println("Invalid range: Start date must be <= End date!");
                } else {
                    break;
                }
            } catch (ParseException e) {
                System.out.println("Invalid date format! Use dd/MM/yyyy");
            }
        }

        // Extend end date to 23:59:59 to include the full day
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = cal.getTime();

        List<LogEntry> results = new ArrayList<>();
        for (LogEntry e : LOG)
            if (!e.getTimestamp().before(startDate) && !e.getTimestamp().after(endDate))
                results.add(e);

        if (results.isEmpty()) { System.out.println("No log entries found in this date range."); return; }

        System.out.println("\n--- Log Entries: " +
                           new SimpleDateFormat("dd/MM/yyyy").format(startDate) + " to " +
                           new SimpleDateFormat("dd/MM/yyyy").format(endDate) + " ---");
        System.out.println("  [Read-only. Logs cannot be edited or deleted.]");
        for (LogEntry e : results) System.out.println(e);
        System.out.println("Entries found: " + results.size());
    }

    // Filter by user ID — validated format before searching
    static void filterByUserId() {
        String userId;
        while (true) {
            System.out.print("Enter User ID to filter (3-20 alphanumeric chars): ");
            userId = sc.nextLine().trim();
            if (userId.isEmpty()) { System.out.println("User ID cannot be empty!"); continue; }
            if (!USER_ID_PATTERN.matcher(userId).matches()) {
                System.out.println("Invalid User ID! Only letters, numbers, underscores (3-20 chars).");
                continue;
            }
            break;
        }

        final String uid = userId;
        List<LogEntry> results = new ArrayList<>();
        for (LogEntry e : LOG) if (e.getUserId().equalsIgnoreCase(uid)) results.add(e);

        if (results.isEmpty()) { System.out.println("No log entries found for user: " + uid); return; }

        System.out.println("\n--- Log Entries for User: " + uid + " ---");
        System.out.println("  [Read-only. Logs cannot be edited or deleted.]");
        for (LogEntry e : results) System.out.println(e);
        System.out.println("Entries found: " + results.size());
    }


    // =========================================================================
    //  SEED DATA
    // =========================================================================

    static void seedAll() {
        // ── Customers ──
        customers.put("jdoe", "pass123");
        customerEmails.put("jdoe", "john@email.com");
        ArrayList<String> jdoeBookings = new ArrayList<>();
        jdoeBookings.add("Avengers | 10:00 AM");
        customerBookings.put("jdoe", jdoeBookings);

        customers.put("asmith", "pass456");
        customerEmails.put("asmith", "alice@email.com");
        customerBookings.put("asmith", new ArrayList<>());

        customers.put("mjones", "pass789");
        customerEmails.put("mjones", "mike@email.com");
        customerBookings.put("mjones", new ArrayList<>());

        customers.put("lwilson", "pass000");
        customerEmails.put("lwilson", "lucy@email.com");
        customerBookings.put("lwilson", new ArrayList<>());

        customers.put("rlee", "pass111");
        customerEmails.put("rlee", "ray@email.com");
        customerBookings.put("rlee", new ArrayList<>());

        // ── Showtimes ──
        try {
            showtimes.add(new Showtime(1, "Avengers: Endgame", ST_SDF.parse("20/06/2026 14:00"), "Hall A"));
            showtimes.add(new Showtime(2, "Avengers: Endgame", ST_SDF.parse("20/06/2026 19:30"), "Hall A"));
            showtimes.add(new Showtime(3, "Inception",         ST_SDF.parse("21/06/2026 15:00"), "Hall B"));
            showtimes.add(new Showtime(4, "Inception",         ST_SDF.parse("22/06/2026 20:00"), "Hall B"));
            showtimes.add(new Showtime(5, "The Dark Knight",   ST_SDF.parse("23/06/2026 18:00"), "Hall C"));
        } catch (ParseException ignored) {}

        // ── Tickets ──
        tickets.add(new Ticket(1, "jdoe",    "A1", 12.50));
        tickets.add(new Ticket(1, "asmith",  "A2", 12.50));
        tickets.add(new Ticket(1, "mjones",  "B3", 12.50));
        tickets.add(new Ticket(2, "jdoe",    "C1", 12.50));
        tickets.add(new Ticket(2, "lwilson", "C2", 12.50));
        tickets.add(new Ticket(3, "asmith",  "A5", 15.00));
        tickets.add(new Ticket(3, "rlee",    "A6", 15.00));
        tickets.add(new Ticket(4, "mjones",  "D4", 15.00));
        tickets.add(new Ticket(5, "lwilson", "B1", 10.00));
        tickets.add(new Ticket(5, "rlee",    "B2", 10.00));

        // ── Base price rules ──
        basePriceRules.put("1-Hall A", 12.50);
        basePriceRules.put("2-Hall A", 12.50);
        basePriceRules.put("3-Hall B", 15.00);
        basePriceRules.put("4-Hall B", 15.00);
        basePriceRules.put("5-Hall C", 10.00);

        // ── Seed log entries ──
        try {
            SimpleDateFormat full = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            full.setLenient(false);
            LOG.add(new LogEntry("jdoe",   "User login",                       full.parse("01/05/2026 09:00:00")));
            LOG.add(new LogEntry("asmith", "User login",                       full.parse("01/05/2026 09:15:00")));
            LOG.add(new LogEntry("jdoe",   "Booking created: Showtime #1, A2", full.parse("01/05/2026 09:20:00")));
            LOG.add(new LogEntry("asmith", "Payment success: Ticket #3",       full.parse("01/05/2026 09:45:00")));
            LOG.add(new LogEntry("admin",  "Showtime cancellation: #2",        full.parse("02/05/2026 10:30:00")));
            LOG.add(new LogEntry("jdoe",   "Payment failure: Ticket #5",       full.parse("03/05/2026 11:00:00")));
            LOG.add(new LogEntry("jdoe",   "User logout",                      full.parse("03/05/2026 11:05:00")));
            LOG.add(new LogEntry("admin",  "Voided ticket #5 (Showtime #3)",   full.parse("04/05/2026 14:00:00")));
            LOG.add(new LogEntry("admin",  "Updated customer info: jdoe",      full.parse("05/05/2026 08:30:00")));
        } catch (ParseException ignored) {}
    }


    // =========================================================================
    //  SHARED HELPERS
    // =========================================================================

    // Seat number: letter + 1-2 digits, not already occupied in same showtime
    static String promptSeatNumber(int showtimeId, int excludeTicketId) {
        while (true) {
            System.out.print("Enter new Seat Number (e.g. A1, B12): ");
            String seat = sc.nextLine().trim().toUpperCase();

            if (!SEAT_PATTERN.matcher(seat).matches()) {
                System.out.println("Invalid seat format! Must be a letter + 1-2 digits (e.g. A1, B12).");
                continue;
            }

            boolean taken = false;
            for (Ticket t : tickets) {
                if (t.getShowtimeId() == showtimeId &&
                    t.getTicketId()   != excludeTicketId &&
                    !t.isVoided() &&
                    t.getSeatNumber().equalsIgnoreCase(seat)) {
                    taken = true; break;
                }
            }

            if (taken) {
                System.out.println("Seat " + seat + " is already occupied for this showtime. Choose another.");
            } else {
                return seat;
            }
        }
    }

    // Ticket price: numeric and positive
    static double promptTicketPrice() {
        while (true) {
            System.out.print("Enter Ticket Price (e.g. 12.50): ");
            try {
                double price = Double.parseDouble(sc.nextLine().trim());
                if (price <= 0) { System.out.println("Price must be a positive value!"); continue; }
                return price;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Price must be numeric.");
            }
        }
    }

    static Showtime findShowtime(int id) {
        for (Showtime s : showtimes) if (s.getShowtimeId() == id) return s;
        return null;
    }

    static Ticket findTicket(int id) {
        for (Ticket t : tickets) if (t.getTicketId() == id) return t;
        return null;
    }

    static void listShowtimes() {
        System.out.println("\n-- Showtimes --");
        for (Showtime s : showtimes) System.out.println(s);
    }

    static int readInt() {
        while (true) {
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.print("Please enter a valid number: "); }
        }
    }
}
