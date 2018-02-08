package com.jk.reports;

import com.jk.util.DBUtil;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

public class BillsReport {

    private double one = 0.00;
    private double two = 0.00;
    private double three = 0.00;
    private double four = 0.00;
    private double five = 0.00;
    private double six = 0.00;

    public BillsReport(){ showit(); }

    private void showit(){
        JFrame f = new JFrame("Bills Schedule Report");

        ArrayList<Vector> items = new ArrayList<>();

        Vector<Vector> rowData = new Vector<>();
        Vector<String> columnNames = new Vector<>();
        columnNames.addElement("Payday");
        columnNames.addElement("Now");

        Vector<String> paychecks = new Vector<>();
        paychecks.addElement("Paycheck Income");
        paychecks.addElement("");

        DecimalFormat df2 = new DecimalFormat("###,###.00");

        Connection conn = DBUtil.connect();
        String sql = "select * from schedule order by due";
        PreparedStatement ps = null;
        ResultSet rs = null;

        int counter = 0;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next() && counter < 6) {
                String desc = rs.getString("description");
                String due = rs.getString("due");
                String amount = rs.getString("amount");
                String income = rs.getString("income");
                String cat = rs.getString("category");

                if("Paycheck".equalsIgnoreCase(desc)){
                    for(int j = 1; j < items.size(); j++){
                        Vector<String> item = items.get(j);
                        while(item.size() < columnNames.size()){
                            item.addElement("");
                        }
                        items.set(j, item);
                    }
                    paychecks.addElement(income);
                    counter++;
                    if(counter < 6){
                        columnNames.addElement(due);
                    }

                }else{
                    if(!itemExists(items, desc)){
                        Vector<String> temp = new Vector<>();
                        temp.addElement(desc);
                        while(temp.size() < columnNames.size() - 1) {
                            temp.addElement("");
                        }
                        items.add(temp);
                    }

                    for(int i = 0; i < items.size(); i++){
                        Vector<String> item = items.get(i);
                        if(desc.equalsIgnoreCase(item.firstElement())){
                            item.addElement(amount);
                            addemup(counter + 1, amount);
                            items.set(i, item);
                        }
                    }
                }
            }
        }catch(Exception e){ e.printStackTrace(); DBUtil.closeit(conn, ps); } finally{ DBUtil.closeit(conn, ps, rs); }

        rowData.addElement(paychecks);

        for(Vector<String> item : items){
            rowData.addElement(item);
        }

        Vector<String> totals = new Vector<>();
        totals.addElement("Totals: ");
        totals.addElement(df2.format(one));
        totals.addElement(df2.format(two));
        totals.addElement(df2.format(three));
        totals.addElement(df2.format(four));
        totals.addElement(df2.format(five));
        totals.addElement(df2.format(six));
        rowData.addElement(totals);

        // TODO Serious Bug --> If there are not 6 paychecks scheduled or left in year then add blank data

        Vector<String> diffs = new Vector<>();
        diffs.addElement("Difference: ");
        diffs.addElement(df2.format( Double.parseDouble(DBUtil.getLedgerBalance()) - one ));
        diffs.addElement(df2.format(Double.parseDouble(paychecks.get(2)) - two));
        diffs.addElement(df2.format(Double.parseDouble(paychecks.get(3)) - three));
        diffs.addElement(df2.format(Double.parseDouble(paychecks.get(4)) - four));
        diffs.addElement(df2.format(Double.parseDouble(paychecks.get(5)) - five));
        diffs.addElement(df2.format(Double.parseDouble(paychecks.get(6)) - six));
        rowData.addElement(diffs);

        JTable table = new JTable(rowData, columnNames);
        JScrollPane scroller = new JScrollPane(table);

        f.add(scroller);
        f.setResizable(true);
        f.setSize(900,500);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private boolean itemExists(ArrayList<Vector> items, String category){
        for(Vector<String> item : items){
            if(category.equalsIgnoreCase(item.firstElement())){
                return true;
            }
        }
        return false;
    }

    private void addemup(int counter, String price){
        double cost = Double.parseDouble(price.replace(",",""));
        switch (counter){
            case 1: one += cost;
                break;
            case 2: two += cost;
                break;
            case 3: three += cost;
                break;
            case 4: four += cost;
                break;
            case 5: five += cost;
                break;
            case 6: six += cost;
                break;
            default: break;
        }
    }
}
