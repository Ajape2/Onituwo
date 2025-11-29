import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Simple multi-business Billing System
 * - Supports Provision Store, Market Stall, POS, Gas Station
 * - Console UI, writes receipts to receipts/ folder
 *
 * Customize item lists and tax/discount rules as needed.
 */
public class MarketBillingSystem {
    static Scanner sc = new Scanner(System.in);
    static final String RECEIPT_DIR = "receipts";

    public static void main(String[] args) {
        ensureReceiptDir();
        System.out.println("=== Simple Billing System ===");

        while (true) {
            System.out.println("\nChoose business type:");
            System.out.println("1. Provision Store");
            System.out.println("2. Market Stall");
            System.out.println("3. POS / Till");
            System.out.println("4. Gas Station");
            System.out.println("5. Exit");
            System.out.print("Select (1-5): ");
            int choice = readInt();

            switch (choice) {
                case 1 -> runBusiness(BusinessType.PROVISION_STORE);
                case 2 -> runBusiness(BusinessType.MARKET_STALL);
                case 3 -> runBusiness(BusinessType.POS);
                case 4 -> runBusiness(BusinessType.GAS_STATION);
                case 5 -> { System.out.println("Goodbye!"); return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void runBusiness(BusinessType type) {
        Inventory inventory = Inventory.createFor(type);
        System.out.println("\nBusiness: " + type.displayName);
        System.out.println("Tax rate: " + (type.taxRate * 100) + "%");

        Bill bill = new Bill(type.taxRate);
        boolean adding = true;

        while (adding) {
            System.out.println("\nOptions:");
            System.out.println("1. List items");
            System.out.println("2. Add item by code");
            System.out.println("3. Add custom item");
            System.out.println("4. Finish and print receipt");
            System.out.print("Choose (1-4): ");
            int opt = readInt();
            switch (opt) {
                case 1 -> inventory.printAll();
                case 2 -> addFromInventory(inventory, bill, type);
                case 3 -> addCustomItem(bill);
                case 4 -> {
                    finishAndSave(bill, type);
                    adding = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void addFromInventory(Inventory inventory, Bill bill, BusinessType type) {
        System.out.print("Enter item code: ");
        String code = sc.next().trim();
        Item itm = inventory.findByCode(code);
        if (itm == null) {
            System.out.println("Item not found.");
            return;
        }

        if (type == BusinessType.GAS_STATION) {
            System.out.println("Gas station mode: choose input type:");
            System.out.println("1. Enter litres");
            System.out.println("2. Enter money amount (will compute litres)");
            System.out.print("Choice: ");
            int c = readInt();
            if (c == 1) {
                System.out.print("Enter litres: ");
                double litres = readDouble();
                double pricePerLitre = itm.price;
                double amount = litres * pricePerLitre;
                bill.addLine(new BillLine(itm.name + " (fuel)", litres, pricePerLitre, amount, true));
            } else {
                System.out.print("Enter amount (currency): ");
                double amount = readDouble();
                double pricePerLitre = itm.price;
                double litres = amount / pricePerLitre;
                bill.addLine(new BillLine(itm.name + " (fuel)", litres, pricePerLitre, amount, true));
            }
        } else {
            System.out.print("Enter quantity: ");
            double qty = readDouble();
            bill.addLine(new BillLine(itm.name, qty, itm.price, itm.price * qty, false));
        }
        System.out.println("Added to bill.");
    }

    static void addCustomItem(Bill bill) {
        sc.nextLine(); // clear line
        System.out.print("Custom item name: ");
        String name = sc.nextLine().trim();
        System.out.print("Price per unit: ");
        double price = readDouble();
        System.out.print("Quantity: ");
        double qty = readDouble();
        bill.addLine(new BillLine(name, qty, price, qty * price, false));
        System.out.println("Custom item added.");
    }

    static void finishAndSave(Bill bill, BusinessType type) {
        // Possibly ask for discount or customer details
        System.out.print("Apply discount percent? (0 for none): ");
        double discPct = readDouble();
        bill.setDiscountPercent(discPct);

        System.out.print("Customer name (empty for walk-in): ");
        sc.nextLine();
        String customer = sc.nextLine().trim();
        bill.customerName = customer.isEmpty() ? "Walk-in" : customer;

        String receipt = bill.generateReceipt(type);
        System.out.println("\n--- Receipt ---");
        System.out.println(receipt);

        // save receipt
        try {
            String filename = saveReceiptToFile(receipt);
            System.out.println("Receipt saved to: " + filename);
        } catch (IOException e) {
            System.out.println("Failed to save receipt: " + e.getMessage());
        }
    }

    static void ensureReceiptDir() {
        try {
            Files.createDirectories(Paths.get(RECEIPT_DIR));
        } catch (IOException e) {
            // ignore
        }
    }

    static String saveReceiptToFile(String content) throws IOException {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = RECEIPT_DIR + "/receipt_" + ts + ".txt";
        Files.writeString(Paths.get(filename), content, StandardOpenOption.CREATE_NEW);
        return filename;
    }

    // Utilities for safe input
    static int readInt() {
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Enter integer: ");
        }
        int v = sc.nextInt();
        return v;
    }

    static double readDouble() {
        while (!sc.hasNextDouble()) {
            sc.next();
            System.out.print("Enter number: ");
        }
        double v = sc.nextDouble();
        return v;
    }

    // --- Domain classes ---

    enum BusinessType {
        PROVISION_STORE("Provision Store", 0.05),
        MARKET_STALL("Market Stall", 0.00),
        POS("POS / Till", 0.07),
        GAS_STATION("Gas Station", 0.05);

        final String displayName;
        final double taxRate;

        BusinessType(String name, double taxRate) {
            this.displayName = name;
            this.taxRate = taxRate;
        }
    }

    static class Item {
        String code;
        String name;
        double price; // price per unit (or per litre for gas)
        Item(String code, String name, double price) {
            this.code = code;
            this.name = name;
            this.price = price;
        }
        public String toString() {
            return code + " - " + name + " @ " + String.format("%.2f", price);
        }
    }

    static class Inventory {
        private final Map<String, Item> items = new LinkedHashMap<>();
        static Inventory createFor(BusinessType type) {
            Inventory inv = new Inventory();
            switch (type) {
                case PROVISION_STORE -> {
                    inv.add(new Item("P001","Rice 50kg (per bag)", 25000));
                    inv.add(new Item("P002","Sugar 1kg", 900));
                    inv.add(new Item("P003","Bread (loaf)", 150));
                    inv.add(new Item("P004","Vegetable oil 5L", 7500));
                }
                case MARKET_STALL -> {
                    inv.add(new Item("M001","Tomatoes (per kg)", 400));
                    inv.add(new Item("M002","Onions (per kg)", 350));
                    inv.add(new Item("M003","Pepper (per kg)", 700));
                }
                case POS -> {
                    inv.add(new Item("S001","Airtime 1000", 1000));
                    inv.add(new Item("S002","Electricity Token (min)", 100));
                    inv.add(new Item("S003","Data 1GB", 500));
                }
                case GAS_STATION -> {
                    // price per litre
                    inv.add(new Item("G001", "Premium Gasoline", 760.0));
                    inv.add(new Item("G002", "Diesel", 720.0));
                }
            }
            return inv;
        }
        void add(Item it) { items.put(it.code, it); }
        Item findByCode(String code) { return items.get(code.toUpperCase()); }
        void printAll() {
            System.out.println("\nAvailable items:");
            for (Item it : items.values()) System.out.println(it);
        }
    }

    static class BillLine {
        String name;
        double quantity; // can be litres or units
        double unitPrice;
        double lineTotal;
        boolean isFuel; // special flag to indicate litres-mode
        BillLine(String name, double quantity, double unitPrice, double lineTotal, boolean isFuel) {
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.lineTotal = lineTotal;
            this.isFuel = isFuel;
        }
    }

    static class Bill {
        List<BillLine> lines = new ArrayList<>();
        double taxRate; // eg 0.05 for 5%
        double discountPercent = 0.0;
        String customerName = "Walk-in";

        Bill(double taxRate) { this.taxRate = taxRate; }

        void addLine(BillLine l) { lines.add(l); }

        void setDiscountPercent(double pct) { if (pct >= 0) discountPercent = pct; }

        double subtotal() {
            double s = 0;
            for (BillLine l : lines) s += l.lineTotal;
            return s;
        }
        double discountAmount() { return subtotal() * (discountPercent / 100.0); }
        double taxAmount() { return (subtotal() - discountAmount()) * taxRate; }
        double total() { return subtotal() - discountAmount() + taxAmount(); }

        String generateReceipt(BusinessType type) {
            StringBuilder sb = new StringBuilder();
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            sb.append("=== Receipt ===\n");
            sb.append("Business: ").append(type.displayName).append("\n");
            sb.append("Date: ").append(ts).append("\n");
            sb.append("Customer: ").append(customerName).append("\n");
            sb.append("-------------------------------\n");
            sb.append(String.format("%-20s %6s %8s\n","Item","Qty","Amount"));
            sb.append("-------------------------------\n");
            for (BillLine l : lines) {
                String qStr = l.isFuel ? String.format("%.2fL", l.quantity) : String.format("%.2f", l.quantity);
                sb.append(String.format("%-20s %6s %8.2f\n", truncate(l.name,20), qStr, l.lineTotal));
            }
            sb.append("-------------------------------\n");
            sb.append(String.format("%-20s %14.2f\n","Subtotal:", subtotal()));
            if (discountPercent > 0.0) {
                sb.append(String.format("%-20s %13.2f\n", "Discount ("+discountPercent+"%):", -discountAmount()));
            }
            sb.append(String.format("%-20s %14.2f\n","Tax ("+ (int)(taxRate*100) +"%):", taxAmount()));
            sb.append(String.format("%-20s %14.2f\n","TOTAL:", total()));
            sb.append("-------------------------------\n");
            sb.append("Thank you for your business!\n");
            return sb.toString();
        }

        private String truncate(String s, int len) {
            if (s.length() <= len) return s;
            return s.substring(0, len-3) + "...";
        }
    }
}
