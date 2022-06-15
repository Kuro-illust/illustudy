package com.example.illustudy.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

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

import com.example.illustudy.entity.Bookmark;
import com.example.illustudy.entity.Favorite;
import com.example.illustudy.entity.Topic;
import com.example.illustudy.entity.UserInf;
import com.example.illustudy.form.BookmarkForm;
import com.example.illustudy.form.CommentForm;
import com.example.illustudy.form.TopicForm;
import com.example.illustudy.repository.BookmarkRepository;
import com.example.illustudy.repository.CommentRepository;
import com.example.illustudy.repository.TopicRepository;

@Controller
public class BookmarksController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private BookmarkRepository repository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private TopicsController topicsController;

	@GetMapping(path = "bookmark/{topicId}")
	public String artworks(Principal principal, @PathVariable("topicId") Long topicId, Model model) throws IOException {
		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		Bookmark entity = repository.findByUserIdAndTopicId(user.getUserId(), topicId);

		if (entity != null) {// すでにブックマークされていたら、formに登録データを渡す。
			BookmarkForm bookmarkForm = new BookmarkForm();
			bookmarkForm.setDescription(entity.getDescription());
			model.addAttribute("form", bookmarkForm);
		}
		if (entity == null) {// ブックマークされていなかった場合、新規formを作成。
			model.addAttribute("form", new BookmarkForm());
		}

		Topic topic = topicRepository.findByTopicId(topicId);
		TopicForm topicForm = topicsController.getTopic(user, topic);
		System.out.println(topicForm);
		model.addAttribute("topicForm", topicForm);

		return "bookmarks/new";
	}

	@RequestMapping(value = "/bookmark", method = RequestMethod.POST)
	public String newBookmark(Principal principal, @RequestParam("topicId") long topicId, Model model,
			@Validated @ModelAttribute("form") BookmarkForm form, BindingResult result, RedirectAttributes redirAttrs)
			throws IOException {

		String redirectUrl = "redirect:/artworks/" + topicId;

		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();

		// ブックマークされていなかったら登録
		if (repository.findByUserIdAndTopicId(user.getUserId(), topicId) == null) {
			Bookmark entity = new Bookmark();
			entity.setTopicId(topicId);
			entity.setUserId(user.getUserId());
			entity.setDescription(form.getDescription());
			repository.saveAndFlush(entity);

			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message", "ブックマークに成功しました。");

			return redirectUrl;
		}
		// ブックマークされていたら編集
		Bookmark entity = repository.findByUserIdAndTopicId(user.getUserId(), topicId);
		entity.setDescription(form.getDescription());
		repository.saveAndFlush(entity);

		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", "alert-info");
		redirAttrs.addFlashAttribute("message", "ブックマークを更新しました。");

		return redirectUrl;
	}

	@RequestMapping(value = "/bookmark", method = RequestMethod.DELETE)
	@Transactional
	public String destroy(Principal principal, @RequestParam("topicId") long topicId, RedirectAttributes redirAttrs,
			Locale locale) {
		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		Bookmark entity = repository.findByUserIdAndTopicId(user.getUserId(), topicId);

		if (entity != null) {
			repository.deleteByUserIdAndTopicId(user.getUserId(), topicId);

			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message", "ブックマークを解除しました。");
		}

		String redirectUrl = "redirect:/artworks/" + topicId;

		return redirectUrl;
	}

}
