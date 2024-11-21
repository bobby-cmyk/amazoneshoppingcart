package vttp.batch5.ssf.shoppingcart.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.batch5.ssf.shoppingcart.models.Cart;
import vttp.batch5.ssf.shoppingcart.repositories.CartRepository;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepo;

    public String createCart(String username) {

        Cart cart = new Cart();
        String cartId = UUID.randomUUID().toString().substring(0, 8);
        cart.setId(cartId);
        cart.setUsername(username);

        cartRepo.insertCart(username, cartId, cart);

        return cartId;
    }

    public Optional<Cart> getCart(String username, String cartId) {
        return cartRepo.getCart(username, cartId);
    }


    public List<Cart> getAllCarts(String username) {
        return cartRepo.getAllCarts(username);
    }

    public void addItem(String username, String cartId, String itemName, int itemQuantity) {

        Optional<Cart> opt = cartRepo.getCart(username, cartId);
        Cart cart = opt.get();

        cart.addItem(itemName, itemQuantity);

        cartRepo.insertCart(username, cartId, cart);
    }

    public void deleteItem(String username, String cartId, String itemName) {
        Optional<Cart> opt = cartRepo.getCart(username, cartId);
        Cart cart = opt.get();

        cart.deleteItem(itemName);

        cartRepo.insertCart(username, cartId, cart);
    }

    public void deleteCart(String username, String cartId) {
        cartRepo.deleteCart(username, cartId);
    }
}
