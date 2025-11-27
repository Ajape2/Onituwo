import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Complete Java Banking System (features 1-9)
 *
 * Files:
 *  - accounts.csv                 (stores accounts)
 *  - transactions_<accountNo>.csv (per-account transaction history)
 *
 * Admin password: admin123
 *
 * Notes:
 *  - Login uses Account Number + PIN
 *  - BVN is auto-generated at account creation but NOT required for login
 *  - PINs are stored in plain text here (for learning/demo). For production, hash them.
 */
public class AdvancedBankSystem3 {

    static final String ACCOUNTS_FILE = "accounts.csv";
    static final Scanner input = new Scanner(System.in);
    static final ArrayList<BankAccount> accounts = new ArrayList<>();
    static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        loadAccounts();
        mainMenu();
        saveAccounts(); // final save on exit
    }

    // ---------------- BankAccount class ----------------
    static class BankAccount {
        String name;
        String email;
        String phone;
        String bvn;
        String accountNumber;
        String accountType; // SAVINGS or CURRENT
        String pin;
        double balance;

        BankAccount(String name, String email, String phone, String bvn,
                    String accountNumber, String accountType, String pin, double balance) {
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.bvn = bvn;
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.pin = pin;
            this.balance = balance;
        }

        // CSV for accounts file
        String toCSV() {
            // Simple CSV; don't include commas in fields for simplicity
            return String.join(",", name, email, phone, bvn, accountNumber, accountType, pin, String.valueOf(balance));
        }

        static BankAccount fromCSV(String line) {
            String[] p = line.split(",", -1);
            if (p.length < 8) return null;
            return new BankAccount(p[0], p[1], p[2], p[3], p[4], p[5], p[6], Double.parseDouble(p[7]));
        }
    }

    // ---------------- Persistence ----------------
    static void saveAccounts() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (BankAccount a : accounts) pw.println(a.toCSV());
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    static void loadAccounts() {
        File f = new File(ACCOUNTS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                BankAccount a = BankAccount.fromCSV(line);
                if (a != null) accounts.add(a);
            }
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    // ---------------- Generators ----------------
    static String generateBVN() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

    static String generateAccountNumber() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) sb.append(r.nextInt(10));
        return sb.toString();
    }

    // ---------------- Transactions logging ----------------
    static void logTransaction(String accNumber, String type, double amount, double before, double after, String note) {
        String fileName = "transactions_" + accNumber + ".csv";
        String timestamp = LocalDateTime.now().format(TF);
        String line = String.join(",", timestamp, type, String.valueOf(amount),
                String.valueOf(before), String.valueOf(after), note == null ? "" : note);
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName, true))) {
            pw.println(line);
        } catch (IOException e) {
            System.out.println("Error logging transaction: " + e.getMessage());
        }
    }

    static void printReceipt(String accNumber, String type, double amount, double before, double after) {
        String ts = LocalDateTime.now().format(TF);
        System.out.println("\n----- TRANSACTION RECEIPT -----");
        System.out.println("Timestamp     : " + ts);
        System.out.println("Account Number: " + accNumber);
        System.out.println("Type          : " + type);
        System.out.println("Amount        : " + amount);
        System.out.println("Balance Before: " + before);
        System.out.println("Balance After : " + after);
        System.out.println("-------------------------------\n");
    }

    // ---------------- Main menu ----------------
    static void mainMenu() {
        while (true) {
            System.out.println("\n=== WELCOME TO ADVANCED JAVA BANK ===");
            System.out.println("1. Create Account");
            System.out.println("2. Login (AccountNo + PIN)");
            System.out.println("3. Forgot PIN");
            System.out.println("4. Admin Dashboard");
            System.out.println("5. Exit");
            System.out.print("Choose: ");
            String ch = input.nextLine().trim();

            switch (ch) {
                case "1" -> createAccount();
                case "2" -> userLogin();
                case "3" -> forgotPin();
                case "4" -> adminLogin();
                case "5" -> {
                    saveAccounts();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ---------------- Create account ----------------
    static void createAccount() {
        System.out.println("\n--- Create Account ---");
        System.out.print("Full name: ");
        String name = input.nextLine().trim();
        System.out.print("Email: ");
        String email = input.nextLine().trim();
        System.out.print("Phone: ");
        String phone = input.nextLine().trim();

        String accType;
        while (true) {
            System.out.print("Account Type (SAVINGS/CURRENT): ");
            accType = input.nextLine().trim().toUpperCase();
            if (accType.equals("SAVINGS") || accType.equals("CURRENT")) break;
            System.out.println("Type must be SAVINGS or CURRENT.");
        }

        String pin;
        while (true) {
            System.out.print("Set 4-digit PIN: ");
            pin = input.nextLine().trim();
            if (pin.matches("\\d{4}")) break;
            System.out.println("PIN must be exactly 4 digits.");
        }

        System.out.print("Initial deposit amount (numbers only): ");
        double initBalance = readDouble();

        String bvn = generateBVN();
        String accNo = generateAccountNumber();

        BankAccount a = new BankAccount(name, email, phone, bvn, accNo, accType, pin, initBalance);
        accounts.add(a);
        saveAccounts();
        System.out.println("\nAccount created successfully!");
        System.out.println("Name: " + name);
        System.out.println("BVN: " + bvn + "  (kept for your record)");
        System.out.println("Account Number: " + accNo);
        System.out.println("Use Account Number + PIN to login.");
        logTransaction(accNo, "ACCOUNT_OPEN", initBalance, 0.0, initBalance, "Initial deposit");
    }

    // ---------------- Login ----------------
    static void userLogin() {
        System.out.println("\n--- User Login ---");
        System.out.print("Account Number: ");
        String accNo = input.nextLine().trim();
        BankAccount acc = findByAccountNumber(accNo);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("PIN: ");
        String pin = input.nextLine().trim();
        if (!acc.pin.equals(pin)) {
            System.out.println("Incorrect PIN.");
            return;
        }
        userMenu(acc);
    }

    // ---------------- User menu (logged in) ----------------
    static void userMenu(BankAccount acc) {
        while (true) {
            System.out.println("\n--- Welcome, " + acc.name + " ---");
            System.out.println("1. Show Account Details");
            System.out.println("2. Check Balance");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Transfer");
            System.out.println("6. Change PIN");
            System.out.println("7. Delete Account");
            System.out.println("8. Logout");
            System.out.print("Choose: ");
            String ch = input.nextLine().trim();

            switch (ch) {
                case "1" -> showAccountDetails(acc);
                case "2" -> System.out.println("Balance: ₦" + acc.balance);
                case "3" -> doDeposit(acc);
                case "4" -> doWithdraw(acc);
                case "5" -> doTransfer(acc);
                case "6" -> changePin(acc);
                case "7" -> {
                    if (confirmAction("Are you sure you want to DELETE your account? This cannot be undone (yes/no): ")) {
                        deleteAccount(acc);
                        return; // logged out after deletion
                    }
                }
                case "8" -> {
                    System.out.println("Logged out.");
                    saveAccounts();
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // ---------------- Show details ----------------
    static void showAccountDetails(BankAccount a) {
        System.out.println("\n--- Account Details ---");
        System.out.println("Name          : " + a.name);
        System.out.println("Email         : " + a.email);
        System.out.println("Phone         : " + a.phone);
        System.out.println("BVN           : " + a.bvn);
        System.out.println("Account No.   : " + a.accountNumber);
        System.out.println("Account Type  : " + a.accountType);
        System.out.println("Balance       : ₦" + a.balance);
    }

    // ---------------- Deposit ----------------
    static void doDeposit(BankAccount a) {
        System.out.print("Amount to deposit: ");
        double amt = readDouble();
        if (amt <= 0) { System.out.println("Invalid amount."); return; }
        double before = a.balance;
        a.balance += amt;
        double after = a.balance;
        saveAccounts();
        logTransaction(a.accountNumber, "DEPOSIT", amt, before, after, "");
        printReceipt(a.accountNumber, "DEPOSIT", amt, before, after);
        System.out.println("Deposit successful.");
    }

    // ---------------- Withdraw ----------------
    static void doWithdraw(BankAccount a) {
        System.out.print("Amount to withdraw: ");
        double amt = readDouble();
        if (amt <= 0) { System.out.println("Invalid amount."); return; }
        if (amt > a.balance) { System.out.println("Insufficient funds."); return; }
        double before = a.balance;
        a.balance -= amt;
        double after = a.balance;
        saveAccounts();
        logTransaction(a.accountNumber, "WITHDRAW", amt, before, after, "");
        printReceipt(a.accountNumber, "WITHDRAW", amt, before, after);
        System.out.println("Withdrawal successful.");
    }

    // ---------------- Transfer ----------------
    static void doTransfer(BankAccount sender) {
        System.out.print("Receiver Account Number: ");
        String rAccNo = input.nextLine().trim();
        BankAccount receiver = findByAccountNumber(rAccNo);
        if (receiver == null) { System.out.println("Receiver not found."); return; }
        System.out.print("Amount to transfer: ");
        double amt = readDouble();
        if (amt <= 0) { System.out.println("Invalid amount."); return; }
        if (amt > sender.balance) { System.out.println("Insufficient funds."); return; }

        double sBefore = sender.balance;
        double rBefore = receiver.balance;

        sender.balance -= amt;
        receiver.balance += amt;

        saveAccounts();
        logTransaction(sender.accountNumber, "TRANSFER_OUT", amt, sBefore, sender.balance, "To " + receiver.accountNumber);
        logTransaction(receiver.accountNumber, "TRANSFER_IN", amt, rBefore, receiver.balance, "From " + sender.accountNumber);

        printReceipt(sender.accountNumber, "TRANSFER_OUT", amt, sBefore, sender.balance);
        System.out.println("Transfer successful.");
    }

    // ---------------- Change PIN ----------------
    static void changePin(BankAccount a) {
        System.out.print("Enter current PIN: ");
        String cur = input.nextLine().trim();
        if (!cur.equals(a.pin)) { System.out.println("Incorrect current PIN."); return; }
        String np;
        while (true) {
            System.out.print("Enter new 4-digit PIN: ");
            np = input.nextLine().trim();
            if (np.matches("\\d{4}")) break;
            System.out.println("PIN must be exactly 4 digits.");
        }
        a.pin = np;
        saveAccounts();
        System.out.println("PIN changed successfully.");
    }

    // ---------------- Delete account (user) ----------------
    static void deleteAccount(BankAccount a) {
        if (!accounts.remove(a)) {
            System.out.println("Error deleting account.");
            return;
        }
        saveAccounts();
        // Optionally archive or delete transaction file:
        String txFile = "transactions_" + a.accountNumber + ".csv";
        File f = new File(txFile);
        if (f.exists()) f.delete();
        System.out.println("Account deleted.");
    }

    // ---------------- Forgot PIN ----------------
    static void forgotPin() {
        System.out.println("\n--- Forgot PIN ---");
        System.out.print("Enter Account Number: ");
        String accNo = input.nextLine().trim();
        BankAccount a = findByAccountNumber(accNo);
        if (a == null) { System.out.println("Account not found."); return; }

        System.out.println("Verify using either registered Email or Phone.");
        System.out.print("Enter your registered email OR phone: ");
        String proof = input.nextLine().trim();

        if (proof.equalsIgnoreCase(a.email) || proof.equals(a.phone)) {
            String newPin;
            while (true) {
                System.out.print("Enter new 4-digit PIN: ");
                newPin = input.nextLine().trim();
                if (newPin.matches("\\d{4}")) break;
                System.out.println("PIN must be 4 digits.");
            }
            a.pin = newPin;
            saveAccounts();
            System.out.println("PIN reset successful. Use Account Number + new PIN to login.");
        } else {
            System.out.println("Verification failed. Email or phone does not match.");
        }
    }

    // ---------------- Admin login ----------------
    static void adminLogin() {
        System.out.print("Enter admin password: ");
        String pw = input.nextLine().trim();
        if (!pw.equals(ADMIN_PASSWORD)) {
            System.out.println("Incorrect admin password.");
            return;
        }
        adminMenu();
    }

    // ---------------- Admin menu ----------------
    static void adminMenu() {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. View all accounts");
            System.out.println("2. Search account (AccountNo or BVN)");
            System.out.println("3. Delete account");
            System.out.println("4. View account transactions");
            System.out.println("5. Total bank balance");
            System.out.println("6. Apply interest to SAVINGS accounts");
            System.out.println("7. Back to main menu");
            System.out.print("Choose: ");
            String ch = input.nextLine().trim();

            switch (ch) {
                case "1" -> viewAllAccounts();
                case "2" -> searchAccount();
                case "3" -> adminDeleteAccount();
                case "4" -> adminViewTransactions();
                case "5" -> totalBankBalance();
                case "6" -> applyInterest();
                case "7" -> { saveAccounts(); return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void viewAllAccounts() {
        System.out.println("\n--- ALL ACCOUNTS ---");
        if (accounts.isEmpty()) { System.out.println("No accounts found."); return; }
        for (BankAccount a : accounts) {
            System.out.printf("Name: %s | AccNo: %s | BVN: %s | Type: %s | Balance: ₦%.2f%n",
                    a.name, a.accountNumber, a.bvn, a.accountType, a.balance);
        }
    }

    static void searchAccount() {
        System.out.print("Enter Account Number or BVN: ");
        String key = input.nextLine().trim();
        BankAccount found = null;
        for (BankAccount a : accounts) {
            if (a.accountNumber.equals(key) || a.bvn.equals(key)) { found = a; break; }
        }
        if (found == null) System.out.println("Account not found.");
        else showAccountDetails(found);
    }

    static void adminDeleteAccount() {
        System.out.print("Enter Account Number to delete: ");
        String accNo = input.nextLine().trim();
        BankAccount a = findByAccountNumber(accNo);
        if (a == null) { System.out.println("Not found."); return; }
        if (confirmAction("Are you sure you want to delete account " + accNo + " (yes/no): ")) {
            accounts.remove(a);
            saveAccounts();
            File f = new File("transactions_" + accNo + ".csv");
            if (f.exists()) f.delete();
            System.out.println("Account deleted.");
        }
    }

    static void adminViewTransactions() {
        System.out.print("Enter Account Number: ");
        String accNo = input.nextLine().trim();
        File f = new File("transactions_" + accNo + ".csv");
        if (!f.exists()) {
            System.out.println("No transactions found for this account.");
            return;
        }
        System.out.println("\n--- TRANSACTIONS for " + accNo + " ---");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                // timestamp,type,amount,before,after,note
                String[] p = line.split(",", -1);
                System.out.printf("%s | %s | %s | before=%s after=%s | %s%n", p[0], p[1], p[2], p[3], p[4], p.length > 5 ? p[5] : "");
            }
        } catch (IOException e) {
            System.out.println("Error reading transactions: " + e.getMessage());
        }
    }

    static void totalBankBalance() {
        double total = 0;
        for (BankAccount a : accounts) total += a.balance;
        System.out.printf("Total bank balance across all accounts: ₦%.2f%n", total);
    }

    // ---------------- Apply interest (admin) ----------------
    static void applyInterest() {
        System.out.print("Enter annual interest rate percent to apply to SAVINGS (e.g. 1.5): ");
        double rate = readDouble(); // percent
        if (rate <= 0) { System.out.println("Rate must be positive."); return; }
        System.out.print("Apply now? This will update balances immediately (yes/no): ");
        String ok = input.nextLine().trim().toLowerCase();
        if (!ok.equals("yes")) { System.out.println("Cancelled."); return; }

        for (BankAccount a : accounts) {
            if ("SAVINGS".equalsIgnoreCase(a.accountType)) {
                double before = a.balance;
                // For simplicity: apply rate as a single-period percentage (not compounding)
                double interest = before * (rate / 100.0);
                a.balance += interest;
                logTransaction(a.accountNumber, "INTEREST", interest, before, a.balance, "Interest applied: " + rate + "%");
            }
        }
        saveAccounts();
        System.out.println("Interest applied to all SAVINGS accounts.");
    }

    // ---------------- Helpers ----------------
    static BankAccount findByAccountNumber(String accNo) {
        for (BankAccount a : accounts) if (a.accountNumber.equals(accNo)) return a;
        return null;
    }

    static boolean confirmAction(String prompt) {
        System.out.print(prompt);
        String r = input.nextLine().trim().toLowerCase();
        return r.equals("yes");
    }

    static double readDouble() {
        while (true) {
            String s = input.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}
