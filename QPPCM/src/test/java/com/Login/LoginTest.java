//package com.Login;
//
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Hashtable;
//
//import org.testng.SkipException;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import org.testng.asserts.SoftAssert;
//import com.relevantcodes.extentreports.ExtentReports;
//import com.relevantcodes.extentreports.ExtentTest;
//import com.relevantcodes.extentreports.LogStatus;
//import com.utility.DataUtil;
//import com.utility.Read_XLS;
//
//
//public class LoginTest extends LoginBase{
//	String testCaseName="LoginTest";
//	SoftAssert softAssert;
//	Read_XLS FilePath= TestCaseListExcelUSERLOGIN;
//	
//	
//	
//	@Test(dataProvider="getData")
//	public void doLoginTest(Hashtable<String,String> data){
//		
//		test = rep.startTest("LoginTest");
//		test.log(LogStatus.INFO, data.toString());
//		if(data.get("Runmode").equals("N") || DataUtil.isSkip(FilePath, testCaseName)){
//			test.log(LogStatus.SKIP, "Skipping the test as runmode is N");
//			throw new SkipException("Skipping the test as runmode is N");
//		}
//	try{
//		loadWebBrowser();
//	
//	}catch(Exception f){
//		f.printStackTrace();
//	}
//	
////		openBrowser(data.get("Browser"));
////		navigate("appurl");
////		boolean actualResult=doLogin(data.get("Username"), data.get("Password"));
////		
////		boolean expectedResult=false;
////		if(data.get("ExpectedResult").equals("Y"))
////			expectedResult=true;
////		else
////			expectedResult=false;
////		
////		if(expectedResult!=actualResult)
////			reportFailure("Login Test Failed.");
////		
////		reportPass("Login Test Passed");
//		
//
//	}
//	
//	
//	@BeforeMethod
//	public void before() throws IOException{
//		init();
//		softAssert = new SoftAssert();
//	}
//	
//	
//	@AfterMethod
//	public void quit(){
//		try{
//			softAssert.assertAll();
//		}catch(Error e){
//			test.log(LogStatus.FAIL, e.getMessage());
//		}
//		if(rep!=null){
//			rep.endTest(test);
//			rep.flush();
//		}
//		
//		if(driver!=null)
//			driver.quit();
//	}
//	
//	@DataProvider(parallel=true)
//	public Object[][] getData(){			
////		xls = new Read_XLS(FilePath);
//		return DataUtil.getData(TestCaseListExcelUSERLOGIN, testCaseName);
//		
//	}
//
//}
