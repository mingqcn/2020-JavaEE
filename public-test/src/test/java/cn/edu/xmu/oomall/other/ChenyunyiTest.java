package cn.edu.xmu.oomall.other;


import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.springframework.beans.factory.annotation.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 陈芸衣
 * 24320182203182
 */
@SpringBootTest(classes = PublicTestApp.class)

public class ChenyunyiTest {


    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setup(){
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }

    private String Userlogin(String userName, String password) throws Exception {
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    /**
     * 获得售后单所有状态
     * @throws Exception
     */
    @Test
    public void getSaleStateTest1() throws Exception{

        byte[] responseString=mallClient.get().uri("/aftersales/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expected="{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"code\": 0,\n" +
                "            \"name\": \"待管理员审核\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": 1,\n" +
                "            \"name\": \"待买家发货\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": 2,\n" +
                "            \"name\": \"买家已发货\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": 3,\n" +
                "            \"name\": \"待店家退款\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": 4,\n" +
                "            \"name\": \"待店家发货\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": 5,\n" +
                "            \"name\": \"店家已发货\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": 6,\n" +
                "            \"name\": \"审核不通过\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": 7,\n" +
                "            \"name\": \"已取消\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"code\": 8,\n" +
                "            \"name\": \"已结束\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expected, new String(responseString, "UTF-8"), true);
    }


}
