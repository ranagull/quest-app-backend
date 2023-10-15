package com.project.questapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.questapp.entities.Comment;
import com.project.questapp.entities.Post;
import com.project.questapp.entities.User;
import com.project.questapp.repos.CommentRepository;
import com.project.questapp.requests.CommentCreateRequest;
import com.project.questapp.requests.CommentUpdateRequest;

@Service
public class CommentService {
	
	private CommentRepository commentRepository;
	private UserService userService;
	private PostService postService;
	
	
	public CommentService(CommentRepository commentRepository, UserService userService,
			PostService postService) {
		this.commentRepository = commentRepository;
		this.userService = userService;
		this.postService = postService;
	}

	public List<Comment> getAllCommentsWithParam(Optional<Long> userId, Optional<Long> postId) {
		
		// ./comments?postId={postId}& user={userId}
		if(userId.isPresent() && postId.isPresent()) {
			return commentRepository.findByUserIdAndPostId(userId.get(), postId.get());
		}
		// ./comments?user={userId}
		else if(userId.isPresent()) {
			return commentRepository.findByUserId(userId.get());
		}
		//./comments?postId={postId}
		else if(postId.isPresent()) {
			return commentRepository.findByPostId(postId.get());
		}
		// ./comments
		else {
			return commentRepository.findAll();
		}
		
	}
	
	// ./comments/{commentId}
	public Comment getOneCommentById(Long commentId) {
		return commentRepository.findById(commentId).orElse(null);
	}

	public Comment createOneComment(CommentCreateRequest request) {
		
		User user = userService.getOneUserById(request.getUserId());
		Post post = postService.getOnePostById(request.getPostId());
		
		// control post and user is exist
		if(user != null && post != null) {
			Comment commentToSave = new Comment();
			commentToSave.setId(request.getId());
			commentToSave.setPost(post);
			commentToSave.setUser(user);
			commentToSave.setText(request.getText());
			return commentRepository.save(commentToSave);
		}
		else
			return null;
		
	}

	public Comment updateOneCommentById(Long commentId, CommentUpdateRequest request) {
		
		Optional<Comment> comment = commentRepository.findById(commentId);
		if(comment.isPresent()) {
			Comment commentToUpdate = comment.get();
			commentToUpdate.setText(request.getText());
			return commentRepository.save(commentToUpdate);
			
		}else 
			return null;
	}

	public void deleteOneCommentById(Long commentId) {
		commentRepository.deleteById(commentId);
	}

}
