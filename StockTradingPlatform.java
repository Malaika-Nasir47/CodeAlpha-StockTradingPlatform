
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


// ======================================================
// STOCK CLASS
// ======================================================
class Stock {

    private String symbol;
    private String companyName;
    private double price;

    public Stock(String symbol, String companyName, double price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}


// ======================================================
// TRANSACTION CLASS
// ======================================================
class Transaction {

    private String type;
    private String stockSymbol;
    private int quantity;
    private double price;

    public Transaction(
            String type,
            String stockSymbol,
            int quantity,
            double price) {

        this.type = type;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
    }

    public void displayTransaction() {

        double total = quantity * price;

        System.out.printf(
                "%-8s %-8s %-10d $%-10.2f Total: $%.2f%n",
                type,
                stockSymbol,
                quantity,
                price,
                total
        );
    }

    public String toFileString() {

        return type + "," +
                stockSymbol + "," +
                quantity + "," +
                price;
    }
}


// ======================================================
// USER CLASS
// ======================================================
class User {

    private String name;
    private double balance;

    private ArrayList<Stock> portfolio;
    private ArrayList<Integer> quantities;

    private ArrayList<Transaction> transactions;

    private final String PORTFOLIO_FILE = "portfolio.txt";
    private final String TRANSACTION_FILE = "transactions.txt";


    // ==================================================
    // CONSTRUCTOR
    // ==================================================
    public User(String name, double balance) {

        this.name = name;
        this.balance = balance;

        portfolio = new ArrayList<>();
        quantities = new ArrayList<>();

        transactions = new ArrayList<>();

        loadPortfolio();
        loadTransactions();
    }


    // ==================================================
    // BUY STOCK
    // ==================================================
    public void buyStock(Stock stock, int quantity) {

        if (quantity <= 0) {

            System.out.println(
                    "Quantity must be greater than 0."
            );

            return;
        }


        double totalCost =
                stock.getPrice() * quantity;


        if (totalCost > balance) {

            System.out.println(
                    "Insufficient balance!"
            );

            System.out.printf(
                    "Required: $%.2f%n",
                    totalCost
            );

            System.out.printf(
                    "Available: $%.2f%n",
                    balance
            );

            return;
        }


        balance -= totalCost;


        int index =
                findStockIndex(stock.getSymbol());


        if (index >= 0) {

            int oldQuantity =
                    quantities.get(index);

            quantities.set(
                    index,
                    oldQuantity + quantity
            );

        } else {

            portfolio.add(stock);
            quantities.add(quantity);
        }


        Transaction transaction =
                new Transaction(
                        "BUY",
                        stock.getSymbol(),
                        quantity,
                        stock.getPrice()
                );


        transactions.add(transaction);


        savePortfolio();
        saveTransactions();


        System.out.println();
        System.out.println(
                "Stock purchased successfully!"
        );

        System.out.printf(
                "Stock: %s%n",
                stock.getSymbol()
        );

        System.out.printf(
                "Quantity: %d%n",
                quantity
        );

        System.out.printf(
                "Total Cost: $%.2f%n",
                totalCost
        );

        System.out.printf(
                "Remaining Balance: $%.2f%n",
                balance
        );
    }


    // ==================================================
    // SELL STOCK
    // ==================================================
    public void sellStock(Stock stock, int quantity) {

        if (quantity <= 0) {

            System.out.println(
                    "Quantity must be greater than 0."
            );

            return;
        }


        int index =
                findStockIndex(stock.getSymbol());


        if (index == -1) {

            System.out.println(
                    "You do not own this stock."
            );

            return;
        }


        int ownedQuantity =
                quantities.get(index);


        if (quantity > ownedQuantity) {

            System.out.println(
                    "You do not have enough shares."
            );

            System.out.println(
                    "You currently own: "
                            + ownedQuantity
            );

            return;
        }


        double totalValue =
                stock.getPrice() * quantity;


        balance += totalValue;


        int remaining =
                ownedQuantity - quantity;


        if (remaining == 0) {

            portfolio.remove(index);
            quantities.remove(index);

        } else {

            quantities.set(
                    index,
                    remaining
            );
        }


        Transaction transaction =
                new Transaction(
                        "SELL",
                        stock.getSymbol(),
                        quantity,
                        stock.getPrice()
                );


        transactions.add(transaction);


        savePortfolio();
        saveTransactions();


        System.out.println();
        System.out.println(
                "Stock sold successfully!"
        );

        System.out.printf(
                "Stock: %s%n",
                stock.getSymbol()
        );

        System.out.printf(
                "Quantity: %d%n",
                quantity
        );

        System.out.printf(
                "Total Received: $%.2f%n",
                totalValue
        );

        System.out.printf(
                "Current Balance: $%.2f%n",
                balance
        );
    }


    // ==================================================
    // FIND STOCK IN PORTFOLIO
    // ==================================================
    private int findStockIndex(String symbol) {

        for (int i = 0; i < portfolio.size(); i++) {

            if (portfolio.get(i)
                    .getSymbol()
                    .equalsIgnoreCase(symbol)) {

                return i;
            }
        }

        return -1;
    }


    // ==================================================
    // DISPLAY PORTFOLIO
    // ==================================================
    public void displayPortfolio() {

        System.out.println();
        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                    MY PORTFOLIO"
        );

        System.out.println(
                "======================================================"
        );


        System.out.printf(
                "Cash Balance: $%.2f%n",
                balance
        );


        if (portfolio.isEmpty()) {

            System.out.println();
            System.out.println(
                    "Portfolio is empty."
            );

            return;
        }


        double currentValue = 0;
        double investedValue = 0;


        System.out.println();

        System.out.printf(
                "%-10s %-18s %-10s %-12s%n",
                "Symbol",
                "Company",
                "Quantity",
                "Value"
        );

        System.out.println(
                "------------------------------------------------------"
        );


        for (int i = 0; i < portfolio.size(); i++) {

            Stock stock =
                    portfolio.get(i);

            int quantity =
                    quantities.get(i);


            double value =
                    stock.getPrice() * quantity;


            currentValue += value;


            /*
             * For this simulation, the current
             * transaction history is used to
             * calculate the amount spent on
             * currently owned shares.
             */
            double estimatedInvested =
                    calculateInvestedValue(
                            stock.getSymbol()
                    );


            investedValue += estimatedInvested;


            System.out.printf(
                    "%-10s %-18s %-10d $%.2f%n",
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    quantity,
                    value
            );
        }


        System.out.println(
                "------------------------------------------------------"
        );


        double profitLoss =
                currentValue - investedValue;


        double profitPercentage = 0;


        if (investedValue > 0) {

            profitPercentage =
                    (profitLoss / investedValue) * 100;
        }


        System.out.printf(
                "Total Invested:       $%.2f%n",
                investedValue
        );


        System.out.printf(
                "Current Stock Value:  $%.2f%n",
                currentValue
        );


        System.out.printf(
                "Profit / Loss:        $%.2f%n",
                profitLoss
        );


        System.out.printf(
                "Profit / Loss %%:      %.2f%%%n",
                profitPercentage
        );


        System.out.printf(
                "Total Portfolio Value: $%.2f%n",
                balance + currentValue
        );
    }


    // ==================================================
    // CALCULATE INVESTED VALUE
    // ==================================================
    private double calculateInvestedValue(
            String symbol) {

        double invested = 0;


        /*
         * Go through transactions and calculate
         * the net cost of the currently held stock.
         */
        for (Transaction transaction :
                transactions) {

            /*
             * This method cannot directly access
             * private transaction fields.
             *
             * The file-based calculation is therefore
             * handled separately below.
             */
        }


        /*
         * For simplicity, calculate the original
         * purchase cost from the portfolio file.
         */
        File file =
                new File(PORTFOLIO_FILE);


        if (!file.exists()) {

            return 0;
        }


        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(file)
                    );


            reader.readLine();
            reader.readLine();


            String line;


            while ((line = reader.readLine())
                    != null) {

                String[] data =
                        line.split(",");


                if (data.length == 4 &&
                        data[0].equalsIgnoreCase(symbol)) {

                    double originalPrice =
                            Double.parseDouble(data[2]);

                    int quantity =
                            Integer.parseInt(data[3]);


                    invested =
                            originalPrice * quantity;


                    break;
                }
            }


            reader.close();


        } catch (IOException |
                 NumberFormatException e) {

            System.out.println(
                    "Error calculating investment."
            );
        }


        return invested;
    }


    // ==================================================
    // DISPLAY TRANSACTIONS
    // ==================================================
    public void displayTransactions() {

        System.out.println();
        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                 TRANSACTION HISTORY"
        );

        System.out.println(
                "======================================================"
        );


        if (transactions.isEmpty()) {

            System.out.println(
                    "No transactions yet."
            );

            return;
        }


        System.out.printf(
                "%-8s %-8s %-10s %-12s %s%n",
                "Type",
                "Stock",
                "Quantity",
                "Price",
                "Total"
        );


        System.out.println(
                "------------------------------------------------------"
        );


        for (Transaction transaction :
                transactions) {

            transaction.displayTransaction();
        }
    }


    // ==================================================
    // SAVE PORTFOLIO
    // ==================================================
    private void savePortfolio() {

        try {

            PrintWriter writer =
                    new PrintWriter(
                            new FileWriter(
                                    PORTFOLIO_FILE
                            )
                    );


            writer.println(name);
            writer.println(balance);


            for (int i = 0;
                 i < portfolio.size();
                 i++) {

                Stock stock =
                        portfolio.get(i);

                int quantity =
                        quantities.get(i);


                writer.println(
                        stock.getSymbol()
                                + ","
                                + stock.getCompanyName()
                                + ","
                                + stock.getPrice()
                                + ","
                                + quantity
                );
            }


            writer.close();


        } catch (IOException e) {

            System.out.println(
                    "Error saving portfolio."
            );
        }
    }


    // ==================================================
    // LOAD PORTFOLIO
    // ==================================================
    private void loadPortfolio() {

        File file =
                new File(PORTFOLIO_FILE);


        if (!file.exists()) {

            return;
        }


        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(file)
                    );


            reader.readLine();


            String balanceLine =
                    reader.readLine();


            if (balanceLine != null) {

                balance =
                        Double.parseDouble(
                                balanceLine
                        );
            }


            String line;


            while ((line = reader.readLine())
                    != null) {

                String[] data =
                        line.split(",");


                if (data.length == 4) {

                    String symbol =
                            data[0];

                    String company =
                            data[1];

                    double price =
                            Double.parseDouble(
                                    data[2]
                            );

                    int quantity =
                            Integer.parseInt(
                                    data[3]
                            );


                    Stock stock =
                            new Stock(
                                    symbol,
                                    company,
                                    price
                            );


                    portfolio.add(stock);

                    quantities.add(quantity);
                }
            }


            reader.close();


        } catch (IOException |
                 NumberFormatException e) {

            System.out.println(
                    "Error loading portfolio."
            );
        }
    }


    // ==================================================
    // SAVE TRANSACTIONS
    // ==================================================
    private void saveTransactions() {

        try {

            PrintWriter writer =
                    new PrintWriter(
                            new FileWriter(
                                    TRANSACTION_FILE
                            )
                    );


            for (Transaction transaction :
                    transactions) {

                writer.println(
                        transaction.toFileString()
                );
            }


            writer.close();


        } catch (IOException e) {

            System.out.println(
                    "Error saving transactions."
            );
        }
    }


    // ==================================================
    // LOAD TRANSACTIONS
    // ==================================================
    private void loadTransactions() {

        File file =
                new File(TRANSACTION_FILE);


        if (!file.exists()) {

            return;
        }


        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader(file)
                    );


            String line;


            while ((line = reader.readLine())
                    != null) {

                String[] data =
                        line.split(",");


                if (data.length == 4) {

                    String type =
                            data[0];

                    String symbol =
                            data[1];

                    int quantity =
                            Integer.parseInt(
                                    data[2]
                            );

                    double price =
                            Double.parseDouble(
                                    data[3]
                            );


                    transactions.add(
                            new Transaction(
                                    type,
                                    symbol,
                                    quantity,
                                    price
                            )
                    );
                }
            }


            reader.close();


        } catch (IOException |
                 NumberFormatException e) {

            System.out.println(
                    "Error loading transactions."
            );
        }
    }
}


// ======================================================
// MAIN CLASS
// ======================================================
public class StockTradingPlatform {


    // ==================================================
    // DISPLAY MARKET
    // ==================================================
    public static void displayMarket(
            ArrayList<Stock> stocks) {

        System.out.println();
        System.out.println(
                "======================================================"
        );

        System.out.println(
                "                    STOCK MARKET"
        );

        System.out.println(
                "======================================================"
        );


        System.out.printf(
                "%-10s %-20s %-12s%n",
                "Symbol",
                "Company",
                "Price"
        );


        System.out.println(
                "------------------------------------------------------"
        );


        for (Stock stock : stocks) {

            System.out.printf(
                    "%-10s %-20s $%.2f%n",
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    stock.getPrice()
            );
        }


        System.out.println(
                "------------------------------------------------------"
        );
    }


    // ==================================================
    // UPDATE STOCK PRICE
    // ==================================================
    public static void updateStockPrice(
            ArrayList<Stock> stocks,
            Scanner scanner) {

        displayMarket(stocks);


        System.out.print(
                "\nEnter stock symbol to update: "
        );


        String symbol =
                scanner.nextLine()
                        .trim();


        Stock stock =
                findStock(
                        stocks,
                        symbol
                );


        if (stock == null) {

            System.out.println(
                    "Stock not found!"
            );

            return;
        }


        System.out.printf(
                "Current price of %s: $%.2f%n",
                stock.getSymbol(),
                stock.getPrice()
        );


        System.out.print(
                "Enter new price: $"
        );


        String priceInput =
                scanner.nextLine()
                        .trim();


        double newPrice;


        try {

            newPrice =
                    Double.parseDouble(
                            priceInput
                    );

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid price!"
            );

            return;
        }


        if (newPrice <= 0) {

            System.out.println(
                    "Price must be greater than 0."
            );

            return;
        }


        stock.setPrice(newPrice);


        System.out.println();

        System.out.println(
                "Stock price updated successfully!"
        );


        System.out.printf(
                "%s new price: $%.2f%n",
                stock.getSymbol(),
                stock.getPrice()
        );
    }


    // ==================================================
    // FIND STOCK
    // ==================================================
    public static Stock findStock(
            ArrayList<Stock> stocks,
            String symbol) {

        for (Stock stock : stocks) {

            if (stock.getSymbol()
                    .equalsIgnoreCase(symbol)) {

                return stock;
            }
        }

        return null;
    }


    // ==================================================
    // MAIN
    // ==================================================
    public static void main(String[] args) {

        Scanner scanner =
                new Scanner(System.in);


        // ==============================================
        // STOCK MARKET
        // ==============================================

        ArrayList<Stock> stocks =
                new ArrayList<>();


        stocks.add(
                new Stock(
                        "AAPL",
                        "Apple",
                        180.00
                )
        );


        stocks.add(
                new Stock(
                        "MSFT",
                        "Microsoft",
                        420.00
                )
        );


        stocks.add(
                new Stock(
                        "GOOG",
                        "Google",
                        175.00
                )
        );


        stocks.add(
                new Stock(
                        "AMZN",
                        "Amazon",
                        185.00
                )
        );


        // ==============================================
        // USER
        // ==============================================

        User user =
                new User(
                        "Malaika",
                        10000.00
                );


        // ==============================================
        // MENU
        // ==============================================

        while (true) {

            System.out.println();

            System.out.println(
                    "======================================================"
            );

            System.out.println(
                    "              STOCK TRADING PLATFORM"
            );

            System.out.println(
                    "======================================================"
            );

            System.out.println(
                    "1. Display Market"
            );

            System.out.println(
                    "2. Update Stock Prices"
            );

            System.out.println(
                    "3. Buy Stock"
            );

            System.out.println(
                    "4. Sell Stock"
            );

            System.out.println(
                    "5. View Portfolio"
            );

            System.out.println(
                    "6. View Transactions"
            );

            System.out.println(
                    "7. Exit"
            );

            System.out.println(
                    "======================================================"
            );


            System.out.print(
                    "Enter your choice: "
            );


            // Read the complete line
            String input =
                    scanner.nextLine()
                            .trim();


            // Handle Enter
            if (input.isEmpty()) {

                System.out.println(
                        "Please enter a choice from 1 to 7."
                );

                continue;
            }


            int choice;


            try {

                choice =
                        Integer.parseInt(input);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid input! Please enter a number from 1 to 7."
                );

                continue;
            }


            // ==========================================
            // OPTION 1
            // ==========================================

            if (choice == 1) {

                displayMarket(stocks);
            }


            // ==========================================
            // OPTION 2
            // ==========================================

            else if (choice == 2) {

                updateStockPrice(
                        stocks,
                        scanner
                );
            }


            // ==========================================
            // OPTION 3 - BUY
            // ==========================================

            else if (choice == 3) {

                displayMarket(stocks);


                System.out.print(
                        "\nEnter stock symbol to buy: "
                );


                String symbol =
                        scanner.nextLine()
                                .trim();


                Stock stock =
                        findStock(
                                stocks,
                                symbol
                        );


                if (stock == null) {

                    System.out.println(
                            "Stock not found!"
                    );

                    continue;
                }


                System.out.print(
                        "Enter quantity: "
                );


                String quantityInput =
                        scanner.nextLine()
                                .trim();


                int quantity;


                try {

                    quantity =
                            Integer.parseInt(
                                    quantityInput
                            );

                } catch (NumberFormatException e) {

                    System.out.println(
                            "Invalid quantity!"
                    );

                    continue;
                }


                user.buyStock(
                        stock,
                        quantity
                );
            }


            // ==========================================
            // OPTION 4 - SELL
            // ==========================================

            else if (choice == 4) {

                displayMarket(stocks);


                System.out.print(
                        "\nEnter stock symbol to sell: "
                );


                String symbol =
                        scanner.nextLine()
                                .trim();


                Stock stock =
                        findStock(
                                stocks,
                                symbol
                        );


                if (stock == null) {

                    System.out.println(
                            "Stock not found!"
                    );

                    continue;
                }


                System.out.print(
                        "Enter quantity: "
                );


                String quantityInput =
                        scanner.nextLine()
                                .trim();


                int quantity;


                try {

                    quantity =
                            Integer.parseInt(
                                    quantityInput
                            );

                } catch (NumberFormatException e) {

                    System.out.println(
                            "Invalid quantity!"
                    );

                    continue;
                }


                user.sellStock(
                        stock,
                        quantity
                );
            }


            // ==========================================
            // OPTION 5 - PORTFOLIO
            // ==========================================

            else if (choice == 5) {

                user.displayPortfolio();
            }


            // ==========================================
            // OPTION 6 - TRANSACTIONS
            // ==========================================

            else if (choice == 6) {

                user.displayTransactions();
            }


            // ==========================================
            // OPTION 7 - EXIT
            // ==========================================

            else if (choice == 7) {

                System.out.println();

                System.out.println(
                        "Your data has been saved."
                );

                System.out.println(
                        "Thank you for using "
                                + "Stock Trading Platform!"
                );

                break;
            }


            // ==========================================
            // INVALID
            // ==========================================

            else {

                System.out.println(
                        "Invalid choice!"
                );

                System.out.println(
                        "Please select a number from 1 to 7."
                );
            }
        }


        scanner.close();
    }
}

