package vttp.batch5.ssf.shoppingcart.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.batch5.ssf.shoppingcart.services.CartService;
import vttp.batch5.ssf.shoppingcart.models.Cart;
import vttp.batch5.ssf.shoppingcart.models.LoginForm;
import vttp.batch5.ssf.shoppingcart.services.AuthService;

import static vttp.batch5.ssf.shoppingcart.Constants.*;

@Controller
@RequestMapping(path={"/", "/index", "/login"})
public class AuthController {
    
    private final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private AuthService authService;

    @Autowired
    private CartService cartService;

    
    @GetMapping
    public ModelAndView getLoginPage(
        HttpSession sess) 
    {
        ModelAndView mav = new ModelAndView();

        // Generate a captcha
        String currentCaptcha = generateCaptcha();
        // Add generated captcha to session for validation
        sess.setAttribute("currentCaptcha", currentCaptcha);

        logger.info("Captcha (%s)) is generated and added to session".formatted(currentCaptcha));

        mav.addObject("currentCaptcha", currentCaptcha);
        mav.addObject("loginForm", new LoginForm());
        mav.setViewName("index");
        mav.setStatus(CODE_200);

        return mav;
    }

    @PostMapping("/auth")
    public ModelAndView authUser(
        @Valid LoginForm loginForm,
        BindingResult bindings,
        HttpSession sess) 
    {
        ModelAndView mav = new ModelAndView();

        if (bindings.hasErrors()) {
            logger.info("Username is empty or captcha does not match");

            // Generate a new captcha
            String currentCaptcha = generateCaptcha();
            
            // Overwrite previous generated captcha in session
            sess.setAttribute("currentCaptcha", currentCaptcha);

            logger.info("Captcha (%s)) is generated and added to session".formatted(currentCaptcha));

            mav.addObject("currentCaptcha", currentCaptcha);
            mav.setViewName("index");
            mav.setStatus(CODE_404);

            return mav;
        }

        String username = loginForm.getUsername();

        if (authService.authUser(username)) {
            logger.info("User (%s) has been successfully authenticated.".formatted(username));

            // Set username attribute to the current session
            sess.setAttribute("username", username);

            List<Cart> carts = cartService.getAllCarts(username);

            logger.info("Retrieved all carts for %s.".formatted(username));

            mav.addObject("carts", carts);
            mav.addObject("username", username);
            mav.setViewName("home");
            mav.setStatus(CODE_200);

            return mav;
        }

        else {
            String errorMessage = "User (%s) does not exist. Authentication failed".formatted(username);

            logger.info(errorMessage);

            // Create global error
            ObjectError err = new ObjectError("globalError", errorMessage);
            bindings.addError(err);

            // Generate a new captcha
            String currentCaptcha = generateCaptcha();

            // Overwrite previous generated captcha in session
            sess.setAttribute("currentCaptcha", currentCaptcha);

            logger.info("Captcha (%s)) is generated and added to session".formatted(currentCaptcha));

            mav.addObject("currentCaptcha", currentCaptcha);
            mav.setViewName("index");
            mav.setStatus(CODE_404);

            return mav;
        }
    }

    @PostMapping("/logout")
    public ModelAndView logout(
        HttpSession sess, 
        HttpServletRequest request) 
    {
        ModelAndView mav = new ModelAndView();

        String currentUser = (String) sess.getAttribute("username");

        // Destroy session
        sess.invalidate();
        logger.info("User (%s) is logged out".formatted(currentUser));

        // Generate a captcha
        String currentCaptcha = generateCaptcha();
        
        // Reinitialise session to store captcha for validation
        HttpSession newSession = request.getSession(true);

        logger.info("New session has started");

        // Add generated captcha to session for validation
        newSession.setAttribute("currentCaptcha", currentCaptcha);

        logger.info("Captcha (%s)) is generated and added to session".formatted(currentCaptcha));

        mav.addObject("currentCaptcha", currentCaptcha);
        mav.addObject("loginForm", new LoginForm());
        mav.setViewName("index");
        mav.setStatus(CODE_200);

        return mav;
    }
    
}
