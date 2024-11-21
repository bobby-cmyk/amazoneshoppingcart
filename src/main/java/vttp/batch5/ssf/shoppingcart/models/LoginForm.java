package vttp.batch5.ssf.shoppingcart.models;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import vttp.batch5.ssf.shoppingcart.validators.CaptchaConstraint;

public class LoginForm {
    
    @NotEmpty(message="Username cannot be empty")
    @NotNull(message="Username cannot be empty")
    private String username;
    
    @CaptchaConstraint(message = "Captcha does not match")
    private String captcha;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getCaptcha() {
        return captcha;
    }
    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
