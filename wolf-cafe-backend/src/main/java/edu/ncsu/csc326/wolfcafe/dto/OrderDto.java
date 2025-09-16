/**
 *
 */
package edu.ncsu.csc326.wolfcafe.dto;

import java.util.ArrayList;
import java.util.List;

import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Order object for data transfer.
 *
 * @author Rohit Kunta
 */
@Getter
@Setter
@AllArgsConstructor
public class OrderDto {

    private Long         id;
    private String       customerUsername;
    /**
     * This is subtotal without tax
     */
    private double       totalPrice;
    private double       amountPaid;
    private List<String> itemsOrdered;
    private double       tipAmountDollars;
    /**
     * This is taxrate as a whole number, not a percent.
     *
     * e.g. taxrate should be assigned 0.02 for a 2% taxRate.
     */
    private double       taxRate;
    private OrderStatus  status;

    /**
     * NO arg constructor, specifies itemsOrdered list.
     */
    public OrderDto () {
        itemsOrdered = new ArrayList<String>();
    }

}
