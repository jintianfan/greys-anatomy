package com.github.ompc.greys.core.server.jetty;

import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.manager.HandlerMetaDataManager;
import com.github.ompc.greys.core.server.FrameException;
import com.github.ompc.greys.core.server.FrameReq;
import com.github.ompc.greys.core.server.FrameResp;
import com.github.ompc.greys.core.util.LogUtil;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_DASHES;

/**
 * JettyFrameSocket
 * Created by vlinux on 15/10/24.
 */
public abstract class JettyFrameSocket extends WebSocketAdapter {

    private final Logger logger = LogUtil.getLogger();
    private final Gson gson = new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_DASHES).create();
    private final JsonParser jsonParser = new JsonParser();
    private final HandlerMetaDataManager handlerMetaDataManager = HandlerMetaDataManager.Factory.getInstance();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final Flusher flusher = new Flusher();

    private InetSocketAddress remoteAddress;

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        remoteAddress = session.getRemoteAddress();

        // 启动刷新线程
        new Thread(flusher).start();

        logger.info("connection[remote={}] connected.", remoteAddress);
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);

        // 关闭刷新线程
        flusher.isRunning = false;

        logger.info("remote={} closed. code={};reason={};", remoteAddress, statusCode, reason);
    }


    @Override
    public void onWebSocketError(Throwable cause) {
        logger.warn("websocket occur an error, connection[remote={}] will be close.", remoteAddress, cause);
        closeSession();
    }

    @Override
    public void onWebSocketText(String json) {

        // 空请求内容直接忽略
        if (StringUtils.isBlank(json)) {
            logger.debug("remote={} send an empty frame-req, ignore this req.", remoteAddress);
            return;
        }


        // 序列化帧头
        final JsonElement jsonElement;
        final FrameReq frameReq;
        try {
            jsonElement = jsonParser.parse(json);
            frameReq = gson.fromJson(jsonElement, FrameReq.class);
        } catch (JsonSyntaxException e) {
            // 解析FrameReq时发生错误，则认为是帧格式错误
            // 对于帧格式错误而言，目前只能简单的打行日志
            logger.warn("illegal frame-req format. json={}", json);
            return;
        }


        logger.debug("remote={} send an frame-req. json={}", remoteAddress, json);
        try {

            // 反序列化帧体
            parseFrameBody(frameReq, jsonElement);

            // 应答请求帧
            onFrameReq(frameReq);

        } catch (Throwable t) {
            onError(frameReq.getId(), t);
        }

    }

    /**
     * 校验请求参数是否合法
     *
     * @param req 请求参数
     * @throws FrameException 请求参数不合法
     */
    private void validate(final Handler.Req req) throws FrameException {

        // 如果帧请求数据为空，则说明传入的body不合法
        if (null == req) {
            throw new FrameException(FrameResp.STATUS_ILLEGAL_BODY_FORMAT);
        }

        // 校验数据格式
        final Set<ConstraintViolation<Handler.Req>> violations = validator.validate(req);
        if (null != violations
                && !violations.isEmpty()) {
            throw new FrameException(FrameResp.STATUS_ILLEGAL_PARAMETERS);
        }
    }


    /*
     * 解析帧体
     */
    private Handler.Req parseFrameBody(final FrameReq frameReq, final JsonElement jsonElement) throws FrameException {
        final String type = frameReq.getType();
        final HandlerMetaDataManager.MetaData metaData = handlerMetaDataManager.mapping(type);
        if (null == metaData) {
            // meta-data中如果没有注册到对应的type，则认为是无效的frame-type
            throw new FrameException(FrameResp.STATUS_ILLEGAL_FRAME_TYPE);
        }

        final Handler.Req req;
        try {
            req = gson.fromJson(jsonElement.getAsJsonObject().getAsJsonObject("body"), metaData.getReqClass());

            // 验证请求参数是否合法
            validate(req);

            // 填充FrameReq
            frameReq.setBody(req);
            frameReq.setHandlerClass(metaData.getHandlerClass());

        } catch (JsonSyntaxException e) {
            // 解析body的时候出错，则需要抛出帧请求数据错误
            throw new FrameException(FrameResp.STATUS_ILLEGAL_BODY_FORMAT, e);
        }

        return req;
    }

    /**
     * 处理帧请求
     *
     * @param frameReq 帧请求
     */
    abstract public void onFrameReq(final FrameReq frameReq) throws FrameException;

    /**
     * 组织返回错误信息
     *
     * @param id    帧序列
     * @param cause 错误异常
     */
    private void onError(final int id, final Throwable cause) {

        // 构造错误应答报文
        final FrameResp frameResp;
        if (cause instanceof FrameException) {
            frameResp = new FrameResp(id, ((FrameException) cause).getStatus(), true);
            logger.warn("handle frame-req failed. id={};status={};", id, frameResp.getStatus());
        } else {
            frameResp = new FrameResp(id, FrameResp.STATUS_UN_KNOW_ERROR, true);
            logger.warn("handle frame-req failed. id={};status={};", id, frameResp.getStatus(), cause);
        }

        // 发送应答报文
        onFrameResp(frameResp);

    }

    /**
     * 应答帧
     *
     * @param frameResp 应答帧
     */
    public void onFrameResp(final FrameResp frameResp) {

        if (isNotConnected()) {
            logger.info("ignore frame-resp={}, because network was not connected.", frameResp);
            return;
        }

        // 丢入发送队列
        flusher.push(frameResp);

    }

    /**
     * 关闭会话
     */
    public void closeSession() {
        final Session session = getSession();
        if (isConnected()
                && null != session) {
            session.close();
        }
    }


    /**
     * 获取远程客户端地址信息
     *
     * @return 远程客户端地址信息
     */
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }


    /**
     * 异步刷新
     */
    class Flusher implements Runnable {

        // 刷新队列
        private final BlockingQueue<FrameResp> flushingQueue = new LinkedBlockingQueue<FrameResp>();

        /**
         * 推应大包到刷新队列
         *
         * @param frameResp 应答包
         */
        private void push(FrameResp frameResp) {
            flushingQueue.offer(frameResp);
        }

        /*
         * 刷新线程运行标记
         */
        volatile boolean isRunning = true;

        @Override
        public void run() {

            final Thread currentThread = Thread.currentThread();
            currentThread.setName("greys-worker-flusher");

            final RemoteEndpoint remote = getRemote();

            while (isRunning) {
                // 等待获取等着刷新
                try {

                    // 从队列获取待发送报文
                    final FrameResp frameResp = flushingQueue.poll(500L, TimeUnit.MILLISECONDS);
                    if (null == frameResp) {
                        continue;
                    }

                    // 刷新
                    try {

                        // 序列化为Json
                        final String frameRespJson = gson.toJson(frameResp);

                        // 发送报文
                        remote.sendString(frameRespJson);

                        // 当获取到队列最后一个报文后强制刷新
                        if (flushingQueue.isEmpty()) {
                            remote.flush();
                        }

                    }

                    // 网络传输出错：关闭会话
                    catch (IOException e) {
                        logger.warn("send frame-resp occur I/O error. connection[remote={}] will be closed.",
                                remoteAddress, e);
                        closeSession();
                        break;
                    }

                    // 其他异常：关闭会话
                    catch (Throwable cause) {
                        logger.warn("send frame-resp occur error. connection[remote={}] will be closed.",
                                remoteAddress, cause);
                        closeSession();
                        break;
                    }
                } catch (InterruptedException e) {
                    // ignore
                }

            }//while

            logger.info("{} was quit.", currentThread.getName());
        }
    }

}
