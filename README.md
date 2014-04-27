GPASS
=====

"Google Play App Store Spider"

This repository contains a project built for Professor Grinstein's "Visual Analytics" class (91.540 Spring 2014 semester) at UMass Lowell. The project's purpose was to create a visualization of the Google Play App Store. 

GPASS is a multi-threaded Java application that "spiders" across the Android app store, extracting all publically available information displayed on the page. Those attributes are as follows: name, appStoreID, category, priceInCents, hasInAppPurchases, totalReviews, averageRating, fiveStars, fourStars, threeStars, twoStars, oneStar, lastUpdateMillis, appSizeKB, downloadCount, appVersion, requiredAPI, contentRating. GPASS stores the app information into a HyperSQL DB.

There is the source code for the GPASS application under the javasrc/ folder. 
The libraries used (HyperSQL http://hsqldb.org/) can be found under lib/. 
There is an output in CSV format under extras/. This output contains 405,709 unique rows resulting from running the GPASS application for ~3 days. There is a performance issue when running the program for this long; suspected database limitation.
The PowerPoint used to present the project to the class can also be found under extras/.



USAGE
=====

There are two main() files in the source code: one to run the application, one to export the information stored in the database to an external CSV file. The usages are as follows:

GPASS main (com.benchevoor.spider.Main): 

GPASS.jar [arg0] [arg1]
arg0 = number of spider threads
arg1 = database path


Export main (com.benchevoor.spider.export.Export):
Export.jar (no arguments, arguments inputted at run time)
arg0 = export method (1 = CSV) (CSV only export method implemented as of right now)
arg1 = export location



TODO
=====

 - One of the biggest issues is the perfomance of the database after a few days running. In my experience, the app can process ~20 apps per second in the beginning. After 400,000 apps stored, this number falls to 1 app every two seconds.
 - The "apps per second" (APS) calculation printed out at run time isn't correct.
 - There are some poor code design issues (lack of abstraction, package circular dependencies)
 
