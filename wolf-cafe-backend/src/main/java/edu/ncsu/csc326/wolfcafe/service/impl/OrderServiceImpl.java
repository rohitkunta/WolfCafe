/**
 *
 */
package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderStatusRepository;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.MakeItemService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import edu.ncsu.csc326.wolfcafe.service.TaxRateService;
import lombok.AllArgsConstructor;

/**
 * Implemented OrderService interface
 *
 * @author Rohit Kunta
 */
@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    private final OrderRepository       orderRepository;

    @Autowired
    private final ItemRepository        itemRepository;

    @Autowired
    private final ModelMapper           modelMapper;
    
    @Autowired
    private final MakeItemService makeItemService;
    
    @Autowired
    private final InventoryService inventoryService;

    /**
     * Contains all possible OrderStatuses.
     *
     * id=1, name: PENDING id=2, name: READY_FOR_PICKUP id=3, name: COMPLETE
     * id=4, name: INVALID
     */
    @Autowired
    private final OrderStatusRepository orderStatusRepository;

    // add a taxRateRepository variable here to pull the tax rate from when
    // implementing UC11.
    @Autowired
    private final TaxRateService taxRateService;

    /**
     * Creates/places an order. Creates an Order object. initializes totalPrice
     * and OrderStatus fields.
     *
     * @param orderDto
     *            the information of the order being placed
     * @return a savedOrder the order we just created. Order status if
     *         successful is PENDING, but if not successful, it is INVALID.
     *
     */
    @Override
    public OrderDto placeOrder ( final OrderDto orderDto ) {

        final Order order = modelMapper.map( orderDto, Order.class );

        if ("guest".equalsIgnoreCase(orderDto.getCustomerUsername())) {
            order.setCustomerUsername("guest");
        }

        
        double totalprice = 0;

        Item item;

        // set the taxRate here by pulling the taxRate from the
        // TaxRateRepository

        try { // sums the price of all items, makes sure they all exist.
            for ( int i = 0; i < orderDto.getItemsOrdered().size(); i++ ) {
                item = itemRepository.findByName( orderDto.getItemsOrdered().get( i ) )
                        .orElseThrow( () -> new ResourceNotFoundException( "Item not found" ) );
                totalprice += item.getPrice();
            }
            double taxRate = taxRateService.getCurrentTaxRate();
            double taxAdded = totalprice * taxRate;
            totalprice += taxAdded;
        }
        catch ( final Exception e ) { // sets to INVALID if something is wrong
            OrderStatus newStatus;

            // add the status into the OrderStatusRepository if it does not
            // already
            // exist
            if ( orderStatusRepository.findByName( "INVALID" ) == null ) {
                orderStatusRepository.save( new OrderStatus( 4L, "INVALID" ) );
                newStatus = orderStatusRepository.findByName( "INVALID" );
            }
            else {
                newStatus = orderStatusRepository.findByName( "INVALID" );
            }

            order.setStatus( newStatus );

            // complete order object with all fields initialized to correct
            // values.
            final Order savedOrderInvalid = orderRepository.save( order );

            return modelMapper.map( savedOrderInvalid, OrderDto.class );
        }

        order.setTotalPrice( totalprice );

        OrderStatus newStatus;

        // add the status into the OrderStatusRepository if it does not already
        // exist
        if ( orderStatusRepository.findByName( "PENDING" ) == null ) {
            orderStatusRepository.save( new OrderStatus( 1L, "PENDING" ) );
            newStatus = orderStatusRepository.findByName( "PENDING" );
        }
        else {
            newStatus = orderStatusRepository.findByName( "PENDING" );
        }

        order.setStatus( newStatus );

        // complete order object with all fields initialized to correct values.
        final Order savedOrder = orderRepository.save( order );

        // return complete OrderDto, with all fields initialized
        return modelMapper.map( savedOrder, OrderDto.class );
    }

    /**
     * changes the status of an order to COMPLETE, from READY_TO_PICKUP
     *
     * @param orderDto
     *            the order which status is being changed to COMPLETE
     * @param id
     *            the id of the order that's status is being changed to COMPLETE
     * @return string containing a success/error message of the picking up the
     *         order
     */
    @Override
    public String pickupOrder ( final Long id ) {

        final Order order = orderRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with id " + id ) );

        OrderStatus newStatus;

        if ( orderStatusRepository.findByName( "COMPLETE" ) == null ) {
            orderStatusRepository.save( new OrderStatus( 3L, "COMPLETE" ) );
            newStatus = orderStatusRepository.findByName( "COMPLETE" );
        }
        else {
            newStatus = orderStatusRepository.findByName( "COMPLETE" );
        }

        order.setStatus( newStatus );

        orderRepository.save( order );

        return "Order picked up successfully, order completed.";
    }

    /**
     * Returns a list of all the Orders.
     *
     * @return list of all the orders that have been created/placed.
     */
    @Override
    public List<OrderDto> viewOrders () {

        final List<Order> orders = orderRepository.findAll();

        return orders.stream().map( ( order ) -> modelMapper.map( order, OrderDto.class ) )
                .collect( Collectors.toList() );
    }

    /**
     * Return the specified Order by Id
     *
     * @param id
     *            the id of the order.
     * @return the OrderDto of the order.
     */
    @Override
    public OrderDto viewOrder ( final Long id ) {
        final Order order = orderRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with id " + id ) );
        return modelMapper.map( order, OrderDto.class );
    }

    /**
     * changes the status of an order to READY_FOR_PICKUP
     *
     * @param orderDto
     *            the order that was just made
     * @param the
     *            id of the order that's status is being changed to
     *            READY_FOR_PICKUP
     * @return string containing a success/error message of the fulfilling the
     *         order
     */
    @Override
    public String fulfillOrder ( final Long id ) {
        final Order order = orderRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "Order not found with id " + id ) );

        OrderStatus newStatus;

        if ( orderStatusRepository.findByName( "READY_FOR_PICKUP" ) == null ) {
            orderStatusRepository.save( new OrderStatus( 2L, "READY_FOR_PICKUP" ) );
            newStatus = orderStatusRepository.findByName( "READY_FOR_PICKUP" );
        }
        else {
            newStatus = orderStatusRepository.findByName( "READY_FOR_PICKUP" );
        }

        order.setStatus( newStatus );

        orderRepository.save( order );
                
        Item item;
        
        for ( int i = 0; i < order.getItemsOrdered().size(); i++ ) {
            item = itemRepository.findByName( order.getItemsOrdered().get( i ) )
                    .orElseThrow( () -> new ResourceNotFoundException( "Item not found" ) );
            makeItemService.makeItem(inventoryService.getInventory(), modelMapper.map( item, ItemDto.class ));
        }

        return "Order now ready for pickup.";
    }

}
