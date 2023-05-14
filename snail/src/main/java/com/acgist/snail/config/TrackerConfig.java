package com.acgist.snail.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.acgist.snail.logger.Logger;
import com.acgist.snail.logger.LoggerFactory;
import com.acgist.snail.net.torrent.tracker.TrackerContext;
import com.acgist.snail.net.torrent.tracker.TrackerSession;
import com.acgist.snail.utils.StringUtils;

/**
 * Tracker配置
 * 
 * @author acgist
 */
public final class TrackerConfig extends PropertiesConfig {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackerConfig.class);
    
    /**
     * 动作
     * 
     * @author acgist
     */
    public enum Action {
        
        /**
         * 连接
         */
        CONNECT(0, "connect"),
        /**
         * 声明
         */
        ANNOUNCE(1, "announce"),
        /**
         * 刮擦
         */
        SCRAPE(2, "scrape"),
        /**
         * 错误
         */
        ERROR(3, "error");
        
        /**
         * 动作ID
         */
        private final int id;
        /**
         * 动作名称
         */
        private final String value;

        /**
         * @param id    动作ID
         * @param value 动作名称
         */
        private Action(int id, String value) {
            this.id    = id;
            this.value = value;
        }
        
        /**
         * @return 动作ID
         */
        public int getId() {
            return this.id;
        }
        
        /**
         * @return 动作名称
         */
        public String getValue() {
            return this.value;
        }
        
        /**
         * @param id 动作ID
         * 
         * @return 动作
         */
        public static final Action of(int id) {
            final Action[] values = Action.values();
            for (final Action action : values) {
                if(action.id == id) {
                    return action;
                }
            }
            return null;
        }
        
    }
    
    /**
     * 声明事件
     * 
     * @author acgist
     * 
     * @see Action#ANNOUNCE
     */
    public enum Event {
        
        /**
         * none
         */
        NONE(0, "none"),
        /**
         * 完成
         */
        COMPLETED(1, "completed"),
        /**
         * 开始
         */
        STARTED(2, "started"),
        /**
         * 停止
         */
        STOPPED(3, "stopped");
        
        /**
         * 事件ID
         */
        private final int id;
        /**
         * 事件名称
         */
        private final String value;

        /**
         * @param id    事件ID
         * @param value 事件名称
         */
        private Event(int id, String value) {
            this.id    = id;
            this.value = value;
        }

        /**
         * @return 事件ID
         */
        public int getId() {
            return this.id;
        }
        
        /**
         * @return 事件名称
         */
        public String getValue() {
            return this.value;
        }

    }
    
    /**
     * Tracker配置文件
     */
    public static final String TRACKER_CONFIG = "/config/bt.tracker.properties";
    /**
     * Tracker最大请求失败次数
     * 超过以后不会再次使用并且不会保持
     */
    public static final int MAX_FAIL_TIMES = 3;
    /**
     * Tracker服务器最大保存数量
     */
    public static final int MAX_TRACKER_SIZE = 512;
    
    /**
     * 默认Tracker服务器
     * index=AnnounceUrl
     */
    private final List<String> announces = new ArrayList<>();
    
    private static final TrackerConfig INSTANCE = new TrackerConfig();
    
    public static final TrackerConfig getInstance() {
        return INSTANCE;
    }
    
    private TrackerConfig() {
        super(TRACKER_CONFIG);
        this.init();
        this.release();
    }
    
    @Override
    public void init() {
        this.properties.entrySet().forEach(entry -> {
            final String announce = (String) entry.getValue();
            if(StringUtils.isNotEmpty(announce)) {
                this.announces.add(announce);
            } else {
                LOGGER.warn("默认Tracker服务器注册失败：{}", announce);
            }
        });
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("加载Tracker服务器数量：{}", this.announces.size());
        }
    }
    
    @Override
    public void persistent() {
        final AtomicInteger index = new AtomicInteger(0);
        final Map<String, String> data = TrackerContext.getInstance().sessions().stream()
            .filter(TrackerSession::available)
            .sorted()
            .limit(MAX_TRACKER_SIZE)
            .collect(Collectors.toMap(
                session -> String.format("%04d", index.incrementAndGet()),
                TrackerSession::announceUrl
            ));
        this.persistent(data, TRACKER_CONFIG);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("保存Tracker服务器数量：{}", data.size());
        }
    }
    
    /**
     * @return 默认Tracker服务器
     */
    public List<String> getAnnounces() {
        return this.announces;
    }
    
}
