package com.acgist.snail.net.torrent.tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.acgist.snail.config.PeerConfig.Action;
import com.acgist.snail.context.SystemThreadContext;
import com.acgist.snail.logger.Logger;
import com.acgist.snail.logger.LoggerFactory;
import com.acgist.snail.net.torrent.TorrentSession;

/**
 * Tracker执行器组
 * 
 * @author acgist
 */
public final class TrackerLauncherGroup {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackerLauncherGroup.class);
    
    /**
     * BT任务信息
     */
    private final TorrentSession torrentSession;
    /**
     * TrackerLauncher集合
     */
    private final List<TrackerLauncher> trackerLaunchers;
    
    /**
     * @param torrentSession BT任务信息
     */
    private TrackerLauncherGroup(TorrentSession torrentSession) {
        this.torrentSession = torrentSession;
        this.trackerLaunchers = new ArrayList<>();
    }
    
    /**
     * 新建Tracker执行器组
     * 
     * @param torrentSession BT任务信息
     * 
     * @return {@link TrackerLauncherGroup}
     */
    public static final TrackerLauncherGroup newInstance(TorrentSession torrentSession) {
        return new TrackerLauncherGroup(torrentSession);
    }

    /**
     * @return Tracker执行器的声明地址
     */
    public List<String> trackers() {
        synchronized (this.trackerLaunchers) {
            return this.trackerLaunchers.stream()
                .map(TrackerLauncher::announceUrl)
                .collect(Collectors.toList());
        }
    }
    
    /**
     * 加载TrackerLauncher
     */
    public void loadTracker() {
        List<TrackerSession> sessions = null;
        final var action = this.torrentSession.action();
        final var context = TrackerContext.getInstance();
        if(action == Action.TORRENT) {
            final var torrent = this.torrentSession.torrent();
            sessions = context.sessions(torrent.getAnnounce(), torrent.getAnnounceList(), this.torrentSession.privateTorrent());
        } else if(action == Action.MAGNET) {
            final var magnet = this.torrentSession.magnet();
            sessions = context.sessions(magnet.getTr());
        } else {
            sessions = context.sessions();
        }
        final var list = sessions.stream()
            .map(client -> context.buildTrackerLauncher(client, this.torrentSession))
            .collect(Collectors.toList());
        synchronized (this.trackerLaunchers) {
            this.trackerLaunchers.addAll(list);
        }
    }

    /**
     * 查找Peer
     * 
     * @see TrackerLauncher#findPeer()
     */
    public void findPeer() {
        LOGGER.debug("Tracker查找Peer：{}", this.torrentSession);
        final List<TrackerLauncher> list;
        // 新建集合进行查找：防止释放资源等待
        synchronized (this.trackerLaunchers) {
            list = new ArrayList<>(this.trackerLaunchers);
        }
        list.forEach(TrackerLauncher::findPeer);
    }

    /**
     * 释放资源
     */
    public void release() {
        LOGGER.debug("释放TrackerLauncherGroup：{}", this.torrentSession);
        synchronized (this.trackerLaunchers) {
            this.trackerLaunchers.forEach(launcher -> SystemThreadContext.submit(launcher::release));
            this.trackerLaunchers.clear();
        }
    }
    
}
