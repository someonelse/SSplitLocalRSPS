package server;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import server.model.players.Client;

public class Connection {

    public static final List<String> bannedIps = new ArrayList<>();
    public static final List<String> bannedNames = new ArrayList<>();
    public static final List<String> mutedIps = new ArrayList<>();
    public static final List<String> mutedNames = new ArrayList<>();
    public static final List<String> loginLimitExceeded = new ArrayList<>();

    public static void initialize() {
        banUsers();
        banIps();
        muteUsers();
        muteIps();
    }

    public static void addIpToLoginList(String IP) {
        loginLimitExceeded.add(IP);
    }

    public static void removeIpFromLoginList(String IP) {
        loginLimitExceeded.remove(IP);
    }

    public static void clearLoginList() {
        loginLimitExceeded.clear();
    }

    public static boolean checkLoginList(String IP) {
        loginLimitExceeded.add(IP);
        long count = loginLimitExceeded.stream().filter(IP::equals).count();
        return count > 5;
    }

    public static void unMuteUser(String name) {
        mutedNames.remove(name);
        deleteFromFile("./Data/bans/UsersMuted.txt", name);
    }

    public static void unIPMuteUser(String name) {
        mutedIps.remove(name);
        deleteFromFile("./Data/bans/IpsMuted.txt", name);
    }

    public static void addIpToBanList(String IP) {
        bannedIps.add(IP);
    }

    public static void addIpToMuteList(String IP) {
        mutedIps.add(IP);
        addIpToMuteFile(IP);
    }

    public static void removeIpFromBanList(String IP) {
        bannedIps.remove(IP);
    }

    public static boolean isIpBanned(String IP) {
        return bannedIps.contains(IP);
    }

    public static void addNameToBanList(String name) {
        bannedNames.add(name.toLowerCase());
    }

    public static void addNameToMuteList(String name) {
        mutedNames.add(name.toLowerCase());
        addUserToFile(name);
    }

    public static void removeNameFromBanList(String name) {
        bannedNames.remove(name.toLowerCase());
        deleteFromFile("./Data/bans/UsersBanned.txt", name);
    }

    public static void removeNameFromMuteList(String name) {
        bannedNames.remove(name.toLowerCase());
        deleteFromFile("./Data/bans/UsersMuted.txt", name);
    }

    public static void deleteFromFile(String file, String name) {
        try {
            var lines = Files.readAllLines(Paths.get(file));
            var filtered = lines.stream()
                    .filter(line -> !line.trim().equalsIgnoreCase(name))
                    .collect(Collectors.toList());
            Files.write(Paths.get(file), filtered);
        } catch (IOException ignored) {}
    }

    public static boolean isNamedBanned(String name) {
        return bannedNames.contains(name.toLowerCase());
    }

    public static void banUsers() {
        try (var in = Files.newBufferedReader(Paths.get("./Data/bans/UsersBanned.txt"))) {
            String data;
            while ((data = in.readLine()) != null) {
                addNameToBanList(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void muteUsers() {
        try (var in = Files.newBufferedReader(Paths.get("./Data/bans/UsersMuted.txt"))) {
            String data;
            while ((data = in.readLine()) != null) {
                mutedNames.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void banIps() {
        try (var in = Files.newBufferedReader(Paths.get("./Data/bans/IpsBanned.txt"))) {
            String data;
            while ((data = in.readLine()) != null) {
                addIpToBanList(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void muteIps() {
        try (var in = Files.newBufferedReader(Paths.get("./Data/bans/IpsMuted.txt"))) {
            String data;
            while ((data = in.readLine()) != null) {
                mutedIps.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addNameToFile(String Name) {
        try (var out = Files.newBufferedWriter(Paths.get("./Data/bans/UsersBanned.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            out.newLine();
            out.write(Name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUserToFile(String Name) {
        try (var out = Files.newBufferedWriter(Paths.get("./Data/bans/UsersMuted.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            out.newLine();
            out.write(Name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addIpToFile(String Name) {
        try (var out = Files.newBufferedWriter(Paths.get("./Data/bans/IpsBanned.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            out.newLine();
            out.write(Name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addIpToMuteFile(String Name) {
        try (var out = Files.newBufferedWriter(Paths.get("./Data/bans/IpsMuted.txt"), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            out.newLine();
            out.write(Name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isMuted(Client c) {
        return mutedNames.contains(c.playerName.toLowerCase()) || mutedIps.contains(c.connectedFrom);
    }
}
