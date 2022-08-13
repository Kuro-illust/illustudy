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
@Table(name = "bookmark_hashtag")
@Data
public class Bookmark_Hashtag extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "topic_hashtag_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
	
	@Column(nullable = false)
	private Long bookmarkId;
	
	
	/*
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> tagName;
	*/
	@Column(nullable = false)
	private Long hashtagId;
	
	
	@ManyToOne
	@JoinColumn(name = "bookmarkId", insertable = false, updatable = false)
	private Bookmark Bookmark;
	
	
	@ManyToOne
	@JoinColumn(name = "hashtagId", insertable = false, updatable = false)
	private Hashtag hashtags;
	
	
}
