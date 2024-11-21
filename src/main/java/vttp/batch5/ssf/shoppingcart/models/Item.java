package vttp.batch5.ssf.shoppingcart.models;

import java.io.Serializable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class Item implements Serializable{

    private static final long serialVersionUID = 1L;

    @NotEmpty(message="Item name cannot be empty")
    @NotNull(message="Item name cannot be empty")
    private String name;

    @NotNull(message="Quantity cannot be empty")
    @Min(value=1, message="Quantity cannot be less than 1")
    @Max(value=999, message="Quantity cannot be more than 999")
    private int quantity;

    public Item(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public Item() {

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
