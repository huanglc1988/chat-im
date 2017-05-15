package com.hantang.chat.startup;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 简易聊天室程序服务端启动类
 * @author HuangLongcan
 * @since 2017-05-14
 */
public class StartupBootStrap {

    private static volatile boolean running = false;

    private static final Object monitor = new Object();

    public static void main(String[] args) {

        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:spring-context.xml");
        ctx.start();
        running = true;

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                synchronized (monitor){
                    try {
                        running = false;
                        ctx.close();
                    }finally {
                        monitor.notifyAll();
                    }
                }
            }
        });

        synchronized (monitor){
            while (running){
                try {
                    monitor.wait();
                } catch (Throwable e) {
                }
            }
        }

    }

}
