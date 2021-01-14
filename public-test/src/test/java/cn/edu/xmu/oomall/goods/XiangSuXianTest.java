package cn.edu.xmu.oomall.goods;

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

import java.nio.charset.StandardCharsets;
// 14个用例，应当全部通过，由于本组原因，有一个用例未通过，集成后再试
// ERROR: FIRST GROUP ERROR
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = PublicTestApp.class)
public class XiangSuXianTest {

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
        System.out.println(mallGate);

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

    /**
     * 不用登陆查询预售活动状态
     * @author 向素娴
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getPresaleStates() throws Exception {
        byte[] responseString=mallClient.get().uri("/presales/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\": 0, \"data\": [{ \"name\": \"已下线\", \"code\": 0 },{ \"name\": \"已上线\", \"code\": 1 },{ \"name\": \"已删除\", \"code\": 2 }],\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 查询团购活动状态
     * @author 向素娴
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getGrouponStates() throws Exception {
        byte[] responseString=mallClient.get().uri("/groupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\": 0, \"data\": [{ \"name\": \"已下线\", \"code\": 0 },{ \"name\": \"已上线\", \"code\": 1 },{ \"name\": \"已删除\", \"code\": 2 }],\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /**
     * 查看所有预售活动（传了skuid和shopId）
     * @author 向素娴
     * @throws Exception
     */
    @Test
    @Order(3)
    public void getPresales() throws Exception {
        byte[]responseString=mallClient.get().uri("/presales?skuId=273")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"name\": \"七夕节\",\n" +
                "        \"payTime\": null,\n" +
                "        \"endTime\": \"2020-06-02T11:57:39\",\n" +
                "        \"advancePayPrice\": 0,\n" +
                "        \"restPayPrice\": 0,\n" +
                "        \"beginTime\": \"2020-06-01T11:57:39\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    //查询评论状态
    @Test
    @Order(5)
    public void getCommentStates() throws Exception {
        byte[] responseString=mallClient.get().uri("/comments/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"未审核\",\n" +
                "      \"code\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"评论成功\",\n" +
                "      \"code\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"未通过\",\n" +
                "      \"code\": 2\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    //查看sku的评价列表（已通过审核）
    // 测不过，集成上再测试
    @Test
    @Order(7)
    public void getComments() throws Exception {
        byte[] responseString = mallClient.get().uri("/skus/273/comments")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.goodsSkuId == 273)]").exists()
                .returnResult().getResponseBodyContent();
    }

    //查询店铺状态
    @Test
    @Order(8)
    public void getShopStates() throws Exception {
        byte[] responseString=mallClient.get().uri("/shops/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        System.out.println(new String(responseString, "UTF-8"));
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"未审核\",\n" +
                "      \"code\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"未上线\",\n" +
                "      \"code\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"上线\",\n" +
                "      \"code\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"关闭\",\n" +
                "      \"code\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"审核未通过\",\n" +
                "      \"code\": 4\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    //删除预售(预售id和预售id对不上)
    @Test
    @Order(9)
    public void deletePresales1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/1/presales/4")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 505\n" +
//                "  \"errmsg\": \"操作的资源id不存在\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    //删除预售（预售活动状态禁止）
    @Test
    @Order(10)
    public void deletePresales2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/2/presales/4")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 906\n" +
                //               "  \"errmsg\": \"预售活动状态禁止\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    //删除预售（删除成功）
    @Test
    @Order(11)
    public void deletePresales3() throws Exception {
        String token = this.login("13088admin", "123456");
        manageClient.delete().uri("/shops/1/presales/5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();
    }
    //    //删除团购（团购id跟shopId对不上）
    @Test
    @Order(12)
    public void deleteGroupon1() throws Exception {
        String token = this.login("13088admin", "123456");
        manageClient.delete().uri("/shops/2/groupons/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(504)
                .returnResult()
                .getResponseBodyContent();
    }
    //删除团购（团购状态禁止）
    @Test
    @Order(13)
    public void deleteGroupon2() throws Exception {
        String token = this.login("13088admin", "123456");
        manageClient.delete().uri("/goods/shops/1/groupons/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(907)
                .returnResult()
                .getResponseBodyContent();
    }

    //删除团购（删除成功）
    @Test
    @Order(15)
    public void deleteGroupon3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/1/groupons/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 0\n" +
                //             "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }    //管理员审核评论(审核失败)

    //    //修改预售活动
//    @Test
//    @Order(16)
//    public void modifyPresale() throws Exception {
//        String token = this.login("13088admin", "123456");
//        String contentJson = "{\n" +
//                "  \"advancePayPrice\": 0,\n" +
//                "  \"beginTime\": \"2020-12-17 00:00:00\",\n" +
//                "  \"endTime\": \"2020-12-31 00:00:00\",\n" +
//                "  \"name\": \"string\",\n" +
//                "  \"payTime\": \"2021-01-01 00:00:00\",\n" +
//                "  \"quantity\":20,\n" +
//                "  \"restPayPrice\": 0\n" +
//                "}";
//
//        byte[] responseString = manageClient.put().uri("/shops/2/presales/4")
//                .header("authorization", token)
//                .bodyValue(contentJson)
//                .exchange()
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse = "{\n" +
//                "  \"errno\": 906,\n" +
//                "  \"errmsg\": \"预售活动状态禁止\"\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
//    }
//    @Order(17)
//    @Test
//    //修改预售活动（成功）
//    public void modifyPresale1() throws Exception {
//        String token = this.login("13088admin", "123456");
//        String contentJson = "{\n" +
//                "  \"advancePayPrice\": 0,\n" +
//                "  \"beginTime\": \"2020-12-17 00:00:00\",\n" +
//                "  \"endTime\": \"2020-12-31 00:00:00\",\n" +
//                "  \"name\": \"string\",\n" +
//                "  \"payTime\": \"2021-01-01 00:00:00\",\n" +
//                "  \"quantity\":20,\n" +
//                "  \"restPayPrice\": 0\n" +
//                "}";
//
//        byte[] responseString = manageClient.put().uri("/shops/1/presales/3100")
//                .header("authorization", token)
//                .bodyValue(contentJson)
//                .exchange()
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse =  "{\n" +
//                "  \"errno\": 0,\n" +
//                "  \"errmsg\": \"成功\"\n" +
//                "}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
//    }
//修改预售活动
    @Test
    @Order(18)
    public void modifyPresale() throws Exception {
        String token = this.login("13088admin", "123456");
        String contentJson = "{\n" +
                "  \"advancePayPrice\": 0,\n" +
                "  \"beginTime\": \"2020-12-31T00:00:00\",\n" +
                "  \"endTime\": \"2022-12-31T00:00:00\",\n" +
                "  \"name\": \"string\",\n" +
                "  \"payTime\": \"2021-01-01T00:00:00\",\n" +
                "  \"quantity\":20,\n" +
                "  \"restPayPrice\": 0\n" +
                "}";

        byte[] responseString = manageClient.put().uri("/shops/2/presales/4")
                .header("authorization", token)
                .bodyValue(contentJson)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        var x = new String(responseString, StandardCharsets.UTF_8);
        String expectedResponse = "{\n" +
                "  \"errno\": 906\n" +
                //   "  \"errmsg\": \"预售活动状态禁止\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
//    测试数据冲突
//    @Order(19)
//    @Test
//    //修改预售活动（成功）
//    public void modifyPresale1() throws Exception {
//        String token = this.login("13088admin", "123456");
//        String contentJson = "{\n" +
//                "  \"advancePayPrice\": 0,\n" +
//                "  \"beginTime\": \"2020-12-31T00:00:00\",\n" +
//                "  \"endTime\": \"2022-12-31T00:00:00\",\n" +
//                "  \"name\": \"string\",\n" +
//                "  \"payTime\": \"2021-01-01T00:00:00\",\n" +
//                "  \"quantity\":20,\n" +
//                "  \"restPayPrice\": 0\n" +
//                "}";
//
//        byte[] responseString = manageClient.put().uri("/shops/1/presales/3101")
//                .header("authorization", token)
//                .bodyValue(contentJson)
//                .exchange()
//                .expectBody()
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse =  "{\n" +
//                "  \"errno\": 0\n" +
//                //     "  \"errmsg\": \"成功\"\n" +
//                "}";
//        String x = new String(responseString, StandardCharsets.UTF_8);
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
//    }


}
