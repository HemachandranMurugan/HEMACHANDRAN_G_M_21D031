package ZohoProject;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EmployeeFrame extends Frame {
    BillingDAO billingDAO = new BillingDAO();

     EmployeeFrame(Frame frame) {
        setTitle("Employee DashBoard");

        Button printBill = new Button("Generate Invoice");
        printBill.setBounds(150, 75, 200, 50);

        Button paidInvoices = new Button("PaidInvoices");
        paidInvoices.setBounds(150, 150, 200, 50);

        Button CustomerBalance = new Button("Customer Balance");
        CustomerBalance.setBounds(150, 225, 200, 50);

        Button allInvoices = new Button("Fetch Invoices");
        allInvoices.setBounds(150, 300, 200, 50);

        add(allInvoices);
        add(paidInvoices);
        add(CustomerBalance);
        add(printBill);

        allInvoices.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 new fetchbillframe();
            }
        });
        paidInvoices.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               fetchAndPrintInvoiceByStatus();
            }
        });
        CustomerBalance.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                billingDAO.fetchCustomerBalance();
                fetchAndPrintCustomerBalances();
            }
        });
        printBill.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    new CheckCustomerframe();
            }
        });

        setLayout(null);
        setSize(500, 500);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                Main frame = new Main();
                setEnabled(true);
                dispose();
            }
        });
        frame.dispose();
    }

    private void showDialog(String title, String message) {
        Dialog dialog = new Dialog(this, title, true);
        dialog.setLayout(new FlowLayout());
        dialog.add(new Label(message));
        Button okButton = new Button("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        dialog.add(okButton);
        dialog.setSize(300, 100);
        dialog.setVisible(true);
    }

    private boolean isValidPhoneNumber(String phoneNo) {
        // Check if the phone number has at least 10 digits and contains only digits
        if (phoneNo.length() < 10) {
            return false;
        }
        for (char c : phoneNo.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private void fetchAndPrintCustomerBalances() {
        Map<String, Double> balances = billingDAO.fetchCustomerBalance();
        System.out.println("Customer Balances:");
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            System.out.println("Customer ID: " + entry.getKey() + ", Balance: " + entry.getValue());
        }
    }

    private void fetchAndPrintInvoiceByStatus() {
        List<String> paidInvoices = billingDAO.fetchInvoicesByStatus(true);
        List<String> unpaidInvoices = billingDAO.fetchInvoicesByStatus(false);

        System.out.println("Paid Invoices:");
        printPaidInvoices(paidInvoices);

        System.out.println("\nUnpaid Invoices:");
        printUnpaidInvoices(unpaidInvoices);
    }

    private void fetchAndPrintBill(String billNo) {
        List<String> billDetails = billingDAO.fetchBillDetails(billNo);

        // Print header
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = formatter.format(new Date());

        // Assuming the first entry contains the common bill details
        String[] firstBillDetail = billDetails.get(0).split("\t");
        String billDate = firstBillDetail[1];  // Assuming the second column is the Bill_Date
        String customerId = firstBillDetail[2];  // Assuming the third column is the Customer_id
        double discount = Double.parseDouble(firstBillDetail[4]);  // Assuming the fifth column is the Bill_discount

        System.out.println("G.M.H Stores");
        System.out.println("Date: " + currentDate + "\t\t\tBill NO: " + billNo);
        System.out.println("Bill Date: " + billDate + "\t\t\tCustomer ID: " + customerId);
        System.out.println("--------------------------------------------------------------");
        System.out.println(String.format("%-6s%-20s%-10s%-10s%-10s%-15s", "S.No", "Name", "Price", "Quantity", "Unit", "Total Price"));

        int serialNumber = 1;
        double totalAmount = 0.0;

        for (String detail : billDetails) {
            String[] parts = detail.split("\t");
            String productName = parts[5];
            double price = Double.parseDouble(parts[6]);
            String unit = parts[7];  // Assuming the eighth column is the unit
            int quantity = Integer.parseInt(parts[8]);
            double totalPrice = Double.parseDouble(parts[9]);

            System.out.println(String.format("%-6d%-20s%-10.2f%-10d%-10s%-15.2f", serialNumber, productName, price, quantity, unit, totalPrice));

            totalAmount += totalPrice;
            serialNumber++;
        }

        double finalAmount = totalAmount - discount;

        // Print totals and footer
        System.out.println("--------------------------------------------------------------");
        System.out.println("Total Bill Amount: " + totalAmount);
        System.out.println("Discount: " + discount);
        System.out.println("Final Bill Amount: " + finalAmount);
        System.out.println("--------------------------------------------------------------");
        System.out.println("***Thank you**\n**Visit again***\n");
    }

    private void printPaidInvoices(List<String> invoices) {
        if (invoices.isEmpty()) {
            System.out.println("No paid invoices found.");
            return;
        }

        for (String invoice : invoices) {
            String[] parts = invoice.split("\t");
            String billNo = parts[0];
            String customerId = parts[1];
            System.out.println("Bill No: " + billNo + ", Customer ID: " + customerId);
        }
    }

    private void printUnpaidInvoices(List<String> invoices) {
        if (invoices.isEmpty()) {
            System.out.println("No unpaid invoices found.");
            return;
        }
        Map<String, Double> customerBalances = billingDAO.fetchCustomerBalance();

        for (String invoice : invoices) {
            String[] parts = invoice.split("\t");
            String billNo = parts[0];
            String customerId = parts[1];
            Double balance = customerBalances.get(customerId);
            System.out.println("Bill No: " + billNo + ", Customer ID: " + customerId + ", Balance: " + (balance != null ? balance : "N/A"));
        }
    }

    class fetchbillframe extends Frame{
        fetchbillframe(){
            setTitle("Fetch Invoice") ;
            Label bill = new Label("Enter Bill No:");
            bill.setBounds(150, 150, 100, 25);
            TextField BIllNO = new TextField(); // Set the customer ID
            BIllNO.setBounds(250, 150, 150, 25);
            BIllNO.setEditable(true);

            Button checkStatus = new Button("Fetch Invoice details");
            checkStatus.setBounds(200, 200, 150, 25);

            checkStatus.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String BillNO = BIllNO.getText();
                    fetchAndPrintBill(BillNO);
                }
            });

            add(bill);add(BIllNO);add(checkStatus);

            setLayout(null);
            setSize(500, 500);
            setVisible(true);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    Main frame = new Main();
                    setEnabled(true);
                    dispose();
                }
            });
        }
    }

    class CheckCustomerframe extends Frame {
        CheckCustomerframe() {
            setTitle("Check Customer");
            Label CustomerLabel = new Label("Customer ID:");
            CustomerLabel.setBounds(150, 150, 100, 25);
            TextField customerField = new TextField(); // Set the customer ID
            customerField.setBounds(250, 150, 150, 25);
            customerField.setEditable(true);

            Button checkStatus = new Button("CheckStatus");
            checkStatus.setBounds(200, 200, 100, 25);

            checkStatus.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String customerId = customerField.getText();// Fetch customer ID here
                    // Validate the phone number length and content
                    if (!isValidPhoneNumber(customerId)) {
                        showDialog("Error", "Please enter a valid phone number");
                        return;
                    }
                    if (!billingDAO.customerExists(customerId)) {
                        new AddCustomerFrame(customerId, CheckCustomerframe.this);

                    } else {
                        new BillingFrame(customerId, CheckCustomerframe.this); // Pass customerId to BillingFrame constructor
                    }
                }
            });

            add(CustomerLabel);
            add(customerField);
            add(checkStatus);


            setLayout(null);
            setSize(500, 500);
            setVisible(true);

            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    setEnabled(true);
                    dispose();
                }
            });
        }
    }
}
