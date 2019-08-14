package com.yee.bigdata.common.thread;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class LogService {
    private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1000);

    private LoggerThread loggerThread;

    private PrintWriter writer;

    // @GuardeBy("this")
    private boolean isShutdown;
    // @GuardeBy("this")
    private int reservations;

    public static void main(String[] args) throws Exception{
        LogService ls = new LogService();
        ls.start();
        for(int i = 0; i < 100; i++){
            ls.log("msg:" + i);
        }

        ls.stop();
    }

    private void setThread(LoggerThread lt){
        loggerThread = lt;
    }

    private void setWriter(PrintWriter pw){
        writer = pw;
    }

    public LogService(){
        loggerThread = new LogService.LoggerThread();
        long currTimeMillis = System.currentTimeMillis();
        try{
            writer = new PrintWriter("F:\\tmp\\data\\log\\" + currTimeMillis + ".log");
        }catch(FileNotFoundException fe){
           fe.printStackTrace();
        }
    }


    public void start(){
        loggerThread.start();
    }

    public void stop(){
        synchronized (this){
            isShutdown = true;
        }

        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException{
        synchronized (this){
            if(isShutdown){
                throw new IllegalStateException();
            }
            ++reservations;
        }

        queue.put(msg);
    }

    private class LoggerThread extends Thread{
        public void run(){
            try{
                while(true){
                    try{
                        synchronized (LogService.this){
                            if(isShutdown && reservations == 0){
                                break;
                            }
                        }

                        String msg = queue.take();
                        synchronized (LogService.this){
                            --reservations;
                        }

                        writer.println(msg);
                    }catch (InterruptedException e){

                    }
                }
            }finally {
                writer.close();
            }
        } // end of run
    }

}
