package com.example.backend.controllers;

import com.example.backend.dto.PostRequestDTO;
import com.example.backend.dto.PostResponseDTO;
import com.example.backend.model.Post;
import com.example.backend.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    public PostController(final PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/get-all-posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(Authentication authentication,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postService.getAllPosts(authentication, pageable);

        Page<PostResponseDTO> response = posts.map(post -> new PostResponseDTO(
                "Post retrieved!",
                post.getAuthor().getUsername(),
                post.getId(),
                post.getCreated_at(),
                post.getContent(),
                post.getTitle()
        ));

        return ResponseEntity.ok(response);
    }


    @GetMapping("/get-my-posts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PostResponseDTO>> getMyPosts(Authentication authentication, @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postService.getMyPosts(authentication, pageable);

        Page<PostResponseDTO> response = posts.map(post -> new PostResponseDTO(
                "Post retrieved!",
                post.getAuthor().getUsername(),
                post.getId(),
                post.getCreated_at(),
                post.getContent(),
                post.getTitle()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long id, Authentication authentication) {
        Post post = postService.getPost(id, authentication);
        return ResponseEntity.ok(new PostResponseDTO(
                "Post retrieved!",
                post.getAuthor().getUsername(),
                post.getId(),
                post.getCreated_at(),
                post.getContent(),
                post.getTitle()
        ));
    }

    @PostMapping("/new-post")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostRequestDTO postRequestDTO, Authentication authentication) {
        Post post = postService.createPost(postRequestDTO, authentication);
        return ResponseEntity.ok(new PostResponseDTO(
                "Post created!",
                post.getAuthor().getUsername(),
                post.getId(),
                post.getCreated_at(),
                post.getContent(),
                post.getTitle()
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @RequestBody PostRequestDTO postRequestDTO, Authentication authentication) {
        Post post = postService.updatePost(id, postRequestDTO, authentication);
        return ResponseEntity.ok(new PostResponseDTO(
                "Post updated!",
                post.getAuthor().getUsername(),
                post.getId(),
                post.getCreated_at(),
                post.getContent(),
                post.getTitle()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, Authentication authentication) {
        postService.deletePost(id, authentication);
        return ResponseEntity.ok("Post deleted!");
    }
}
