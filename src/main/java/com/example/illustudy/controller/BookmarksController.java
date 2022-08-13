package com.example.illustudy.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
import com.example.illustudy.entity.Bookmark_Hashtag;
import com.example.illustudy.entity.Favorite;
import com.example.illustudy.entity.Hashtag;
import com.example.illustudy.entity.Topic;
import com.example.illustudy.entity.Topic_Hashtag;
import com.example.illustudy.entity.UserInf;
import com.example.illustudy.form.BookmarkForm;
import com.example.illustudy.form.BookmarktagForm;
import com.example.illustudy.form.CommentForm;
import com.example.illustudy.form.HashtagForm;
import com.example.illustudy.form.TopicForm;
import com.example.illustudy.repository.BookmarkRepository;
import com.example.illustudy.repository.Bookmark_HashtagRepository;
import com.example.illustudy.repository.CommentRepository;
import com.example.illustudy.repository.HashtagRepository;
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
	private Bookmark_HashtagRepository bookmark_hashtagRepository;

	@Autowired
	private HashtagRepository hashtagRepository;

	@Autowired
	private TopicsController topicsController;

	@GetMapping(path = "bookmark/{topicId}")
	public String artworks(Principal principal, @PathVariable("topicId") Long topicId, Model model) throws IOException {
		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		Bookmark entity = repository.findByUserIdAndTopicId(user.getUserId(), topicId);
		BookmarkForm bookmarkForm = new BookmarkForm();
		List<BookmarktagForm> bookmarktags = new ArrayList<BookmarktagForm>();

		if (entity != null) {// すでにブックマークされていたら、formに登録データを渡す。

			bookmarkForm.setDescription(entity.getDescription());

			/*
			 * フォームにそのまま、ブックマークタグ情報を渡す方法 System.out.println("てってえええええええええええええええ");
			 * System.out.println(entity.getBookmarkId());
			 * 
			 * //フォームに登録タグ情報を出力 String tagnames = connectTags(entity);
			 * System.out.println(tagnames); BookmarktagForm bookmarktag = new
			 * BookmarktagForm(); bookmarktag.setTagName(tagnames);
			 * bookmarkForm.setBookmarktag(bookmarktag);
			 * 
			 * //bookmarkForm.getBookmarktag().setTagName(tagnames);
			 * System.out.println("いせえびいいいいいいいいいいいいい"); //Iterable<Bookmark_Hashtag>
			 * bookmarktagEntity =
			 * bookmark_hashtagRepository.findByBookmarkId(entity.getBookmarkId());
			 * 
			 */
			// ブックマークタグの取得
			for (Bookmark_Hashtag bookmark_hashtagEntity : bookmark_hashtagRepository
					.findByBookmarkId(entity.getBookmarkId())) {
				BookmarktagForm bookmarktag = modelMapper.map(bookmark_hashtagEntity, BookmarktagForm.class);
				bookmarktag.setTagName(
						hashtagRepository.findByHashtagId(bookmark_hashtagEntity.getHashtagId()).getTagName());

				System.out.println("えええええええええええええええええええええ");
				System.out.println(bookmarktag);
				System.out.println("おおおおおおおおおおおおおおおおおおお");

				bookmarktags.add(bookmarktag);

			}

		}
		if (entity == null) {// ブックマークされていなかった場合、そのまま

		}
		bookmarkForm.setBookmarktags(bookmarktags);
		model.addAttribute("form", bookmarkForm);

		Topic topic = topicRepository.findByTopicId(topicId);
		TopicForm topicForm = topicsController.getTopic(user, topic);
		// System.out.println(topicForm);
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

			System.out.println("ブックマークコメント");
			System.out.println(form.getBookmarktag());
			System.out.println("ブックマークコメント終了");

			addBookmarktags(form.getBookmarktag(), entity.getBookmarkId());

			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message", "ブックマークに成功しました。");

			return redirectUrl;
		}
		// ブックマークされていたら編集
		Bookmark entity = repository.findByUserIdAndTopicId(user.getUserId(), topicId);
		entity.setDescription(form.getDescription());
		repository.saveAndFlush(entity);
		addBookmarktags(form.getBookmarktag(), entity.getBookmarkId());

		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", "alert-info");
		redirAttrs.addFlashAttribute("message", "ブックマークを更新しました。");

		return redirectUrl;
	}

	// ブックマークタグの追加
	public void addBookmarktags(BookmarktagForm form, Long bookmarkId) {

		Long hashtagId;

		// formのタグ欄が未入力でなかったら登録
		if (form.getTagName().length() != 0) {

			String[] tagnames = form.getTagName().replaceAll("　", " ").split("\\s+");// タグを空白区切りに設定

			List<String> tagnamesList = new ArrayList<>(List.of(tagnames));
			// tagnamesResultListにはtagnames内の重複要素を排除したものを格納
			List<String> tagnamesResultList = tagnamesList.stream().distinct().collect(Collectors.toList());

			for (String tagname : tagnamesResultList) {
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
				if (bookmark_hashtagRepository.findByBookmarkIdAndHashtagId(bookmarkId, hashtagId) == null) {// 重複するものは登録しないようにする。
					bookmark_hashtagRepository.saveAndFlush(bookmark_hashtagEntity);
				}
			}
		}

	}

	// 現在のブックマークタグを","で連結
	public String connectTags(Bookmark entity) {
		List<String> tagnameList = new ArrayList<String>();

		Iterable<Bookmark_Hashtag> bookmarktagEntity = bookmark_hashtagRepository
				.findByBookmarkId(entity.getBookmarkId());

		for (Bookmark_Hashtag bookmarktag : bookmarktagEntity) {
			tagnameList.add(hashtagRepository.findByHashtagId(bookmarktag.getHashtagId()).getTagName());
		}
		String tagnames = String.join(",", tagnameList);

		return tagnames;
	}

	@RequestMapping(value = "/bookmark", method = RequestMethod.DELETE)
	@Transactional
	public String destroy(Principal principal, @RequestParam("topicId") long topicId, RedirectAttributes redirAttrs,
			Locale locale) {
		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		Bookmark entity = repository.findByUserIdAndTopicId(user.getUserId(), topicId);

		if (entity != null) {
			// ブックマークタグが登録されていたら、ブックマークタグを削除
			if (bookmark_hashtagRepository.findByBookmarkId(entity.getBookmarkId()) != null) {
				bookmark_hashtagRepository.deleteByBookmarkId(entity.getBookmarkId());
			}
			repository.deleteByUserIdAndTopicId(user.getUserId(), topicId);

			redirAttrs.addFlashAttribute("hasMessage", true);
			redirAttrs.addFlashAttribute("class", "alert-info");
			redirAttrs.addFlashAttribute("message", "ブックマークを解除しました。");
		}

		String redirectUrl = "redirect:/artworks/" + topicId;

		return redirectUrl;
	}

}
