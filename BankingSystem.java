import java.io.*;
import java.util.*;

// ====================== ACCOUNT CLASS =========================

class BankAccount implements Serializable {
    private String name;
    private String email;
    private String phone;
    private String bvn;
    private String accountType;
    private String pin;
    private double balance;

    public BankAccount(String name, String email, String phone,
                       String accountType, String bvn, String pin, double balance) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.accountType = accountType;
        this.bvn = bvn;
        this.pin = pin;
        this.balance = balance;
    }

    public String getName() { return name; }
    public String getBVN() { return bvn; }
    public String getPIN() { return pin; }
    public double getBalance() { return balance; }

    public void deposit(double amt) { balance += amt; }
    
    public boolean withdraw(double amt) {
        if (amt > 0 && amt <= balance) {
            balance -= amt;
            return true;
        }
        return false;
    }

    public void transfer(BankAccount receiver, double amt) {
        this.balance -= amt;
        receiver.balance += amt;
    }

    @Override
    public String toString() {
        return name + "," + email + "," + phone + "," + accountType 
               + "," + bvn + "," + pin + "," + balance;
    }
}

// ========================== MAIN SYSTEM ============================

public class BankingSystem {

    private static Scanner input = new Scanner(System.in);
    private static Map<String, BankAccount> accounts = new HashMap<>();
    private static final String FILE_NAME = "accounts.txt";

    public static void main(String[] args) {
        loadAccounts();

        int choice = 0;

        while (choice != 3) {
            System.out.println("\n===== JAVA BANK SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            choice = input.nextInt();
            input.nextLine();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> login();
                case 3 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ===================== BVN GENERATOR ======================

    public static String generateBVN() {
        Random rand = new Random();
        StringBuilder bvn = new StringBuilder();
        for (int i = 0; i < 10; i++) bvn.append(rand.nextInt(10));
        return bvn.toString();
    }

    // ===================== SAVE & LOAD ======================

    public static void saveAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (BankAccount acc : accounts.values()) {
                writer.write(acc.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts!");
        }
    }

    public static void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                
                BankAccount acc = new BankAccount(
                        data[0], data[1], data[2], data[3],
                        data[4], data[5], Double.parseDouble(data[6])
                );

                accounts.put(data[4], acc);
            }
        } catch (IOException ignored) {}
    }

    // ===================== CREATE ACCOUNT ======================

    public static void createAccount() {
        System.out.print("Enter full name: ");
        String name = input.nextLine();

        System.out.print("Enter email: ");
        String email = input.nextLine();

        System.out.print("Enter phone number: ");
        String phone = input.nextLine();

        System.out.print("Enter account type (SAVINGS/CURRENT): ");
        String accountType = input.nextLine().toUpperCase();

        System.out.print("Set your 4-digit PIN: ");
        String pin = input.nextLine();

        System.out.print("Enter initial deposit: ");
        double initial = input.nextDouble();
        input.nextLine();

        String bvn = generateBVN();

        BankAccount acc = new BankAccount(name, email, phone, accountType, bvn, pin, initial);
        accounts.put(bvn, acc);
        saveAccounts();

        System.out.println("\nAccount created successfully!");
        System.out.println("Your BVN: " + bvn);
        System.out.println("Use this BVN + PIN to log in.");
    }

    // ===================== LOGIN ======================

    public static void login() {
        System.out.print("Enter BVN: ");
        String bvn = input.nextLine();

        if (!accounts.containsKey(bvn)) {
            System.out.println("BVN not found!");
            return;
        }

        System.out.print("Enter PIN: ");
        String pin = input.nextLine();

        BankAccount acc = accounts.get(bvn);

        if (!acc.getPIN().equals(pin)) {
            System.out.println("Incorrect PIN!");
            return;
        }

        System.out.println("\nLogin Successful! Welcome " + acc.getName());
        accountMenu(acc);
    }

    // ===================== ACCOUNT MENU ======================

    public static void accountMenu(BankAccount acc) {
        int choice = 0;

        while (choice != 5) {
            System.out.println("\n===== ACCOUNT MENU =====");
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Transfer");
            System.out.println("4. Check Balance");
            System.out.println("5. Logout");
            System.out.print("Choice: ");
            choice = input.nextInt();

            switch (choice) {
                case 1 -> deposit(acc);
                case 2 -> withdraw(acc);
                case 3 -> transfer(acc);
                case 4 -> System.out.println("Balance: " + acc.getBalance());
                case 5 -> System.out.println("Logged out.");
                default -> System.out.println("Invalid option!");
            }
        }
    }

    // ===================== BANK OPERATIONS ======================

    public static void deposit(BankAccount acc) {
        System.out.print("Enter amount: ");
        double amt = input.nextDouble();
        acc.deposit(amt);
        saveAccounts();
        System.out.println("Deposit successful!");
    }

    public static void withdraw(BankAccount acc) {
        System.out.print("Enter amount: ");
        double amt = input.nextDouble();

        if (acc.withdraw(amt)) {
            saveAccounts();
            System.out.println("Withdrawal successful!");
        } else {
            System.out.println("Insufficient funds!");
        }
    }

    public static void transfer(BankAccount acc) {
        input.nextLine();
        System.out.print("Enter receiver BVN: ");
        String receiverBVN = input.nextLine();

        if (!accounts.containsKey(receiverBVN)) {
            System.out.println("Receiver not found!");
            return;
        }

        System.out.print("Enter amount: ");
        double amt = input.nextDouble();

        BankAccount receiver = accounts.get(receiverBVN);

        if (acc.getBalance() >= amt) {
            acc.transfer(receiver, amt);
            saveAccounts();
            System.out.println("Transfer successful!");
        } else {
            System.out.println("Insufficient balance!");
        }
    }
}
