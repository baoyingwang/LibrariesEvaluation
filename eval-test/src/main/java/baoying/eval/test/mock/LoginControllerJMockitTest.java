package baoying.eval.test.mock;

import mockit.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


//@RunWith(JMockit.class) TODO - baoying  - 无法找到import mockit.integration.junit4.JMockit;
public class LoginControllerJMockitTest {
    @Injectable
    private LoginDao loginDao;

    @Injectable
    private LoginService loginService;

    @Tested
    private LoginController loginController;

    /**
     * Verifying No Calls to Mock
     */
    @Test
    public void assertThatNoMethodHasBeenCalled() {
        loginController.login(null);
        new FullVerifications(loginService) {};
    }

    /**
     * Defining Mocked Method Calls and Verifying Calls to Mocks
     */
    @Test
    public void assertTwoMethodsHaveBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        new Expectations() {{
            loginService.login(userForm); result = true;
            loginService.setCurrentUser("foo");
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        new FullVerifications(loginService) {};
    }

    /**
     * Defining Mocked Method Calls and Verifying Calls to Mocks
     */
    @Test
    public void assertOnlyOneMethodHasBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        new Expectations() {{
            loginService.login(userForm); result = false;
            // no expectation for setCurrentUser
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("KO", login);
        new FullVerifications(loginService) {};
    }

    /**
     * Mocking Exception Throwing
     */
    @Test
    public void mockExceptionThrowing() {
        UserForm userForm = new UserForm();
        new Expectations() {{
            loginService.login(userForm); result = new IllegalArgumentException();
            // no expectation for setCurrentUser
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("ERROR", login);
        new FullVerifications(loginService) {};
    }


    /**
     * Mocking an Object to Pass Around
     */
    @Test
    public void mockAnObjectToPassAround(@Mocked UserForm userForm) {
        new Expectations() {{
            userForm.getUsername(); result = "foo";
            loginService.login(userForm); result = true;
            loginService.setCurrentUser("foo");
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        new FullVerifications(loginService) {};
        new FullVerifications(userForm) {};
    }


    /**
     * Custom Argument Matching
     */
    @Test
    public void argumentMatching() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        // default matcher
        new Expectations() {{
            loginService.login((UserForm) any);
            result = true;
            // complex matcher
            //TODO baoying withArgThat无法识别 - 可能与开头的JMockit.class无法识别有关系，就是依赖没有设定好
//            loginService.setCurrentUser(withArgThat(new BaseMatcher<String>() {
//                @Override
//                public boolean matches(Object item) {
//                    return item instanceof String && ((String) item).startsWith("foo");
//                }
//
//                @Override
//                public void describeTo(Description description) {
//                    //NOOP
//                }
//            }));
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        new FullVerifications(loginService) {};
    }

    /**
     * Partial Mocking
     */
    @Test
    public void partialMocking() {
        LoginService partialLoginService = new LoginService();
        partialLoginService.setLoginDao(loginDao);
        loginController.loginService = partialLoginService;

        UserForm userForm = new UserForm();
        userForm.username = "foo";

        new Expectations(partialLoginService) {{
            // let's mock DAO call
            loginDao.login(userForm); result = 1;

            // no expectation for login method so that real implementation is used

            // mock setCurrentUser call
            partialLoginService.setCurrentUser("foo");
        }};

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        // verify mocked call
        new Verifications() {{
            partialLoginService.setCurrentUser("foo");
        }};
    }
}
