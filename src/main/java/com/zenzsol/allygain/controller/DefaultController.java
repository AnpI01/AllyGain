package com.zenzsol.allygain.controller;

import java.util.logging.Logger;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zenzsol.allygain.data.db.RegistrationDataHandler;
import com.zenzsol.allygain.data.db.UserDataHandler;
import com.zenzsol.allygain.data.dto.db.AgUser;
import com.zenzsol.allygain.data.dto.db.Community;
import com.zenzsol.allygain.data.dto.db.Message;
import com.zenzsol.allygain.data.dto.db.PasswdToken;
import com.zenzsol.allygain.data.dto.ui.CommunityUI;
import com.zenzsol.allygain.data.dto.ui.ResetPwd;
import com.zenzsol.allygain.email.EmailManager;
import com.zenzsol.allygain.security.AGUserRole;

import jakarta.validation.Valid;

@Controller
public class DefaultController {
	private static final Logger log = Logger.getLogger(DefaultController.class.getName());

	@Autowired
	private RegistrationDataHandler regDataHandler;
	@Autowired
	private UserDataHandler userDataHandler;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private EmailManager emailManager;

	@GetMapping("/")
	public String mainPg(ModelMap model) {
		return "/ag/content/mainpage";
	}

	@GetMapping("/login")
	public String loginPage( @RequestParam( required = false) String error, ModelMap model) {
		if (error != null) {
			model.put("error", "invalid login");
		}else {
			model.put("error", "");
		}
		return "/ag/common/login";
	}
	@GetMapping("/forgotpwd")
	public String getForgotPasswordPage(ModelMap model) {
		model.addAttribute("emailerror", false);
		model.addAttribute("status", false);
		return "/ag/common/forgotpwd";
	} 
	@PostMapping("/forgotpwd")
	public String forgotPassword(@RequestParam String email, ModelMap model) {
		PasswdToken ptoken;
		boolean status=true;
		model.addAttribute("emailerror", false);
		try {
			AgUser auser = userDataHandler.getUserByUsername(email);
			if(auser == null) {
				model.addAttribute("status", false);
				model.addAttribute("emailerror", true);
				return "/ag/common/forgotpwd";
			}
			ptoken = userDataHandler.generateAndStorePasswdToken(email);
			emailManager.sendResetPasswordEmail(email, ptoken.getToken());
		} catch (Exception e) {
			status = false;
		}
		model.addAttribute("status", status);
		return "/ag/common/forgotpwd";
	}
	@GetMapping("/reginit/{token}")
	public String initLogin(@PathVariable String token, ModelMap model) {
		ResetPwd rpwd = new ResetPwd();
		try {
			PasswdToken pt = userDataHandler.getPasswordToken(token);
			if(pt != null) {
				rpwd.setToken(pt.getToken());
			}			
		} catch (Exception e) {

		}
		//if token is invalid, blank token, so reset password will fail next step
		model.put("resetPwd", rpwd);
		return "/ag/common/resetpwd";
	}

	@PostMapping("/resetpwd")
	public String resetpwd(@Valid ResetPwd rpwd, BindingResult result) {

		if (!result.hasErrors()) {
			try {
				PasswdToken pt = userDataHandler.getPasswordToken(rpwd.getToken());
				if(pt != null) {
					if(!(rpwd.getPassword().equals(rpwd.getRepassword()))) {
						result.rejectValue("password","error.resetPwd", "Password and reentered password mismatch.");
						return "/ag/common/resetpwd";
					}
					AgUser auser = userDataHandler.getUserByUsername(pt.getMail());
					auser.setPassword(passwordEncoder.encode(rpwd.getPassword()));
					userDataHandler.updateAgUser(auser);

					rpwd.setSubmitted(true);
				}else {
					result.rejectValue("password","error.resetPwd", "Error resetting password.");
				}

			} catch (Exception e) {
				result.rejectValue("password","error.resetPwd", "Error resetting password.");
			}
		}
		return "/ag/common/resetpwd";
	}

	@GetMapping("/addcommunity")
	public String addCommunityPage(ModelMap model) {
		model.put("communityUI", new CommunityUI());
		return "/ag/content/addcommunity";
	}


	@PostMapping("/addcommunity")
	public String addCommunity(@Valid CommunityUI communityUI, BindingResult result) {


		if (!EmailValidator.getInstance().isValid(communityUI.getEmail())) {
			result.rejectValue("email","error.communityUI",
					"Enter valid email address."); return "/ag/content/addcommunity";
		}


		if (!result.hasErrors()) {
			try {

				if (communityExists(communityUI)) {
					result.rejectValue("name","error.communityUI", "Community or spoc is already registered on AllyGain.");
					return "/ag/content/addcommunity";
				}
				Community communityDB = createDBCommunityObject(communityUI);
				regDataHandler.registerCommunity(communityDB);
				userDataHandler.registerAgUser(createDBUserObject(communityUI, communityDB.getId()));

				communityUI.setSubmitted(true);
			} catch (Exception e) {
				result.addError(new ObjectError("message", "Community registration failed."));
				log.info("add community exception: " + e);
			}

		}
		return "/ag/content/addcommunity";
	}

	private boolean communityExists(CommunityUI community) throws Exception{

		Community commun = regDataHandler.getCommunityByCommunityName(community.getName());

		if (commun != null && commun.getName().equalsIgnoreCase(community.getName())
				&& commun.getCity().equalsIgnoreCase(community.getCity())
				&& commun.getCountry().equalsIgnoreCase(community.getCountry())) {
			return true;
		}

		commun = regDataHandler.getCommunityBySPOCPhone(community.getPhone());

		if (commun != null) {
			return true;
		}

		AgUser aguser = userDataHandler.getUserByUsername(community.getEmail());
		if (aguser != null) {
			return true;
		}
		log.info("no community exists");


		return false;

	}

	private Community createDBCommunityObject(CommunityUI community) {
		Community communityDB = new Community();
		communityDB.setName(community.getName());
		communityDB.setAddressOne(community.getAddressOne());
		communityDB.setAddressTwo(community.getAddressTwo());
		communityDB.setCity(community.getCity());
		communityDB.setCountry(community.getCountry());
		communityDB.setPhone(community.getPhone());
		communityDB.setSpoc(community.getSpoc());
		communityDB.setState(community.getState());

		return communityDB;
	}
	private AgUser createDBUserObject(CommunityUI community, String communityId) {
		AgUser agUser = new AgUser();

		agUser.setEmail(community.getEmail());
		agUser.setPassword(passwordEncoder.encode(community.getPassword()));
		agUser.setCommunityId(communityId);

		//Set<AGUserRole> roles = EnumSet.noneOf(AGUserRole.class);
		//roles.add(AGUserRole.ROLE_AGADMIN);
		AGUserRole ar = AGUserRole.ROLE_AGADMIN;
		agUser.setAuthorities(ar);
		return agUser;
	}

	@GetMapping("/error")
	public String showError(ModelMap model) {
		return "/ag/common/error";
	}


	@GetMapping("/comp/about")
	public String showAboutUs(ModelMap model) {
		return "/ag/common/aboutus";
	}

	@GetMapping("/comp/privacy")
	public String showPrivacy(ModelMap model) {

		return "/ag/common/privacy";
	}

	@GetMapping("/comp/termsofuse")
	public String showTC(ModelMap model) {

		return "/ag/common/termcons";
	}

	@GetMapping("/comp/contact")
	public String showContactUs(ModelMap model) {
		model.put("message", new Message());
		return "/ag/content/contactus";
	}
	@PostMapping("/comp/contact")
	public String contactUs(@Valid Message message, BindingResult result) {

		if(!result.hasErrors()) {
			try {
				userDataHandler.addMessage(message);
			} catch (Exception e) {
				result.addError(new ObjectError("message", "Failed to save the message."));
			}
			message.setSubmitted(true);

		}
		return "/ag/content/contactus";
	}
}
