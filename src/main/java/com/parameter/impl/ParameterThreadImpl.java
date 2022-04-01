package com.parameter.impl;

import com.parameter.thread.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author xiaodong
 * @version 1.0.0
 * @ClassName ParameterThread.java
 * @Description TODO
 * @createTime 2022年03月17日 10:00:00
 */
@Service
public class ParameterThreadImpl {

    @Async
    @PostConstruct
    public void taskParameter(){
        Thread tt = new Thread(new TaskIssueThread());
        tt.start();
    }

    @Async
    @PostConstruct
    public void timingDelete(){
        Thread td = new Thread(new TimingDelete());
        td.start();
    }

    @Async
    @PostConstruct
    public void createZlibThread(){
        Thread td = new Thread(new CreateZlibThread());
        td.start();
    }

    @Async
    @PostConstruct
    public void createTaskThread(){
        Thread td = new Thread(new CreateTaskThread());
        td.start();
    }

    @Async
    @PostConstruct
    public void cenerateFileThread(){
        Thread td = new Thread(new GenerateFileThread());
        td.start();
    }
}
