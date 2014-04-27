/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.benchevoor.spider;

import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author Benjamin Chevoor
 */
public class Main {
    
    public static void main(String[] args) throws SQLException, InterruptedException {
        if(args.length < 2) {
            System.out.println("Usage:");
            System.out.println("\t GPASS.jar [arg0] [arg1]");
            System.out.println("\t [arg0] = number of spider threads");
            System.out.println("\t [arg1] = database path");
            System.out.println("Exiting");
            System.out.println("");
            return;
        }
        
        SpiderSovereign ss = new SpiderSovereign(Integer.parseInt(args[0]), args[1]);
        
        Scanner in = new Scanner(System.in);
        
        System.out.println("Press enter key to stop application...");
        
        in.hasNext();
        
        System.out.println("Stopping application...");
        
        ss.doStop();
        
        System.out.println("Application stopped!");
    }
    
}
