package MassExecution_Maven.MassExecution_Maven;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class AddRoles_MassExec{

	public static String browser = null;
	public static String uRL = null;
	public static String userName = null;
	public static String password = null;
	public static String country = null;
	public static String release = null;
	public static String testPhase = null;

	public static String testPlanName = null;

	public static String driverPath = null;
	public static String excelFilePath = null;

	public static FluentWait<WebDriver> wait = null;
	private static int waitTime = 120;

	public static WebDriver driver = null;
	public static Actions actions = null;

	public static List<String> userNamesList = new ArrayList<String>();;
	public static Map<String,String> userNamesMap = new HashMap<String,String>();
	public static Map<String,String> configMap = new HashMap<String,String>();
	public static List<String> roles = new ArrayList<String>();
	public static List<String> columnNames = new ArrayList<String>();
	public static List<String> usersToBeAdded = new ArrayList<String>();
	public static List<String> usersAlreadyAvailable = new ArrayList<String>();

	public static String globalRole = "SAP_BR_BPC_EXPERT";

	public static void main(String[] args) throws Exception {

		excelFilePath = System.getProperty("user.home") + "//OneDrive - SAP SE//Desktop//AddRoles//AddRoles.xlsx";
		System.out.println("Excel Path : "+excelFilePath);
		readExcel(excelFilePath);
		initSettings();
		launchURL();
		loginApp();

		for(int i=0;i<userNamesList.size();i++)
			//searchAndAddRoles(userNamesList.get(i));
			searchAndAddUsers(userName);
	}

	public static void initSettings()throws Exception{	

		browser = configMap.get("Browser");
		uRL = configMap.get("URL");
		userName = configMap.get("UserName");
		password = configMap.get("Password");

		driverPath = System.getProperty("user.home")+"\\Desktop\\SeleniumFiles";
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

		type(By.xpath("//input[@id='USERNAME_FIELD-inner' or @id='j_username']"), userName);
		type(By.xpath("//input[@id='PASSWORD_FIELD-inner' or @id='j_password']"), password);
		click(By.xpath("//button[@id='LOGIN_LINK' or @id='logOnFormSubmit']"));

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[contains(@id,'btnBasicSearch-I')]")));

		for(int i=0;i<6;i++){
			if(driver.findElements(By.xpath("//input[contains(@id,'btnBasicSearch-I')]")).size()!=1)
				Thread.sleep(10000);
			else
				break;
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
			userNamesList.add(userName);
		}

		System.out.println("User Names : "+userNamesList);
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

	public static void searchAndAddUsers(String userName)throws Exception{

		try{
			
		
		String user = null;

		Thread.sleep(20000);
		System.out.println("Search and open role : "+globalRole);

		clear(By.xpath("//input[contains(@id,'btnBasicSearch-I')]"));
		type(By.xpath("//input[contains(@id,'btnBasicSearch-I')]"), globalRole);
		click(By.xpath("//button//bdi[text()='Go']"));

		Thread.sleep(10000);
		String busRolestblXpath  = "//table[contains(@id,'tblBusinessRoles-listUl')]";
		String assignBusUsersTblXpath = "//table[contains(@id,'BusinessUserAssignment-listUl')]";
		String addBusUsersTblXpath = "//table[@id='table.AddBusinessUserAssignment-listUl']";

		int rowCountaddBusUsersTbl = 0;
		int rowCountbusRolesTbl = getRowCount(busRolestblXpath);
		int busRoleIDCol = getColumnNumber(busRolestblXpath, "Business Role ID");
		System.out.println("rowCountbusRolesTbl : "+rowCountbusRolesTbl+" , busRoleIDCol : "+busRoleIDCol);

		String val = null, value = null;
		int rowCountAssignedBusUsersTbl = 0, colNumUserName = 0;

		for(int i=1;i<=rowCountbusRolesTbl;i++){

			val = driver.findElement(By.xpath(busRolestblXpath+"/tbody/tr["+i+"]/td["+busRoleIDCol+"]//div/span")).getText();

			if(globalRole.equals(val)){
				System.out.println("Drilling into Role : "+globalRole);
				actionsClick(By.xpath(busRolestblXpath+"/tbody/tr["+i+"]/td["+busRoleIDCol+"]//div/span"));

				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'button.Download')]//bdi[text()='Download']")));

				//Clicking on edit button only if it exists.
				if(driver.findElements(By.xpath("//button[contains(@id,'btnFooterMainAction-button')]//bdi[text()='Edit']")).size()==1)
					actionsClick(By.xpath("//button[contains(@id,'btnFooterMainAction-button')]//bdi[text()='Edit']"));

				wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'btnFooterMainAction-button')]//bdi[text()='Save']")));

				actionsClick(By.xpath("//div[contains(@id,'icontabfilter.brovbua')]"));
				Thread.sleep(10000);
				if(!(driver.findElements(By.xpath("//input[contains(@id,'BusinessUserAssignmentTable-I')]")).size()==1)){
					System.out.println("Clicking on Assigned Business Users again..");
					click(By.xpath("//div[contains(@id,'icontabfilter.brovbua')]"));
					Thread.sleep(10000);
				}

				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(assignBusUsersTblXpath)));

				String tempRowCount = driver.findElement(By.xpath("//span[contains(@id,'application-BusinessUserRole-maintain-component---object--icontabfilter.brovbua-count')]")).getText();
				rowCountAssignedBusUsersTbl = Integer.parseInt(tempRowCount);

				int rc = getRowCount(assignBusUsersTblXpath);

				colNumUserName = getColumnNumber(assignBusUsersTblXpath, "User Name");
				System.out.println("rowCountAssignedBusRolTbl : "+rowCountAssignedBusUsersTbl+" , colNumUserName : "+colNumUserName);

				tab();
				
				do{
					System.out.println("Scrolling down!!");
					scrollDown();
					rc = getRowCount(assignBusUsersTblXpath);
					System.out.println("rc : "+rc+" , rowCountAssignedBusRolTbl : "+rowCountAssignedBusUsersTbl);

				}while(rc!=rowCountAssignedBusUsersTbl);

				for(int j=1;j<=rowCountAssignedBusUsersTbl;j++){

					value = driver.findElement(By.xpath(assignBusUsersTblXpath+"/tbody/tr["+j+"]/td["+colNumUserName+"]//div/span")).getText();
					usersAlreadyAvailable.add(value);
				}

				System.out.println("usersAlreadyAvailable : "+usersAlreadyAvailable);

				boolean isAddBusUsersPopupOpen = false;

				for(int k=0;k<userNamesList.size();k++){

					user = userNamesList.get(k);
					if(usersAlreadyAvailable.contains(user)){
						System.out.println("User : "+user+" is already added to "+globalRole);
					}
					else
					{
						//Clicking on Add button only if the Add Business Users pop-up is not open
						if(!isAddBusUsersPopupOpen){
							System.out.println("Business Users pop-up is not open. Hence clicking on Add button!!!");
							click(By.xpath("//button[contains(@id,'BusinessUserAssignmentTableAdd')]//bdi[text()='Add']"));
							isAddBusUsersPopupOpen = true;
						}

						wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//input[@id='searchField.AddBusinessUserAssignmentTable-I']")));

						/*click(By.xpath("//button[contains(@id,'smartFilterBar.AddBusinessUserAssignmentDialog-btnGo')]//bdi[text()='Go']"));
						Thread.sleep(5000);
*/
						/*for(int m=1;m<15;m++){
							System.out.println("scrolling down - "+m);
							scrollDown();
						}
*/
						clear(By.xpath("//input[@id='searchField.AddBusinessUserAssignmentTable-I']"));
						type(By.xpath("//input[@id='searchField.AddBusinessUserAssignmentTable-I']"),user);
						click(By.xpath("//div[contains(@id,'searchField.AddBusinessUserAssignmentTable')]//div[@title='Search']"));
						Thread.sleep(10000);
						 
						wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(addBusUsersTblXpath)));

						colNumUserName = getColumnNumber(addBusUsersTblXpath, "User Name");

						rowCountaddBusUsersTbl = getRowCount(addBusUsersTblXpath);
						
						System.out.println("rowCountaddBusUsersTbl : "+rowCountaddBusUsersTbl);
						
						actionsClick(By.xpath(addBusUsersTblXpath+"/tbody/tr[1]/td[2]//div/input"));
						actionsClick(By.xpath("//button[contains(@id,'button.AddBusinessUserAssignmentDialogApply')]//bdi[text()='Apply']"));
						Thread.sleep(10000);
						System.out.println("User : "+user+" is added to "+globalRole);
						
						
						
						/*for(int j=1;j<=rowCountaddBusUsersTbl;j++){
							String cellVal = driver.findElement(By.xpath(addBusUsersTblXpath+"/tbody/tr["+j+"]/td["+colNumUserName+"]//div//span")).getText();
							if(user.equalsIgnoreCase(cellVal)){

								actionsClick(By.xpath(addBusUsersTblXpath+"/tbody/tr["+j+"]/td[2]//div/input"));
								click(By.xpath("//button[contains(@id,'button.AddBusinessUserAssignmentDialogApply')]//bdi[text()='Apply']"));
								System.out.println("User : "+user+" is added to "+globalRole);
								break;
							}
						}
*/					}
				}
				
				actionsClick(By.xpath("//button[@id='button.AddBusinessUserAssignmentDialogOK']//bdi[text()='OK']"));
				Thread.sleep(10000);
				actionsClick(By.xpath("//span[contains(@id,'btnFooterMainAction-button-content')]//bdi[text()='Save']"));
			}
		}
		}
		catch(Exception e){
			System.err.println(e);
			
			if(driver.findElements(By.xpath("//button[@id='button.AddBusinessUserAssignmentDialogOK']//bdi[text()='OK']")).size()>0)
				actionsClick(By.xpath("//button[@id='button.AddBusinessUserAssignmentDialogOK']//bdi[text()='OK']"));
			
			Thread.sleep(10000);
			
			actionsClick(By.xpath("//span[contains(@id,'btnFooterMainAction-button-content')]//bdi[text()='Save']"));
			
		}
	}

	/*public static void searchAndAddRoles(String userName)throws Exception{

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

				usersToBeAdded = new ArrayList<String>(Arrays.asList(rolesArray));

				System.out.println("rolesToBeAdded before : "+usersToBeAdded);

				//Removing the roles from ArrayList that are already added
				for(int p=0;p<usersToBeAdded.size();p++){
					role = usersToBeAdded.get(p);
					if("No data".equals(driver.findElement(By.xpath(assignBusRolesTblXpath+"/tbody/tr[1]/td[1]")).getText())){
						System.out.println("No data in Assigned Business Roles table");
						break;	
					}
					for(int n=1;n<=rowCountAssignedBusRolTbl;n++){
						System.out.println("n : "+n);
						value = driver.findElement(By.xpath(assignBusRolesTblXpath+"/tbody/tr["+n+"]/td["+colNumAssignBusRolTbl+"]/a")).getText();
						System.out.println("value : "+value);
						if(role.equalsIgnoreCase(value)){
							usersToBeAdded.remove(role);
							break;
						}
					}
				}

				System.out.println("rolesToBeAdded : "+usersToBeAdded);

				if(usersToBeAdded.size()>0){
					click(By.xpath("//button//bdi[text()='Add']"));

					busRoleIDCol = getColumnNumber(busRolesTblXpath, "Business Role ID");

					for(int m=0;m<usersToBeAdded.size();m++){
						role = usersToBeAdded.get(m);
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
				usersToBeAdded.clear();
				break;
			}
		}
	}
	 */
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

	public static void scrollDown()throws Exception{
		new Actions(driver).sendKeys(Keys.PAGE_DOWN).perform();
	}

	public static void tab()throws Exception{
		new Actions(driver).sendKeys(Keys.TAB).perform();
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