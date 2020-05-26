package MassExecution_Maven.MassExecution_Maven;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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

public class Mvn_Full_ReadTPStatus_MassExec{

	public static String browser = null;
	public static String uRL = null;
	public static String userName = null;
	public static String password = null;
	public static String country = null;
	public static String release = null;
	public static String testPhase = null;
	public static String search = null;
	public static String uniqueString = null;
	public static String genericTPName = null;

	public static String testPlanName = null;

	public static String driverPath = null;
	public static String excelFilePath = null;
	public static String resultsFilePath = null;
	public static String resultFileName = null;

	public static FluentWait<WebDriver> wait = null;
	public static WebDriver driver = null;

	public static List<String> scopeItems = new ArrayList<String>();
	public static Map<String,String> configMap = new HashMap<String,String>();
	public static List<String> columnNames = new ArrayList<String>();
	public static Map<String,String> testPlanNames = new HashMap<String,String>();
	public static Map<String,String> testPlanStatus = new HashMap<String,String>();
	public static List<String> testPlanStatusList = new ArrayList<String>();

	public static CellStyle headerCellStyle = null;
	public static CellStyle valueCellStyle = null;

	public static Actions actions = null;

	public static void scrollDown()throws Exception{
		actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform();
	}

	public static void mouseOver(WebElement ele)throws Exception{
		actions.moveToElement(ele).build().perform();
	}
	public static List<String> readStatus()throws Exception{

		//String searchCriteria = null;
		List<String> liList = new ArrayList<String>();
		Thread.sleep(10000);

		/*if("Yes".equals(genericTPName))
			searchCriteria = "TC_";
		else
			searchCriteria = release+"_"+uniqueString+"_"+testPhase;

		type(By.xpath("//form[@class='sapMSFF']/input"), searchCriteria);
		 */

		click(By.xpath("//button[@title='View Settings']/span/span"));
		click(By.xpath("//div[text()='Last Run On']"));
		
		type(By.xpath("//input[contains(@id,'lastExecutedOnDateRangeSelection-inner')]"), "02/09/2019 - 02/21/2019");
		click(By.xpath("//button//bdi[text()='OK']"));
		Thread.sleep(10000);
		keyPress(By.xpath("//form[@class='sapMSFF']/input"), Keys.TAB);
		click(By.xpath("//form[@class='sapMSFF']/div[contains(@id,'searchField-search')]"));
		Thread.sleep(3000);
		click(By.xpath("//form[@class='sapMSFF']/div[contains(@id,'searchField-search')]"));
		Thread.sleep(3000);

		String testPlansTitle = driver.findElement(By.xpath("//span[contains(@id,'page-title-inner')]")).getText();

		while(testPlansTitle.contains("undefined")){
			System.out.println("Page is still loading...");
			Thread.sleep(10000);
			testPlansTitle = driver.findElement(By.xpath("//span[contains(@id,'page-title-inner')]")).getText();
		}

		System.out.println("testPlansTitle : "+testPlansTitle);
		String subStr = testPlansTitle.substring(testPlansTitle.indexOf("(")+1, testPlansTitle.indexOf(")"));
		int tpCount = Integer.parseInt(subStr);
		System.out.println("No. of Test Plans filtered : "+tpCount);

		Thread.sleep(6000);
		actionsClick(By.xpath("//ul[contains(@id,'list-listUl')]/li[1]/div"));

		int liSize = driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li")).size();
		System.out.println("liSize : "+liSize);

		while(liSize!=tpCount){
			scrollDown();
			liSize = driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li")).size();
			Thread.sleep(6000);
			actionsClick(By.xpath("//ul[contains(@id,'list-listUl')]/li["+liSize+"]/div"));
			Thread.sleep(6000);
		}

		System.out.println("No. of Test Plans displayed : "+liSize);

		for(int j=0;j<liSize;j++){
			String lastExecDate = null;
			String testPlanName = driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(j+1)+"]//span[contains(@id,'titleText')]/span")).getText();
			
			if(driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(j+1)+"]//div[contains(@class,'sapMObjLAttrDiv')]//span[contains(@id,'text')]")).size()>0)
				lastExecDate = driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(j+1)+"]//div[contains(@class,'sapMObjLAttrDiv')]//span[contains(@id,'text')]")).getText();
			
			String testPlanStatus = driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(j+1)+"]//div[contains(@class,'StatusDiv')]//span[contains(@id,'text')]")).getText();

			liList.add(testPlanName+" - "+testPlanStatus+" - "+lastExecDate);
			System.out.println(testPlanName+" - "+testPlanStatus+" , "+lastExecDate);
		}

		System.out.println("liList : "+liList);

		return liList;
	}

	public static void main(String[] args) throws Exception {

		excelFilePath = System.getProperty("user.home") + "//Desktop//CreateAndRunTestPlans//ScopeItems.xlsx";
		System.out.println("Scope Items Excel Path : "+excelFilePath);	

		readExcel(excelFilePath);
		initSettings();
		launchURL();
		loginApp();

		testPlanStatusList = readStatus();

		DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHMMss");
		Date date = new Date();
		String today = dateFormat.format(date);

		resultFileName = "Status_"+today+".xlsx";

		resultsFilePath = System.getProperty("user.home") + "//Desktop//Test Plan Status//Results//"+resultFileName;

		writeExcel(resultsFilePath);
	}

	public static void initSettings()throws Exception{	

		browser = configMap.get("Browser");
		uRL = "https://my300031.s4hana.ondemand.com/ui?saml2=disabled&sap-client=100&sap-language=EN#CloudSolution-manage&/h4screen=test";//configMap.get("URL");
		userName = "_SAPI340909";//configMap.get("UserName");
		password = "6kD4hgw#=Srdc46L=_y(";//configMap.get("Password");
		release = configMap.get("Release");
		testPhase = configMap.get("TestPhase");
		country = configMap.get("Country");
		search = configMap.get("Search");
		uniqueString = configMap.get("UniqueString");
		genericTPName = configMap.get("GenericTestPlanName");


		driverPath = System.getProperty("user.home")+"\\Desktop\\SeleniumFiles";
		browser = browser.toLowerCase();

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
		wait = new WebDriverWait(driver, 100);
		actions = new Actions(driver);
	}

	public static void launchURL()throws Exception{

		driver.get(uRL);
		Thread.sleep(5000);
		driver.manage().window().maximize();
	}

	public static void loginApp()throws Exception{

		wait("//input[@id='USERNAME_FIELD-inner' or @id='j_username']");
		type(By.xpath("//input[@id='USERNAME_FIELD-inner' or @id='j_username']"), userName);
		type(By.xpath("//input[@id='PASSWORD_FIELD-inner' or @id='j_password']"), password);
		click(By.xpath("//button[@id='LOGIN_LINK' or @id='logOnFormSubmit']"));

		String testYourProcesses = "//div[contains(@id,'HomePage')]//span[text()='Test Your Processes']";
		Thread.sleep(30000);

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
		
		if(driver.findElements(By.xpath("//span[text()='Authentication Information']")).size()==1)
			click(By.xpath("//button//bdi[text()='Close']"));
			
	}

	public static int getColumnNumber(String tblXpath, String colName)throws Exception{

		int colNum = -1;
		int colCount = driver.findElements(By.xpath(tblXpath+"/thead/tr/th")).size();
		List<String> columnNames = new ArrayList<String>();

		for(int i=0;i<colCount;i++)
			columnNames.add(driver.findElement(By.xpath(tblXpath+"/thead/tr/th["+(i+1)+"]")).getText());

		colNum = columnNames.indexOf(colName);
		colNum = colNum+1;

		System.out.println("Column Number : "+colNum);
		return colNum;
	}

	public static boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public static void readExcel(String filePath) throws IOException {

		String key = null, value = null;

		FileInputStream ips = new FileInputStream(new File(filePath)); 
		XSSFWorkbook workbook = new XSSFWorkbook(ips);

		Sheet sh = workbook.getSheet("ScopeItems");
		Row row = sh.getRow(0);

		Cell cell = null;

		//To get the Column Names
		for(int i=0;i<row.getLastCellNum();i++){
			cell = row.getCell(i);
			if(cell==null)
				cell=row.createCell(i);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			value = cell.getStringCellValue();

			/*if(value.isEmpty())
				break;
			else*/
			columnNames.add(value);
		}

		System.out.println("Excel columnNames List : "+columnNames);

		int rowCount = sh.getLastRowNum() - sh.getFirstRowNum();
		System.out.println("Excel rowCount : "+rowCount);

		//To get the Process Steps and Labels
		for(int j=0;j<rowCount;j++){
			row = sh.getRow(j+1);
			cell = row.getCell(columnNames.indexOf("Scope Items"));
			cell.setCellType(Cell.CELL_TYPE_STRING);

			value = cell.getStringCellValue();

			scopeItems.add(value);
		}

		System.out.println("Scope Items List : "+scopeItems);

		//To get the Configurations
		sh = workbook.getSheet("Configuration");
		cell = null;
		int n = 0;

		rowCount = sh.getLastRowNum() - sh.getFirstRowNum() + 1;
		System.out.println("Config Sheet rowCount : "+rowCount);

		for(int m=0;m<rowCount;m++){

			row = sh.getRow(m);

			cell = row.getCell(n);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			key = cell.getStringCellValue();

			cell = row.getCell(n+1);
			if(cell==null)
				cell = row.createCell(n+1);
			cell.setCellType(Cell.CELL_TYPE_STRING);	
			value = cell.getStringCellValue();

			configMap.put(key, value);
		}

		System.out.println("Config Map : "+configMap);

		ips.close();
	}


	public static void createFolder(String filePath)throws Exception{

		boolean result = false;
		File folder = new File(filePath);
		// if the directory does not exist, create it
		if (!folder.exists()) {
			System.out.println("creating directory: " + folder.getName());
			folder.mkdir();
			result = true;
		}
	}


	public static void createExcel(String filePath, String sheetName) throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		FileOutputStream fileOut = new FileOutputStream(filePath);
		workbook.write(fileOut);
		fileOut.close();
	}

	public static void copyExcel(String sourceFilePath, String destFilePath)throws Exception{

		FileSystem system = FileSystems.getDefault();
		Path source = system.getPath(sourceFilePath);
		Path target = system.getPath(destFilePath);

		try {
			// Throws an exception if the original file is not found.
			Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			System.out.println("FILE NOT FOUND ERROR");
		}
	}


	// Function to copy a file from one directory to another.
	public static void copyFile(File varFromFile, File varToFile) throws IOException {

		FileInputStream varFromSource = null;	             
		FileOutputStream VarToDestination = null;
		varFromSource = new FileInputStream(varFromFile);				
		VarToDestination = new FileOutputStream(varToFile);				
		byte[] buffer = new byte[4096];

		int bytes_read;

		while ((bytes_read = varFromSource.read(buffer)) != -1)
			VarToDestination.write(buffer, 0, bytes_read);
	}


	@SuppressWarnings("deprecation")
	public static void writeExcel(String resultFilePath) throws IOException {

		XSSFWorkbook workbook = new XSSFWorkbook();
	
		Sheet sh = workbook.createSheet("TestPlanStatus");

		Row row = sh.createRow(0);
		row = sh.getRow(0);

		Cell cell = null;

		columnNames.clear();
		columnNames.add("S.No");
		columnNames.add("Test Plan Name");
		columnNames.add("Last Executed Date");
		columnNames.add("Status");
		
		System.out.println("columnNames : "+columnNames);
		
		//Create new columns for Test Plan Name, Status
		for(int i=0;i<columnNames.size();i++){
			cell = row.createCell(i);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(columnNames.get(i));
		}


		//Writing the test plan name and status

		String status = null, tpName = null, lastExecDate = null;

		for(int i=0;i<testPlanStatusList.size();i++){

			row = sh.createRow(i+1);

			cell = null;

			tpName = testPlanStatusList.get(i).split(" - ")[0];
			status = testPlanStatusList.get(i).split(" - ")[1];
			lastExecDate = testPlanStatusList.get(i).split(" - ")[2];

			System.out.println("tpName : "+tpName+" , status : "+status+"lastExecDate : "+lastExecDate);

			//Writing the Test Plan Name and Status of Scope Item 

			cell = row.createCell(columnNames.indexOf("S.No"));
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue((i+1));

			cell = null;

			cell = row.createCell(columnNames.indexOf("Test Plan Name"));
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(tpName);

			cell = null;

			cell = row.createCell(columnNames.indexOf("Last Executed Date"));
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(lastExecDate);

			cell = null;

			cell = row.createCell(columnNames.indexOf("Status"));
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(status);
		}

		//Deleting the sheets that are not required(if any)
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			if(!(workbook.getSheetName(i).equals("TestPlanStatus") || workbook.getSheetName(i).equals("Configuration")))
				workbook.removeSheetAt(i);
		}

		//Writing in excel workbook
		FileOutputStream out = new FileOutputStream(new File(resultFilePath));
		workbook.write(out);
		out.close();
		System.out.println("Status file generated successfully!!!");
	}

	//Checks for the Test Plan execution status
	public static String readTestPlanStatus(String testPlan)throws Exception{

		int testPlanPercentage = 0;
		String tpStatus = null;

		Thread.sleep(8000);
		wait("//form[@class='sapMSFF']/input");
		clear(By.xpath("//form[@class='sapMSFF']/input"));
		click(By.xpath("//ul[contains(@id,'list-listUl')]/li[1]/div"));
		//scroll();
		click(By.xpath("//ul[contains(@id,'list-listUl')]/li[1]/div"));
		//scroll();
		Thread.sleep(60000);
		type(By.xpath("//form[@class='sapMSFF']/input"), testPlan);
		keyPress(By.xpath("//form[@class='sapMSFF']/input"), Keys.TAB);
		click(By.xpath("//form[@class='sapMSFF']/div[contains(@id,'searchField-search')]"));
		Thread.sleep(3000);
		click(By.xpath("//form[@class='sapMSFF']/div[contains(@id,'searchField-search')]"));
		Thread.sleep(3000);

		int tpSize = driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li")).size();

		if(driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li[1]//div[text()='No data']")).size()==1){
			System.out.println("Test Plan : "+testPlan+" is not found.Hence, skipping");
			tpStatus = "NA";
		}
		else
		{
			System.out.println("No. of rows after search : "+tpSize);

			String value = null, titleUI = null;
			int i=0;

			for(i=0;i<tpSize;i++){
				value = driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(i+1)+"]//span[contains(@id,'titleText')]/span")).getText();

				if(testPlan.equals(value)){
					System.out.println("Selecting row : "+value);
					Thread.sleep(5000);
					click(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(i+1)+"]/div"));
					Thread.sleep(10000);
					titleUI = driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(i+1)+"]/div")).getText();
					System.out.println("titleUI : "+titleUI);
					for(int j=0;j<5;j++){
						Thread.sleep(5000);
						if(!(value.equals(driver.findElement(By.xpath("//h1//span[contains(@id,'titleText-inner')]")).getText())))
							click(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(i+1)+"]/div"));
						else
							break;
					}

					tpStatus = driver.findElement(By.xpath("//div[contains(@id,'status1')]//span[contains(@id,'status1-text')]")).getText();

					if("In Process".equalsIgnoreCase(tpStatus)){
						testPlanPercentage = Integer.parseInt(titleUI.split("%")[0]);
						tpStatus = tpStatus+" - "+testPlanPercentage;
					}

					System.out.println("Test Plan Status : "+tpStatus);

					break;
				}
			}
		}
		return tpStatus;
	}

	public static void doubleClick(By locator)throws Exception{

		wait.until(ExpectedConditions.elementToBeClickable(locator));
		new Actions(driver).moveToElement(driver.findElement(locator)).doubleClick().perform();
	}

	public static void actionsClick(By locator)throws Exception{

		wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		new Actions(driver).moveToElement(driver.findElement(locator)).click().perform();
	}

	public static void type(By locator , String value)throws Exception{

		wait.until(ExpectedConditions.elementToBeClickable(locator));
		driver.findElement(locator).sendKeys(value);
	}

	public static void click(By locator)throws Exception{

		if(locator.toString().contains("bdi"))
			Thread.sleep(10000);
		else
			wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

		driver.findElement(locator).click();
	}

	public static void clear(By locator)throws Exception{

		wait.until(ExpectedConditions.elementToBeClickable(locator));
		driver.findElement(locator).clear();
	}

	public static void keyPress(By locator, Keys key)throws Exception{

		wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		driver.findElement(locator).sendKeys(key);
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
}