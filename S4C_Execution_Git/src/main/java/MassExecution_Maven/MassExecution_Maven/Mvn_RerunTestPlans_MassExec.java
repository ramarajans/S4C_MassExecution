package MassExecution_Maven.MassExecution_Maven;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

public class Mvn_RerunTestPlans_MassExec{

	public static String browser = null;
	public static String uRL = null;
	public static String userName = null;
	public static String password = null;
	public static String country = null;
	public static String release = null;
	public static String testPhase = null;
	public static String uniqueString = null;
	public static String genericTPName = null;

	public static String testPlanName = null;

	public static String driverPath = null, excelFilePath = null, configFilePath = null;

	public static FluentWait<WebDriver> wait = null;
	public static WebDriver driver = null;

	public static List<String> scopeItems = new ArrayList<String>();
	public static List<String> testPlanNames = new ArrayList<String>();
	public static Map<String,String> configMap = new HashMap<String,String>();
	public static List<String> columnNames = new ArrayList<String>();

	public static void main(String[] args) throws Exception {

		excelFilePath = System.getProperty("user.home") + "//Desktop//CreateAndRunTestPlans//ScopeItems1 - Copy.xlsx";
		configFilePath = System.getProperty("user.home") + "//Desktop//CreateAndRunTestPlans//ScopeItems.xlsx";
		System.out.println("Excel Path : "+excelFilePath);
		readExcel(excelFilePath,configFilePath);
		initSettings();
		launchURL();
		loginApp();

		for(int i=0;i<scopeItems.size();i++){

			/*//Format : TC_1GA or 1902_TestRun_SAT_1GA_DE
			if("Yes".equals(genericTPName))
				testPlanName = "TC_"+scopeItems.get(i);
			else
				testPlanName = release+"_"+uniqueString+"_"+testPhase+"_"+scopeItems.get(i)+"_"+country;
*/			
			testPlanName = testPlanNames.get(i);
			
			executeTestPlan(testPlanName, scopeItems.get(i));
			Thread.sleep(30000);

			//wait between every 10 test plans
			if(i>0 && i%10==0){
				System.out.println("Waiting for few minutes!!!");
				Thread.sleep(120*1000);
			}
		}
	}

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
	public static void readExcel(String filePath, String configFilePath) throws IOException {

		String key = null, value = null;

		FileInputStream ips = new FileInputStream(new File(filePath)); 
		XSSFWorkbook workbook = new XSSFWorkbook(ips);

		Sheet sh = workbook.getSheet("ScopeItems");
		Row row = sh.getRow(0);

		Cell cell = null;

		//To get the Column Names
		for(int i=0;i<row.getLastCellNum();i++){
			cell = row.getCell(i);
			cell.setCellType(Cell.CELL_TYPE_STRING);	
			value = cell.getStringCellValue();

			if(value.isEmpty())
				break;
			else
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

		//To get the Test Plan names
		for(int k=0;k<rowCount;k++){
			row = sh.getRow(k+1);
			cell = row.getCell(columnNames.indexOf("Test Plan Name"));
			cell.setCellType(Cell.CELL_TYPE_STRING);

			value = cell.getStringCellValue();

			testPlanNames.add(value);
		}

		System.out.println("testPlanNames : "+testPlanNames);

		//To get the Configurations

		ips = new FileInputStream(new File(configFilePath)); 
		workbook = new XSSFWorkbook(ips);

		sh = workbook.getSheet("Configuration");

		row = sh.getRow(1);
		cell = row.getCell(0);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		value = cell.getStringCellValue();

		/*sh = workbook.getSheet(value);
		System.out.println("Config Sheet : "+value);
		 */
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
			cell.setCellType(Cell.CELL_TYPE_STRING);	
			value = cell.getStringCellValue();

			configMap.put(key, value);
		}

		System.out.println("Config Map : "+configMap);

		ips.close();
	}

	//To create a Test Plan
	public static String createTestPlan(String testPlanName, String scopeItem)throws Exception{

		System.out.println("Inside createTestPlan");

		System.out.println("Test Plan to be created is : "+testPlanName);
		Thread.sleep(15000);
		wait("//button[@title='Add test plan']/span/span");	
		click(By.xpath("//button[@title='Add test plan']/span/span"));
		wait("//input[contains(@id,'TestplanName-inner')]");
		type(By.xpath("//input[contains(@id,'TestplanName-inner')]") , testPlanName);
		type(By.xpath("//form[@id='searchId-F']/input[@id='searchId-I']") , scopeItem);
		click(By.id("searchId-search"));

		int colNumberType = 0, colNumberScopeItem = 0;

		colNumberType = getColumnNumber("//table[contains(@id,'addScenarioTable-listUl')]", "Type");
		colNumberScopeItem = getColumnNumber("//table[contains(@id,'addScenarioTable-listUl')]", "Scope Item");

		if(colNumberScopeItem==0)
			colNumberScopeItem = getColumnNumber("//table[contains(@id,'addScenarioTable-listUl')]", "Name");

		System.out.println("colNumberType : "+colNumberType+" , colNumberScopeItem : "+colNumberScopeItem);

		int rowCount = driver.findElements(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr")).size();
		System.out.println("rowCount : "+rowCount);

		String cellValueType = null, cellValueScopeItem = null;

		boolean stdProcessFound = false;

		for(int g=1;g<=rowCount;g++){

			if("No data".equals(driver.findElement(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr[1]/td[1]")).getText())){
				System.out.println(driver.findElement(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr[1]/td[1]")).getText());
			}
			else{

				cellValueType = driver.findElement(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr["+g+"]/td["+colNumberType+"]/span")).getText();
				cellValueScopeItem = driver.findElement(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr["+g+"]/td["+colNumberScopeItem+"]/span")).getText();

				System.out.println("cellValueType : "+cellValueType+" , cellValueScopeItem : "+cellValueScopeItem);

				if(scopeItem.equalsIgnoreCase(cellValueScopeItem) && "Standard".equals(cellValueType)){
					stdProcessFound = true;
					actionsClick(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr["+g+"]/td[2]/div/div/input"));
					//click(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr["+g+"]/td[2]/div/div/input"));
					click(By.xpath("//button//bdi[text()='Save']"));

					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1//span[contains(@id,'titleText-inner')]")));

					String testPlanNameHeader = driver.findElement(By.xpath("//h1//span[contains(@id,'titleText-inner')]")).getText();

					for(int i=0;i<5;i++){

						testPlanNameHeader = driver.findElement(By.xpath("//h1//span[contains(@id,'titleText-inner')]")).getText();

						if(!(testPlanName.equals(testPlanNameHeader)))
							Thread.sleep(10000);
						else
							break;
					}

					System.out.println("testPlanNameHeader : "+testPlanNameHeader);

					if(testPlanName.equals(testPlanNameHeader))
						System.out.println("Test Plan created successfully");
					else{
						System.out.println("Test Plan not created");
						throw new NoSuchElementException();
					}
					break;
				}
			}
		}

		if(stdProcessFound)
			return testPlanName;
		else{
			System.out.println("Standard Test Process for scope item : "+scopeItem+" is not available. main skipping test plan creation");

			click(By.xpath("//button//bdi[text()='Cancel']"));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@id,'mbox')]//bdi[text()='Yes']")));
			click(By.xpath("//button[contains(@id,'mbox')]//bdi[text()='Yes']"));

			return null;
		}	
	}

	//To execute a test plan. If test plan is present already, it selects the test plan and executes. Else creates a test plan and executes.
	public static void executeTestPlan(String tPName, String scopeItem)throws Exception{

		String testPlan = null;
		String selectVariantTbl = "//table[contains(@id,'procedureListEditTable-listUl')]";
		boolean tpFound = searchTestPlan(tPName);
		int colNumVarName = 0, varTblRowCount = 0;

		if(tpFound){
			System.out.println("Test Plan : "+tPName+" already exists");
			testPlan = tPName;
		}
		else
			testPlan = createTestPlan(tPName, scopeItem);

		Thread.sleep(8000);

		if(testPlan!=null){

			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'runBtn') or contains(@id,'stopBtn')]//bdi[contains(text(),'Execute') or text()='Stop Test']")));

			//Executing the created Test Plan
			if(driver.findElements(By.xpath("//button[contains(@id,'stopBtn')]//bdi[text()='Stop Test']")).size()==1){
				System.out.println("Test Plan : "+tPName+ " is already In Process");
			}
			else if(driver.findElements(By.xpath("//button[contains(@id,'runBtn')]//bdi[contains(text(),'Execute')]")).size()==1){
				System.out.println("Executing the test plan");
				actionsClick(By.xpath("//button[contains(@id,'runBtn')]//bdi[contains(text(),'Execute')]"));

				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@id,'popover')]//button//bdi[text()='Execute with variants']")));
				actionsClick(By.xpath("//div[contains(@id,'popover')]//button//bdi[text()='Execute with variants']"));

				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(selectVariantTbl)));

				colNumVarName= getColumnNumber(selectVariantTbl, "Variant Name");
				varTblRowCount = driver.findElements(By.xpath(selectVariantTbl+"/tbody/tr")).size();

				for(int j=1;j<=varTblRowCount;j++){
					String cellVal = driver.findElement(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td["+colNumVarName+"]/span")).getText();

					if("DEFAULT_VARIANT".equals(cellVal)){
						actionsClick(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]"));
						//driver.findElement(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]")).click();
						Thread.sleep(2000);
						break;
					}
				}

				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'execvar--execWithVar')]//bdi[contains(text(),'Execute')]")));

				actionsClick(By.xpath("//button[contains(@id,'execvar--execWithVar')]//bdi[contains(text(),'Execute')]"));

				Thread.sleep(10000);
				//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@id,'stopBtn')]//bdi[text()='Stop Test']")));

				System.out.println("Test Plan : "+tPName+ " triggered for execution");

			}
			else
				System.out.println("Execute or Re-Execute Button is not found");
		}
	}

	//Searches for a test plan. Returns true if the test plan is found. Else False.
	public static boolean searchTestPlan(String testPlan)throws Exception{

		boolean testPlanFound = false;

		Thread.sleep(20000);
		wait("//form[@class='sapMSFF']/input");
		Thread.sleep(4000);
		clear(By.xpath("//form[@class='sapMSFF']/input"));
		type(By.xpath("//form[@class='sapMSFF']/input"), testPlan);
		driver.findElement(By.xpath("//form[@class='sapMSFF']/input")).sendKeys(Keys.TAB);
		click(By.xpath("//form[@class='sapMSFF']/div[contains(@id,'searchField-search')]"));
		Thread.sleep(3000);
		click(By.xpath("//form[@class='sapMSFF']/div[contains(@id,'searchField-search')]"));
		Thread.sleep(5000);

		int tpSize = driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li")).size();

		if(driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li[1]//div[text()='No data']")).size()==1){
			System.out.println("Test Plan : "+testPlan+" is not found");
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
					testPlanFound = true;
					break;
				}
			}
		}

		return testPlanFound;
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
}