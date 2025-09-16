package edu.ncsu.csc326.wolfcafe.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.entity.Inventory;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.mapper.InventoryMapper;
import edu.ncsu.csc326.wolfcafe.repository.InventoryRepository;
import edu.ncsu.csc326.wolfcafe.service.MakeItemService;

/**
 * Implementation of the MakeItemService interface.
 *
 * Now, makeItem() should work
 */
@Service
public class MakeItemServiceImpl implements MakeItemService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper         modelMapper;

    /**
     * Removes the ingredients used to make the specified item. Assumes that the
     * user has checked that there are enough ingredients to make
     *
     * @param inventoryDto
     *            current inventory
     * @param itemDto
     *            item to make
     * @return updated inventory
     */
    @Override
    public boolean makeItem ( final InventoryDto inventoryDto, final ItemDto itemDto ) {
        final Inventory inventory = InventoryMapper.mapToInventory( inventoryDto );
        final Item item = modelMapper.map( itemDto, Item.class );

        // need to change this part to ensure it deducts the ingredients from
        // inventory correctly.

        // more specifically, we need to find a way to deduct(set) each
        // ingredient from the item to the correct amount for the
        // corresponding ingredient in inventory

        // we could do this through A method in Inventory and InventoryDto which
        // sets a specific ingredients amount to the given value

        // we can call this method setIngredientAmount(final int amount)

        // Remember, we have to set all the ingredients from item in inventory
        // to the correct amount.

        // we can do this by a for-loop which runs through all ingredients in
        // item and sets the correct amount in the inventory for each
        // ingredient using the method

        // for i in range(r.getIngredients().size()):
        // {setIngredientAmount(amount)}
        if ( enoughIngredients( inventory, item ) ) {

            /// continue making this
            for ( int i = 0; i < item.getIngredients().size(); i++ ) {

                // equivalent to inventory.setMilk( inventory.getMilk() -
                // item.getMilk() ); but with generic ingredients
                inventory.setIngredientAmount( item.getIngredients().get( i ).getName(),
                        inventory.amountOfIngredient( item.getIngredients().get( i ).getName() )
                                - item.getIngredients().get( i ).getAmount() );

            }

            inventoryRepository.save( inventory );
            return true;
        }

        return false;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param inventory
     *            coffee maker inventory
     * @param item
     *            item to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    private boolean enoughIngredients ( final Inventory inventory, final Item item ) {
        boolean isEnough = true;

        // need to change the check for enough ingredients to work with the new
        // Ingredient and Inventory design, which contains a list of
        // ingredients.

        // create a for-loop to check every ingredient in item, and
        // then check if that ingredient is in inventory, and if it is, check if
        // a sufficient amount is in inventory.

        // this should work to check the ingredients amount
        for ( int i = 0; i < item.getIngredients().size(); i++ ) {

            if ( inventory.amountOfIngredient( item.getIngredients().get( i ).getName() ) < item.getIngredients()
                    .get( i ).getAmount() ) {
                isEnough = false;
            }
        }

        return isEnough;
    }

}
