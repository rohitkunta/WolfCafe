/**
 *
 */
package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;

/**
 * Order Service interface, outlines methods for creating and modifying Order
 * objects
 *
 * @author Rohit Kunta
 */
public interface OrderService {

    /**
     * Creates/places an order. Creates an Order object.
     *
     * @param orderDto
     *            the information of the order being placed
     * @return a savedOrder the order we just created
     */
    OrderDto placeOrder ( OrderDto orderDto );

    /**
     * changes the status of an order to COMPLETE, from READY_TO_PICKUP
     *
     * @param orderDto
     *            the order which status is being changed to COMPLETE
     * @return string containing a success/error message of the picking up the
     *         order
     */
    String pickupOrder ( Long id );

    /**
     * Returns a list of all the Orders.
     *
     * @return list of all the orders that have been created/placed.
     */
    List<OrderDto> viewOrders ();

    /**
     * Return the specified Order by Id
     *
     * @param id
     *            the id of the order.
     * @return the OrderDto of the order.
     */
    OrderDto viewOrder ( Long id );

    /**
     * changes the status of an order to READY_FOR_PICKUP
     *
     * @param orderDto
     *            the order that was just made
     * @return string containing a success/error message of the fulfilling the
     *         order
     */
    String fulfillOrder ( Long id );

}
