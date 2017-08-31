package com.jk.display;

import com.jk.util.AIUtility;
import com.jk.util.DBUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class Categorize {

    public Categorize(){
        showit();
        btnAction();
    }

    private JFrame f;
    private JTextField dateText, descText, amtText, categoryText;
    private JButton submit, cancel;
    private JComboBox categoryCB;
    private JCheckBox isAll;
    private boolean updateAll;

    void showit(){
        f = new JFrame("Categorize Transactions");
        //GUIUtility.changeLNF("Nimbus");
        JPanel p = new JPanel();

        // labels and fields
        p.add(new JLabel("       Date"));
        dateText = new JTextField(20);
        dateText.setEditable(false);
        p.add(dateText);

        p.add(new JLabel("Description"));
        descText = new JTextField(20);
        descText.setEditable(false);
        p.add(descText);

        p.add(new JLabel("     Amount"));
        amtText = new JTextField(20);
        amtText.setEditable(false);
        p.add(amtText);

        p.add(new JLabel("             Category"));
        List facts = DBUtil.getCategories();
        categoryCB = new JComboBox();
        categoryCB.setSize(30,5);
        for(Object fact : facts){ categoryCB.addItem(fact); }
        p.add(categoryCB);

        isAll = new JCheckBox("Update All");
        p.add(isAll);

        //p.add(new JLabel("   Category"));
        categoryText = new JTextField(20);
        p.add(categoryText);

        // buttons
        submit = new JButton("Submit/New");
        p.add(submit);

        cancel = new JButton("Close");
        p.add(cancel);

        f.add(p);
        f.setResizable(false);
        f.setSize(330,227);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        getNextItem();

        categoryCB.addActionListener(e -> categoryText.setText("") );
    }

    void getNextItem(){
        dateText.setText("");
        descText.setText("");
        amtText.setText("");
        categoryText.setText("");

        Connection conn = DBUtil.connect();
        String sql = "select * from register where category ISNULL order by date desc";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            rs.next();
            dateText.setText(rs.getString("Date"));
            descText.setText(rs.getString("Description"));
            if (rs.getString("Amount Credit").equalsIgnoreCase("")) {
                amtText.setText(rs.getString("Amount Debit"));
                categoryText.setText(AIUtility.whatCategoryAmI(rs.getString("Description")));
            } else {
                amtText.setText(rs.getString("Amount Credit"));
                categoryText.setText("Paycheck");
            }
            rs = null;
            sql = "select count(0) as total from register where category isnull";
            PreparedStatement pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            rs.next();
            f.setTitle("Categorize Transactions (" + rs.getString("total") + ")");
        }catch(Exception e){ e.printStackTrace(); DBUtil.closeit(conn, ps, rs); } finally{ DBUtil.closeit(conn, ps, rs); }
    }

    void btnAction(){
        submit.addActionListener(e -> {
            String category = "", sql = "";

            if(descText.getText().equalsIgnoreCase("")){
                JOptionPane.showMessageDialog(null, "Nothing to update!");
                return;
            }

            if(categoryText.getText().equalsIgnoreCase("")){
                category = categoryCB.getSelectedItem().toString();
            }else{
                category = categoryText.getText();
                categoryCB.addItem(category);
            }

            Connection conn = DBUtil.connect();
            if(isAll.isSelected()){
                sql = "update register set category = ? where Description = ? and category is null";
            }else{
                sql = "update register set category = ? where date = ? and Description = ? and category is null";
            }
            PreparedStatement ps = null;
            try{
                ps = conn.prepareStatement(sql);

                if(isAll.isSelected()){
                    ps.setString(1, category);
                    ps.setString(2, descText.getText());
                }else{
                    ps.setString(1, category);
                    ps.setString(2, dateText.getText());
                    ps.setString(3, descText.getText());
                }

                ps.executeUpdate();
            }catch(Exception ex){ ex.printStackTrace(); DBUtil.closeit(conn, ps); } finally{ DBUtil.closeit(conn, ps); }

            getNextItem();
        });

        cancel.addActionListener(e -> f.dispose());
    }
}
