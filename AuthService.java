package backend;

import java.util.*;

public class AuthService {
    private static final Map<String, User> users = new HashMap<>();
    private static final Set<String> loggedInUsers = new HashSet<>();
    
    static {
        // Default users
        users.put("admin", new User("admin", "admin123"));
        users.put("user", new User("user", "user123"));
    }
    
    public static boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            loggedInUsers.add(username);
            return true;
        }
        return false;
    }
    
    public static boolean register(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        users.put(username, new User(username, password));
        return true;
    }
    
    public static boolean isLoggedIn(String username) {
        return loggedInUsers.contains(username);
    }
    
    public static void logout(String username) {
        loggedInUsers.remove(username);
    }
}
