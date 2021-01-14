package cn.edu.xmu.oomall.runner;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.bo.User;
import cn.edu.xmu.oomall.service.LocalService;
import cn.edu.xmu.timer.client.TimerService;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Task;
import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xincong yao
 */
@Component
public class TaskSender implements CommandLineRunner {

	private  static  final Logger logger = LoggerFactory.getLogger(LocalService.class);

	@DubboReference(version = "0.0.1-SNAPSHOT")
	private TimerService timerService;

	@Override
	public void run(String... args) throws Exception {

		LocalDateTime dateTime = LocalDateTime.now();

		for (int i = 0; i < 60; i++) {
			Task t1 = getLocalServiceTest1(1, 2L, dateTime, i + 60 * 5L);
			ReturnObject<Task> r1 = timerService.createTask(t1, 8);
			logger.info("send task successfully, response: " + JSON.toJSONString(r1));
		}
		Task t1 = getLocalServiceTest1(1, 2L, dateTime, 1 + 60 * 5L);
		ReturnObject<Task> r1 = timerService.createTask(t1, 8);
		logger.info("send task successfully, response: " + JSON.toJSONString(r1));

		Task t11 = getLocalServiceTest1(1, 2L, dateTime, 1 + 60 * 5L);
		ReturnObject<Task> r11 = timerService.createTask(t11, 0);
		logger.info("send task successfully, response: " + JSON.toJSONString(r11));
		Task t12 = getLocalServiceTest1(1, 2L, dateTime, 20 + 60 * 5L);
		ReturnObject<Task> r12 = timerService.createTask(t12, 0);
		logger.info("send task successfully, response: " + JSON.toJSONString(r12));


		Task t2 = getLocalServiceTest2(2, 4, dateTime, 1 + 60 * 5L);
		ReturnObject<Task> r2 = timerService.createTask(t2, 0);
		logger.info("send task successfully, response: " + JSON.toJSONString(r2));

		Task t3 = getLocalServiceTest3(new User(123L, "superuser"), dateTime, 1 + 60 * 5L);
		ReturnObject<Task> r3 = timerService.createTask(t3, 0);
		logger.info("send task successfully, response: " + JSON.toJSONString(r3));
	}

	private Task getLocalServiceTest1(Integer a, Long b, LocalDateTime base, Long seconds) throws NoSuchMethodException {
		Task task = new Task();
		task.setBeanName("localService");
		task.setMethodName("test1");
		task.setBeginTime(base.plusSeconds(seconds));
		task.setReturnTypeName(LocalService.class.getMethod("test1", Integer.class, Long.class).getReturnType().getName());
		task.setTopic("timer");
		task.setTag("test");
		List<Param> params = new ArrayList<>();
		Param p1 = new Param();
		p1.setSeq(1);
		p1.setTypeName(Integer.class.getName());
		p1.setParamValue(JSON.toJSONString(a));
		Param p2 = new Param();
		p2.setSeq(2);
		p2.setTypeName(Long.class.getName());
		p2.setParamValue(JSON.toJSONString(b));
		params.add(p1);
		params.add(p2);
		task.setParamList(params);

		return task;
	}

	private Task getLocalServiceTest2(int a, int b, LocalDateTime base, Long seconds) throws NoSuchMethodException {
		Task task = new Task();
		task.setBeanName("localService");
		task.setMethodName("test2");
		task.setBeginTime(base.plusSeconds(seconds));
		task.setReturnTypeName(LocalService.class.getMethod("test2", int.class, long.class).getReturnType().getName());
		task.setTopic("timer");
		task.setTag("test");
		List<Param> params = new ArrayList<>();
		Param p1 = new Param();
		p1.setSeq(1);
		p1.setTypeName(int.class.getName());
		p1.setParamValue(JSON.toJSONString(a));
		Param p2 = new Param();
		p2.setSeq(2);
		p2.setTypeName(long.class.getName());
		p2.setParamValue(JSON.toJSONString(b));
		params.add(p1);
		params.add(p2);
		task.setParamList(params);

		return task;
	}

	private Task getLocalServiceTest3(User u, LocalDateTime base, Long seconds) throws NoSuchMethodException {
		Task task = new Task();
		task.setBeanName("localService");
		task.setMethodName("test3");
		task.setBeginTime(base.plusSeconds(seconds));
		task.setReturnTypeName(LocalService.class.getMethod("test3", User.class).getReturnType().getName());
		task.setTopic("timer");
		task.setTag("test");
		List<Param> params = new ArrayList<>();
		Param p1 = new Param();
		p1.setSeq(1);
		p1.setTypeName(User.class.getName());
		p1.setParamValue(JSON.toJSONString(u));
		params.add(p1);
		task.setParamList(params);

		return task;
	}
}
