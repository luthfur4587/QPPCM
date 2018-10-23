package com.ApplicationDataMigration;

import java.io.File;
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

public class LinksMigration extends ApplicationDataMigrationBase{

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
	String ResultURL;
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
	
	@Test(dataProvider="createLinks")
	public void TestName(String UserName, String Password, String Title, String Description, String CreatedDate, String Year, String RepTrack,String PYCategory, 
			String ResourceType, String Link, String RolloutDate, String RolloutTime, String FileName,String LcFilePath, String NodeID,String QPPCM_URL) throws InterruptedException, ParseException, MalformedURLException{
		
		DataSet++;

		extentReportStart(Title, SuiteName);
		Result = "";
		ResultURL="";
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
					driver.get(Param.getProperty("QPPCMURLunderTest"));
					driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
					//Login 
					doLogin(UserName, Password);
//					doLogin(Param.getProperty("AuthorUserID"), Param.getProperty("AuthorPassword"));
			 }else{
			
//			System.out.println(CreatedDate);
			System.out.println(OriginalCreateDate(CreatedDate));
			wait(2);
			//Create a Document
			driver.get("https://qppcm-imp.cms.gov/dashboardAuthor");
//			driver.get("/html/body/app-root/header/div/div/div[1]/a/img");
			getElement("AddLinkButton_xpath").click();
			if (getElement("PageTitle_xpath").getText().equals("Add Document")==false){
				reportWarning("Page Title mismatch");
			}
			
			String linkTitle=Title+"-"+getRandomInt(1000);
//			String linkTitle=Title.trim();
			type("Title_xpath", linkTitle);
			type("Description_xpath", Description);			
			String PY_year=Year.replace(".0", "");
			System.out.println(PY_year);
			new Select(getElement("selectYear_xpath")).selectByVisibleText(PY_year);
			wait (1);
			String Category=RepTrack.trim()+"--"+PYCategory.trim();
//			Category=Category.replace("All", "MIPS").replace("--Overview", "").replace("APMS", "APM");
			System.out.println(Category);
			new Select(getElement("selectCategory_xpath")).selectByVisibleText(Category);
			new Select(getElement("RTypes_xpath")).selectByVisibleText(ResourceType.trim());
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("document.getElementById('rollOutDate').setAttribute('onkeydown', 'return true')");
			type("RolloutDate_xpath", "10/31/2018");
			wait(2);
			type("RolloutTime_xpath", RolloutTime);

			System.out.println(FileName);
//			getElement("DocumentFileUploadButton_xpath").sendKeys(FilePath);
			

//			File file = new File("C:/QPPCM/DataMigration2018");
//	         File[] files = file.listFiles();
//	         int count=files.length;
//	         System.out.println(count);
//	         for(File f: files){
////	             System.out.println(f.getName());
//	             if (f.getName().replace(".pdf.pdf", ".pdf").trim().equals(FileName.trim())){
//		             String FilePath="C:" + File.separator + "QPPCM\\DataMigration2018" + File.separator +f.getName().trim();
//		             System.out.println(FilePath);
//		             getElement("DocumentFileUploadButton_xpath").sendKeys(FilePath);
//		             wait(3);
//	             break;
//	             }
//	         }
			System.out.println(LcFilePath);
			getElement("DocumentFileUploadButton_xpath").sendKeys(LcFilePath);
			driver.findElement(By.xpath("//input[@type='text'][@name='migrationDate']")).sendKeys(OriginalCreateDate(CreatedDate));
//			fileUpload("DocumentFileUploadButton_xpath", FilePath);
			
//			driver.findElement(By.xpath("//input[@type='file'][@class='form-control']")).sendKeys(FilePath.replace("?", ""));
//			driver.findElement(By.xpath("//input[@type='file'][@class='form-control']")).sendKeys("C:\\zoneupload\\Add files doc.pdf");
		
//			click("CancelButton_xpath");
			
			wait(2);
			click("SubmitButton_xpath");
			wait (3);
			
			
			//Verify the banner 
			if (getElement("SuccessBanner_xpath").getText().equals("Success! Your content has been submitted for approval & is listed below.")){
				reportPass("Confirmation Success");
				reportPass("Following Document has been submitted for approval: "+ linkTitle.toString());
			}else{reportWarning("Confirmation failed");}
			System.out.println(linkTitle);
			System.out.println(linkTitle.toString());
			
			wait(1);
			//Verify the Pending Approval status 
			getElementByPassingXPath("//a[contains(text(),'"+linkTitle.trim()+"')]").click();
			wait(1);
//			Result = driver.findElement(By.xpath("//section[1]/div[1]/div[1]/div[1]/form[1]/div[1]/small[1]")).getText();
			Result = driver.findElement(By.xpath("//small[@id='hideAttachmentTXT']")).getText();
			ResultURL=getUrl();
			System.out.println(ResultURL);
//			//doLoout();
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
			extentReportFlush(TestCaseName);
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
			extentReportFlush(TestCaseName);
		}
		else{
			Add_Log.info(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS In excel.");
			 reportPass(TestCaseName+" : Reporting test data set line "+(DataSet+1)+" as PASS in excel.");
			//If found Testskip = false and Testfail = false, Result will be reported as PASS against data set line In excel sheet.
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Pass/Fail/Skip", DataSet+1, "PASS");
			// enter actual result
			System.out.println(Result);
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "Node ID", DataSet+1, Result);
			SuiteUtility.WriteResultUtility(FilePath, TestCaseName, "QPPCM_URL", DataSet+1, ResultURL);
			extentReportFlush(TestCaseName);
		}
		//At last make both flags as false for next data set.
		Testskip=false;
		Testfail=false;
	}

	//This data provider method will return 4 column's data one by one In every Iteration.
	@DataProvider
	public Object[][] createLinks(){
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
//		extentReportFlush(TestCaseName);
	}
}