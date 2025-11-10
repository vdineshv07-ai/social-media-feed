package backend;

public class Post {
    private String id;
    private String title;
    private String author;
    private String content;
    private String imageUrl;
    private String websiteUrl;
    private int likes;
    private long timestamp;

    public Post(String id, String title, String author, String content, String imageUrl, String websiteUrl) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.imageUrl = imageUrl != null ? imageUrl : "";
        this.websiteUrl = websiteUrl != null ? websiteUrl : "";
        this.likes = 0;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public String getWebsiteUrl() { return websiteUrl; }
    public int getLikes() { return likes; }
    public long getTimestamp() { return timestamp; }
    
    public void incrementLikes() { this.likes++; }
}
