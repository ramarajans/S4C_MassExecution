package MassExecution_Maven.MassExecution_Maven;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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

public class Mvn_ManageUsers_MassExec{

	public static String browser = null;
	public static String uRL = null;
	public static String userName = null;
	public static String password = null;

	public static String driverPath = null;
	public static String excelFilePath = null;

	public static FluentWait<WebDriver> wait = null;
	private static int waitTime = 200;

	public static WebDriver driver = null;

	public static List<String> userNamesList = new ArrayList<String>();
	public static List<String> userNamesListExcel = new ArrayList<String>();
	public static Map<String,String> userNamesMap = new HashMap<String,String>();
	public static Map<String,String> configMap = new HashMap<String,String>();
	public static List<String> roles = new ArrayList<String>();
	public static List<String> columnNames = new ArrayList<String>();
	public static List<String> rolesToBeAdded = new ArrayList<String>();

	public static String globalRole = "SAP_BR_BPC_EXPERT";

	public static void main(String[] args) throws Exception {

		excelFilePath = System.getProperty("user.home") + "//OneDrive - SAP SE//Desktop//AddRoles//ManageUsers2.xlsx";
		System.out.println("Excel Path : "+excelFilePath);
		readExcel(excelFilePath);
		initSettings();
		launchURL();
		loginApp();
		manageUsers();
	}

	public static void initSettings()throws Exception{	

		browser = configMap.get("Browser");
		uRL = configMap.get("URL");
		userName = configMap.get("UserName");
		password = configMap.get("Password");

		driverPath = System.getProperty("user.home")+"\\OneDrive - SAP SE\\Desktop\\SeleniumFiles";
		browser = browser.toLowerCase();

		if(browser.contains("chrome")){
			System.setProperty("webdriver.chrome.driver",driverPath+"\\chromedriver.exe");

			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			//chromePrefs.put("download.default_directory", downloadFilepath);
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
		wait = new WebDriverWait(driver, waitTime);
	}

	public static void launchURL()throws Exception{

		driver.get(uRL);
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
		return colNum;
	}

	public static int getRowCount(String tblXpath)throws Exception{

		int rowCount = driver.findElements(By.xpath(tblXpath+"/tbody/tr")).size();
		return rowCount;
	}

	public static boolean isAlertPresent()throws Exception {

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

		Sheet sh = workbook.getSheet("Users");
		Row row = sh.getRow(0);

		Cell cell = null, cell1 = null;

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

		String userName = null, role = null;

		//To get the User Names
		for(int j=0;j<rowCount;j++){
			row = sh.getRow(j+1);
			cell = row.getCell(columnNames.indexOf("User Name"));
			cell.setCellType(Cell.CELL_TYPE_STRING);

			userName = cell.getStringCellValue();
			userNamesListExcel.add(userName);

			cell1 = row.getCell(columnNames.indexOf("Role"));
			cell1.setCellType(Cell.CELL_TYPE_STRING);
			role = cell1.getStringCellValue();

			//Reading the Roles
			if(!role.isEmpty())
				userNamesMap.put(userName,role);
		}

		System.out.println("userNamesListExcel : "+userNamesListExcel);
		System.out.println("userNamesMap : " +userNamesMap);

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
			cell.setCellType(Cell.CELL_TYPE_STRING);	
			value = cell.getStringCellValue();

			configMap.put(key, value);
		}

		System.out.println("Config Map : "+configMap);

		ips.close();
	}

	/*public static void manageUsers()throws Exception{

		String val = null;

		click(By.xpath("//button[@title='Manage User']/span/span"));
		String testUsersTbl = "//table[contains(@id,'viewSecureTable-listUl')]";

		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(testUsersTbl)));

		int rowTestUsers = getRowCount(testUsersTbl);
		int userNameCol = getColumnNumber(testUsersTbl, "User Name");
		System.out.println("userNameCol : "+userNameCol);

		for(int j=1;j<=rowTestUsers;j++){
			val = driver.findElement(By.xpath(testUsersTbl+"/tbody/tr["+j+"]/td["+userNameCol+"]/span/bdi")).getText();
			userNamesList.add(val);
		}
		Set<String> userNameKeys = userNamesMap.keySet();

		for(int i=0;i<userNamesMap.size();i++){
			if(!userNamesList.contains("")){
				click(By.xpath("//button[contains(@id,'viewSecureTableAddBtn')]/span/span"));

				wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Secure_Dialog--roleId-inner")));
				type(By.id("Secure_Dialog--roleId-inner"),"");
			}
		}

	}

	public static void manageUsers_0512()throws Exception{

		String val = null;

		Thread.sleep(10000);
		click(By.xpath("//button[@title='Manage User']/span/span"));
		Thread.sleep(10000);
		String testUsersTbl = "//table[contains(@id,'viewSecureTable-listUl')]";

		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(testUsersTbl)));

		int rowTestUsers = getRowCount(testUsersTbl);
		System.out.println("rowTestUsers : "+rowTestUsers);
		int userNameCol = getColumnNumber(testUsersTbl, "User Name");
		System.out.println("userNameCol : "+userNameCol);

		Thread.sleep(5000);
		//Fetching user names from Test Users table
		for(int j=1;j<=rowTestUsers;j++){
			val = driver.findElement(By.xpath(testUsersTbl+"/tbody/tr["+j+"]/td["+userNameCol+"]/span")).getText();
			userNamesList.add(val);
		}

		System.out.println("userNamesList : "+userNamesList);


		//Set<String> userNameKeys = userNamesMap.keySet();
		String userName = null,role = null;

		String customURL = uRL.split("ui")[0];

		//for(int j=0;j<userNamesList.size();j++){
		for(int i=0;i<userNamesListExcel.size();i++){
			userName = userNamesListExcel.get(i);
			System.out.println("User Name : "+userName);
			if(!userNamesList.contains(userName)){

				click(By.xpath("//button[contains(@id,'viewSecureTableAddBtn')]/span/span"));

				wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Secure_Dialog--roleId-inner")));

				role = userNamesMap.get(userName);

				type(By.id("Secure_Dialog--roleId-inner"), role);
				type(By.name("DescriptionName"), role);
				type(By.name("UserName"), userName);
				type(By.name("PasswordName"), password);
				type(By.name("UrlName"), customURL);
				click(By.xpath("//button//bdi[text()='OK']"));
			}
		}
		//}
	}
	 */

	public static void manageUsers()throws Exception{

		String val = null;
		int userNameCol;
		String testUsersTbl = "//table[contains(@id,'viewSecureTable-listUl')]";
		String busUsersItemsTbl = "//table[contains(@id,'businessUserValueHelp-table-table')]";

		Thread.sleep(30000);

		//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@title='Manage User']/span/span")));
		click(By.xpath("//button[@title='Manage User']/span/span"));
		click(By.xpath("//button//bdi[text()='Manage User']"));
		Thread.sleep(10000);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(testUsersTbl)));
		int rowTestUsers = getRowCount(testUsersTbl);
		System.out.println("rowTestUsers : "+rowTestUsers);
		userNameCol = getColumnNumber(testUsersTbl, "User Name or Email ID");
		if(userNameCol==0)
			userNameCol = getColumnNumber(testUsersTbl, "UserName or Email ID");
		
		System.out.println("userNameCol : "+userNameCol);
		Thread.sleep(30000);

		//Fetching user names from Test Users table

		if("No data".equals(driver.findElement(By.xpath(testUsersTbl+"/tbody/tr[1]/td[1]")).getText())){
			System.out.println("No test users available!!");
		}
		else{
			for(int j=1;j<=rowTestUsers;j++){
				val = driver.findElement(By.xpath(testUsersTbl+"/tbody/tr["+j+"]/td["+userNameCol+"]/span")).getText();
				userNamesList.add(val);
			}
		}
		System.out.println("------------------");
		System.out.println("userNamesListfromUI : "+userNamesList);
		System.out.println("UserNameListFromExcel:  "+userNamesListExcel);
		System.out.println("------------------");
		List<String> union = new ArrayList<String>(userNamesList);
		union.addAll(userNamesListExcel);
		List<String> intersection = new ArrayList<String>(userNamesList);
		intersection.retainAll(userNamesListExcel);
		List<String> symmetricDifference = new ArrayList<String>(union);
		symmetricDifference.removeAll(intersection);
		List<String> usersTobeAdded= new ArrayList<String>(symmetricDifference);
		usersTobeAdded.retainAll(userNamesListExcel);
		System.out.println("UsersTobeAdded--------"+usersTobeAdded);
		//Set<String> userNameKeys = userNamesMap.keySet();
		String userName = null,role = null;
		String customURL = uRL.split("ui")[0];
		int i=0, rc=0;
		String cellVal = "";
		boolean userFound = false;

		//for(int j=0;j<userNamesList.size();j++){
		for(i=0;i<usersTobeAdded.size();i++){
			System.out.println("UsersTobeAdded : "+usersTobeAdded);
			userName = usersTobeAdded.get(i);
			System.out.println("User Name : "+userName);
			Thread.sleep(3000);
			//click(By.xpath("//button[contains(@id,'viewSecureTableAddBtn')]/span/span"));
			click(By.xpath("//span[contains(@id,'masterPageId--pullToRefresh-T')]"));
			System.out.println("refresh button clicked");
			wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(@id,'viewSecureTableAddBtn-img')]")));
			System.out.println("Add button is displayed");
			click(By.xpath("//span[contains(@id,'viewSecureTableAddBtn-img')]"));
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("Secure_Dialog--roleId-inner")));
			role = userNamesMap.get(userName);
			System.out.println("role : "+role);
			Thread.sleep(20000);
			clear(By.xpath("//input[@id='Secure_Dialog--roleId-inner']"));
			type(By.xpath("//input[@id='Secure_Dialog--roleId-inner']"), role);
			type(By.xpath("//input[@id='Secure_Dialog--descriptionId-inner']"), userName);
			click(By.xpath("//span[@id='Secure_Dialog--userId-vhi']"));

			Thread.sleep(10000);
			type(By.xpath("//section[contains(@id,'businessUserValueHelp-cont')]//input[contains(@id,'usernameFilter-inner')]"),userName);

			click(By.xpath("//button[contains(@id,'btnGo')]//bdi[text()='Go']"));

			Thread.sleep(15000);

			/*if(driver.findElements(By.xpath("//span[text()='Use the search to get results']")).size()>0){
				System.out.println("User : "+userName+" is not available!!");
				click(By.xpath("//button[contains(@id,'businessUserValueHelp-cancel')]//bdi[text()='Cancel']"));
				click(By.xpath("//button//bdi[text()='Cancel']"));

			}
			else{
			 */	
			rc = getRowCount(busUsersItemsTbl);
			System.out.println("rc : "+rc);

			for(int j=1;j<rc;j++){
				cellVal = driver.findElement(By.xpath(busUsersItemsTbl+"//tr["+j+"]/td//bdi")).getText();
				System.out.println("cellVal : "+cellVal);
				if(cellVal!=null & userName.equals(cellVal)){
					click(By.xpath(busUsersItemsTbl+"//tr["+j+"]/td//bdi"));
					Thread.sleep(5000);
					System.out.println("Selected the row : "+cellVal);
					userFound = true;
					break;
				}
				else
					userFound = false;
			}

			if(userFound){
				Thread.sleep(10000);
				type(By.name("PasswordName"), password);
				//type(By.name("UrlName"), customURL);
				click(By.xpath("//button//bdi[text()='Save']"));
				Thread.sleep(10000);
			}
			else{
				System.out.println("User : "+userName+" not found");
				click(By.xpath("//div[contains(@id,'businessUserValueHelp')]//button//bdi[text()='Cancel']"));
				Thread.sleep(8000);
				click(By.xpath("//div[contains(@id,'footer')]//button//bdi[text()='Cancel']"));				
			}
		}
	}

	public static void test(String userName)throws Exception{

		String[] rolesArray = null;
		String roles = null;
		String role = null;
		roles = globalRole;

		if(userNamesMap.containsKey(userName)){
			roles = roles+","+userNamesMap.get(userName);
			System.out.println("userName : "+userName+" , roles : "+roles);	
		}

		rolesArray = roles.split("\\,");

		System.out.println("Searching for user : "+userName);
		clear(By.xpath("//input[contains(@id,'btnBasicSearch-I')]"));
		type(By.xpath("//input[contains(@id,'btnBasicSearch-I')]"), userName);

		click(By.xpath("//div[contains(@id,'btnBasicSearch-search')]"));
		click(By.xpath("//button//bdi[text()='Go']"));

		String busUserstblXpath  = "//table[contains(@id,'userTable-listUl')]";
		String assignBusRolesTblXpath = "//table[contains(@id,'tblAssignedBusinessRoles-listUl')]";
		String busRolesTblXpath = "//table[@id='selectBusinessRolesTable-listUl']";

		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(busUserstblXpath)));

		int rowCountbususersTbl = getRowCount(busUserstblXpath);
		int userNameCol = getColumnNumber(busUserstblXpath, "User Name");
		System.out.println("userNameCol : "+userNameCol);

		String val = null, value = null;
		int busRoleIDCol = 0, rowCountAssignedBusRolTbl = 0, rowCountbusRolesTbl = 0, colNumAssignBusRolTbl = 0;

		for(int i=1;i<=rowCountbususersTbl;i++){
			val = driver.findElement(By.xpath(busUserstblXpath+"/tbody/tr["+i+"]/td["+userNameCol+"]//div/span")).getText();
			if(userName.equals(val)){
				System.out.println("Drilling into user : "+userName);
				actionsClick(By.xpath(busUserstblXpath+"/tbody/tr["+i+"]/td[2]//div/input"));
				click(By.xpath("//button[contains(@id,'editButton')]//bdi[text()='Edit']"));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button//bdi[text()='Add']")));

				rowCountAssignedBusRolTbl = getRowCount(assignBusRolesTblXpath);
				System.out.println("rowCountAssignedBusRolTbl : "+rowCountAssignedBusRolTbl);
				colNumAssignBusRolTbl = getColumnNumber(assignBusRolesTblXpath, "Business Role ID");

				rolesToBeAdded = new ArrayList<String>(Arrays.asList(rolesArray));

				System.out.println("rolesToBeAdded before : "+rolesToBeAdded);

				//Removing the roles from ArrayList that are already added
				for(int p=0;p<rolesToBeAdded.size();p++){
					role = rolesToBeAdded.get(p);
					if("No data".equals(driver.findElement(By.xpath(assignBusRolesTblXpath+"/tbody/tr[1]/td[1]")).getText())){
						System.out.println("No data in Assigned Business Roles table");
						break;	
					}
					for(int n=1;n<=rowCountAssignedBusRolTbl;n++){
						System.out.println("n : "+n);
						value = driver.findElement(By.xpath(assignBusRolesTblXpath+"/tbody/tr["+n+"]/td["+colNumAssignBusRolTbl+"]/a")).getText();
						System.out.println("value : "+value);
						if(role.equalsIgnoreCase(value)){
							rolesToBeAdded.remove(role);
							break;
						}
					}
				}

				System.out.println("rolesToBeAdded : "+rolesToBeAdded);

				if(rolesToBeAdded.size()>0){
					click(By.xpath("//button//bdi[text()='Add']"));

					busRoleIDCol = getColumnNumber(busRolesTblXpath, "Business Role ID");

					for(int m=0;m<rolesToBeAdded.size();m++){
						role = rolesToBeAdded.get(m);
						clear(By.xpath("//input[@id='searchField.selectBusinessRolesTable-I']"));
						type(By.xpath("//input[@id='searchField.selectBusinessRolesTable-I']"),role);
						click(By.xpath("//div[@id='searchField.selectBusinessRolesTable-search']"));
						Thread.sleep(5000);

						rowCountbusRolesTbl = getRowCount(busRolesTblXpath);
						for(int k=1;k<=rowCountbusRolesTbl;k++){
							String cellVal = driver.findElement(By.xpath(busRolesTblXpath+"/tbody/tr["+k+"]/td["+busRoleIDCol+"]//span")).getText();
							if(role.equalsIgnoreCase(cellVal)){
								actionsClick(By.xpath(busRolesTblXpath+"/tbody/tr["+k+"]/td[2]//div/input"));
								click(By.xpath("//button[contains(@id,'button.AddBusinessUserAssignmentDialogApply')]//bdi[text()='Apply']"));
								break;
							}
						}

					}

					click(By.xpath("//button[contains(@id,'button.AddBusinessUserAssignmentDialogCancel')]//bdi[text()='Cancel']"));
					//click(By.xpath("//button[contains(@id,'saveButton')]//bdi[text()='Save']"));
					click(By.xpath("//button//bdi[text()='Save']"));
				}
				else
					System.out.println("No Roles to be added for user : "+userName);

				click(By.xpath("//a[@title='Back']"));
				rolesToBeAdded.clear();
				break;
			}
		}
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
			wait.until(ExpectedConditions.elementToBeClickable(locator));

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
		wait.withTimeout(Duration.ofSeconds(120))
		.ignoring(NoSuchElementException.class)
		.pollingEvery(Duration.ofSeconds(5))
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