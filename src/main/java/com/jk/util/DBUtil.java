package com.jk.util;

import javax.swing.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.jk.util.Holder;

public class DBUtil {

    public static Connection connect() {
        Connection conn;
        try {
            String url = "jdbc:sqlite:D:/IdeaProjects/MoneySaver/database/patrick.db";
            conn = DriverManager.getConnection(url);
            return conn;
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean doesDatabaseExist(){
        //TODO add logic to see if the database exists
        return true;
    }

    public static void addEntryToRegister(String date, String desc, String memo, Double price, Double balance){
        Connection conn = connect();
        String sql = "insert into register values (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1, date);
            ps.setString(2, desc);
            ps.setString(3, memo);
            if(price > 0){
                ps.setDouble(4, price - price - price);
                ps.setString(5, "");
            }else{
                ps.setString(4, "");
                ps.setDouble(5, price + price + price);
            }
            ps.setDouble(6, balance);
            ps.setString(7, null);
            ps.setString(8, null);
            ps.setString(9, null);

            ps.execute();
        }catch(SQLException se){ se.printStackTrace(); }finally{ closeit(conn, ps); }
    }

    public static String getLedgerBalance(){
        Connection conn = connect();
        String sql = "select min(cast(balance as double)) as balance from register where date = (select max(date) from register)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            return rs.getString("balance");
        }catch(Exception e){ e.printStackTrace(); closeit(conn, ps); } finally{ closeit(conn, ps, rs); }
        return "ERROR";
    }

    public static String getTransactions(String date){
        Connection conn = connect();
        String result, sql = "";
        String days = Holder.daysTransaction;
        if(date.equalsIgnoreCase("")){
            sql = "select * from register where date >= strftime('%m', date('now','-" + days + " day')) ||'/' || " +
                    "strftime('%d', date('now','-" + days + " day')) || '/' ||strftime('%Y', date('now','-" + days + " day')) order by date desc";
            result = "Transactions for last " + days + " days:\n\n";
        }else{
            sql = "select * from register where date >= ? order by date desc";
            result = "Transactions:\n\n";
        }

        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            if(!date.equalsIgnoreCase("")){ ps.setString(1, date); }

            rs = ps.executeQuery();

            while(rs.next()){
                String tdate = rs.getString("Date");
                String desc = rs.getString("Description");
                String amt = "";
                if(rs.getString("Amount Debit").equalsIgnoreCase("")){
                    amt = "+" + rs.getString("Amount Credit");
                }else{
                    amt = rs.getString("Amount Debit").substring(1);
                }
                result += tdate.substring(0,5) + " ";
                if(desc.length() > 27){
                    result += desc.substring(0,27);
                }else{
                    result += GUIUtility.rightPad(desc, 27);
                }
                result += " $" + amt + "\n";
            }
        }catch(Exception e){ e.printStackTrace(); closeit(conn, ps, rs); } finally{ closeit(conn, ps, rs); }
        return result;
    }

    public static String getAvailableFunds(String ledgerBalance){
        Double bal = Double.parseDouble(ledgerBalance);
        Connection conn = connect();
        String sql = "select * from schedule where due < ( " +
                "  select min(due) from schedule where category = 'Paycheck')";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                Double one = Double.parseDouble(rs.getString("amount"));
                bal = bal - one;
            }
        }catch(SQLException se){ se.printStackTrace(); }finally{ closeit(conn, ps, rs); }
        DecimalFormat df2 = new DecimalFormat(".##");
        return df2.format(bal).toString();
    }

    public static void deleteFromSchedule(String input){
        String date = input.substring(0,5) + "/2017";
        String desc = input.substring(6,21).trim();
        String amount = input.substring(24).trim();
        String sql;

        Connection conn = connect();

        if(desc.equalsIgnoreCase("Paycheck")){
            sql = "delete from schedule where due = ? and trim(substr(description,0,16)) = ? and income = ?";
        }else {
            sql = "DELETE FROM schedule WHERE due = ? AND trim(substr(description,0,16)) = ? AND amount = ?";
        }

        PreparedStatement ps = null;

        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1, date);
            ps.setString(2, desc);
            ps.setString(3, amount);
            System.out.println(sql);
            System.out.println(date + ": " + desc + ": " + amount);
            ps.execute();
        }catch(Exception e){ e.printStackTrace(); closeit(conn, ps); } finally{ closeit(conn, ps); }
    }

    public static void addSchedule(String desc, String due, String amount, String income, String cat, String freq){
        Connection conn = connect();
        String sql = "insert into schedule values (?,?,?,?,?,?)";
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (desc.length() > 17) { desc = desc.substring(0,17); }

        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1, desc);
            ps.setString(2, due);
            ps.setString(3, amount);
            ps.setString(4, income);
            ps.setString(5, cat);
            ps.setString(6, freq);
            ps.execute();
        }catch(Exception e){ e.printStackTrace(); closeit(conn, ps); } finally{ closeit(conn, ps); }
    }

    public static ArrayList<String> getDebtTitles(){
        ArrayList debts = new ArrayList();

        Connection conn = connect();
        String sql = "select * from debt";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                debts.add(rs.getString("name"));
            }
        }catch(Exception e){ e.printStackTrace(); closeit(conn, ps); } finally{ closeit(conn, ps, rs); }

        return debts;
    }

    public static int uncategorizedItems(){
        Connection conn = connect();
        String sql = "select count(0) as total from register where category is NULL";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt("total");
        }catch(Exception e){ e.printStackTrace(); closeit(conn, ps); } finally{ closeit(conn, ps, rs); }
        return 0;
    }

    public static List getCategories(){
        ArrayList results = new ArrayList();

        Connection conn = connect();
        String sql = "select distinct category from register where category is NOT NULL and category not in ('') ";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()) {
                /*if(rs.getString("category").equalsIgnoreCase("") || rs.getString("category").isEmpty()){
                    continue;
                }else{
                    results.add(rs.getString("category"));
                }*/
                results.add(rs.getString("category"));
            }
        }catch(Exception e){ e.printStackTrace(); closeit(conn, ps); } finally{ closeit(conn, ps, rs); }
        return results;
    }

    public static DefaultListModel getFormattedDebts(){
        Connection conn = connect();
        DefaultListModel list = new DefaultListModel();
        String sql = "select * from debt";

        PreparedStatement ps = null;
        ResultSet rs = null;
        String result = "";
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                String name = rs.getString("name");

                if(name.length() > 20){
                    name = name.substring(0,20);
                }else{
                    name = GUIUtility.rightPad(name, 20);
                }

                String total = rs.getString("total");
                result += name + "  $" + total;

                list.addElement(result);
                result = "";
            }
            return list;
        }catch(SQLException se){ se.printStackTrace(); }finally{ closeit(conn, ps, rs); }
        return list;
    }

    public static DefaultListModel getCurrentSchedule(){
        Connection conn = connect();
        DefaultListModel list = new DefaultListModel();
        String sql = "select * from schedule where due < ( " +
                "  select min(due) from schedule where category = 'Paycheck')" +
                "UNION select * from schedule where due = (select min(due) from schedule " +
                " where category = 'Paycheck') and category = 'Paycheck' " +
                "order by due";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String result = "";
        try{
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                String due = rs.getString("due");
                String desc = rs.getString("description");
                if(desc.length() > 16) {
                    desc = desc.substring(0, 17);
                }
                result += due.substring(0,5) + " ";
                result += GUIUtility.rightPad(desc, 17);
                if(rs.getString("category").equalsIgnoreCase("Paycheck")){
                    result += "$" + GUIUtility.leftPad(rs.getString("income"), 7);
                }else{
                    result += "$" + GUIUtility.leftPad(rs.getString("amount"), 7);
                }

                list.addElement(result);
                result = "";
            }
            return list;
        }catch(SQLException se){ se.printStackTrace(); }finally{ closeit(conn, ps, rs); }
        return list;
    }

    public static String getNextIncomeDate(){ //TODO get next income day from schedule
        return "";
    }

    public static void closeit(ResultSet rs){ closeit(null, null, rs); }
    public static void closeit(Connection conn){ closeit(conn, null, null); }
    public static void closeit(PreparedStatement ps){ closeit(null, ps, null); }
    public static void closeit(Connection conn, ResultSet rs){ closeit(conn, null, rs); }
    public static void closeit(PreparedStatement ps, ResultSet rs){ closeit(null, ps, rs); }
    public static void closeit(Connection conn, PreparedStatement ps){ closeit(conn, ps, null); }

    public static void closeit(Connection conn, PreparedStatement ps, ResultSet rs){
        if(rs != null){ try{ rs.close(); } catch(SQLException se){ System.out.println("Failed closing ResultSet: " + se); } }
        if(ps != null){ try{ ps.close(); } catch(SQLException se){ System.out.println("Failed closing PreparedStatement: " + se); } }
        if(conn != null){ try{ conn.close(); } catch(SQLException se){ System.out.println("Failed closing connection: " + se); } }
    }
}
