package server.model.players;

/**
 * @author Sanity
 */

public class PotionMixing {
	
	private Client c;
	
	public PotionMixing(Client c) {
		this.c = c;
	}
	
	public void mixPotion2(int id, int id2) {
		String id11 = c.getItems().getItemName(id);
		String id22 = c.getItems().getItemName(id2);
		if (id11.substring(0, id11.indexOf("(")).equalsIgnoreCase(id22.substring(0, id22.indexOf("(")))) {
			try {
				int amount1 = Integer.parseInt(id11.substring(id11.indexOf("(") + 1, id11.indexOf("(") + 2));
				int amount2 = Integer.parseInt(id22.substring(id22.indexOf("(") + 1, id22.indexOf("(") + 2));
				int totalAmount = amount1 + amount2;
				if (totalAmount > 4) { 
					amount1 = 4;
					amount2 = totalAmount - 4;
					String item1 = id11.substring(0, id11.indexOf("(") + 1) + amount1 + ")";
					String item2 = id11.substring(0, id11.indexOf("(") + 1) + amount2 + ")";
					c.getItems().deleteItem(id, c.getItems().getItemSlot(id), 1);
					c.getItems().deleteItem(id2, c.getItems().getItemSlot(id2), 1);
					c.getItems().addItem(c.getItems().getItemId(item1), 1);
					c.getItems().addItem(c.getItems().getItemId(item2), 1);
				} else {
					amount1 = totalAmount;
					String item1 = id11.substring(0, id11.indexOf("(") + 1) + amount1 + ")";
					c.getItems().deleteItem(id, c.getItems().getItemSlot(id), 1);
					c.getItems().deleteItem(id2, c.getItems().getItemSlot(id2), 1);
					c.getItems().addItem(c.getItems().getItemId(item1), 1);
					c.getItems().addItem(229, 1); // Add empty vial
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// use id, use id2, new id1, new id2
	private int[][] potMixing = {
		{117,117,113,227},{119,115,113,227},{115,119,113,227},{123,123,2428,227},
		{121,125,2428,227},{125,121,2428,227},{133,133,2432,227},{133,137,2432,227},
		{137,133,2432,227},{159,159,2440,227},{157,161,2440,227},{161,157,2440,227},
		{147,147,2436,227},{149,145,2436,227},{145,149,2436,227},{165,165,2442,227},
		{163,167,2442,227},{167,163,2442,227},{6689,6689,6685,227},{6687,6691,2442,227},
		{6691,6687,2442,227},{171,171,2444,227},{169,173,2444,227},{173,169,2444,227},
		{177,177,2446,227},{179,175,2446,227},{175,179,2446,227},{3028,3028,3024,227},
		{3030,3026,3024,227},{3026,3030,3024,227},{141,141,2434,227},{139,143,2434,227},
		{143,139,2434,227},{3044,3044,3040,227},{3042,3046,3040,227},{3046,3042,3040,227},
		{2456,2456,2452,227},{2454,2458,2452,227},{2458,2454,2452,227}
	};
	
	public void mixPotion(int id, int id2) {
		for (int j = 0; j < potMixing.length; j++) {
			if ((potMixing[j][0] == id && potMixing[j][1] == id2) || (potMixing[j][1] == id && potMixing[j][0] == id2)) {
				c.getItems().deleteItem(id, c.getItems().getItemSlot(id), 1);
				c.getItems().deleteItem(id2, c.getItems().getItemSlot(id2), 1);
				c.getItems().addItem(potMixing[j][2], 1);
				c.getItems().addItem(potMixing[j][3], 1);
				break;
			}
		}	
	}
}
