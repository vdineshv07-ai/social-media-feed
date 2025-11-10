package backend;

import java.util.*;
import java.util.stream.Collectors;

public class PostService {
    private static final List<Post> posts = new ArrayList<>();
    
    static {
        // Sample posts
        addPost("Best RPG Games 2024", "GameReviewer", "Just finished playing Baldur's Gate 3 and it's amazing!", "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400", "https://store.steampowered.com/app/1086940/Baldurs_Gate_3/");
        addPost("Italian Pasta Recipe", "ChefMaster", "Today I made a delicious pasta with garlic and herbs. Perfect for a quick dinner!", "https://images.unsplash.com/photo-1551782450-17144efb9c50?w=400", "https://www.allrecipes.com/recipe/23431/to-die-for-fettuccine-alfredo/");
        addPost("Tokyo Food Adventure", "Wanderer", "Just visited Tokyo and the food scene is incredible. So many amazing restaurants to try.", "https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=400", "https://www.japan-guide.com/e/e2036.html");
        addPost("AI Revolution 2024", "TechGuru", "Artificial Intelligence is changing everything. From healthcare to education, AI is everywhere.", "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=400", "https://www.openai.com/");
        addPost("Morning Workout Routine", "FitnessFan", "Started my day with a 5K run and strength training. Feeling energized and ready!", "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400", "https://www.nike.com/training");
    }
    
    public static synchronized boolean addPost(String title, String author, String content, String imageUrl, String websiteUrl) {
        if (title == null || title.trim().isEmpty() || author == null || author.trim().isEmpty()) {
            return false;
        }
        String id = UUID.randomUUID().toString();
        Post post = new Post(id, title.trim(), author.trim(), content != null ? content.trim() : "", imageUrl, websiteUrl);
        posts.add(0, post);
        return true;
    }
    
    public static synchronized boolean likePost(String postId) {
        return posts.stream()
            .filter(p -> p.getId().equals(postId))
            .findFirst()
            .map(post -> { post.incrementLikes(); return true; })
            .orElse(false);
    }
    
    public static synchronized List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }
    
    public static synchronized List<Post> searchPosts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPosts();
        }
        String searchTerm = query.toLowerCase().trim();
        return posts.stream()
            .filter(post -> 
                post.getTitle().toLowerCase().contains(searchTerm) ||
                post.getContent().toLowerCase().contains(searchTerm) ||
                post.getAuthor().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }
}
