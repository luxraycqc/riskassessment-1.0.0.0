package cn.net.aichain.edge.ms.idm.account;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = { "/idm/account" })
public final class IdmAccount {
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(final String username,final String password) {
		return "login-test";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout() {
		return "logout-test";
	}
	
}