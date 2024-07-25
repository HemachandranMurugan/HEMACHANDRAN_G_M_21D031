package ZohoProject;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class BillingFrame extends Frame {
    BillingDAO billingDAO = new BillingDAO();
    Bill bill = new Bill();
    String Billno = billingDAO.getNextBillNumber();
    int serialNumber;
    double totalPrice;
    boolean paidStatus;

    List<String> billItems; // To keep track of added items
    private Map<String, Integer> productQuantityMap;

    TextField productIdField, productNameField, productQuantityField;
    Choice UnitField;
    TextArea billTextArea;
    Label totalBillLabel;

    public BillingFrame(String customerId, Frame AddcustomerFrame) {
        setTitle("Billing System");

        productQuantityMap = new HashMap<>();

        Label dateLabel = new Label("Date:");
        dateLabel.setBounds(50, 50, 50, 25);
        TextField dateField = new TextField();
        dateField.setBounds(100, 50, 100, 25);
        dateField.setText(getCurrentDate());
        dateField.setEditable(false);

        Label CustomerLabel = new Label("Customer ID:");
        CustomerLabel.setBounds(300, 50, 100, 25);
        TextField CustomerField = new TextField(customerId);
        CustomerField.setBounds(400, 50, 125, 25);
        CustomerField.setEditable(false);

        Label productLabel = new Label("Product:");
        productLabel.setBounds(50, 100, 75, 25);

        Label productIdLabel = new Label("ID:");
        productIdLabel.setBounds(50, 125, 25, 25);
        productIdField = new TextField();
        productIdField.setBounds(75, 125, 75, 25);

        productIdField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                fillProductName();
            }
        });

        Label productNameLabel = new Label("Name:");
        productNameLabel.setBounds(160, 125, 45, 25);
        productNameField = new TextField();
        productNameField.setBounds(205, 125, 100, 25);
        productNameField.setEditable(false);


        Label productQuantityLabel = new Label("Quantity:");
        productQuantityLabel.setBounds(305, 125, 75, 25);
        productQuantityField = new TextField();
        productQuantityField.setBounds(380, 125, 75, 25);

        Label unitLabel = new Label("Unit:");
        unitLabel.setBounds(475, 125, 50, 25);
        UnitField = new Choice();
        UnitField.add("kg");
        UnitField.add("g");
        UnitField.add("l");
        UnitField.add("ml");
        UnitField.add("pcs");
        UnitField.setBounds(525, 125, 50, 25);

        Button Stock = new Button("Stock available");
        Stock.setBounds(100, 175, 100, 25);
        Stock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StockFrame();
            }
        });

        Button addButton = new Button("Add to Bill");
        addButton.setBounds(250, 175, 100, 25);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItemToBill();
            }
        });

        Button clearLastItemButton = new Button("Clear Last Item");
        clearLastItemButton.setBounds(400, 175, 100, 25);
        clearLastItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearLastItem();
            }
        });

        billTextArea = new TextArea();
        billTextArea.setBounds(100, 225, 400, 200);
        billTextArea.setEditable(false);
        billTextArea.setFont(new Font("Courier", Font.PLAIN, 12));
        billTextArea.setText(String.format("%-6s%-15s%-10s%-10s%-10s%-15s\n", "S.No", "Name", "Price", "Quantity", "Unit", "Total Price"));

        Button printButton = new Button("Print Bill");
        printButton.setBounds(350, 450, 100, 25);

        Label totalamt = new Label("Total Bill Amount: ");
        totalamt.setBounds(50, 450, 150, 25);

        totalBillLabel = new Label(" 0.0");
        totalBillLabel.setBounds(200, 450, 50, 25);

        Checkbox paid = new Checkbox("Paid");
        paid.setBounds(100, 475, 50, 50);

        Checkbox unpaid = new Checkbox("Unpaid");
        unpaid.setBounds(175, 475, 50, 50);

        Label AmtPaid = new Label("AmtPaid:");
        AmtPaid.setBounds(250, 490, 50, 25);

        TextField paidField = new TextField();
        paidField.setBounds(300, 490, 100, 25);


        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Customer customer = billingDAO.getCustomerById(customerId);
                printBill(customer);

                billTextArea.setText("");
                productIdField.setText("");
                productNameField.setText("");
                productQuantityField.setText("");
                totalBillLabel.setText("");

            }
        });

        class CheckboxListener implements ItemListener {
            public void itemStateChanged(ItemEvent e) {
                Checkbox checkbox = (Checkbox) e.getItemSelectable();
                boolean isChecked = checkbox.getState();

                if (checkbox == paid && isChecked) {
                    System.out.println("Bill marked as Paid");
                    paidStatus = true;
                    bill.setPaidStatus(paidStatus);
                } else if (checkbox == unpaid && isChecked) {
                    System.out.println("Bill marked as Unpaid");
                    paidStatus = false;
                    bill.setPaidStatus(paidStatus);

                    double amt = getTotalBillamt();
                    double amtpaid = Double.parseDouble(paidField.getText());
                    billingDAO.updateCustomerBalance(customerId, amt - amtpaid);
                }
            }
        }

        CheckboxListener listener = new CheckboxListener();
        paid.addItemListener(listener);
        unpaid.addItemListener(listener);

        add(dateLabel);add(dateField);add(productLabel);
        add(CustomerLabel);add(CustomerField);
        add(productIdLabel);add(productIdField);
        add(productNameLabel);add(productNameField);
        add(productQuantityLabel);add(productQuantityField);
        add(unitLabel);add(UnitField);add(Stock);add(addButton);add(clearLastItemButton);
        add(billTextArea);add(printButton);add(totalamt);
        add(totalBillLabel);add(paid);add(unpaid);add(paidField);add(AmtPaid);

        setLayout(null);
        setSize(600, 550);
        setVisible(true);

        serialNumber = 1;
        totalPrice = 0.0;
        billItems = new ArrayList<>(); // Initialize the list

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });

        AddcustomerFrame.dispose();
    }
    private void fillProductName() {
        String productId = productIdField.getText();
        Product product = billingDAO.getProductById(productId);
        if (product != null) {
            if (product.getProductQuantity() > 0) {
                productNameField.setText(product.getProductName());
            } else {
                productNameField.setText("Out of Stock");
                System.out.println("Product is out of stock.");
            }
        } else {
            productNameField.setText("");
        }
    }

    private void addItemToBill() {
        String productId = productIdField.getText();
        String productName = productNameField.getText();
        String quantityStr = productQuantityField.getText();
        String unit = UnitField.getSelectedItem();

        Product product = billingDAO.getProductById(productId);

        if (productId.isEmpty() || productName.isEmpty() || quantityStr.isEmpty()) {
            System.out.println("All fields must be filled.");
            return;
        }

        int quantity;

        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            System.out.println("Quantity must be a number.");
            return;
        }

        if (productName.equals("Out of Stock")) {
            System.out.println("Cannot add product that is out of stock.");
            return;
        }

        if (product != null && product.getProductQuantity() >= quantity) {
            billingDAO.updateProductQuantity(productId, quantity);

            double price = product.getProductPrice();
            double itemTotalPrice = price * quantity;
            totalPrice += itemTotalPrice;

            String itemDetails = String.format("%-6d%-15s%-10.2f%-10d%-10s%-15.2f\n", serialNumber, productName, price, quantity, unit, itemTotalPrice);

            billTextArea.append(itemDetails);
            billItems.add(itemDetails); // Add the item to the list
            totalBillLabel.setText(" " + totalPrice);

            if (productQuantityMap.containsKey(productId)) {
                int currentQuantity = productQuantityMap.get(productId);
                productQuantityMap.put(productId, currentQuantity + quantity);
            } else {
                productQuantityMap.put(productId, quantity);
            }
            serialNumber++;
        }
            else {
            System.out.println("Requested quantity is not available.");
        }
    }

    public Double getTotalBillamt() {
        String Billamt = totalBillLabel.getText();
        double totalamt = Double.parseDouble(Billamt);
        return totalamt;
    }

    private void clearLastItem() {
        if (!billItems.isEmpty()) {
            String lastItem = billItems.remove(billItems.size() - 1);
            String[] details = lastItem.trim().split("\\s+");

            if (details.length == 5) {
                double itemTotalPrice = Double.parseDouble(details[4]);
                totalPrice -= itemTotalPrice;
            }
            // Update the TextArea
            billTextArea.setText(String.format("%-6s%-15s%-10s%-10s%-15s\n", "S.No", "Name", "Price", "Quantity", "Total Price"));
            for (int i = 0; i < billItems.size(); i++) {
                billTextArea.append(billItems.get(i));
            }
            totalBillLabel.setText("Total Bill Amount: " + totalPrice);
            serialNumber--;
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    public void printBill(Customer customer) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = formatter.format(date);

        System.out.println("G.M.H Stores");
        System.out.println("Date: " + currentDate + "\t\t\tBill NO: " + Billno);
        System.out.println("Customer ID: " + customer.getCustomerPhoneNo() + "\t \t Customer Name: " + customer.getCustomerName());
        System.out.println("--------------------------------------------------------------");
        System.out.println(String.format("%-6s%-15s%-10s%-10s%-10s%-15s", "S.No", "Name", "Price", "Quantity", "Unit", "Total Price"));

        int serialNumber = 1;
        double totalAmount = 0.0;
        double totalBillAmount;
        double membershipPoint = customer.getMembershipPoints();
        double discount = 0;

        for (String line : billItems) {
            String[] parts = line.trim().split("\\s{2,}"); // Split by 2 or more spaces

            if (parts.length >= 6) {
                String productName = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                int quantity = Integer.parseInt(parts[3].trim());
                String unit = parts[4].trim();
                double itemTotalPrice = Double.parseDouble(parts[5].trim());

                System.out.println(String.format("%-6d%-15s%-10.2f%-10d%-10s%-15.2f", serialNumber, productName, price, quantity, unit, itemTotalPrice));

                totalAmount += itemTotalPrice;
                serialNumber++;
            }
        }

        System.out.println("--------------------------------------------------------------");

        if (totalAmount > 1000 && totalAmount < 2000) {
            System.out.println("Discount 10%");
            totalBillAmount = totalAmount * 0.9;
            discount = totalAmount * 0.1;
        } else if (totalAmount > 2000) {
            System.out.println("Discount 20%");
            totalBillAmount = totalAmount * 0.8;
            discount = totalAmount * 0.2;
        } else {
            totalBillAmount = totalAmount;
        }

        membershipPoint = membershipPoint + totalBillAmount * 0.1;
        if (membershipPoint >= 1000) {
            membershipPoint = membershipPoint - 1000;
            totalBillAmount = totalBillAmount * 0.9;
            System.out.println("The Redeemed Membership Points = 10%");
        }
        customer.setMembershipPoints(membershipPoint);

        billingDAO.UpdateMembershipPoints(membershipPoint, customer.getCustomerPhoneNo());

        System.out.printf("\nThe earned Membership points: %.2f\n", membershipPoint);

        // Display total bill amount
        System.out.println("Total Bill Amount: " + totalBillAmount);
        System.out.println("Savings: " + (totalAmount - totalBillAmount));

        System.out.println("--------------------------------------------------------------");
        System.out.println("***Thank you**\n**Visit again***");
        System.out.println();

        bill.setBillNo(Billno);
        bill.setbillamt(totalBillAmount);
        bill.setDate(currentDate);
        bill.setCustomerid(customer.getCustomerPhoneNo());
        bill.setDiscount(discount);
        bill.setPaidStatus(paidStatus);

        billingDAO.insertBill(bill);

        for (Map.Entry<String, Integer> entry : productQuantityMap.entrySet()) {
            String productId = entry.getKey();
            int quantity = entry.getValue();
            billingDAO.insertItemSales(productId, Billno, quantity);
        }
    }

    class StockFrame extends Frame {
        BillingDAO billingDAO = new BillingDAO();
        TextArea stockTextArea;

        public StockFrame() {
            setTitle("Stock Availability");
            stockTextArea = new TextArea();
            stockTextArea.setBounds(50, 50, 400, 300);
            stockTextArea.setEditable(false);
            stockTextArea.setFont(new Font("Courier", Font.PLAIN, 12));
            fillStockDetails();
            add(stockTextArea);
            setLayout(null);
            setSize(500, 300);
            setVisible(true);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    dispose();
                }
            });
        }

        private void fillStockDetails() {
            List<Product> products = billingDAO.getAllProducts();
            stockTextArea.setText(String.format("%-15s%-20s%-10s%-10s\n", "Product ID", "Product Name", "Price", "Quantity"));

            for (Product product : products) {
                stockTextArea.append(String.format("%-15s%-20s%-10.2f%-10d\n",
                        product.getProductId(),
                        product.getProductName(),
                        product.getProductPrice(),
                        product.getProductQuantity()));
            }
        }
    }
}
