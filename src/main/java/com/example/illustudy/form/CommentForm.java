package com.example.illustudy.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CommentForm {

	private Long commentId;
	private Long userId;
	private Long topicId;
	
	@NotEmpty
	@Size(max = 1000)
    private String description;
	
	private boolean deleted;
	
	private UserForm user;
	
}
