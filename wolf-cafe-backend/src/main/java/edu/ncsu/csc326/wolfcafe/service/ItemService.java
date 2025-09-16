package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;

/**
 * Item service
 */
public interface ItemService {

    /**
     * Adds given item
     *
     * @param itemDto
     *            item to add
     * @return added item
     */
    ItemDto addItem ( ItemDto itemDto );

    /**
     * Gets item by id
     *
     * @param id
     *            id of item to get
     * @return returned item
     */
    ItemDto getItemById ( Long id );

    /**
     * Returns the item with the given name
     *
     * @param itemName
     *            item's name
     * @return the item with the given name.
     * @throws ResourceNotFoundException
     *             if the item doesn't exist
     */
    ItemDto getItemByName ( String itemName );

    /**
     * Returns all items
     *
     * @return all items
     */
    List<ItemDto> getAllItems ();

    /**
     * Returns true if the item already exists in the database.
     *
     * @param itemName
     *            item's name to check
     * @return true if already in the database
     */
    public boolean isDuplicateName ( final String itemName );

    /**
     * Updates the item with the given id
     *
     * @param id
     *            id of item to update
     * @param itemDto
     *            information of item to update
     * @return updated item
     */
    ItemDto updateItem ( Long id, ItemDto itemDto );

    /**
     * Deletes the item with the given id
     *
     * @param id
     *            id of item to delete
     */
    void deleteItem ( Long id );
}
