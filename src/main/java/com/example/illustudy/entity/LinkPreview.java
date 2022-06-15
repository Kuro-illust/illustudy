package com.example.illustudy.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "LinkPreview")
@Data
public class LinkPreview extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "LinkPreview_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long LinkPreviewId;

	//@Column(nullable = false)
	//private Long topicId;

	@Column(nullable = false)
	private String description;
	
	@Column(nullable = false)
	private String image;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String url;
	
		
}