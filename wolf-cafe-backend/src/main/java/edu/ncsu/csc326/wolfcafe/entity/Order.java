/**
 *
 */
package edu.ncsu.csc326.wolfcafe.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents an Order the customer can make
 *
 * @author Rohit Kunta
 */
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table ( name = "orders" )
public class Order {

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long         id;
    private String       customerUsername;

    /**
     * This is subtotal without tax
     */
    @Column ( nullable = false )
    private double       totalPrice;

    @Column ( nullable = false )
    private double       amountPaid;

    @ElementCollection ( fetch = FetchType.EAGER )
    @CollectionTable ( name = "order_items", joinColumns = @JoinColumn ( name = "order_id" ) )
    @Column ( name = "item_name", nullable = false )
    private List<String> itemsOrdered;

    @Column ( nullable = false )
    private double       tipAmountDollars;

    /**
     * This is taxrate as a whole number, not a percent.
     *
     * e.g. taxrate should be assigned 0.02 for a 2% taxRate.
     */
    @Column ( nullable = false )
    private double       taxRate;

    // its probably this that is causing this whole thing to crash.
    @ManyToOne ( fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    @JoinColumn ( name = "order_status_id" )
    private OrderStatus  status;

    /**
     * NO arg constructor, specifies itemsOrdered list.
     */
    public Order () {
        itemsOrdered = new ArrayList<String>();
    }

}
