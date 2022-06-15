package com.example.illustudy.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;


@Entity
@Table(name = "Bookmark")
@Data
public class Bookmark extends AbstractEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "bookmark_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bookmarkId;
	
	@Column(nullable = false)
	private Long userId;
	
	@Column(nullable = false)
	private Long topicId;
	
	@Column(length = 1000)
	private String description;
		
	@ManyToOne
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "topicId", insertable = false, updatable = false)
	private Topic topic;

	
}
