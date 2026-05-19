import java.util.UUID;

public class TestUtils {

    // Generates a unique string every single time it runs
    public static String generateRandomText() {
        return "Selenium automation test post: " + UUID.randomUUID().toString();
    }
}