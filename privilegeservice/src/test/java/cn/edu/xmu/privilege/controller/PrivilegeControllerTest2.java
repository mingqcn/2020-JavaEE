package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.dao.RoleDao;
import cn.edu.xmu.privilege.dao.RoleDaoTest;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 角色控制器测试类
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
@Transactional
public class PrivilegeControllerTest2 {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MockMvc mvc;

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeControllerTest2.class);

    /**
     * 创建测试用token
     *
     * @author 24320182203281 王纬策
     * @param userId
     * @param departId
     * @param expireTime
     * @return token
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        logger.debug(token);
        return token;
    }

    /**
     * 查询角色 成功
     *
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void selectRoleTest() {
        String responseString = null;
        String token = creatTestToken(1L, 0L, 100);
        try {
            responseString = this.mvc.perform(get("/privilege/shops/0/roles?page=1&pageSize=2").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":23,\"name\":\"管理员\",\"desc\":\"超级管理员，所有权限都有\",\"createdBy\":1,\"departId\":0},{\"id\":80,\"name\":\"财务\",\"desc\":null,\"createdBy\":1,\"departId\":0}]},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询角色 失败 departId不匹配
     *
     * @author 24320182203253
     * createdBy 钱秋妍 2020/11/18 23:50
     * modifiedBy 钱秋妍 2020/11/18 23:50
     */
    @Test
    public void selectRoleTestPrivilege() {
        String responseString = null;
        String token = creatTestToken(1L, 1L, 100);
        try {
            responseString = this.mvc.perform(get("/privilege/shops/0/roles?page=1&pageSize=2").header("authorization", token))
                    .andExpect(status().isForbidden())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String expectedResponse = "{\"errno\":503,\"errmsg\":\"departId不匹配\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入角色 成功
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void insertRoleTest1() {
        RoleVo vo = new RoleVo();
        vo.setName("test");
        vo.setDescr("test");
        String token = creatTestToken(1L, 0L, 100);
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/privilege/roles").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"data\":{\"id\":88,\"name\":\"test\",\"createdBy\":1,\"departId\":0,\"desc\":\"test\",\"gmtModified\":null},\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入角色 角色名已存在
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void insertRoleTest2() {
        RoleVo vo = new RoleVo();
        vo.setName("管理员");
        vo.setDescr("管理员test");
        String token = creatTestToken(1L, 0L, 100);
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/privilege/roles").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":736,\"errmsg\":\"角色名重复：管理员\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入角色 角色名为空
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void insertRoleTest3() {
        RoleVo vo = new RoleVo();
        vo.setName("");
        vo.setDescr("test");
        String token = creatTestToken(1L, 0L, 100);
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(post("/privilege/roles").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":503,\"errmsg\":\"角色名不能为空;\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改角色 成功
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void updateRoleTest1() {
        RoleVo vo = new RoleVo();
        vo.setName("test");
        vo.setDescr("test");
        String token = creatTestToken(1L, 0L, 100);
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/privilege/shops/0/roles/23").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改角色 角色名为空
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void updateRoleTest2() {
        RoleVo vo = new RoleVo();
        vo.setName("");
        vo.setDescr("test");
        String token = creatTestToken(1L, 0L, 100);
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/privilege/shops/0/roles/87").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":503,\"errmsg\":\"角色名不能为空;\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改角色 id不存在
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void updateRoleTest3() {
        RoleVo vo = new RoleVo();
        vo.setName("test");
        vo.setDescr("test");
        String token = creatTestToken(1L, 0L, 100);
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/privilege/shops/0/roles/0").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改角色 角色名重复
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void updateRoleTest4() {
        RoleVo vo = new RoleVo();
        vo.setName("财务");
        vo.setDescr("财务test");
        String token = creatTestToken(1L, 0L, 100);
        String roleJson = JacksonUtil.toJson(vo);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(put("/privilege/shops/0/roles/23").header("authorization", token).contentType("application/json;charset=UTF-8").content(roleJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":736,\"errmsg\":\"角色名重复：财务\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除角色 成功
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void deleteRoleTest1() {
        //测试数据
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        //测试删除成功
        try {
            responseString = this.mvc.perform(delete("/privilege/shops/0/roles/23").header("authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除角色 id不存在
     * @author 24320182203281 王纬策
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    @Test
    public void deleteRoleTest2() {
        String token = creatTestToken(1L, 0L, 100);
        String expectedResponse = "";
        String responseString = null;
        try {
            responseString = this.mvc.perform(delete("/privilege/shops/0/roles/0").header("authorization", token))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andReturn().getResponse().getContentAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        expectedResponse = "{\"errno\":504,\"errmsg\":\"角色id不存在：0\"}";
        try {
            JSONAssert.assertEquals(expectedResponse, responseString, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
