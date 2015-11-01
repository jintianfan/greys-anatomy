package com.github.ompc.greys.core.util.collection;

/**
 * 堆栈接口
 * Created by vlinux on 15/10/24.
 */
public interface GaStack<E> {

    /**
     * 弹栈
     *
     * @return 栈顶元素
     */
    E pop();

    /**
     * 压栈
     *
     * @param e 元素
     */
    void push(E e);

    /**
     * 窥探栈顶元素
     *
     * @return 栈顶元素
     */
    E peek();

    /**
     * 堆栈是否为空
     *
     * @return true:空栈/false:非空栈
     */
    boolean isEmpty();

}
