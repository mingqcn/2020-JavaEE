package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author XQChen
 * @version 创建时间：2020/12/7 下午1:35
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
public class Auth003Test {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;
    private String expectedOutputAuth003;

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

        try {
            this.expectedOutputAuth003 = new String(Files.readAllBytes(Paths.get("src/test/resources/auth003.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 测试用桩模块--登录
     * @param userName
     * @param password
     * @return
     * @throws Exception
     */
    private String login(String userName, String password) throws Exception{
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
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

    /***
     * 正确查找用户
     * @throws Exception
     */
    @Test
    public void findAllUsers1() throws Exception {

        String token = this.login("13088admin", "123456");

        String response = new String(Objects.requireNonNull(manageClient
                .get()
                .uri("/shops/0/adminusers/all?userName=&mobile=&page=2&pagesize=3")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent()));

        String expectOutput = JacksonUtil.parseSubnodeToString(expectedOutputAuth003, "/findAllUsers1");

        System.out.println(response);

        JSONAssert.assertEquals(expectOutput, response, false);
    }

    /***
     * 查找用户参数错误
     * @throws Exception
     */
    @Test
    public void findAllUsers2() throws Exception {

        String token = this.login("13088admin", "123456");

        String response = new String(Objects.requireNonNull(manageClient
                .get()
                .uri("/shops/0/adminusers/all?userName=&mobile=&page=1&pagesize=-2")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent()));

        String expectOutput = JacksonUtil.parseSubnodeToString(expectedOutputAuth003, "/findAllUsers2");

        System.out.println(response);

        JSONAssert.assertEquals(expectOutput, response, false);
    }

    /***
     * 查找用户自身信息
     * @throws Exception
     */
    @Test
    public void findUserSelf1() throws Exception {

        String token = this.login("13088admin", "123456");

        String response = new String(Objects.requireNonNull(manageClient
                .get()
                .uri("/adminusers")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent()));

        String expectOutput = JacksonUtil.parseSubnodeToString(expectedOutputAuth003, "/findUserSelf1");

        System.out.println(response);

        JSONAssert.assertEquals(expectOutput, response, new CustomComparator(JSONCompareMode.LENIENT,
                new Customization("data.lastLoginTime", (o1, o2) -> true),
                new Customization("data.lastLoginIp", (o1, o2) -> true)));
    }

    /***
     * 查找用户
     * @throws Exception
     */
    @Test
    public void findUserById1() throws Exception {

        String token = this.login("13088admin", "123456");

        String response = new String(Objects.requireNonNull(manageClient
                .get()
                .uri("/shops/0/adminusers/46")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent()));

        String expectOutput = JacksonUtil.parseSubnodeToString(expectedOutputAuth003, "/findUserById1");

        System.out.println(response);

        JSONAssert.assertEquals(expectOutput, response, false);
    }

    /***
     * 查找不存在的用户
     * @throws Exception
     */
    @Test
    public void findUserById2() throws Exception {

        String token = this.login("13088admin", "123456");

        String response = new String(Objects.requireNonNull(manageClient
                .get()
                .uri("/shops/0/adminusers/23")
                .header("authorization", token)
                .exchange()
                .expectHeader()
                .contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult()
                .getResponseBodyContent()));

        String expectOutput = JacksonUtil.parseSubnodeToString(expectedOutputAuth003, "/findUserById2");

        System.out.println(response);

        JSONAssert.assertEquals(expectOutput, response, false);
    }
}
