package com.example.illustudy.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.illustudy.entity.Topic;
import com.example.illustudy.entity.Topic_Hashtag;
import com.example.illustudy.entity.UserInf;
import com.example.illustudy.entity.Favorite;
import com.example.illustudy.entity.Hashtag;
import com.example.illustudy.entity.Bookmark;
import com.example.illustudy.entity.Comment;
import com.example.illustudy.form.TopicForm;
import com.example.illustudy.form.UserForm;
import com.example.illustudy.form.BookmarkForm;
import com.example.illustudy.form.CommentForm;
import com.example.illustudy.form.FavoriteForm;
import com.example.illustudy.form.HashtagForm;
import com.example.illustudy.repository.TopicRepository;
import com.example.illustudy.repository.Topic_HashtagRepository;
import com.example.illustudy.repository.HashtagRepository;

@Controller
public class TopicsController {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private TopicRepository repository;

	@Autowired
	private HashtagRepository hashtagRepository;

	@Autowired
	private Topic_HashtagRepository topic_hashtagRepository;

	@Autowired
	private HttpServletRequest request;

	@Value("${image.local:false}")
	private String imageLocal;

	// ユーザー認証が不要なバージョン
	@GetMapping(path = "topics")
	public String index(Model model) throws IOException {

		Iterable<Topic> topics = repository.findAllByOrderByUpdatedAtDesc();
		List<TopicForm> list = new ArrayList<>();

		model.addAttribute("list", list);
		for (Topic entity : topics) {
			TopicForm form = getThumbnail(entity);
			list.add(form);
		}
		model.addAttribute("list", list);

		return "topics/index";
	}

	// 画像詳細ページ
	/*
	 * form:元画像のフォーム form2:ユーザー投稿画像一覧に用いるサムネイル画像のフォーム
	 */
	@GetMapping(path = "artworks/{topicId}")
	public String artworks(Principal principal, @PathVariable("topicId") Long topicId, Model model) throws IOException {

		model.addAttribute("commentForm", new CommentForm());

		Authentication authentication = (Authentication) principal;
		UserInf user = null;
		if (authentication != null) {
			user = (UserInf) authentication.getPrincipal();
		}

		Topic topic = repository.findByTopicId(topicId);
		TopicForm form = null;

		Iterable<Topic> userTopics = repository.findByUserIdOrderByUpdatedAtDesc(topic.getUserId());
		List<TopicForm> list = new ArrayList<>();

		if (topic != null) {
			form = getTopic(user, topic);
			model.addAttribute("form", form);
		}

		for (Topic entity : userTopics) {
			TopicForm form2 = getThumbnail(entity);
			list.add(form2);
		}
		model.addAttribute("artworkList", list);

		return "artworks/index";
	}

	// 元画像の取得
	public TopicForm getTopic(UserInf user, Topic entity) throws FileNotFoundException, IOException {
		// 循環回避
		modelMapper.typeMap(Topic.class, TopicForm.class).addMappings(mapper -> mapper.skip(TopicForm::setUser));
		modelMapper.typeMap(Topic.class, TopicForm.class).addMappings(mapper -> mapper.skip(TopicForm::setFavorites));
		modelMapper.typeMap(Topic.class, TopicForm.class).addMappings(mapper -> mapper.skip(TopicForm::setBookmarks));
		modelMapper.typeMap(Topic.class, TopicForm.class).addMappings(mapper -> mapper.skip(TopicForm::setComments));
		modelMapper.typeMap(Topic.class, TopicForm.class).addMappings(mapper -> mapper.skip(TopicForm::setHashtags));

		boolean isImageLocal = false;
		if (imageLocal != null) {
			isImageLocal = new Boolean(imageLocal);
		}
		TopicForm form = modelMapper.map(entity, TopicForm.class);

		if (isImageLocal) {
			try (InputStream is = new FileInputStream(new File(entity.getPath()));
					ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				byte[] indata = new byte[10240 * 16];
				int size;
				while ((size = is.read(indata, 0, indata.length)) > 0) {
					os.write(indata, 0, size);
				}
				StringBuilder data = new StringBuilder();
				data.append("data:");
				data.append(getMimeType(entity.getPath()));
				data.append(";base64,");

				data.append(new String(Base64Utils.encode(os.toByteArray()), "ASCII"));
				form.setImageData(data.toString());
			}
		}

		UserForm userForm = modelMapper.map(entity.getUser(), UserForm.class);
		form.setUser(userForm);

		// お気に入り取得
		List<FavoriteForm> favorites = new ArrayList<FavoriteForm>();
		for (Favorite favoriteEntity : entity.getFavorites()) {
			FavoriteForm favorite = modelMapper.map(favoriteEntity, FavoriteForm.class);
			favorites.add(favorite);

			if (user != null && user.getUserId().equals(favoriteEntity.getUserId())) {
				form.setFavorite(favorite);
			}
		}

		form.setFavorites(favorites);

		// ブックマークの取得
		List<BookmarkForm> bookmarks = new ArrayList<BookmarkForm>();
		for (Bookmark bookmarkEntity : entity.getBookmarks()) {
			BookmarkForm bookmark = modelMapper.map(bookmarkEntity, BookmarkForm.class);
			bookmarks.add(bookmark);

			if (user != null && user.getUserId().equals(bookmarkEntity.getUserId())) {
				form.setBookmark(bookmark);
			}
		}
		form.setBookmarks(bookmarks);

		// コメント取得
		List<CommentForm> comments = new ArrayList<CommentForm>();
		for (Comment commentEntity : entity.getComments()) {
			CommentForm comment = modelMapper.map(commentEntity, CommentForm.class);
			comments.add(comment);
		}
		Collections.reverse(comments); // コメントを逆順に並び替え（新着順）
		form.setComments(comments);

		// ハッシュタグの取得
		List<HashtagForm> hashtags = new ArrayList<HashtagForm>();
		for (Topic_Hashtag topic_hashtagEntity : entity.getTopic_hashtags()) {
			HashtagForm hashtag = modelMapper.map(topic_hashtagEntity, HashtagForm.class);
			hashtag.setTagName(hashtagRepository.findByHashtagId(hashtag.getHashtagId()).getTagName());

			hashtags.add(hashtag);
		}
		form.setHashtags(hashtags);

		System.out.println("--------getTopicの取得結果---------------");
		System.out.println(form);
		System.out.println("--------------ここまで--------------------");
		return form;
	}

	// サムネイル画像取得
	public TopicForm getThumbnail(Topic entity) throws FileNotFoundException, IOException {

		boolean isImageLocal = false;
		if (imageLocal != null) {
			isImageLocal = new Boolean(imageLocal);
		}
		TopicForm form = modelMapper.map(entity, TopicForm.class);

		if (isImageLocal) {
			try (InputStream is = new FileInputStream(new File(entity.getThumbnailPath()));
					ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				byte[] indata = new byte[10240 * 16];
				int size;
				while ((size = is.read(indata, 0, indata.length)) > 0) {
					os.write(indata, 0, size);
				}
				StringBuilder data = new StringBuilder();
				data.append("data:");
				data.append(getMimeType(entity.getThumbnailPath()));
				data.append(";base64,");

				data.append(new String(Base64Utils.encode(os.toByteArray()), "ASCII"));
				form.setImageData(data.toString());
			}
		}

		UserForm userForm = modelMapper.map(entity.getUser(), UserForm.class);
		form.setUser(userForm);

		return form;
	}

	private String getMimeType(String path) {
		String extension = FilenameUtils.getExtension(path);
		String mimeType = "image/";
		switch (extension) {
		case "jpg":
		case "jpeg":
			mimeType += "jpeg";
			break;
		case "png":
			mimeType += "png";
			break;
		case "gif":
			mimeType += "gif";
			break;
		}
		return mimeType;
	}

	@GetMapping(path = "/topics/new")
	public String newTopic(Model model) {
		model.addAttribute("form", new TopicForm());
		return "topics/new";
	}

	@RequestMapping(value = "/topic", method = RequestMethod.POST)
	public String create(Principal principal, @Validated @ModelAttribute("form") TopicForm form, BindingResult result,
			Model model, @RequestParam MultipartFile image, @RequestParam MultipartFile thumbnailImage,
			RedirectAttributes redirAttrs) throws IOException {
		if (result.hasErrors()) {
			model.addAttribute("hasMessage", true);
			model.addAttribute("class", "alert-danger");
			model.addAttribute("message", "投稿に失敗しました。");
			return "topics/new";
		}

		boolean isImageLocal = false;
		if (imageLocal != null) {
			isImageLocal = new Boolean(imageLocal);
		}

		Topic entity = new Topic();
		Authentication authentication = (Authentication) principal;
		UserInf user = (UserInf) authentication.getPrincipal();
		entity.setUserId(user.getUserId());
		File destFile = null;
		if (isImageLocal) {
			destFile = saveImageLocal(image, entity);
			entity.setPath(destFile.getAbsolutePath());
			destFile = saveImageLocal(thumbnailImage, entity);
			entity.setThumbnailPath(destFile.getAbsolutePath());

		} else {
			entity.setPath("");
			entity.setThumbnailPath("");
		}
		entity.setDescription(form.getDescription());
		repository.saveAndFlush(entity);

		Long topicId = entity.getTopicId();

		addHashtags(form, topicId);

		redirAttrs.addFlashAttribute("hasMessage", true);
		redirAttrs.addFlashAttribute("class", "alert-info");
		redirAttrs.addFlashAttribute("message", "投稿に成功しました。");

		return "redirect:/topics";
	}

	// タグ判別機能
	/*
	 * list化前 private void addHashtags(TopicForm form,Long topicId) { Hashtag entity
	 * = new Hashtag();
	 * 
	 * entity.setTopicId(topicId);
	 * 
	 * entity.setTagName(form.getHashtag().getTagName());
	 * hashtagRepository.saveAndFlush(entity); }
	 */

	// タグ判別機能
	/*
	 * list化後 private void addHashtags(TopicForm form,Long topicId) { Hashtag entity
	 * = new Hashtag(); entity.setTopicId(topicId);
	 * 
	 * List<String> tagnames = new ArrayList<>();; List<String> tagnamesForm =
	 * form.getHashtag().getTagName(); for (String tagname : tagnamesForm) {
	 * String[] data = tagname.split(",");
	 * 
	 * tagnames.add(data[0]); }
	 * 
	 * entity.setTagName(tagnames); hashtagRepository.saveAndFlush(entity); }
	 */

	private void addHashtags(TopicForm form, Long topicId) {

		Long hashtagId;

		String[] tagnames = form.getHashtag().getTagName().split(",");// タグを，区切りに設定
		for (String tagname : tagnames) {
			if (hashtagRepository.findByTagName(tagname) == null) {
				Hashtag hashtagEntity = new Hashtag();
				hashtagEntity.setTagName(tagname);
				hashtagRepository.saveAndFlush(hashtagEntity);
				hashtagId = hashtagEntity.getHashtagId();
			} else {
				hashtagId = hashtagRepository.findByTagName(tagname).getHashtagId();
			}
			Topic_Hashtag topic_hashtagEntity = new Topic_Hashtag();
			topic_hashtagEntity.setTopicId(topicId);
			topic_hashtagEntity.setHashtagId(hashtagId);
			topic_hashtagRepository.saveAndFlush(topic_hashtagEntity);
		}
	}

	private File saveImageLocal(MultipartFile image, Topic entity) throws IOException {
		File uploadDir = new File("/uploads");
		uploadDir.mkdir();

		String uploadsDir = "/uploads/";
		String realPathToUploads = request.getServletContext().getRealPath(uploadsDir);
		if (!new File(realPathToUploads).exists()) {
			new File(realPathToUploads).mkdir();
		}
		String fileName = image.getOriginalFilename();
		File destFile = new File(realPathToUploads, fileName);
		image.transferTo(destFile);

		return destFile;
	}

}
