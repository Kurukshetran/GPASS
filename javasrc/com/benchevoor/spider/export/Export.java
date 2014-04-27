/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.benchevoor.spider.export;

import com.benchevoor.spider.database.DatabaseManager;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 *
 * @author benjaminchevoor
 */
public class Export {
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int num = 0;
        String location = null;
        
        while (num == 0) {
            System.out.println("\nEnter the export method: \n  1 = CSV\n");
            num = scanner.nextInt();
        }
        
        while (location == null || location.equals("")) {
            System.out.println("\nChoose an export location: \n");
            location = scanner.next();
        }
        
        ExportCallback callback = new ExportCallback() {

            @Override
            public void statusUpdate(int update) {
                if(update % 10000 == 0) {
                    System.out.println(update);
                }
            }
        };
        
        System.out.println("\nOpening database...\n");
        
        DatabaseManager dbm = new DatabaseManager(DatabaseManager.DB_DEFAULT_LOCATION);
        
        System.out.println("\nFinished opening database. Exporting.\n");

        if(num == 1) {
            ResultSet rs = dbm.getData();
            
            CSV.export(rs, location, callback);
        } else {
            System.out.println("\nUnknown export method...\n");
        }
        
        System.out.println("\nDone.\n");
    }
    
}
