package com.enigmacamp.enigmaCademy.service;

import com.enigmacamp.enigmaCademy.dto.request.CommentRequest;
import com.enigmacamp.enigmaCademy.model.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getAllComments();
    Comment addComment(CommentRequest commentRequest);
}
