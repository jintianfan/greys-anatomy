package com.github.ompc.greys.core.manager;

import com.github.ompc.greys.core.handler.Handler;
import com.github.ompc.greys.core.manager.impl.DefaultHandlerMetaDataManager;

/**
 * 处理器信息管理
 * Created by vlinux on 15/10/26.
 */
public interface HandlerMetaDataManager {

    /**
     * 处理器MetaData信息
     */
    class MetaData {

        private final Class<? extends Handler> handlerClass;
        private final Class<? extends Handler.Req> reqClass;
        private final Class<? extends Handler.Resp> respClass;

        public MetaData(
                final Class<? extends Handler> handlerClass,
                final Class<? extends Handler.Req> reqClass,
                final Class<? extends Handler.Resp> respClass) {
            this.handlerClass = handlerClass;
            this.reqClass = reqClass;
            this.respClass = respClass;
        }

        /**
         * 获取处理器类型
         *
         * @return 处理器类型
         */
        public Class<? extends Handler> getHandlerClass() {
            return handlerClass;
        }

        /**
         * 获取处理器请求类型
         *
         * @return 处理器请求类型
         */
        public Class<? extends Handler.Req> getReqClass() {
            return reqClass;
        }

        /**
         * 获取处理器返回类型
         *
         * @return 处理器返回类型
         */
        public Class<? extends Handler.Resp> getRespClass() {
            return respClass;
        }

    }


    /**
     * 映射处理器
     *
     * @param type 请求类型
     * @return Handler的Meta信息
     */
    MetaData mapping(String type);


    class Factory {
        private final static HandlerMetaDataManager instance = new DefaultHandlerMetaDataManager();

        public static HandlerMetaDataManager getInstance() {
            return instance;
        }
    }

}
