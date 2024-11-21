package vttp.batch5.ssf.shoppingcart.validators;

import java.util.logging.Logger;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CaptchaValidator implements ConstraintValidator<CaptchaConstraint, String> {

    private final Logger logger = Logger.getLogger(CaptchaValidator.class.getName());
    
    @Override
    public void initialize(CaptchaConstraint captcha) {
    }

    @Override
    public boolean isValid(String captchaField, ConstraintValidatorContext cxt) {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession sess = attr.getRequest().getSession(true);

        // Get the captcha value from session
        String currentCaptcha = (String) sess.getAttribute("currentCaptcha");

        logger.info("Session captcha: %s".formatted(currentCaptcha));

        return captchaField.matches(currentCaptcha);
    }
}

// https://www.baeldung.com/spring-mvc-custom-validator