package com.acgist.snail.gui.javafx;

import java.util.Optional;

import com.acgist.snail.gui.GuiContext;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * 窗口助手
 * 
 * @author acgist
 */
public final class Alerts {
    
    private Alerts() {
    }
    
    /**
     * 判断是否点击OK按钮
     * 
     * @param optional 按钮类型
     * 
     * @return 是否点击OK按钮
     */
    public static final boolean ok(Optional<ButtonType> optional) {
        return optional.isPresent() && optional.get() == ButtonType.OK;
    }
    
    /**
     * 提示窗口
     * 
     * @param title   标题
     * @param message 内容
     * 
     * @return 点击按钮类型
     */
    public static final Optional<ButtonType> info(String title, String message) {
        return Alerts.build(title, message, GuiContext.MessageType.INFO);
    }
    
    /**
     * 警告窗口
     * 
     * @param title   标题
     * @param message 内容
     * 
     * @return 点击按钮类型
     */
    public static final Optional<ButtonType> warn(String title, String message) {
        return Alerts.build(title, message, GuiContext.MessageType.WARN);
    }
    
    /**
     * 提示窗口
     * 
     * @param title   标题
     * @param message 内容
     * @param type    类型
     * 
     * @return 点击按钮类型
     */
    public static final Optional<ButtonType> build(String title, String message, GuiContext.MessageType type) {
        final Alert alert = new Alert(Alerts.getAlertType(type));
        final Scene scene = alert.getDialogPane().getScene();
        Themes.applyStyle(scene);
        final Stage stage = (Stage) scene.getWindow();
        Themes.applyLogo(stage.getIcons());
        alert.setTitle(title);
        // 去掉头部
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
    
    /**
     * 通过Gui消息类型获取窗口消息类型
     * 
     * @param type Gui消息类型
     * 
     * @return 窗口消息类型
     */
    private static final AlertType getAlertType(GuiContext.MessageType type) {
        return switch (type) {
        case NONE    -> AlertType.NONE;
        case INFO    -> AlertType.INFORMATION;
        case WARN    -> AlertType.WARNING;
        case CONFIRM -> AlertType.CONFIRMATION;
        case ERROR   -> AlertType.ERROR;
        default      -> AlertType.INFORMATION;
        };
    }
    
}
