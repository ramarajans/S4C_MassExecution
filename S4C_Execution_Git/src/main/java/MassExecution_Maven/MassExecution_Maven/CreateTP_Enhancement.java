package MassExecution_Maven.MassExecution_Maven;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CreateTP_Enhancement{

	public static String excelFilePath = null, configFilePath = null;
	public static List<String> scopeItems = new ArrayList<String>();
	public static String testPlanName = null;
	public static List<String> columnNames = new ArrayList<String>();

	public static List<String> processedScopeItems = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {

		excelFilePath = System.getProperty("user.home") + "//Desktop//CreateAndRunTestPlans//ScopeItems0.xlsx";
		configFilePath = System.getProperty("user.home") + "//Desktop//CreateAndRunTestPlans//ScopeItems.xlsx";
		System.out.println("Excel Path : "+excelFilePath);
		readExcel(excelFilePath,configFilePath);
		GenericUtils.initSettings();
		GenericUtils.launchURL();
		GenericUtils.loginApp();

		for(int i=0;i<scopeItems.size();i++){

			//Format : TC_1GA or 1902_TestRun_SAT_1GA_DE
			if("Yes".equals(GenericUtils.genericTPName))
				testPlanName = "TC_F3_"+scopeItems.get(i);
			else
				testPlanName = GenericUtils.release+"_"+GenericUtils.uniqueString+"_"+GenericUtils.testPhase+"_"+scopeItems.get(i)+"_"+GenericUtils.country;

			try{
			//executeTestPlan_Popup(testPlanName, scopeItems.get(i));
			executeTestPlan(testPlanName, scopeItems.get(i));
			Thread.sleep(30000);

			//wait between every 10 test plans
			if(i>0 && i%10==0){
				System.out.println("Waiting for few minutes!!!");
				Thread.sleep(120*1000);
			}
			}finally{
				
				System.out.println("Inside Finally");
				writeExcel(excelFilePath , processedScopeItems);
			}
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

		//To get the Configurations

		ips = new FileInputStream(new File(configFilePath)); 
		workbook = new XSSFWorkbook(ips);

		sh = workbook.getSheet("Configuration");

		row = sh.getRow(1);
		cell = row.getCell(0);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		value = cell.getStringCellValue();

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

			GenericUtils.configMap.put(key, value);
		}

		System.out.println("Config Map : "+GenericUtils.configMap);

		ips.close();
	}

	//To create a Test Plan
	public static String createTestPlan(String testPlanName, String scopeItem)throws Exception{

		System.out.println("Inside createTestPlan");

		System.out.println("Test Plan to be created is : "+testPlanName);
		Thread.sleep(15000);
		GenericUtils.wait("//button[@title='Add test plan']/span/span");	
		GenericUtils.click(By.xpath("//button[@title='Add test plan']/span/span"));
		GenericUtils.wait("//input[contains(@id,'TestplanName-inner')]");
		GenericUtils.type(By.xpath("//input[contains(@id,'TestplanName-inner')]") , testPlanName);
		GenericUtils.type(By.xpath("//form[contains(@id,'searchId-F')]/input[contains(@id,'searchId-I')]") , scopeItem);
		//GenericUtils.click(By.id("searchId-search"));
		GenericUtils.click(By.xpath("//form[contains(@id,'searchId-F')]//div[@title='Search']"));

		int colNumberType = 0, colNumberScopeItem = 0;

		colNumberType = GenericUtils.getColumnNumber("//table[contains(@id,'addScenarioTable-listUl')]", "Type");
		colNumberScopeItem = GenericUtils.getColumnNumber("//table[contains(@id,'addScenarioTable-listUl')]", "Scope Item");

		if(colNumberScopeItem==0)
			colNumberScopeItem = GenericUtils.getColumnNumber("//table[contains(@id,'addScenarioTable-listUl')]", "Name");

		System.out.println("colNumberType : "+colNumberType+" , colNumberScopeItem : "+colNumberScopeItem);

		int rowCount = GenericUtils.driver.findElements(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr")).size();
		System.out.println("rowCount : "+rowCount);

		String cellValueType = null, cellValueScopeItem = null;

		boolean stdProcessFound = false;

		for(int g=1;g<=rowCount;g++){

			if("No data".equals(GenericUtils.driver.findElement(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr[1]/td[1]")).getText())){
				System.out.println(GenericUtils.driver.findElement(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr[1]/td[1]")).getText());
			}
			else{

				cellValueType = GenericUtils.driver.findElement(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr["+g+"]/td["+colNumberType+"]/span")).getText();
				cellValueScopeItem = GenericUtils.driver.findElement(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr["+g+"]/td["+colNumberScopeItem+"]/span")).getText();

				System.out.println("cellValueType : "+cellValueType+" , cellValueScopeItem : "+cellValueScopeItem);

				if(scopeItem.equalsIgnoreCase(cellValueScopeItem) && "Standard".equals(cellValueType)){
					stdProcessFound = true;
					GenericUtils.actionsClick(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr["+g+"]/td[2]/div/div/input"));
					//click(By.xpath("//table[contains(@id,'addScenarioTable-listUl')]/tbody/tr["+g+"]/td[2]/div/div/input"));
					GenericUtils.click(By.xpath("//button//bdi[text()='Save']"));

					GenericUtils.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1//span[contains(@id,'titleText-inner')]")));

					String testPlanNameHeader = GenericUtils.driver.findElement(By.xpath("//h1//span[contains(@id,'titleText-inner')]")).getText();

					for(int i=0;i<5;i++){

						testPlanNameHeader = GenericUtils.driver.findElement(By.xpath("//h1//span[contains(@id,'titleText-inner')]")).getText();

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

			GenericUtils.click(By.xpath("//button//bdi[text()='Cancel']"));
			GenericUtils.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@id,'mbox')]//bdi[text()='Yes']")));
			GenericUtils.click(By.xpath("//button[contains(@id,'mbox')]//bdi[text()='Yes']"));

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

			GenericUtils.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'runBtn') or contains(@id,'stopBtn')]//bdi[contains(text(),'Execute') or text()='Stop Test']")));

			//Executing the created Test Plan
			if(GenericUtils.driver.findElements(By.xpath("//button[contains(@id,'stopBtn')]//bdi[text()='Stop Test']")).size()==1){
				System.out.println("Test Plan : "+tPName+ " is already In Process");
			}
			else if(GenericUtils.driver.findElements(By.xpath("//button[contains(@id,'runBtn')]//bdi[contains(text(),'Execute')]")).size()==1){
				System.out.println("Executing the test plan");
				GenericUtils.actionsClick(By.xpath("//button[contains(@id,'runBtn')]//bdi[contains(text(),'Execute')]"));

				GenericUtils.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@id,'popover')]//button//bdi[text()='Execute with variants']")));
				//GenericUtils.actionsClick(By.xpath("//div[contains(@id,'popover')]//button//bdi[text()='Execute with variants']"));
				GenericUtils.click(By.xpath("//div[contains(@id,'popover')]//button//bdi[text()='Execute with variants']"));

				GenericUtils.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(selectVariantTbl)));

				colNumVarName= GenericUtils.getColumnNumber(selectVariantTbl, "Variant Name");
				varTblRowCount = GenericUtils.driver.findElements(By.xpath(selectVariantTbl+"/tbody/tr")).size();

				for(int j=1;j<=varTblRowCount;j++){
					String cellVal = GenericUtils.driver.findElement(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td["+colNumVarName+"]/span")).getText();

					if("DEFAULT_VARIANT".equals(cellVal)){
						System.out.println("Selecting Default variant");
						Thread.sleep(10000);
						//GenericUtils.actionsClick(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]"));

						//WebElement element = GenericUtils.driver.findElement(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]"));
						//((JavascriptExecutor) GenericUtils.driver).executeScript("arguments[0].click();", element);

						//GenericUtils.driver.findElement(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]")).sendKeys(Keys.SPACE);
						//GenericUtils.click(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]"));
						//new WebDriverWait(GenericUtils.driver, 20).until(ExpectedConditions.elementToBeClickable(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]"))).click();

						new WebDriverWait(GenericUtils.driver, 20).until(ExpectedConditions.elementToBeClickable(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/div"))).click();

						//driver.findElement(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]")).click();
						Thread.sleep(2000);
						break;
					}
				}

				GenericUtils.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'execvar--execWithVar')]//bdi[contains(text(),'Execute')]")));

				//GenericUtils.actionsClick(By.xpath("//button[contains(@id,'execvar--execWithVar')]//bdi[contains(text(),'Execute')]"));
				GenericUtils.click(By.xpath("//button[contains(@id,'execvar--execWithVar')]//bdi[contains(text(),'Execute')]"));

				Thread.sleep(10000);

				int m=0;
				
				while(m<60){
					Thread.sleep(2000);
					if(GenericUtils.driver.findElements(By.xpath("//span[text()='Pre-Check Results']")).size()==1){
						GenericUtils.click(By.xpath("//button//bdi[text()='Close']"));
						System.out.println("Role validation popup displayed. Hence, Test Plan : "+tPName+ " did not get triggered. Please check!!!");
						break;
					}
					else if(GenericUtils.driver.findElements(By.xpath("//button[contains(@id,'stopBtn')]//bdi[text()='Stop Test']")).size()==1){
						System.out.println("Test Plan : "+tPName+ " triggered for execution");
						break;
					}
				}
			}
			else
				System.out.println("Execute or Re-Execute Button is not found");
			
			processedScopeItems.add(tPName);
		}
	}

	@SuppressWarnings("deprecation")
	public static void writeExcel(String excelFilePath, List<String> createdList) throws IOException {

		String value = null;

		FileInputStream ips = new FileInputStream(new File(excelFilePath)); 
		XSSFWorkbook workbook = new XSSFWorkbook(ips);
		
		Sheet sh = workbook.getSheet("ScopeItems");
		Row row = sh.getRow(0);

		Cell cell = null;

		int rowCount = sh.getLastRowNum() - sh.getFirstRowNum();
		System.out.println("Excel rowCount : "+rowCount);

		//To search for the scope item row and add cell color for the completed cells
		for(int i=0;i<scopeItems.size();i++){
			for(int j=0;j<rowCount;j++){
				row = sh.getRow(j+1);
				cell = row.getCell(columnNames.indexOf("Scope Items"));
				cell.setCellType(Cell.CELL_TYPE_STRING);
				value = cell.getStringCellValue();
				
				if(createdList.contains(value)){
					CellStyle style = workbook.createCellStyle();
		            // Setting Background color  
		            style.setFillBackgroundColor(IndexedColors.GREEN.getIndex());  
				}
			}
		}

		//Writing in excel workbook
		FileOutputStream out = new FileOutputStream(new File(excelFilePath));
		workbook.write(out);
		out.close();

		ips.close();

	}

	
	//Searches for a test plan. Returns true if the test plan is found. Else False.
	public static boolean searchTestPlan(String testPlan)throws Exception{

		boolean testPlanFound = false;

		Thread.sleep(20000);
		GenericUtils.wait("//form[@class='sapMSFF']/input");
		Thread.sleep(4000);
		GenericUtils.clear(By.xpath("//form[@class='sapMSFF']/input"));
		GenericUtils.type(By.xpath("//form[@class='sapMSFF']/input"), testPlan);
		GenericUtils.driver.findElement(By.xpath("//form[@class='sapMSFF']/input")).sendKeys(Keys.TAB);
		GenericUtils.click(By.xpath("//form[@class='sapMSFF']/div[contains(@id,'searchField-search')]"));
		Thread.sleep(3000);
		GenericUtils.click(By.xpath("//form[@class='sapMSFF']/div[contains(@id,'searchField-search')]"));
		Thread.sleep(5000);

		int tpSize = GenericUtils.driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li")).size();

		if(GenericUtils.driver.findElements(By.xpath("//ul[contains(@id,'list-listUl')]/li[1]//div[text()='No data']")).size()==1){
			System.out.println("Test Plan : "+testPlan+" is not found");
		}
		else
		{
			System.out.println("No. of rows after search : "+tpSize);

			String value = null, titleUI = null;
			int i=0;

			for(i=0;i<tpSize;i++){
				value = GenericUtils.driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(i+1)+"]//span[contains(@id,'titleText')]/span")).getText();

				if(testPlan.equals(value)){
					System.out.println("Selecting row : "+value);
					Thread.sleep(5000);
					GenericUtils.click(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(i+1)+"]/div"));
					Thread.sleep(10000);
					titleUI = GenericUtils.driver.findElement(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(i+1)+"]/div")).getText();
					System.out.println("titleUI : "+titleUI);
					for(int j=0;j<5;j++){
						Thread.sleep(5000);
						if(!(value.equals(GenericUtils.driver.findElement(By.xpath("//h1//span[contains(@id,'titleText-inner')]")).getText())))
							GenericUtils.click(By.xpath("//ul[contains(@id,'list-listUl')]/li["+(i+1)+"]/div"));
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

	public static void executeTestPlan_Popup(String tPName, String scopeItem)throws Exception{

		String testPlan = null;
		String selectVariantTbl = "//table[contains(@id,'procedureListEditTable-listUl')]";
		boolean tpFound = searchTestPlan(tPName);
		int colNumVarName = 0, varTblRowCount = 0;
		int m=0;
		boolean rolePopup = false , tPexecuted = false;

		if(tpFound){
			System.out.println("Test Plan : "+tPName+" already exists");
			testPlan = tPName;
		}
		else
			testPlan = createTestPlan(tPName, scopeItem);

		Thread.sleep(8000);

		if(testPlan!=null){

			GenericUtils.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'runBtn') or contains(@id,'stopBtn')]//bdi[contains(text(),'Execute') or text()='Stop Test']")));

			//Executing the created Test Plan
			if(GenericUtils.driver.findElements(By.xpath("//button[contains(@id,'stopBtn')]//bdi[text()='Stop Test']")).size()==1){
				System.out.println("Test Plan : "+tPName+ " is already In Process");
			}
			else if(GenericUtils.driver.findElements(By.xpath("//button[contains(@id,'runBtn')]//bdi[contains(text(),'Execute')]")).size()==1){
				System.out.println("Executing the test plan");
				GenericUtils.actionsClick(By.xpath("//button[contains(@id,'runBtn')]//bdi[contains(text(),'Execute')]"));

				GenericUtils.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@id,'popover')]//button//bdi[text()='Execute with variants']")));
				GenericUtils.actionsClick(By.xpath("//div[contains(@id,'popover')]//button//bdi[text()='Execute with variants']"));

				GenericUtils.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(selectVariantTbl)));

				colNumVarName= GenericUtils.getColumnNumber(selectVariantTbl, "Variant Name");
				varTblRowCount = GenericUtils.driver.findElements(By.xpath(selectVariantTbl+"/tbody/tr")).size();

				for(int j=1;j<=varTblRowCount;j++){
					String cellVal = GenericUtils.driver.findElement(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td["+colNumVarName+"]/span")).getText();

					if("DEFAULT_VARIANT".equals(cellVal)){
						GenericUtils.actionsClick(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]"));
						//driver.findElement(By.xpath(selectVariantTbl+"/tbody/tr["+j+"]/td[2]//div/input[contains(@id,'selectMulti-CB')]")).click();
						Thread.sleep(2000);
						break;
					}
				}

				GenericUtils.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@id,'execvar--execWithVar')]//bdi[contains(text(),'Execute')]")));

				GenericUtils.actionsClick(By.xpath("//button[contains(@id,'execvar--execWithVar')]//bdi[contains(text(),'Execute')]"));


				Thread.sleep(10000);

				if(GenericUtils.driver.findElements(By.xpath("//button[contains(@id,'stopBtn')]//bdi[text()='Stop Test']")).size()==1){
					tPexecuted = true;
					System.out.println("Test Plan : "+tPName+ " triggered for execution");
				}
				else{
					while(m<60){
						Thread.sleep(1000);

						if(GenericUtils.driver.findElements(By.xpath("//button[contains(@id,'stopBtn')]//bdi[text()='Stop Test']")).size()==1){
							tPexecuted = true;
							System.out.println("Test Plan : "+tPName+ " triggered for execution");
							break;
						}
						else if(GenericUtils.driver.findElements(By.xpath("//span[text()='Pre-Check Results']")).size()==1){
							GenericUtils.click(By.xpath("//button//bdi[text()='Close']"));
							System.out.println("Role validation popup is displayed. Please check!!");
							rolePopup = true;
							break;
						}
						else
							System.out.println("Page is loading!!!");
					}
				}

				if(rolePopup)
					System.out.println("Role validation popup displayed. Hence, Test Plan : "+tPName+ " did not get triggered");
				else
					System.out.println("Test Plan : "+tPName+ " triggered for execution");
			}
			else
				System.out.println("Execute or Re-Execute Button is not found");
		}
	}

}