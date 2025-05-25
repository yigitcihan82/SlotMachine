package com.example.slotmachineapp;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UserManager {
    private static UserManager instance;
    private Map<String, User> users;
    private User currentUser;
    private final String USER_FILE = "users.properties";

    private UserManager() {
        users = new HashMap<>();
        loadUsers();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private void loadUsers() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(USER_FILE)) {
            props.load(in);
            for (String username : props.stringPropertyNames()) {
                String[] data = props.getProperty(username).split(",");
                String password = data[0];
                int credits = 100;
                if (data.length > 1) {
                    try {
                        credits = Integer.parseInt(data[1]);
                    } catch (NumberFormatException e) {

                    }
                }
                User user = new User(username, password);
                user.setCredits(credits);
                users.put(username, user);
            }
        } catch (IOException e) {

            System.out.println("Creating new user database");
        }
    }

    private void saveUsers() {
        Properties props = new Properties();
        for (Map.Entry<String, User> entry : users.entrySet()) {
            User user = entry.getValue();

            String userData = user.getPassword() + "," + user.getCredits();
            props.setProperty(user.getUsername(), userData);
        }

        try (FileOutputStream out = new FileOutputStream(USER_FILE)) {
            props.store(out, "Slot Machine Users");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password));
        saveUsers();
        return true;
    }

    public boolean loginUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        if (currentUser != null) {

            users.put(currentUser.getUsername(), currentUser);
            saveUsers();
            currentUser = null;
        }
    }


    public void updateCurrentUserCredits(int newCredits) {
        if (currentUser != null) {
            currentUser.setCredits(newCredits);
            users.put(currentUser.getUsername(), currentUser);
            saveUsers();
        }
    }
} 