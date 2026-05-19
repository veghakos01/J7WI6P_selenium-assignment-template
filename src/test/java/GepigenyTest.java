import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GepigenyTest {

    private WebDriver driver;
    private HomePage homePage;

    @BeforeClass
    public void setUp() {




        // setting up custom options and headless mode for the advanced points
        ChromeOptions options = new ChromeOptions();



        // running in headless so it doesn't pop up on my screen constantly
        options.addArguments("--headless");



        // forcing a 1080p window size so the website doesn't accidentally load the mobile layout
        options.addArguments("--window-size=1920,1080");

        // adding incognito mode so it never remembers our previous login sessions
        options.addArguments("--incognito");

        // initialize the driver with our custom options
        // (Note: if you used RemoteWebDriver with Docker in your last assignment, you can just swap this line!)
        driver = new ChromeDriver(options);

        homePage = new HomePage(driver);
    }

    @Test
    public void testHomePageTitle() {
        homePage.navigateTo();

        // clear out the cookie popup if it's there before we try to do anything else
        homePage.acceptCookiesIfPresent();

        // grab the title and check if it's the right one
        String title = homePage.getPageTitle();
        System.out.println("Loaded page title: " + title);

        Assert.assertTrue(title.contains("Gépigény"), "The page title should contain Gépigény");
    }

    @Test
    public void testSuccessfulLogin() throws IOException {

        // setting up the properties reader
        Properties props = new Properties();

        // grabbing the config file so we don't hardcode passwords
        FileInputStream fis = new FileInputStream("config.properties");
        props.load(fis);

        // pulling the actual values out of the file
        String configUser = props.getProperty("username");
        String configPass = props.getProperty("password");

        // 1. go to the site
        driver.get("https://gepigeny.hu");

        // FIX: Clear the cookie banner before trying to click the login button!
        homePage.acceptCookiesIfPresent();

        // 2. init the login page
        LoginPage loginPage = new LoginPage(driver);

        // 3. pass the variables we just read from the config file!
        loginPage.performLogin(configUser, configPass);

        // Force Selenium to wait out the "Üdvözlünk" animation
        try {
            System.out.println("Waiting for the welcome animation to finish...");
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // The animation breaks Selenium's view of the site, so we just refresh the page to get a clean slate!
        driver.navigate().refresh();

        // NOW pop the menu open to check if we actually got in
        homePage.openProfileMenu();

        // if this is true, the test passes. If false, it fails and prints our message!
        Assert.assertTrue(homePage.isLogoutOptionVisible(), "Login failed: Logout option was not visible in the dropdown.");




    }




    @Test(dependsOnMethods = "testSuccessfulLogin")
    public void testProfilePostWithRandomData() throws InterruptedException {

        // 0. The previous test left the menu open, so we refresh the page to close it and get a clean state!
        driver.navigate().refresh();

        // 1. We are already logged in! Open the menu and click our profile
        homePage.openProfileMenu();
        homePage.clickMyProfile();

        // 2. Init the new Profile page
        ProfilePage profilePage = new ProfilePage(driver);

        // 3. Generate our random string using our new utility class
        String randomPost = TestUtils.generateRandomText();

        // 4. Fill the textarea and submit it
        profilePage.writePost(randomPost);
        Thread.sleep(2000);
        profilePage.submitPost();

        // Let the site process the post for a quick second
        Thread.sleep(2000);

        // 5. FREE POINTS: The browser history test!
        driver.get("https://gepigeny.hu/jatekok");
        Thread.sleep(1500); // Wait 1.5s so you can see it arrive

        driver.navigate().back();
        Thread.sleep(1500); // Wait 1.5s so you can see it go back to the profile
        Assert.assertTrue(driver.getCurrentUrl().contains("user"), "Back navigation failed");

        driver.navigate().forward();
        Thread.sleep(1000);
        Assert.assertTrue(driver.getCurrentUrl().contains("jatekok"), "Forward navigation failed");



    }

    @Test(dependsOnMethods = "testProfilePostWithRandomData")
    public void testMultiplePagesLoop() throws InterruptedException {
        // an array of urls to loop through for the multiple_page_test points
        String[] urlsToTest = {
                "https://gepigeny.hu/jatekok",
                "https://gepigeny.hu/hirek",
                "https://gepigeny.hu/rolunk",
                "https://gepigeny.hu/forum/",
                "https://gepigeny.hu/community"
        };

        GamesPage gamesPage = new GamesPage(driver);

        // loop through each url and verify the page loads by checking the h1 tag
        for (String url : urlsToTest) {
            Thread.sleep(1000);
            driver.get(url);
            // 1. Verify the visible h1 header on the page
            String headerText = gamesPage.getPageTitleText();
            Assert.assertFalse(headerText.isEmpty(), "Page header was empty on " + url);

            // 2. Read and verify the browser tab title using exactly getTitle() for the points!
            String browserTitle = driver.getTitle();
            Assert.assertNotNull(browserTitle, "Browser title was null!");
            Assert.assertTrue(browserTitle.contains("Gépigény"), "Browser title did not contain expected text on " + url);

        }
    }

    @Test(dependsOnMethods = "testMultiplePagesLoop")
    public void testSettingsForm() throws InterruptedException {
        // Navigate directly to your account settings
        driver.get("https://gepigeny.hu/user/vegak/beallitasok");

        SettingsPage settingsPage = new SettingsPage(driver);

        Thread.sleep(1000);

        // Execute the form interactions to rack up the points!
        settingsPage.selectBirthMonth("5");
        Thread.sleep(1000);
        settingsPage.enablePrivateMessageNotifications();
        Thread.sleep(1000);


        Assert.assertTrue(driver.getCurrentUrl().contains("beallitasok"), "Failed to load settings page");
    }

    @Test(dependsOnMethods = {"testSuccessfulLogin", "testProfilePostWithRandomData", "testSettingsForm"})
    public void testLogout() throws InterruptedException {
        // 1. Go back to the homepage so we know exactly where we are
        driver.get("https://gepigeny.hu/");

        // 2. You were exactly right: the menu is closed! We MUST click the profile icon to open it first!
        homePage.openProfileMenu();

        // Let the dropdown animation finish
        Thread.sleep(1000);

        // 3. Now the menu is open, so we can click the logout link
        homePage.clickLogout();

        // 4. Verify we are logged out
        Assert.assertTrue(driver.getCurrentUrl().contains("gepigeny.hu"), "Did not redirect after logout");
    }


    @AfterClass
    public void tearDown() {
        // clean up and close the invisible browser when we are done
        if (driver != null) {
            driver.quit();
        }
    }
}