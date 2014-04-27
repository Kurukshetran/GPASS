/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.benchevoor.spider;

import com.benchevoor.util.Averager;
import com.benchevoor.spider.SpiderSovereign.DataReceiver;
import com.benchevoor.spider.SpiderSovereign.AppIDFeeder;
import com.benchevoor.spider.datamodel.AppData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Barry Bostwick
 */
public class SpiderThread extends Thread {
    
    private final AppIDFeeder appIDFeeder;
    private final DataReceiver receiver;
    private final AtomicBoolean doStop = new AtomicBoolean(false);

    public SpiderThread(AppIDFeeder urlFeeder, DataReceiver receiver) {
        this.appIDFeeder = urlFeeder;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        AppData data = new AppData();
        
        while(!this.doStop.get()) {
            String appID = this.appIDFeeder.getAppID();
            if(appID == null) {
                //System.out.println("Wait for not null link.");
                continue;
            }
            
            InputStream is = null;
            BufferedReader br;
            String line;
            sb.setLength(0);
            
            try {
                URL url = new URL("https://play.google.com/store/apps/details?id=" + appID);
                
                is = url.openStream();  // throws an IOException
                br = new BufferedReader(new InputStreamReader(is));
                
                while ((line = br.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                try {
                    data.decode(appID, sb.toString());
                } catch (ParseException ex) {
                    Logger.getLogger(SpiderThread.class.getName()).log(Level.SEVERE, null, ex);
                }
//                System.out.println("-----------------------------------------------");
//                System.out.println("Appname: " + data.getAppName());
//                System.out.println("App ID: " + data.getAppStoreID());
//                System.out.println("Category: " + data.getCategory());
//                System.out.println("Price: " + data.getPriceCents());
//                System.out.println("Has in app purchases: " + data.hasInAppPurchases());
//                System.out.println("Total reviews: " + data.getTotalReviews());
//                System.out.println("Average rating: " + data.getAverageRating());
//                System.out.println("Five star ratings: " + data.getFiveStarReviews());
//                System.out.println("Four star ratings: " + data.getFourStarReviews());
//                System.out.println("Three star ratings: " + data.getThreeStarReviews());
//                System.out.println("Two star ratings: " + data.getTwoStarReviews());
//                System.out.println("One star ratings: " + data.getOneStarReviews());
//                System.out.println("Last update milli: " + data.getLastUpdateMillis());
//                System.out.println("App size: " + data.getAppSizeKB() + "KB");
//                System.out.println("Download count: " + data.getDownloadCount() + "+");
//                System.out.println("App version: " + data.getAppVersion());
//                System.out.println("Required API: " + data.getRequiredAPI());
//                System.out.println("Content rating: " + data.getContentRating());
                
                receiver.putData(data);
                
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException ioe) {
                    // nothing to see here
                }
            }
        }
    }
    
    /**
     * Stops this thread.
     */
    public void doStop() {
        this.doStop.set(true);
    }
    
    
    
}
