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

public class Mvn_ReadTestPlanStatus_MassExec{

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
	public static String lastExecutedOn = null;

	public static String testPlanName = null;

	public static String driverPath = null;
	public static String excelFilePath = null;
	public static String resultsFilePath = null;
	public static String resultFileName = null;

	public static FluentWait<WebDriver> wait = null;
	public static WebDriver driver = null;

	public static List<String> scopeItems = new ArrayList<String>();;
	public static Map<String,String> configMap = new HashMap<String,String>();
	public static List<String> columnNames = new ArrayList<String>();
	public static Map<String,String> testPlanNames = new HashMap<String,String>();
	public static Map<String,String> testPlanStatus = new HashMap<String,String>();

	public static CellStyle headerCellStyle = null;
	public static CellStyle valueCellStyle = null;

	public static Actions actions = null;

	public static void scrollDown()throws Exception{
		actions.keyDown(Keys.CONTROL).sendKeys(Keys.END).perform();
	}

	public static void mouseOver(WebElement ele)throws Exception{
		actions.moveToElement(ele).build().perform();
	}
	public static Map<String,String> readStatus()throws Exception{

		String searchCriteria = null;
		Map<String,String> liMap = new HashMap<String,String>();
		//click(By.xpath("//li[text()='Post-Upgrade Tests']"));
		Thread.sleep(3000);
		//System.out.println("lastExecutedOn");
		
		/*if(!("".equals(lastExecutedOn))){
			click(By.xpath("//button[@title='View settings']/span/span"));
			click(By.xpath("//li[contains(@id,'lastExecutedOnViewSettings')]//div[text()='Last Executed On']"));
			type(By.xpath("//input[contains(@id,'lastExecutedOnDateRangeSelection')]") , lastExecutedOn);
			click(By.xpath("//button[contains(@id,'acceptbutton')]//bdi[text()='OK']"));
		}*/

		if("Yes".equals(genericTPName))
			searchCriteria = "TC_";
		else
			searchCriteria = release+"_"+uniqueString+"_"+testPhase;
		Thread.sleep(20000);
		type(By.xpath("//form[@class='sapMSFF']/input"), searchCriteria);
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
			String testPlanName = driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(j+1)+"]//span[contains(@id,'titleText')]/span")).getText();
			String testPlanStatus = driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(j+1)+"]//div[contains(@class,'StatusDiv')]//span[contains(@id,'text')]")).getText();

			if(!liMap.containsKey(testPlanName))
				liMap.put(testPlanName,testPlanStatus);
		}

		System.out.println("liMap : "+liMap);

		for(Entry<String, String> entry: liMap.entrySet()) {
			System.out.println(entry.getKey()+" - "+entry.getValue());  
		}
		return liMap;
	}
	public static void main(String[] args) throws Exception {

		excelFilePath = System.getProperty("user.home") + "//Desktop//CreateAndRunTestPlans//ScopeItems.xlsx";
		System.out.println("Scope Items Excel Path : "+excelFilePath);	

		readExcel(excelFilePath);
		initSettings();
		launchURL();
		loginApp();
Thread.sleep(20000);
		testPlanStatus = readStatus();

		DateFormat dateFormat = new SimpleDateFormat("ddMMyyHHMMss");
		Date date = new Date();
		String today = dateFormat.format(date);

		resultFileName = "Status_"+today+".xlsx";

		resultsFilePath = System.getProperty("user.home") + "//Desktop//Test Plan Status//Results//"+resultFileName;

		File src = new File(excelFilePath);
		File dest = new File(resultsFilePath);

		copyFile(src,dest);

		writeExcel(excelFilePath, resultsFilePath);
	}

	public static void initSettings()throws Exception{	

		browser = configMap.get("Browser");
		uRL = configMap.get("URL");
		userName = configMap.get("UserName");
		password = configMap.get("Password");
		release = configMap.get("Release");
		testPhase = configMap.get("TestPhase");
		country = configMap.get("Country");
		search = configMap.get("Search");
		uniqueString = configMap.get("UniqueString");
		genericTPName = configMap.get("GenericTestPlanName");
		lastExecutedOn = configMap.get("LastExecutedOn");


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
			//cap.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
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
		wait = new WebDriverWait(driver, 120);
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
	public static void writeExcel(String excelFilePath, String resultFilePath) throws IOException {

		String value = null;

		FileInputStream ips = new FileInputStream(new File(resultFilePath)); 
		XSSFWorkbook workbook = new XSSFWorkbook(ips);
		//XSSFCellStyle style = null;

		Sheet sh = workbook.getSheet("ScopeItems");
		Row row = sh.getRow(0);

		Cell cell = null;

		int rowCount = sh.getLastRowNum() - sh.getFirstRowNum();
		System.out.println("Excel rowCount : "+rowCount);

		//style = workbook.createCellStyle();

		//Create new column for Test Plan Name
		if(!columnNames.contains("Test Plan Name")){
			System.out.println("Test Plan Name column is not available in the excel.Hence creating");
			cell = row.createCell(columnNames.size());
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue("Test Plan Name");

			cell = row.getCell(columnNames.size()-1);
			CellStyle cs = cell.getCellStyle();

			cell = row.getCell(columnNames.size());
			cell.setCellStyle(cs);
			columnNames.add("Test Plan Name");
		}

		//Create new Column for Status
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
		Date date = new Date();
		String today = dateFormat.format(date);
		System.out.println("Today's Date : "+today);
		String statusColumn = testPhase+" Status "+today;

		if(!columnNames.contains(statusColumn)){
			System.out.println(statusColumn+" column is not avialable in the excel");
			cell = row.createCell(columnNames.size());
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(statusColumn);

			cell = row.getCell(columnNames.size()-1);
			CellStyle cs = cell.getCellStyle();

			cell = row.getCell(columnNames.size());
			cell.setCellStyle(cs);
			columnNames.add(statusColumn);
		}

		//To search for the scope item row and write the test plan and status
		for(int i=0;i<scopeItems.size();i++){
			for(int j=0;j<rowCount;j++){
				row = sh.getRow(j+1);
				cell = row.getCell(columnNames.indexOf("Scope Items"));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				value = cell.getStringCellValue();

				cell = null;
				String status = null;
				String tpName = null;

				//String tpName = release+"_TestRun_"+testPhase+"_"+value+"_"+country;

				//Format : TC_1GA or 1902_TestRun_SAT_1GA_DE
				if("Yes".equals(genericTPName))
					tpName = "TC_"+scopeItems.get(i);
				else
					tpName = release+"_"+uniqueString+"_"+testPhase+"_"+value+"_"+country;

				if(testPlanStatus.containsKey(tpName))
					status = testPlanStatus.get(tpName);
				else{
					tpName = "NA";
					status = "NA";
				}

				//Writing the Test Plan Name and Status of Scope Item 
				cell = row.getCell(columnNames.indexOf("Test Plan Name"));
				if(cell==null)
					cell = row.createCell(columnNames.indexOf("Test Plan Name"));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(tpName);

				cell = row.getCell(columnNames.indexOf("Test Plan Name")-1);
				CellStyle cs = cell.getCellStyle();

				cell = row.getCell(columnNames.indexOf("Test Plan Name"));
				cell.setCellStyle(cs);

				cell = null;

				//Writing the Execution Status of Scope Item
				cell = row.getCell(columnNames.indexOf(statusColumn));
				if(cell==null)
					cell = row.createCell(columnNames.indexOf(statusColumn));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(status);

				cell = row.getCell(columnNames.indexOf(statusColumn)-1);
				cs = cell.getCellStyle();

				cell = row.getCell(columnNames.indexOf(statusColumn));
				cell.setCellStyle(cs);
			}
		}

		//Deleting the sheets that are not required(if any)
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			if(!(workbook.getSheetName(i).equals("ScopeItems") || workbook.getSheetName(i).equals("Configuration")))
				workbook.removeSheetAt(i);
		}

		//Renaming Sheet
		workbook.setSheetName(workbook.getSheetIndex("ScopeItems"), release+" "+testPhase+" Status");

		//Writing in excel workbook
		FileOutputStream out = new FileOutputStream(new File(resultFilePath));
		workbook.write(out);
		out.close();

		ips.close();

		//Writing ReadTestPlanStatus File Name in Scope Items excel
		ips = new FileInputStream(new File(excelFilePath)); 
		workbook = new XSSFWorkbook(ips);

		sh = workbook.getSheet("Configuration");
		row = sh.getRow(0);

		cell = null;
		int n = 0;
		String key = null;
		boolean lastRunExcel = false;

		rowCount = sh.getLastRowNum() - sh.getFirstRowNum() + 1;
		System.out.println("Config Sheet rowCount : "+rowCount);

		for(int m=0;m<rowCount;m++){
			row = sh.getRow(m);
			cell = row.getCell(n);
			cell.setCellType(Cell.CELL_TYPE_STRING);	
			key = cell.getStringCellValue();

			if("Last Run Excel".equalsIgnoreCase(key)){
				lastRunExcel = true;
				break;
			}
		}

		if(lastRunExcel){
			cell = row.getCell(n+1);
			if(cell==null)
				cell = row.createCell(n+1);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(resultFileName);

			cell = row.getCell(n);
			CellStyle cs = cell.getCellStyle();

			cell = row.getCell(n+1);
			cell.setCellStyle(cs);
		}
		else
		{
			row = sh.getRow(rowCount+1);

			cell = row.getCell(n);
			if(cell==null)
				cell = row.createCell(n);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue("Last Run Excel");

			cell = row.getCell(n+1);
			if(cell==null)
				cell = row.createCell(n+1);
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(resultFileName);
		}


		//Writing in excel workbook
		out = new FileOutputStream(new File(excelFilePath));
		workbook.write(out);
		out.close();

		ips.close();
	}

	/*@SuppressWarnings("deprecation")
	public static void writeExcel(String destFilePath, String sheetName) throws IOException {

		String value = null;

		//FileInputStream ips = new FileInputStream(new File(destFilePath)); 

		FileUtils.copyFile(new File(excelFilePath), new File(destFilePath));

		FileInputStream ips = new FileInputStream(new File(destFilePath));
		XSSFWorkbook workbook = new XSSFWorkbook(ips);

		String timeStamp = destFilePath.split("_")[1];
		timeStamp = timeStamp.split(".")[0];

		Sheet sh = workbook.getSheet(sheetName);
		workbook.setSheetName(workbook.getSheetIndex(sheetName), workbook.getSheetIndex(sheetName)+"_"+timeStamp);

		Row row = sh.getRow(0);

		Cell cell = null;

		int rowCount = sh.getLastRowNum() - sh.getFirstRowNum();
		System.out.println("Excel rowCount : "+rowCount);

		//Create new Column
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
		Date date = new Date();
		String today = dateFormat.format(date);
		System.out.println("Today's Date : "+today);
		String statusColumn = testPhase+" Status "+today;

		if(!columnNames.contains(statusColumn)){
			System.out.println(statusColumn+" column is not available in the excel");
			cell = row.createCell(columnNames.size());
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(statusColumn);

			cell = row.getCell(columnNames.size()-1);
			headerCellStyle = cell.getCellStyle();

			cell = row.getCell(columnNames.size());
			cell.setCellStyle(headerCellStyle);
			columnNames.add(statusColumn);
		}

		//To search for the scope item row and write the test plan and status
		for(int i=0;i<scopeItems.size();i++){
			for(int j=0;j<rowCount;j++){
				row = sh.getRow(j+1);
				cell = row.getCell(columnNames.indexOf("Scope Items"));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				value = cell.getStringCellValue();

				cell = null;

				if(value.equals(scopeItems.get(i))){

					//Writing the Test Plan Name of Scope Item
					cell = row.getCell(columnNames.indexOf("Test Plan Name"));
					if(cell==null)
						cell = row.createCell(columnNames.indexOf("Test Plan Name"));
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(testPlanNames.get(scopeItems.get(i)));

					cell = row.getCell(columnNames.indexOf("Test Plan Name")-1);
					valueCellStyle = cell.getCellStyle();

					cell = row.getCell(columnNames.indexOf("Test Plan Name"));
					cell.setCellStyle(valueCellStyle);

					cell = null;

					//Writing the Execution Status of Scope Item
					cell = row.getCell(columnNames.indexOf(statusColumn));
					if(cell==null)
						cell = row.createCell(columnNames.indexOf(statusColumn));
					cell.setCellType(Cell.CELL_TYPE_STRING);
					cell.setCellValue(testPlanStatus.get(scopeItems.get(i)));

					cell = row.getCell(columnNames.indexOf(statusColumn)-1);
					headerCellStyle = cell.getCellStyle();

					cell = row.getCell(columnNames.indexOf(statusColumn));
					cell.setCellStyle(headerCellStyle);		
				}
			}
		}

		//Writing in excel workbook
		FileOutputStream out = new FileOutputStream(new File(destFilePath));
		workbook.write(out);
		out.close();

		ips.close();
	}
	 */
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