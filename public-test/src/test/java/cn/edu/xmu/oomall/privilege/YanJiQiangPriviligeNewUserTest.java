package cn.edu.xmu.oomall.privilege;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;


/**
 * 2 * @author: 颜吉强24320172203229
 * 3 * @date: 2020/12/15 下午3:13
 * 4
 */
@SpringBootTest(classes = PublicTestApp.class)
public class YanJiQiangPriviligeNewUserTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp() {

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://" + managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://" + mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }
    //获取token
    public String LoginSuccessPlatform(String name, String pass) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", name);
        body.put("password", pass);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String token = JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
        return token;
    }


    @Test
    public void getAllState() throws Exception {
        String token= LoginSuccessPlatform("8532600003","123456");
        String responseString=new String(manageClient.get().uri("/adminusers/states")
                .header("authorization",token)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 0, \"data\": [ { \"name\": \"新注册\", \"code\": 0 }, { \"name\": \"正常\", \"code\": 1 }, { \"name\": \"封禁\", \"code\": 2 }, { \"name\": \"废弃\", \"code\": 3 } ], \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 正常注册
     * @throws Exception
     */
    @Test
    public void register1() throws Exception {
        String requireJson="{\n    \"userName\": \"anormalusername3\",\n    \"password\": \"1234aBa!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"13888888388\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test1\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
        .bodyValue(requireJson)
        .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 0, \"data\": {  \"userName\": \"anormalusername3\", \"mobile\": \"8733C04F80C594827F776CD726B85472\", \"email\": \"5643361C11D3299408C7EA82206AFCC7\", \"name\": \"4A3BE008F8DE844B7EE7042E1B7B8842\", \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\", \"openId\": \"test1\", \"departId\": 1 }, \"errmsg\": \"成功\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 手机号不正确
     * @throws Exception
     */
    @Test
    public void register2() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"ab6411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503, \"errmsg\": \"手机号格式不正确;\" }";
        JSONAssert.assertEquals(expectedResponse,responseString,true);
    }

    /**
     * 空电话号码
     * @throws Exception
     */
    @Test
    public void register3() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": null,\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     *空用户名
     * @throws Exception
     */
    @Test
    public void register4() throws Exception {
        String requireJson="{\n    \"userName\": null,\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 用户名长度过短
     * @throws Exception
     */
    @Test
    public void register5() throws Exception {
        String requireJson="{\n    \"userName\": \"13087\",\n    \"password\": \"AaBD123!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 密码格式错误
     * @throws Exception
     */
    @Test
    public void register6() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD123123\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 名称为空
     * @throws Exception
     */
    @Test
    public void register7() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": null,\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * email格式不正确
     * @throws Exception
     */
    @Test
    public void register8() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686886\",\n    \"email\": \"233\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503 }";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * email为空
     * @throws Exception
     */
    @Test
    public void register9() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686886\",\n    \"email\": null,\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 部门id为负数
     * @throws Exception
     */
    @Test
    public void register10() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": -2\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503 }";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 部门id为空
     * @throws Exception
     */
    @Test
    public void register11() throws Exception {
        String requireJson="{\n    \"userName\": \"13087admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686886\",\n    \"email\": \"test@test.com\",\n    \"openId\": \"test\",\n    \"departId\": null\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{ \"errno\": 503 }";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 重复的用户名
     * @throws Exception
     */
    @Test
    public void duplicateRegister1() throws Exception {
        String requireJson="{\n    \"userName\": \"duplicateTest\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686836\",\n    \"email\": \"test2@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\":731}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 重复的电话
     * @throws Exception
     */
    @Test
    public void duplicateRegister2() throws Exception {
        String requireJson="{\n    \"userName\": \"duplicateTest2\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6411686836\",\n    \"email\": \"test3@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\":733}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 重复的email
     * @throws Exception
     *
     */
    @Test
    public void duplicateRegister3() throws Exception {
        String requireJson="{\n    \"userName\": \"duplicateTest2\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"6413686846\",\n    \"email\": \"test2@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\":732}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

    /**
     * 与用户表用户名重复
     * @throws Exception
     */
    @Test
    public void duplicateRegister4() throws Exception {
        String requireJson="{\n    \"userName\": \"13088admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"641168683243243236\",\n    \"email\": \"test2jcs@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\":731}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
    /**
     * 与用户表电话重复
     * @throws Exception
     */
    @Test
    public void duplicateRegister5() throws Exception {
        String requireJson="{\n    \"userName\": \"13089admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"16978955874\",\n    \"email\": \"test0112@test.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\":733}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }
    /**
     * 与用户表email重复
     * @throws Exception
     */
    @Test
    public void duplicateRegister6() throws Exception {
        String requireJson="{\n    \"userName\": \"13089admin\",\n    \"password\": \"AaBD1231!!\",\n    \"name\": \"LiangJi\",\n    \"avatar\": \"https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png\",\n    \"mobile\": \"16978933874\",\n    \"email\": \"minge@163.com\",\n    \"openId\": \"test\",\n    \"departId\": 1\n}";
        String responseString=new String(manageClient.post().uri("/adminusers")
                .bodyValue(requireJson)
                .exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody().returnResult().getResponseBodyContent(),StandardCharsets.UTF_8);
        String expectedResponse="{\"errno\":732}";
        JSONAssert.assertEquals(expectedResponse,responseString,false);
    }

}
