package frontend;

import backend.Post;
import backend.PostService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimpleFeedApp extends JFrame {
    private JTextField searchField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextArea contentArea;
    private JPanel postsPanel;
    private JScrollPane scrollPane;

    public SimpleFeedApp() {
        setTitle("Social Media Feed Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        initializeComponents();
        layoutComponents();
        loadPosts();
        
        setSize(800, 700);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // Search components
        searchField = new JTextField(20);
        
        // Create post components
        titleField = new JTextField(30);
        authorField = new JTextField(20);
        contentArea = new JTextArea(3, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        // Posts display
        postsPanel = new JPanel();
        postsPanel.setLayout(new BoxLayout(postsPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(postsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    private void layoutComponents() {
        // Header
        JLabel headerLabel = new JLabel("Social Media Feed Viewer", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Posts"));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchPosts());
        searchPanel.add(searchBtn);
        
        JButton showAllBtn = new JButton("Show All");
        showAllBtn.addActionListener(e -> loadPosts());
        searchPanel.add(showAllBtn);
        
        // Create post panel
        JPanel createPanel = new JPanel(new GridBagLayout());
        createPanel.setBorder(BorderFactory.createTitledBorder("Create New Post"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        createPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        createPanel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        createPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        createPanel.add(authorField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        createPanel.add(new JLabel("Content:"), gbc);
        gbc.gridx = 1;
        createPanel.add(new JScrollPane(contentArea), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        JButton createBtn = new JButton("Create Post");
        createBtn.setBackground(new Color(40, 167, 69));
        createBtn.setForeground(Color.WHITE);
        createBtn.addActionListener(e -> createPost());
        createPanel.add(createBtn, gbc);
        
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(createPanel, BorderLayout.SOUTH);
        
        // Posts panel
        JPanel postsDisplayPanel = new JPanel(new BorderLayout());
        postsDisplayPanel.setBorder(BorderFactory.createTitledBorder("Posts Feed"));
        postsDisplayPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Main layout
        add(topPanel, BorderLayout.NORTH);
        add(postsDisplayPanel, BorderLayout.CENTER);
    }

    private void createPost() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String content = contentArea.getText().trim();
        
        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Author are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = PostService.addPost(title, author, content);
        
        if (success) {
            // Clear form
            titleField.setText("");
            authorField.setText("");
            contentArea.setText("");
            loadPosts();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create post!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchPosts() {
        String query = searchField.getText().trim();
        List<Post> results = PostService.searchPosts(query);
        displayPosts(results);
    }

    private void loadPosts() {
        List<Post> posts = PostService.getAllPosts();
        displayPosts(posts);
    }

    private void displayPosts(List<Post> posts) {
        postsPanel.removeAll();
        
        if (posts.isEmpty()) {
            JLabel noPostsLabel = new JLabel("No posts found. Create your first post!");
            noPostsLabel.setHorizontalAlignment(JLabel.CENTER);
            postsPanel.add(noPostsLabel);
        } else {
            for (Post post : posts) {
                JPanel postCard = createPostCard(post);
                postsPanel.add(postCard);
                postsPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        postsPanel.revalidate();
        postsPanel.repaint();
    }

    private JPanel createPostCard(Post post) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        
        // Title
        JLabel titleLabel = new JLabel(post.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 123, 255));
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel authorLabel = new JLabel("By: " + post.getAuthor());
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        authorLabel.setForeground(Color.GRAY);
        
        JLabel likesLabel = new JLabel("Likes: " + post.getLikes());
        likesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        likesLabel.setForeground(Color.GRAY);
        
        JButton likeBtn = new JButton("Like");
        likeBtn.setBackground(new Color(40, 167, 69));
        likeBtn.setForeground(Color.WHITE);
        likeBtn.setPreferredSize(new Dimension(60, 25));
        likeBtn.addActionListener(e -> {
            boolean success = PostService.likePost(post.getId());
            if (success) {
                loadPosts();
            }
        });
        
        infoPanel.add(authorLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(likesLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(likeBtn);
        
        // Content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        
        if (!post.getContent().isEmpty()) {
            JTextArea contentText = new JTextArea(post.getContent());
            contentText.setEditable(false);
            contentText.setLineWrap(true);
            contentText.setWrapStyleWord(true);
            contentText.setBackground(card.getBackground());
            contentText.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            contentPanel.add(contentText, BorderLayout.SOUTH);
        }
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimpleFeedApp().setVisible(true);
        });
    }
}
