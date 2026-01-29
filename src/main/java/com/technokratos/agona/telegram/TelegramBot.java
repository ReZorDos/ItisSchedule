package com.technokratos.agona.telegram;

import com.technokratos.agona.config.BotConfig;
import com.technokratos.agona.handler.StartHandler;
import com.technokratos.agona.service.GoogleSheetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final GoogleSheetsService googleSheetsService;


    private final StartHandler startHandler;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            /*
            крч, нужно всегда получать состояние пользователя и отталкиваться от него.
            И разделить методы на обработку команди и на обработку сообщений
            */
            String answer = "";
            switch (messageText){
                case "/start":
                    String message = startHandler.startCommandReceived(update.getMessage().getChat().getFirstName());
                    sendMessage(chatId, message);
                    break;
                case "/groups":
                    List<List<Object>> groups = googleSheetsService.getAllGroups();
                    sendMessage(chatId, String.format("Выберите вашу группу: %s", groups.toString()));

                    break;
                case "/ok":
                    sendMessage(chatId, "ok");
                    break;
                default:
                    List<List<Object>> list = googleSheetsService.readMyColumnsFromSheet(messageText);
                    sendMessage(chatId, list.toString());
            }
        }

    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(textToSend)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    
}
