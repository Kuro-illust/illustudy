package com.example.illustudy.form;

import lombok.Data;

@Data
public class BookmarktagForm {
	private Long Id;
	private Long hashtagId;
	private Long bookmarkId;
	private String tagName;
}
