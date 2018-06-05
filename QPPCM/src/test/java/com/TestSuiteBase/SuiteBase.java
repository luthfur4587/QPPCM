package com.TestSuiteBase;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
//import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.google.common.base.Function;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.utility.ExtentManager;
import com.utility.Read_XLS;

public class SuiteBase {	
	public static Read_XLS TestSuiteListExcel=null;
	public static Read_XLS TestCaseListExcelSERVICES = null;
	public static Read_XLS TestCaseListExcelUSERLOGIN = null;
	public static Logger Add_Log = null;
	public boolean BrowseralreadyLoaded=false;
	public static Properties Param = null;
	public static Properties Object = null;
	public static WebDriver driver=null;
	public static WebDriver ExistingchromeBrowser;
	public static WebDriver ExistingmozillaBrowser;
	public static WebDriver ExistingIEBrowser;
	private String elementToHighlight = null;	
	protected static By title = By.xpath("//title");
	private int defaultTimeout = 10000;
	
	public ExtentReports rep = ExtentManager.getInstance();
	public ExtentTest test;
	
	public void init() throws IOException{
		//To Initialize logger service.
		Add_Log = Logger.getLogger("rootLogger");
		//Please change file's path strings bellow If you have stored them at location other than bellow.
		//Initializing Test Suite List(TestSuiteList.xls) File Path Using Constructor Of Read_XLS Utility Class.
		TestSuiteListExcel = new Read_XLS(System.getProperty("user.dir")+"\\src\\test\\java\\com\\ExcelFiles\\TestSuiteList.xls");
//		//Initializing Test Suite(Services.xls) File Path Using Constructor Of Read_XLS Utility Class.
//		TestCaseListExcelSERVICES = new Read_XLS(System.getProperty("user.dir")+"\\src\\com\\ExcelFiles\\Services.xlsx");
//		//Initializing Test Suite(UserLogin.xls) File Path Using Constructor Of Read_XLS Utility Class.
		TestCaseListExcelUSERLOGIN = new Read_XLS(System.getProperty("user.dir")+"\\src\\test\\java\\com\\ExcelFiles\\UserLogin.xls");

		
		//Below given syntax will Insert log In applog.log file.
		Add_Log.info("All Excel Files Initialised successfully.");
		
		//Initialize Param.properties file.
		Param = new Properties();
		FileInputStream fip = new FileInputStream(System.getProperty("user.dir")+"//src//test//java//com//property//Param.properties");
		Param.load(fip);
		Add_Log.info("Param.properties file loaded successfully.");		
	
		//Initialize Objects.properties file.
		Object = new Properties();
		fip = new FileInputStream(System.getProperty("user.dir")+"//src//test//java//com//property//Objects.properties");
		Object.load(fip);
		Add_Log.info("Objects.properties file loaded successfully.");
	}
	
	public void loadWebBrowser() throws InterruptedException, MalformedURLException{
		//Check If any previous webdriver browser Instance Is exist then run new test In that existing webdriver browser Instance.
			if(Param.getProperty("testBrowser").equalsIgnoreCase("Mozilla") && ExistingmozillaBrowser!=null){
				System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"//BrowserDrivers//geckodriver.exe");
				driver = ExistingmozillaBrowser;
				return;
			}else if(Param.getProperty("testBrowser").equalsIgnoreCase("chrome") && ExistingchromeBrowser!=null){
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//BrowserDrivers//chromedriver.exe");
				driver = ExistingchromeBrowser;
				return;
			}else if(Param.getProperty("testBrowser").equalsIgnoreCase("IE") && ExistingIEBrowser!=null){
				System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"//BrowserDrivers//IEDriverServer.exe");
				driver = ExistingIEBrowser;
				return;
			}		
		
		
			if(Param.getProperty("testBrowser").equalsIgnoreCase("Mozilla")){
//				//To Load Firefox driver Instance.
				System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+"//BrowserDrivers//geckodriver.exe");
				ProfilesIni profile = new ProfilesIni();
				FirefoxProfile myprofile = profile.getProfile("Test");
				myprofile.setPreference("browser.startup.homepage","http://www.google.com");
				myprofile.setPreference("dom.webnotifications.enabled", false);
				FirefoxOptions options = new FirefoxOptions().setProfile(myprofile);
				driver = new FirefoxDriver(options);
				ExistingmozillaBrowser=driver;
				Add_Log.info("Firefox Driver Instance loaded successfully.");
				
			}else if(Param.getProperty("testBrowser").equalsIgnoreCase("Chrome")){
//				//To Load Chrome driver Instance.
				System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"//BrowserDrivers//chromedriver.exe");
				DesiredCapabilities capabilities = DesiredCapabilities.chrome();
				capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
				capabilities.setCapability (CapabilityType.ACCEPT_SSL_CERTS, true);
				ChromeOptions opts = new ChromeOptions();
				opts.addArguments("start-maximized");
				opts.addArguments("-incognito");
				opts.addArguments("--disable-popup-blocking");
				capabilities.setCapability(ChromeOptions.CAPABILITY, opts);
				driver = new ChromeDriver(capabilities);
				
				ExistingchromeBrowser=driver;
				Add_Log.info("Chrome Driver Instance loaded successfully.");
				
			}else if(Param.getProperty("testBrowser").equalsIgnoreCase("IE")){
				//To Load IE driver Instance.
				System.setProperty("webdriver.ie.driver", System.getProperty("user.dir")+"//BrowserDrivers//IEDriverServer.exe");
				DesiredCapabilities cap = new DesiredCapabilities();
				cap.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
				cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
//				cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				cap.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
				driver = new InternetExplorerDriver(cap);
				ExistingIEBrowser=driver;
				Add_Log.info("IE Driver Instance loaded successfully.");
				
			}	
			test.log(LogStatus.INFO, "Opeing Browser");
			driver.manage().deleteAllCookies();
			Thread.sleep(5000);
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);	
	}
	
	public void closeWebBrowser(){
		test.log(LogStatus.INFO, "Closing Browser");
		wait(1);
//		driver.close();
		driver.quit();
		//null browser Instance when close.
		ExistingchromeBrowser=null;
		ExistingmozillaBrowser=null;
		ExistingIEBrowser=null;	
	}
	
	//getElementByXPath function for static xpath
	public WebElement getElementByXPath(String Key){
		try{
			reportInfo("Locating: "+Key);
			//This block will find element using Key value from web page and return It.
			return driver.findElement(By.xpath(Object.getProperty(Key)));
		}catch(Throwable t){
			//If element not found on page then It will return null.
			//test.log(LogStatus.INFO, "Not able to locate "+Object.getProperty(Key));
			reportError("Element Not Found "+Key);
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//getElementByPassingXPath function for static xpath
	public WebElement getElementByPassingXPath(String Key){
		try{
			reportInfo(Key);
			//This block will find element using Key value from web page and return It.
			return driver.findElement(By.xpath(Key));
		}catch(Throwable t){
			//If element not found on page then It will return null.
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	//getElementByXPath function for dynamic xpath
	public WebElement getElementByXPath(String Key1, int val, String key2){
		try{
			//This block will find element using values of Key1, val and key2 from web page and return It.
			return driver.findElement(By.xpath(Object.getProperty(Key1)+val+Object.getProperty(key2)));
		}catch(Throwable t){
			//If element not found on page then It will return null.
			Add_Log.debug("Object not found for custom xpath");
			return null;
		}
	}
	
	//Call this function to locate element by ID locator.
	public WebElement getElementByID(String Key){
		try{
			return driver.findElement(By.id(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			reportFailure("Element Not Found "+Key);
			return null;
		}
	}
	
	//Call this function to locate element by Name Locator.
	public WebElement getElementByName(String Key){
		try{
			return driver.findElement(By.name(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			reportFailure("Element Not Found "+Key);
			return null;
		}
	}
	
	//Call this function to locate element by cssSelector Locator.
	public WebElement getElementByCSS(String Key){
		try{
			return driver.findElement(By.cssSelector(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			reportFailure("Element Not Found "+Key);
			return null;
		}
	}
	
	//Call this function to locate element by cssSelector Locator.
		public WebElement getElementByPassingCSS(String Key){
			try{
				return driver.findElement(By.cssSelector(Key));
			}catch(Throwable t){
				Add_Log.debug("Object not found for key --"+Key);
				reportFailure("Element Not Found "+Key);
				return null;
			}
		}
	
	//Call this function to locate element by ClassName Locator.
	public WebElement getElementByClass(String Key){
		try{
			return driver.findElement(By.className(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			reportFailure("Element Not Found "+Key);
			return null;
		}
	}
	
	//Call this function to locate element by tagName Locator.
	public WebElement getElementByTagName(String Key){
		try{
			return driver.findElement(By.tagName(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			reportFailure("Element Not Found "+Key);
			return null;
		}
	}
	
	//Call this function to locate element by link text Locator.
	public WebElement getElementBylinkText(String KeyText){
		try{
			return driver.findElement(By.linkText(Object.getProperty(KeyText)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+KeyText);
			reportFailure("Element Not Found "+KeyText);
			return null;
		}
	}
	
	//Call this function to locate element by passing actual link text.
		public WebElement getElementByActualLinkText(String Key){
			try{
				return driver.findElement(By.linkText(Key));
			}catch(Throwable t){
				Add_Log.debug("Object not found for key --"+Key);
				return null;
			}
		}
	
	//Call this function to locate element by partial link text Locator.
	public WebElement getElementBypLinkText(String Key){
		try{
			return driver.findElement(By.partialLinkText(Object.getProperty(Key)));
		}catch(Throwable t){
			Add_Log.debug("Object not found for key --"+Key);
			return null;
		}
	}
	
	// Fetch element by
	public WebElement fetchElement(By by) {
	    return driver.findElement(by);
	}
	
	// Finds the element by the given selector
	protected WebElement find(By by)
	{
		long startTime = System.currentTimeMillis();
		
		while(driver.findElements(by).size() == 0)
		{
			if (System.currentTimeMillis() - startTime > defaultTimeout)
			{
                String errMsg = String.format(
						"Could not find element %s after %d seconds.",
						by,
						defaultTimeout);
				throw new NoSuchElementException(errMsg);
			}
		}
		return driver.findElement(by);
	}
	
	//  Gets everything inside the html tags for the given selector.
	protected String getInnerHtml(By by)
	{
		return find(by).getAttribute("innerHTML");
	}
	
	// Gets the title of the current page.
	public String getTitle()
	{
		return getInnerHtml(title);
	}
	
	// Gets the URL of the current page.
	public String getUrl()
	{
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		return driver.getCurrentUrl();
	}
	
	// Finds all elements by the given selector.
	protected Collection<WebElement> findAll(String key)
	{
		By by = By.xpath(Object.getProperty(key));
		return driver.findElements(by);
	}
	
	public WebElement fluentWait(final By locator) {
	    Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
	            .withTimeout(30, TimeUnit.SECONDS)
	            .pollingEvery(5, TimeUnit.SECONDS)
	            .ignoring(NoSuchElementException.class);

	    WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
	        public WebElement apply(WebDriver driver) {
	            return driver.findElement(locator);
	        }
	    });

	    return  foo;
	};
	
	public WebElement fluentWaitforElement(WebElement element, int timoutSec, int pollingSec) {

	    FluentWait<WebDriver> fWait = new FluentWait<WebDriver>(driver).withTimeout(timoutSec, TimeUnit.SECONDS)
	    .pollingEvery(pollingSec, TimeUnit.SECONDS)
	    .ignoring(NoSuchElementException.class, TimeoutException.class);

	    for (int i = 0; i < 2; i++) {
	        try {
	            fWait.until(ExpectedConditions.visibilityOf(element));
	            fWait.until(ExpectedConditions.elementToBeClickable(element));
	        } 
	        catch (Exception e) {

	            System.out.println("Element Not found trying again - " + element.toString().substring(70));
	            e.printStackTrace();
	        }
	    }

	    return element;
	}
	
	// wait until element is visible.
	protected void waitUntil(String key){
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.visibilityOfElementLocated((By)getElementByXPath(key)));
	}

	// Pauses play until a given element becomes visible.
	protected void waitForElementToExist(String key)
	{
		waitForElementToExist(key, defaultTimeout);
	}

	// Pauses play until a given element becomes visible.
	protected void waitForElementToExist( String key, long timeout)
	{
		long startTime = System.currentTimeMillis();
		
		while(findAll(key).size() == 0)
		{
			if (System.currentTimeMillis() - startTime > timeout)
			{
				Assert.fail(String.format("Could not find element '%s' after %s seconds",
						key.toString(),
						timeout / 1000));
			}
		}
	}
	
	// click on element
	public void clickElement(String key){
		WebElement element = getElementByXPath(key);
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().perform();
	}
	
	public void clickLinkByText (String key){
		List<WebElement> links = driver.findElements(By.tagName("a"));
		for (int i = 0; i<links.size(); i=i+1){
			WebElement currentLink=(links.get(i));
			if(currentLink.getText().equalsIgnoreCase(key.trim())){
				currentLink.click();
				break;
				}
			if (i==(links.size()-1)){
				reportError("Link Not Found: "+key);
			}
			}
		}
	
	public void hoverOverWebElement(String key) throws InterruptedException{
//		if(Param.getProperty("testBrowser").equalsIgnoreCase("mozilla") ){
//			mozillaHoverOverElement(key);
//		}
		By by = By.xpath(Object.getProperty(key));
		Actions action = new Actions(driver);
		WebElement element = driver.findElement(by);
		action.moveToElement(element);
		action.perform();
//		action.build().perform();
//		action.wait();
		Thread.sleep(3000);
	}
	
	public void hoverOverClickAndHold(String key) throws InterruptedException{
//		if(Param.getProperty("testBrowser").equalsIgnoreCase("mozilla") ){
//			mozillaHoverOverElement(key);
//		}
		By by = By.xpath(Object.getProperty(key));
		Actions action = new Actions(driver);
		WebElement element = driver.findElement(by);
		action.moveToElement(element).clickAndHold().perform();
		//action.perform();
		//action.build().perform();
		//action.wait();
		Thread.sleep(3000);
	}
	
	
	// hover over element
	public boolean hoverOverElement(String key) throws InterruptedException{
		if(Param.getProperty("testBrowser").equalsIgnoreCase("mozilla") ){
			boolean hover = mozillaHoverOverElement(key);
			return hover;
		}
		By by = By.xpath(Object.getProperty(key));
		Actions action = new Actions(driver);
		WebElement element = driver.findElement(by);
		action.moveToElement(element);
		action.build().perform();
		if(key.contains("start")){
			String helpButtonOn = getElementByXPath("startDateHelpLabel").getAttribute("class");
			if(helpButtonOn.equals("helpButtonOn")){
				System.out.println("IT SHOWS!");
				String Result = getElementByXPath("startDateHelpLabel").getText();
				System.out.println("THE RESULT IS " + Result);
				return true;
			} else {
				System.out.println("IT DOES NOT SHOW");
				@SuppressWarnings("unused")
				String Result = "";
				return false;
			}
		}else {
			String helpButtonOn = getElementByXPath("endDateHelpLabel").getAttribute("class");
			if(helpButtonOn.equals("helpButtonOn")){
				System.out.println("IT SHOWS!");
				String Result = getElementByXPath("endDateHelpLabel").getText();
				System.out.println("THE RESULT IS " + Result);
				return true;
			} else {
				System.out.println("IT DOES NOT SHOW");
				@SuppressWarnings("unused")
				String Result = "";
				return false;
			}
		}
	}
	
	// hover over element for mozilla
	private boolean mozillaHoverOverElement(String key) {
		By by = By.xpath(Object.getProperty(key));
		WebElement element = driver.findElement(by);
		Point p = element.getLocation();
        int x = p.getX();
        int y = p.getY();

        Dimension d = element.getSize();
        int h = d.getHeight();
        int w = d.getWidth();

        Robot robot = null;
        try {
			robot = new Robot();
			robot.mouseMove(x + (w / 2), y + (h / 2) + 50);
	        robot.mousePress(InputEvent.BUTTON1_MASK);
			return true;
		} catch (AWTException e) {
			e.printStackTrace();
			return false;
		}
        
	}

	// selects from dropdown by visible text
	public void selectFromDropdown(String dropdownKey, String visibleText){
		Select select = new Select(getElementByXPath(dropdownKey));
		select.selectByVisibleText(visibleText);
	}
	
	public String modifyDateLayout(String dateFormat, String inputDate) throws ParseException{
		Date date = new SimpleDateFormat(dateFormat).parse(inputDate);
	    return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}
	
	// modify date layout
	public String modifyDateLayout(String inputDate) throws ParseException{
	    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDate);
	    return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}
	
	// modify time layout
	public String modifyTimeLayout(String inputDate) throws ParseException{
	    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDate);
	    System.out.println(new SimpleDateFormat("hh a").format(date));
	    return new SimpleDateFormat("h a").format(date);
	}
	
	// modify date layout for date pickers
	public String modifyDateLayoutForDatePicker(String inputDate) throws ParseException{
	    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDate);
	    return new SimpleDateFormat("MMMM,dd,yyyy").format(date);
	}
	
	public String CALTDate(String inputDate) throws ParseException{
	    Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(inputDate);
	    return new SimpleDateFormat("MM/dd/yyyy HH.mm").format(date);
	}
	// change time format from 10:00:00 AM to 10 AM
	public String changeTimeFormat(String time){
		String regex;
		if(time.contains("A")){
			regex = "\\A";
		} else {
			regex = "\\P";
		}
		String[] parts = time.split(regex);
		String first = parts[0];
		String last = time.substring(time.length() - 2);
		String timeString = first + " " + last;
		System.out.println(timeString);
		return timeString;
	}
	
	// separate by commas
	public String[] seperateByCommas(String array){
		String[] parts = null;
		if(!array.contains(",")){
			parts = new String[1];
			parts[0] = array;
			return parts;
		} else {
			parts = array.split("\\,");
			return parts;
		}
	}
	
	// remove spaces and asterix
	public String removeSpacesAndAsterix(String word){
		word = word.replace("\n", "");
		word = word.replace("*", "");
		System.out.println(word);
		return word;
	}
	
	protected void highlightLabel(String element){
		unhighlightLastHighlightedElement(1);
		
		if(element.isEmpty()){
			return;
		} else if(element.contains(",")){
			elementToHighlight = element;
			String[] elements = seperateByCommas(element);
			for(int i=0;i<elements.length;i++){
				WebElement elem = getElementByXPath(elements[i]);
				JavascriptExecutor js = (JavascriptExecutor)driver;
			    js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", elem);
			    js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", elem); 
			}
		} else {
			elementToHighlight = element;
			WebElement elem = getElementByXPath(element);
			JavascriptExecutor js = (JavascriptExecutor)driver;
		    js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", elem);
		    js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", elem); 
		}
	}
	
	protected void highlightTextBox(String element) {
		unhighlightLastHighlightedElement(2);
		
		if(element.isEmpty()){
			return;
		} else if(element.contains(",")){
			elementToHighlight = element;
			String[] elements = seperateByCommas(element);
			for(int i=0;i<elements.length;i++){
				WebElement elem = getElementByXPath(elements[i]);
				JavascriptExecutor js = (JavascriptExecutor)driver;
			    js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", elem);
			    js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", elem); 
			}
		} else {
			elementToHighlight = element;
			WebElement elem = getElementByXPath(element);
			JavascriptExecutor js = (JavascriptExecutor)driver;
		    js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 2px solid red;');", elem);
		    js.executeScript("arguments[0].setAttribute('style','border: solid 2px black');", elem); 
		}
	}

	private void unhighlightLastHighlightedElement(int number) {
		
		if(elementToHighlight != null){
			try {
				
				if(elementToHighlight.contains(",")){
					String[] elements = seperateByCommas(elementToHighlight);
					for(int i=0;i<elements.length;i++){
						WebElement elem = getElementByXPath(elements[i]);
						JavascriptExecutor js = (JavascriptExecutor)driver;
						switch(number){
						case 1:
							js.executeScript("arguments[0].setAttribute('style','border: 0');", elem); 
							break;
						case 2:
							js.executeScript("arguments[0].setAttribute('style','border: solid 1px grey');", elem); 
							break;
						}
					}
				} else {
					WebElement elem = getElementByXPath(elementToHighlight);
					JavascriptExecutor js = (JavascriptExecutor)driver;
					switch(number){
					case 1:
						js.executeScript("arguments[0].setAttribute('style','border: 0');", elem); 
						break;
					case 2:
						js.executeScript("arguments[0].setAttribute('style','border: solid 1px grey');", elem); 
						break;
					}
				}
				
			}catch(StaleElementReferenceException ignored){
				ignored.printStackTrace();
			} finally {
				elementToHighlight = null;
			}
		}
	}
	
	/*****************************New Added Functions********************************/
	/*****************************Global********************************/
	
	public void wait(int timeToWaitInSec){
		try {
			Thread.sleep(timeToWaitInSec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void waitForPageToLoad() {
		wait(1);
		JavascriptExecutor js=(JavascriptExecutor)driver;
		String state = (String)js.executeScript("return document.readyState");
		
		while(!state.equals("complete")){
			wait(2);
			state = (String)js.executeScript("return document.readyState");
		}
	}
	

	
	public boolean checkElementExists(By by, int seconds){
		boolean result=false;
		try{
			driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
			driver.findElement(by).isDisplayed();
			result =true;
		} catch (org.openqa.selenium.NoSuchElementException ex){
			 result = false;		 
		} finally {
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			 }
			 return result;
			}

	public boolean checkElementEnabled(By by, int seconds){
		boolean result=false;
		try{
			driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
			driver.findElement(by).isEnabled();
			result =true;
		} catch (org.openqa.selenium.NoSuchElementException ex){
			 result = false;		 
		} finally {
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
			 }
			 return result;
			}
	
	public boolean isElementPresent(String locatorKey){
		reportInfo("Locating: " + locatorKey);
		List<WebElement> elementList=null;
		if(locatorKey.endsWith("_id"))
			elementList = driver.findElements(By.id(Object.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_name"))
			elementList = driver.findElements(By.name(Object.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_xpath"))
			elementList = driver.findElements(By.xpath(Object.getProperty(locatorKey)));
		else{
			reportFailure("Locator not correct - " + locatorKey);
		}
		
		if(elementList.size()==0)
			return false;	
		else
			return true;
	}
	
	
	public void acceptAlert(){
		WebDriverWait wait = new WebDriverWait(driver,5);
		wait.until(ExpectedConditions.alertIsPresent());
		test.log(LogStatus.INFO,"Accepting alert");
		driver.switchTo().alert().accept();
		driver.switchTo().defaultContent();
	}
	
	/*****************************Main********************************/
	
	// finding element and returning it
	public WebElement getElement(String locatorKey){
		reportInfo("Locating: " + locatorKey);
		WebElement e=null;
		try{
		if(locatorKey.endsWith("_id"))
			e = driver.findElement(By.id(Object.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_name"))
			e = driver.findElement(By.name(Object.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_xpath"))
			e = driver.findElement(By.xpath(Object.getProperty(locatorKey)));
		else if(locatorKey.endsWith("_text"))
			e = driver.findElement(By.linkText(Object.getProperty(locatorKey)));
		else{
			reportFailure("Locator not correct - " + locatorKey);
		}
		
		}catch(Exception ex){
			// fail the test and report the error
			reportFailure(ex.getMessage());
			ex.printStackTrace();
		}
		return e;
	}
	

	public String getText(String locatorKey){
		test.log(LogStatus.INFO, "Getting text from "+locatorKey);
		return getElement(locatorKey).getText();

	}
	
	public void navigateTo(String urlKey){
		test.log(LogStatus.INFO, "Navigating to "+urlKey);
		driver.navigate().to(urlKey);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	public void click(String locatorKey){
		test.log(LogStatus.INFO, "Clicking on "+locatorKey);
		getElement(locatorKey).click();
		test.log(LogStatus.INFO, "Clicked successfully on "+locatorKey);

	}
	
	public void clickAndWait(String locator_clicked,String locator_pres){
		test.log(LogStatus.INFO, "Clicking and waiting on - "+locator_clicked);
		int count=5;
		for(int i=0;i<count;i++){
			getElement(locator_clicked).click();
			wait(2);
			if(isElementPresent(locator_pres))
				break;
		}
	}
	
	public void type(String locatorKey,String data){
		test.log(LogStatus.INFO, "Tying in "+locatorKey+". Data - "+data);
		getElement(locatorKey).sendKeys(data);
		test.log(LogStatus.INFO, "Typed successfully in "+locatorKey);
	}
	
	public void clear(String locatorKey){
		test.log(LogStatus.INFO, "Clearing: "+locatorKey);
		getElement(locatorKey).clear();
		test.log(LogStatus.INFO, "Cleared: "+locatorKey);

	}
	
	public void doLogin(String userID, String Password){
		click("zoneAcceptButton_xpath");
		type("zoneUserIDTextbox_xpath",userID);
		click("zoneNextButton_xpath");
		type("zonePwdTextbox_xpath",Password);
		click("zoneLoginButton_xpath");
		test.log(LogStatus.INFO, "Login Successfully");

	}
	/*****************************Reporting********************************/
	
	public void extentReportStart(String TestCaseName){
		test = rep.startTest(TestCaseName);
		test.log(LogStatus.INFO, TestCaseName+" : Execution Started.");
	}
	public void extentReportStart(String TestCaseName, String SuiteName){
		test = rep.startTest(TestCaseName);
		test.assignCategory(SuiteName);
		test.assignAuthor("QPPCM","Scope Infotech");
		test.log(LogStatus.INFO, TestCaseName+" : Execution Started.");
	}
	public void reportInfo(String msg){
		test.log(LogStatus.INFO, msg);
	}
	public void reportSkip(String msg){
		test.log(LogStatus.SKIP, msg);
	}
	
	public void reportPass(String msg){
		test.log(LogStatus.PASS, msg);
	}
	
	public void reportFailure(String msg){
		test.log(LogStatus.FAIL, msg);
		takeScreenShot();
	}
	
	public void reportCriticalFailure(String msg){
		test.log(LogStatus.FATAL, msg);
		takeScreenShot();
		Assert.fail(msg);
	}
	public void reportError(String msg){
		test.log(LogStatus.ERROR, msg);
	}
	
	public void reportWarning(String msg){
		test.log(LogStatus.WARNING, msg);
	}
	
	public void extentReportFlush(String TestCaseName){
	test.log(LogStatus.INFO, TestCaseName+" : Execution Ended.");
	if(rep!=null){
		rep.endTest(test);
		rep.flush();
	}
	}
	
	public void extentReportTestSuiteSkip(String SuiteName){
		extentReportStart(SuiteName,SuiteName);
		reportSkip("SuiteToRun = N for "+SuiteName+" so Skipping Execution.");
		extentReportFlush(SuiteName);
	}
	
	public void extentReportTestCaseSkip(String TestCaseName,String SuiteName){
		extentReportStart(TestCaseName,SuiteName);
		reportSkip("CaseToRun = N for "+TestCaseName+" so Skipping Execution.");
		extentReportFlush(TestCaseName);
	}
	
	
	public void takeScreenShot(){
		// fileName of the screenshot
		Date d=new Date();
		String screenshotFile=d.toString().replace(":", "_").replace(" ", "_")+".png";
		// store screenshot in that file
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
//			FileUtils.copyFile(scrFile, new File(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
			FileUtils.copyFile(scrFile, new File("C:\\Selenium\\zOneTestReports\\screenshots\\"+screenshotFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//put screenshot file in reports
		if(Param.getProperty("extentScreenShot").equalsIgnoreCase("Yes")){
//		test.log(LogStatus.INFO,"Screenshot-> "+ test.addScreenCapture(System.getProperty("user.dir")+"//screenshots//"+screenshotFile));
		test.log(LogStatus.INFO,"Screenshot-> "+ test.addScreenCapture("./screenshots/"+screenshotFile));
		}
	}


}
