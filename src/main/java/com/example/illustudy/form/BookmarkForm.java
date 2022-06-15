package com.example.illustudy.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class BookmarkForm {

	private Long bookmarkId;
	private Long userId;
	private Long topicId;
	
	@Size(max = 1000)
    private String description;
	
	private UserForm user;
	
	private TopicForm topic;
	
}
