package com.enigmacamp.enigmaCademy.controller;

import com.enigmacamp.enigmaCademy.dto.request.CommentRequest;
import com.enigmacamp.enigmaCademy.dto.response.CommonResponse;
import com.enigmacamp.enigmaCademy.model.Comment;
import com.enigmacamp.enigmaCademy.service.CommentService;
import com.enigmacamp.enigmaCademy.utils.ResponseUtil;
import com.enigmacamp.enigmaCademy.utils.constants.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/json/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<Comment>>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();

        CommonResponse<List<Comment>> response = CommonResponse.<List<Comment>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("berhasil mengambil semua comment")
                .data(comments)
                .build();

                return ResponseEntity.ok(response);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<Comment>> addComment(@RequestBody CommentRequest commentRequest){
        Comment comment = commentService.addComment(commentRequest);

        return ResponseUtil.buildResponse(
                HttpStatus.CREATED,
                ResponseMessage.SUCCES_CREATE_DATA,
                comment
        );
    }
}
