package com.jk.util;

import com.jk.display.UpdateDebt;
import com.jk.reports.BillsReport;
import com.jk.reports.DebtAmortSchedule;
import com.jk.reports.DebtReport;
import com.jk.reports.ViewSchedule;
import com.jk.display.Something;
import javax.swing.*;
import com.jk.display.Categorize;

public class GUIUtility {

    public static JMenuItem getLnFMenuItem(String name){
        JMenuItem newItem = new JMenuItem(name);
        newItem.addActionListener(e -> changeLNF(name) );
        return newItem;
    }

    public static void changeLNF(String action){
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (action.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) { }
        SwingUtilities.updateComponentTreeUI(Something.frame);
    }

    public static JMenuItem getMenuItem(String input){
        JMenuItem newItem = new JMenuItem(input);
        newItem.addActionListener(e -> execute(input));
        return newItem;
    }

    public static JMenuItem getAmortMenuItem(String input){
        JMenuItem newItem = new JMenuItem(input);
        newItem.addActionListener(e -> new DebtAmortSchedule(input));
        return newItem;
    }

    private static void execute(String input){
        switch(input){
            case "Categorize": new Categorize();
                break;
            case "View All Scheduled": new ViewSchedule();
                break;
            case "Refresh Transactions":

                break;
            case "View Debt Report": new DebtReport();
                break;
            case "Modify Debts": new UpdateDebt();
                break;
            case "Bills Schedule": new BillsReport();
                break;
            default:
                JOptionPane.showMessageDialog(null, "This does nothing yet;");
                break;
        }
    }

    public static String rightPad(String input, int length){ return String.format("%1$-" + length + "s", input); }
    public static String leftPad(String input, int length){ return String.format("%1$" + length + "s", input);  }
}
