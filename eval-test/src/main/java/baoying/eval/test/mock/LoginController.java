package baoying.eval.test.mock;

public class LoginController {
    public LoginService loginService;

    public String login(UserForm userForm){
        System.out.println("LoginController - login enter");
        if(null == userForm){
            return "ERROR";
        }else{
            boolean logged;

            try {
                logged = loginService.login(userForm);
            } catch (Exception e) {
                return "ERROR";
            }

            if(logged){
                loginService.setCurrentUser(userForm.getUsername());
                return "OK";
            }else{
                return "KO";
            }
        }
    }
}