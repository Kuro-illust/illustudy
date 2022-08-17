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
@Table(name = "resource")
@Data
public class Resource extends AbstractEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "resource_id_seq")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long resourceId;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long linkPreviewId;

	@Column(nullable = false, length = 1000)
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "linkPreviewId", insertable = false, updatable = false)
	private LinkPreview linkPreview;
	
	@ManyToOne
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private User user;
	
}