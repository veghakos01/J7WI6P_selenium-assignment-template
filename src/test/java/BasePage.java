import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        // Selenium 4 syntax for a 10 sec wait
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // init page factory
        PageFactory.initElements(driver, this);
    }
}