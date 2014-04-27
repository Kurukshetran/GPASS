/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.benchevoor.spider.datamodel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Barry Bostwick
 */
public class AppData {
    
    private static final String appNameMarkerStart = "<div class=\"document-title\" itemprop=\"name\"> <div>";
    private static final String appNameMarkerEnd = "</div>";
    private static final String categoryMarkerStart = "<span itemprop=\"genre\">";
    private static final String categoryMarkerEnd = "</span>";
    private static final String priceMarkerStart = "itemprop=\"offerType\"> <meta content=\"$";
    private static final String priceMarkerEnd = "\"";
    private static final String inAppPurchasesMarker = "Offers in-app purchases";
    private static final String averageRatingMarkerStart = "itemtype=\"http://schema.org/AggregateRating\"> <meta content=\"";
    private static final String averageRatingMarkerEnd = "\" itemprop=\"";
    private static final String totalReviewsMarkerStart = "<span class=\"reviews-num\">";
    private static final String totalReviewsMarkerEnd = "</span>";
    private static final String fiveStarMarkerStart = "</span>5 </span> <span class=\"bar\"";
    private static final String fourStarMarkerStart = "</span>4 </span> <span class=\"bar\"";
    private static final String threeStarMarkerStart = "</span>3 </span> <span class=\"bar\"";
    private static final String twoStarMarkerStart = "</span>2 </span> <span class=\"bar\"";
    private static final String oneStarMarkerStart = "</span>1 </span> <span class=\"bar\"";
    private static final String barIntermediate = "<span class=\"bar-number\">";
    private static final String starMarkerEnd = "</span>";
    private static final String lastUpdateMarkerStart = "<div class=\"content\" itemprop=\"datePublished\">";
    private static final String lastUpdateMarkerEnd = "</div>";
    private static final String appSizeMarkerStart = "<div class=\"content\" itemprop=\"fileSize\">";
    private static final String appSizeMarkerEnd = "</div>";
    private static final String appSizeMegabytes = "M";
    private static final String appSizeKilobytes = "k";
    private static final String downloadCountMarkerStart = "<div class=\"content\" itemprop=\"numDownloads\">";
    private static final String downloadCountMarkerEnd = " - ";
    private static final String appVersionMarkerStart = "<div class=\"content\" itemprop=\"softwareVersion\">";
    private static final String appVersionMarkerEnd = "</div>";
    private static final String requiredAPIMarkerStart = "<div class=\"content\" itemprop=\"operatingSystems\">";
    private static final String requiredAPIMarkerEnd = " and";
    private static final String contentRatingMarkerStart = "<div class=\"content\" itemprop=\"contentRating\">";
    private static final String contentRatingMarkerEnd = "</div>";
    private static final String appPageLinkMarkerStart = "/store/apps/details?id=";
    private static final String excludeLanguageLinkMarker1 = "hl=";
    private static final String excludeLanguageLinkMarker2 = "&amp";
    
    private String appStoreID;
    private String appName;
    private String category;
    private int priceCents;
    private boolean hasInAppPurchases;
    private float averageRating;
    private int totalReviews;
    private int fiveStarReviews;
    private int fourStarReviews;
    private int threeStarReviews;
    private int twoStarReviews;
    private int oneStarReviews;
    private long lastUpdateMillis;
    private int appSizeKB;
    private int downloadCount;
    private String appVersion;
    private int requiredAPI;
    private String contentRating;
    private final List<String> additionalLinks = new ArrayList<>();
    
    public void decode(String url, String webPage) throws ParseException {
        this.appStoreID = url;
        
        int indexStart = webPage.indexOf(appNameMarkerStart);
        int indexEnd = webPage.indexOf(appNameMarkerEnd, indexStart + appNameMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.appName = cleanName(webPage.substring(indexStart + appNameMarkerStart.length(), indexEnd).trim());
        } else {
            this.appName = null;
        }
        
        indexStart = webPage.indexOf(categoryMarkerStart);
        indexEnd = webPage.indexOf(categoryMarkerEnd, indexStart + categoryMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.category = webPage.substring(indexStart + categoryMarkerStart.length(), indexEnd).trim();
        } else {
            this.category = null;
        }
        
        indexStart = webPage.indexOf(priceMarkerStart);
        indexEnd = webPage.indexOf(priceMarkerEnd, indexStart + priceMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.priceCents = (int) (Float.parseFloat(webPage.substring(indexStart + priceMarkerStart.length(), indexEnd).trim()) * 100);
        } else {
            this.priceCents = 0;
        }
        
        indexStart = webPage.indexOf(inAppPurchasesMarker);
        if(indexStart >= 0) {
            this.hasInAppPurchases = true;
        } else {
            this.hasInAppPurchases = false;
        }
        
        indexStart = webPage.indexOf(averageRatingMarkerStart);
        indexEnd = webPage.indexOf(averageRatingMarkerEnd, indexStart + averageRatingMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.averageRating = Float.parseFloat(cleanNumber(webPage.substring(indexStart + averageRatingMarkerStart.length(), indexEnd)));
        } else {
            this.averageRating = -1;
        }
        
        indexStart = webPage.indexOf(totalReviewsMarkerStart);
        indexEnd = webPage.indexOf(totalReviewsMarkerEnd, indexStart + totalReviewsMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.totalReviews = Integer.parseInt(cleanNumber(webPage.substring(indexStart + totalReviewsMarkerStart.length(), indexEnd)));
        } else {
            this.totalReviews = -1;
        }
        
        indexStart = webPage.indexOf(fiveStarMarkerStart);
        indexStart = webPage.indexOf(barIntermediate, indexStart + fiveStarMarkerStart.length());
        indexEnd = webPage.indexOf(starMarkerEnd, indexStart + barIntermediate.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.fiveStarReviews = Integer.parseInt(cleanNumber(webPage.substring(indexStart + barIntermediate.length(), indexEnd)));
        } else {
            this.fiveStarReviews = -1;
        }
        
        indexStart = webPage.indexOf(fourStarMarkerStart);
        indexStart = webPage.indexOf(barIntermediate, indexStart + fiveStarMarkerStart.length());
        indexEnd = webPage.indexOf(starMarkerEnd, indexStart + barIntermediate.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.fourStarReviews = Integer.parseInt(cleanNumber(webPage.substring(indexStart + barIntermediate.length(), indexEnd)));
        } else {
            this.fourStarReviews = -1;
        }
        
        indexStart = webPage.indexOf(threeStarMarkerStart);
        indexStart = webPage.indexOf(barIntermediate, indexStart + threeStarMarkerStart.length());
        indexEnd = webPage.indexOf(starMarkerEnd, indexStart + barIntermediate.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.threeStarReviews = Integer.parseInt(cleanNumber(webPage.substring(indexStart + barIntermediate.length(), indexEnd)));
        } else {
            this.threeStarReviews = -1;
        }
        
        indexStart = webPage.indexOf(twoStarMarkerStart);
        indexStart = webPage.indexOf(barIntermediate, indexStart + twoStarMarkerStart.length());
        indexEnd = webPage.indexOf(starMarkerEnd, indexStart + barIntermediate.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.twoStarReviews = Integer.parseInt(cleanNumber(webPage.substring(indexStart + barIntermediate.length(), indexEnd)));
        } else {
            this.twoStarReviews = -1;
        }
        
        indexStart = webPage.indexOf(oneStarMarkerStart);
        indexStart = webPage.indexOf(barIntermediate, indexStart + oneStarMarkerStart.length());
        indexEnd = webPage.indexOf(starMarkerEnd, indexStart + barIntermediate.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.oneStarReviews = Integer.parseInt(cleanNumber(webPage.substring(indexStart + barIntermediate.length(), indexEnd)));
        } else {
            this.oneStarReviews = -1;
        }
        
        indexStart = webPage.indexOf(lastUpdateMarkerStart);
        indexEnd = webPage.indexOf(lastUpdateMarkerEnd, indexStart + lastUpdateMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            DateFormat fmt = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            Date d = fmt.parse(webPage.substring(indexStart + lastUpdateMarkerStart.length(), indexEnd).trim());
            this.lastUpdateMillis = d.getTime();
        } else {
            this.lastUpdateMillis = -1;
        }
        
        indexStart = webPage.indexOf(appSizeMarkerStart);
        indexEnd = webPage.indexOf(appSizeMarkerEnd, indexStart + appSizeMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            String size = webPage.substring(indexStart + appSizeMarkerStart.length(), indexEnd).trim();
            
            if(size.contains(appSizeMegabytes)) {
                this.appSizeKB = (int) (Float.parseFloat(cleanNumber(size.replace(appSizeMegabytes, ""))) * 1024);
            } else if (size.contains(appSizeKilobytes)) {
                this.appSizeKB = (int) (Float.parseFloat(cleanNumber(size.replace(appSizeKilobytes, ""))));
            } else {
                this.appSizeKB = -1;
            }
        } else {
            this.appSizeKB = -1;
        }
        
        indexStart = webPage.indexOf(downloadCountMarkerStart);
        indexEnd = webPage.indexOf(downloadCountMarkerEnd, indexStart + downloadCountMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            String downloads = webPage.substring(indexStart + downloadCountMarkerStart.length(), indexEnd);
            
            this.downloadCount = Integer.parseInt(cleanNumber(downloads));
        } else {
            this.downloadCount = -1;
        }
        
        indexStart = webPage.indexOf(requiredAPIMarkerStart);
        indexEnd = webPage.indexOf(requiredAPIMarkerEnd, indexStart + requiredAPIMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            String version = webPage.substring(indexStart + requiredAPIMarkerStart.length(), indexEnd).trim();
            
            if(version == null) {
                this.requiredAPI = -1;
            } else if("4.4".equals(version)) {
                this.requiredAPI = 19;
            } else if ("4.3".equals(version)) {
                this.requiredAPI = 18;
            } else if ("4.2".equals(version) || "4.2.1".equals(version) || "4.2.2".equals(version)) {
                this.requiredAPI = 17;
            } else if ("4.1".equals(version) || "4.2.1".equals(version)) {
                this.requiredAPI = 16;
            } else if ("4.0.3".equals(version) || "4.0.4".equals(version)) {
                this.requiredAPI = 15;
            } else if ("4.0".equals(version) || "4.0.1".equals(version) || "4.0.2".equals(version)) {
                this.requiredAPI = 14;
            } else if ("3.2".equals(version)) {
                this.requiredAPI = 13;
            } else if (version.contains("3.1")) {
                this.requiredAPI = 12;
            } else if (version.contains("3.0")) {
                this.requiredAPI = 11;
            } else if ("2.3.3".equals(version) || "2.3.4".equals(version)) {
                this.requiredAPI = 10;
            } else if ("2.3".equals(version) || "2.3.1".equals(version) || "2.3.2".equals(version)) {
                this.requiredAPI = 9;
            } else if (version.contains("2.2")) {
                this.requiredAPI = 8;
            } else if (version.contains("2.1")) {
                this.requiredAPI = 7;
            } else if ("2.0.1".equals(version)) {
                this.requiredAPI = 6;
            } else if ("2.0".equals(version)) {
                this.requiredAPI = 5;
            } else if ("1.6".equals(version)) {
                this.requiredAPI = 4;
            } else if ("1.5".equals(version)) {
                this.requiredAPI = 3;
            } else if ("1.1".equals(version)) {
                this.requiredAPI = 2;
            } else if ("1.0".equals(version)) {
                this.requiredAPI = 1;
            } else {
                this.requiredAPI = -1;
            }
        } else {
            this.requiredAPI = -1;
        }
        
        indexStart = webPage.indexOf(appVersionMarkerStart);
        indexEnd = webPage.indexOf(appVersionMarkerEnd, indexStart + appVersionMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.appVersion = webPage.substring(indexStart + appVersionMarkerStart.length(), indexEnd).trim();
        } else {
            this.appVersion = null;
        }
        
        indexStart = webPage.indexOf(contentRatingMarkerStart);
        indexEnd = webPage.indexOf(contentRatingMarkerEnd, indexStart + contentRatingMarkerStart.length());
        if(indexStart >= 0 && indexEnd >= 0) {
            this.contentRating = webPage.substring(indexStart + contentRatingMarkerStart.length(), indexEnd).trim();
        } else {
            this.contentRating = null;
        }
        
        indexStart = 0;
        indexEnd = 0;
        this.additionalLinks.clear();
        
        while(indexStart >= 0 && indexEnd >= 0) {
            indexStart = webPage.indexOf(appPageLinkMarkerStart, indexStart);
            indexEnd = Math.min(
                    Math.min(webPage.indexOf(")", indexStart + appPageLinkMarkerStart.length()), webPage.indexOf(",", indexStart + appPageLinkMarkerStart.length())),
                    Math.min(webPage.indexOf("\"", indexStart + appPageLinkMarkerStart.length()), webPage.indexOf("<", indexStart + appPageLinkMarkerStart.length())));
            
            if (indexStart >= 0 && indexEnd >= 0) {
                String link = webPage.substring(indexStart + appPageLinkMarkerStart.length(), indexEnd);

                if (!link.contains(excludeLanguageLinkMarker1) && !link.contains(excludeLanguageLinkMarker2)) {
                    this.additionalLinks.add(link);
                }

                indexStart++;
            }
        }
    }
    
    public static String cleanName(String name) {
        return name.replace("&amp;", "and").replace("'", "").trim();
    }
    
    public static String cleanNumber(String num) {
        return num.trim().replace(",", "");
    } 

    public String getAppName() {
        return appName;
    }
    
    public String getAppStoreID() {
        return this.appStoreID;
    }

    public List<String> getAdditionalLinks() {
        return additionalLinks;
    }

    public String getCategory() {
        return category;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public int getFiveStarReviews() {
        return fiveStarReviews;
    }

    public int getFourStarReviews() {
        return fourStarReviews;
    }

    public int getThreeStarReviews() {
        return threeStarReviews;
    }

    public int getTwoStarReviews() {
        return twoStarReviews;
    }

    public int getOneStarReviews() {
        return oneStarReviews;
    }

    public long getLastUpdateMillis() {
        return lastUpdateMillis;
    }

    public int getAppSizeKB() {
        return appSizeKB;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public boolean hasInAppPurchases() {
        return hasInAppPurchases;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public int getRequiredAPI() {
        return requiredAPI;
    }

    public String getContentRating() {
        return contentRating;
    }

    public void setAppStoreID(String appStoreID) {
        this.appStoreID = appStoreID;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPriceCents(int priceCents) {
        this.priceCents = priceCents;
    }

    public void setHasInAppPurchases(boolean hasInAppPurchases) {
        this.hasInAppPurchases = hasInAppPurchases;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public void setFiveStarReviews(int fiveStarReviews) {
        this.fiveStarReviews = fiveStarReviews;
    }

    public void setFourStarReviews(int fourStarReviews) {
        this.fourStarReviews = fourStarReviews;
    }

    public void setThreeStarReviews(int threeStarReviews) {
        this.threeStarReviews = threeStarReviews;
    }

    public void setTwoStarReviews(int twoStarReviews) {
        this.twoStarReviews = twoStarReviews;
    }

    public void setOneStarReviews(int oneStarReviews) {
        this.oneStarReviews = oneStarReviews;
    }

    public void setLastUpdateMillis(long lastUpdateMillis) {
        this.lastUpdateMillis = lastUpdateMillis;
    }

    public void setAppSizeKB(int appSizeKB) {
        this.appSizeKB = appSizeKB;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setRequiredAPI(int requiredAPI) {
        this.requiredAPI = requiredAPI;
    }

    public void setContentRating(String contentRating) {
        this.contentRating = contentRating;
    }
    
    
}
