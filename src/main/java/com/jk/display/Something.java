package com.jk.display;

import javax.swing.*;
import javax.swing.UIManager.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.jk.util.GUIUtility;
import com.jk.util.DBUtil;
import com.jk.util.ValidUtility;

public class Something {

    public static JFrame frame;
    private JPanel mainForm;
    private JLabel topLabel, ledgerBalLabel, tailLabel;
    private JList debtList;
    private JTextField dateFieldAdd, descFieldAdd, memoFieldAdd, priceFieldAdd;
    private JLabel dateField;
    private JLabel descField;
    private JTextArea scheduleTextArea;
    private JTextField scheduleDescField, scheduleDueField, scheduleAmountField, scheduleIncomeField;
    private JButton scheduleButton, scheduleRemoveButton, doSomethingButton, quickAddButton;
    private JList scheduleList;
    private JScrollPane scheduleScroll;
    private JComboBox freqDDL, scheduleCatDDL;
    private JTextField balanceFieldAdd;
    public DefaultListModel listModel, debtListModel;

    public Something() {
        ledgerBalLabel.setText("Ledger Balance: $" + DBUtil.getLedgerBalance());
        scheduleTextArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        scheduleTextArea.setEditable(false);
        scheduleTextArea.setLineWrap(true);
        scheduleTextArea.setText("");
        scheduleTextArea.setText(DBUtil.getTransactions(""));

        /* populate JList with schedule */
        listModel = DBUtil.getCurrentSchedule();
        scheduleList.setModel(listModel);
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        debtListModel = DBUtil.getFormattedDebts();
        debtList.setModel(debtListModel);
        debtList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        // populating frequency DDL
        freqDDL.addItem("Monthly");
        freqDDL.addItem("Quarterly");
        freqDDL.addItem("Biweekly");

        // populate category DDL
        List facts = DBUtil.getCategories();
        for(Object fact : facts){ scheduleCatDDL.addItem(fact); }
        //for(Object fact : facts){ categoryDDL.addItem(fact); }

        updateAvailableFunds();

        quickAddButton.addActionListener(e -> {
            // get values from all fields
            String date;
            Double price;
            if(ValidUtility.isValidDate(dateFieldAdd.getText())){
                date = dateFieldAdd.getText();
            }else{
                JOptionPane.showMessageDialog(null, "Date must be in valid date format: mm/dd/yyyy");
                dateFieldAdd.requestFocus();
                return;
            }

            String desc = descFieldAdd.getText();
            String memo = memoFieldAdd.getText();
            if(ValidUtility.isValidPrice(priceFieldAdd.getText())){
                price = Double.parseDouble(priceFieldAdd.getText());
            }else{
                JOptionPane.showMessageDialog(null, "Debit/Credit must be valid: 123.45");
                priceFieldAdd.requestFocus();
                return;
            }

            Double balance = null;
            if(ValidUtility.isValidPrice(balanceFieldAdd.getText())){
                balance = Double.parseDouble(balanceFieldAdd.getText());
            }else{
                JOptionPane.showMessageDialog(null, "Balance must be valid: 123.45");
                balanceFieldAdd.requestFocus();
                return;
            }

            //Double balance = Double.parseDouble(DBUtil.getLedgerBalance()) - price;
            DBUtil.addEntryToRegister(date, desc, memo, price, balance);
            dateFieldAdd.setText("");
            descFieldAdd.setText("");
            memoFieldAdd.setText("");
            priceFieldAdd.setText("");
            balanceFieldAdd.setText("");

            JOptionPane.showMessageDialog(null, "Added transaction, new balance = " + DBUtil.getLedgerBalance());

            ledgerBalLabel.setText("Ledger Ballance: $" + DBUtil.getLedgerBalance());
            updateAvailableFunds();
            scheduleTextArea.setText("");
            scheduleTextArea.setText(DBUtil.getTransactions(""));

            tailLabel.setText(desc + " has been added to register.");
        });

        doSomethingButton.addActionListener(e -> {
            //TODO refresh everything (GUIUtility function?!?)
        });

        scheduleButton.addActionListener( e -> {
            String desc = null, due = null, amount = null, income = null, category = null, freq = null;

            if(scheduleDescField.getText().equalsIgnoreCase("")){
                JOptionPane.showMessageDialog(null, "Desctription can't be empty.");
                scheduleDescField.requestFocus();
                return;
            }else{
                desc = scheduleDescField.getText();
            }

            if(ValidUtility.isValidDate(scheduleDueField.getText())){
                due = scheduleDueField.getText();
            }else{
                JOptionPane.showMessageDialog(null, "Date is not valid (mm/dd/yyyy): " + scheduleDueField.getText());
                scheduleDueField.requestFocus();
                return;
            }

            if(!scheduleAmountField.getText().equalsIgnoreCase("")){
                if(ValidUtility.isValidPrice(scheduleAmountField.getText())){
                    amount = scheduleAmountField.getText();
                }else {
                    JOptionPane.showMessageDialog(null, scheduleAmountField.getText() + " is not valid for amount.");
                    scheduleAmountField.requestFocus();
                    return;
                }
            }else{
                amount = "";
            }

            if(!scheduleIncomeField.getText().equalsIgnoreCase("")){
                if(ValidUtility.isValidPrice(scheduleIncomeField.getText())){
                    income = scheduleIncomeField.getText();
                }else {
                    JOptionPane.showMessageDialog(null, scheduleAmountField.getText() + " is not valid for income.");
                    scheduleIncomeField.requestFocus();
                    return;
                }
            }else{
                income = "";
            }

            // if both are empty then throw an error
            if(scheduleIncomeField.getText().equalsIgnoreCase("") && scheduleAmountField.getText().equalsIgnoreCase("")){
                JOptionPane.showMessageDialog(null, "Both amount and income can't be null.");
                return;
            }

            category = scheduleCatDDL.getSelectedItem().toString();
            freq = freqDDL.getSelectedItem().toString();

            DBUtil.addSchedule(desc, due, amount, income, category, freq);

            scheduleList.removeAll();
            listModel = DBUtil.getCurrentSchedule();
            scheduleList.setModel(listModel);
            tailLabel.setText("'" + scheduleDescField.getText() + "' has been added to the schedule for " + scheduleDueField.getText() + ".");

            scheduleDescField.setText("");
            scheduleDueField.setText("");
            scheduleAmountField.setText("");
            scheduleIncomeField.setText("");

            updateAvailableFunds();
        });

        scheduleRemoveButton.addActionListener( e -> {
            if(scheduleList.getSelectedIndex() >= 0) {
                tailLabel.setText("'" + scheduleList.getSelectedValue() + "' has been removed from the schedule.");
                DBUtil.deleteFromSchedule(scheduleList.getSelectedValue().toString());
                updateAvailableFunds();
                listModel.remove(scheduleList.getSelectedIndex());
            }
        });

        // determine if there are uncategorized transactions and ask to do them
        int cats = DBUtil.uncategorizedItems();
        if(cats > 0){
            int n = JOptionPane.showConfirmDialog(null, "There are " + cats + " uncategorized transactions, would you like to set them now?",
                    "Uncategorized Message", JOptionPane.YES_NO_OPTION);
            if(n == JOptionPane.YES_OPTION){
                new Categorize();
            }
        }

    }

    public void updateAvailableFunds(){
        topLabel.setText("You have " + GUIUtility.leftPad("$" + DBUtil.getAvailableFunds(DBUtil.getLedgerBalance()), 7) + " available to spend!");
    }

    public static void main(String[] args){

        frame = new JFrame("Debt Shredder");
        GUIUtility.changeLNF("Nimbus");

        /*************************** menu bar **********************************************/

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");


        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(exit);
        exit.addActionListener( e -> System.exit(0));

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(GUIUtility.getMenuItem("Refresh Transactions"));
        editMenu.add(GUIUtility.getMenuItem("Categorize"));

        JMenu reportsMenu = new JMenu("Reports");
        reportsMenu.add(GUIUtility.getMenuItem("View Debt Report"));
        reportsMenu.add(GUIUtility.getMenuItem("View All Scheduled"));

        JMenu amortMenu = new JMenu("Build Amortization Schedule");
        reportsMenu.add(amortMenu);

        ArrayList<String> debts = DBUtil.getDebtTitles();

        for(String debt : debts){ amortMenu.add(GUIUtility.getAmortMenuItem(debt)); }

        JMenu helpMenu = new JMenu("Help");
        JMenu viewMenu = new JMenu("View");
        JMenu lnfMenu = new JMenu("Look and Feel");

        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            lnfMenu.add(GUIUtility.getLnFMenuItem(info.getName()));
        }

        viewMenu.add(lnfMenu);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(reportsMenu);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        /***********************************************************************************/

        frame.setContentPane(new Something().mainForm);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(1100,600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }
}