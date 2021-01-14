package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 权限模块公开测试
 * 测试需添加相应的数据库数据
 * @author 张湘君 24320182203327
 * @date 2020/12/13 20:15
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ZhangXiangJunPrivTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp(){

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }


    /**
     * 1
     * 修改权限，修改成功
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(1)
    public void changePrivTest1() throws Exception{
        String token =this.login("13088admin", "123456");
        String privJson = "{\"name\":\"测试改变\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"0\"}";
        byte[] responseString = manageClient.put().uri("/privileges/1000").header("authorization",token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 2
     * 修改权限，name为空
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(2)
    public void changePrivTest2() throws Exception{
        String token =this.login("13088admin", "123456");
        String privJson = "{\"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"0\"}";
        byte[] responseString = manageClient.put().uri("/privileges/1000").header("authorization",token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":503,\"errmsg\":\"name不得为空;\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 3
     * 修改权限，url为空
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(3)
    public void changePrivTest3() throws Exception{
        String token =this.login("13088admin", "123456");
        String privJson = "{\"name\":\"测试修改\", \"requestType\": \"0\"}";
        byte[] responseString = manageClient.put().uri("/privileges/1000").header("authorization",token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":503,\"errmsg\":\"url不得为空;\"}", new String(responseString, "UTF-8"), true);
    }



    /**
     * 4
     * 修改权限，requestType为空
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(4)
    public void changePrivTest4() throws Exception{
        String token =this.login("13088admin", "123456");
        String privJson = "{\"name\":\"测试修改\", \"url\": \"/adminusers/{id}/testChange\"}";
        byte[] responseString = manageClient.put().uri("/privileges/1000").header("authorization",token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":503,\"errmsg\":\"requestType不得为空;\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 5
     * 修改权限，requestType数值错误
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(5)
    public void changePrivTest5() throws Exception{
        String token =this.login("13088admin", "123456");
        String privJson = "{\"name\":\"测试修改\", \"url\": \"/adminusers/{id}/testChange\", \"requestType\": \"7\"}";
        byte[] responseString = manageClient.put().uri("/privileges/1000").header("authorization",token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":503,\"errmsg\":\"错误的requestType数值;\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 6
     * 修改权限，输入了重复的url和requestType
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(6)
    public void changePrivTest6() throws Exception{
        String token =this.login("13088admin", "123456");
        String privJson = "{\"name\":\"测试修改\", \"url\": \"/shops/{id}/skus/{id}/onshelves\", \"requestType\": 2}";
        byte[] responseString = manageClient.put().uri("/privileges/1000").header("authorization",token)
                .bodyValue(privJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":742}", new String(responseString, "UTF-8"), false);
    }

    /**
     * 7
     * 获得角色所有权限，成功获取
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(7)
    public void getRolePrivsTest1() throws Exception{
        String token =this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/roles/85/privileges").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":[{\"id\":13,\"role\":{\"id\":85,\"name\":\"总经办\"},\"privilege\":{\"id\":13,\"name\":\"取消角色权限\"},\"creator\":{\"id\":1,\"username\":\"13088admin\"},\"gmtModified\":\"2020-11-01T10:11:21\"},{\"id\":13,\"role\":{\"id\":85,\"name\":\"总经办\"},\"privilege\":{\"id\":13,\"name\":\"取消角色权限\"},\"creator\":{\"id\":1,\"username\":\"13088admin\"},\"gmtModified\":\"2020-11-01T10:11:21\"},{\"id\":13,\"role\":{\"id\":85,\"name\":\"总经办\"},\"privilege\":{\"id\":13,\"name\":\"取消角色权限\"},\"creator\":{\"id\":1,\"username\":\"13088admin\"},\"gmtModified\":\"2020-11-01T10:11:21\"},{\"id\":13,\"role\":{\"id\":85,\"name\":\"总经办\"},\"privilege\":{\"id\":13,\"name\":\"取消角色权限\"},\"creator\":{\"id\":1,\"username\":\"13088admin\"},\"gmtModified\":\"2020-11-01T10:11:21\"},{\"id\":13,\"role\":{\"id\":85,\"name\":\"总经办\"},\"privilege\":{\"id\":13,\"name\":\"取消角色权限\"},\"creator\":{\"id\":1,\"username\":\"13088admin\"},\"gmtModified\":\"2020-11-01T10:11:21\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 8
     * 获得角色所有权限，id不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(8)
    public void getRolePrivsTest2() throws Exception{
        String token =this.login("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/0/roles/2000/privileges").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 9
     * 增加角色的权限，增加成功
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(9)
    public void addRolePrivTest1() throws Exception{
        String token =this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/roles/{roleid}/privileges/{privilegeid}",777,2).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        //查询刚刚插入的信息
        byte[] responseString1 = manageClient.get().uri("/shops/0/roles/777/privileges").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":[{\"id\":2,\"role\":{\"id\":777,\"name\":\"testRole\"},\"privilege\":{\"id\":2,\"name\":\"查看任意用户信息\"},\"creator\":{\"id\":1,\"username\":\"13088admin\"},\"gmtModified\":\"2020-11-01T10:11:21\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString1, "UTF-8"), false);
    }

    /**
     * 10
     * 增加角色的权限，roleId不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(10)
    public void addRolePrivTest2() throws Exception{
        String token =this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/roles/{roleid}/privileges/{privilegeid}",2000,5).header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), false);

    }

    /**
     * 11
     * 增加角色的权限，privilegeId不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(11)
    public void addRolePrivTest3() throws Exception{
        String token =this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/0/roles/{roleid}/privileges/{privilegeid}",777,2000).header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), false);

    }

    /**
     * 12
     * 增加角色的权限，角色已拥有该权限
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(12)
    public void addRolePrivTest4() throws Exception{
        String token =this.login("13088admin", "123456");
        byte[] responseString1 = manageClient.post().uri("/shops/0/roles/{roleid}/privileges/{privilegeid}",777,3).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        byte[] responseString = manageClient.post().uri("/shops/0/roles/{roleid}/privileges/{privilegeid}",777,3).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":503,\"errmsg\":\"角色权限已存在\"}", new String(responseString, "UTF-8"), false);

    }


    /**
     * 14
     * 更新用户数据，email重复
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(14)
    public void modifyUser2() throws Exception{
        String token =this.login("13088admin", "123456");
        String regJson = "{\"name\": \"testU1\",\"email\": \"test@test.cn\", \"mobile\": \"11111111100\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/58").header("authorization",token)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", new String(responseString, "UTF-8"), true);
        String regJson1 = "{\"name\": \"testU2\",\"email\": \"test@test.cn\", \"mobile\": \"11111111110\"}";
        //测试是否重复
        byte[] responseString1 = manageClient.put().uri("/shops/0/adminusers/57").header("authorization",token)
                .bodyValue(regJson1)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":732}" +
                "\n", new String(responseString1, "UTF-8"), false);
    }

    /**
     * 15
     * 更新用户数据，电话重复
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(15)
    public void modifyUser3() throws Exception{
        String token =this.login("13088admin", "123456");
        String regJson = "{\"name\": \"testU1\",\"email\": \"test@test.edu.xmu.cn\", \"mobile\": \"11111111100\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/58").header("authorization",token)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", new String(responseString, "UTF-8"), false);
        String regJson1 = "{\"name\": \"testU2\",\"email\": \"test@test.cn\", \"mobile\": \"11111111100\"}";
        //测试是否重复
        byte[] responseString1 = manageClient.put().uri("/shops/0/adminusers/57").header("authorization",token)
                .bodyValue(regJson1)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":733}", new String(responseString1, "UTF-8"), false);
    }

    /**
     * 16
     * 更新用户数据，电话格式不对
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(16)
    public void modifyUser4() throws Exception{
        String token =this.login("13088admin", "123456");
        String regJson = "{\"name\": \"testU\",\"email\": \"test@test.cn\", \"mobile\": \"777a\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/59").header("authorization",token)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":503,\"errmsg\":\"手机号码格式不正确;\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 17
     * 更新用户数据，email格式不对
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(17)
    public void modifyUser5() throws Exception{
        String token =this.login("13088admin", "123456");
        String regJson = "{\"name\": \"testU\",\"email\": \"阿巴阿巴\", \"mobile\": \"11111111111\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/59").header("authorization",token)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":503}", new String(responseString, "UTF-8"), false);
    }

    /**
     * 18
     * 更新用户数据，不存在此用户
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(18)
    public void modifyUser6() throws Exception{
        String token =this.login("13088admin", "123456");
        String regJson = "{\"name\": \"testU\",\"email\": \"test@test.cn\", \"mobile\": \"11111111111\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/777").header("authorization",token)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 19
     * 逻辑删除用户
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(19)
    public void delUser1() throws Exception{
        String token =this.login("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/53").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":0}", new String(responseString, "UTF-8"), false);

        //检验是否被删除了
        byte[] responseString1 = manageClient.delete().uri("/shops/0/adminusers/53").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504}", new String(responseString1, "UTF-8"), false);

    }

    /**
     * 20
     * 逻辑删除用户，用户不存在
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(20)
    public void delUser2() throws Exception{
        String token =this.login("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/adminusers/2000").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504}", new String(responseString, "UTF-8"), false);

    }

    /**
     * 21
     * 测试更新被删除的用户
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(21)
    public void modifyUser7() throws Exception{
        String token =this.login("13088admin", "123456");

        byte[] responseString1 = manageClient.delete().uri("/shops/0/adminusers/52").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":0}", new String(responseString1, "UTF-8"), false);


        String regJson = "{\"name\": \"testU\",\"email\": \"test@test.cn\", \"mobile\": \"11111111111\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/52").header("authorization",token)
                .bodyValue(regJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), false);
    }


    /**
     * 22
     * 测试封禁用户
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(22)
    public void forbidUser1() throws Exception{
        String token =this.login("13088admin", "123456");

        //封禁id为54号的用户
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/54/forbid").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", new String(responseString, "UTF-8"), true);

        //通过登录来验证是否成功
        loginForbiddenUser("9259200008","123456");
    }

    /**
     * 23
     * 测试封禁用户,用户不存在（id不存在）
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(23)
    public void forbidUser2() throws Exception{
        String token =this.login("13088admin", "123456");

        //封禁id为54号的用户
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/2000/forbid").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 24
     * 测试封禁用户,用户不存在（用户已被逻辑删除）
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(24)
    public void forbidUser3() throws Exception{
        String token =this.login("13088admin", "123456");

        //封禁id为53号的用户
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/53/forbid").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 25
     * 测试解禁用户
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(25)
    public void releaseUser1() throws Exception{
        String token =this.login("13088admin", "123456");

        //解禁id为54号的用户
        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/54/release").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":0,\"errmsg\":\"成功\"}", new String(responseString, "UTF-8"), true);

        //通过登录来验证是否成功
        login("9259200008","123456");
    }

    /**
     * 26
     * 测试解禁用户，用户不存在（id不存在）
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(26)
    public void releaseUser2() throws Exception{
        String token =this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/2000/release").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), true);
    }

    /**
     * 27
     * 测试解禁用户，用户不存在（用户已被逻辑删除）
     * @author 张湘君 24320182203327
     * @throws Exception
     */
    @Test
    @Order(27)
    public void releaseUser3() throws Exception{
        String token =this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/0/adminusers/53/release").header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        JSONAssert.assertEquals("{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}", new String(responseString, "UTF-8"), true);
    }

    private String login(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    //登录封禁用户
    private void loginForbiddenUser(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_USER_FORBIDDEN.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.AUTH_USER_FORBIDDEN.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

}
