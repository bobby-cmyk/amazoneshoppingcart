package vttp.batch5.ssf.shoppingcart.repositories;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.batch5.ssf.shoppingcart.models.Cart;

@Repository
public class CartRepository {
    
    // Dependency Inject RedisTemplate into CartRepository
    @Autowired @Qualifier("redis-object")
    private RedisTemplate<String, Object> template;

    // exists username
    public boolean authUser(String username) {
        // Check if username exists
        return template.hasKey(username);
    }

    // hset username cartId cart
    public void insertCart(String username, String cartId, Cart cart) {
        
        template.opsForHash().put(username, cartId, cart);
    }

    // hget username cartId
    public Optional<Cart> getCart(String username, String cartId) {

        if (username.isEmpty() || cartId.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((Cart)template.opsForHash().get(username, cartId));
    }

    // hvals username
    public List<Cart> getAllCarts(String username) {
        List<Cart> carts = template.opsForHash().values(username).stream()
            .map(object -> (Cart) object)
            .collect(Collectors.toList());
        return carts;
    } 

    // hdel username cartId
    public void deleteCart(String username, String cartId) {
        template.opsForHash().delete(username, cartId);
    }
}
