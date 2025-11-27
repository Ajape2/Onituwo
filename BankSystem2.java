import java.io.*;
import java.util.*;

class BankAccount {
    String name;
    String email;
    String phone;
    String bvn;
    String accountNumber;
    String accountType;
    String pin;
    double balance;

    public BankAccount(String name, String email, String phone, String bvn,
                       String accNum, String accType, String pin, double balance) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bvn = bvn;
        this.accountNumber = accNum;
        this.accountType = accType;
        this.pin = pin;
        this.balance = balance;
    }

    public String toCSV() {
        return name + "," + email + "," + phone + "," + bvn + "," +
                accountNumber + "," + accountType + "," + pin + "," + balance;
    }

    public static BankAccount fromCSV(String line) {
        String[] p = line.split(",");
        return new BankAccount(p[0], p[1], p[2], p[3], p[4], p[5], p[6],
                Double.parseDouble(p[7]));
    }
}

public class BankSystem2 {

    static Scanner input = new Scanner(System.in);
    static ArrayList<BankAccount> accounts = new ArrayList<>();
    static final String FILE_NAME = "accounts.csv";

    public static void main(String[] args) {
        loadAccounts();
        menu();
        saveAccounts();
    }

    // -------- LOAD ACCOUNTS --------
    static void loadAccounts() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                accounts.add(BankAccount.fromCSV(line));
            }
            br.close();
        } catch (Exception e) {
            System.out.println("Error loading accounts.");
        }
    }

    // -------- SAVE ACCOUNTS --------
    static void saveAccounts() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME));
            for (BankAccount acc : accounts) {
                pw.println(acc.toCSV());
            }
            pw.close();
        } catch (Exception e) {
            System.out.println("Error saving accounts.");
        }
    }

    // -------- MENU --------
    static void menu() {
        int choice;

        do {
            System.out.println("\n=== BANKING SYSTEM ===");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            choice = input.nextInt();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> login();
                case 3 -> System.out.println("Goodbye!");
            }

        } while (choice != 3);
    }

    // -------- CREATE ACCOUNT --------
    static void createAccount() {
        input.nextLine();

        System.out.print("Enter Full Name: ");
        String name = input.nextLine();

        System.out.print("Enter Email: ");
        String email = input.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = input.nextLine();

        System.out.print("Create 4-digit PIN: ");
        String pin = input.nextLine();

        String bvn = generateBVN();
        String accNum = generateAccountNumber();

        System.out.print("Account Type (SAVINGS / CURRENT): ");
        String accType = input.nextLine().toUpperCase();

        BankAccount acc = new BankAccount(
                name, email, phone, bvn, accNum, accType, pin, 0.0
        );

        accounts.add(acc);
        saveAccounts();

        System.out.println("\n=== ACCOUNT CREATED ===");
        System.out.println("Name: " + name);
        System.out.println("BVN: " + bvn);
        System.out.println("Account Number: " + accNum);
        System.out.println("Account Type: " + accType);
        System.out.println("Use your PIN to login.");
    }

    // -------- LOGIN --------
    static void login() {
        input.nextLine();
        System.out.print("Enter PIN: ");
        String pin = input.nextLine();

        BankAccount user = null;

        for (BankAccount acc : accounts) {
            if (acc.pin.equals(pin)) {
                user = acc;
                break;
            }
        }

        if (user == null) {
            System.out.println("Invalid PIN!");
            return;
        }

        dashboard(user);
    }

    // -------- DASHBOARD --------
    static void dashboard(BankAccount acc) {
        int option;

        do {
            System.out.println("\n=== WELCOME " + acc.name + " ===");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Change PIN");
            System.out.println("6. Show Account Details");
            System.out.println("7. Delete Account");
            System.out.println("8. Logout");
            System.out.print("Choose option: ");
            option = input.nextInt();

            switch (option) {
                case 1 -> checkBalance(acc);
                case 2 -> deposit(acc);
                case 3 -> withdraw(acc);
                case 4 -> transfer(acc);
                case 5 -> changePIN(acc);
                case 6 -> showDetails(acc);
                case 7 -> {
                    deleteAccount(acc);
                    return;
                }
            }

            saveAccounts();

        } while (option != 8);
    }

    // -------- BALANCE --------
    static void checkBalance(BankAccount acc) {
        System.out.println("Your Balance: ₦" + acc.balance);
    }

    // -------- DEPOSIT --------
    static void deposit(BankAccount acc) {
        System.out.print("Enter amount: ");
        double amount = input.nextDouble();

        if (amount > 0) {
            acc.balance += amount;
            System.out.println("Deposit successful!");
        } else {
            System.out.println("Invalid amount!");
        }
    }

    // -------- WITHDRAW --------
    static void withdraw(BankAccount acc) {
        System.out.print("Enter amount: ");
        double amount = input.nextDouble();

        if (amount > 0 && amount <= acc.balance) {
            acc.balance -= amount;
            System.out.println("Withdrawal successful!");
        } else {
            System.out.println("Not enough balance!");
        }
    }

    // -------- TRANSFER --------
    static void transfer(BankAccount sender) {
        input.nextLine();

        System.out.print("Enter receiver account number: ");
        String accNo = input.nextLine();

        BankAccount receiver = null;

        for (BankAccount acc : accounts) {
            if (acc.accountNumber.equals(accNo)) {
                receiver = acc;
                break;
            }
        }

        if (receiver == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter amount: ");
        double amount = input.nextDouble();

        if (amount > 0 && sender.balance >= amount) {
            sender.balance -= amount;
            receiver.balance += amount;
            System.out.println("Transfer successful!");
        } else {
            System.out.println("Insufficient funds!");
        }
    }

    // -------- CHANGE PIN (Feature 1) --------
    static void changePIN(BankAccount acc) {
        input.nextLine();
        System.out.print("Enter new PIN: ");
        String newPIN = input.nextLine();

        acc.pin = newPIN;
        System.out.println("PIN changed successfully!");
    }

    // -------- SHOW ACCOUNT DETAILS (Feature 3) --------
    static void showDetails(BankAccount acc) {
        System.out.println("\n=== ACCOUNT DETAILS ===");
        System.out.println("Name: " + acc.name);
        System.out.println("Email: " + acc.email);
        System.out.println("Phone: " + acc.phone);
        System.out.println("BVN: " + acc.bvn);
        System.out.println("Account Number: " + acc.accountNumber);
        System.out.println("Account Type: " + acc.accountType);
        System.out.println("Balance: ₦" + acc.balance);
    }

    // -------- DELETE ACCOUNT (Feature 2) --------
    static void deleteAccount(BankAccount acc) {
        accounts.remove(acc);
        saveAccounts();
        System.out.println("Account deleted successfully!");
    }

    // -------- GENERATE BVN --------
    static String generateBVN() {
        return "22" + (int)(Math.random() * 900000000 + 100000000);
    }

    // -------- GENERATE ACCOUNT NUMBER --------
    static String generateAccountNumber() {
        return "10" + (int)(Math.random() * 900000000 + 100000000);
    }
}
