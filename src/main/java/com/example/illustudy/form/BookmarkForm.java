package com.example.illustudy.form;

import java.util.List;

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

	private BookmarktagForm bookmarktag;
	
	private List<BookmarktagForm> bookmarktags;
	
}
