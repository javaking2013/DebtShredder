package com.jk.reports;

import com.jk.util.DBUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class ViewSchedule {

    public ViewSchedule(){
        showit();
    }

    void showit(){
        JFrame f = new JFrame("View Entire Schedule");

        Vector<Vector> rowData = new Vector<>();
        Vector<String> columnNames = new Vector<>();
        columnNames.addElement("Due Date");
        columnNames.addElement("Description");
        columnNames.addElement("Amount");
        columnNames.addElement("Category");
        columnNames.addElement("Frequency");

        Connection conn = DBUtil.connect();
        String sql = "select due, description, " +
                "case when amount in('') then income else amount end as amount" +
                ", category, frequency from schedule order by due";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                Vector<String> vec = new Vector<>();
                vec.addElement(rs.getString("due"));
                vec.addElement(rs.getString("description"));
                vec.addElement(rs.getString("amount"));
                vec.addElement(rs.getString("category"));
                vec.addElement(rs.getString("frequency"));
                rowData.addElement(vec);
            }
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
