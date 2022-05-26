package com.example.illustudy.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

import org.modelmapper.ModelMapper;
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
import com.example.illustudy.entity.Topic;
import com.example.illustudy.entity.UserInf;
import com.example.illustudy.form.CommentForm;
import com.example.illustudy.form.TopicForm;
import com.example.illustudy.repository.CommentRepository;

@Controller
public class CommentsController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CommentRepository repository;

	@RequestMapping(value = "comment/new", method = RequestMethod.POST)
	public String newComment(Principal principal, @RequestParam("topic_id") long topicId, Model model,
			@Validated @ModelAttribute("commentForm") CommentForm commentForm, BindingResult result, RedirectAttributes redirAttrs)
			throws IOException{
		
		String redirectUrl = "redirect:/artworks/" + topicId;
		
		if (result.hasErrors()) {
			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-danger");
			redirAttrs.addFlashAttribute("message", "コメントを入力してください。");	
			return redirectUrl;
		}
		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		Comment entity = new Comment();
		entity.setUserId(user.getUserId());
		entity.setTopicId(topicId);
		entity.setDescription(commentForm.getDescription());
		
		repository.saveAndFlush(entity);

		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", "alert-info");
		redirAttrs.addFlashAttribute("message", "コメントをしました。");			

		return redirectUrl;
	}

	@GetMapping(path = "comment/{commentId}/delete")
	public String deleteComment(Principal principal, @PathVariable("commentId") Long commentId, 
			RedirectAttributes redirAttrs, Locale locale)
			throws IOException{
		
		Comment comment = repository.findByCommentId(commentId);
		comment.setDeleted(true);
		Long topicId = comment.getTopicId();
		repository.saveAndFlush(comment);
		
		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", "alert-info");
		redirAttrs.addFlashAttribute("message", "コメントを削除しました。");	
		
		String redirectUrl = "redirect:/artworks/" + topicId;
		return redirectUrl;
	}
}
