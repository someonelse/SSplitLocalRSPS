package server.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import server.Server;
import server.model.objects.Objects;
import server.model.players.Client;
import server.model.players.Player;
import server.util.Misc;

public class ObjectHandler {

    public List<Objects> globalObjects = new ArrayList<>();

    public ObjectHandler() {
        loadGlobalObjects("./Data/cfg/global-objects.cfg");
        loadDoorConfig("./Data/cfg/doors.cfg");
    }

    public void addObject(Objects object) {
        globalObjects.add(object);
    }

    public void removeObject(Objects object) {
        globalObjects.remove(object);
    }

    public Objects objectExists(int objectX, int objectY, int objectHeight) {
        for (var o : globalObjects) {
            if (o.getObjectX() == objectX && o.getObjectY() == objectY && o.getObjectHeight() == objectHeight) {
                return o;
            }
        }
        return null;
    }

    public void updateObjects(Client c) {
        for (var o : globalObjects) {
            if (c != null && c.heightLevel == o.getObjectHeight() && o.objectTicks == 0) {
                if (c.distanceToPoint(o.getObjectX(), o.getObjectY()) <= 60) {
                    c.getPA().object(o.getObjectId(), o.getObjectX(), o.getObjectY(), o.getObjectFace(), o.getObjectType());
                }
            }
        }
        if (c.distanceToPoint(2813, 3463) <= 60) {
            c.getFarming().updateHerbPatch();
        }
        if (c.distanceToPoint(2961, 3389) <= 60) {
            c.getPA().object(6552, 2961, 3389, -1, 10);
        }
    }

    public void placeObject(Objects o) {
        for (var p : Server.playerHandler.players) {
            if (p != null) {
                var person = (Client) p;
                if (person != null && person.heightLevel == o.getObjectHeight() && o.objectTicks == 0) {
                    if (person.distanceToPoint(o.getObjectX(), o.getObjectY()) <= 60) {
                        person.getPA().object(o.getObjectId(), o.getObjectX(), o.getObjectY(), o.getObjectFace(), o.getObjectType());
                    }
                }
            }
        }
    }

    public void process() {
        for (int j = 0; j < globalObjects.size(); j++) {
            var o = globalObjects.get(j);
            if (o != null) {
                if (o.objectTicks > 0) {
                    o.objectTicks--;
                }
                if (o.objectTicks == 1) {
                    var deleteObject = objectExists(o.getObjectX(), o.getObjectY(), o.getObjectHeight());
                    removeObject(deleteObject);
                    o.objectTicks = 0;
                    placeObject(o);
                    removeObject(o);
                    if (isObelisk(o.objectId)) {
                        int index = getObeliskIndex(o.objectId);
                        if (activated[index]) {
                            activated[index] = false;
                            teleportObelisk(index);
                        }
                    }
                }
            }
        }
    }

    public boolean loadGlobalObjects(String fileName) {
        try (var objectFile = new BufferedReader(new FileReader("./" + fileName))) {
            String line;
            while ((line = objectFile.readLine()) != null) {
                line = line.trim();
                int spot = line.indexOf("=");
                if (spot > -1) {
                    var token = line.substring(0, spot).trim();
                    var token2 = line.substring(spot + 1).trim();
                    var token2_2 = token2.replaceAll("\t+", "\t");
                    var token3 = token2_2.split("\t");
                    if (token.equals("object")) {
                        var object = new Objects(
                            Integer.parseInt(token3[0]), Integer.parseInt(token3[1]),
                            Integer.parseInt(token3[2]), Integer.parseInt(token3[3]),
                            Integer.parseInt(token3[4]), Integer.parseInt(token3[5]), 0
                        );
                        addObject(object);
                    }
                } else {
                    if (line.equals("[ENDOFOBJECTLIST]")) {
                        return true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Misc.println(fileName + ": file not found.");
            return false;
        } catch (IOException e) {
            Misc.println(fileName + ": error loading file.");
            return false;
        }
        return false;
    }

    public static final int MAX_DOORS = 30;
    public static int[][] doors = new int[MAX_DOORS][5];
    public static int doorFace = 0;

    public void doorHandling(int doorId, int doorX, int doorY, int doorHeight) {
        for (var door : doors) {
            if (doorX == door[0] && doorY == door[1] && doorHeight == door[2]) {
                doorId += (door[4] == 0) ? 1 : -1;
                for (var p : Server.playerHandler.players) {
                    if (p != null) {
                        var person = (Client) p;
                        if (person != null && person.heightLevel == doorHeight && person.distanceToPoint(door[0], door[1]) <= 60) {
                            person.getPA().object(-1, door[0], door[1], 0, 0);
                            if (door[3] == 0 && door[4] == 1) {
                                person.getPA().object(doorId, door[0], door[1] + 1, -1, 0);
                            } else if (door[3] == -1 && door[4] == 1) {
                                person.getPA().object(doorId, door[0] - 1, door[1], -2, 0);
                            } else if (door[3] == -2 && door[4] == 1) {
                                person.getPA().object(doorId, door[0], door[1] - 1, -3, 0);
                            } else if (door[3] == -3 && door[4] == 1) {
                                person.getPA().object(doorId, door[0] + 1, door[1], 0, 0);
                            } else if (door[3] == 0 && door[4] == 0) {
                                person.getPA().object(doorId, door[0] - 1, door[1], -3, 0);
                            } else if (door[3] == -1 && door[4] == 0) {
                                person.getPA().object(doorId, door[0], door[1] - 1, 0, 0);
                            } else if (door[3] == -2 && door[4] == 0) {
                                person.getPA().object(doorId, door[0] + 1, door[1], -1, 0);
                            } else if (door[3] == -3 && door[4] == 0) {
                                person.getPA().object(doorId, door[0], door[1] + 1, -2, 0);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean loadDoorConfig(String fileName) {
        try (var objectFile = new BufferedReader(new FileReader("./" + fileName))) {
            String line;
            int door = 0;
            while ((line = objectFile.readLine()) != null) {
                line = line.trim();
                int spot = line.indexOf("=");
                if (spot > -1) {
                    var token = line.substring(0, spot).trim();
                    var token2 = line.substring(spot + 1).trim();
                    var token2_2 = token2.replaceAll("\t+", "\t");
                    var token3 = token2_2.split("\t");
                    if (token.equals("door")) {
                        doors[door][0] = Integer.parseInt(token3[0]);
                        doors[door][1] = Integer.parseInt(token3[1]);
                        doors[door][2] = Integer.parseInt(token3[2]);
                        doors[door][3] = Integer.parseInt(token3[3]);
                        doors[door][4] = Integer.parseInt(token3[4]);
                        door++;
                    }
                } else {
                    if (line.equals("[ENDOFDOORLIST]")) {
                        return true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Misc.println(fileName + ": file not found.");
            return false;
        } catch (IOException e) {
            Misc.println(fileName + ": error loading file.");
            return false;
        }
        return false;
    }

    public final int IN_USE_ID = 14825;
    public boolean isObelisk(int id) {
        for (var obeliskId : obeliskIds) {
            if (obeliskId == id)
                return true;
        }
        return false;
    }
    public int[] obeliskIds = {14829, 14830, 111235, 14828, 14826, 14831};
    public int[][] obeliskCoords = {
        {3154, 3618}, {3225, 3665}, {3033, 3730},
        {3104, 3792}, {2978, 3864}, {3305, 3914}
    };
    public boolean[] activated = {false, false, false, false, false, false};

    public void startObelisk(int obeliskId) {
        int index = getObeliskIndex(obeliskId);
        if (index >= 0 && !activated[index]) {
            activated[index] = true;
            var obby1 = new Objects(14825, obeliskCoords[index][0], obeliskCoords[index][1], 0, -1, 10, 0);
            var obby2 = new Objects(14825, obeliskCoords[index][0] + 4, obeliskCoords[index][1], 0, -1, 10, 0);
            var obby3 = new Objects(14825, obeliskCoords[index][0], obeliskCoords[index][1] + 4, 0, -1, 10, 0);
            var obby4 = new Objects(14825, obeliskCoords[index][0] + 4, obeliskCoords[index][1] + 4, 0, -1, 10, 0);
            addObject(obby1); addObject(obby2); addObject(obby3); addObject(obby4);
            Server.objectHandler.placeObject(obby1);
            Server.objectHandler.placeObject(obby2);
            Server.objectHandler.placeObject(obby3);
            Server.objectHandler.placeObject(obby4);
            var obby5 = new Objects(obeliskIds[index], obeliskCoords[index][0], obeliskCoords[index][1], 0, -1, 10, 10);
            var obby6 = new Objects(obeliskIds[index], obeliskCoords[index][0] + 4, obeliskCoords[index][1], 0, -1, 10, 10);
            var obby7 = new Objects(obeliskIds[index], obeliskCoords[index][0], obeliskCoords[index][1] + 4, 0, -1, 10, 10);
            var obby8 = new Objects(obeliskIds[index], obeliskCoords[index][0] + 4, obeliskCoords[index][1] + 4, 0, -1, 10, 10);
            addObject(obby5); addObject(obby6); addObject(obby7); addObject(obby8);
        }
    }

    public int getObeliskIndex(int id) {
        for (int j = 0; j < obeliskIds.length; j++) {
            if (obeliskIds[j] == id)
                return j;
        }
        return -1;
    }

    public void teleportObelisk(int port) {
        int random = Misc.random(5);
        while (random == port) {
            random = Misc.random(5);
        }
        for (var player : Server.playerHandler.players) {
            if (player != null) {
                var c = (Client) player;
                if (c.goodDistance(c.getX(), c.getY(), obeliskCoords[port][0] + 2, obeliskCoords[port][1] + 2, 1)) {
                    c.getPA().startTeleport2(obeliskCoords[random][0] + 2, obeliskCoords[random][1] + 2, 0);
                }
            }
        }
    }
}
