package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * @author 24320182203293+wumengchen
 * @date 2020-12-13
 */
@SpringBootTest(classes = PublicTestApp.class)
@Slf4j
public class WumengchenTest {

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


    private String login(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }




    /**
     * @author 24320182203293+wumengchen
     * 测试管理员审核广告
     * id不存在
     */
    @Test
    public void messageAd2() throws Exception {

        String token = this.login("13088admin", "123456");
        String mesJson = "{\"conclusion\": \"false\",\"message\": \"hhh\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/300/audit").header("authorization", token)
                .bodyValue(mesJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                //.jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }



    /**
     * @author 24320182203293+wumengchen
     * 测试管理员审核广告
     * 广告状态禁止（上架）
     */
    @Test
    public void messageAd3() throws Exception {

        String token = this.login("13088admin", "123456");
        String mesJson = "{\"conclusion\": \"false\",\"message\": \"hhh\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/202/audit").header("authorization", token)
                .bodyValue(mesJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员审核广告
     * 广告状态禁止（下架）
     */
    @Test
    public void messageAd4() throws Exception {

        String token = this.login("13088admin", "123456");
        String mesJson = "{\"conclusion\": \"false\",\"message\": \"hhh\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/209/audit").header("authorization", token)
                .bodyValue(mesJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员审核广告(审核通过)
     * 成功
     */
    @Test
    public void messageAd() throws Exception {


        String mesJson = "{\"conclusion\": \"true\",\"message\": \"ok\"}";
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/210/audit").header("authorization", token)
                .bodyValue(mesJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] responseString3 = manageClient.put().uri("/shops/0/advertisement/210/onshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();


        byte[] ret = manageClient.get().uri("/shops/0/timesegments/17/advertisement").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='210')].state").isEqualTo(4)
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

    }



    /**
     * @author 24320182203293+wumengchen
     * 测试管理员设置默认广告
     * 资源不存在
     */
    @Test
    public void becomeDefault() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/300/default").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();

    }




    /**
     * @author 24320182203293+wumengchen
     * 测试管理员上架广告
     * 操作资源的id不存在
     */
    @Test
    public void onshelf0() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/300/onshelves").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }


    /**
     * @author 24320182203293+wumengchen
     * 测试管理员上架广告
     * 广告状态禁止
     */
    @Test
    public void onshelf() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/203/onshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员上架广告
     * 成功
     */
    @Test
    public void onshelf1() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/204/onshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/0/timesegments/18/advertisement").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='204')].state").isEqualTo(4)
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员下架广告
     * 操作资源的id不存在
     */
    @Test
    public void offshelf0() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/300/offshelves").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员下架广告
     * 成功
     */
    @Test
    public void offshelf() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/205/offshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] ret = manageClient.get().uri("/shops/0/timesegments/19/advertisement").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list[?(@.id=='205')].state").isEqualTo(6)

                //.jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员下架广告
     * 广告状态禁止
     */
    @Test
    public void offshelf1() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.put().uri("/shops/0/advertisement/206/offshelves").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * @author 24320182203293+wumengchen
     * 测试管理员删除广告
     * 操作资源的id不存在
     */
    @Test
    public void deleteAd0() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/300").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();


    }


    /**
     * @author 24320182203293+wumengchen
     * 测试管理员删除广告（下架态）
     * 成功
     */
    @Test
    public void deleteAd() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/207").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                // .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }


    /**
     * @author 24320182203293+wumengchen
     * 测试管理员删除广告(审核态)
     * 成功
     */
    @Test
    public void deleteAd2() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/211").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();



    }




    /**
     * @author 24320182203293+wumengchen
     * 测试管理员删除广告(上架)
     * 成功
     */
    @Test
    public void deleteAd1() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/advertisement/208").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                //  .jsonPath("$.errmsg").isEqualTo(ResponseCode.ADVERTISEMENT_STATENOTALLOW.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }


}


