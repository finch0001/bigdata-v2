package com.yee.bigdata.common.util;

import java.lang.reflect.Array;

public class ArrayUtil {

    /**
     * Copies the specified range of the given array into a new sub array.
     * @param array the input array
     * @param from  the initial index of range to be copied (inclusive)
     * @param to    the final index of range to be copied (exclusive)
     */
    public static byte[] copyOfSubArray(byte[] array, int from, int to) {
        final byte[] copy = new byte[to-from];
        System.arraycopy(array, from, copy, 0, to-from);
        return copy;
    }

    /**
     * Copies the specified range of the given array into a new sub array.
     * @param array the input array
     * @param from  the initial index of range to be copied (inclusive)
     * @param to    the final index of range to be copied (exclusive)
     */
    public static char[] copyOfSubArray(char[] array, int from, int to) {
        final char[] copy = new char[to-from];
        System.arraycopy(array, from, copy, 0, to-from);
        return copy;
    }

    /**
     * Copies the specified range of the given array into a new sub array.
     * @param array the input array
     * @param from  the initial index of range to be copied (inclusive)
     * @param to    the final index of range to be copied (exclusive)
     */
    public static short[] copyOfSubArray(short[] array, int from, int to) {
        final short[] copy = new short[to-from];
        System.arraycopy(array, from, copy, 0, to-from);
        return copy;
    }

    /**
     * Copies the specified range of the given array into a new sub array.
     * @param array the input array
     * @param from  the initial index of range to be copied (inclusive)
     * @param to    the final index of range to be copied (exclusive)
     */
    public static int[] copyOfSubArray(int[] array, int from, int to) {
        final int[] copy = new int[to-from];
        System.arraycopy(array, from, copy, 0, to-from);
        return copy;
    }

    /**
     * Copies the specified range of the given array into a new sub array.
     * @param array the input array
     * @param from  the initial index of range to be copied (inclusive)
     * @param to    the final index of range to be copied (exclusive)
     */
    public static long[] copyOfSubArray(long[] array, int from, int to) {
        final long[] copy = new long[to-from];
        System.arraycopy(array, from, copy, 0, to-from);
        return copy;
    }

    /**
     * Copies the specified range of the given array into a new sub array.
     * @param array the input array
     * @param from  the initial index of range to be copied (inclusive)
     * @param to    the final index of range to be copied (exclusive)
     */
    public static float[] copyOfSubArray(float[] array, int from, int to) {
        final float[] copy = new float[to-from];
        System.arraycopy(array, from, copy, 0, to-from);
        return copy;
    }

    /**
     * Copies the specified range of the given array into a new sub array.
     * @param array the input array
     * @param from  the initial index of range to be copied (inclusive)
     * @param to    the final index of range to be copied (exclusive)
     */
    public static double[] copyOfSubArray(double[] array, int from, int to) {
        final double[] copy = new double[to-from];
        System.arraycopy(array, from, copy, 0, to-from);
        return copy;
    }

    /**
     * Copies the specified range of the given array into a new sub array.
     * @param array the input array
     * @param from  the initial index of range to be copied (inclusive)
     * @param to    the final index of range to be copied (exclusive)
     */
    public static <T> T[] copyOfSubArray(T[] array, int from, int to) {
        final int subLength = to - from;
        final Class<? extends Object[]> type = array.getClass();
        @SuppressWarnings("unchecked")
        final T[] copy = (type == Object[].class)
                ? (T[]) new Object[subLength]
                : (T[]) Array.newInstance(type.getComponentType(), subLength);
        System.arraycopy(array, from, copy, 0, subLength);
        return copy;
    }

}
