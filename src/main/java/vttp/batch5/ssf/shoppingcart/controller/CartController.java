package vttp.batch5.ssf.shoppingcart.controller;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.batch5.ssf.shoppingcart.models.Cart;
import vttp.batch5.ssf.shoppingcart.models.Item;
import vttp.batch5.ssf.shoppingcart.models.LoginForm;
import vttp.batch5.ssf.shoppingcart.services.CartService;

import static vttp.batch5.ssf.shoppingcart.Constants.*;

@Controller
@RequestMapping
public class CartController {

    private final Logger logger = Logger.getLogger(CartController.class.getName());

     @Autowired
    private CartService cartService;

    @GetMapping("home/{username}")
    public ModelAndView getHome(
        @PathVariable String username, 
        HttpSession sess) 
    {
        ModelAndView mav = new ModelAndView();

        String currentUser = (String) sess.getAttribute("username");

        if (username.equals(currentUser)) {

            List<Cart> carts = cartService.getAllCarts(username);

            logger.info("Retrieved all carts for %s.".formatted(username));

            mav.addObject("carts", carts);
            mav.addObject("username", username);
            mav.setViewName("home");
            mav.setStatus(CODE_200);
            
            return mav;
        }

        else {
            String errorMessage = "User not allowed. Please login before proceeding";

            logger.info(errorMessage);

            // Generate a new captcha
            String currentCaptcha = generateCaptcha();
            
            // Overwrite previous generated captcha in session
            sess.setAttribute("currentCaptcha", currentCaptcha);

            logger.info("Captcha (%s)) is generated and added to session".formatted(currentCaptcha));

            mav.addObject("currentCaptcha", currentCaptcha);
            mav.addObject("loginForm", new LoginForm());
            mav.setViewName("index");
            mav.setStatus(CODE_404);

            return mav;
        }
    }
    
    @PostMapping("/newcart")
    public ModelAndView getNewCart(
        HttpSession sess)     
    {
        ModelAndView mav = new ModelAndView();

        String currentUser = (String) sess.getAttribute("username");

        // Create a new cart
        String cartId = cartService.createCart(currentUser);

        logger.info("New cart %s has been created by %s".formatted(cartId, currentUser));

        // Try to get cart
        Optional<Cart> opt = cartService.getCart(currentUser, cartId);

        if (opt.isEmpty()) {
            logger.info("Unsuccessful retrieval. Cart %s does not exist for %s".formatted(cartId, currentUser));

            mav.addObject("cartId", cartId);
            mav.setViewName("cart_notfound");
            mav.setStatus(CODE_404);

            return mav;
        }

        // Get cart
        Cart cart = opt.get();
        
        logger.info("Retrieved Cart %s for %s".formatted(cartId, currentUser));

        mav.addObject("cart", cart);
        mav.addObject("item", new Item());
        mav.setViewName("cart");
        mav.setStatus(CODE_200);

        return mav;
    }

    @PostMapping("/deletecart")
    public ModelAndView deleteCart(
        HttpSession sess,
        @RequestBody MultiValueMap<String, String> form) 
    {
        ModelAndView mav = new ModelAndView();

        String currentUser = (String) sess.getAttribute("username");

        String cartId = form.getFirst("cartId");

        cartService.deleteCart(currentUser, cartId);

        logger.info("Cart %s (%s) has been deleted".formatted(cartId, currentUser));

        List<Cart> carts = cartService.getAllCarts(currentUser);

        logger.info("Retrieved all carts for %s.".formatted(currentUser));

        mav.addObject("carts", carts);
        mav.addObject("username",currentUser);
        mav.setViewName("home");
        mav.setStatus(CODE_200);
        
        return mav;
    }

    @GetMapping("cart/{cartId}")
    public ModelAndView getCart(
        @PathVariable String cartId, 
        HttpSession sess) 
    {
        ModelAndView mav = new ModelAndView();

        String currentUser = (String) sess.getAttribute("username");

        // Try to get cart
        Optional<Cart> opt = cartService.getCart(currentUser, cartId);

        if (opt.isEmpty()) {
            logger.info("Unsuccessful retrieval. Cart %s does not exist for %s".formatted(cartId, currentUser));

            mav.setViewName("cart_notfound");
            mav.setStatus(CODE_404);
            mav.addObject("cartId", cartId);

            return mav;
        }

        // Get cart
        Cart cart = opt.get();
        
        logger.info("Retrieved Cart %s for %s".formatted(cartId, currentUser));

        mav.addObject("cart", cart);
        mav.addObject("item", new Item());
        mav.setViewName("cart");
        mav.setStatus(CODE_200);

        return mav;
    }

    @PostMapping("/additem")
    public ModelAndView addItem(
        @Valid Item item, 
        BindingResult bindings,
        HttpSession sess,
        @RequestBody MultiValueMap<String, String> form)     
    {
        ModelAndView mav = new ModelAndView();

        // Get current user from session
        String currentUser = (String) sess.getAttribute("username");
        
        // Get hidden field
        String cartId = form.getFirst("cartId");

        // Get item name and quantity
        String itemName = item.getName();
        int itemQuantity = item.getQuantity();

        logger.info("Adding item (Name: %s, Qty: %d) into cart %s (%s)".formatted(itemName, itemQuantity, cartId, currentUser));

        if (bindings.hasErrors()) {

            logger.info("Invalid input in form for adding item");

            // try to get cart
            Optional<Cart> opt = cartService.getCart(currentUser, cartId);

            if (opt.isEmpty()) {

                logger.info("Unsuccessful retrieval. Cart %s does not exist for %s".formatted(cartId, currentUser));

                mav.setViewName("cart_notfound");
                mav.addObject("cartId", cartId);
                mav.setStatus(CODE_404);

                return mav;
            }

            // Get cart
            Cart cart = opt.get();
            
            logger.info("Retrieved Cart %s for %s".formatted(cartId, currentUser));

            mav.setViewName("cart");
            mav.addObject("cart", cart);
            mav.setStatus(CODE_404);

            return mav;
        }

        cartService.addItem(currentUser, cartId, itemName, itemQuantity);

        logger.info("Successfully added item (Name: %s, Qty: %d) into cart %s (%s)".formatted(itemName, itemQuantity, cartId, currentUser));

        // try to get cart
        Optional<Cart> opt = cartService.getCart(currentUser, cartId);

        if (opt.isEmpty()) {

            logger.info("Unsuccessful retrieval. Cart %s does not exist for %s".formatted(cartId, currentUser));

            mav.setViewName("cart_notfound");
            mav.setStatus(CODE_404);
            mav.addObject("cartId", cartId);

            return mav;
        }

        // Get cart
        Cart cart = opt.get();
        
        logger.info("Retrieved Cart %s for %s".formatted(cartId, currentUser));

        mav.setViewName("cart");
        mav.addObject("cart", cart);
        mav.setStatus(CODE_200);

        return mav;
    }

    @PostMapping("/deleteitem")
    public ModelAndView deleteItem(
        HttpSession sess,
        @RequestBody MultiValueMap<String, String> form)     
    {
        ModelAndView mav = new ModelAndView();

        // Get current user from session
        String currentUser = (String) sess.getAttribute("username");
        
        // Get hidden field
        String cartId = form.getFirst("cartId");
        String itemName = form.getFirst("itemName");

        logger.info("Deleting item (Name: %s) from cart %s (%s)".formatted(itemName, cartId, currentUser));

        cartService.deleteItem(currentUser, cartId, itemName);

        logger.info("Successfully deleted item (Name: %s) from cart %s (%s)".formatted(itemName, cartId, currentUser));

            // try to get cart
            Optional<Cart> opt = cartService.getCart(currentUser, cartId);

            if (opt.isEmpty()) {
    
                logger.info("Unsuccessful retrieval. Cart %s does not exist for %s".formatted(cartId, currentUser));
    
                mav.setViewName("cart_notfound");
                mav.setStatus(CODE_404);
                mav.addObject("cartId", cartId);
    
                return mav;
            }
            // Get cart
            Cart cart = opt.get();
            
            logger.info("Retrieved Cart %s for %s".formatted(cartId, currentUser));
    
            mav.addObject("cart", cart);
            mav.addObject("item", new Item());
            mav.setViewName("cart");
            mav.setStatus(CODE_200);
    
            return mav;
    }

    
}
