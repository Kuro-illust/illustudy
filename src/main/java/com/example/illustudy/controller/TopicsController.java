package com.example.illustudy.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
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
import com.example.illustudy.entity.UserInf;
import com.example.illustudy.form.TopicForm;
import com.example.illustudy.form.UserForm;
import com.example.illustudy.repository.TopicRepository;

@Controller
public class TopicsController {

    @Autowired
    private ModelMapper modelMapper;
	
	@Autowired
    private TopicRepository repository;
	
    @Autowired
    private HttpServletRequest request;
    
    @Value("${image.local:false}")
    private String imageLocal;

    
    //ユーザー認証が不要なバージョン
    @GetMapping(path = "topics")
    public String index(Model model) throws IOException{
   	 
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
    
    
     
	 //画像詳細ページ
    /*
     * form:元画像のフォーム
     * form:ユーザー投稿画像一覧に用いるサムネイル画像のフォーム
     */
	 @GetMapping(path = "artworks/{topicID}")
	    public String artworks(@PathVariable("topicID") Long topicID, Model model) throws IOException{
		
		 	Topic topic = repository.findByTopicId(topicID);
		 	TopicForm form = null;
		 	
		 	Iterable<Topic> userTopics = repository.findByUserIdOrderByUpdatedAtDesc(topic.getUserId());
		 	List<TopicForm> list = new ArrayList<>();
		 			 	
		 	if(topic != null) {
		 		form = getTopic(topic);	
		 		model.addAttribute("form",form);
		 	}
		 	
		 	for (Topic entity : userTopics) {
	             TopicForm form2 = getThumbnail(entity);
	             list.add(form2);
	         } 
		 	model.addAttribute("artworkList",list);
		 	
		 		        
	        return "artworks/index";
	    }
     
	 //元画像の取得
	public TopicForm getTopic(Topic entity) throws FileNotFoundException, IOException{
		boolean isImageLocal = false;
        if(imageLocal != null) {
        	isImageLocal = new Boolean(imageLocal);
        }
    	TopicForm form = modelMapper.map(entity,TopicForm.class);
    	
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
         
		return form;
	}
	 
	 
    
	//サムネイル画像取得
    public TopicForm getThumbnail(Topic entity) throws FileNotFoundException, IOException{	
        
        boolean isImageLocal = false;
        if(imageLocal != null) {
        	isImageLocal = new Boolean(imageLocal);
        }
    	TopicForm form = modelMapper.map(entity,TopicForm.class);
    	
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
	            Model model, @RequestParam MultipartFile image, @RequestParam MultipartFile thumbnailImage, RedirectAttributes redirAttrs)
	            throws IOException {
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

	        redirAttrs.addFlashAttribute("hasMessage", true);
	        redirAttrs.addFlashAttribute("class", "alert-info");
	        redirAttrs.addFlashAttribute("message", "投稿に成功しました。");

	        return "redirect:/topics";
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
