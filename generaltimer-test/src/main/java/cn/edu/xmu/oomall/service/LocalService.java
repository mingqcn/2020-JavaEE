package cn.edu.xmu.oomall.service;

import cn.edu.xmu.oomall.bo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * @author xincong yao
 * @date 2020-12-2
 */
@Service
public class LocalService {

	private  static  final Logger logger = LoggerFactory.getLogger(LocalService.class);

	public void test1(Integer a, Long b) {
		logger.info("method test1 executed; "
				+ "param(" + a + ", " + b + ");");
	}

	public void test2(int a, int b) {
		logger.info("method test2 executed; "
				+ "param(" + a + ", " + b + ");");
	}

	public void test3(User u) {
		logger.info("method test1 executed; "
				+ "param(" + u.toString() + ");");
	}

}
