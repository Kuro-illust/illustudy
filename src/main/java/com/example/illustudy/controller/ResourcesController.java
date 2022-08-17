package com.example.illustudy.controller;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.illustudy.entity.LinkPreview;
import com.example.illustudy.entity.Resource;
import com.example.illustudy.entity.Topic;
import com.example.illustudy.entity.UserInf;
import com.example.illustudy.form.LinkPreviewForm;
import com.example.illustudy.form.ResourceForm;
import com.example.illustudy.form.TopicForm;
import com.example.illustudy.form.UserForm;
import com.example.illustudy.repository.LinkPreviewRepository;
import com.example.illustudy.repository.ResourceRepository;

@Controller
public class ResourcesController {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private ResourceRepository repository;
	
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LinkPreviewRepository linkPreviewRepository;
	
	// ユーザー認証が不要なバージョン
		@GetMapping(path = "resources")
		public String index(Model model) throws IOException {

			Iterable<Resource> resources = repository.findAllByOrderByUpdatedAtDesc();
			List<ResourceForm> list = new ArrayList<>();

			model.addAttribute("list", list);
			for (Resource entity : resources) {
				ResourceForm form = getResource(entity);
				list.add(form);
			}
			model.addAttribute("list", list);

			return "resources/index";
		}
		
		public ResourceForm getResource(Resource entity) throws IOException{
			// 循環回避
			modelMapper.typeMap(Resource.class, ResourceForm.class).addMappings(mapper -> mapper.skip(ResourceForm::setUser));
			modelMapper.typeMap(Resource.class, ResourceForm.class).addMappings(mapper -> mapper.skip(ResourceForm::setUser));
			
			ResourceForm form = modelMapper.map(entity, ResourceForm.class);
			
			UserForm userForm = modelMapper.map(entity.getUser(),UserForm.class);
			form.setUser(userForm);
			
			LinkPreviewForm linkPreviewForm = modelMapper.map(entity.getLinkPreview(),LinkPreviewForm.class);
			form.setLinkPreview(linkPreviewForm);
			
			return form;
		}
		
		
		
		
		
		
		@GetMapping(path = "/resources/new")
		public String newTopic(Model model) {
			model.addAttribute("form", new ResourceForm());
			return "resources/new";
		}
		
		
		@RequestMapping(value = "/resource", method = RequestMethod.POST)
		public String create(Principal principal, @Validated @ModelAttribute("form") ResourceForm form, BindingResult result,
				Model model, RedirectAttributes redirAttrs) throws IOException {
			if (result.hasErrors()) {
				model.addAttribute("hasMessage", true);
				model.addAttribute("class", "alert-danger");
				model.addAttribute("message", "投稿に失敗しました。");
				return "resources/new";
			}

			addLinkPreview(form.getLinkPreview());
			Resource entity = new Resource();
			Authentication authentication = (Authentication) principal;
			UserInf user = (UserInf) authentication.getPrincipal();
			entity.setUserId(user.getUserId());
			entity.setDescription(form.getDescription());
			entity.setLinkPreviewId((linkPreviewRepository.findByUrl(form.getLinkPreview().getUrl())).getLinkPreviewId());
			repository.saveAndFlush(entity);
			/*
			Resource entity = new Resource();
			Authentication authentication = (Authentication) principal;
			UserInf user = (UserInf) authentication.getPrincipal();
			entity.setUserId(user.getUserId());
			File destFile = null;
		
			entity.setDescription(form.getDescription());
			repository.saveAndFlush(entity);

			//Long topicId = entity.getTopicId();

			//addHashtags(form.getHashtag(), topicId);

*/

			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message", "投稿に成功しました。");

			return "redirect:/resources";
		}
		
	public void addLinkPreview(LinkPreviewForm form) {
		if(linkPreviewRepository.findByUrl(form.getUrl()) == null) {
			LinkPreview linkPreviewEntity = new LinkPreview();
			linkPreviewEntity.setTitle(form.getTitle());
			linkPreviewEntity.setImage(form.getImage());
			linkPreviewEntity.setUrl(form.getUrl());
			linkPreviewEntity.setDescription(form.getDescription());
			linkPreviewRepository.saveAndFlush(linkPreviewEntity);
		}	
		
	}	
	
}
