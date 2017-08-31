package com.jk.reports;

import com.jk.util.DBUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Vector;

public class DebtReport {
    public DebtReport(){
        showit();
    }

    void showit(){
        JFrame f = new JFrame("View Debt Report");

        Vector<Vector> rowData = new Vector<>();
        Vector<String> columnNames = new Vector<>();
        columnNames.addElement("Debt");
        columnNames.addElement("Monthly");
        columnNames.addElement("Total");
        columnNames.addElement("Interest");
        columnNames.addElement("Maturity Date");
        columnNames.addElement("Months");

        Double totalMonthly = 0.00, totalTotal = 0.00;

        DecimalFormat df2 = new DecimalFormat("###,###.00");

        Connection conn = DBUtil.connect();
        String sql = "select * from debt";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                Vector<String> vec = new Vector<>();
                String name = rs.getString("name");
                String monthly = rs.getString("monthly");
                String total = rs.getString("total");
                String interest = rs.getString("interest");

                totalMonthly += Double.parseDouble(monthly.replace(",",""));
                totalTotal += Double.parseDouble(total.replace(",",""));

                // maturity date
                int months = 1;
                Double balance = Double.parseDouble(total.replace(",",""));
                Double rate = Double.parseDouble(interest) / 100;
                Double payment = Double.parseDouble(monthly.replace(",",""));

                while(balance > 0){
                    Double interestPaid = (balance * rate / 365) * 30;
                    Double principal = (payment - interestPaid) < balance ? payment - interestPaid : balance;

                    balance = balance - principal;
                    months++;
                }
                vec.addElement(name);
                vec.addElement(monthly);
                vec.addElement(total);
                vec.addElement(interest);
                vec.addElement(LocalDate.now().plusMonths(months).toString());
                vec.addElement(Integer.toString(months));
                rowData.addElement(vec);
            }
            Vector<String> vector = new Vector<>();
            vector.addElement("Totals:");
            vector.addElement(df2.format(totalMonthly));
            vector.addElement(df2.format(totalTotal));
            rowData.addElement(vector);
        }catch(Exception e){ e.printStackTrace(); DBUtil.closeit(conn, ps); } finally{ DBUtil.closeit(conn, ps, rs); }

        JTable table = new JTable(rowData, columnNames);
        JScrollPane scroller = new JScrollPane(table);

        f.add(scroller);
        f.setResizable(true);
        f.setSize(800,400);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
