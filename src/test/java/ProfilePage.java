import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;

public class ProfilePage extends BasePage {

    // The textarea for the post
    @FindBy(name = "comment_message")
    private WebElement commentTextArea;

    // The submit button
    @FindBy(name = "post_comment")
    private WebElement submitPostButton;

    public ProfilePage(WebDriver driver) {
        super(driver);
    }

    public void writePost(String text) {
        // waiting for the textarea to be ready, then typing our random string
        wait.until(ExpectedConditions.visibilityOf(commentTextArea)).sendKeys(text);
    }

    public void submitPost() {
        // Wait for the button to be visible on the screen
        wait.until(ExpectedConditions.visibilityOf(submitPostButton));

        // Use JS to click it directly in the DOM, completely ignoring the fact that the textarea expanded and shifted the page!
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", submitPostButton);
    }
}