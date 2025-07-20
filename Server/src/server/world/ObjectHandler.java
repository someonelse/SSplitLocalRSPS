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
        if (c.distanceToPoint(2813, 3463) <
