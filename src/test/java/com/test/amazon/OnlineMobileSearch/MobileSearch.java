package com.test.amazon.OnlineMobileSearch;

import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class MobileSearch {

	//global variables
	public static WebDriver driver; 
	private String baseUrl;
	private String browser;
	private String search;
	private String optionToSelect;
	private WebElement sortList;
	private XSSFWorkbook workBook;
	
	@BeforeSuite
	//This method is used to get inputs from Excel and assign the value to the global variable
	public void setExcelFile() {
		try {
			
			FileInputStream input=new FileInputStream(".\\excel\\inputs_for_miniproject.xlsx");
			workBook=new XSSFWorkbook(input);
			XSSFSheet sheet=workBook.getSheet("inputParameters");
			XSSFRow rowOne=sheet.getRow(1);
			browser=rowOne.getCell(0).toString(); //to get browser as input
			baseUrl=rowOne.getCell(1).toString(); //to get URL as input
			search =rowOne.getCell(2).toString(); //to get product to be search in textbox as input
			optionToSelect=rowOne.getCell(3).toString(); // to get option to be selected in sortby listbox as input
			
		}catch(Exception e) {
			System.out.println(e); 
		}
	}
	
	@BeforeTest
	//The method is used to set the driver property for chrome and firefox
	public void createDriver() {
	  try {
			
		//code to execute using firefox browser
		if(browser.equals("firefox")) {
			DesiredCapabilities capabilities=DesiredCapabilities.firefox();
			capabilities.setCapability("marionette", true );
			FirefoxOptions option=new FirefoxOptions();
			option.addArguments("--disable-notifications"); //to block notifications
			option.addArguments("--disable-infobars"); // to block infobars
			System.setProperty("webdriver.gecko.driver", ".\\driver\\geckodriver.exe");
			driver=new FirefoxDriver(option);	
			driver.navigate().to(baseUrl);
			
		}
		//code to execute using chrome browser 
		else if(browser.equals("chrome")) {
			
			ChromeOptions option=new ChromeOptions();
			option.addArguments("--disable-notifications"); //to block notifications
			option.addArguments("--disable-infobars"); // to block infobars
			System.setProperty("webdriver.chrome.driver", ".\\driver\\chromedriver.exe");
			driver=new ChromeDriver(option);
			driver.navigate().to(baseUrl);
			driver.manage().window().maximize(); 
			
		}
	  }catch(Exception e) {
		  System.out.println(e); 
	  }
	}
	
	@Test(priority=0)
	//This method is used to search mobile based on given 'search' input in the textbox and search for it
	public void searchMobile() {
		try {
			
			WebElement condition=driver.findElement(By.id("twotabsearchtextbox"));
			condition.sendKeys(search);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			driver.findElement(By.id("nav-search-submit-button")).click();
			
		}catch(Exception e) {
			
			System.out.println(e); 
		}
	}
	
	@Test(priority=1)
	//This method is used to validate search string and displays the string in the console
	public void validateSearch() {
		try {
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			WebElement validate=driver.findElement(By.xpath("//h1//div/span"));
			String validateString=validate.getText();
			String page=validateString.substring(0,4);
			String items=validateString.substring(13,18);
			System.out.println("As per search result- number of pages ("+page+") and number of items ("+items+")");
			
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	@Test(priority=2)
	//This method is used to select sort by list option
	public void sortByList() {
		try {
			//wait for the sort by list option present
			WebDriverWait dElement = new WebDriverWait(driver,20);
			dElement.until(ExpectedConditions.elementToBeClickable(By.id("a-autoid-0-announce")));
			
			//click the sort by list option
			WebElement sortClick=driver.findElement(By.id("a-autoid-0-announce"));
			sortClick.click();
			
			// wait for all the list option to be display
			dElement.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ul[@role='listbox']")));
			
		}catch(Exception e) {
			System.out.println(e); 	
		}
	}
	
	@Test(priority=3)
	//This method is used to select Newest Arrival option and check the option is selected
	public void selectNewArrived() {
		
		try {	
			
			Actions selectSort=new Actions(driver);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			//Get all the options in the sort by list
			List<WebElement> list=driver.findElements(By.xpath("//ul[@role='listbox']/li/a"));
			
			//Iterate the list to check and get Newest Arrival option in sort by list
			for(int i=0;i<list.size();i++) {
				String listText=list.get(i).getText();
				if(listText.equalsIgnoreCase(optionToSelect)) {
					sortList=list.get(i);
					break;
				}
			}
			
			//if Newest Arrival Option is present, the option should be clicked
			if(sortList!=null) {
				selectSort.moveToElement(sortList).build().perform();
			}
			
			//To check Newest Arrival option is selected
			String sortListText=sortList.getText();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			System.out.println();
			Assert.assertEquals(sortListText, optionToSelect,"The Newest Arrivals option is not selected");
			if(sortListText.equalsIgnoreCase(optionToSelect)) {
				System.out.println("TEST CASE PASSED: The Newest Arrivals option is selected"); 
			}else {
				System.out.println("TEST CASE FAILED: The Newest Arrivals option is not selected"); 
			}
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
		}catch(Exception e) {
			System.out.println(e); 
		}
	}
	
	@AfterTest
	//This method is used to close the browser and the driver
	public void closeBrowser(){
		// closes the driver
		try {
			driver.quit();
		}catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	@AfterSuite
	public void closeExcel() {
		try {
			workBook.close();
		}catch(Exception e) {
			System.out.println(e); 
		}
	}
}
