/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.benchevoor.spider;

import com.benchevoor.spider.database.DatabaseManager;
import com.benchevoor.util.Averager;
import com.benchevoor.spider.datamodel.AppData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Barry Bostwick
 */
public class SpiderSovereign {
    
    private final DatabaseManager dbMan;
    private final List<SpiderThread> spiders = new LinkedList<>();
    private final float spiderCount;
    private int appCount = 1;
    private final Object lock = new Object();
    private Averager avg = new Averager(25);
    private long intermediateTime = System.nanoTime();

    
    public static interface AppIDFeeder {
        public String getAppID();
    }
    
    public static interface DataReceiver {
        public void putData(AppData data);
    }

    public SpiderSovereign(int spiders, String DB_PATH) throws SQLException {
        AppIDFeeder feeder = new AppIDFeeder() {

            @Override
            public String getAppID() {
                try {
                    return dbMan.removeAppID();
                } catch (SQLException ex) {
                    Logger.getLogger(SpiderSovereign.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                return null;
            }
        };
        
        DataReceiver receiver = new DataReceiver() {

            @Override
            public void putData(AppData data) {
                try {
                    synchronized(lock) {
                        double differenceSeconds = 1 / ((float) (System.nanoTime() - intermediateTime) / 1_000_000_000);
                        avg.add(differenceSeconds);

                        System.out.println("App #" + appCount++ + "   \t" + (avg.getAverage() / spiderCount) + " aps");
                        
                        intermediateTime = System.nanoTime();
                    }
                    
                    dbMan.addAppData(data);
                } catch (SQLException ex) {
                    Logger.getLogger(SpiderSovereign.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        this.dbMan = new DatabaseManager(DB_PATH);
        
        this.appCount = this.dbMan.getAppStartCount();
        
        for(int i = 0; i < spiders; i++) {
            SpiderThread spider = new SpiderThread(feeder, receiver);
            spider.start();
            this.spiders.add(spider);
        }
        
        this.spiderCount = this.spiders.size();
    }

    @Override
    protected void finalize() throws Throwable {
        this.doStop();
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void doStop() throws InterruptedException, SQLException {
        int count = 1;
        for(SpiderThread thread : this.spiders) {
            System.out.println("Stopping spider #" + count + "...");
            thread.doStop();
            thread.join();
            System.out.println("Stopped spider #" + count + ".");
            
            count++;
        }
        
        this.dbMan.close();
    }
    
}
