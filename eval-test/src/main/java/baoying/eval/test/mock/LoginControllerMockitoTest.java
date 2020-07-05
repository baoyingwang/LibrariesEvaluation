package baoying.eval.test.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

/**
 * 1. 基本代码来自 https://www.baeldung.com/mockito-vs-easymock-vs-jmockit
 * 2. 这个链接讲解的非常好 http://liangfei.me/2017/07/06/mockito-details-1-usage/
 *
 * 两类mock：class mock和partial mock。
 * - class mock - 所有方法都被替换，如果没有提供替换行为（所谓的没有进行stubing/插桩）则返回默认值（null、false等）
 * - partial mock - 只替换stubbing的方法，其余方法正常调用
 * - note：这个概念非常非常关键/基础，先了解再看其他的才比较顺利
 * - note：这个链接的讲解和例子都很直观 http://liangfei.me/2017/07/06/mockito-details-1-usage/
 *
 * Spring与MockitTo合用
 * - 增加Resource("beanName")来指定对哪个bean进行mock
 * https://blog.csdn.net/dc_726/article/details/8568537
 */
public class LoginControllerMockitoTest {

    @Mock
    private LoginDao loginDao;


    /**
     * 声明一个partial lock的方法有两种
     * 1 - 使用这里的annotation @Spy
     * 2 - 或者使用spy
     *
     * 同时这个又是一个@InjectMocks，则其中的field都会被被mock替换，如loginDao
     */
    @Spy
    @InjectMocks
    private LoginService spiedLoginService;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @Before
    public void setUp() {

        /**
         * @InjectMocks on loginController的注解是，则表明
         * - 其自身不是mock
         * - 其fields都会被当前类中的mock对象替换
         *   - mock的赖于是前类, e.g. LoginControllerMockitoTest的中field定义的mock对象(譬如这里声明的loginService)
         * - note：猜想@InjectMocks的目标是为了方便的将所有的mock都赋值到当前field上面，解脱大家的工序哦
         *   - 但是如果定义不清楚，会导致更多的混乱
         *   - 这个概念弄晕了很多人，看来其定义不是那么理想
         * - https://stackoverflow.com/questions/16467685/difference-between-mock-and-injectmocks
         * - aldok 在这个链接中给了一个比较清楚的解释
         *
         * TODO - 这个loginController的loginService到底是上面的spy哪个，还是普通的哪个？
         * 
         */
        loginController = new LoginController();

        //为了@InjectMocks工作，必须用下面的初始化，或者@RunWith(MockitoJUnitRunner.class)
        // - https://stackoverflow.com/questions/16467685/difference-between-mock-and-injectmocks
        MockitoAnnotations.initMocks(this);
    }


    /**
     * Verifying No Calls to Mock
     */
    @Test
    public void assertThatNoMethodHasBeenCalled() {
        loginController.login(null);
        Mockito.verifyZeroInteractions(loginService);
    }

    /**
     * Defining Mocked Method Calls and Verifying Calls to Mocks
     */
    @Test
    public void assertTwoMethodsHaveBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        Mockito.when(loginService.login(userForm)).thenReturn(true);

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        Mockito.verify(loginService).login(userForm);
        Mockito.verify(loginService).setCurrentUser("foo");
    }

    /**
     * Defining Mocked Method Calls and Verifying Calls to Mocks
     */
    @Test
    public void assertOnlyOneMethodHasBeenCalled() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        Mockito.when(loginService.login(userForm)).thenReturn(false);

        String login = loginController.login(userForm);

        Assert.assertEquals("KO", login);
        Mockito.verify(loginService).login(userForm);
        Mockito.verifyNoMoreInteractions(loginService);
    }

    /**
     * Mocking Exception Throwing
     */
    @Test
    public void mockExceptionThrowin() {
        UserForm userForm = new UserForm();
        Mockito.when(loginService.login(userForm)).thenThrow(IllegalArgumentException.class);

        String login = loginController.login(userForm);

        Assert.assertEquals("ERROR", login);
        Mockito.verify(loginService).login(userForm);
        Mockito.verifyZeroInteractions(loginService);
    }

    /**
     * Mocking an Object to Pass Around
     */
    @Test
    public void mockAnObjectToPassAround() {
        UserForm userForm = Mockito.when(Mockito.mock(UserForm.class).getUsername())
                .thenReturn("foo").getMock();
        Mockito.when(loginService.login(userForm)).thenReturn(true);

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        Mockito.verify(loginService).login(userForm);
        Mockito.verify(loginService).setCurrentUser("foo");
    }

    /**
     * Custom Argument Matching
     */
    @Test
    public void argumentMatching() {
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        // default matcher
        Mockito.when(loginService.login(Mockito.any(UserForm.class))).thenReturn(true);

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        Mockito.verify(loginService).login(userForm);
        // complex matcher
        Mockito.verify(loginService).setCurrentUser(ArgumentMatchers.argThat(
                new ArgumentMatcher<String>() {
                    @Override
                    public boolean matches(String argument) {
                        return argument.startsWith("foo");
                    }
                }
        ));
    }

    /**
     * Partial Mocking
     */
    @Test
    public void partialMocking() {
        // use partial mock
        loginController.loginService = spiedLoginService;
        UserForm userForm = new UserForm();
        userForm.username = "foo";
        // let service's login use implementation so let's mock DAO call
        Mockito.when(loginDao.login(userForm)).thenReturn(1);

        String login = loginController.login(userForm);

        Assert.assertEquals("OK", login);
        // verify mocked call
        Mockito.verify(spiedLoginService).setCurrentUser("foo");
    }
}
