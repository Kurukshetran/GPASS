/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.benchevoor.spider.export;

import com.benchevoor.spider.database.DatabaseManager;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author benjaminchevoor
 */
public class CSV {

    public static void export(ResultSet rs, String location, ExportCallback callback) throws FileNotFoundException, SQLException {
        PrintWriter pw = new PrintWriter(location);

        ResultSetMetaData md = rs.getMetaData();
        int rowCount = 0;
        
        StringBuilder sb = new StringBuilder();
        
        List<String> headings = DatabaseManager.getAppDataHeadings();
        
        for(String heading : headings) {
            sb.append(heading);
            sb.append(", ");
        }
        
        pw.append(sb.substring(0, sb.length()-2) + "\n");
        
        sb.setLength(0);

        while (rs.next()) {
            for (int i = 1; i <= md.getColumnCount(); i++) {
                sb.append(rs.getString(i));
                
                if(i < md.getColumnCount()) {
                    sb.append(", ");
                }
            }
            
            sb.append("\n");
            
            pw.append(sb.toString());
            
            sb.setLength(0);
            
            callback.statusUpdate(rowCount++);
        }
    }

}
