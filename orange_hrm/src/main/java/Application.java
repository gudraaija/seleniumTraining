import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class Application {

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, IOException {
        //defining webdriver
        System.setProperty("webdriver.chrome.driver", "../chromedriver_win32/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://opensource-demo.orangehrmlive.com/");//navigate to needed page

        login(driver);
        waitUntilJSReady(driver);//wait till JS is done

        //check if needed page is loaded
        String header = driver.findElement(By.className("head")).getText();
        assertThat(header).isEqualTo("Dashboard");

        //opens Employee list view (Mitarbeiterliste = employee list)
        hover_click(driver);

        //populate form with random values and clicks search
        populate_form(driver);

        logout(driver);

        driver.quit();//closing the session
    }


    //method for login
    public static void login(WebDriver driver) throws InterruptedException, ClassNotFoundException, IOException {

        Properties prop = loadConfiguration();//loading properties from credentials file
        String user = prop.getProperty("username");//setting the values
        String pwd = prop.getProperty("pwd");

        //retrieving needed elements from form
        WebElement login = driver.findElement(By.id("btnLogin"));
        WebElement username = driver.findElement(By.id("txtUsername"));
        WebElement password = driver.findElement(By.id("txtPassword"));
        //populating from
        username.sendKeys(user);
        password.sendKeys(pwd);
        //submitting form
        login.click();
        Thread.sleep(2000);//so you can see something

    }

    //Method for loading credentials for login page
    private static Properties loadConfiguration() throws ClassNotFoundException, IOException {
        Properties prop = new Properties();
        String propFileName = "credentials.properties";

        Class cls = Class.forName("Application");
        ClassLoader cLoader = cls.getClassLoader();
        InputStream inputStream = cLoader.getResourceAsStream(propFileName);
        prop.load(inputStream);

        return prop;
    }

    //Method for hover on menu and click on needed sub-tab
    public static void hover_click(WebDriver driver) throws InterruptedException {
        //find tab in menu
        WebElement element = driver.findElement(By.id("menu_pim_viewPimModule"));

        //hovers on needed "main" tab
        Actions action = new Actions(driver);
        action.moveToElement(element).build().perform();

        //click on needed  sub-tab
        driver.findElement(By.id("menu_pim_viewEmployeeList")).click();

        Thread.sleep(2000);//so you can see something

    }

    //populates form. used when user navigated to employee list page
    private static void populate_form(WebDriver driver) throws InterruptedException {

        Random random = new Random();
        //retrieving all from elements
        WebElement name = driver.findElement(By.id("empsearch_employee_name_empName"));
        WebElement supName = driver.findElement(By.id("empsearch_supervisor_name"));
        WebElement id = driver.findElement(By.id("empsearch_id"));
        Select jobList = new Select(driver.findElement(By.id("empsearch_job_title")));
        Select statusList = new Select (driver.findElement(By.id("empsearch_employee_status")));
        Select unitList = new Select (driver.findElement(By.id("empsearch_sub_unit")));
        Select includeList = new Select (driver.findElement(By.id("empsearch_termination")));
        WebElement search = driver.findElement(By.id("searchBtn"));

        //populating all form with random values
        name.sendKeys(RandomStringUtils.randomAlphabetic(7));
        supName.sendKeys(RandomStringUtils.randomAlphabetic(7));
        id.sendKeys(RandomStringUtils.randomNumeric(5));

        int index = random.nextInt(9)+1;//random value from 1 to 9
        jobList.selectByIndex(index);

        index = random.nextInt(6)+1;//random value from 1 to 6
        statusList.selectByIndex(index);

        index = random.nextInt(4)+1;//random value from 1 to 4
        unitList.selectByIndex(index);

        index = random.nextInt(2)+1;//random value from 1 to 2
        includeList.selectByIndex(index);

        search.click();//search the employee in list

        Thread.sleep(2000);//so you can see something
    }

    //Method for logout
    private static void logout(WebDriver driver) throws InterruptedException {
        driver.findElement(By.id("welcome")).click();
        WebElement menu = driver.findElement(By.id("welcome-menu"));

        Thread.sleep(1000);
        driver.findElement(By.linkText("Logout"));//for this we need to wait a bit

        Thread.sleep(2000);//so you can see something

    }

    //these two are some weird functions for JS file check
    //variables for asserting when JS is done
    private static WebDriver jsWaitDriver;
    private static WebDriverWait jsWait;
    private static JavascriptExecutor jsExec;
    //retrieving driver
    public static void setDriver (WebDriver driver) {
        jsWaitDriver = driver;
        jsWait = new WebDriverWait(jsWaitDriver, 10);
        jsExec = (JavascriptExecutor) jsWaitDriver;
    }

    //waits till JS document state is ready
    public static void waitUntilJSReady(WebDriver driver) {
        setDriver(driver);
        WebDriverWait wait = new WebDriverWait(jsWaitDriver,15);
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;

        //wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = jsDriver -> ((JavascriptExecutor) jsWaitDriver)
                .executeScript("return document.readyState").toString().equals("complete");
    }
}



