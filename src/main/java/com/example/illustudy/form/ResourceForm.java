package com.example.illustudy.form;

import java.util.List;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.example.illustudy.entity.Hashtag;
import com.example.illustudy.validation.constraints.ImageByte;
import com.example.illustudy.validation.constraints.ImageNotEmpty;

import lombok.Data;

@Data
public class ResourceForm {

	
	private Long resourceId;

	private Long userId;
	
	@NotEmpty
	private String description;
	
	private UserForm user;
	
	private LinkPreviewForm linkPreview;
	
}
