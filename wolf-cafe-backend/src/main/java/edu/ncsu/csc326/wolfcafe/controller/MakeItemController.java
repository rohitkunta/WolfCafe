package edu.ncsu.csc326.wolfcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import edu.ncsu.csc326.wolfcafe.service.MakeItemService;

/**
 * MakeItemController provides the endpoint for making a item.
 *
 * MakeItemController is fine, nothing to change here for UC6.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/makeitem" )
public class MakeItemController {

    /** Connection to InventoryService */
    @Autowired
    private InventoryService inventoryService;

    /** Connection to ItemService */
    @Autowired
    private ItemService      itemService;

    /** Connection to MakeItemService */
    @Autowired
    private MakeItemService  makeItemService;

    /**
     * REST API method to make coffee by completing a POST request with the ID
     * of the item as the path variable and the amount that has been paid as the
     * body of the response
     *
     * @param name
     *            item name
     * @param amtPaid
     *            amount paid
     * @return The change the customer is due if successful
     */
    @PostMapping ( "{name}" )
    public ResponseEntity<Double> makeItem ( @PathVariable ( "name" ) final String itemName,
            @RequestBody final Double amtPaid ) {
        final ItemDto itemDto = itemService.getItemByName( itemName );

        final double change = makeItem( itemDto, amtPaid );
        if ( change == amtPaid ) {
            if ( amtPaid < itemDto.getPrice() ) {
                return new ResponseEntity<>( amtPaid, HttpStatus.CONFLICT );
            }
            else {
                return new ResponseEntity<>( amtPaid, HttpStatus.BAD_REQUEST );
            }
        }
        return ResponseEntity.ok( change );
    }

    /**
     * Helper method to make coffee
     *
     * @param toPurchase
     *            item that we want to make
     * @param amtPaid
     *            money that the user has given the machine
     * @return change if there was enough money to make the coffee, throws
     *         exceptions if not
     */
    private double makeItem ( final ItemDto toPurchase, final double amtPaid ) {
        double change = amtPaid;
        final InventoryDto inventoryDto = inventoryService.getInventory();

        if ( toPurchase.getPrice() <= amtPaid ) {
            if ( makeItemService.makeItem( inventoryDto, toPurchase ) ) {
                change = amtPaid - toPurchase.getPrice();
                return change;
            }
            else {
                // not enough inventory
                return change;
            }
        }
        else {
            // not enough money
            return change;
        }

    }

}
