import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
import java.io.File;
import java.nio.file.Files;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class LoginPage {

    WebDriver driver;
    WebDriverWait wait;

    // Locators up top to keep things tidy
    By profileIconBtn = By.cssSelector(".user_hbutton.login_t");
    By usernameField = By.name("user_name");
    By passwordField = By.name("user_pass");
    By loginSubmitBtn = By.name("login");

    // Constructor to pass the driver from the main test file
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        // 10 secs should be plenty for the popup to load
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }



    // Call this method from your @Test to actually do the login
    public void performLogin(String username, String password) {


        // DEBUG: Take a picture of the screen right before we try to find the login button
        try {
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), new java.io.File("debug_screen.png").toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.out.println("Couldn't take screenshot");
        }

        // just waiting for the element to physically exist on the page, not necessarily be "clickable"
        WebElement profileIcon = wait.until(ExpectedConditions.presenceOfElementLocated(profileIconBtn));

        // Selenium's normal click is being way too picky, so we force it with JS
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", profileIcon);

        // Gotta wait for the form animation to finish, otherwise Selenium throws a fit
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        usernameInput.sendKeys(username);

        // Since we waited for the username, the rest of the form is definitely there
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginSubmitBtn).click();
    }



}