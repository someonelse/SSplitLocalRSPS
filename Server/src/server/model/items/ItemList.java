package server.model.items;

import java.util.Arrays;

/**
 * Represents an item in the game with its attributes and bonuses.
 */
public class ItemList {

    public int itemId;
    public String itemName;
    public String itemDescription;
    public double shopValue;
    public double lowAlch;
    public double highAlch;
    public int[] bonuses = new int[100];

    /**
     * Constructs an item list entry with a specified item ID.
     * 
     * @param itemId the unique identifier of the item
     */
    public ItemList(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "ItemList{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", shopValue=" + shopValue +
                ", lowAlch=" + lowAlch +
                ", highAlch=" + highAlch +
                ", bonuses=" + Arrays.toString(Arrays.copyOf(bonuses, 10)) +
                '}';
    }
}
