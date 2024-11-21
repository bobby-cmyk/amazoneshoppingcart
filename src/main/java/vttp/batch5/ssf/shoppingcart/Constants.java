package vttp.batch5.ssf.shoppingcart;

import java.util.Random;

import org.springframework.http.HttpStatusCode;

public class Constants {
    public static final HttpStatusCode CODE_200 = HttpStatusCode.valueOf(200);
    public static final HttpStatusCode CODE_404 = HttpStatusCode.valueOf(404);

    // FOR CAPTCHA
    public static final int CAPTCHA_LENGTH = 6;

    public static final int ALPHABET_COUNT= 26;
    public static final int NUMBER_COUNT = 10;

    // REFER TO ASCii TABLE
    public static final int START_INDEX_UPPER = 65;
    public static final int START_INDEX_LOWER = 97;
    public static final int START_INDEX_NUMBER = 48;

    public static String generateCaptcha() {

        String captcha = "";

        Random rand = new Random();

        for (int i = 0; i < CAPTCHA_LENGTH/3; i++) {

            char upperC = (char) (rand.nextInt(ALPHABET_COUNT) + START_INDEX_UPPER);
            char lowerC = (char) (rand.nextInt(ALPHABET_COUNT) + START_INDEX_LOWER);
            char numberC = (char) (rand.nextInt(NUMBER_COUNT) + START_INDEX_NUMBER);

            captcha += upperC;
            captcha += lowerC;
            captcha += numberC;
        }
        return captcha;
    }
}
