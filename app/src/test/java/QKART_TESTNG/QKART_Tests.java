package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.annotations.Test;

@Listeners(ListenerClass.class)
public class QKART_Tests {

    public static RemoteWebDriver driver;
    public static String lastGeneratedUserName;

     @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        final DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(BrowserType.CHROME);
        driver = new RemoteWebDriver(new URL("http://localhost:8082/wd/hub"), capabilities);
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
         @Test(description = "Verify registration happens correctly", priority = 1, groups = {"Sanity_Test"})
         @Parameters({"testUser", "password"})
         public void TestCase01(@Optional("defaultTestUser") String testUser, @Optional("defaultPassword") String password) throws InterruptedException {
        Boolean status;
         logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");
          takeScreenshot(driver, "StartTestCase", "TestCase01");

        // Visit the Registration page and register a new user
       
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
         status = registration.registerUser(testUser, password, true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
         status = login.PerformLogin(lastGeneratedUserName, password);
         logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
         assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

         logStatus("End TestCase", "Test Case 1: Verify user Registration : ", status
         ? "PASS" : "FAIL");
         takeScreenshot(driver, "EndTestCase", "TestCase01");
    }
    
     @Test(description = "Verify re-registering an already registered user fails", priority = 2, groups = {"Sanity_Test"})
    public void TestCase02() throws InterruptedException {
        Boolean status;
        logStatus("Start Testcase", "Test Case 2: Verify User Registration with an existing username ", "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase02");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");
        logStatus("Test Step", "User Registration : ", status ? "PASS" : "FAIL");
       
        
        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, "abc@123", false);
        assertFalse(status, "Re-registration is successfull");

        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success
        logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ? "FAIL" : "PASS");
        takeScreenshot(driver, "EndTestCase", "TestCase02");
        
    }
    
    @Test(description = "Verify the functionality of search text box", priority = 3, groups = {"Sanity_Test"})
    public void TestCase03() throws InterruptedException {
        logStatus("TestCase 3", "Start test case : Verify functionality of search box ", "DONE");
        boolean status;
        takeScreenshot(driver, "StartTestCase", "TestCase03");

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct("YONEX");
        assertTrue(status, "Failed to search for the product: YONEX");

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        assertTrue(searchResults.size()>0, "No search result found for the product");

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            
            assertTrue(elementText.toUpperCase().contains("YONEX"), "Unexpected search result: " + elementText);
        }

        logStatus("Step Success", "Successfully validated the search results ", "PASS");

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        assertTrue(status, "Failed to search for the product: Gesundheit");

        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        assertTrue(searchResults.isEmpty(), "Search Results were found");
        if (searchResults.size() == 0) {
              assertTrue(homePage.isNoResultFound(), "No products found is not displayed");
                logStatus("Step Success", "Successfully validated that no products found message is displayed", "PASS");
            
            logStatus("TestCase 3", "Test Case PASS. Verified that no search results were found for the given text",
                    "PASS");
        } else {
            logStatus("TestCase 3", "Test Case Fail. Expected: no results , actual: Results were available", "FAIL");
            
        }
        takeScreenshot(driver, "EndTestCase", "TestCase03");
  
        
    }
    @Test(description = "Verify the existence of size chart for certain items and validate contents of size chart", priority = 4, groups = {"Regression_Test"})
    public void TestCase04() throws InterruptedException {
        logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;
        takeScreenshot(driver, "StartTestCase", "TestCase04");

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
        assertTrue(status, "Failed to search for the product: Running Shoes");
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            if (result.verifySizeChartExists()) {
                logStatus("Step Success", "Successfully validated presence of Size Chart Link", "PASS");
                 assertTrue(result.verifySizeChartExists(), "Size chart link not found");
                // Verify if size dropdown exists
                status = result.verifyExistenceofSizeDropdown(driver);
                assertTrue(status, "Size dropdown not found");
                logStatus("Step Success", "Validated presence of drop down", status ? "PASS" : "FAIL");

                // Open the size chart
                if (result.openSizechart()) {
                    // Verify if the size chart contents matches the expected values
                
                    if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver)) {
                        logStatus("Step Success", "Successfully validated contents of Size Chart Link", "PASS");
                        assertTrue(result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver), "Size chart content does not match with the expected values");
                    } else {
                        logStatus("Step Failure", "Failure while validating contents of Size Chart Link", "FAIL");
                        status = false;
                    }

                    // Close the size chart modal
                    status = result.closeSizeChart(driver);

                } else {
                    logStatus("TestCase 4", "Test Case Fail. Failure to open Size Chart", "FAIL");
                    
                }

            } else {
                logStatus("TestCase 4", "Test Case Fail. Size Chart Link does not exist", "FAIL");
                
            }
        }
        logStatus("TestCase 4", "End Test Case: Validated Size Chart Details", status ? "PASS" : "FAIL");
        takeScreenshot(driver, "EndTestCase", "TestCase04");
        
    }
    
    @Test(description = "Verify that a new user can add multiple products in to the cart and Checkout", priority = 5, groups = {"Sanity_Test"})
    @Parameters({"YONEX", "Tan", "Addr"})
    public void TestCase05(String YONEX, String Tan, String Addr) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase05");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "Failed to login with registered user");
       
        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct(YONEX);
        assertTrue(status, "Failed to search for the product: YONEX");
        homePage.addProductToCart(YONEX);
        status = homePage.searchForProduct(Tan);
        assertTrue(status, "Failed to search for the product: Tan");
        homePage.addProductToCart(Tan);

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(Addr);
        checkoutPage.selectAddress(Addr);
        
        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");
        assertTrue(status, "Placing the order not redirected to the Thanks page");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();

        logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status ? "PASS" : "FAIL");
        takeScreenshot(driver, "EndTestCase", "TestCase05");
        
    }

    @Test(description = "Verify that the contents of the cart can be edited", priority = 6, groups = {"Regression_Test"})
    @Parameters({"Xtend", "Yarine"})
    public void TestCase06(String Xtend, String Yarine) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase06");
        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "Failed to login with registered user");
        homePage.navigateToHome();
        status = homePage.searchForProduct(Xtend);
        assertTrue(status, "Failed to search for the product: Xtend");
        homePage.addProductToCart(Xtend);

        status = homePage.searchForProduct(Yarine);
        assertTrue(status, "Failed to search for the product: Yarine");
        homePage.addProductToCart(Yarine);

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(Xtend, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(Yarine, 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(Xtend, 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());
            
        }

        status = driver.getCurrentUrl().endsWith("/thanks");
        assertTrue(status, "Placing the order not redirected to the Thanks page");

        homePage.navigateToHome();
        homePage.PerformLogout();

        logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ? "PASS" : "FAIL");
        takeScreenshot(driver, "EndTestCase", "TestCase06");
    }
    
    @Test(description = "Verify that insufficient balance error is thrown when the wallet balance is not enough", priority = 7, groups = {"Sanity_Test"})
    @Parameters({"Stylecon", "quantity"})
    public void TestCase07(String Stylecon, Integer quantity) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase",
                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough",
                "DONE");
                takeScreenshot(driver, "StartTestCase", "TestCase07");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "Failed to login with registered user");
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct(Stylecon);
        assertTrue(status, "Failed to search for the product: Stylecon");
        homePage.addProductToCart(Stylecon);

        homePage.changeProductQuantityinCart(Stylecon, quantity);

        homePage.clickCheckout();
      
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);
        status = checkoutPage.verifyInsufficientBalanceMessage();
        assertTrue(status, "Insufficient balance error not thrown when the wallet balance is not enough");

        logStatus("End TestCase",
                "Test Case 7: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",
                status ? "PASS" : "FAIL");
                takeScreenshot(driver, "EndTestCase", "TestCase07");

        
    }
    
     @Test(description = "Verify that a product added to a cart is available when a new tab is added", priority = 8, groups = {"Regression_Test"})
    public void TestCase08() throws InterruptedException {
        Boolean status = false;

        logStatus("Start TestCase",
                "Test Case 8: Verify that product added to cart is available when a new tab is opened",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase08");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "Failed to login with registered user");
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        assertTrue(status, "Failed to search for the product: YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);
        assertTrue(status, "Unable to verify cart contents");

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        logStatus("End TestCase",
        "Test Case 8: Verify that product added to cart is available when a new tab is opened",
        status ? "PASS" : "FAIL");
        takeScreenshot(driver, "EndTestCase", "TestCase08");

       
    }
    
    @Test(description = "Verify that privacy policy and about us links are working fine", priority = 9, groups = {"Regression_Test"})
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        logStatus("Start TestCase",
                "Test Case 09: Verify that the Privacy Policy, About Us are displayed correctly ",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase09");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "Failed to login with registered user");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        assertTrue(status, "Unable to verify the Privacy Policy URL");
        

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        assertTrue(status, "Unable to verify the text");

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        assertTrue(status, "Unable to verify the text");

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        logStatus("End TestCase",
        "Test Case 9: Verify that the Privacy Policy, About Us are displayed correctly ",
        "PASS");
         takeScreenshot(driver, "EndTestCase", "TestCase9");

    
    }
    
    @Test(description = "Verify that the contact us dialog works fine", priority = 10, groups = {"Regression_Test"})
    public void TestCase10() throws InterruptedException {
        logStatus("Start TestCase",
                "Test Case 10: Verify that contact us option is working correctly ",
                "DONE");
         takeScreenshot(driver, "StartTestCase", "TestCase10");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys("crio user");
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys("criouser@gmail.com");
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys("Testing the contact us page");

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));

        logStatus("End TestCase",
                "Test Case 10: Verify that contact us option is working correctly ",
                "PASS");

        takeScreenshot(driver, "EndTestCase", "TestCase10");

        assertTrue(true);
    }


    @Test(description = "Ensure that the Advertisement Links on the QKART page are clickable", priority = 11, groups = {"Sanity_Test"})
    public void TestCase11() throws InterruptedException {
        Boolean status = false;
        logStatus("Start TestCase",
                "Test Case 11: Ensure that the links on the QKART advertisement are clickable",
                "DONE");
        takeScreenshot(driver, "StartTestCase", "TestCase11");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status, "Failed to register new user");
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status, "Failed to login with registered user");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX Smash Badminton Racquet");
        assertTrue(status, "Failed to search for the product: YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        homePage.changeProductQuantityinCart("YONEX Smash Badminton Racquet", 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.selectAddress("Addr line 1  addr Line 2  addr line 3");
        checkoutPage.placeOrder();
       
        WebDriverWait wait = new WebDriverWait(driver, 3);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        status = driver.getCurrentUrl().endsWith("/thanks");
        assertTrue(status, "Placing the order not redirected to the Thanks page");

        List<WebElement> ads = driver.findElements(By.xpath("//iframe"));
        int actualSize = ads.size();
        assertTrue(actualSize ==3, "Number of ads is not 3");
       
      WebElement adv1 = driver.findElement(By.xpath("(//iframe)[1]"));
      driver.switchTo().frame(adv1);
      WebElement buyNow = driver.findElement(By.xpath("//button[text()='Buy Now']"));
      buyNow.click();
      wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/checkout"));
      assertTrue(driver.getCurrentUrl().endsWith("/checkout"), "URL does not contain checkout");
    
       
      takeScreenshot(driver, "Failed", "TestCase11");
    
     
      driver.navigate().back();
      driver.switchTo().parentFrame();
   
      WebElement adv2 = driver.findElement(By.xpath("(//iframe)[2]"));
      driver.switchTo().frame(adv2);
      buyNow = driver.findElement(By.xpath("//button[text()='Buy Now']"));
      buyNow.click();
      wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/checkout"));
      assertTrue(driver.getCurrentUrl().endsWith("/checkout"), "URL does not contain checkout");
    
       
       takeScreenshot(driver, "Failed", "TestCase11");
    
      driver.navigate().back();
      driver.switchTo().parentFrame();
   
     
      logStatus("End TestCase", "Test Case 11: TestCase for Check Advertisement ", status ? "PASS" : "FAIL");
      takeScreenshot(driver, "End Test Case", "TestCase11");

    
        
    }


   

    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
                message, status));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        try {
            File theDir = new File("/screenshots");
            if (!theDir.exists()) {
                theDir.mkdirs();
            }
            String timestamp = String.valueOf(java.time.LocalDateTime.now());
            String fileName = String.format("screenshot_%s_%s_%s.png", timestamp, screenshotType, description);
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
            File DestFile = new File("screenshots/" + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

