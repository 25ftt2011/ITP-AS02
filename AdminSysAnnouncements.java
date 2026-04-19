import java.util.*;
import java.text.*;

class Announcement {
    private String title;
    private String message;
    private Date startDate;
    private Date endDate;

    public Announcement(String title, String message, Date startDate, Date endDate) {
        this.title = title;
        this.message = message;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "\n--- Announcement ---" +
               "\nTitle: " + title +
               "\nMessage: " + message +
               "\nStart Date: " + sdf.format(startDate) +
               "\nEnd Date: " + sdf.format(endDate);
    }
}

public class AdminSysAnnouncements {

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Announcement> announcements = new ArrayList<>();
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        sdf.setLenient(false); // strict date validation

        int choice;
        do {
            System.out.println("\n=== Admin System Announcements ===");
            System.out.println("1. Create Announcement");
            System.out.println("2. View Announcements");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    createAnnouncement();
                    break;
                case 2:
                    viewAnnouncements();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 0);
    }

    // =========================
    // CREATE ANNOUNCEMENT
    // =========================
    public static void createAnnouncement() {
        String title, message;
        Date startDate = null, endDate = null;

        // Title validation
        while (true) {
            System.out.print("Enter Title (max 100 chars): ");
            title = sc.nextLine();

            if (title.isEmpty()) {
                System.out.println("Title cannot be empty!");
            } else if (title.length() > 100) {
                System.out.println("Title exceeds 100 characters!");
            } else {
                break;
            }
        }

        // Message validation
        while (true) {
            System.out.print("Enter Message (max 1500 chars): ");
            message = sc.nextLine();

            if (message.isEmpty()) {
                System.out.println("Message cannot be empty!");
            } else if (message.length() > 1500) {
                System.out.println("Message exceeds 1500 characters!");
            } else {
                break;
            }
        }

        // Date validation
        while (true) {
            try {
                System.out.print("Enter Start Date (dd/MM/yyyy): ");
                startDate = sdf.parse(sc.nextLine());

                System.out.print("Enter End Date (dd/MM/yyyy): ");
                endDate = sdf.parse(sc.nextLine());

                if (startDate.after(endDate)) {
                    System.out.println("Start date must be BEFORE or EQUAL to end date!");
                } else {
                    break;
                }

            } catch (ParseException e) {
                System.out.println("Invalid date format! Please use dd/MM/yyyy");
            }
        }

        // Save announcement
        announcements.add(new Announcement(title, message, startDate, endDate));
        System.out.println("Announcement created successfully!");
    }

    // =========================
    // VIEW ANNOUNCEMENTS
    // =========================
    public static void viewAnnouncements() {
        if (announcements.isEmpty()) {
            System.out.println("No announcements available.");
            return;
        }

        for (Announcement a : announcements) {
            System.out.println(a);
        }
    }
}