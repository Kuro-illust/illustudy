package com.example.illustudy.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.illustudy.entity.Bookmark_Hashtag;
import com.example.illustudy.entity.Hashtag;
import com.example.illustudy.entity.Topic;
import com.example.illustudy.entity.Topic_Hashtag;
import com.example.illustudy.entity.UserInf;
import com.example.illustudy.form.BookmarktagForm;
import com.example.illustudy.form.HashtagForm;
import com.example.illustudy.form.TopicForm;
import com.example.illustudy.repository.Bookmark_HashtagRepository;
import com.example.illustudy.repository.HashtagRepository;
import com.example.illustudy.repository.TopicRepository;
import com.example.illustudy.repository.Topic_HashtagRepository;

@Controller
public class BookmarktagsController {

	@Autowired
	private Bookmark_HashtagRepository repository;
	
	@Autowired
	private TopicsController topicsController;
	
	@Autowired
	private HashtagRepository hashtagRepository;

	
	@Autowired
	private TopicRepository topicRepository;
	
	
	@RequestMapping(value = "bookmarktag/delete", method = RequestMethod.DELETE)
	@Transactional
	public String destroy(Principal principal,@RequestParam("bookmarkId") long bookmarkId, @RequestParam("hashtagId") long hashtagId, @RequestParam("topicId") long topicId, RedirectAttributes redirAttrs,
			Locale locale) {

		repository.deleteByBookmarkIdAndHashtagId(bookmarkId,hashtagId);
		
			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message", "タグを削除しました。");


		String redirectUrl = "redirect:/bookmark/" + topicId;

		return redirectUrl;
	}
	
/*		
	@RequestMapping(value = "bookmarktag/add", method = RequestMethod.POST)
	public String edit(Principal principal,@RequestParam("topicId") long topicId, @ModelAttribute("form") BookmarktagForm form, RedirectAttributes redirAttrs,
			Locale locale) {		
		
		addBookmarktags(form, bookmarkId);
		
		String redirectUrl = "redirect:bookmark/{topicId}";
	return redirectUrl;
	}
*/	
	
	/*
	@RequestMapping(value = "hashtag/delete", method = RequestMethod.DELETE)
	@Transactional
	public String destroy(Principal principal,@RequestParam("topicId") long topicId, @RequestParam("hashtagId") long hashtagId, RedirectAttributes redirAttrs,
			Locale locale) {

		repository.deleteByBookmarkIdAndHashtagId(topicId,hashtagId);
		
			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message", "タグを削除しました。");


		String redirectUrl = "redirect:/artworks/" + topicId + "/hashtag/edit";

		return redirectUrl;
	}

*/
	/*
	public void addBookmarktags(BookmarktagForm form, Long bookmarkId) {

		Long hashtagId;
		
		//タグが未入力でなかったら登録
		if (form.getTagName().length() != 0) {

			String[] tagnames = form.getTagName().split(",");// タグを，区切りに設定
			for (String tagname : tagnames) {
				if (hashtagRepository.findByTagName(tagname) == null) {
					Hashtag hashtagEntity = new Hashtag();
					hashtagEntity.setTagName(tagname);
					hashtagRepository.saveAndFlush(hashtagEntity);
					hashtagId = hashtagEntity.getHashtagId();
				} else {
					hashtagId = hashtagRepository.findByTagName(tagname).getHashtagId();
				}
				Bookmark_Hashtag bookmark_hashtagEntity = new Bookmark_Hashtag();
				bookmark_hashtagEntity.setBookmarkId(bookmarkId);
				bookmark_hashtagEntity.setHashtagId(hashtagId);
				repository.saveAndFlush(bookmark_hashtagEntity);
			}
		}
	}
	*/
	
}

	