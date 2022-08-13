package com.example.illustudy.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.illustudy.entity.Comment;
import com.example.illustudy.entity.Favorite;
import com.example.illustudy.entity.Hashtag;
import com.example.illustudy.entity.Topic;
import com.example.illustudy.entity.Topic_Hashtag;
import com.example.illustudy.entity.UserInf;
import com.example.illustudy.form.CommentForm;
import com.example.illustudy.form.HashtagForm;
import com.example.illustudy.form.TopicForm;
import com.example.illustudy.repository.HashtagRepository;
import com.example.illustudy.repository.TopicRepository;
import com.example.illustudy.repository.Topic_HashtagRepository;



@Controller
public class HashtagsController {

	@Autowired
	private Topic_HashtagRepository repository;
	
	@Autowired
	private TopicsController topicsController;
	
	@Autowired
	private HashtagRepository hashtagRepository;

	
	@Autowired
	private TopicRepository topicRepository;
	
	@GetMapping(path = "hashtag/artworks/{topicId}")
	public String artworks(Principal principal, @PathVariable("topicId") Long topicId, Model model) throws IOException {
		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
	
		model.addAttribute("form", new HashtagForm());

		Topic topic = topicRepository.findByTopicId(topicId);
		TopicForm topicForm = topicsController.getTopic(user, topic);
		System.out.println(topicForm);
		model.addAttribute("topicForm", topicForm);
		
		return "hashtags/edit";
	}
	/*このマッピングだとログインしてない場合、ログインページに飛ばない
	@GetMapping(path = "artworks/{topicId}/hashtag/edit")
	public String edit(Principal principal, @PathVariable("topicId") Long topicId, Model model) throws IOException {
		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		
		
		model.addAttribute("form", new HashtagForm());
		
		Topic topic = topicRepository.findByTopicId(topicId);
		TopicForm topicForm = topicsController.getTopic(user, topic);
		System.out.println(topicForm);
		model.addAttribute("topicForm", topicForm);
		
		return "hashtags/edit";
	}
		*/
		
		
	@RequestMapping(value = "hashtag/add", method = RequestMethod.POST)
	public String edit(Principal principal,@RequestParam("topicId") long topicId, @ModelAttribute("form") HashtagForm form, RedirectAttributes redirAttrs,
			Locale locale) {		
		
		topicsController.addHashtags(form, topicId);
		
		String redirectUrl = "redirect:/hashtag/artworks/" + topicId;
	return redirectUrl;
	}
	
	@RequestMapping(value = "hashtag/delete", method = RequestMethod.DELETE)
	@Transactional
	public String destroy(Principal principal,@RequestParam("topicId") long topicId, @RequestParam("hashtagId") long hashtagId, RedirectAttributes redirAttrs,
			Locale locale) {

		repository.deleteByTopicIdAndHashtagId(topicId,hashtagId);
		
			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message", "タグを削除しました。");


		String redirectUrl = "redirect:/hashtag/artworks/" + topicId;

		return redirectUrl;
	}

	
}
