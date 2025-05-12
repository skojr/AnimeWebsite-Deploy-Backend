package com.example.backend.services;

import com.example.backend.dto.PostRequestDTO;
import com.example.backend.model.Post;
import com.example.backend.model.User;
import com.example.backend.repositories.PostRepository;
import com.example.backend.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final UserRepository userRepository;;
    private final PostRepository postRepository;

    public PostService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public Page<Post> getMyPosts(Authentication authentication, Pageable pageable) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
        return postRepository.findByAuthor(user, pageable);
    }

    public Page<Post> getAllPosts(Authentication authentication, Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post createPost(PostRequestDTO postRequestDTO, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = new Post();
        post.setAuthor(user);
        post.setContent(postRequestDTO.getContent());
        post.setTitle(postRequestDTO.getTitle());
        post.setCreated_at(LocalDateTime.now());
        user.addPost(post);
        return postRepository.save(post);
    }

    public Post updatePost(Long id, PostRequestDTO postRequestDTO, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!user.getId().equals(post.getAuthor().getId())) {
            throw new AccessDeniedException("You are not authorized to update this post.");
        }
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        return postRepository.save(post);
    }

    public void deletePost(Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!user.getId().equals(post.getAuthor().getId())) {
            throw new AccessDeniedException("You are not authorized to update this post.");
        }
        postRepository.delete(post);
    }

    public Post getPost(Long id, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!user.getId().equals(post.getAuthor().getId())) {
            throw new AccessDeniedException("You are not authorized to see this post.");
        }
        return post;
    }
}
