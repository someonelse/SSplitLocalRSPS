package server.model.npcs;

public class NPCList {

	private final int npcId;
	private String npcName;
	private int npcCombat;
	private int npcHealth;

	public NPCList(int npcId) {
		this.npcId = npcId;
	}

	public NPCList(int npcId, String npcName, int npcCombat, int npcHealth) {
		this.npcId = npcId;
		this.npcName = npcName;
		this.npcCombat = npcCombat;
		this.npcHealth = npcHealth;
	}

	public int getNpcId() {
		return npcId;
	}

	public String getNpcName() {
		return npcName;
	}

	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}

	public int getNpcCombat() {
		return npcCombat;
	}

	public void setNpcCombat(int npcCombat) {
		this.npcCombat = npcCombat;
	}

	public int getNpcHealth() {
		return npcHealth;
	}

	public void setNpcHealth(int npcHealth) {
		this.npcHealth = npcHealth;
	}
}
