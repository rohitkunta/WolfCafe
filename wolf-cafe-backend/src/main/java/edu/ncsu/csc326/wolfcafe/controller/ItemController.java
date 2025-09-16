package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.service.ItemService;
import lombok.AllArgsConstructor;

/**
 * Controller for API endpoints for an Item
 */
@RestController
@RequestMapping ( "api/items" )
@AllArgsConstructor
@CrossOrigin ( "*" )
public class ItemController {

    /** Link to ItemService */
    @Autowired
    private final ItemService itemService;

    /**
     * Adds an item to the list of items. Requires the STAFF role.
     *
     * @param itemDto
     *            item to add
     * @return added item
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'CUSTOMER', 'ADMIN')" )
    @PostMapping
    public ResponseEntity<ItemDto> addItem ( @RequestBody final ItemDto itemDto ) {
        if ( itemService.isDuplicateName( itemDto.getName() ) ) {
            return new ResponseEntity<>( itemDto, HttpStatus.CONFLICT );
        }
        if ( itemService.getAllItems().size() < 3 ) {
            final ItemDto savedItem = itemService.addItem( itemDto );
            return new ResponseEntity<>( savedItem, HttpStatus.CREATED );
        }
        else {
            return new ResponseEntity<>( itemDto, HttpStatus.INSUFFICIENT_STORAGE );
        }
    }

    /**
     * Gets an item by id. Requires the STAFF or CUSTOMER role.
     *
     * @param id
     *            item id
     * @return item with the id
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'CUSTOMER', 'ADMIN')" )
    @GetMapping ( "{id}" )
    public ResponseEntity<ItemDto> getItem ( @PathVariable ( "id" ) final Long id ) {
        final ItemDto item = itemService.getItemById( id );
        return ResponseEntity.ok( item );
    }

    /**
     * Returns all items. Requires the STAFF or CUSTOMER role.
     *
     * @return a list of all items
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'CUSTOMER', 'ADMIN')" )
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems () {
        final List<ItemDto> items = itemService.getAllItems();
        return ResponseEntity.ok( items );
    }

    /**
     * Updates the item with the given id. Requires STAFF role.
     *
     * @param id
     *            item to update
     * @param itemDto
     *            information about the item to update
     * @return updated item
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @PutMapping ( "{id}" )
    public ResponseEntity<ItemDto> updateItem ( @PathVariable ( "id" ) final Long id,
            @RequestBody final ItemDto itemDto ) {
        final ItemDto updatedItem = itemService.updateItem( id, itemDto );
        return ResponseEntity.ok( updatedItem );
    }

    /**
     * Deletes the item with the given id. Requires the STAFF role.
     *
     * @param id
     *            item to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasAnyRole('STAFF', 'ADMIN')" )
    @DeleteMapping ( "{id}" )
    public ResponseEntity<String> deleteItem ( @PathVariable ( "id" ) final Long id ) {
        itemService.deleteItem( id );
        return ResponseEntity.ok( "Item deleted successfully" );
    }
}
