package MassExecution_Maven.MassExecution_Maven;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class GenericUtils {

	public static String browser = null;
	public static String uRL = null;
	public static String userName = null;
	public static String password = null;
	public static String country = null;
	public static String release = null;
	public static String testPhase = null;
	public static String uniqueString = null;
	public static String genericTPName = null;
	
	public static String driverPath = null;

	public static FluentWait<WebDriver> wait = null;
	public static WebDriver driver = null;

	public static Map<String,String> configMap = new HashMap<String,String>();

	public static void initSettings()throws Exception{	

		browser = configMap.get("Browser");
		uRL = configMap.get("URL");
		userName = configMap.get("UserName");
		password = configMap.get("Password");
		release = configMap.get("Release");
		testPhase = configMap.get("TestPhase");
		country = configMap.get("Country");
		uniqueString = configMap.get("UniqueString");
		genericTPName = configMap.get("GenericTestPlanName");

		driverPath = System.getProperty("user.home")+"\\Desktop\\SeleniumFiles";
		//driverPath = System.getProperty("user.dir");
		browser = browser.toLowerCase();
		System.out.println("driverPath : "+driverPath);
		if(browser.contains("chrome")){
			System.setProperty("webdriver.chrome.driver",driverPath+"\\chromedriver.exe");

			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.prompt_for_download", "true");

			chromePrefs.put("safebrowsing.enabled", "true"); 
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("prefs", chromePrefs);
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			cap.setCapability(ChromeOptions.CAPABILITY, options);
			driver = new ChromeDriver(cap);
		}else if(browser.contains("firefox")){
			System.setProperty("webdriver.gecko.driver",driverPath+"\\geckodriver.exe");  
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			driver = new FirefoxDriver(capabilities);
		}
		else if(browser.contains("ie") || browser.contains("explorer") || browser.contains("internet")){
			System.setProperty("webdriver.ie.driver",driverPath+"\\IEDriverServer.exe");
			DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
			caps.setCapability("EnableNativeEvents", false);
			caps.setCapability("ignoreZoomSetting", true);

			driver = new InternetExplorerDriver(caps);
			driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, "0")); 
		}
		System.out.println("Browser : "+browser);

		//Defining wait
		wait = new WebDriverWait(driver, 200);
	}

	public static void launchURL()throws Exception{

		driver.get(uRL);
		Thread.sleep(5000);
		driver.manage().window().maximize();
	}

	public static void loginApp()throws Exception{

		String testYourProcesses = "//div[contains(@id,'HomePage')]//span[text()='Test Your Processes']";
		wait("//input[@id='USERNAME_FIELD-inner' or @id='j_username']");
		type(By.xpath("//input[@id='USERNAME_FIELD-inner' or @id='j_username']"), userName);
		type(By.xpath("//input[@id='PASSWORD_FIELD-inner' or @id='j_password']"), password);
		click(By.xpath("//button[@id='LOGIN_LINK' or @id='logOnFormSubmit']"));
		//wait.until(ExpectedConditions.elementToBeClickable(By.xpath(testYourProcesses)));
		Thread.sleep(30000);

		//Authentication Information pop-up
		if(driver.findElements(By.xpath("//button//bdi[text()='Close']")).size()==1){
			System.out.println("Authentication pop-up is displayed.Closing it");
			click(By.xpath("//button//bdi[text()='Close']"));
		}

		if(!uRL.contains("h4screen=test")){
			for(int i=0;i<6;i++){
				if(driver.findElements(By.xpath(testYourProcesses)).size()!=1)
					Thread.sleep(10000);
				else
					break;
			}

			wait(testYourProcesses);
			click(By.xpath(testYourProcesses));
		}
	}

	
	public static void doubleClick(By locator)throws Exception{

		new Actions(driver).moveToElement(driver.findElement(locator)).doubleClick().perform();
		Thread.sleep(10000);
	}

	public static void actionsClick(By locator)throws Exception{

		new Actions(driver).moveToElement(driver.findElement(locator)).click().perform();
		Thread.sleep(10000);
	}

	public static void type(By locator , String value)throws Exception{

		driver.findElement(locator).sendKeys(value);
		Thread.sleep(1000);
	}

	public static void click(By locator)throws Exception{

		driver.findElement(locator).click();
		Thread.sleep(10000);
	}

	public static void clear(By locator)throws Exception{

		driver.findElement(locator).clear();
		Thread.sleep(2000);
	}


	public static void wait(String locator)throws Exception{

		wait = new FluentWait<WebDriver>(driver);
		Thread.sleep(10000);
		wait.withTimeout(100, TimeUnit.SECONDS)
		.ignoring(NoSuchElementException.class)
		.pollingEvery(5, TimeUnit.SECONDS)
		.withMessage("checking for the element")
		.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
	}

	public static void captureScreenshot()throws Exception{
		File src= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(src, new File("C:/selenium/error.png"));
		}
		catch (IOException e){
			System.out.println(e.getMessage());
		}

	}
	
	public static boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	public static int getColumnNumber(String tblXpath, String colName)throws Exception{

		int colNum = -1;
		int colCount = GenericUtils.driver.findElements(By.xpath(tblXpath+"/thead/tr/th")).size();
		List<String> columnNames = new ArrayList<String>();

		for(int i=0;i<colCount;i++)
			columnNames.add(GenericUtils.driver.findElement(By.xpath(tblXpath+"/thead/tr/th["+(i+1)+"]")).getText());

		colNum = columnNames.indexOf(colName);
		colNum = colNum+1;

		System.out.println("Column Number : "+colNum);
		return colNum;
	}

	public static int getColumnCount(String tblXpath)throws Exception{

		int colCount = GenericUtils.driver.findElements(By.xpath(tblXpath+"/thead/tr/th")).size();
		
		return colCount;
	}
	public static void keyPress(By locator, Keys key)throws Exception{

		wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		driver.findElement(locator).sendKeys(key);
		Thread.sleep(2000);
	}


	public static void generateDate(String dateFormat, String value)throws Exception{
		
		DateFormat df = new SimpleDateFormat(dateFormat);//"ddMMyyHHMMss"
		Date date = new Date();
		
		if(value.contains("+") | value.contains("-")){
			
		}
		String timeStamp = value.split("_")[1];
		timeStamp = timeStamp.split(".")[0];

		String today = df.format(date);

	}
}
