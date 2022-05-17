package com.example.illustudy.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.example.illustudy.validation.constraints.ImageByte;
import com.example.illustudy.validation.constraints.ImageNotEmpty;

import lombok.Data;

@Data
public class TopicForm {

	private Long topicID;
	private Long userID;
	
	@ImageNotEmpty
	@ImageByte(max = 32000000)
	private MultipartFile image;
	
	private MultipartFile thumbnailImage;
	
	private String imageData;
	
	private String path;
	
	private String thumbnailPath;
	
	@NotEmpty
	@Size(max = 1000)
	private String description;
	
	private UserForm user;
	
}
