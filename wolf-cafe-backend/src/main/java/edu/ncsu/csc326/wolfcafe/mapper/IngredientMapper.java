package edu.ncsu.csc326.wolfcafe.mapper;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;

public class IngredientMapper {

    public static IngredientDto mapToIngredientDto ( final Ingredient ingredient ) {
        final IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setId( ingredient.getId() );
        ingredientDto.setName( ingredient.getName() );
        ingredientDto.setAmount( ingredient.getAmount() );
        ingredientDto.setUnits( ingredient.getUnits() );
        return ingredientDto;
    }

    public static Ingredient mapToIngredient ( final IngredientDto ingredientDto ) {
        final Ingredient ingredient = new Ingredient();
        ingredient.setId( ingredientDto.getId() );
        ingredient.setName( ingredientDto.getName() );
        ingredient.setAmount( ingredientDto.getAmount() );
        ingredient.setUnits( ingredientDto.getUnits() );
        return ingredient;
    }

}
