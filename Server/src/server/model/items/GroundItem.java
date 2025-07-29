package server.model.items;

public class GroundItem {

	private final int itemId;
	private final int itemX;
	private final int itemY;
	private final int itemAmount;
	private final int itemController;
	private final int hideTicks;
	private int removeTicks;
	private final String ownerName;

	public GroundItem(int id, int x, int y, int amount, int controller, int hideTicks, String name) {
		this.itemId = id;
		this.itemX = x;
		this.itemY = y;
		this.itemAmount = amount;
		this.itemController = controller;
		this.hideTicks = hideTicks;
		this.ownerName = name;
	}

	public int getItemId() {
		return itemId;
	}

	public int getItemX() {
		return itemX;
	}

	public int getItemY() {
		return itemY;
	}

	public int getItemAmount() {
		return itemAmount;
	}

	public int getItemController() {
		return itemController;
	}

	public int getHideTicks() {
		return hideTicks;
	}

	public int getRemoveTicks() {
		return removeTicks;
	}

	public void setRemoveTicks(int removeTicks) {
		this.removeTicks = removeTicks;
	}

	public String getName() {
		return ownerName;
	}
}
