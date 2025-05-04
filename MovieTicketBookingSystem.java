import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MovieTicketBookingSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static List<Movie> movies = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();
    private static int bookingIdCounter = 1;

    public static void main(String[] args) {
        initializeMovies();
        
        while (true) {
            System.out.println("\n=== Movie Ticket Booking System ===");
            System.out.println("1. Show Available Movies");
            System.out.println("2. Book Tickets");
            System.out.println("3. View Booking Details");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    displayMovies();
                    break;
                case 2:
                    bookTickets();
                    break;
                case 3:
                    viewBookingDetails();
                    break;
                case 4:
                    cancelBooking();
                    break;
                case 5:
                    System.out.println("Thank you for using our service. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void initializeMovies() {
        movies.add(new Movie(1, "Spirit Part-1", "Action", "10:00 AM", 250));
        movies.add(new Movie(2, "Kushi", "Love", "1:00 PM", 200));
        movies.add(new Movie(3, "Devara: Part 2", "Drama", "4:00 PM", 220));
        movies.add(new Movie(4, "Hit: The Final Case", "Crime", "7:00 PM", 180));
        movies.add(new Movie(5, "Kalki 2898 AD", "Sci-Fi", "10:00 PM", 230));
    }

    private static void displayMovies() {
        System.out.println("\nAvailable Movies:");
        System.out.println("----------------------------------------------------------------");
        System.out.printf("%-5s %-20s %-15s %-10s %-10s %-10s\n", 
                          "ID", "Title", "Genre", "Time", "Price", "Available");
        System.out.println("----------------------------------------------------------------");
        
        for (Movie movie : movies) {
            System.out.printf("%-5d %-20s %-15s %-10s $%-9d %-10d\n", 
                             movie.getId(), 
                             movie.getTitle(), 
                             movie.getGenre(), 
                             movie.getShowTime(), 
                             movie.getPrice(), 
                             movie.getAvailableSeats());
        }
        System.out.println("----------------------------------------------------------------");
    }

    private static void bookTickets() {
        displayMovies();
        
        System.out.print("\nEnter Movie ID to book tickets: ");
        int movieId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        Movie selectedMovie = null;
        for (Movie movie : movies) {
            if (movie.getId() == movieId) {
                selectedMovie = movie;
                break;
            }
        }
        
        if (selectedMovie == null) {
            System.out.println("Invalid Movie ID.");
            return;
        }
        
        // Display seating arrangement
        selectedMovie.displaySeating();
        
        System.out.print("Enter number of tickets: ");
        int numTickets = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        if (numTickets <= 0) {
            System.out.println("Invalid number of tickets.");
            return;
        }
        
        if (numTickets > selectedMovie.getAvailableSeats()) {
            System.out.println("Only " + selectedMovie.getAvailableSeats() + " seats available.");
            return;
        }
        
        // Select seats
        List<String> selectedSeats = new ArrayList<>();
        for (int i = 0; i < numTickets; i++) {
            System.out.print("Enter seat (e.g., A5) for ticket " + (i+1) + ": ");
            String seat = scanner.nextLine().toUpperCase();
            
            if (!selectedMovie.isValidSeat(seat)) {
                System.out.println("Invalid seat format. Please use format like A1, B5, J10.");
                i--;
                continue;
            }
            
            if (selectedMovie.isSeatAvailable(seat)) {
                selectedSeats.add(seat);
                selectedMovie.bookSeat(seat);
            } else {
                System.out.println("Seat " + seat + " is already booked. Please choose another seat.");
                i--;
            }
        }
        
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
        
        System.out.print("Enter customer email: ");
        String customerEmail = scanner.nextLine();
        
        System.out.print("Enter customer phone: ");
        String customerPhone = scanner.nextLine();
        
        // Select payment method
        System.out.println("\nSelect Payment Method:");
        System.out.println("1. Credit Card");
        System.out.println("2. Debit Card");
        System.out.println("3. Net Banking");
        System.out.println("4. UPI");
        System.out.print("Enter payment option (1-4): ");
        int paymentOption = scanner.nextInt();
        scanner.nextLine();
        
        String paymentMethod = "";
        switch (paymentOption) {
            case 1: paymentMethod = "Credit Card"; break;
            case 2: paymentMethod = "Debit Card"; break;
            case 3: paymentMethod = "Net Banking"; break;
            case 4: paymentMethod = "UPI"; break;
            default: paymentMethod = "Unknown";
        }
        
        // Payment details
        if (paymentOption >= 1 && paymentOption <= 4) {
            System.out.print("Enter " + paymentMethod + " number: ");
            String paymentNumber = scanner.nextLine();
            
            double totalAmount = numTickets * selectedMovie.getPrice();
            System.out.printf("Total amount to pay: $%.2f\n", totalAmount);
            
            System.out.print("Enter amount to pay: $");
            double amountPaid = scanner.nextDouble();
            scanner.nextLine();
            
            if (amountPaid < totalAmount) {
                System.out.println("Insufficient payment. Booking cancelled.");
                // Release the seats
                for (String seat : selectedSeats) {
                    selectedMovie.cancelSeat(seat);
                }
                return;
            }
            
            if (amountPaid > totalAmount) {
                System.out.printf("Change: $%.2f\n", (amountPaid - totalAmount));
            }
            
            Booking booking = new Booking(
                bookingIdCounter++,
                selectedMovie,
                customerName,
                customerEmail,
                customerPhone,
                numTickets,
                totalAmount,
                paymentMethod,
                selectedSeats
            );
            
            bookings.add(booking);
            
            System.out.println("\n=== Booking Confirmation ===");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Movie: " + selectedMovie.getTitle());
            System.out.println("Show Time: " + selectedMovie.getShowTime());
            System.out.println("Selected Seats: " + String.join(", ", selectedSeats));
            System.out.println("Number of Tickets: " + numTickets);
            System.out.printf("Total Amount: $%.2f\n", totalAmount);
            System.out.println("Payment Method: " + paymentMethod);
            System.out.println("Thank you for your booking!");
        } else {
            System.out.println("Invalid payment option. Booking cancelled.");
            // Release the seats
            for (String seat : selectedSeats) {
                selectedMovie.cancelSeat(seat);
            }
        }
    }

    private static void viewBookingDetails() {
        System.out.print("\nEnter Booking ID: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        Booking foundBooking = null;
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                foundBooking = booking;
                break;
            }
        }
        
        if (foundBooking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        System.out.println("\n=== Booking Details ===");
        System.out.println("Booking ID: " + foundBooking.getBookingId());
        System.out.println("Customer: " + foundBooking.getCustomerName());
        System.out.println("Email: " + foundBooking.getCustomerEmail());
        System.out.println("Phone: " + foundBooking.getCustomerPhone());
        System.out.println("Movie: " + foundBooking.getMovie().getTitle());
        System.out.println("Genre: " + foundBooking.getMovie().getGenre());
        System.out.println("Show Time: " + foundBooking.getMovie().getShowTime());
        System.out.println("Selected Seats: " + String.join(", ", foundBooking.getSelectedSeats()));
        System.out.println("Number of Tickets: " + foundBooking.getNumberOfTickets());
        System.out.printf("Total Amount: $%.2f\n", foundBooking.getTotalAmount());
        System.out.println("Payment Method: " + foundBooking.getPaymentMethod());
    }

    private static void cancelBooking() {
        System.out.print("\nEnter Booking ID to cancel: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        Booking foundBooking = null;
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                foundBooking = booking;
                break;
            }
        }
        
        if (foundBooking == null) {
            System.out.println("Booking not found.");
            return;
        }
        
        // Return seats to available pool
        for (String seat : foundBooking.getSelectedSeats()) {
            foundBooking.getMovie().cancelSeat(seat);
        }
        
        bookings.remove(foundBooking);
        System.out.println("Booking ID " + bookingId + " has been cancelled successfully.");
    }

    static class Movie {
        private int id;
        private String title;
        private String genre;
        private String showTime;
        private int price;
        private boolean[][] seats; // 10 rows (A-J) x 10 columns (1-10)
        private int availableSeats;

        public Movie(int id, String title, String genre, String showTime, int price) {
            this.id = id;
            this.title = title;
            this.genre = genre;
            this.showTime = showTime;
            this.price = price;
            this.seats = new boolean[10][10]; // false means available
            this.availableSeats = 10 * 10; // 100 seats initially
        }

        public void displaySeating() {
            System.out.println("\nSeating Arrangement for " + title + ":");
            System.out.println("    1  2  3  4  5  6  7  8  9 10");
            System.out.println("   -----------------------------");
            
            for (int row = 0; row < 10; row++) {
                System.out.print((char)('A' + row) + " | ");
                for (int col = 0; col < 10; col++) {
                    System.out.print(seats[row][col] ? "X  " : "O  ");
                }
                System.out.println();
            }
            System.out.println("\nO = Available, X = Booked");
        }

        public boolean isValidSeat(String seat) {
            if (seat == null || seat.length() < 2 || seat.length() > 3) {
                return false;
            }
            
            char rowChar = seat.charAt(0);
            int row = rowChar - 'A';
            if (row < 0 || row >= 10) {
                return false;
            }
            
            try {
                int col = Integer.parseInt(seat.substring(1)) - 1;
                return col >= 0 && col < 10;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public boolean isSeatAvailable(String seat) {
            if (!isValidSeat(seat)) return false;
            
            int row = seat.charAt(0) - 'A';
            int col = Integer.parseInt(seat.substring(1)) - 1;
            return !seats[row][col];
        }

        public void bookSeat(String seat) {
            if (isValidSeat(seat) && isSeatAvailable(seat)) {
                int row = seat.charAt(0) - 'A';
                int col = Integer.parseInt(seat.substring(1)) - 1;
                seats[row][col] = true;
                availableSeats--;
            }
        }

        public void cancelSeat(String seat) {
            if (isValidSeat(seat)) {
                int row = seat.charAt(0) - 'A';
                int col = Integer.parseInt(seat.substring(1)) - 1;
                if (seats[row][col]) {
                    seats[row][col] = false;
                    availableSeats++;
                }
            }
        }

        // Getters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getGenre() { return genre; }
        public String getShowTime() { return showTime; }
        public int getPrice() { return price; }
        public int getAvailableSeats() { return availableSeats; }
    }

    static class Booking {
        private int bookingId;
        private Movie movie;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private int numberOfTickets;
        private double totalAmount;
        private String paymentMethod;
        private List<String> selectedSeats;

        public Booking(int bookingId, Movie movie, String customerName, String customerEmail, 
                     String customerPhone, int numberOfTickets, double totalAmount,
                     String paymentMethod, List<String> selectedSeats) {
            this.bookingId = bookingId;
            this.movie = movie;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.customerPhone = customerPhone;
            this.numberOfTickets = numberOfTickets;
            this.totalAmount = totalAmount;
            this.paymentMethod = paymentMethod;
            this.selectedSeats = selectedSeats;
        }

        // Getters
        public int getBookingId() { return bookingId; }
        public Movie getMovie() { return movie; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public String getCustomerPhone() { return customerPhone; }
        public int getNumberOfTickets() { return numberOfTickets; }
        public double getTotalAmount() { return totalAmount; }
        public String getPaymentMethod() { return paymentMethod; }
        public List<String> getSelectedSeats() { return selectedSeats; }
    }
}