package server.model.objects;

public class Objects {

	private final int objectId;
	private final int objectX;
	private final int objectY;
	private final int objectHeight;
	private final int objectFace;
	private final int objectType;
	private final int objectTicks;

	public Objects(int id, int x, int y, int height, int face, int type, int ticks) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.objectHeight = height;
		this.objectFace = face;
		this.objectType = type;
		this.objectTicks = ticks;
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

	public int getObjectHeight() {
		return objectHeight;
	}

	public int getObjectFace() {
		return objectFace;
	}

	public int getObjectType() {
		return objectType;
	}

	public int getObjectTicks() {
		return objectTicks;
	}
}
