package com.zenzsol.allygain.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import com.zenzsol.allygain.data.dto.ui.AgItemUI;
import com.zenzsol.allygain.data.dto.ui.ShareItemUI;

import jakarta.servlet.http.HttpServletRequest;

@Component
@ControllerAdvice
public class GlobalExceptionHandler {
	
	// Image upload exception handler for sell item and share item
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ModelAndView fileUploadmaxSizeExceededExceptionHandler(HttpServletRequest req,
			MaxUploadSizeExceededException e) {
		
		ModelAndView mav = new ModelAndView();
		
		if (req.getHeader("referer").contains("share")) {
			mav.setViewName("/ag/content/share/addshareitem");						 
			mav.addObject("shareItemUI", new ShareItemUI());
		}else {
			mav.setViewName("/ag/content/trade/additem");
			mav.addObject("agItemUI", new AgItemUI());
		}		
		
		mav.addObject("bigfile", "y");
		
		return mav;
	}
	
	@ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	 
}
