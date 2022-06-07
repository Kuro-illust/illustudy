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
@Table(name = "hashtag")
@Data
public class Hashtag extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name = "hashtag_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long hashtagId;
	
	@Column(nullable = false, unique = true)
	private String tagName;
	
	@OneToMany
	@JoinColumn(name = "hashtagId", insertable = false, updatable = false)
	private List<Topic_Hashtag> topic_hashtag;
	
}
