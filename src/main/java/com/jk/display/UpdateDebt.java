package com.jk.display;

import com.jk.util.DBUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UpdateDebt {

    private JPanel panel1;
    private JTextField lastPaidField, monthlyField, rateField, totalField;
    private JTextField nextField, paymentField, interestField;
    private JLabel debtNameLabel;
    private JButton updateButton;
    private JComboBox debtsDDL;

    public UpdateDebt() {
        JFrame f = new JFrame("Debt Updater");

        List facts = getDebts();
        for(Object fact : facts){ debtsDDL.addItem(fact); }
        getDebtDetails();

        f.add(panel1);
        f.setResizable(false);
        f.setSize(450,280);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        updateButton.addActionListener(e -> updateDatabase() );
        debtsDDL.addActionListener(e -> getDebtDetails() );
    }

    private void updateDatabase(){
        String debt = debtsDDL.getSelectedItem().toString();
        String lastPaid = lastPaidField.getText();
        String monthly = monthlyField.getText();
        String interest = rateField.getText();
        String total = totalField.getText();

        Connection conn = DBUtil.connect();
        String sql = "update debt set total = ?, interest = ?, monthly = ?, lastPaid = ? where name = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1, total);
            ps.setString(2, interest);
            ps.setString(3, monthly);
            ps.setString(4, lastPaid);
            ps.setString(5, debt);
            ps.executeUpdate();
        }catch(Exception e){ e.printStackTrace(); DBUtil.closeit(conn, ps); } finally{ DBUtil.closeit(conn, ps, rs); }
    }

    private void getDebtDetails(){
        String debt = debtsDDL.getSelectedItem().toString();
        DecimalFormat df2 = new DecimalFormat("###,###.00");
        Connection conn = DBUtil.connect();
        String sql = "select * from debt where name = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1, debt);
            rs = ps.executeQuery();
            rs.next();
            this.lastPaidField.setText(rs.getString("lastPaid"));
            this.monthlyField.setText(rs.getString("monthly"));
            this.rateField.setText(rs.getString("interest"));
            this.totalField.setText(rs.getString("total"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date = LocalDate.parse(rs.getString("lastPaid"), formatter);
            this.nextField.setText(date.plusMonths(1).toString());

            String temp = rs.getString("total").replace(",","");
            Double balance = Double.parseDouble(temp);
            temp = rs.getString("interest");
            Double rate = Double.parseDouble(temp) / 100;
            temp = rs.getString("monthly");
            Double monthly = Double.parseDouble(temp);

            Double interestPaid = (balance * rate / 365) * 30;
            interestField.setText(df2.format(interestPaid));
            paymentField.setText(df2.format(monthly - interestPaid));
        }catch(Exception e){ e.printStackTrace(); DBUtil.closeit(conn, ps); } finally{ DBUtil.closeit(conn, ps, rs); }
    }

    private List getDebts(){
        ArrayList results = new ArrayList();
        Connection conn = DBUtil.connect();
        String sql = "select * from debt ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                results.add(rs.getString("name"));
            }
        }catch(Exception e){ e.printStackTrace(); DBUtil.closeit(conn, ps); } finally{ DBUtil.closeit(conn, ps, rs); }
        return results;
    }
}
