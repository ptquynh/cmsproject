package org.selenium.cms.test;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.selenium.cms.common.UnitBase;
import org.selenium.cms.common.Utils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CMS_TestScripts extends UnitBase{
	
	public final By ELEMENT_EMAIL=By.xpath(".//*[@name='identification']");
	public final By ELEMENT_PASS=By.xpath(".//*[@name='password']");
	public final By ELEMENT_SIGNIN_BTN=By.xpath(".//*[@type='submit']");
	
	public final By ELEMENT_NEWPOST_BTN=By.cssSelector(".icon-pen");
	public final By ELEMENT_POST_SETTING_BTN = By.xpath(".//*[@class='post-settings']");
	public final By ELEMENT_POST_SETTING_UPLOAD = By.xpath(".//*[@class='upload-form']//label");
	public final By ELEMENT_POST_SETTING_CLOSE=By.xpath(".//*[@id='entry-controls']//button[contains(@class,'close')]");
	public final By ELEMENT_POST_TITLE=By.xpath(".//*[@id='entry-title']");
	public final By ELEMENT_POST_CONTENT=By.xpath(".//*[contains(@class,'ember-text-area')]");
	public final By ELEMENT_ACTIONS_LIST=By.xpath(".//button[contains(@class,'ember-view closed')]");
	public final By ELEMENT_ACTIONS_LIST_PUBLISH_LINK=By.xpath(".//*[contains(@class,'post-save-publish') and .//*[text()='Publish Now']]");
	public final By ELEMENT_ACTIONS_LIST_DELETE_LINK=By.xpath(".//*[text()='Delete Post']");
	public final By ELEMENT_PUBLISH_BTN=By.xpath(".//button[contains(@class,'ember-view') and @type='button']");
	
	public final By ELEMENT_DELETE_CONFIRM_BTN=By.xpath(".//*[contains(@class,'btn-red')]");
	public final By ELEMENT_CONTENT_LINK=By.xpath(".//i[@class='icon-content']");
	public final By ELEMENT_EDIT_POST_BTN=By.xpath(".//i[@class='icon-edit']");
	
	public final By ELEMENT_UPDATE_POST_BTN = By.xpath(".//*[contains(@class,'js-publish-button')]");
	public final By ELEMENT_POST_FIRST_PUBLISH=By.xpath("(.//time[contains(@class,'published')])[1]");
	
	public final By ELEMENT_POST_FIRST_PUBLISH_TITLE=By.xpath("(.//time[contains(@class,'published')])[1]/../../..//*[@class='entry-title']");
	public final String ELEMENT_POST_TITLE_TEXT=".//h3[@class='entry-title'][text()='$title']";
	public final String ELEMENT_POST_PUBLISH_STATUS="(.//time[contains(@class,'published')])[1]/../../..//h3[text()='$title']";

	
	@BeforeTest
	public void beforeMethod() throws InterruptedException {
		System.out.print("Start beforeMethod");
		//Init Chrome driver on Window 64bit
		System.setProperty("webdriver.chrome.driver",DEFAULT_DRIVERPATH_WIN);
		driver = new ChromeDriver();
		//Open Admin page
		driver.navigate().to(DEFAULT_BASEURL);
		driver.manage().window().maximize();
		driver.navigate().refresh();
		
		//Login the system
		login();
		System.out.print("End beforeMethod");
	}

	@AfterTest
	public void afterMethod() {
		System.out.print("Start AfterMethod");
		driver.manage().deleteAllCookies();
		driver.quit();
		System.out.print("End AfterMethod");
	}

	@Test
	public void test_01_CreateANewPostSuccessfully() throws InterruptedException {
		System.out.print("Create a new post successfully");
		
		String title ="This is my post "+getRandomNumber();
		String content="Hello! "+title+" is posted successfully";
		
		System.out.print("Step1: Click on New Post button");
		click(ELEMENT_NEWPOST_BTN);
		Utils.pause(1000);
		
		System.out.print("Step 2: Type post's title");
		type(ELEMENT_POST_TITLE,title,true);
		Utils.pause(1000);
		
		System.out.print("Step3: Type post's content");
		type(ELEMENT_POST_CONTENT,content,true);
		Utils.pause(1000);
		
		System.out.print("Step4: Upload Image");
		click(ELEMENT_POST_SETTING_BTN);
		Utils.pause(2000);
		click(ELEMENT_POST_SETTING_UPLOAD);
		uploadFileUsingRobot(DEFAULT_FILEIMAGE_PATH);
		Utils.pause(3000);
		
		System.out.print("Step 5: close Setting area");
		click(ELEMENT_POST_TITLE);
		Utils.pause(1000);
		
		System.out.print("Step 6: Select Publish Now");
		click(ELEMENT_ACTIONS_LIST);
		Utils.pause(1000);
		click(ELEMENT_ACTIONS_LIST_PUBLISH_LINK);
		Utils.pause(5000);
		
		System.out.print("Step 7: Click on Publish Now button");
		click(ELEMENT_PUBLISH_BTN,2000,1);
		
		System.out.print("Step 8: Open content page");
		click(ELEMENT_CONTENT_LINK);
		Utils.pause(1000);
		
		System.out.print("Step 9: Verify that the new post at step 5 is posted.");
		waitForAndGetElement(ELEMENT_POST_TITLE_TEXT.replace("$title",title),2000,1);
		waitForAndGetElement(ELEMENT_POST_PUBLISH_STATUS.replace("$title",title),2000,1);
	}
	
	@Test
	public void test_02_EditAPostSuccessfully() {
		System.out.print("Edit a post successfully");
		
		String newTitle ="my post "+getRandomNumber();
		
		System.out.print("Step1: Open a post");
		click(ELEMENT_POST_FIRST_PUBLISH);
		Utils.pause(1000);
		
		System.out.print("Step2: Click on Edit button");
		click(ELEMENT_EDIT_POST_BTN);
		Utils.pause(1000);
		
		System.out.print("Step3: Type a new title");
		type(ELEMENT_POST_TITLE,newTitle,true);
		Utils.pause(1000);
		
		System.out.print("Step4: Click on Update Post button");
		click(ELEMENT_UPDATE_POST_BTN);
		Utils.pause(1000);
		
		System.out.print("Step5: Open Content page");
		click(ELEMENT_CONTENT_LINK);
		Utils.pause(1000);
		
		System.out.print("Step6: Verify that the title of the post is changed");
		waitForAndGetElement(ELEMENT_POST_TITLE_TEXT.replace("$title",newTitle),2000,1);
	}
	
	@Test
	public void test_03_DeleteAPostSuccessfully() {
		System.out.print("Delete a post successfully");
		
		String post = waitForAndGetElement(ELEMENT_POST_FIRST_PUBLISH_TITLE).getText();
		
		System.out.print("Step1: Open a post");
		click(ELEMENT_POST_FIRST_PUBLISH);
		Utils.pause(1000);
		System.out.print("Step2: Click on Edit button");
		click(ELEMENT_EDIT_POST_BTN);
		Utils.pause(1000);
		System.out.print("Step3: Select Delete Post");
		click(ELEMENT_ACTIONS_LIST);
		Utils.pause(1000);
		click(ELEMENT_ACTIONS_LIST_DELETE_LINK);
		Utils.pause(3000);
		
		System.out.print("Step4: Click on Delete button");
		click(ELEMENT_DELETE_CONFIRM_BTN);
		Utils.pause(1000);
		System.out.print("Step5: Verify that the post is deleted successfully");
		waitForElementNotPresent(ELEMENT_POST_TITLE_TEXT.replace("$title",post),2000,1);
	}
	
	/**
	 * Login to the system with correct email and password
	 * @throws InterruptedException 
	 */
	public void login() throws InterruptedException{
		System.out.print("Login");
		String email ="admin@test.com";
		String pass="1q2w3e4r";
		
		System.out.print("Step 1: Input email");
		type(ELEMENT_EMAIL,email,true);
		Utils.pause(1000);
		System.out.print("Step 2: Input password");
		type(ELEMENT_PASS,pass,true);
		Utils.pause(1000);
		System.out.print("Step 3: Click on Sign In button");
		click(ELEMENT_SIGNIN_BTN);
		Utils.pause(3000);
		System.out.print("Verify that login successfully");
		waitForAndGetElement(ELEMENT_NEWPOST_BTN,2000,1);
	}
	
}
