package com.zenzsol.allygain.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.zenzsol.allygain.data.db.UserDataHandler;
import com.zenzsol.allygain.data.dto.db.AgUser;
import com.zenzsol.allygain.data.dto.db.PasswdToken;
import com.zenzsol.allygain.data.dto.db.Resource;
import com.zenzsol.allygain.data.dto.ui.AgItemUI;
import com.zenzsol.allygain.data.dto.ui.ResourceUI;
import com.zenzsol.allygain.data.dto.ui.UsersFile;
import com.zenzsol.allygain.email.EmailManager;
import com.zenzsol.allygain.security.AGUserDetails;
import com.zenzsol.allygain.security.AGUserRole;

import jakarta.validation.Valid;

@Controller
public class UserActionsController {
	private static final Logger log = Logger.getLogger(UserActionsController.class.getName());
	@Autowired
	private UserDataHandler userDataHandler;
	@Autowired
	private EmailManager emailManager;

	@GetMapping("/myaccount")
	public String getReserve(ModelMap model) {
		model.put("agItemUI", new AgItemUI());		
		return "/ag/content/user/profile";
	}
	@GetMapping("/user/uploadusers")
	public String getUploadUsers(ModelMap model) {
		model.put("usersFile", new UsersFile());		
		return "/ag/content/user/uploadusers";
	}
	@PostMapping("/user/uploadusers")
	public String uploadUsers(@Valid UsersFile usersFile, BindingResult result) {
		if (!result.hasErrors()) {
			
			try {
				AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
						.getContext().getAuthentication().getPrincipal();
				
				processUsersFile(usersFile, activeUser.getCommunityId());

				//	WebAuthenticationDetails wadet = (WebAuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
				//	log.info("upload user tracking "+activeUser.getUsername()+"-"+wadet.getRemoteAddress());


			} catch (Exception e) {
				log.info("uploadUsers error "+e);
				result.addError(new ObjectError("message", "Failed to load users."));
				return "/ag/content/user/uploadusers";
			}
			usersFile.setSubmitted(true);
		}
		return "/ag/content/user/uploadusers";
	}
	private void processUsersFile(UsersFile usersFile, String communityId) {
		InputStream inputStream = null;
		try {
			inputStream = usersFile.getUsers().getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			String line = null;       
			AgUser user = new AgUser();
			AGUserRole ar = AGUserRole.ROLE_USER;
			user.setAuthorities(ar);
			user.setCommunityId(communityId);	
			
			int count = 0;

			while ((line = br.readLine()) != null) {
				try {
					//allowing only 4 users, back end channel is used for creating more users
					if(count > 4) {
						break;
					}
					String[] userinf = line.split("\\s*,\\s*");
					user.setUnitId(userinf[0]);
					user.setEmail(userinf[1]);
					AgUser auser = userDataHandler.getUserByUsername(user.getEmail());
					if(auser != null) {
						//log.info("uploadUsers user exists "+user.getEmail());
						continue;
					}
					userDataHandler.registerAgUser(user);
					
					PasswdToken ptoken = userDataHandler.generateAndStorePasswdToken(user.getEmail());							
					emailManager.sendWelcomeEmail(user.getEmail(), ptoken.getToken());
					
					count++;
				} catch (Exception e) {
					log.info("uploadUsers failed line "+line);
				}
			}
		} catch (IOException e) {
			log.info("uploadUsers failed processing ");
		}                  
	}
	@GetMapping("/user/resource/add")
	public String getaddResource(ModelMap model) {
		model.put("resourceUI", new ResourceUI());		
		return "/ag/content/user/addresource";
	}
	@PostMapping("/user/resource/add")
	public String addResource(@Valid ResourceUI resourceUI, BindingResult result) {

		if (!result.hasErrors()) {
			try {

				AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
						.getContext().getAuthentication().getPrincipal();
				userDataHandler.addResource(createResourceDBObject(resourceUI, activeUser.getCommunityId()));
			} catch (Exception e) {
				log.info("addResource error "+e);
				result.addError(new ObjectError("message", "Failed to load users."));
				return "/ag/content/user/addresource";
			}
			resourceUI.setSubmitted(true);
		}else {
			log.info("addResource "+result.toString());	
		}
		return "/ag/content/user/addresource";
	}

	private Resource createResourceDBObject(ResourceUI resourceUI, String communityId) {
		Resource resource = new Resource();
		resource.setId(resourceUI.getId());
		resource.setName(resourceUI.getName());
		resource.setoAt(resourceUI.getOpensAt());
		resource.setcAt(resourceUI.getClosesAt());
		resource.setRd(resourceUI.getReservationDuration());
		resource.setComments(resourceUI.getComments());
		resource.setcId(communityId);
		return resource;
	}
	@GetMapping("/user/resource/view")
	public String viewResources(ModelMap model) {
		AGUserDetails activeUser = (AGUserDetails) SecurityContextHolder
				.getContext().getAuthentication().getPrincipal();
		ArrayList<Resource> itmslst = new ArrayList<Resource>();
		try {
			itmslst = userDataHandler.getResourcesByCommunity(activeUser.getCommunityId());
		} catch (Exception e) {
			log.info("view resources error "+e);
		} 
		model.put("itemslist", itmslst);		
		return "/ag/content/user/viewresources";
	}
	
	
	@GetMapping("/user/resource/modify/{resourceid}")
	public String modifyItem(@PathVariable String resourceid, ModelMap model) {
		ResourceUI resourceUI;
		try {
			Resource resource = userDataHandler.getResourceById(resourceid);
			resourceUI = getResourceUI(resource);
		} catch (Exception e) {	
			resourceUI = new ResourceUI();
			log.info("viewItem error "+e);
		}
		model.put("resourceUI", resourceUI);	
		return "/ag/content/user/addresource";
	}
	private ResourceUI getResourceUI(Resource res) {
		ResourceUI resUi = new ResourceUI();
		resUi.setId(res.getId());
		resUi.setClosesAt(res.getcAt());
		resUi.setComments(res.getComments());
		resUi.setName(res.getName());
		resUi.setOpensAt(res.getoAt());
		resUi.setReservationDuration(res.getRd());
		return resUi;
	}

	@GetMapping("/user/resource/delete/{resourceid}")
	public String deleteItem(@PathVariable String resourceid, ModelMap model) {
		
		model.put("resourceid", resourceid);
		model.put("deleted", "n");
		return "/ag/content/user/deleteresource";
	}
	@GetMapping("/user/resource/delete/{answ}/{resourceid}")
	public String deleteItemYesNo(@PathVariable String answ, @PathVariable String resourceid, ModelMap model) {
		if(answ.equals("accept")) {
			try {
				userDataHandler.deleteResource(resourceid);
			} catch (Exception e) {				
				log.info("delete resource error "+e);
				return "redirect:/user/resource/view";
			}
			model.put("deleted", "y");
			return "/ag/content/user/deleteresource";
		}		
		
		return "redirect:/user/resource/view";
	}

}

