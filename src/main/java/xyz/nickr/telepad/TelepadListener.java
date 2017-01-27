package xyz.nickr.telepad;

import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pro.zackpollard.telegrambot.api.chat.CallbackQuery;
import pro.zackpollard.telegrambot.api.chat.inline.InlineCallbackQuery;
import pro.zackpollard.telegrambot.api.chat.message.MessageCallbackQuery;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineCallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.MessageCallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;
import xyz.nickr.telepad.menu.InlineMenuButtonResponse;
import xyz.nickr.telepad.menu.InlineMenuMessage;

/**
 * @author Nick Robson
 */
@Getter
@AllArgsConstructor
public class TelepadListener implements Listener {

    private final TelepadBot bot;

    private void handleCallback(String callback, User user, CallbackQuery query) {
        try {
            if (callback.startsWith(InlineMenuMessage.CALLBACK_UNIQUE)) {
                String[] split = callback.split("\\[");
                if (split.length == 5) {
                    InlineMenuMessage message = InlineMenuMessage.getMessage(split[1], split[2]);
                    if (message != null) {
                        if (message.getUserPredicate() == null || message.getUserPredicate().test(user)) {
                            int row = Integer.parseInt(split[3], InlineMenuMessage.RADIX);
                            int col = Integer.parseInt(split[4], InlineMenuMessage.RADIX);
                            BiFunction<InlineMenuMessage, User, InlineMenuButtonResponse> func = message.getMenu().getRows().get(row).getButtons().get(col).getCallback();
                            if (func != null) {
                                try {
                                    InlineMenuButtonResponse response = func.apply(message, user);
                                    if (response != null) {
                                        query.answer(response.getText(), response.isAlert());
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        } else {
                            query.answer("You are not allowed to use that!", true);
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onMessageCallbackQueryReceivedEvent(MessageCallbackQueryReceivedEvent event) {
        MessageCallbackQuery query = event.getCallbackQuery();
        handleCallback(query.getData(), query.getFrom(), query);
    }

    @Override
    public void onInlineCallbackQueryReceivedEvent(InlineCallbackQueryReceivedEvent event) {
        InlineCallbackQuery query = event.getCallbackQuery();
        handleCallback(query.getData(), query.getFrom(), query);
    }

}
