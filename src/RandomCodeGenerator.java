import java.util.Random;

public class RandomCodeGenerator {

    // Method to generate a random alphanumeric code of specified length
    public static String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    // Example usage
    public static void main(String[] args) {
        // Generate a random code of length 8
        String randomCode = RandomCodeGenerator.generateRandomCode(8);
        System.out.println("Random Code: " + randomCode);
    }
}
