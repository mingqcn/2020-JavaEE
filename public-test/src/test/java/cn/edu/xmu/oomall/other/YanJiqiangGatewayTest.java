package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * 2 * @author: 颜吉强24320172203229
 * 3 * @date: 2020/12/15 下午2:42
 * 4
 */
@SpringBootTest(classes = PublicTestApp.class)
public class YanJiqiangGatewayTest {
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

    @Test
    public void testTokenNeededSuccessWithOldToken() {
        String token = createToken(5);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isOk().expectHeader().value("authorization", s -> {
            assertNotEquals(s, token);
        });
    }

    @Test
    public void testTokenNeededSuccessWithNewToken() {
        String token = createToken(1000000);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isOk().expectHeader().value("authorization", s -> {
            assertEquals(s, token);
        });
    }

    @Test
    public void testTokenNeededWithWrongToken() {
        mallClient.get().uri("/users").header("authorization", "test").exchange().
                expectStatus().isUnauthorized().expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode());
    }

    @Test
    public void testTokenNeededFailedTimeExpired() throws InterruptedException {
        String token = createToken(0);
        Thread.sleep(1000);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isUnauthorized().expectBody().
                jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode());

    }

    @Test
    public void testTokenUnneededWithToken() {
        String token = createToken(100);
        String requiredJson = "{\n" +
                "\t\"userName\": \"ThisNameCantExist\",\n" +
                "    \"password\": \"wrongPass\"\n" +
                "}";
        mallClient.post().uri("/users/login").header("authorization", token).bodyValue(requiredJson).
                exchange().expectBody().jsonPath("$.errno").isEqualTo(700);
    }

    @Test
    public void testTokenUnneededNormally() {
        String requiredJson = "{\n" +
                "\t\"userName\": \"ThisNameCantExist\",\n" +
                "    \"password\": \"wrongPass\"\n" +
                "}";
        mallClient.post().uri("/users/login").bodyValue(requiredJson).
                exchange().expectBody().jsonPath("$.errno").isEqualTo(700);
    }

    @Test
    public void testTokenWithPlatformDepartId() {
        String token = createWrongToken(0L, 100);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isUnauthorized().expectBody().
                jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode());
    }
    @Test
    public void testTokenWithShopDepartId() {
        String token = createWrongToken(0L, 100);
        mallClient.get().uri("/users").header("authorization", token).exchange().
                expectStatus().isUnauthorized().expectBody().
                jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode());
    }

    String createToken(int expireTime) {
        JwtHelper jwtHelper = new JwtHelper();
        String token = jwtHelper.createToken(1L, -2L, expireTime);
        return token;
    }

    String createWrongToken(Long departId,int expireTime){
        JwtHelper jwtHelper=new JwtHelper();
        String token=jwtHelper.createToken(1L,departId,expireTime);
        return token;
    }


}
