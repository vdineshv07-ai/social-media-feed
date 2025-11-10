package backend;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.List;

public class WebServer {
    private HttpServer server;
    private final int port;

    public WebServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        server.createContext("/api/posts", new PostsHandler());
        server.createContext("/api/posts/like", new LikeHandler());
        server.createContext("/", new StaticHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("Web server started on http://localhost:" + port);
    }

    static class PostsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            
            if ("GET".equals(method)) {
                handleGetPosts(exchange);
            } else if ("POST".equals(method)) {
                handleCreatePost(exchange);
            }
        }
        
        private void handleGetPosts(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            List<Post> posts;
            
            if (query != null && query.startsWith("search=")) {
                String searchTerm = URLDecoder.decode(query.substring(7), "UTF-8");
                posts = PostService.searchPosts(searchTerm);
            } else {
                posts = PostService.getAllPosts();
            }
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"id\":\"").append(post.getId()).append("\",")
                    .append("\"title\":\"").append(escapeJson(post.getTitle())).append("\",")
                    .append("\"author\":\"").append(escapeJson(post.getAuthor())).append("\",")
                    .append("\"content\":\"").append(escapeJson(post.getContent())).append("\",")
                    .append("\"imageUrl\":\"").append(escapeJson(post.getImageUrl())).append("\",")
                    .append("\"websiteUrl\":\"").append(escapeJson(post.getWebsiteUrl())).append("\",")
                    .append("\"likes\":").append(post.getLikes()).append(",")
                    .append("\"timestamp\":").append(post.getTimestamp())
                    .append("}");
            }
            json.append("]");
            
            String response = json.toString();
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
        
        private void handleCreatePost(HttpExchange exchange) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            
            String json = body.toString();
            String title = extractJsonValue(json, "title");
            String author = extractJsonValue(json, "author");
            String content = extractJsonValue(json, "content");
            String imageUrl = extractJsonValue(json, "imageUrl");
            
            boolean success = PostService.addPost(title, author, content, imageUrl, "");
            
            String response = success ? 
                "{\"success\":true,\"message\":\"Post created successfully\"}" :
                "{\"success\":false,\"message\":\"Failed to create post\"}";
            
            exchange.sendResponseHeaders(success ? 200 : 400, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
        
        private String extractJsonValue(String json, String key) {
            String pattern = "\"" + key + "\":\"";
            int start = json.indexOf(pattern);
            if (start == -1) return "";
            start += pattern.length();
            int end = json.indexOf("\"", start);
            return end > start ? json.substring(start, end) : "";
        }
        
        private String escapeJson(String str) {
            if (str == null) return "";
            return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        }
    }

    static class LikeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
                return;
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            
            String json = body.toString();
            String postId = extractJsonValue(json, "postId");
            
            boolean success = PostService.likePost(postId);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            String response = success ?
                "{\"success\":true,\"message\":\"Post liked\"}" :
                "{\"success\":false,\"message\":\"Post not found\"}";
            
            exchange.sendResponseHeaders(success ? 200 : 404, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
        
        private String extractJsonValue(String json, String key) {
            String pattern = "\"" + key + "\":\"";
            int start = json.indexOf(pattern);
            if (start == -1) return "";
            start += pattern.length();
            int end = json.indexOf("\"", start);
            return end > start ? json.substring(start, end) : "";
        }
    }

    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = "<!DOCTYPE html><html><head><title>Social Media Feed Viewer</title>" +
                "<style>body{font-family:Arial;max-width:800px;margin:0 auto;padding:20px;background:#e6f3ff}" +
                ".section{margin:20px 0;padding:15px;border:1px solid #ddd;border-radius:8px;background:white}" +
                ".post{background:#f9f9f9;padding:15px;margin:10px 0;border-radius:8px;cursor:pointer}" +
                "input,textarea,button{margin:5px;padding:8px}" +
                "button{background:#007bff;color:white;border:none;border-radius:4px;cursor:pointer}" +
                ".like-btn{background:#28a745}</style></head><body>" +
                "<h1>Social Media Feed Viewer</h1>" +
                "<div class='section'><h3>Search Posts</h3>" +
                "<input type='text' id='searchInput' placeholder='Search posts...'>" +
                "<button onclick='searchPosts()'>Search</button>" +
                "<button onclick='loadPosts()'>Show All</button></div>" +
                "<div class='section'><h3>Create New Post</h3>" +
                "<input type='text' id='titleInput' placeholder='Post title...'><br>" +
                "<input type='text' id='authorInput' placeholder='Your name...'><br>" +
                "<textarea id='contentInput' placeholder='What is on your mind?' rows='3'></textarea><br>" +
                "<input type='text' id='imageInput' placeholder='Image URL (optional)...'><br>" +
                "<button onclick='createPost()'>Create Post</button></div>" +
                "<div class='section'><h3>Posts</h3><div id='postsContainer'></div></div>" +
                "<script>" +
                "async function createPost(){" +
                "const title=document.getElementById('titleInput').value.trim();" +
                "const author=document.getElementById('authorInput').value.trim();" +
                "const content=document.getElementById('contentInput').value.trim();" +
                "const imageUrl=document.getElementById('imageInput').value.trim();" +
                "if(!title||!author){alert('Title and Author are required!');return;}" +
                "await fetch('/api/posts',{method:'POST',headers:{'Content-Type':'application/json'}," +
                "body:JSON.stringify({title,author,content,imageUrl})});" +
                "document.getElementById('titleInput').value='';" +
                "document.getElementById('authorInput').value='';" +
                "document.getElementById('contentInput').value='';" +
                "document.getElementById('imageInput').value='';loadPosts();}" +
                "async function loadPosts(){" +
                "const response=await fetch('/api/posts');" +
                "const posts=await response.json();displayPosts(posts);}" +
                "async function searchPosts(){" +
                "const query=document.getElementById('searchInput').value.trim();" +
                "if(!query){loadPosts();return;}" +
                "const response=await fetch('/api/posts?search='+encodeURIComponent(query));" +
                "const posts=await response.json();displayPosts(posts);}" +
                "async function likePost(id){" +
                "await fetch('/api/posts/like',{method:'POST',headers:{'Content-Type':'application/json'}," +
                "body:JSON.stringify({postId:id})});loadPosts();}" +
                "function openWebsite(url){if(url&&url.trim()!==''){window.open(url,'_blank');}}" +
                "function displayPosts(posts){" +
                "const container=document.getElementById('postsContainer');" +
                "if(posts.length===0){container.innerHTML='<p>No posts found.</p>';return;}" +
                "container.innerHTML=posts.map(post=>" +
                "'<div class=\"post\" onclick=\"openWebsite(\\''+post.websiteUrl+'\\')\">" +
                "<h4>'+post.title+'</h4>" +
                "<p><strong>By: '+post.author+'</strong> | Likes: '+post.likes+'</p>'" +
                "+(post.imageUrl?'<img src=\"'+post.imageUrl+'\" style=\"max-width:100%;height:200px;object-fit:cover;border-radius:8px;margin:10px 0\">':'')" +
                "+(post.content?'<p>'+post.content+'</p>':'')" +
                "+'<button class=\"like-btn\" onclick=\"event.stopPropagation();likePost(\\''+post.id+'\\')\" style=\"margin-top:10px\">Like</button></div>').join('');}" +
                "loadPosts();</script></body></html>";
            
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            exchange.getResponseBody().write(html.getBytes());
            exchange.getResponseBody().close();
        }
    }
}
