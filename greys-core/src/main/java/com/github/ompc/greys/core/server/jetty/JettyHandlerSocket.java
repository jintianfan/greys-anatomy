package com.github.ompc.greys.core.server.jetty;

import com.github.ompc.greys.core.PointCut;
import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.listener.InvokeListener;
import com.github.ompc.greys.core.server.FrameException;
import com.github.ompc.greys.core.server.FrameReq;
import com.github.ompc.greys.core.server.FrameResp;
import com.github.ompc.greys.core.server.Session;
import com.github.ompc.greys.core.util.LogUtil;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JettyHandlerSocket
 * Created by vlinux on 15/10/25.
 */
public class JettyHandlerSocket extends JettyFrameSocket implements Session {

    private final Logger logger = LogUtil.getLogger();

    // 处理器线程池
    private final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = new Thread(r, "greys-handler-worker");
            thread.setDaemon(true);
            return thread;
        }
    });

    // 帧请求集合
    private final ConcurrentHashMap<Integer, CountDownLatch> requests = new ConcurrentHashMap<Integer, CountDownLatch>();

    @Override
    public void onFrameReq(final FrameReq frameReq) throws FrameException {

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                final int id = frameReq.getId();

                // 检查帧序号是否已经存在于请求列表中
                // 如果已经存在则返回正在处理中
                if (requests.containsKey(id)) {
                    onFrameResp(new FrameResp(id, FrameResp.STATUS_HANDLING, true));
                    return;
                }

                // 构造CountDown
                final CountDownLatch countDown;
                final CountDownLatch oCountDown = new CountDownLatch(1);
                if (null != requests.putIfAbsent(id, oCountDown)) {
                    // 这里可能存在并发写入同一个帧序号
                    // 所以需要利用putIfAbsent再次进行校验
                    // 如果请求已经在处理，则直接打日志走掉
                    logger.info("frame-req={} already handing, ignore this request.", frameReq);
                    return;
                } else {
                    countDown = oCountDown;
                }

                // 是否已发送过结束帧
                // 某些主动结束的处理器会发出结束帧，但有些处理器是需要强制结束的，这部分处理器的结束帧由FrameSocket代劳
                // 所以需要一个标志变量来区分这两种情况
                final AtomicBoolean hasFinishRef = new AtomicBoolean(false);

                final Handler.Req req = frameReq.getBody();
                try {

                    // 输出器
                    final Handler.Out out = new Handler.Out() {

                        @Override
                        public Handler.Out out(Handler.Resp resp) {

                            // 发送帧报文
                            _out(resp, false);
                            return this;
                        }

                        @Override
                        public void finish(Handler.Resp resp) {

                            // 发送结束帧报文
                            _out(resp, true);

                            // 标记结束帧已经发过了
                            hasFinishRef.set(true);

                            finish();
                        }

                        private void _out(Handler.Resp resp, boolean isFinish) {
                            final FrameResp frameResp = new FrameResp(id, FrameResp.STATUS_SUCCESS, isFinish);
                            frameResp.setBody(resp);
                            onFrameResp(frameResp);
                        }

                        @Override
                        public void finish() {
                            countDown.countDown();
                        }

                    };

                    // 实例化处理器类
                    final Handler handler = frameReq.getHandlerClass().newInstance();

                    // 处理器初始化
                    handler.init(id, JettyHandlerSocket.this);
                    logger.debug("frame-req[id={};type={};]'s handler init.", id, frameReq.getType());

                    try {

                        // 处理器处理请求
                        final LinkedHashMap<InvokeListener, ArrayList<PointCut>> listenerMap = handler.handle(req, out);
                        logger.debug("frame-req[id={};type={};]'s handler handle.", id, frameReq.getType());

                        // 等待命令完成
                        waitingForFinish(id, countDown, hasFinishRef.get());

                    } finally {

                        // 处理器销毁
                        // 如论如何处理器的销毁在初始化成功之后是已定要调用
                        handler.destroy();
                        logger.debug("frame-req[id={};type={};]'s handler destroy.", id, frameReq.getType());

                    }

                } catch (Throwable t) {
                    logger.warn("frame-req[id={};type={};] handle failed.", id, frameReq.getType(), t);
                    onFrameResp(new FrameResp(id, FrameResp.STATUS_HANDLER_ERROR, true));
                    return;
                } finally {
                    // 命令处理完之后要记得擦屁股
                    requests.remove(id);
                }

                logger.debug("frame-req[id={};type={};] handle finished. ", id, frameReq);

            }
        });

    }

    /* 等待命令执行完成
     * 命令执行完成的标记是调用了Out.finish方法，或被强制关闭
     */
    private void waitingForFinish(final int id, final CountDownLatch countDown, final boolean hasFinish) {
        try {
            countDown.await();

            // 检查是否之前已经发过结束帧，如果尚未发则由此处代劳
            // 发一个空数据的结束帧，强制通知客户端当前处理已经结束
            if (!hasFinish) {
                onFrameResp(new FrameResp(id, FrameResp.STATUS_SUCCESS, true));
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);

        final InetSocketAddress remoteAddress = getRemoteAddress();
        logger.info("connection[remote={};] will be closed.", remoteAddress);

        // 清空等待锁
        final Iterator<Map.Entry<Integer, CountDownLatch>> requestIt = requests.entrySet().iterator();
        while (requestIt.hasNext()) {
            final Map.Entry<Integer, CountDownLatch> entry = requestIt.next();
            final Integer id = entry.getKey();
            final CountDownLatch countDown = entry.getValue();
            countDown.countDown();
            requestIt.remove();
            logger.info("wakeup handler-worker for cleanup. id={};remote={};", id, remoteAddress);
        }

        // 关闭线程池
        executorService.shutdown();
        logger.info("shutdown handler-worker-pool. remote={};", remoteAddress);
    }

    @Override
    public void terminate(int targetId) {
        final CountDownLatch countDown = requests.get(targetId);
        if (null != countDown) {
            countDown.countDown();
            logger.info("wakeup handler-worker for terminate. target_id={};remote={};",
                    targetId, getRemoteAddress());
        }
    }

}
