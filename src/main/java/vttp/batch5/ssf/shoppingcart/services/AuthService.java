package vttp.batch5.ssf.shoppingcart.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.batch5.ssf.shoppingcart.repositories.CartRepository;

@Service
public class AuthService {
    
    @Autowired
    private CartRepository cartRepo;

    public boolean authUser(String username) {
        return cartRepo.authUser(username);
    }
}
