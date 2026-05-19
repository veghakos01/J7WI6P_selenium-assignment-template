import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

public class HomePage extends BasePage {

    // Locate the login button (usually found in the top navigation bar)
    @FindBy(xpath = "//a[contains(translate(text(), 'BELÉPÉS', 'belépés'), 'belépés') or contains(@href, 'login')]")
    private WebElement loginButton;

    // If Gepigeny has a cookie consent popup, we can catch it here
    // Updated XPath: Finds the specific div, goes down to the button with mode='primary', and checks for the 'ELFOGADOM' span
    @FindBy(xpath = "//div[contains(@class, 'qc-cmp2-summary-buttons')]//button[@mode='primary' and .//span[text()='ELFOGADOM']]")
    private WebElement acceptCookiesButton;




    // grabbing the active profile icon that shows up after we get in
    // Grabbing the active profile icon using its specific title attribute so Selenium doesn't get confused
    @FindBy(xpath = "//div[contains(@class, 'user_hbutton') and @title='Profil']")
    private WebElement loggedInProfileIcon;

    // grabbing the exact logout link from the dropdown menu
    @FindBy(xpath = "//div[@id='user-menu']//a[contains(@href, 'logout=yes')]")
    private WebElement logoutLink;


    // Locates the "Profil" link inside the dropdown menu
    // Locates the link by looking for the child span that has the 'icon-user' class
    @FindBy(xpath = "//div[@id='user-menu']//a[.//span[@class='icon-user']]")
    private WebElement myProfileLink;



    public void clickMyProfile() {
        // Wait for the link to actually be visible inside the dropdown
        WebElement profileLink = wait.until(ExpectedConditions.visibilityOf(myProfileLink));

        // Use JS to click it instantly, bypassing any slide-down or fade-in CSS animations
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", profileLink);
    }



    public HomePage(WebDriver driver) {
        // This calls the BasePage constructor to set up the driver and our 10-second wait!
        super(driver);
    }

    public void navigateTo() {
        driver.get("https://gepigeny.hu/");
    }



    public String getPageTitle() {
        return driver.getTitle();
    }

    public void acceptCookiesIfPresent() {
        try {
            // Create a temporary, short wait JUST for the cookie banner
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));

            // Use the shortWait instead of the main wait
            shortWait.until(ExpectedConditions.visibilityOf(acceptCookiesButton));
            acceptCookiesButton.click();
        } catch (Exception e) {
            // Now it only wastes 3 seconds before moving on!
            System.out.println("No cookie popup found or already accepted.");
        }
    }





    public void clickLogin() {
        // Ensure the button is actually ready to be clicked before we try
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }






    public void openProfileMenu() {
        // waiting for the button to actually be ready
        WebElement profileBtn = wait.until(ExpectedConditions.elementToBeClickable(loggedInProfileIcon));

        // normal click just doesn't do anything for some reason, maybe the event listener is weird.
        // using the Actions class to simulate a real mouse moving over it and clicking it usually does the trick
        Actions actions = new Actions(driver);
        actions.moveToElement(profileBtn).click().perform();
    }

    public boolean isLogoutOptionVisible() {
        try {
            // making sure the menu actually popped open and the link is there
            return wait.until(ExpectedConditions.visibilityOf(logoutLink)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickLogout() {
        // clicking the button to get out
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        logoutLink.click();
    }



}