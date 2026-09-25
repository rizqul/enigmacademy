package com.enigmacamp.enigmaCademy.service.impl;

import com.enigmacamp.enigmaCademy.dto.request.CommentRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommentResponse;
import com.enigmacamp.enigmaCademy.model.Comment;
import com.enigmacamp.enigmaCademy.service.CommentService;
import com.enigmacamp.enigmaCademy.utils.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final RestClient restClient;

    @Override
    public List<Comment> getAllComments() {
        try {
            return restClient.get()
                    .uri("/comments")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Comment>>() {
                        @Override
                        public String toString() {
                            return super.toString();
                        }
                    });
        }catch (Exception e) {
            log.error("Error fetching semua comments : " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Comment addComment(CommentRequest commentRequest) {
        try {
            return  restClient.post()
                    .uri("/comments")
                    .accept(MediaType.APPLICATION_JSON)
                    .body(commentRequest) // body request data type
                    .retrieve()
                    .body(Comment.class); // body response data type
        }catch (Exception e){
            log.error("Error add comments : " + e.getMessage());
            return null;
        }
    }

    @Override
    public CommentResponse getCommentById(Integer id) {
        try {
            return restClient.get()
                    .uri("/comments/" + id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(CommentResponse.class);
        } catch (Exception e) {
            log.error("Error menampilkan comment :" + e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteComment(Integer id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("ID tidak boleh kurang dari nol");
        }

        try {
            restClient.delete()
                    .uri("/comments/" + id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Gagal menghapus: " + e.getMessage());
        }
    }
}
