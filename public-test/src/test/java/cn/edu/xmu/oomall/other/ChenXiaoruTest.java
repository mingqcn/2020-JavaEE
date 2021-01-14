package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.JacksonUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 测试
 * @author cxr
 * @date 2020/12/11 7:54
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@Slf4j
public class ChenXiaoruTest {

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
     * 获得广告的所有状态
     * 管理员
     * @author 24320182203175 陈晓如
     */
    @Order(0)
    @Test
    public void advertiseTest1() throws Exception {
        byte[] responseString = manageClient.get().uri("/advertisement/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"code\": 0,\n" +
                "      \"name\": \"待审核\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 4,\n" +
                "      \"name\": \"上架\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 6,\n" +
                "      \"name\": \"下架\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获得广告的所有状态
     * 用户
     * @throws Exception
     */
    @Order(1)
    @Test
    public void advertiseTest2() throws Exception {
        byte[] responseString = mallClient.get().uri("/advertisement/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"code\": 0,\n" +
                "      \"name\": \"待审核\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 4,\n" +
                "      \"name\": \"上架\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": 6,\n" +
                "      \"name\": \"下架\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 未登录设置默认广告
     *
     * @author 24320182203175 陈晓如
     */
    @Order(2)
    @Test
    public void advertiseTest3() throws Exception {
        byte[] ret = manageClient.put().uri("/shops/0/advertisement/121/default")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 使用伪造token设置默认广告
     *
     * @author 24320182203175 陈晓如
     */
    @Order(3)
    @Test
    public void advertiseTest4() throws Exception {
        byte[] ret = manageClient.put().uri("/shops/0/advertisement/121/default")
                .header("authorization", "test")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员设置默认广告
     * 操作的广告不存在
     * @throws Exception
     */
    @Order(4)
    @Test
    public void advertiseTest5() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/1/default")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 管理员设置默认广告
     * 下架态的广告从非默认变为默认
     * @throws Exception
     */
    @Order(6)
    @Test
    public void advertiseTest7() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/422/default").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='422')].beDefault").isEqualTo(true)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员设置默认广告
     * 审核态的广告从非默认变为默认
     * @throws Exception
     */
    @Order(7)
    @Test
    public void advertiseTest8() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/423/default").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='423')].beDefault").isEqualTo(true)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 操作的广告不存在
     * @throws Exception
     */
    @Order(8)
    @Test
    public void advertiseTest9() throws Exception{
        String token = this.adminLogin("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("content", "广告内容1");
        body.put("beginDate", "2020-12-01");
        body.put("endDate", "2020-12-02");
        body.put("weight", 5);
        body.put("repeat", true);
        body.put("link", "link1");
        String requireJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}",0,1)
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入一个空的body
     * @throws Exception
     */
    @Order(9)
    @Test
    public void advertiseTest10() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();

        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 421)
                .header("authorization", token)
                .bodyValue(body.toString())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入开始日期和结束日期格式错误
     * @throws Exception
     */
    @Order(10)
    @Test
    public void advertiseTest11() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content", "广告内容1");
        body.put("beginDate","2020/01/12");
        body.put("endDate","2020/03/06");
        body.put("weight", 5);
        body.put("repeat", true);
        body.put("link", "link1");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 421)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入开始日期值不合理
     * @throws Exception
     */
    @Order(11)
    @Test
    public void advertiseTest12() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content", "广告内容1");
        body.put("beginDate","2020-02-30");
        body.put("endDate","2020-03-06");
        body.put("weight", 5);
        body.put("repeat", true);
        body.put("link", "link1");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 421)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入开始日期大于结束日期
     * @throws Exception
     */
    @Order(12)
    @Test
    public void advertiseTest13() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content", "广告内容1");
        body.put("beginDate","2020-12-12");
        body.put("endDate","2020-03-06");
        body.put("weight", 5);
        body.put("repeat", true);
        body.put("link", "link1");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 421)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入的内容不存在content
     * @throws Exception
     */
    @Order(13)
    @Test
    public void advertiseTest14() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("beginDate","2020-02-12");
        body.put("endDate","2020-03-06");
        body.put("weight", 5);
        body.put("repeat", true);
        body.put("link", "link1");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 421)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入的内容不含weight
     * @throws Exception
     */
    @Order(14)
    @Test
    public void advertiseTest15() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content", "广告内容1");
        body.put("beginDate","2020-12-12");
        body.put("endDate","2020-03-06");
        body.put("repeat", true);
        body.put("link", "link1");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 421)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 传入的内容不含repeat
     * @throws Exception
     */
    @Order(15)
    @Test
    public void advertiseTest16() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content", "广告内容1");
        body.put("beginDate","2020-12-12");
        body.put("endDate","2020-03-06");
        body.put("weight", 5);
        body.put("link", "link1");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 421)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员修改广告内容
     * 上架态广告修改成功
     * @throws Exception
     */
    @Order(16)
    @Test
    public void advertiseTest17() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content", "广告内容1");
        body.put("beginDate", "2020-12-01");
        body.put("endDate", "2020-12-02");
        body.put("weight", 5);
        body.put("repeat", true);
        body.put("link", "link1");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}", 0, 421)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='421')].content").isEqualTo("广告内容1")
                .jsonPath("$.data.list[?(@.id=='421')].beginDate").isEqualTo("2020-12-01")
                .jsonPath("$.data.list[?(@.id=='421')].endDate").isEqualTo("2020-12-02")
                .jsonPath("$.data.list[?(@.id=='421')].weight").isEqualTo("5")
                .jsonPath("$.data.list[?(@.id=='421')].repeat").isEqualTo(true)
                .jsonPath("$.data.list[?(@.id=='421')].link").isEqualTo("link1")
                .jsonPath("$.data.list[?(@.id=='421')].state").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除某一个广告
     * 操作的资源不存在
     * @throws Exception
     */
    @Order(17)
    @Test
    public void advertiseTest18() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{did}/advertisement/{id}", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员删除某一个广告
     * 上架态广告删除成功
     * @throws Exception
     */
    @Order(18)
    @Test
    public void advertiseTest19() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{did}/advertisement/{id}", 0, 447)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                // .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
    }

    /**
     * 管理员删除某一个广告
     * 下架态广告删除成功
     * @throws Exception
     */
    @Order(19)
    @Test
    public void advertiseTest20() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{did}/advertisement/{id}", 0, 446)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
    }

    /**
     * 管理员删除某一个广告
     * 审核态广告删除成功
     * @throws Exception
     */
    @Order(20)
    @Test
    public void advertiseTest21() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/{did}/advertisement/{id}", 0, 445)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString,"UTF-8"));
    }

    /**
     * 管理员上架广告
     * 操作的资源不存在
     * @throws Exception
     */
    @Order(21)
    @Test
    public void advertiseTest22() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员上架广告
     * 操作的广告状态为审核态（失败）
     * @throws Exception
     */
    @Order(22)
    @Test
    public void advertiseTest23() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 203)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员上架广告
     * 操作的广告状态为上架态（失败）
     * @throws Exception
     */
    @Order(23)
    @Test
    public void advertiseTest24() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 443)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员上架广告
     * 操作的广告状态为下架态（成功）
     * @throws Exception
     */
    @Order(24)
    @Test
    public void advertiseTest25() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/onshelves", 0, 442)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='442')].state").isEqualTo(4)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员下架广告
     * 操作的广告状态为审核态（失败）
     * @throws Exception
     */
    @Order(25)
    @Test
    public void advertiseTest26() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/offshelves", 0, 444)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员下架广告
     * 操作的广告状态为下架态（失败）
     * @throws Exception
     */
    @Order(26)
    @Test
    public void advertiseTest27() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/offshelves", 0, 440)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员下架广告
     * 操作的广告状态为上架态（成功）
     * @throws Exception
     */
    @Order(27)
    @Test
    public void advertiseTest28() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/offshelves", 0, 442)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='442')].state").isEqualTo(6)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核广告
     * 操作的广告状态为上架态（失败）
     * @throws Exception
     */
    @Order(28)
    @Test
    public void advertiseTest29() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("conclusion", true);
        body.put("message", "审核通过");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/audit", 0, 443)
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核广告
     * 操作的广告状态为下架态（失败）
     * @throws Exception
     */
    @Order(29)
    @Test
    public void advertiseTest30() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("conclusion", true);
        body.put("message", "审核通过");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/audit", 0, 440)
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核广告
     * 审核结果失败
     * @throws Exception
     */
    @Order(30)
    @Test
    public void advertiseTest31() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("conclusion", false);
        body.put("message", "审核不通过");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/audit", 0, 444)
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='444')].state").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核广告
     * 操作的广告状态为审核态（成功）
     * @throws Exception
     */
    @Order(31)
    @Test
    public void advertiseTest32() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("conclusion", true);
        body.put("message", "审核通过");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.put().uri("/shops/{did}/advertisement/{id}/audit", 0, 444)
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement",0,0)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='444')].state").isEqualTo(6)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 广告时段不存在
     * @throws Exception
     */
    @Order(32)
    @Test
    public void advertiseTest33() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement/?beginDate=2020-01-10&endDate=2020-12-12", 0, 99)
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString,"UTF-8"));
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 广告日期格式错误
     * @throws Exception
     */
    @Order(33)
    @Test
    public void advertiseTest34() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement" +
                "/?beginDate=2020/01/10&endDate=2020/12/12", 0, 1)
                .header("authorization",token)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 广告日期开始日期比结束日期晚
     * @throws Exception
     */
    @Order(34)
    @Test
    public void advertiseTest35() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement/?" +
                "beginDate=2020-12-10&endDate=2020-6-12", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 成功
     * @throws Exception
     */
    @Order(35)
    @Test
    public void advertiseTest36() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement?" +
                "beginDate=2020-01-10&endDate=2022-12-12", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":7,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[" +
                "{\"id\":122,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1476498482597274807.jpg\"," +
                "\"content\":null,\"segId\":2,\"state\":4,\"weight\":\"1\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":true}," +
                "{\"id\":424,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1475992167803037996.jpg\"," +
                "\"content\":null,\"segId\":2,\"state\":4,\"weight\":\"4\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":false}," +
                "{\"id\":425,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1476498085799000890.jpg\"," +
                "\"content\":null,\"segId\":2,\"state\":4,\"weight\":\"5\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":true}," +
                "{\"id\":426,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1477275374163106414.jpg\"," +
                "\"content\":null,\"segId\":2,\"state\":4,\"weight\":\"1\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":false}," +
                "{\"id\":427,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1477249392109367112.jpg\"," +
                "\"content\":null,\"segId\":2,\"state\":4,\"weight\":\"2\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":false}," +
                "{\"id\":428,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201611/1479267660182463918.jpg\"," +
                "\"content\":null,\"segId\":2,\"state\":4,\"weight\":\"3\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":false}," +
                "{\"id\":429,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201705/1494051636236103847.png\"," +
                "\"content\":null,\"segId\":2,\"state\":4,\"weight\":\"4\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":true}]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 设置页数
     * @throws Exception
     */
    @Order(36)
    @Test
    public void advertiseTest37() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement/?" +
                "beginDate=2020-01-10&endDate=2020-12-12&page=1&pageSize=3", 0, 2)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":{\"list\":[]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员查看某一个广告时间段的广告
     * 时段为0
     * @throws Exception
     */
    @Order(37)
    @Test
    public void advertiseTest38() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString = manageClient.get().uri("/shops/{did}/timesegments/{id}/advertisement/?" +
                "beginDate=2020-01-10&endDate=2022-12-12&page=1&pageSize=3", 0, 0)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":" +
                "{\"total\":10,\"pages\":4,\"pageSize\":3,\"page\":1,\"list\":[" +
                "{\"id\":121,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1475991949547324589.jpg\"," +
                "\"content\":null,\"segId\":0,\"state\":4,\"weight\":\"1\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":true}," +
                "{\"id\":421,\"link\":\"link1\",\"imagePath\":\"http://47.52.88.176/file/images/201610/1475991949547324589.jpg\"," +
                "\"content\":\"广告内容1\",\"segId\":0,\"state\":0,\"weight\":\"5\",\"beDefault\":false,\"beginDate\":\"2020-12-01\"," +
                "\"endDate\":\"2020-12-02\",\"repeat\":true}," +
                "{\"id\":422,\"link\":null,\"imagePath\":\"http://47.52.88.176/file/images/201610/1476498482597274807.jpg\"," +
                "\"content\":null,\"segId\":0,\"state\":6,\"weight\":\"2\",\"beDefault\":false,\"beginDate\":\"2020-12-15\"," +
                "\"endDate\":\"2021-10-10\",\"repeat\":false}" +
                "]}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员在广告时段下新建广告
     * 时段不存在
     * @throws Exception
     */
    @Order(38)
    @Test
    public void advertiseTest39() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content","内容");
        body.put("weight",1);
        body.put("beginDate","2020-01-20");
        body.put("endDate","2020-02-01");
        body.put("repeat",true);
        body.put("link","链接");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisement", 0, 99)
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 传入开始日期和结束日期格式错误
     * @throws Exception
     */
    @Order(39)
    @Test
    public void advertiseTest40() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content","内容");
        body.put("weight",1);
        body.put("beginDate","2020/01/20");
        body.put("endDate","2020/02/01");
        body.put("repeat",false);
        body.put("link","链接");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisement", 0, 2)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 传入开始日期值不合理
     * @throws Exception
     */
    @Order(40)
    @Test
    public void advertiseTest41() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content","内容");
        body.put("weight",1);
        body.put("beginDate","2020-02-30");
        body.put("endDate","2020-03-06");
        body.put("repeat",false);
        body.put("link","链接");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisement", 0, 2)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 传入开始日期大于结束日期
     * @throws Exception
     */
    @Order(41)
    @Test
    public void advertiseTest42() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content","内容");
        body.put("weight",1);
        body.put("beginDate","2020-12-12");
        body.put("endDate","2020-03-06");
        body.put("repeat",true);
        body.put("link","链接");
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisement", 0, 2)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下新建广告
     * 成功
     * @throws Exception
     */
    @Order(42)
    @Test
    public void advertiseTest43() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        JSONObject body = new JSONObject();
        body.put("content","广告内容1");
        body.put("weight",5);
        body.put("beginDate","2020-12-01");
        body.put("endDate","2020-12-02");
        body.put("repeat",true);
        body.put("link","link1");
        String requireJson = body.toJSONString();
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{id}/advertisement", 0, 2)
                .header("authorization", token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":" +
                "{\"id\":1042,\"link\":\"link1\",\"imagePath\":null,\"content\":\"广告内容1\"," +
                "\"segId\":2,\"state\":0,\"weight\":\"5\",\"beDefault\":false," +
                "\"beginDate\":\"2020-12-01\",\"endDate\":\"2020-12-02\",\"repeat\":true}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 管理员在广告时段下增加广告
     * 时段id不存在
     * @throws Exception
     */
    @Order(43)
    @Test
    public void advertiseTest44() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{tid}/advertisement/{id}", 0, 99,430)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员在广告时段下增加广告
     * 广告id不存在
     * @throws Exception
     */
    @Order(44)
    @Test
    public void advertiseTest45() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{tid}/advertisement/{id}", 0, 2,20)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * 管理员在广告时段下增加广告
     * 成功
     * @throws Exception
     */
    @Order(45)
    @Test
    public void advertiseTest46() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =  manageClient.post().uri("/shops/{did}/timesegments/{tid}/advertisement/{id}", 0, 2,430)
                .header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        log.info(new String(responseString, "UTF-8"));
        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":430,\"link\":null," +
                "\"imagePath\":\"http://47.52.88.176/file/images/201705/1494051740110225284.png\"," +
                "\"content\":null,\"segId\":2,\"state\":4,\"weight\":\"5\",\"beDefault\":false," +
                "\"beginDate\":\"2020-12-15\",\"endDate\":\"2021-10-10\"," +
                "\"repeat\":false}}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 用户登录
     *
     * @author 王显伟
     */
    public String userLogin(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString)).getString("data");
    }

    private String adminLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

}