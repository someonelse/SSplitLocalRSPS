// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

import java.util.Random;

public class Entity extends Animable {

	public final void setPos(int i, int j, boolean flag) {
		if (anim != -1 && Animation.anims[anim].anInt364 == 1)
			anim = -1;

		if (!flag) {
			int dx = i - smallX[0];
			int dy = j - smallY[0];
			if (dx >= -8 && dx <= 8 && dy >= -8 && dy <= 8) {
				if (smallXYIndex < 9)
					smallXYIndex++;
				for (int idx = smallXYIndex; idx > 0; idx--) {
					smallX[idx] = smallX[idx - 1];
					smallY[idx] = smallY[idx - 1];
					aBooleanArray1553[idx] = aBooleanArray1553[idx - 1];
				}
				smallX[0] = i;
				smallY[0] = j;
				aBooleanArray1553[0] = false;
				return;
			}
		}

		smallXYIndex = 0;
		anInt1542 = 0;
		anInt1503 = 0;
		smallX[0] = i;
		smallY[0] = j;
		x = i * 128 + anInt1540 * 64;
		y = j * 128 + anInt1540 * 64;
	}

	public final void method446() {
		smallXYIndex = 0;
		anInt1542 = 0;
	}

	public final void updateHitData(int type, int damage, int cycle) {
		for (int i = 0; i < 4; i++) {
			if (hitsLoopCycle[i] <= cycle) {
				hitArray[i] = damage * ((client.newDamage && damage > 0) ? 10 : 1);
				if (client.newDamage && damage > 0) {
					hitArray[i] += new Random().nextInt(9);
				}
				hitMarkTypes[i] = type;
				hitsLoopCycle[i] = cycle + 70;
				return;
			}
		}
	}

	public final void moveInDir(boolean flag, int direction) {
		int j = smallX[0];
		int k = smallY[0];

		switch (direction) {
			case 0 -> {
				j--;
				k++;
			}
			case 1 -> k++;
			case 2 -> {
				j++;
				k++;
			}
			case 3 -> j--;
			case 4 -> j++;
			case 5 -> {
				j--;
				k--;
			}
			case 6 -> k--;
			case 7 -> {
				j++;
				k--;
			}
		}

		if (anim != -1 && Animation.anims[anim].anInt364 == 1)
			anim = -1;

		if (smallXYIndex < 9)
			smallXYIndex++;

		for (int i = smallXYIndex; i > 0; i--) {
			smallX[i] = smallX[i - 1];
			smallY[i] = smallY[i - 1];
			aBooleanArray1553[i] = aBooleanArray1553[i - 1];
		}
		smallX[0] = j;
		smallY[0] = k;
		aBooleanArray1553[0] = flag;
	}

	public int entScreenX;
	public int entScreenY;
	public final int index = -1;

	public boolean isVisible() {
		return false;
	}

	public Entity() {
		smallX = new int[10];
		smallY = new int[10];
		interactingEntity = -1;
		anInt1504 = 32;
		anInt1505 = -1;
		height = 200;
		anInt1511 = -1;
		anInt1512 = -1;
		hitArray = new int[4];
		hitMarkTypes = new int[4];
		hitsLoopCycle = new int[4];
		anInt1517 = -1;
		anInt1520 = -1;
		anim = -1;
		loopCycleStatus = -1000;
		textCycle = 100;
		anInt1540 = 1;
		aBoolean1541 = false;
		aBooleanArray1553 = new boolean[10];
		anInt1554 = -1;
		anInt1555 = -1;
		anInt1556 = -1;
		anInt1557 = -1;
	}

	public final int[] smallX;
	public final int[] smallY;
	public int interactingEntity;
	int anInt1503;
	int anInt1504;
	int anInt1505;
	public String textSpoken;
	public int height;
	public int turnDirection;
	int anInt1511;
	int anInt1512;
	int anInt1513;
	final int[] hitArray;
	final int[] hitMarkTypes;
	final int[] hitsLoopCycle;
	int anInt1517;
	int anInt1518;
	int anInt1519;
	int anInt1520;
	int anInt1521;
	int anInt1522;
	int anInt1523;
	int anInt1524;
	int smallXYIndex;
	public int anim;
	int anInt1527;
	int anInt1528;
	int anInt1529;
	int anInt1530;
	int anInt1531;
	public int loopCycleStatus;
	public int currentHealth;
	public int maxHealth;
	int textCycle;
	int anInt1537;
	int anInt1538;
	int anInt1539;
	int anInt1540;
	boolean aBoolean1541;
	int anInt1542;
	int anInt1543;
	int anInt1544;
	int anInt1545;
	int anInt1546;
	int anInt1547;
	int anInt1548;
	int anInt1549;
	public int x;
	public int y;
	int anInt1552;
	final boolean[] aBooleanArray1553;
	int anInt1554;
	int anInt1555;
	int anInt1556;
	int anInt1557;
}
