package edu.ncsu.csc326.wolfcafe.dto;

public class IngredientDto {

    private Long    id;

    private String  name;

    private String  units;

    private Integer amount;

    public IngredientDto () {

    }

    public IngredientDto ( final String name, final Integer amount ) {
        this.name = name;
        this.amount = amount;
    }

    public IngredientDto ( final String name, final Integer amount, final String units ) {
        this.name = name;
        this.amount = amount;
        this.units = units;
    }

    /**
     * @return the units
     */
    public String getUnits () {
        return units;
    }

    /**
     * @param units
     *            the units to set
     */
    public void setUnits ( final String units ) {
        this.units = units;
    }

    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName ( final String name ) {
        this.name = name;
    }

    public Integer getAmount () {
        return amount;
    }

    public void setAmount ( final Integer amount ) {
        this.amount = amount;
    }
}
