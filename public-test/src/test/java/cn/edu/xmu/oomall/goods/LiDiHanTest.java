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
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


/**
 * @author  24320182203221 Li Dihan
 * @date 2020/12/09 15:15
 */

/**
 * ERROR: FIRST GROUP ERROR
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
public class LiDiHanTest {

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
        //endregion
    }




    @Test
    @Order(0)
    public void getGrouponAllStates() throws Exception {
        byte[] ret = mallClient.get()
                .uri("/groupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"已下线\",\"code\":0}," +
                "{\"name\":\"已上线\",\"code\":1},{\"name\":\"已删除\",\"code\":2}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    @Test
    @Order(3)
    // 路径错误
    public void createGrouponAc3() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2020-12-03T11:57:39\"," +
                "\"endTime\":\"2020-12-09T11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/spus/274/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(4)
    // 创建团购活动不传名字，这是不对的
    public void createGrouponAc4() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07T11:57:39\"," +
                "\"endTime\":\"2021-12-09T11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/2/spus/274/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(6)
    // 创建团购活动不传名字，这是不对的
    public void createGrouponAc7() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07T11:57:39\"," +
                "\"endTime\":\"2021-12-09T11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.post()
                .uri("/shops/1/spus/1/groupons")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(8)
    // 改团购活动不传名字，这是不对的
    public void changeGrouponAc2() throws Exception {
//        String token = this.login("13088admin","123456");
//        String json = "{\"beginTime\":\"2021-12-12T11:57:39\"," +
//                "\"endTime\":\"2021-12-09T11:57:39\",\"strategy\":\"无\"}";
//        byte[] ret = manageClient.put()
//                .uri("/shops/1/groupons/1")
//                .header("authorization",token)
//                .bodyValue(json)
//                .exchange()
//                .expectStatus().isBadRequest()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo("503")
//                .returnResult()
//                .getResponseBodyContent();
//        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(9)
    // 改团购活动不传名字，这是不对的
    public void changeGrouponAc3() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2020-12-03T11:57:39\"," +
                "\"endTime\":\"2020-12-09T11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/1/groupons/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("503")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(10)
    // 改团购活动不传名字，这是不对的
    public void changeGrouponAc4() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07T11:57:39\"," +
                "\"endTime\":\"2021-12-09T11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/2/groupons/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("505")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(11)
    // 改团购活动不传名字，这是不对的
    public void changeGrouponAc5() throws Exception {
//        String token = this.login("13088admin","123456");
//        String json = "{\"beginTime\":\"2021-12-07T11:57:39\"," +
//                "\"endTime\":\"2021-12-09T11:57:39\",\"strategy\":\"无\"}";
//        byte[] ret = manageClient.put()
//                .uri("/shops/10/groupons/1")
//                .header("authorization",token)
//                .bodyValue(json)
//                .exchange()
//                .expectStatus().isNotFound()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo("504")
//                .returnResult()
//                .getResponseBodyContent();
//        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(12)
    // 改团购活动不传名字，这是不对的
    public void changeGrouponAc7() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"beginTime\":\"2021-12-07T11:57:39\"," +
                "\"endTime\":\"2021-12-09T11:57:39\",\"strategy\":\"无\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/1/groupons/10")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }



    @Test
    @Order(16)
    public void cancelGrouponAc5() throws Exception {
        String token = this.login("13088admin","123456");;
        byte[] ret = manageClient.delete()
                .uri("/shops/1/groupons/10")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    /**
     * ERROR: FIRST GROUP ERROR
     *
     */
    @Test
    @Order(21)
    // 根据当前数据库数据，显然没有记录
    public void getShopGrouponAc2() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.get()
                .uri(uriBuilder -> uriBuilder.path("/shops/1/groupons")
                        .queryParam("state",(byte) 1)
                        .queryParam("spuId",273L)
                        .queryParam("endTime","2020-12-12T11:57:39")
                        .build())
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":0,\"pages\":0," +
                "\"pageSize\":10,\"page\":1,\"list\":[]}" +
                ",\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    @Test
    @Order(23)
    public void getShopAllStates() throws Exception {
        byte[] ret = mallClient.get()
                .uri("/shops/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未审核\",\"code\":0}," +
                "{\"name\":\"未上线\",\"code\":1},{\"name\":\"上线\",\"code\":2}," +
                "{\"name\":\"关闭\",\"code\":3},{\"name\":\"审核未通过\",\"code\":4}],\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    @Test
    @Order(24)
    public void changeShop1() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"name\":\"麦当劳\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/1")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(25)
    public void changeShop3() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"name\":\"麦当劳\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/10")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(27)
    public void closeShop3() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.delete()
                .uri("/shops/10")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(28)
    public void auditShop1() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"conclusion\":\"true\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/0/newshops/1/audit")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }


    @Test
    @Order(29)
    // 数据库状态错误
    public void auditShop3() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"conclusion\":\"true\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/0/newshops/5/audit")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("980")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(30)
    public void auditShop4() throws Exception {
        String token = this.login("13088admin","123456");
        String json = "{\"conclusion\":\"true\"}";
        byte[] ret = manageClient.put()
                .uri("/shops/0/newshops/10/audit")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }


    @Test
    @Order(31)
    public void onlineShop1() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/3/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(32)
    public void onlineShop2() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/10/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(33)
    public void onlineShop3() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/5/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("980")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }



    @Test
    @Order(34)
    public void offlineShop1() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/4/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    @Test
    @Order(35)
    public void offlineShop2() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/10/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    @Order(36)
    public void offlineShop3() throws Exception {
        String token = this.login("13088admin","123456");
        byte[] ret = manageClient.put()
                .uri("/shops/5/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("980")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }

    @Test
    public void transSkuImage() throws Exception {
        String token = this.login("13088admin","123456");
        File file = new File("time.png");
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("img", Base64.getDecoder().decode(Base64.getEncoder().encode(data)))
                .header("Content-Disposition", "form-data; name=img; filename=image.jpg");
        byte[] ret = manageClient.post()
                .uri("/shops/1/skus/1/uploadImg")
                .header("authorization",token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("0")
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
    }







}
