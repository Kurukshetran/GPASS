/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.benchevoor.spider.database;

import com.benchevoor.spider.datamodel.AppData;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Barry Bostwick
 */
public class DatabaseManager {
    
    public static final String DB_DEFAULT_LOCATION = "F:\\Dropbox\\GPASS\\2\\GPASS_DB";
    
    private final String DB_LOCATION;
    
    private static final String tb_Links = "links";
    private static final String tb_Links_appStoreID = "appStoreID";
    
    private static final String tb_AppData = "appData";
    private static final String tb_AppData_name = "name";
    private static final String tb_AppData_storeID = "appStoreID";
    private static final String tb_AppData_category = "category";
    private static final String tb_AppData_priceCents = "priceInCents";
    private static final String tb_AppData_hasInAppPurchases = "hasInAppPurchases";
    private static final String tb_AppData_totalReviews = "totalReviews";
    private static final String tb_AppData_averageRating = "averageRating";
    private static final String tb_AppData_fiveStars = "fiveStars";
    private static final String tb_AppData_fourStars = "fourStars";
    private static final String tb_AppData_threeStars = "threeStars";
    private static final String tb_AppData_twoStars = "twoStars";
    private static final String tb_AppData_oneStar = "oneStar";
    private static final String tb_AppData_lastUpdate = "lastUpdateMillis";
    private static final String tb_AppData_appSizeKB = "appSizeKB";
    private static final String tb_AppData_downloadCount = "downloadCount";
    private static final String tb_AppData_appVersion = "appVersion";
    private static final String tb_AppData_requiredAPI = "requiredAPI";
    private static final String tb_AppData_contentRating = "contentRating";
    
    private static final String tb_Links_CREATE_STATEMENT = "CREATE TABLE " + tb_Links + " (" +
            tb_Links_appStoreID + " VARCHAR(255) PRIMARY KEY)";
    
    private static final String tb_AppData_CREATE_STATEMENT = "CREATE TABLE " + tb_AppData + " (" +
            tb_AppData_name + " VARCHAR(255), " + 
            tb_AppData_storeID + " VARCHAR(255) PRIMARY KEY, " +
            tb_AppData_category + " VARCHAR(63), " + 
            tb_AppData_priceCents + " INTEGER, " + 
            tb_AppData_hasInAppPurchases + " BOOLEAN, " + 
            tb_AppData_totalReviews + " INTEGER, " + 
            tb_AppData_averageRating + " FLOAT, " + 
            tb_AppData_fiveStars + " INTEGER, " +
            tb_AppData_fourStars + " INTEGER, " + 
            tb_AppData_threeStars + " INTEGER, " + 
            tb_AppData_twoStars + " INTEGER, " + 
            tb_AppData_oneStar + " INTEGER, " +
            tb_AppData_lastUpdate + " BIGINT, " +
            tb_AppData_appSizeKB + " INTEGER, " + 
            tb_AppData_downloadCount + " INTEGER, " +
            tb_AppData_appVersion + " VARCHAR(31), " + 
            tb_AppData_requiredAPI + " INTEGER, " + 
            tb_AppData_contentRating + " VARCHAR(31))";
    
    private static final String selectTop1Link = "SELECT TOP 1 * FROM " + tb_Links;
    private static final String deleteLink = "DELETE FROM " + tb_Links + " WHERE " + tb_Links_appStoreID + "='";
    
    private final Connection c;
    private final Statement s;
    private int appStartCount = 0;
    
    public DatabaseManager(String DB_PATH) throws SQLException {
        this.DB_LOCATION = DB_PATH;
        
        this.c = DriverManager.getConnection("jdbc:hsqldb:file:" + DB_LOCATION + ";shutdown=true", "SA", "");
        
        this.s = this.c.createStatement();
        
        if(!verifyTables()) {
            createTables();
            
            this.addAppID("com.benchevoor.huepro");
        }
        
        ResultSet rs = this.s.executeQuery("SELECT COUNT(*) as c FROM " + tb_AppData);
        
        if(rs.next()) {
            this.appStartCount = rs.getInt("c");
        } else {
            this.appStartCount = 0;
        }
    }
    
    public final synchronized void addAppID(String id) throws SQLException {        
        String sql = "SELECT * FROM " + tb_AppData + " WHERE " + tb_AppData_storeID + "=\'" + id + "\'";
        
        try (ResultSet rs = this.s.executeQuery(sql)) {
            if(!rs.next()) {
                sql = "INSERT INTO " + tb_Links + " VALUES('" + id + "')";
                
                try {
                    this.s.executeUpdate(sql);
                } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                    //ignore
                }
            }
        }
    }
    
    public synchronized void addAppData(AppData data) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tb_AppData).append(" VALUES(");
        sb.append("'").append(data.getAppName()).append("', "); 
        sb.append("'").append(data.getAppStoreID()).append("', ");
        sb.append("'").append(data.getCategory()).append("', ");
        sb.append(data.getPriceCents()).append(", ");
        sb.append(data.hasInAppPurchases()).append(", ");
        sb.append(data.getTotalReviews()).append(", ");
        sb.append(data.getAverageRating()).append(", ");
        sb.append(data.getFiveStarReviews()).append(", ");
        sb.append(data.getFourStarReviews()).append(", ");
        sb.append(data.getThreeStarReviews()).append(", ");
        sb.append(data.getTwoStarReviews()).append(", ");
        sb.append(data.getOneStarReviews()).append(", ");
        sb.append(data.getLastUpdateMillis()).append(", ");
        sb.append(data.getAppSizeKB()).append(", ");
        sb.append(data.getDownloadCount()).append(", ");
        sb.append("'").append(data.getAppVersion()).append("', ");
        sb.append(data.getRequiredAPI()).append(", ");
        sb.append("'").append(data.getContentRating()).append("')");
        
        try {
            this.s.executeUpdate(sb.toString());
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            //ignore
        } catch (SQLException e) {
            System.out.println("Error with: " + sb.toString());
            return;
        }
        
        for(String link : data.getAdditionalLinks()) {
            this.addAppID(link);
        }
    }
    
    public void close() throws SQLException {
        this.s.close();
        this.c.close();
    }
    
    private boolean verifyTables() throws SQLException {
        boolean linksExists = false;
        boolean appDataExists = false;
        
        DatabaseMetaData meta = c.getMetaData();
        ResultSet res = meta.getTables(null, null, null,
                new String[] {"TABLE"});
        while (res.next()) {
            if(tb_Links.equalsIgnoreCase(res.getString("TABLE_NAME"))) {
                linksExists = true;
            } else if (tb_AppData.equalsIgnoreCase(res.getString("TABLE_NAME"))) {
                appDataExists = true;
            }
        }
        res.close();
        
        return linksExists && appDataExists;
    }
    
    public synchronized String removeAppID() throws SQLException {
        ResultSet rs = this.s.executeQuery(selectTop1Link);
        
        if(rs.next()) {
            String appID = rs.getString(tb_Links_appStoreID);
            
            this.s.executeUpdate(deleteLink + appID + "'");
            
            return appID;
        }
        
        rs.close();
        
        return null;
    }
    
    private void createTables() throws SQLException {
        Statement s = c.createStatement();
        s.executeUpdate(tb_Links_CREATE_STATEMENT);
        
        s = c.createStatement();
        s.executeUpdate(tb_AppData_CREATE_STATEMENT);
    }

    public ResultSet getData() throws SQLException {
        return this.s.executeQuery("SELECT * FROM " + DatabaseManager.tb_AppData);
    }
    
    public int getAppStartCount() { 	 	
        return appStartCount; 	 	
    } 
    
    public static List<String> getAppDataHeadings() {
        List<String> headings = new LinkedList<>();
                
        headings.add(tb_AppData_name);
        headings.add(tb_AppData_storeID);
        headings.add(tb_AppData_category);
        headings.add(tb_AppData_priceCents);
        headings.add(tb_AppData_hasInAppPurchases);
        headings.add(tb_AppData_totalReviews);
        headings.add(tb_AppData_averageRating);
        headings.add(tb_AppData_fiveStars);
        headings.add(tb_AppData_fourStars);
        headings.add(tb_AppData_threeStars);
        headings.add(tb_AppData_twoStars);
        headings.add(tb_AppData_oneStar);
        headings.add(tb_AppData_lastUpdate);
        headings.add(tb_AppData_appSizeKB);
        headings.add(tb_AppData_downloadCount);
        headings.add(tb_AppData_appVersion);
        headings.add(tb_AppData_requiredAPI);
        headings.add(tb_AppData_contentRating);
        
        return headings;
    }
}
