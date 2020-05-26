package MassExecution_Maven.MassExecution_Maven;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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


public class Test_1A0 {
//
	public static String browser = null;
	public static String uRL = "https://cc2-390.wdf.sap.corp/ui?sap-theme=sap_belize&help-stateUACP=PRODUCTION#RequestForQuotation-manage";
	public static String userName = "PURCHASER";
	public static String password = "Welcome1!";

	public static String uRLAribaSourcing = "http://test-s4.sourcing-eu.ariba.com";
	public static String userNameAribaSourcing = "S4TEST";
	public static String passwordAribaSourcing = "Welcome2020@";

	public static String driverPath = null;

	public static FluentWait<WebDriver> wait = null;
	public static WebDriver driver = null;

	public static String objCreateRFQ = "//button[contains(@id,'C_RequestForQuotationEnhWD--createWithDocType')]//span[contains(@id,'C_RequestForQuotationEnhWD--createWithDocType-img')]";
	public static String objRFQType = "//button//bdi[text()='Ext. Sourcing Req.']";
	public static String objRFQDescription =  "//input[contains(@id,'RequestForQuotationName::Field-input-inner')]";
	public static String objQuoteDeadline =  "//input[contains(@id,'QuotationLatestSubmissionDate::Field-datePicker-inner')]";
	public static String objPurchasingGroup =  "//input[contains(@id,'C_RequestForQuotationEnhWD--Organization::PurchasingGroup::Field-input-inner')]";
	public static String objPurchasingOrg =  "//input[contains(@id,'Organization::PurchasingOrganization::Field-input-inner')]";
	public static String objCompanyCode =  "//input[contains(@id,'Organization::CompanyCode::Field-input-inner')]";

	public static String objCurrency =  "//input[contains(@id,'Values::DocumentCurrency::Field-input-inner')]";

	public static String objTabItems =  "//button[contains(@id,'Items::Section-anchor')]//bdi[text()='Items']";

	public static String objCreateItem =  "//button[contains(@id,'Items::addEntry')]//bdi[text()='Create']";
	public static String objItemsTbl =  "//table[contains(@id, 'Items::responsiveTable-listUl')]";

	public static String objMaterial =  "//input[contains(@id,'BasicData::Material::Field-input-inner')]";
	public static String objPlant =  "//input[contains(@id,'BasicData::Plant::Field-input-inner')]";
	public static String objRequestedQuantity =  "//input[contains(@id,'ItemDetail::ScheduleLineOrderQuantity::Field-input-inner')]";
	public static String objRequestedQuantityUoM =  "//input[contains(@id,'ItemDetail::ScheduleLineOrderQuantity::Field-sfEdit-input-inner')]";
	public static String objDeliveryDate =  "//input[contains(@id,'ItemDetail::ScheduleLineDeliveryDate::Field-datePicker-inner')]";
	public static String objInfoRecordUpdate =  "//div[contains(@id,'BasicData::IsInfoRecordUpdated::Field-cBoxBool-CbBg')]";


	public static String objDelAddrName =  "//input[contains(@id,'fullNameSmartField-input-inner')]";
	public static String objDelAddrHouseNum =  "//input[contains(@id,'houseNumberSmartField-input-inner')]";
	public static String objDelAddrStreet =  "//input[contains(@id,'streetNameSmartField-input-inner')]";
	public static String objDelAddrCity =  "//input[contains(@id,'cityNameSmartField-input-inner')]";
	public static String objDelAddrRegion =  "//input[contains(@id,'regionSmartField-input-inner')]";
	public static String objDelAddrPostalCode =  "//input[contains(@id,'postalCodeSmartField-input-inner')]";
	public static String objDelAddrPhone =  "//input[contains(@id,'phoneNumberSmartField-input-inner')]";
	public static String objDelAddrEmail =  "//input[contains(@id,'emailAddressSmartField-input-inner')]";
	public static String objDelAddrCountry =  "//input[contains(@id,'countrySmartField-input-inner')]";
	public static String objApplyButton =  "//button//bdi[text()='Apply']";

	public static String objTabBidders =  "//button[contains(@id,'Bidders::Section-anchor')]//bdi[text()='Bidders']";

	public static String objCreateBidders =  "//button[contains(@id,'Bidders::addEntry')]//bdi[text()='Create']";
	public static String objBiddersTbl =  "//table[contains(@id, 'Bidders::responsiveTable-listUl')]";
	public static String objSupplier =  "//input[contains(@id, 'BasicData::Supplier::Field-input-inner')]";

	public static String objBiddersApplyButton =  "//button[contains(@id,'C_RFQBidderEnhWD--footerObjectPageBackTo')]//bdi[text()='Apply']";

	public static String objSaveButton =  "//button[contains(@id,'C_RequestForQuotationEnhWD--activate')]//bdi[text()='Save']";
	public static String objCloseButton =  "//button[contains(@id,'RequestForQuotation-manage-component-appContent--Close')]//bdi[text()='Close']";

	public static String objRFQNumber =  "//span[contains(@id,'C_RequestForQuotationEnhWD--objectPageHeader-subtitle')]";
	public static String objPublishButton =  "//button[contains(@id,'Submit_for_approval')]//bdi[text()='Publish']";

	public static String quoteDesc = null;
	public static String quoteDeadline = "today+5";
	//public static String quoteDeadline = "04/03/2020";//"today+5";04/03/2020
	public static String purchasingGroup = "001";
	public static String purchasingOrg = "1710";
	public static String plantOrCompanyCode = "1710";
	public static String currency = "USD";
	public static String material = "TG0011";

	public static String name = "Rob";
	public static String houseNum = "24";
	public static String street = "Down";
	public static String city = "Cal";
	public static String region = "AA";
	public static String postalCode = "12345";
	public static String phone = "1234567";
	public static String email = "test@sap.com";
	public static String country = "US";
	public static String deliveryDate = "today+30";
	//public static String deliveryDate = "04/26/2020";//"today+30";
	public static String supplier = "17300080";

	public static String rFQNumber = null;

	public static String objSourcingTab =  "//span[@class='w-tabitem-a']//a[text()='Sourcing']";
	public static String objSourcingSearchBox =  "//input[contains(@value,'Search')]";
	public static String objSourcingSearchIcon =  "//button[contains(@id,'rwb')]";
	public static String objSourcingSearchFilterSearchBtn =  "//button[contains(@id,'opc')]/span[contains(text(),'Search')]";

	public static String objSourcingProjSeachLink =  "//a[@title='"+quoteDesc+" - "+rFQNumber+"']";
	//public static String objSearchWithin =  "//a[contains(text(),'Search within')]";
	public static String objPopupDeskFileSyncChkBox = 	"//div[@class='w-chk-container']";
	public static String objPopupDeskFileSyncIgnoreBtn = 	"//button/span[contains(text(),'Ignore')]";
	public static String objSRTabTasks = "//span/a[text()='Tasks']";
	public static String objTasksPrepareSRLink = "//span//a[contains(text(),'Prepare Sourcing Request')]";
	public static String objTasksPrepareSRViewTaskDetailsLink = "//a[@id='_y$el$d']/b[text()='View Task Details']";
	public static String objTasksPrepareSRMarkComplete = "//button/span[contains(text(),'Mark Complete')]";
	//public static String objTasksPrepareSRMarkComplete = "//button/span[contains(text(),'Mark Complete')]";

	public static String objTasksApprovalforSRLink = "//span//a[contains(text(),'Approval for Sourcing Request')]";
	public static String objTasksApprovalforSRViewTaskDetailsLink = "//a[@id='_lwpaid']/b[text()='View Task Details']";
	public static String objTasksApprovalforSRFixedDateChkBox = "//input[@id='_mp$hzc']";
	public static String objTasksApprovalforSRFixedDate = "//input[@id='DF_bsmebb']";//03/26/2020
	public static String objTasksApprovalforSRSubmit = "//button/span[contains(text(),'Submit')]";

	public static String objSRTabDocuments = "//span/a[text()='Documents']";
	public static String objDocumentsSourcingProjectLink = "//span//a[contains(text(),'Sourcing Project') and @id='_fk6ch']";
	public static String objDocumentsSourcingProjectOpenLink = "//a/b[contains(text(),'Open')]";
	public static String objCreateSourcProjName = "//input[@value='Sourcing Project']"; //_vur53b

	public static String objCreateSourcProjQuickProject = "//div//input[@id='_dpagid']";
	public static String objCreateSourcProjEventType = "//span[@id='text__ibpgm']";
	public static String objCreateSourcProjEventTypeRFP = "//div[contains(text(),'RFP')]";
	public static String objCreateSourcProjTestProjectYes = "//div//input[@id='_w_s7nd']";
	public static String objCreateSourcProjReqforProposal = "//input[@id='_irdeed']";
	public static String objCreateSourcProjCreateBtn = "//button/span[contains(text(),'Create')]";	

	public static String tasksApprovalforSRFixedDate = "03/23/2020";
	public static String projName = "Proj"+rFQNumber;


	public static void main(String[] args) throws Exception {

		/*initSettings();
		loginApp(uRL, userName, password);
		createSupplierQuotation();
		logoutApp();
		quitBrowser();
		 */
		/*	rFQNumber = "7081002354";
		quoteDesc = "RFQ230320220313";
		projName = "Proj"+rFQNumber;

		objSourcingProjSeachLink =  "//a[@title='"+quoteDesc+" - "+rFQNumber+"']";
		 */		initSettings();
		 loginApp("https://oc7-715.wdf.sap.corp/ui?help-stateUACP=PRODUCTION#RequestForQuotation-manage", "S4C_AUTOMATION", "Welcome1!");
		 readPDF();
		 //createSourcingProject();
	}


	public static void readPDF()throws Exception{
		try{

			Thread.sleep(5000);
			click(By.xpath("//span[contains(@id,'ListReportTable:::ColumnListItem-__clone0-imgNav')]"));
			Thread.sleep(5000);
			click(By.xpath("//span/bdi[text()='Output Details']"));
			Thread.sleep(10000);
			click(By.xpath("//button[@title='Display Document']"));

			Set<String> windows = driver.getWindowHandles();
			driver.switchTo().window(windows.toArray()[1].toString());
			Thread.sleep(10000);
			String pdf_URL = driver.getCurrentUrl();
			System.out.println("pdf_URL : "+pdf_URL);
			// Authentication code for current URL
			URL url = null;
			url = new URL(pdf_URL);
			System.out.println("url : "+url);
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("S4C_AUTOMATION", "Welcome1!".toCharArray());
				}
			});
			 
			Thread.sleep(15000);

			// read the data from PDF
			BufferedInputStream fileToParse = new BufferedInputStream(url.openStream());
			PDDocument document = null;
			document = PDDocument.load(fileToParse);
			String output = new PDFTextStripper().getText(document);
			System.out.println("output : "+output);
			//Do the checking
			driver.switchTo().window(windows.toArray()[0].toString());
			WebElement rfq_number = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("s2p.mm.pur.rfq.maintains1::sap.suite.ui.generic.template.ObjectPage.view.Details::C_RequestForQuotationEnhWD--objectPageHeader-subtitle")));

			if(output.contains(rfq_number.getText())) 
				System.out.println("PDF verified successfully");
			else 
				throw new Exception("In-Correct PDF.");


		}catch(Exception exception){
			exception.printStackTrace();
		}
	}

	public static void createSourcingProject()throws Exception{

		if(elementExists(objSourcingTab))
		{
			wait(objSourcingTab);
			click(By.xpath(objSourcingTab));
		}

		wait(objSourcingSearchBox);
		type(By.xpath(objSourcingSearchBox) , rFQNumber);
		click(By.xpath(objSourcingSearchIcon));
		wait(objSourcingSearchFilterSearchBtn);
		click(By.xpath(objSourcingSearchFilterSearchBtn));

		wait(objSourcingProjSeachLink);
		click(By.xpath(objSourcingProjSeachLink));

		if(elementExists(objPopupDeskFileSyncChkBox)){
			click(By.xpath(objPopupDeskFileSyncChkBox));
			click(By.xpath(objPopupDeskFileSyncIgnoreBtn));
		}

		/*click(By.xpath(objSRTabTasks));
		wait(objTasksApprovalforSRLink);
		click(By.xpath(objTasksApprovalforSRLink));
		wait(objTasksApprovalforSRViewTaskDetailsLink);
		click(By.xpath(objTasksApprovalforSRViewTaskDetailsLink));

		if(!(driver.findElement(By.xpath(objTasksApprovalforSRFixedDateChkBox)).getAttribute("checked value")==null))
			click(By.xpath(objTasksApprovalforSRFixedDateChkBox));

		type(By.xpath(objTasksApprovalforSRFixedDate) , tasksApprovalforSRFixedDate);
		click(By.xpath(objTasksApprovalforSRSubmit));
		 */
		wait(objSRTabDocuments);
		click(By.xpath(objSRTabDocuments));
		wait(objDocumentsSourcingProjectLink);
		click(By.xpath(objDocumentsSourcingProjectLink));
		wait(objDocumentsSourcingProjectOpenLink);
		click(By.xpath(objDocumentsSourcingProjectOpenLink));
		wait(objCreateSourcProjName);
		type(By.xpath(objCreateSourcProjName) , projName);

		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(objCreateSourcProjQuickProject)));

		//click(By.xpath(objCreateSourcProjQuickProject));
		//click(By.xpath(objCreateSourcProjEventType));
		//wait(objCreateSourcProjEventTypeRFP);
		//click(By.xpath(objCreateSourcProjEventTypeRFP));
		//click(By.xpath(objCreateSourcProjTestProjectYes));
		//click(By.xpath(objCreateSourcProjReqforProposal));
		//click(By.xpath(objCreateSourcProjCreateBtn));

		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(objCreateSourcProjEventType)));	
		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(objCreateSourcProjEventTypeRFP)));	

		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(objCreateSourcProjTestProjectYes)));
		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(objCreateSourcProjReqforProposal)));
		executor.executeScript("arguments[0].click();", driver.findElement(By.xpath(objCreateSourcProjCreateBtn)));

		wait("//span[contains(text(),'"+projName+"')]");

	}
	public static String createSupplierQuotation()throws Exception{

		System.out.println("Manage RFQ app launched!!");
		Thread.sleep(20000);
		wait(objCreateRFQ);	
		click(By.xpath(objCreateRFQ));
		wait(objRFQType);
		click(By.xpath(objRFQType));

		quoteDesc = "RFQ"+getCurrentTimeStamp();

		wait(objRFQDescription);
		type(By.xpath(objRFQDescription) , quoteDesc);
		quoteDesc = getValue(objRFQDescription);
		System.out.println("quoteDesc : "+quoteDesc);

		String dateFormat = getDateFormat(objQuoteDeadline);
		String quoateDeadlineDate = generateDate(dateFormat, quoteDeadline);
		type(By.xpath(objQuoteDeadline) , quoateDeadlineDate);

		type(By.xpath(objPurchasingGroup) , purchasingGroup);
		type(By.xpath(objPurchasingOrg) , purchasingOrg);
		type(By.xpath(objCompanyCode) , plantOrCompanyCode);
		keyPress(By.xpath(objCompanyCode), Keys.ENTER);

		wait(objCurrency);
		type(By.xpath(objCurrency) , currency);
		keyPress(By.xpath(objCurrency), Keys.ENTER);

		click(By.xpath(objTabItems));
		wait(objCreateItem);
		click(By.xpath(objCreateItem));

		int colCountItemsTbl = getColumnCount(objItemsTbl);

		click(By.xpath(objItemsTbl+"/tbody/tr[1]/td["+(colCountItemsTbl-1)+"]/span"));

		wait(objMaterial);
		type(By.xpath(objMaterial) , material);
		keyPress(By.xpath(objMaterial), Keys.ENTER);

		keyPress(By.xpath(objPlant), Keys.TAB);
		type(By.xpath(objPlant) , plantOrCompanyCode);
		keyPress(By.xpath(objPlant), Keys.ENTER);

		keyPress(By.xpath(objDeliveryDate), Keys.TAB);
		dateFormat = getDateFormat(objDeliveryDate);
		System.out.println("dateFormat : "+dateFormat);
		String delDate = generateDate(dateFormat, deliveryDate);
		type(By.xpath(objDeliveryDate) , delDate);
		keyPress(By.xpath(objDeliveryDate) , Keys.ENTER);

		clear(By.xpath(objRequestedQuantity));
		type(By.xpath(objRequestedQuantity) , "20,000");
		clear(By.xpath(objRequestedQuantityUoM));
		type(By.xpath(objRequestedQuantityUoM) , "PC");


		Thread.sleep(5000);
		new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(By.xpath(objInfoRecordUpdate))).click();

		if("".equals(getValue(objDelAddrName)))
			type(By.xpath(objDelAddrName) , name);

		if("".equals(getValue(objDelAddrHouseNum)))
			type(By.xpath(objDelAddrHouseNum) , houseNum);

		if("".equals(getValue(objDelAddrStreet)))
			type(By.xpath(objDelAddrStreet) , street);

		if("".equals(getValue(objDelAddrCity)))
			type(By.xpath(objDelAddrCity) , city);

		/*if("".equals(getValue(objDelAddrRegion))){
			type(By.xpath(objDelAddrRegion) , region);
			keyPress(By.xpath(objDelAddrRegion), Keys.ENTER);
		}
		 */
		if("".equals(getValue(objDelAddrPostalCode)))
			type(By.xpath(objDelAddrPostalCode) , postalCode);

		if("".equals(getValue(objDelAddrPhone)))
			type(By.xpath(objDelAddrPhone) , phone);

		if("".equals(getValue(objDelAddrEmail)))
			type(By.xpath(objDelAddrEmail) , email);

		clear(By.xpath(objDelAddrCountry));
		type(By.xpath(objDelAddrCountry) , country);
		keyPress(By.xpath(objDelAddrCountry), Keys.ENTER);

		click(By.xpath(objApplyButton));

		wait(objTabBidders);
		click(By.xpath(objTabBidders));
		wait(objCreateBidders);
		click(By.xpath(objCreateBidders));

		int colCountBiddersTbl = getColumnCount(objBiddersTbl);

		click(By.xpath(objBiddersTbl+"/tbody/tr[1]/td["+(colCountBiddersTbl-1)+"]/span"));

		wait(objSupplier);
		type(By.xpath(objSupplier) , supplier);
		keyPress(By.xpath(objSupplier), Keys.ENTER);

		click(By.xpath(objBiddersApplyButton));

		wait(objSaveButton);
		click(By.xpath(objSaveButton));


		if(driver.findElements(By.xpath(objCloseButton)).size()==1){
			click(By.xpath(objCloseButton));
			System.out.println("Close button clicked");
		}

		rFQNumber = getText(objRFQNumber);
		System.out.println("RFQ created : "+rFQNumber);

		click(By.xpath(objPublishButton));

		return rFQNumber;
	}


	public static void initSettings()throws Exception{	

		browser = "chrome";

		driverPath = System.getProperty("user.home")+"\\Desktop\\SeleniumFiles";
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

	/*public static void launchURL(String url)throws Exception{

		driver.get(url);
		Thread.sleep(5000);
		driver.manage().window().maximize();
	}
	 */
	public static void loginApp(String url, String userName, String password)throws Exception{

		String objUserName = "//input[@id='USERNAME_FIELD-inner' or @id='j_username' or @name='UserName']";
		String objPassword = "//input[@id='PASSWORD_FIELD-inner' or @id='j_password'  or @name='Password']";
		String objLoginBtn = "//*[@id='LOGIN_LINK' or @id='logOnFormSubmit' or contains(@title,'Login')]";
		driver.get(url);
		Thread.sleep(5000);
		driver.manage().window().maximize();

		wait(objUserName);
		type(By.xpath(objUserName), userName);
		type(By.xpath(objPassword), password);
		click(By.xpath(objLoginBtn));
		Thread.sleep(30000);

		//Authentication Information pop-up
		if(driver.findElements(By.xpath("//button//bdi[text()='Close']")).size()==1){
			System.out.println("Authentication pop-up is displayed.Closing it");
			click(By.xpath("//button//bdi[text()='Close']"));
		}

	}

	public static void logoutApp()throws Exception{

		wait("//a[@id='meAreaHeaderButton']");
		click(By.xpath("//a[@id='meAreaHeaderButton']"));
		click(By.xpath("//div[text()='Sign Out']"));
		wait("//span[text()='Sign Out']");
		click(By.xpath("//button//bdi[text()='OK']"));
	}

	public static void quitBrowser()throws Exception{

		driver.quit();
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
		int colCount = driver.findElements(By.xpath(tblXpath+"/thead/tr/th")).size();
		List<String> columnNames = new ArrayList<String>();

		for(int i=0;i<colCount;i++)
			columnNames.add(driver.findElement(By.xpath(tblXpath+"/thead/tr/th["+(i+1)+"]")).getText());

		colNum = columnNames.indexOf(colName);
		colNum = colNum+1;

		System.out.println("Column Number : "+colNum);
		return colNum;
	}

	public static int getColumnCount(String tblXpath)throws Exception{

		int colCount = driver.findElements(By.xpath(tblXpath+"/thead/tr/th")).size();
		return colCount;
	}
	public static void keyPress(By locator, Keys key)throws Exception{

		wait.until(ExpectedConditions.presenceOfElementLocated(locator));
		driver.findElement(locator).sendKeys(key);
		Thread.sleep(2000);
	}

	public static String getText(String xpath)throws Exception{

		String text = driver.findElement(By.xpath(xpath)).getText();
		return text;
	}

	public static String getValue(String xpath)throws Exception{

		String text = driver.findElement(By.xpath(xpath)).getAttribute("value");
		return text;
	}

	public static String getDateFormat(String xpath)throws Exception{

		String dateFormat = driver.findElement(By.xpath(xpath)).getAttribute("placeholder");
		return dateFormat;
	}

	public static boolean elementExists(String xpath)throws Exception{

		boolean elementPresent = false;

		if(driver.findElements(By.xpath(xpath)).size()>=1)
			elementPresent = true;

		return elementPresent;
	}

	public static String generateDate(String dateFormat, String value)throws Exception{

		Calendar c = null;
		DateFormat df = new SimpleDateFormat(dateFormat);//"ddMMyyHHMMss"

		String days = null;

		if("today".equals(value)){
			c = Calendar.getInstance();
			c.setTime(new Date());
		}
		else if(value.contains("+")){
			days = value.split("\\+")[1];
			c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DATE, +Integer.parseInt(days));	
		}
		else if(value.contains("")){
			days = value.split("\\-")[1];
			c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DATE, -Integer.parseInt(days));
		}

		String timeStamp = df.format(c.getTime());
		System.out.println("timeStamp : "+timeStamp);
		return timeStamp;
	}

	public static String getCurrentTimeStamp()throws Exception{

		DateFormat df = new SimpleDateFormat("ddMMyyHHMMss");
		Date date = new Date();

		String timeStamp = df.format(date);

		return timeStamp;
	}

}
