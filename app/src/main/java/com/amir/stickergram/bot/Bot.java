package com.amir.stickergram.bot;

import android.util.Log;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.IOException;

public class Bot {
    TelegramBot bot;
    int chatId = 43871733;

    public Bot() {
        bot = TelegramBotAdapter.build("234957850:AAECfh0S-KA7pXx856YEP1R2qfQO6aXe7W8");
    }

    //        SendResponse sendResponse = bot.execute(
    public void sendMessage() {

//    SendResponse sendResponse = bot.execute(new SendMessage(chatId, "message text"));

        bot.execute(new SendMessage(chatId, "message <b>bold</b> text")
                        .parseMode(ParseMode.HTML)
                        .replyMarkup(new ReplyKeyboardMarkup(new String[]{"button 1", "button 2"}))
                , new Callback() {
                    @Override
                    public void onResponse(BaseRequest request, BaseResponse response) {
                        Log.e(getClass().getSimpleName(), "chat response");
                    }

                    @Override
                    public void onFailure(BaseRequest request, IOException e) {
                        Log.e(getClass().getSimpleName(), "chat failure");
                    }
                });

        bot.execute(new SendMessage("@Stickers", "message text"), new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                Log.e(getClass().getSimpleName(), "stickers respond");
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                Log.e(getClass().getSimpleName(), "stickers Failed");
            }
        });
    }

}
