// ============================
// Custom Exception Classes
// ============================
class NegativeDepositException extends Exception {
    public NegativeDepositException(String message) {
        super(message);
    }
}

class OverdrawException extends Exception {
    public OverdrawException(String message) {
        super(message);
    }
}

class InvalidAccountOperationException extends Exception {
    public InvalidAccountOperationException(String message) {
        super(message);
    }
}

// ============================
// Observer Pattern - Define Observer Interface
// ============================
interface Observer {
    void update(String message);
}

// TransactionLogger - Concrete Observer
class TransactionLogger implements Observer {
    public void update(String message) {
        System.out.println("[Transaction Log] " + message);
    }
}

// ============================
// BankAccount (Subject in Observer Pattern)
// ============================
import java.util.ArrayList;
import java.util.List;

class BankAccount {
    protected String accountNumber;
    protected double balance;
    protected boolean isActive;
    private List<Observer> observers = new ArrayList<>();

    public BankAccount(String accNum, double initialBalance) {
        this.accountNumber = accNum;
        this.balance = initialBalance;
        this.isActive = true;
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    private void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    public void deposit(double amount) throws NegativeDepositException, InvalidAccountOperationException {
        if (!isActive) throw new InvalidAccountOperationException("Account is closed.");
        if (amount < 0) throw new NegativeDepositException("Cannot deposit a negative amount.");
        balance += amount;
        notifyObservers("Deposited: $" + amount);
    }

    public void withdraw(double amount) throws OverdrawException, InvalidAccountOperationException {
        if (!isActive) throw new InvalidAccountOperationException("Account is closed.");
        if (amount > balance) throw new OverdrawException("Insufficient funds.");
        balance -= amount;
        notifyObservers("Withdrew: $" + amount);
    }

    public double getBalance() {
        return balance;
    }

    public void closeAccount() {
        isActive = false;
        notifyObservers("Account has been closed.");
    }
}

// ============================
// Decorator Pattern - SecureBankAccount
// ============================
abstract class BankAccountDecorator extends BankAccount {
    protected BankAccount decoratedAccount;

    public BankAccountDecorator(BankAccount account) {
        super(account.accountNumber, account.getBalance());
        this.decoratedAccount = account;
    }

    @Override
    public void addObserver(Observer observer) {
        decoratedAccount.addObserver(observer);
    }

    @Override
    public double getBalance() {
        return decoratedAccount.getBalance();
    }

    @Override
    public void closeAccount() {
        decoratedAccount.closeAccount();
    }
}

class SecureBankAccount extends BankAccountDecorator {
    public SecureBankAccount(BankAccount account) {
        super(account);
    }

    @Override
    public void withdraw(double amount) throws Exception {
        if (amount > 500) {
            throw new Exception("Cannot withdraw more than $500 in one transaction.");
        }
        decoratedAccount.withdraw(amount);
    }

    @Override
    public void deposit(double amount) throws Exception {
        decoratedAccount.deposit(amount);
    }
}

// ============================
// Main Program
// ============================
import java.util.Scanner;

public class BankAccountTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter initial balance: ");
            double initialBalance = scanner.nextDouble();
            BankAccount account = new BankAccount("123456", initialBalance);
            System.out.println("Bank Account Created: #123456");

            TransactionLogger logger = new TransactionLogger();
            account.addObserver(logger);

            BankAccount secureAccount = new SecureBankAccount(account);

            System.out.print("Enter amount to deposit: ");
            double depositAmount = scanner.nextDouble();
            secureAccount.deposit(depositAmount);

            System.out.print("Enter amount to withdraw: ");
            double withdrawAmount = scanner.nextDouble();
            secureAccount.withdraw(withdrawAmount);

            System.out.println("Final Balance: $" + secureAccount.getBalance());

        } catch (NegativeDepositException | OverdrawException | InvalidAccountOperationException e) {
            System.err.println("Transaction Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
