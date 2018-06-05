package com.utility;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;

public class ExtentManager {
	private static ExtentReports extent;
	public static Properties Param = null;
	
	public static ExtentReports getInstance() {
		Param = new Properties();
		try {
		FileInputStream fip = new FileInputStream(System.getProperty("user.dir")+"//src//test//java//com//property//Param.properties");
		Param.load(fip);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (extent == null) {
			Date d=new Date();
			String fileName=d.toString().replace(":", "_").replace(" ", "_")+".html";
			extent = new ExtentReports(System.getProperty("user.dir")+"//Reports//QPPCM_RegressionReport.html", true, DisplayOrder.OLDEST_FIRST);

			
			extent.loadConfig(new File(System.getProperty("user.dir")+"//ReportsConfig.xml"));
			// optional
			extent.addSystemInfo("Application Name", "QPPCM").addSystemInfo(
					"Selenium Version", "3.1.0").addSystemInfo("Organization Name", "<a href='https://qpp.cms.gov'>QPPCM</a>");
			
			if(Param.getProperty("zoneURLunderTest").contains("impl")){
				extent.addSystemInfo("Environment", "<a href='https://qpp.cms.gov'>IMPL</a>");
			}else{extent.addSystemInfo("Environment", "<a href='https://qpp.cms.gov/'>TEST</a>");}

		}
		return extent;
	}
}
