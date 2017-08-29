package com.jk.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AIUtility {

    public static String whatCategoryAmI(String input){
        Connection conn = DBUtil.connect();
        String sql = "select * from register where Description like ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            ps = conn.prepareStatement(sql);
            ps.setString(1, input);
            rs = ps.executeQuery();
            rs.next();
            return rs.getString("category");
        }catch(Exception e){ DBUtil.closeit(conn, ps, rs); } finally{ DBUtil.closeit(conn, ps, rs); }
        return "";
    }
}
