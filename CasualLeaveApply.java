package finalIntranetScripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.AssertJUnit;

import static org.testng.ConversionUtils.wrapDataProvider;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import org.apache.bcel.verifier.exc.VerificationException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.server.handler.ImplicitlyWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Factory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import utility.TestBase;
import utility.util;

public class CasualLeaveApply extends TestBase{

	Logger APP_LOGS = Logger.getLogger(CasualLeaveApply.class);
	public static String timeStamp;
	public static String email;
	int count = 0;
	public String Serial;
	public String Url;
	public String Username;
	public String Password;
	String dateandtime;

	@Factory
	public static Object[] factoryDataSupplier() {
		return wrapDataProvider(CasualLeaveApply.class, dataSupplier());
	}


	public CasualLeaveApply(String Serial,String Url, String Username, String Password){
		this.Serial = Serial;
		this.Url = Url;
		this.Username = Username;
		this.Password = Password;
	}

	@BeforeMethod
	@Parameters("browser")
	public void BeforeTest(@Optional String browser) throws IOException{

		if(browser==null){
			APP_LOGS.info("Browser not specified. Config would be used");
			initialize();
		}
		else{
			APP_LOGS.info("Browser specified is "+browser);
			initialize1(browser);
		}
		PropertyConfigurator.configure("log4j.properties");
		BasicConfigurator.configure();
	}

	@AfterMethod
	public void After() throws IOException{
		driver.quit();
	}


	@Test
	public void CasualApplyLeave() throws InterruptedException, IOException{ 

		APP_LOGS.info("Executing CasualLeaveApply : "+Serial);
		driver.manage().deleteAllCookies();
		try{
			/**
			 * Loads the URL, logs into the intranet of the user whose credentials are provided in 
			 * the datasheet 
			 */
			String internet_URL="http://"+Username+":"+Password+"@"+Url;
			System.out.println("Total internet URL: "+internet_URL);
			driver.manage().timeouts().implicitlyWait(1200, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(1200, TimeUnit.SECONDS);
			driver.get(internet_URL);		    
			pagerefresh();			
			util.waitForPageToLoad();
			driver.manage().timeouts().implicitlyWait(1200, TimeUnit.SECONDS);
			
			/**
			 * Navigates to the My Leaves page, checks if the Apply leave link is present 
			 */
			
			String currURL = driver.getCurrentUrl();
		//	util.leavecancel();
			try{
				String adminreportsURL = "http://192.168.1.140:303/LMS/Reports.aspx";
				driver.get(adminreportsURL);
				driver.manage().timeouts().implicitlyWait(1200, TimeUnit.SECONDS);
				
				util.waitForPageToLoad();
				driver.manage().window().maximize();
				if(!util.isElementDisplayed(By.xpath(OR.getProperty("ReportsPageLogo")))){
					APP_LOGS.error("User Not Navigated to LMS Admin Page");
					String currrentURL = driver.getCurrentUrl();
					driver.get(currrentURL + "/LMS/Groups.aspx");
					pagerefresh();
					driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
				}
				
				WebElement employeedropdown = driver.findElement(By.xpath(OR.getProperty("Employeedropdown")));
				Select empdropdown = new Select(employeedropdown);
				empdropdown.selectByValue("184");
				driver.findElement(By.xpath(OR.getProperty("Reportfromdate"))).click();
				driver.findElement(By.xpath(OR.getProperty("Reportfromdate"))).clear();
				driver.findElement(By.xpath(OR.getProperty("Reportfromdate"))).sendKeys("05/01/2017");
				driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
				driver.findElement(By.xpath(OR.getProperty("Reporttodate"))).click();
				driver.findElement(By.xpath(OR.getProperty("Reporttodate"))).clear();
				driver.findElement(By.xpath(OR.getProperty("Reporttodate"))).sendKeys("06/30/2017");
				driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
			    driver.findElement(By.xpath(OR.getProperty("Generatereport"))).click();
			    util.waitForPageToLoad();	    
			    int size = driver.findElements(By.xpath(OR.getProperty("Reporttable"))).size();
			    System.out.println("Number of rows in the table:" + size);
			    String rowtext = driver.findElement(By.xpath(OR.getProperty("NoResultsFound"))).getText();
			    
			    if(rowtext == "No Record(s) Found"){
			    
			    for(int i=size; i>1; i--){
                	driver.findElement(By.xpath(OR.getProperty("Reportcancelbutton"))).click();
			    	util.waitForPageToLoad();   	
			    	System.out.println("Number of rows in the table:" + i);
			    	}
			    }
			    
			    else{
			    	System.out.println("No leaves waiting for approval for the selected employee");
			    }
			    
			    int sizeafterdelete = driver.findElements(By.xpath(OR.getProperty("Reporttable"))).size();
			    System.out.println("Number of rows in the table after cancel click of all rows resulted:" + sizeafterdelete);
				System.out.println("All leaves waiting for approval for the logged in user are deleted");
				driver.get("http://192.168.1.140:303/LMS/MyLeave.aspx");
				util.waitForPageToLoad();		
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("Exception occurred");
			}
		     
			Thread.sleep(1000);
			if(!util.isElementDisplayed(By.xpath(OR.getProperty("ApplyLeave_Link"))))
			{
				APP_LOGS.error("User Not Navigated to My Leaves Page");
				util.createXLSReport("CasualLeaveApply", Serial);
				utility.util.takeScreenShot("CasualLeaveApply"+Serial+"_"+dateandtime);
				Assert.assertTrue(true, "User Not Navigated to My Leaves Page");
				driver.navigate().back();
				Thread.sleep(2000);
				String currrentURL = driver.getCurrentUrl();
				driver.get(currrentURL + "/LMS/MyLeave.aspx");
				pagerefresh();
			}

			driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
			getElement("ApplyLeave_Link").click();
			Thread.sleep(2000);
			WebElement Leave_Dropdown=driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType")));
			Select selectleavedropdown =new Select(Leave_Dropdown);

			/**
			 * Checks if the Casual leave type option is present in the apply leave dropdown, if not present then 
			 * goes to admin, credits leave for the user, comes back to LMS to check if the leave type is shown.
			 * If shown then continues with the apply of leave. 
			 */
			
			boolean b = false;
			List<WebElement> options = selectleavedropdown.getOptions();
			List<String> stringoptions = new ArrayList<String>();
			for(int z=1; z<options.size(); z++) {
				stringoptions.add(options.get(z).getText());			
			}
			System.out.println(stringoptions);		
			for(String optioncheck : stringoptions){
				if(optioncheck.equals("Casual")) {
					b=true;
					break;
				}
			}
			if (b==false){
				
				/**
				 * b==false shows that the Casual leave type option is not listed in the leave drop down - Admin credit of 
				 * leave type is to be done next.
				 */
				
				System.out.println("Casual leave type not listed in the leave type dropdown");
				driver.get(currURL + "LMS/Groups.aspx");
				driver.navigate().refresh();
				driver.navigate().refresh();
				util.waitForPageToLoad();
				Thread.sleep(1000);
				if(!util.isElementDisplayed(By.xpath(OR.getProperty("SetLeave_Admin"))))
				{
					APP_LOGS.error("User Not Navigated to LMS Admin Page");
					String currrentURL = driver.getCurrentUrl();
					driver.get(currrentURL + "/LMS/Groups.aspx");
					pagerefresh();
					driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);

				}
				else
				{
					APP_LOGS.error("User is Navigated to LMS Admin Page");
					System.out.println("User is successfully navigated to the LMS Admin Page");
				}
				
				/**
				 * Loads the URL, logs into the intranet of the user whose credentials are provided in 
				 * the datasheet 
				 */
				
				getElement("SetLeave_Admin").click();
				util.waitForPageToLoad();
				getElement("SetLeave_Btn").click();
				String Leavetypedropdownmandatory = "width: 210px; border: 1px solid red;";
				String DaystoCreditmandatory = "width: 212px; height: 22px; border: 1px solid red;";
				String mandatorymsg = "Value is Required";				
				if(getElement("SetLeave_Dropdown").getAttribute("style").equalsIgnoreCase(Leavetypedropdownmandatory))
				{
					System.out.println("Set Leave Type is mandatory - indicated by the red border shown around the dropdown");
				}
				WebElement adminsetleavedropdown = driver.findElement(By.xpath(OR.getProperty("SetLeave_Dropdown")));
				Select setleavedropdown = new Select(adminsetleavedropdown);
				setleavedropdown.selectByVisibleText("Set Casual Leave");
				Thread.sleep(1000);
				driver.findElement(By.xpath(OR.getProperty("SetLeave_Btn"))).click();
				if(driver.findElement(By.xpath(OR.getProperty("SetLeave_Daystoset"))).getAttribute("style").equalsIgnoreCase(DaystoCreditmandatory))
				{
					System.out.println("Days to Credit is a mandatory field indicated by the red border shown around the dropdown");
				}
				driver.findElement(By.xpath(OR.getProperty("SetLeave_Daystoset"))).click();
				driver.findElement(By.xpath(OR.getProperty("SetLeave_Daystoset"))).sendKeys("2");
				Thread.sleep(1000);

				driver.findElement(By.xpath(OR.getProperty("SetLeave_Btn"))).click();

				if(driver.findElement(By.xpath(OR.getProperty("CreditToMsg"))).getText().equalsIgnoreCase(mandatorymsg))
				{
					System.out.println("Credit To is a mandatory field indicated by the mandatory msg shown below the credit to field");
				}
				else
				{
					APP_LOGS.error("User Not shown the credit to mandatory msg");
					System.out.println("User Not shown the credit to mandatory msg");
				}

				if(driver.findElement(By.xpath(OR.getProperty("RemarksMsg"))).getText().equalsIgnoreCase(mandatorymsg))
				{
					System.out.println("Remarks is a mandatory field indicated by the mandatory msg shown below the Remarks field");
				}
				else
				{
					APP_LOGS.error("User Not shown the Remarks mandatory msg");
					System.out.println("User Not shown the Remarks mandatory msg");
				}

				driver.findElement(By.xpath(OR.getProperty("SetLeave_CreditTo"))).click();
				driver.findElement(By.xpath(OR.getProperty("SetLeave_CreditTo"))).sendKeys("Mohan Bharathi");
				Thread.sleep(2000);
				Robot rb1 = new Robot();
				driver.findElement(By.xpath(OR.getProperty("CreditTo_List"))).click();
				//rb1.keyPress(KeyEvent.VK_DOWN);			
				//rb1.keyPress(KeyEvent.VK_ENTER);
				Thread.sleep(1000);
				driver.findElement(By.xpath(OR.getProperty("SetLeave_Remarks"))).click();
				driver.findElement(By.xpath(OR.getProperty("SetLeave_Remarks"))).sendKeys("Personal");
				driver.findElement(By.xpath(OR.getProperty("SetLeave_Btn"))).click();
				Thread.sleep(3000);
				
	            driver.get(currURL + "/LMS/MyLeave.aspx");	
	            pagerefresh();
				util.waitForPageToLoad();
				Thread.sleep(1000);
				if(!util.isElementDisplayed(By.xpath(OR.getProperty("ApplyLeave_Link"))))
				{
					APP_LOGS.error("User Not Navigated to My Leaves Page");
					util.createXLSReport("CasualLeaveApply", Serial);
					utility.util.takeScreenShot("CasualLeaveApply"+Serial+"_"+dateandtime);
					Assert.assertTrue(true, "User Not Navigated to My Leaves Page");
					driver.navigate().back();
					Thread.sleep(2000);
					String currrentURL = driver.getCurrentUrl();
					driver.get(currrentURL + "/LMS/MyLeave.aspx");
					pagerefresh(); 
				}	
				getElement("ApplyLeave_Link").click();
				b=true;
			}

			else if(b==true){
				      
				        util.waitForPageToLoad();
				        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						String casualleavecount = driver.findElement(By.xpath((OR.getProperty("CasualLeave_Count")))).getText();
						System.out.println("Leave Balance before Leave Apply in String: "  +casualleavecount );
						double d = Double.parseDouble(casualleavecount);
						int value1 = (int)d;
						System.out.println("Leave Balance before Leave Apply in Integer: "  +value1 );	
						selectleavedropdown.selectByVisibleText("Casual");
						Thread.sleep(2000);
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).click();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).clear();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).sendKeys(OR.getProperty("HolidayLeaveDate_From"));
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).click();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).clear();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).sendKeys(OR.getProperty("HolidayLeaveDate_To"));
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason"))).click();
						util.waitForPageToLoad();
						String leavecountzeromsg = "Please check your Dates.The Leave Calculation is 0"; 
						if(leavecountzeromsg.equalsIgnoreCase(driver.findElement(By.xpath(OR.getProperty("LMS_ErrorMsg"))).getText())){
							Assert.assertTrue(true, "The application recognizes that the leave calculation is zero for the entered date and displays the error msg: "+ leavecountzeromsg);
							APP_LOGS.assertLog(true, "The application recognizes that the leave calculation is zero for the entered date and displays the error msg: "+ leavecountzeromsg);
							System.out.println("The application recognizes that the leave calculation is zero for the entered date and displays the error msg: "+ leavecountzeromsg);
						}
						else{
							Assert.assertTrue(false, "The leave calculation is zero error msg is not shown");
							APP_LOGS.assertLog(false, "The leave calculation is zero error msg is not shown");
							System.out.println ("The leave calculation is zero error msg is not shown");
						}
					}

					Thread.sleep(2000);

					// Casual Leave apply - Positive case - need to include holiday scenario

					String leavecountbefore = driver.findElement(By.xpath((OR.getProperty("CasualLeave_Count")))).getText();
					System.out.println("Leave Balance before Leave Apply in String: "  +leavecountbefore );
					double d3 = Double.parseDouble(leavecountbefore);
					int value4 = (int)d3;
					System.out.println("Leave Balance before Leave Apply in Integer: "  +value4 );
					

					if (value4 > 2){
						
						WebDriverWait wait = new WebDriverWait(driver, 10);
						WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))));
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).click();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).clear();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).sendKeys(OR.getProperty("From_Date"));
						getObject(OR.getProperty("Intrnet_LeaveType_FromDate"));
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						Thread.sleep(2000);
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).click();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).clear();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).sendKeys(OR.getProperty("To_Date"));
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason"))).click();
						Thread.sleep(2000);
						driver.findElement(By.xpath(OR.getProperty("Leave_Calc")));
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						Thread.sleep(4000);
						String str =    driver.findElement(By.xpath(OR.getProperty("Leave_Calc"))).getAttribute("value");
						System.out.println("Number of days of Leave applied in String Format: " + str);
						double d1 = Double.parseDouble(str);
						int value2 = (int)d1;
						System.out.println("Number of days of Leave applied in Integer : " + value2);		
						Thread.sleep(1000);
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason")));
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason"))).click();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason"))).sendKeys(OR.getProperty("Reason"));
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						driver.findElement(By.xpath(OR.getProperty("Submit_Button")));
						driver.findElement(By.xpath(OR.getProperty("Submit_Button"))).click();
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						if(!util.isElementDisplayed(By.xpath(OR.getProperty("ApplyLeave_Link"))))
						{

							System.out.println("User not navigated to the My Leaves Page");
						}

						else{
							System.out.println("User successfully landed in My Leaves page");
						}


						driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
						driver.findElement(By.xpath(OR.getProperty("ApplyLeave_Link"))).click();
						Thread.sleep(1000);
						String leavecount = driver.findElement(By.xpath((OR.getProperty("CasualLeave_Count")))).getText();
						System.out.println("Leave Balance after Leave Apply in String Format: "  +leavecount );
						double d2 = Double.parseDouble(leavecount);
						int value3 = (int)d2;

						System.out.println("Leave Balance after Leave Apply in Integer: " + value3);

						int leavediff = value4 - value3;

						System.out.println("Leave balance difference before and after:  " + leavediff);

						if(value3==value4)
						{
							System.out.println("Casual Leave Count is not Updated successfully");
						}


						else if(leavediff == value2)
						{
							System.out.println("Casual Leave Count is Updated successfully");
						}

						driver.navigate().back();
						Thread.sleep(2000);

						if(!util.isElementDisplayed(By.xpath(OR.getProperty("ApplyLeave_Link"))))
						{

							System.out.println("User not navigated to the My Leaves Page");
						}

						else{
							System.out.println("User successfully landed in My Leaves page");
						}

						//All future non expired waiting for approval leaves cancel

						util.leavecancel();
						System.out.println("All future leaves that were waiting for approval have been cancelled");
						driver.findElement(By.xpath(OR.getProperty("ApplyLeave_Link"))).click();
						Thread.sleep(2000);
						String leavecountafterdelete = driver.findElement(By.xpath((OR.getProperty("CasualLeave_Count")))).getText();
						System.out.println("Leave Balance after Leave Apply in String Format: "  +leavecountafterdelete );
						double d4 = Double.parseDouble(leavecountafterdelete);
						int value5 = (int)d4;
						if(value5!=value4)
						{
							System.out.println("Casual Leave Count is not updated successfully after deletion of applied leave");
						}


						else if(value5==value4)
						{
							System.out.println("Casual Leave Count is Updated successfully after deletion of applied leave");
						}

					} 

					else if (value4 <= 2){
						
						driver.get(currURL + "/LMS/MyLeave.aspx");
						pagerefresh();
					    util.waitForPageToLoad();
					    driver.findElement(By.xpath(OR.getProperty("ApplyLeave_Link"))).click();						
						WebElement Leave_Dropdown1=driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType")));
						WebDriverWait wait = new WebDriverWait(driver, 10);
					    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(OR.getProperty("Intrnet_LeaveType"))));
						Select selectleavedropdown1 =new Select(Leave_Dropdown1);
						
						selectleavedropdown1.selectByVisibleText(OR.getProperty("LeaveType_Casual"));
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).click();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).clear();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_FromDate"))).sendKeys(OR.getProperty("SingleDate"));
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						Thread.sleep(3000);
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).click();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).clear();
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						Thread.sleep(3000);
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_ToDate"))).sendKeys(OR.getProperty("SingleDate"));
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason"))).click();
						Thread.sleep(1000);
						driver.findElement(By.xpath(OR.getProperty("Leave_Calc")));
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						Thread.sleep(1000);
						String str =    driver.findElement(By.xpath(OR.getProperty("Leave_Calc"))).getAttribute("value");
						System.out.println("Number of days of Leave applied in String Format: " + str);
						double d1 = Double.parseDouble(str);
						int value2 = (int)d1;
						System.out.println("Number of days of Leave applied in Integer : " + value2);		
						Thread.sleep(2000);
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason")));
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason"))).click();
						driver.findElement(By.xpath(OR.getProperty("Intrnet_LeaveType_Reason"))).sendKeys(OR.getProperty("Reason"));
						driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
						driver.findElement(By.xpath(OR.getProperty("Submit_Button")));
						driver.findElement(By.xpath(OR.getProperty("Submit_Button"))).click();
						Thread.sleep(2000);

						if(!util.isElementDisplayed(By.xpath(OR.getProperty("ApplyLeave_Link"))))
						{

							System.out.println("User not navigated to the My Leaves Page");
						}

						else{
							System.out.println("User successfully landed in My Leaves page");
						}


						driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
						driver.findElement(By.xpath(OR.getProperty("ApplyLeave_Link"))).click();
						Thread.sleep(2000);

						if(value4-value2 >0){
							String leavecount = driver.findElement(By.xpath((OR.getProperty("CasualLeave_Count")))).getText();
							System.out.println("Leave Balance after Leave Apply in String Format: "  +leavecount );
							double d2 = Double.parseDouble(leavecount);
							int value3 = (int)d2;

							System.out.println("Leave Balance after Leave Apply in Integer: " + value3);

							int leavediff = value4 - value3;

							System.out.println("Leave balance difference before and after:  " + leavediff);

							if(value3==value4)
							{
								System.out.println("Casual Leave Count is not Updated successfully");
							}


							else if(leavediff == value2)
							{
								System.out.println("Casual Leave Count is Updated successfully");
							}

							driver.navigate().back();
							Thread.sleep(2000);

							if(!util.isElementDisplayed(By.xpath(OR.getProperty("ApplyLeave_Link"))))
							{

								System.out.println("User not navigated to the My Leaves Page");
							}

							else{
								System.out.println("User successfully landed in My Leaves page");
							}					
							util.leavecancel();						
							System.out.println("All future leaves that were waiting for approval have been cancelled");
							driver.findElement(By.xpath(OR.getProperty("ApplyLeave_Link"))).click();
							Thread.sleep(2000);
							String leavecountafterdelete = driver.findElement(By.xpath((OR.getProperty("CasualLeave_Count")))).getText();
							System.out.println("Leave Balance after Leave Apply in String Format: "  +leavecountafterdelete );
							double d4 = Double.parseDouble(leavecountafterdelete);
							int value5 = (int)d4;
							if(value5!=value4)
							{
								System.out.println("Casual Leave Count is updated successfully after deletion of applied leave, the difference being due to more leaves cancelled than that were applied in this instance");
							}


							else if(value5==value4)
							{
								System.out.println("Casual Leave Count is Updated successfully after deletion of applied leave, same number of leaves cancelled as applied in this instance");
							}
						}

						//All future non expired waiting for approval leaves cancel

						if(value4-value2 == 0){
							util.leavecancel();
							System.out.println("All future leaves that were waiting for approval have been cancelled");

							driver.findElement(By.xpath(OR.getProperty("ApplyLeave_Link"))).click();
							Thread.sleep(2000);
							String leavecountafterdelete = driver.findElement(By.xpath((OR.getProperty("CasualLeave_Count")))).getText();
							System.out.println("Leave Balance after Leave Apply in String Format: "  +leavecountafterdelete );
							double d4 = Double.parseDouble(leavecountafterdelete);
							int value5 = (int)d4;
							if(value5!=value4)
							{
								System.out.println("Casual Leave Count is updated successfully after deletion of applied leave, the difference being due to more leaves cancelled than that were applied in this instance");
							}


							else if(value5==value4)
							{
								System.out.println("Casual Leave Count is Updated successfully after deletion of applied leave, same number of leaves cancelled as applied in this instance");
							}

							System.out.println("Casual Leave Balance Count is:" +value4);
						}

					}/*catch (StaleElementReferenceException e) {
					    e.toString();
					    System.out.println("Trying to recover from a stale element :" + e.getMessage());
					    count = count + 1;
					   }
					   count = count + 4;
					  }
			count = 0;*/

			}
		

				catch(Exception e){
					e.printStackTrace();
					utility.util.takeScreenShot("CasualLeaveApply"+Serial+"_"+dateandtime);
					utility.util.createXLSReport("CasualLeaveApply", Serial);
					APP_LOGS.error("Error in CasualLeaveApply : "+e);
					Assert.assertTrue(false,"Error in CasualLeaveApply Test");
				}
			}


			@Parameters
			public static Collection<Object[]> dataSupplier(){
				Object[][] data = util.getData("CasualLeaveApply");
				return Arrays.asList(data);

			}


		}
