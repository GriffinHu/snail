package com.acgist.snail.net.torrent.peer.extension;

import java.nio.ByteBuffer;

import com.acgist.snail.config.PeerConfig.ExtensionType;
import com.acgist.snail.logger.Logger;
import com.acgist.snail.logger.LoggerFactory;
import com.acgist.snail.net.torrent.peer.ExtensionMessageHandler;
import com.acgist.snail.net.torrent.peer.ExtensionTypeMessageHandler;
import com.acgist.snail.net.torrent.peer.PeerSession;
import com.acgist.snail.utils.NumberUtils;

/**
 * The lt_donthave extension
 * 协议链接：http://bittorrent.org/beps/bep_0054.html
 * 不再含有某个Piece
 * 
 * @author acgist
 */
public final class DontHaveExtensionMessageHandler extends ExtensionTypeMessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DontHaveExtensionMessageHandler.class);
    
    /**
     * @param peerSession             Peer信息
     * @param extensionMessageHandler 扩展协议代理
     */
    private DontHaveExtensionMessageHandler(PeerSession peerSession, ExtensionMessageHandler extensionMessageHandler) {
        super(ExtensionType.LT_DONTHAVE, peerSession, extensionMessageHandler);
    }

    /**
     * 新建dontHave扩展协议代理
     * 
     * @param peerSession             Peer信息
     * @param extensionMessageHandler 扩展协议代理
     * 
     * @return dontHave扩展协议代理
     */
    public static final DontHaveExtensionMessageHandler newInstance(PeerSession peerSession, ExtensionMessageHandler extensionMessageHandler) {
        return new DontHaveExtensionMessageHandler(peerSession, extensionMessageHandler);
    }
    
    @Override
    protected void doMessage(ByteBuffer buffer) {
        this.dontHave(buffer);
    }
    
    /**
     * 发送dontHave消息
     * 
     * @param index Piece索引
     */
    public void dontHave(int index) {
        LOGGER.debug("发送dontHave消息：{}", index);
        final byte[] bytes = NumberUtils.intToBytes(index);
        this.pushMessage(bytes);
    }
    
    /**
     * 处理dontHave消息
     * 
     * @param buffer 消息
     */
    private void dontHave(ByteBuffer buffer) {
        final int index = buffer.getInt();
        LOGGER.debug("处理dontHave消息：{}", index);
        this.peerSession.pieceOff(index);
    }

}
