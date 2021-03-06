package com.jk.reports;

import com.jk.util.DBUtil;
import com.jk.util.Holder;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class DebtAmortSchedule {

    public DebtAmortSchedule(String title){
        this.title = title;
        showit();
    }

    private String title;

    private void showit(){
        JFrame f = new JFrame("Amortization Schedule For " + this.title);

        String total = "", interest = "", monthly = "", lastPaid = "";

        Connection conn = DBUtil.connect();
        String sql = "select * from debt where name = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1, this.title);
            rs = ps.executeQuery();
            while(rs.next()) {
                total = rs.getString("total");
                interest = rs.getString("interest");
                monthly = rs.getString("monthly");
                lastPaid = rs.getString("lastPaid");
            }
        }catch(Exception e){ e.printStackTrace(); DBUtil.closeit(conn, ps); } finally{ DBUtil.closeit(conn, ps, rs); }

        Vector<Vector> rowData = new Vector<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = LocalDate.parse(lastPaid, formatter);
        Double balance = Double.parseDouble(total.replace(",",""));
        Double rate = Double.parseDouble(interest) / 100;
        Double payment = Double.parseDouble(monthly.replace(",",""));
        int month = 1;

        while(balance > 0){
            Vector<String> vec = new Vector<>();
            date = date.plusMonths(1);
            DecimalFormat df2 = new DecimalFormat("###,###.00");

            vec.addElement(Integer.toString(month));
            vec.addElement("$" + df2.format(balance));
            vec.addElement(date.toString());

            Double interestPaid = (balance * rate / 365) * 30;
            Double principal = (payment - interestPaid) < balance ? payment - interestPaid : balance;
            if("Mortgage".equalsIgnoreCase(this.title) && principal > Holder.escrow){
                principal = principal - Holder.escrow;
                System.out.println(principal);
            }

            vec.addElement("$" + df2.format(principal));
            vec.addElement("$" + df2.format(interestPaid));
            if("Mortgage".equalsIgnoreCase(this.title)){
                vec.addElement("$" + df2.format(principal + interestPaid + Holder.escrow));
            }else {
                vec.addElement("$" + df2.format(principal + interestPaid));
            }
            rowData.addElement(vec);

            balance = balance - principal;
            month++;
        }

        Vector<String> columnNames = new Vector<>();
        columnNames.addElement("Month");
        columnNames.addElement("Balance");
        columnNames.addElement("Due Date");
        columnNames.addElement("Principal");
        columnNames.addElement("Interest");
        columnNames.addElement("Total Payment");

        JTable table = new JTable(rowData, columnNames);
        JScrollPane scroller = new JScrollPane(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(3);

        f.add(scroller);
        f.setResizable(true);
        f.setSize(800,400);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}
