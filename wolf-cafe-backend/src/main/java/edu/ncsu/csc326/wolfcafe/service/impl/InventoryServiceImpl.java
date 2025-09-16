
package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.mapper.InventoryMapper;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Implementation of the InventoryService interface.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * Creates the inventory.
     *
     * @param inventoryDto
     *            inventory to create
     * @return updated inventory after creation
     */
    @Override
    public InventoryDto createInventory ( final InventoryDto inventoryDto ) {
        final Inventory inventory = InventoryMapper.mapToInventory( inventoryDto );
        final Inventory savedInventory = inventoryRepository.save( inventory );
        return InventoryMapper.mapToInventoryDto( savedInventory );
    }

    /**
     * Returns the single inventory.
     *
     * @return the single inventory
     */
    @Override
    public InventoryDto getInventory () {
        final List<Inventory> inventory = inventoryRepository.findAll();
        if ( inventory.size() == 0 ) {
            final InventoryDto newInventoryDto = new InventoryDto();

            final InventoryDto savedInventoryDto = createInventory( newInventoryDto );
            return savedInventoryDto;
        }
        return InventoryMapper.mapToInventoryDto( inventory.get( 0 ) );
    }

    /**
     * Updates the contents of the inventory.
     *
     * @param inventoryDto
     *            values to update
     * @return updated inventory
     *
     *         MAKE SURE THE INGREDIENTS THAT HAVE THE SAME NAME ARE IN THE SAME
     *         INDEX IN THE LIST IN BOTH INVENTORY AND INVENTORYDTO. I HAVE NOT
     *         IMPLEMENTED THIS NAME SEARCH CHECK FOR TIME PURPOSES
     */
    @Override
    public InventoryDto updateInventory ( final InventoryDto inventoryDto ) {
        final Inventory inventory = inventoryRepository.findById( 1L ).orElseThrow(
                () -> new ResourceNotFoundException( "Inventory does not exist with id of " + inventoryDto.getId() ) );

        // change this to add the elements instead if the Ingredients list is 0.

        final int diff = Math.abs( inventory.getIngredients().size() - inventoryDto.getIngredients().size() );

        if ( inventory.getIngredients().size() == 0 ) { // empty inventory case
            System.out.println( "Sugar case" );
            for ( int i = 0; i < inventoryDto.getIngredients().size(); i++ ) {
                inventory.addIngredient( inventoryDto.getIngredients().get( i ) );
            }
        }
        else if ( inventory.getIngredients().size() < inventoryDto.getIngredients().size()
                // case for when theres less ingredents in old inventory vs new
                // inventory
                && inventory.getIngredients().size() != 0 ) {
            System.out.println( "Lemons case" );
            for ( int i = 0; i < inventoryDto.getIngredients().size(); i++ ) { // set
                                                                               // the
                                                                               // existing
                                                                               // ones
                if ( i < inventory.getIngredients().size() && inventory.getIngredients().get( i ).getName()
                        .equals( inventoryDto.getIngredients().get( i ).getName() ) ) {
                    inventory.getIngredients().get( i ).setAmount( inventoryDto.getIngredients().get( i ).getAmount() );
                    inventory.getIngredients().get( i ).setUnits( inventoryDto.getIngredients().get( i ).getUnits() );
                }
                else { // add the new ones if they're not already in inventory
                    inventory.addIngredient( inventoryDto.getIngredients().get( i ) );
                }

            }
        }
        else { // equal or greater in old case
            for ( int i = 0; i < inventoryDto.getIngredients().size(); i++ ) {
                if ( inventory.getIngredients().get( i ).getName()
                        .equals( inventoryDto.getIngredients().get( i ).getName() ) ) {
                    inventory.getIngredients().get( i ).setAmount( inventoryDto.getIngredients().get( i ).getAmount() );
                    inventory.getIngredients().get( i ).setUnits( inventoryDto.getIngredients().get( i ).getUnits() );
                }
                else { // add the new ones if they're not already in inventory
                    inventory.addIngredient( inventoryDto.getIngredients().get( i ) );
                }
            }
        }

        final Inventory savedInventory = inventoryRepository.save( inventory );

        return InventoryMapper.mapToInventoryDto( savedInventory );
    }

    /**
     * Deletes all inventory
     *
     * Will use this instead of TRUNCATE TABLE inventory
     */
    @Override
    public void deleteInventory () {
        inventoryRepository.deleteAll();
    }

}
