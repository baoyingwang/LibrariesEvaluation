package baoying.eval.test.mock;

/**
 * all from https://www.baeldung.com/mockito-vs-easymock-vs-jmockit
 */
public class LoginService {
    private LoginDao loginDao;
    private String currentUser;

    public boolean login(UserForm userForm) {
        assert null != userForm;
        int loginResults = loginDao.login(userForm);
        switch (loginResults){
            case 1:
                return true;
            default:
                return false;
        }
    }

    public void setLoginDao(LoginDao loginDao){
        this.loginDao = loginDao;
    }
    public void setCurrentUser(String username) {
        if(null != username){
            this.currentUser = username;
        }
    }
}

class LoginDao {
    public int login(UserForm userForm){
        return 0;
    }
}

class UserForm {
    public String password;
    public String username;
    public String getUsername(){
        return username;
    }
}

