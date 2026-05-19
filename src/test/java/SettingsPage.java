import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;

public class SettingsPage extends BasePage {

    // The true HTML select element for the birthday month
    @FindBy(name = "user_month")
    private WebElement monthDropdown;

    // The invisible checkbox input (used to check the current state)
    @FindBy(name = "pm_mail_noti")
    private WebElement hiddenCheckboxInput;

    // The styled span that a human actually clicks
    @FindBy(xpath = "//input[@name='pm_mail_noti']/following-sibling::span[@class='checkmark']")
    private WebElement visibleCheckmarkSpan;

    public SettingsPage(WebDriver driver) {
        super(driver);
    }

    public void selectBirthMonth(String monthNumber) {
        wait.until(ExpectedConditions.visibilityOf(monthDropdown));
        // Using Selenium's dedicated Select wrapper for 2 points!
        Select selectMonth = new Select(monthDropdown);
        selectMonth.selectByVisibleText(monthNumber);
    }

    public void enablePrivateMessageNotifications() {
        wait.until(ExpectedConditions.visibilityOf(visibleCheckmarkSpan));

        // Check if it's already checked using the hidden input
        if (!hiddenCheckboxInput.isSelected()) {
            // Force the click with JS to ignore any sticky headers/footers in the way!
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", visibleCheckmarkSpan);
        }
    }
}