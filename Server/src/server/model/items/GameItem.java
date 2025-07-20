package server.model.items;

public class GameItem {

	private final int id;
	private final int amount;
	private final boolean stackable;

	public GameItem(int id, int amount) {
		this.id = id;
		this.amount = amount;
		this.stackable = Item.itemStackable[id];
	}

	public int getId() {
		return id;
	}

	public int getAmount() {
		return amount;
	}

	public boolean isStackable() {
		return stackable;
	}
}
