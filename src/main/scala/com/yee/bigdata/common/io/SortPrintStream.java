package com.yee.bigdata.common.io;

import com.google.common.collect.MinMaxPriorityQueue;

import java.io.OutputStream;
import java.util.Comparator;

public class SortPrintStream extends FetchConverter {

    private static final Comparator<String> STR_COMP = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    };

    protected final MinMaxPriorityQueue<String> outputs =
            MinMaxPriorityQueue.orderedBy(STR_COMP).create();

    public SortPrintStream(OutputStream out, String encoding) throws Exception {
        super(out, false, encoding);
    }

    @Override
    public void process(String out) {
        assert out != null;
        outputs.add(out);
    }

    @Override
    public void processFinal() {
        while (!outputs.isEmpty()) {
            printDirect(outputs.removeFirst());
        }
    }
}