import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class AdminCustomerManagement {
    // Shared data structures (mirrored from CustomerRole)
    private static Map<String, String> users = new HashMap<>(); 
    private static Map<String, String> userEmails = new HashMap<>(); // To track emails for validation
    private static Map<String, ArrayList<String>> userBookings = new HashMap<>();
    
    private static Scanner input = new Scanner(System.in);

    // Validation Regex Patterns
    private static final String NAME_PATTERN = "^[a-zA-Z0-9 ]+$"; // No special characters like #, $, &
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$"; // user@email.com format

    public static void main(String[] args) {
        // Sample data for demonstration
        seedData();
        showAdminMenu();
    }

    private static void seedData() {
        users.put("jdoe", "pass123");
        userEmails.put("jdoe", "john@email.com");
        ArrayList<String> bookings = new ArrayList<>();
        bookings.add("Avengers | 10:00 AM");
        userBookings.put("jdoe", bookings);

        users.put("asmith", "pass456");
        userEmails.put("asmith", "alice@email.com");
        userBookings.put("asmith", new ArrayList<>()); // No active bookings
    }

    public static void showAdminMenu() {
        while (true) {
            System.out.println("\n--- Admin: Customer Management ---");
            System.out.println("1. Search Customer (by Name/Email/ID)");
            System.out.println("2. Update Customer Info");
            System.out.println("3. Delete Customer");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            int choice = Integer.parseInt(input.nextLine());
            switch (choice) {
                case 1: searchCustomer(); break;
                case 2: updateCustomer(); break;
                case 3: deleteCustomer(); break;
                case 4: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Requirement: Search by name/email/ID
     * Must be valid name/email/ID
     */
    private static void searchCustomer() {
        System.out.print("Enter search query (ID, Name, or Email): ");
        String query = input.nextLine().trim();

        if (query.isEmpty()) {
            System.out.println("Invalid search: Query cannot be empty.");
            return;
        }

        boolean found = false;
        for (String id : users.keySet()) {
            String email = userEmails.getOrDefault(id, "N/A");
            if (id.contains(query) || email.contains(query)) {
                System.out.println("ID: " + id + " | Email: " + email + " | Bookings: " + userBookings.get(id).size());
                found = true;
            }
        }
        if (!found) System.out.println("No customer found matching: " + query);
    }

    /**
     * Requirement: Name and Email validation
     * Name: No special characters (#, $, &)
     * Email: Valid user@email.com format
     */
    private static void updateCustomer() {
        System.out.print("Enter Customer ID to update: ");
        String id = input.nextLine();

        if (!users.containsKey(id)) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.print("Enter new Name: ");
        String newName = input.nextLine();
        if (!Pattern.matches(NAME_PATTERN, newName)) {
            System.out.println("Error: Name contains invalid special characters.");
            return;
        }

        System.out.print("Enter new Email: ");
        String newEmail = input.nextLine();
        if (!Pattern.matches(EMAIL_PATTERN, newEmail)) {
            System.out.println("Error: Invalid email format.");
            return;
        }

        userEmails.put(id, newEmail);
        System.out.println("Customer info updated successfully.");
    }

    /**
     * Requirement: Delete Customer
     * Constraint: Customer must not have active bookings
     */
    private static void deleteCustomer() {
        System.out.print("Enter Customer ID to delete: ");
        String id = input.nextLine();

        if (!users.containsKey(id)) {
            System.out.println("Customer not found.");
            return;
        }

        // Check for active bookings
        ArrayList<String> bookings = userBookings.get(id);
        if (bookings != null && !bookings.isEmpty()) {
            System.out.println("Error: Cannot delete customer. Active bookings found (" + bookings.size() + ").");
        } else {
            users.remove(id);
            userEmails.remove(id);
            userBookings.remove(id);
            System.out.println("Customer deleted successfully.");
        }
    }
}