package server.model.objects;

import server.Server;

public class GameObject {

	private final int objectId;
	private final int objectX;
	private final int objectY;
	private final int height;
	private final int face;
	private final int type;
	private final int newId;
	private final int tick;

	public GameObject(int id, int x, int y, int height, int face, int type, int newId, int ticks) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.height = height;
		this.face = face;
		this.type = type;
		this.newId = newId;
		this.tick = ticks;
		Server.objectManager.addObject(this);
	}

	public int getObjectId() {
		return objectId;
	}

	public int getObjectX() {
		return objectX;
	}

	public int getObjectY() {
		return objectY;
	}

	public int getHeight() {
		return height;
	}

	public int getFace() {
		return face;
	}

	public int getType() {
		return type;
	}

	public int getNewId() {
		return newId;
	}

	public int getTick() {
		return tick;
	}
}
