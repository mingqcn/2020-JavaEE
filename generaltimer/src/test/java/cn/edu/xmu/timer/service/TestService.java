package cn.edu.xmu.timer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TestService {
    @Autowired
    ScheduleJob scheduleJob;
    List<Integer> results;
    public void test(){
        if(results==null){
            results=new ArrayList<>();
        }
        results.add(scheduleJob.getSecondWheel().getCurrent());
        log.debug("current:"+scheduleJob.getSecondWheel().getCurrent());
    }
}
