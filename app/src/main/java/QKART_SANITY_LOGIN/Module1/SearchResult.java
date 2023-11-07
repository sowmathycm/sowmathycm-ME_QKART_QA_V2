package QKART_SANITY_LOGIN.Module1;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchResult {
    WebElement parentElement;

    public SearchResult(WebElement SearchResultElement) {
        this.parentElement = SearchResultElement;
    }

    /*
     * Return title of the parentElement denoting the card content section of a
     * search result
     */
    public String getTitleofResult() {
        String titleOfSearchResult = "";
        // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 03: MILESTONE 1
        // Find the element containing the title (product name) of the search result and
        // assign the extract title text to titleOfSearchResult
        try{
        Thread.sleep(2000);
        WebElement title = parentElement.findElement(By.xpath(".//p[@class ='MuiTypography-root MuiTypography-body1 css-yg30e6']"));
       titleOfSearchResult = title.getText();
      
        }catch(NoSuchElementException | InterruptedException e) {
            System.out.println("Title element not found:" + e.getMessage());
        }
       return titleOfSearchResult;
    
    }
    /*
     * Return Boolean denoting if the open size chart operation was successful
     */
    public Boolean openSizechart() {
        try {

            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            // Find the link of size chart in the parentElement and click on it
            Thread.sleep(2000);
            WebElement open = parentElement.findElement(By.xpath("//div[@class='MuiCardContent-root css-1qw96cp']/button[contains(text(), 'Size chart')]"));
            open.click();
            return true;
        } catch (Exception e) {
            System.out.println("Exception while opening Size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the close size chart operation was successful
     */
    public Boolean closeSizeChart(WebDriver driver) {
        try {
            Thread.sleep(2000);
            Actions action = new Actions(driver);

            // Clicking on "ESC" key closes the size chart modal
            action.sendKeys(Keys.ESCAPE);
            action.perform();
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception while closing the size chart: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean based on if the size chart exists
     */
    public Boolean verifySizeChartExists() {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            /*
             * Check if the size chart element exists. If it exists, check if the text of
             * the element is "SIZE CHART". If the text "SIZE CHART" matches for the
             * element, set status = true , else set to false
             */
            Thread.sleep(2000);
            WebElement sizechart = parentElement.findElement(By.xpath("//div[@class='MuiCardContent-root css-1qw96cp']/button[contains(text(), 'Size chart')]"));
           if(sizechart.isDisplayed() && sizechart.getText().equals("SIZE CHART")){
            
            status = true;
           }
           else{
            
            status = false;
           }
            
        } catch (Exception e) {
            return status;
        }
        return status;
    }

    /*
     * Return Boolean if the table headers and body of the size chart matches the
     * expected values
     */
    public Boolean validateSizeChartContents(List<String> expectedTableHeaders, List<List<String>> expectedTableBody,
            WebDriver driver) {
        Boolean status = true;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            /*
             * Locate the table element when the size chart modal is open
             * 
             * Validate that the contents of expectedTableHeaders is present as the table
             * header in the same order
             * 
             * Validate that the contents of expectedTableBody are present in the table body
             * in the same order
             */
            Thread.sleep(2000);
            WebElement sizechart = driver.findElement(By.xpath("//table[@class='MuiTable-root css-1v2fgo1']"));
            List<WebElement> tableheaders = sizechart.findElements(By.tagName("th"));
            List<String> actualTable = new ArrayList<>();
            for(WebElement tableheader : tableheaders){
             actualTable.add(tableheader.getText());
            }
            List<WebElement> tableBody = sizechart.findElements(By.tagName("tr"));
            List<List<String>> actualTableBody = new ArrayList<>();
            for(WebElement row : tableBody){
                List<WebElement> rowCells = row.findElements(By.tagName("td"));
                List<String> rowData = new ArrayList<>();
                for(WebElement cell : rowCells){
                    rowData.add(cell.getText());
                }
                actualTableBody.add(rowData);
            }
            if(!expectedTableHeaders.equals(actualTable) || !expectedTableBody.equals(actualTableBody)){
                status = false;
            }
            status = true;
            return status;

        } catch (Exception e) {
            System.out.println("Error while validating chart contents");
            return false;
        }
    }

    /*
     * Return Boolean based on if the Size drop down exists
     */
    public Boolean verifyExistenceofSizeDropdown(WebDriver driver) {
        Boolean status = false;
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 04: MILESTONE 2
            // If the size dropdown exists and is displayed return true, else return false
            List<WebElement> sizeDropDown = driver.findElements(By.xpath("//select[@id='uncontrolled-native']"));
            if(!sizeDropDown.isEmpty() && sizeDropDown.get(0).isDisplayed()){
                status = true;
            }else{
                 status = false;
            }
        
        } catch (Exception e) {
            return status;
        }
        return status;
    }
}