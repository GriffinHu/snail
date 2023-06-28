package com.acgist.snail.gui.event.adapter;

import com.acgist.snail.gui.event.GuiEventMessage;
import com.acgist.snail.net.application.ApplicationMessage;

/**
 * GUI提示消息事件
 * 
 * @author acgist
 */
public class NoticeEventAdapter extends GuiEventMessage {

    public NoticeEventAdapter() {
        super(Type.NOTICE, "提示消息事件", ApplicationMessage.Type.NOTICE);
    }

}
