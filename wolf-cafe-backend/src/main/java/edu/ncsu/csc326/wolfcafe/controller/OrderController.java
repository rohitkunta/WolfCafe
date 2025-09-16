/**
 *
 */
package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import lombok.AllArgsConstructor;

/**
 * Controller for API endpoints for an Order
 *
 * @author Rohit Kunta
 */
@RestController
@RequestMapping ( "api/orders" )
@AllArgsConstructor
@CrossOrigin ( "*" )
public class OrderController {

    /** Link to OrderService */
    @Autowired
    private final OrderService orderService;

    /**
     * Creates/places an order. Creates an Order object. Any role can do this
     *
     * @param orderDto
     *            the information of the order being placed
     * @return a order the orderDto of the Order we just created
     */
    @PostMapping
    public ResponseEntity<OrderDto> placeOrder ( @RequestBody final OrderDto orderDto ) {
        final OrderDto order = orderService.placeOrder( orderDto );
        return new ResponseEntity<>( order, HttpStatus.CREATED );
    }

    /**
     * changes the status of an order to COMPLETE, from READY_TO_PICKUP. any
     * role can do this.
     *
     * @param orderDto
     *            the order which status is being changed to COMPLETE
     * @return string containing a success/error message of the picking up the
     *         order
     */
    @PutMapping ( "/pickup/{id}" )
    public ResponseEntity<String> pickupOrder ( @PathVariable ( "id" ) final Long id ) {
        final String message = orderService.pickupOrder( id );
        return new ResponseEntity<>( message, HttpStatus.OK );
    }

    /**
     * Returns a list of all the Orders. Have to be an Admin or Staff role to do
     * this.
     *
     * @return list of all the orders that have been created/placed.
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @GetMapping
    public ResponseEntity<List<OrderDto>> viewOrders () {
        final List<OrderDto> orders = orderService.viewOrders();
        return new ResponseEntity<>( orders, HttpStatus.OK );
    }

    /**
     * Return the specified Order by Id
     *
     * @param id
     *            the id of the order.
     * @return the ReponseEntity containing OrderDto of the order, with status
     *         200 OK.
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @GetMapping ( "{id}" )
    public ResponseEntity<OrderDto> viewOrder ( @PathVariable ( "id" ) final Long id ) {
        final OrderDto order = orderService.viewOrder( id );
        return new ResponseEntity<>( order, HttpStatus.OK );
    }

    /**
     * changes the status of an order to READY_FOR_PICKUP
     *
     * @param orderDto
     *            the order that was just made
     * @return string containing a success/error message of the fulfilling the
     *         order
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @PutMapping ( "/fulfill/{id}" )
    public ResponseEntity<String> fulfillOrder ( @PathVariable ( "id" ) final Long id ) {
        final String message = orderService.fulfillOrder( id );
        return new ResponseEntity<>( message, HttpStatus.OK );
    }

}
