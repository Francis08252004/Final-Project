import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

// User class to store user details
class User {
    String username;
    String fullName;
    String contactNo;
    String password;

    public User(String username, String fullName, String contactNo, String password) {
        this.username = username;
        this.fullName = fullName;
        this.contactNo = contactNo;
        this.password = password;
    }
}

// Item class to represent grocery items
class Item {
    String name;
    double price;
    String unit;
    int stock;

    public Item(String name, double price, String unit, int stock) {
        this.name = name;
        this.price = price;
        this.unit = unit;
        this.stock = stock;
    }
}

// ShoppingCart class to manage items added to cart
class ShoppingCart {
    Item[] items;
    int[] quantities;
    int currentIndex;

    public ShoppingCart(int size) {
        items = new Item[size];
        quantities = new int[size];
        currentIndex = 0;
    }

    // Method to add an item to the shopping cart
    public void addItem(Item item, int quantity) {
        if (quantity > item.stock) {
            JOptionPane.showMessageDialog(null, "Not enough stock for " + item.name + ". Available: " + item.stock);
            return;
        }
        items[currentIndex] = item;
        quantities[currentIndex] = quantity;
        item.stock -= quantity; // Deduct the quantity from available stock
        currentIndex++;
    }

    // Method to display items in the shopping cart
    public void displayCart() {
        if (currentIndex == 0) {
            JOptionPane.showMessageDialog(null, "Your cart is empty.");
            return;
        }

        double total = 0;
        StringBuilder cartDetails = new StringBuilder("Items in your cart:\n\n");

        for (int i = 0; i < currentIndex; i++) {
            Item item = items[i];
            int quantity = quantities[i];
            double itemTotal = item.price * quantity;
            total += itemTotal;
            cartDetails.append(item.name).append(" - ").append(quantity).append(" ").append(item.unit).append(" - ₱").append(itemTotal).append("\n");
        }

        cartDetails.append("\nTotal amount: ₱").append(total);
        JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea(cartDetails.toString(), 20, 40)));
    }

    // Method to display a receipt after checkout
    public void displayReceipt(String paymentMethod, double payment, double change) {
        double total = 0;
        int totalQuantity = 0;

        String[] columnNames = {"Item", "Price", "Quantity", "Total"};
        Object[][] data = new Object[currentIndex][4];

        for (int i = 0; i < currentIndex; i++) {
            Item item = items[i];
            int quantity = quantities[i];
            double itemTotal = item.price * quantity;
            total += itemTotal;
            totalQuantity += quantity;
            data[i][0] = item.name;
            data[i][1] = String.format("₱%.2f", item.price);
            data[i][2] = quantity;
            data[i][3] = String.format("₱%.2f", itemTotal);
        }

        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane tableScrollPane = new JScrollPane(table);

        StringBuilder receipt = new StringBuilder(paymentMethod + " Payment\n\n");
        receipt.append(String.format("Total quantity: %d\n", totalQuantity));
        receipt.append(String.format("Total: ₱%.2f\n", total));

        if (paymentMethod.equals("Cash")) {
            receipt.append(String.format("Cash: ₱%.2f\n", payment));
            receipt.append(String.format("Change: ₱%.2f\n", change));
        }

        receipt.append("\nThank you for shopping with us!");

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(new JTextArea(receipt.toString(), 5, 40), BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(null, panel, "Receipt of Purchase", JOptionPane.PLAIN_MESSAGE);
    }
}

// CheckoutButton class to handle checkout operations
class CheckoutButton {
    public static void checkout(ShoppingCart cart) {
        if (cart.currentIndex == 0) {
            JOptionPane.showMessageDialog(null, "Your cart is empty. Please add items to the cart before checking out.");
            return;
        }

        cart.displayCart();
        int response = JOptionPane.showConfirmDialog(null, "Proceed to checkout?", "Checkout", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            String[] paymentOptions = {"Cash", "Credit Card"};
            String paymentMethod = (String) JOptionPane.showInputDialog(null, "Choose mode of payment:", "Payment Method", JOptionPane.QUESTION_MESSAGE, null, paymentOptions, paymentOptions[0]);

            double totalAmount = 0;
            for (int i = 0; i < cart.currentIndex; i++) {
                totalAmount += cart.items[i].price * cart.quantities[i];
            }

            if (paymentMethod.equals("Cash")) {
                boolean validPayment = false;
                while (!validPayment) {
                    try {
                        double payment = Double.parseDouble(JOptionPane.showInputDialog("Total amount: ₱" + totalAmount + "\nEnter payment amount:"));

                        if (payment < totalAmount) {
                            JOptionPane.showMessageDialog(null, "Insufficient amount! Please try again.");
                        } else {
                            validPayment = true;
                            double change = payment - totalAmount;
                            cart.displayReceipt(paymentMethod, payment, change);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid payment amount. Please enter a valid number.");
                    }
                }
            } else {
                cart.displayReceipt(paymentMethod, 0, 0); // Display receipt for credit card payment
            }

            int continueResponse = JOptionPane.showConfirmDialog(null, "Do you want to make another transaction?", "New Transaction", JOptionPane.YES_NO_OPTION);
            if (continueResponse == JOptionPane.NO_OPTION) {
                cart.currentIndex = 0; // Reset the cart for a new transaction
                MainApp.showMainMenu(); // Redirect to the main menu after transaction
            } else {
                cart.currentIndex = 0; // Reset the cart for a new transaction
                MainApp.showGrocerySections(); // Show grocery sections again for a new transaction
            }
        }
    }
}

// MainApp class to manage user interaction and grocery section display
public class MainApp {
    static User[] users = new User[10]; // Array to store user objects (max 10 users)
    static Item[][] sections = new Item[6][]; // Array to store grocery sections (6 sections)
    static ShoppingCart cart; // Shopping cart for the current session
    static User currentUser = null; // Currently logged-in user (null if no user logged in)

    static String[] sectionNames = {"Fruits and Vegetables", "Frozen Meats", "Canned/ Jarred Goods", "Dairy and Baking Goods", "Beverage", "Personal Care and Cleaners", "Checkout"};

    public static void main(String[] args) {
        initializeSections(); // Initialize grocery sections with items
        while (true) {
            showMainMenu(); // Show main menu and handle user choices
        }
    }

    // Method to display the main menu and handle user choices
    public static void showMainMenu() {
        String[] options = {"Register", "Login", "Exit"};
        int choice = JOptionPane.showOptionDialog(null, "Welcome to the Shop", "Main Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    
        switch (choice) {
            case 0:
                registerUser(); // Call method to register a new user
                break;
            case 1:
                loginUser(); // Call method to login an existing user
                break;
            case 2:
                System.exit(0); // Exit the application
        }
    }
    
    // Method to register a new user
    public static void registerUser() {
        while (true) {
            String username = JOptionPane.showInputDialog("Username:");
            if (username == null) {
                return; // User canceled registration
            }
            if (username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Username cannot be empty.");
                continue;
            }
    
            String fullName = JOptionPane.showInputDialog("Full Name:");
            if (fullName == null) {
                return; // User canceled registration
            }
            if (fullName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Full Name cannot be empty.");
                continue;
            }
    
            String contactNo = JOptionPane.showInputDialog("Contact No.:");
            if (contactNo == null) {
                return; // User canceled registration
            }
            if (contactNo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Contact No. cannot be empty.");
                continue;
            }
    
            String password = JOptionPane.showInputDialog("Password:");
            if (password == null) {
                return; // User canceled registration
            }
            if (password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Password cannot be empty.");
                continue;
            }
    
            String confirmPassword = JOptionPane.showInputDialog("Confirm Password:");
            if (confirmPassword == null) {
                return; // User canceled registration
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match.");
                continue;
            }
    
            // Display confirmation dialog with user-entered details
            StringBuilder confirmationMessage = new StringBuilder("Please review your details:\n\n");
            confirmationMessage.append("Username: ").append(username).append("\n");
            confirmationMessage.append("Full Name: ").append(fullName).append("\n");
            confirmationMessage.append("Contact No.: ").append(contactNo).append("\n");
            confirmationMessage.append("Password: ").append(password).append("\n");
    
            int confirmResult = JOptionPane.showOptionDialog(null, confirmationMessage.toString(), "Confirm Registration", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Submit", "Cancel"}, "Submit");
    
            if (confirmResult == JOptionPane.YES_OPTION) {
                for (int i = 0; i < users.length; i++) {
                    if (users[i] == null) {
                        users[i] = new User(username, fullName, contactNo, password);
                        JOptionPane.showMessageDialog(null, "Registration successful!");
                        return; // Exit registration loop
                    }
                }
                JOptionPane.showMessageDialog(null, "User registration limit reached.");
                return; // Exit registration loop
            } else {
                return; // Exit registration loop
            }
        }
    }
    
    // Method to handle user login
    static void loginUser() {
        String username = JOptionPane.showInputDialog("Username:");
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty.");
            return;
        }

        String password = JOptionPane.showInputDialog("Password:");
        if (password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Password cannot be empty.");
            return;
        }

        for (User user : users) {
            if (user != null && user.username.equals(username) && user.password.equals(password)) {
                currentUser = user;
                JOptionPane.showMessageDialog(null, "Login successful! Welcome " + user.fullName);
                cart = new ShoppingCart(100); // Initialize cart for the logged-in user (max 100 items)
                showGrocerySections(); // Show grocery sections for the user to start shopping
                return;
            }
        }

        JOptionPane.showMessageDialog(null, "Invalid username or password.");
    }

    // Method to display grocery sections and handle user selection
    public static void showGrocerySections() {
        String selectedSection = (String) JOptionPane.showInputDialog(null, "Select a grocery section:", "Grocery Sections", JOptionPane.QUESTION_MESSAGE, null, sectionNames, sectionNames[0]);

        if (selectedSection == null) {
            JOptionPane.showMessageDialog(null, "Section selection canceled.");
            return;
        }

        if (selectedSection.equals("Checkout")) {
            CheckoutButton.checkout(cart); // Proceed to checkout
            return;
        }

        int index = java.util.Arrays.asList(sectionNames).indexOf(selectedSection);
        if (index >= 0 && index < sections.length) {
            showItemsInSection(index); // Show items in the selected section
        } else {
            JOptionPane.showMessageDialog(null, "Invalid section selected!");
        }
    }

    // Method to display items in a specific grocery section and handle user selection
    public static void showItemsInSection(int sectionIndex) {
        if (sections[sectionIndex] == null) {
            JOptionPane.showMessageDialog(null, "No items available in this section.");
            return;
        }

        String[] itemNames = new String[sections[sectionIndex].length];
        for (int i = 0; i < sections[sectionIndex].length; i++) {
            Item item = sections[sectionIndex][i];
            itemNames[i] = item.name + " - ₱" + item.price + " per " + item.unit + " (Stock: " + item.stock + ")";
        }

        while (true) {
            String selectedItem = (String) JOptionPane.showInputDialog(null, "Items in " + sectionNames[sectionIndex] + ":", "Items", JOptionPane.PLAIN_MESSAGE, null, itemNames, itemNames[0]);

            if (selectedItem == null) {
                JOptionPane.showMessageDialog(null, "Item selection canceled.");
                showGrocerySections();
                return;
            }

            if (selectedItem.equals("Checkout")) {
                CheckoutButton.checkout(cart); // Proceed to checkout
                return;
            }

            int itemIndex = java.util.Arrays.asList(itemNames).indexOf(selectedItem);

            int quantity;
            try {
                quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity:"));
                if (quantity <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
                continue;
            }

            Item selectedItemObject = sections[sectionIndex][itemIndex];
            if (quantity > selectedItemObject.stock) {
                JOptionPane.showMessageDialog(null, "Not enough stock for " + selectedItemObject.name + ". Available: " + selectedItemObject.stock);
                continue;
            }

            cart.addItem(selectedItemObject, quantity); // Add item to cart
            JOptionPane.showMessageDialog(null, "Added to cart!");

            int response = JOptionPane.showConfirmDialog(null, "Do you want to add another item?", "Add More", JOptionPane.YES_NO_OPTION);
            if (response != JOptionPane.YES_OPTION) {
                showGrocerySections(); // Exit to grocery sections menu
                return;
            }
        }
    }

    // Method to initialize grocery sections with predefined items
    public static void initializeSections() {
        sections[0] = new Item[]{
            new Item("Apple", 20.50, "1 piece", 50),
            new Item("Banana", 8.00, "1 piece", 100),
            new Item("Tomato", 150.75, "1 kilo", 30),
            new Item("Potato", 90.00, "½ kilo", 40),
            new Item("Strawberry", 206.50, "¼ kilo", 20)
        };

        sections[1] = new Item[]{
            new Item("Fish", 250.75, "1 kilo", 25),
            new Item("Pork Meat", 170.00, "1 kilo", 50),
            new Item("Chicken", 180.50, "1 kilo", 60),
            new Item("Beef Meat", 500.25, "1 kilo", 15)
        };

        sections[2] = new Item[]{
            new Item("555 Tuna", 25.25, "1 piece", 200),
            new Item("Argentina Corned Beef", 30.75, "1 piece", 150),
            new Item("Maling", 53.00, "1 piece", 100),
            new Item("Silver Swan", 15.75, "1 piece", 250),
            new Item("UFC Ketchup", 11.50, "1 piece", 300),
            new Item("Wow Ulam", 23.00, "1 piece", 120),
            new Item("Spam", 109.50, "1 piece", 90),
            new Item("San Marino", 30.75, "1 piece", 80)
        };

        sections[3] = new Item[]{
            new Item("Butter", 35.25, "1 piece", 60),
            new Item("Star Margarine", 30.75, "1 piece", 70),
            new Item("Flour", 40.00, "1 kilo", 50)
        };

        sections[4] = new Item[]{
            new Item("Gin", 42.75, "1 piece", 120),
            new Item("Zest-o", 12.00, "1 piece", 200),
            new Item("Vita Milk", 35.00, "1 piece", 150),
            new Item("Cobra", 24.50, "1 piece", 100),
            new Item("Bearbrand Sterilized", 38.25, "1 piece", 130),
            new Item("Red Horse", 110.00, "1 piece", 80)
        };

        sections[5] = new Item[]{
            new Item("Shampoo", 50.00, "1 piece", 90),
            new Item("Soap", 20.00, "1 piece", 100),
            new Item("Toothpaste", 15.50, "1 piece", 110),
            new Item("Laundry Detergent", 45.75, "1 piece", 80),
            new Item("Dishwashing Liquid", 25.25, "1 piece", 70)
        };
    }
}