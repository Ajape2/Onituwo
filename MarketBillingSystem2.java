import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class MarketBillingSystem2 {

    static Scanner sc = new Scanner(System.in);
    static String currentCashier = "";

    // --------------------- LOGIN SYSTEM -------------------------
    public static void login() {
        System.out.print("Enter cashier username: ");
        String username = sc.nextLine();

        System.out.print("Enter password: ");
        String password = sc.nextLine();

        if (checkLogin(username, password)) {
            currentCashier = username;
            System.out.println("\nLogin successful! Welcome " + username + "\n");
        } else {
            System.out.println("Invalid login. Try again.\n");
            login();
        }
    }

    public static boolean checkLogin(String username, String password) {
        File f = new File("cashiers.csv");
        if (!f.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                if (arr.length == 2 && arr[0].equals(username) && arr[1].equals(password)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading login file.");
        }
        return false;
    }

    // --------------------- ITEM CLASS ---------------------------
    static class Item {
        String name;
        double price;
        int qty;
        double discount; // percentage

        Item(String name, double price, int qty, double discount) {
            this.name = name;
            this.price = price;
            this.qty = qty;
            this.discount = discount;
        }

        double total() {
            double amount = price * qty;
            double discAmount = amount * (discount / 100.0);
            return amount - discAmount;
        }
    }

    // --------------------- ADD ITEM -----------------------------
    public static Item addItem() {
        System.out.print("Item name: ");
        String name = sc.nextLine();

        System.out.print("Price: ");
        double price = readDouble();

        System.out.print("Quantity: ");
        int qty = readInt();

        System.out.print("Discount (%): ");
        double discount = readDouble();

        return new Item(name, price, qty, discount);
    }

    // --------------------- DAILY SALES SAVE ----------------------
    public static void saveSale(Item item) {
        try {
            String fileName = "sales_" + LocalDate.now() + ".csv";
            FileWriter fw = new FileWriter(fileName, true);

            fw.write(currentCashier + "," + item.name + "," + item.qty + "," +
                    item.price + "," + item.discount + "," + item.total() + "\n");
            fw.close();

        } catch (Exception e) {
            System.out.println("Error saving sales report.");
        }
    }

    // --------------------- VIEW DAILY REPORT ---------------------
    public static void viewReport() {
        String fileName = "sales_" + LocalDate.now() + ".csv";

        File f = new File(fileName);
        if (!f.exists()) {
            System.out.println("No sales recorded today.");
            return;
        }

        System.out.println("\n------ DAILY SALES REPORT ------");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            double total = 0;

            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",");
                System.out.println("Cashier: " + arr[0] + ", Item: " + arr[1] +
                        ", Qty: " + arr[2] + ", Total: " + arr[5]);
                total += Double.parseDouble(arr[5]);
            }

            System.out.println("\nTotal revenue today: ₦" + total);
        } catch (Exception e) {
            System.out.println("Error reading report.");
        }
    }

    // --------------------- INPUT HELPERS -------------------------
    public static int readInt() {
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Enter integer: ");
        }
        return sc.nextInt();
    }

    public static double readDouble() {
        while (!sc.hasNextDouble()) {
            sc.next();
            System.out.print("Enter number: ");
        }
        return sc.nextDouble();
    }

    // --------------------- MAIN MENU -----------------------------
    public static void main(String[] args) {
        System.out.println("=== SIMPLE BILLING SYSTEM ===");

        login(); // must login first
        List<Item> cart = new ArrayList<>();

        sc.nextLine(); // clear buffer

        while (true) {
            System.out.println("\n1. Add Item");
            System.out.println("2. View Cart");
            System.out.println("3. Print Receipt");
            System.out.println("4. Daily Sales Report");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = readInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    Item it = addItem();
                    cart.add(it);
                    saveSale(it);
                    System.out.println("Item added!");
                    break;

                case 2:
                    System.out.println("\n--- CART ITEMS ---");
                    for (Item i : cart) {
                        System.out.println(i.name + " | ₦" + i.price + " x " + i.qty +
                                " | Discount: " + i.discount + "%");
                    }
                    break;

                case 3:
                    System.out.println("\n----- RECEIPT -----");
                    double sum = 0;
                    for (Item i : cart) {
                        System.out.println(i.name + " - Total: ₦" + i.total());
                        sum += i.total();
                    }
                    System.out.println("GRAND TOTAL: ₦" + sum);
                    break;

                case 4:
                    viewReport();
                    break;

                case 5:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
