package cn.edu.xmu.privilege;

import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.RoleDao;
import cn.edu.xmu.privilege.dao.UserDao;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Ming Qiu
 **/
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.privilege"})
@MapperScan("cn.edu.xmu.privilege.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.privilege.service.impl")
@EnableDiscoveryClient
public class PrivilegeServiceApplication implements ApplicationRunner {

    private  static  final Logger logger = LoggerFactory.getLogger(PrivilegeServiceApplication.class);
    /**
     * 是否初始化，生成signature和加密
     */
    @Value("${privilegeservice.initialization}")
    private Boolean initialization;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PrivilegeDao privilegeDao;

    public static void main(String[] args) {
        SpringApplication.run(PrivilegeServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (initialization){
            logger.debug("Initialize......");
            userDao.initialize();
            roleDao.initialize();
            privilegeDao.initialize();
        }
    }
}
