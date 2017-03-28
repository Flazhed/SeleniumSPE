import org.junit.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;

/**
 * Created by hansen on 3/26/17.
 */

public class CarTests {

    private static WebDriver driver;
    private static final int WAIT_MAX = 4;

    @BeforeClass
    public static void setup(){

        System.setProperty("webdriver.chrome.driver","/media/hansen/DATA/Soft/2. sem/Test/Drivers/chromedriver");
        driver = new ChromeDriver();
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
        driver.get("http://localhost:3000");
    }

    @Before
    public void beforeClass(){
        driver.get("http://localhost:3000");
    }

    @After
    public void afterClass(){

        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
    }

    @AfterClass
    public static void tearDown(){
        driver.quit();
        //Reset Database
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
    }

    @Test
    public void DOMLoadedTest() throws Exception {
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            assertThat(rows.size(), is(5));
            return true;
        });
    }

    @Test
    public void filterFunctionTest(){

        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement elementFilter = d.findElement(By.id("filter"));
            elementFilter.sendKeys("2002");
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            assertThat(rows.size(), is(2));
            return true;
        });
    }

    @Test
    public void filterFunctionResetTest(){


        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement elementFilter = d.findElement(By.id("filter"));
            elementFilter.sendKeys(Keys.CONTROL + "a");
            elementFilter.sendKeys(Keys.DELETE);
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            assertThat(rows.size(), is(5));
            return true;
        });

    }

    @Test
    public void sortByYearTest(){
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement elementSort = d.findElement(By.xpath("//*[@id=\"h_year\"]"));
            elementSort.click();
            WebElement e = d.findElement(By.xpath("//*[@id=\"tbodycars\"]"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            String firstRowId = rows.get(0).findElement(By.tagName("td")).getAttribute("innerHTML");
            String lastRowId = rows.get(rows.size()-1).findElement(By.tagName("td")).getAttribute("innerHTML");

            assertThat(firstRowId, is("938"));
            assertThat(lastRowId, is("940"));
            return true;
        });
    }

    @Test
    public void editCarTest(){

        String carDescription = "Cool car";
        String carId = "938";

        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.xpath("//*[@id=\"tbodycars\"]"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));

            WebElement car = e.findElement(By.xpath("//tr/td[contains(text(), '" + carId +"')]/parent::node()"));

            car.findElement(By.tagName("a")).click();
            WebElement elementDescp = d.findElement(By.id("description"));
            elementDescp.clear();
            elementDescp.sendKeys(carDescription);
            WebElement elementSave = d.findElement(By.id("save"));
            elementSave.click();

            e = d.findElement(By.xpath("//*[@id=\"tbodycars\"]"));
            rows = e.findElements(By.tagName("tr"));

            car = e.findElement(By.xpath("//tr/td[contains(text(), '" + carId +"')]/parent::node()/td[6]"));
            assertThat(car.getAttribute("innerHTML"), is(carDescription));
            return true;
        });
    }

    @Test
    public void newCarErrorTest(){
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement elementSave = d.findElement(By.id("save"));
            elementSave.click();

            WebElement elementSubmitText = d.findElement(By.xpath("//*[@id=\"submiterr\"]"));
            assertThat(elementSubmitText.getAttribute("innerHTML"), is("All fields are required"));

            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            assertThat(rows.size(), is(5));
            return true;
        });
    }

    @Test
    public void createNewCarTest(){

        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement elementTemp;
            elementTemp = d.findElement(By.id("year"));
            elementTemp.sendKeys("2008");

            elementTemp = d.findElement(By.id("registered"));
            elementTemp.sendKeys("2002-5-5");

            elementTemp = d.findElement(By.id("make"));
            elementTemp.sendKeys("Kia");

            elementTemp = d.findElement(By.id("model"));
            elementTemp.sendKeys("Rio");

            elementTemp = d.findElement(By.id("description"));
            elementTemp.sendKeys("As new");

            elementTemp = d.findElement(By.id("price"));
            elementTemp.sendKeys("31000");
            WebElement elementSave = d.findElement(By.id("save"));
            elementSave.click();

            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            assertThat(rows.size(), is(6));
            return true;
        });
    }
}
