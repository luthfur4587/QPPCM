package com.ApplicationDataMigration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.utility.Read_XLS;
import com.utility.SuiteUtility;

import atu.testrecorder.ATUTestRecorder;
import atu.testrecorder.exceptions.ATUTestRecorderException;

public class DownloadAttachments extends ApplicationDataMigrationBase {

	Read_XLS FilePath = null;	
	String SheetName = null;
	String TestCaseName = null;	
	String ToRunColumnNameTestCase = null;
	String ToRunColumnNameTestData = null;
	String TestDataToRun[]=null;
	static boolean TestCasePass=true;
	static int DataSet=-1;	
	static boolean Testskip=false;
	static boolean Testfail=false;
	SoftAssert s_assert =null;
	String Result;
	int rowNum = -1;
	String failingColumns;
	ATUTestRecorder recorder;
	
	@BeforeTest
	public void checkCaseToRun() throws IOException, InterruptedException, ATUTestRecorderException {
		//Called init() function from SuiteBase class to Initialize .xls Files
		init();	
		//To set Campaign.xls file's path In FilePath Variable.
		FilePath = TestCaseListExcelDataMigration;		
		TestCaseName = this.getClass().getSimpleName();	
		//SheetName to check CaseToRun flag against test case.
		SheetName = "TestCasesList";
		//Name of column In TestCasesList Excel sheet.
		ToRunColumnNameTestCase = "CaseToRun";
		//Name of column In Test Case Data sheets.
		ToRunColumnNameTestData = "DataToRun";
		//Bellow given syntax will Insert log In applog.log file.
		Add_Log.info(TestCaseName+" : Execution started.");
//		extentReportStart(TestCaseName, SuiteName);
				
		//To check test case's CaseToRun = Y or N In related excel sheet.
		//If CaseToRun = N or blank, Test case will skip execution. Else It will be executed.
		if(!SuiteUtility.checkToRunUtility(FilePath, SheetName,ToRunColumnNameTestCase,TestCaseName)){	
			Add_Log.info(TestCaseName+" : CaseToRun = N for So Skipping Execution.");
//			reportSkip(TestCaseName+" : CaseToRun = N, so Skipping Execution.");
			//To report result as skip for test cases In TestCasesList sheet.
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "SKIP");
			//To throw skip exception for this test case.
			throw new SkipException(TestCaseName+"'s CaseToRun Flag Is 'N' Or Blank. So Skipping Execution Of "+TestCaseName);
		}
		//To retrieve DataToRun flags of all data set lines from related test data sheet.
		TestDataToRun = SuiteUtility.checkToRunUtilityOfData(FilePath, TestCaseName, ToRunColumnNameTestData, rowNum);
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy__hh_mm_ssaa");
		Date date = new Date();

		if(Param.getProperty("recordSuite").equalsIgnoreCase("yes")){
			//create object for recorder
			recorder = new ATUTestRecorder(System.getProperty("user.dir")+"//TestVideos//", "TestVideo-"+TestCaseName+"-"+dateFormat.format(date), false);
					
			//start recording 
			recorder.start();
		}

		//To Initialize browser.
//		loadWebBrowser();
//		driver.get(Param.getProperty("QPPCMURLunderTest"));
//		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	@Test(dataProvider="CreateDocument")
	public void TestName(String title, String Description, String CreatedDate, String PYYear, String Year, String ParCategory, String ResourceType, 
			String AttachmentLink, String QPPCMCategory, String FilePath, String actualResult) throws InterruptedException, ParseException, MalformedURLException{
		
		DataSet++;

		extentReportStart(title, SuiteName);
		Result = "";
		failingColumns = "";
		
		//Created object of testng SoftAssert class.
		s_assert = new SoftAssert();		

		//If found DataToRun = "N" for data set then execution will be skipped for that data set.
		if(!TestDataToRun[DataSet].equalsIgnoreCase("Y")){
			//If DataToRun = "N", Set Testskip=true.
			Testskip=true;
			throw new SkipException("DataToRun for row number "+DataSet+" Is No or Blank. So Skipping Its Execution.");
		}
		
		try {
			
			 if (DataSet==0){
					loadWebBrowser();
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					wait(40);
			 }else{
			
			wait(2);
			driver.get(AttachmentLink);
			wait(2);
			if (!(getUrl().contains(AttachmentLink))){
				reportFailure(getUrl());
				System.out.println(getUrl());
				System.out.println(AttachmentLink);
				Testfail=true;
				s_assert.fail();
			}else{reportPass("URL check Pass");}
			Result =driver.findElement(By.xpath("//span[@class='filename_column']")).getText();
			System.out.println(Result);
			driver.findElement(By.xpath("//span[@class='filename_column']")).click();
			reportPass("Following attachment has been downloaded successfully: "+Result);
			wait(5);
			 }	
			
			
		}catch(NullPointerException | WebDriverException f){
			Testfail = true;
			f.printStackTrace();
			s_assert.fail();
		}
		
		if(Testfail){
			s_assert.assertAll();
		}	
		
	}

	//@AfterMethod method will be executed after execution of @Test method every time.
	@AfterMethod
	public void reporterDataResults(){	
		if(Testskip){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as SKIP In excel.");
			 reportSkip(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as SKIP in excel.");
			//If found Testskip = true, Result will be reported as SKIP against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "SKIP");
		}
		else if(Testfail){
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as FAIL In excel.");
			 reportFailure(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as FAIL in excel.");
			//To make object reference null after reporting In report.
			s_assert = null;
			//Set TestCasePass = false to report test case as fail In excel sheet.
			TestCasePass=false;	
			//If found Testfail = true, Result will be reported as FAIL against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "FAIL");	
			// enter actual result
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Actual Result", DataSet+1, Result);	
			// highlight failing columns
			//SuiteUtility.HighlightFailingColumns(FilePath, TestCaseName, failingColumns, DataSet+1);
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS In excel.");
			 reportPass(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS in excel.");
			//If found Testskip = false and Testfail = false, Result will be reported as PASS against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "PASS");
			// enter actual result
			System.out.println(Result);
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Actual Result", DataSet+1, Result);
		}
		//At last make both flags as false for next data set.
		Testskip=false;
		Testfail=false;
	}

	//This data provider method will return 4 column's data one by one In every Iteration.
	@DataProvider
	public Object[][] CreateDocument(){
		//To retrieve data from Data 1 Column,Data 2 Column,Data 3 Column and Expected Result column of Campaign data Sheet.
		//Last two columns (DataToRun and Pass/Fail/Skip) are Ignored programatically when reading test data.
		return SuiteUtility.GetTestDataUtility(FilePath, TestCaseName, rowNum);
	}

	//To report result as pass or fail for test cases In TestCasesList sheet.
	@AfterTest
	public void closeBrowser() throws ATUTestRecorderException {
		//To Close the web browser at the end of test.
//		closeWebBrowser();

		if(Param.getProperty("recordSuite").equalsIgnoreCase("yes")){
			//stop recording
			recorder.stop();
		}

		if(TestCasePass){
			Add_Log.info(TestCaseName+" : Reporting test case as PASS In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "PASS");
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test case as FAIL In excel.");
			SuiteUtility.WriteResultUtility(FilePath, SheetName, "Pass/Fail/Skip", TestCaseName, "FAIL");

		}
		extentReportFlush(TestCaseName);
	}
}
