package com.jk.reports;

import com.jk.util.DBUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Vector;

public class MonthlySumReport {

    public MonthlySumReport(String month){
        this.month = month;
        showit();
    }

    private String month = "January";

    void showit(){
        JFrame f = new JFrame(month + " Sum Report");

        JPanel panel = new JPanel();
        //panel.setLayout(LayoutManager.BORDER_LAYOUT);

        Vector<Vector> rowData = new Vector<>();
        Vector<String> columnNames = new Vector<>();
        columnNames.addElement("Category");
        columnNames.addElement("Expense");

        DecimalFormat df2 = new DecimalFormat("###,###.00");

        Connection conn = DBUtil.connect();
        String sql = "SELECT category AS Category,\n" +
                "  CASE WHEN category = 'Paycheck' THEN sum(abs(cast(\"Amount Credit\" AS DECIMAL))) " +
                "ELSE sum(abs(cast(\"Amount Debit\" AS DECIMAL))) END AS Expense\n" +
                "FROM register\n" +
                "WHERE substr(date, 0, 3) = ? " +
                "GROUP BY category;";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1, getNumberFromMonth());
            rs = ps.executeQuery();
            while(rs.next()) {
                Vector<String> vec = new Vector<>();
                vec.addElement(rs.getString("Category"));
                vec.addElement(df2.format(Double.parseDouble(rs.getString("Expense"))));
                rowData.addElement(vec);
            }
        }catch(Exception e){ e.printStackTrace(); DBUtil.closeit(conn, ps); } finally{ DBUtil.closeit(conn, ps, rs); }

        JTable table = new JTable(rowData, columnNames);
        JScrollPane scroller = new JScrollPane(table);

        //f.setLayout();
        f.add(scroller);
        f.setResizable(true);
        f.setSize(400,400);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public String getNumberFromMonth(){
        switch (month){
            case "January": return "01";
            case "Febuary": return "02";
            case "March": return "03";
            case "April": return "04";
            case "May": return "05";
            case "June": return "06";
            case "July": return "07";
            case "August": return "08";
            case "September": return "09";
            case "October": return "10";
            case "November": return "11";
            case "December": return "12";
            default: return "01";
        }
    }
}