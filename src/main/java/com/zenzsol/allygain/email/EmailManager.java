package com.zenzsol.allygain.email;

public interface EmailManager {
	
	public void sendWelcomeEmail(String email, String token) ;
	public void sendResetPasswordEmail(String email, String token) ;
	public void borrowItemInformOwner(String email, String item, String borrowerResUnitId) ;
	public void remindBorrowerReturnItem(String email, String item, String ownerResUnitId) ;


}
