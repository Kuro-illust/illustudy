package com.example.illustudy.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class LinkPreviewForm {

	private Long linkPreviewId;
	
	@NotEmpty
	private String image;
	
	@NotEmpty
	private String url;
	
	@NotEmpty
	private String title;
    
	@NotEmpty
	private String description;
  
}
