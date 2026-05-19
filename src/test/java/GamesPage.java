import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class GamesPage extends BasePage {

    // grabbing the main title of the page to prove it loaded
    @FindBy(tagName = "h1")
    private WebElement pageTitle;

    public GamesPage(WebDriver driver) {
        super(driver);
    }

    public String getPageTitleText() {
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
        return pageTitle.getText();
    }
}