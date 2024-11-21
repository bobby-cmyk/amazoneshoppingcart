package vttp.batch5.ssf.shoppingcart.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable{

    private static final long serialVersionUID = 1L;
    
    private String username;
    private String id;
    private List<Item> items;

    public Cart() {
        items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addItem(String itemName, int itemQuantity) {

        // Check if item already exists, if it exists, add the quantity to current quantity
        for (Item item : items) {
            if (item.getName().equals(itemName)) {
                item.setQuantity(item.getQuantity() + itemQuantity);
                return;
            }
        }
        // If the item does not exist, add new item and its quantity
        items.add(new Item(itemName, itemQuantity));
    }

    public void deleteItem(String itemName) {
        items.removeIf(item -> item.getName().equals(itemName));
    }

    public List<Item> getItems() {
        return items;
    }

    public int getNumberOfItems() {
        int totalQuantity = 0;
        for (Item item : items) {
            totalQuantity += item.getQuantity();
        }

        return totalQuantity;
    }
}