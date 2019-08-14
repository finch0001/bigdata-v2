package com.yee.bigdata.common.io;

import java.io.IOException;
import java.io.PrintStream;

//import org.apache.hive.common.util.StreamPrinter;

public class ShellCmdExecutor {
    private String cmd;
    private PrintStream out;
    private PrintStream err;

    public ShellCmdExecutor(String cmd, PrintStream out, PrintStream err) {
        this.cmd = cmd;
        this.out = out;
        this.err = err;
    }

    public int execute() throws Exception {
        try {
            Process executor = Runtime.getRuntime().exec(cmd);
            StreamPrinter outPrinter = new StreamPrinter(executor.getInputStream(), null, out);
            StreamPrinter errPrinter = new StreamPrinter(executor.getErrorStream(), null, err);

            outPrinter.start();
            errPrinter.start();

            int ret = executor.waitFor();
            outPrinter.join();
            errPrinter.join();
            return ret;
        } catch (IOException ex) {
            throw new Exception("Failed to execute " + cmd, ex);
        }
    }

}
