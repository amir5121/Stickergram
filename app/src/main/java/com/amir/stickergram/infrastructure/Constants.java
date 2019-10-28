package com.amir.stickergram.infrastructure;

import android.graphics.Color;
import android.os.Environment;

import com.amir.stickergram.AppType;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import java.io.File;

public class Constants {
    public static final String WEBSITE = "https://stickergramapp.com/";
    private static final String STICKERGRAM = "/Stickergram";

    public static final int TEXT_COLOR = 0;
    public static final int TEXT_SHADOW_COLOR = 1;
    public static final int TEXT_BACKGROUND_COLOR = 2;
    public static final int TEXT_STROKE_COLOR = 3;
    public static final int USER_STICKER_GAIN_PERMISSION = 100;
    public static final int PHONE_STICKERS_GAIN_PERMISSION = 101;
    public static final int TEMPLATE_STICKERS_GAIN_PERMISSION = 102;
    public static final int FROM_SCRATCH_GAIN_PERMISSION = 103;
    public static final int EDIT_ACTIVITY_GAIN_PERMISSION = 104;
    public static final String KEY = "mBRJaVxaA+9k4tiD5rYicw==:II8pAgeooHpABKf7BUOykr9cHLAFHQWFCie0coKLNBw=";

    public static final int PERSIAN_LANGUAGE = 1;
    public static final int ENGLISH_LANGUAGE = 2;
    public static final int SYSTEM_LANGUAGE = 3;
    public static final int RUSSIAN_LANGUAGE = 4;
    public static final int GERMAN_LANGUAGE = 5;

    public static final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    public static final String TELEGRAM_PACKAGE_BETA = "org.telegram.messenger.beta";
    public static final String MOBOGRAM_PACKAGE = "com.hanista.mobogram";
    public static final String MOBOGRAM_TWO = "com.hanista.mobogram.two";
    public static final String GOLDEN_TELEGRAM = "org.ir.talaeii";
    public static final String TELEGRAPH_PACKAGE = "ir.ilmili.telegraph";
    public static final String TELEGRAM_PLUS_PACKAGE = "org.telegram.plus";
    public static final String PERSIAN_TELEGRAM = "ir.persianfox.messenger";
    public static final String ORANGE_TELEGRAM = "org.telegram.comorangetelegram";
    public static final String PERSIAN_VOICE_TELEGRAM = "ir.rrgc.telegram";
    public static final String MY_TELEGRAM = "ir.alimodaresi.mytelegram";
    public static final String ANIWAYS = "com.aniways.anigram.messenger";
    public static final String LAGATGRAM = "org.ilwt.lagatgram";
    public static final String ROYAL_TELEGRAM = "com.shaltouk.mytelegram";
    public static final String TELEGRAM_X = "org.thunderdog.challegram";
    public static final String INFOGRAM_PACKAGE = "com.infogram.app";
    public static final String NITRO_TELEGRAM_PACKAGE = "com.nitro.telegram";
    public static final String BGRAM_PACKAGE = "org.telegram.BifToGram";
    public static final String BLACKGRAM_PACKAGE = "uz.usoft.blackgram";
    public static final String TELEPLUS = "in.teleplus";
    public static final String TELEDR = "com.filtershekanha.teledr";

    public static final String STICKERGRAM_URL = "http://stickergramapp.com/";
    public static final String LIST_DIRECTORIES = "listDirectory.php";
    public static final String CACHE = "cache/";
    public static final String STICKERS = "stickers/";

    public static final String HAS_CASHED_PACK_STICKERS = "HAS_CASHED_PACK_STICKERS";
    public static final String HAS_CASHED_PHONE_STICKERS = "HAS_CASHED_PHONE_STICKERS";
    public static final String SETTING = "SETTING";
    public static final String EDIT_IMAGE_URI = "EDIT_IMAGE_URI";
    public static final String EDIT_IMAGE_DIR_IN_ASSET = "EDIT_IMAGE_DIR_IN_ASSET";

    public static final String PNG = ".png";
    public static final String WEBP = ".webp";
    public static final String EMAIL = "StickergramApp@gmail.com";
    public static final String LINK_TO_CHANNEL = AppType.LINK_TO_CHANNEL;
    public static final String PERSIAN_FONT_DIRECTORY = "Fonts/per";
    public static final String ENGLISH_FONT_DIRECTORY = "Fonts/eng";
    public static final String PERSIAN_FONT_NAME = "per_font_names.txt";
    public static final String FONT_DIRECTORY_IN_ASSET = "Fonts/";
    public static final String LINK_TO_BOT = "https://telegram.me/stickers";
    public static final String WEBP_CASH_DIR = AppType.WEBP_CASH_DIR;
    public static final int PACKAGE_NAME_LENGTH_LIMIT = 50;
    public static final int LIGHT_BLUE = Color.parseColor("#2196f3");
    public static final int DARK_BLUE = Color.parseColor("#1565c0");
    public static final int TRANSPARENT_DARK_BLUE = Color.parseColor("#882196f3");
    public static final String CROP_SOURCE = "CROP_SOURCE";
    public static final String CROP_DESTINY = "CROP_DESTINY";
    public static final String ACTIVE_PACK = "ACTIVE_PACK";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String CACHED_JSON = "CACHED_JSON";
//    public static final String IMAGE_URI = "IMAGE_URI";

    final static String[] availableFormats = {
            TELEGRAM_PACKAGE,
            TELEGRAM_PLUS_PACKAGE,
            MOBOGRAM_PACKAGE,
            TELEGRAPH_PACKAGE,
            GOLDEN_TELEGRAM,
            PERSIAN_TELEGRAM,
            PERSIAN_VOICE_TELEGRAM,
            LAGATGRAM,
            BLACKGRAM_PACKAGE,
            ORANGE_TELEGRAM,
            INFOGRAM_PACKAGE,
            TELEPLUS,
            NITRO_TELEGRAM_PACKAGE,
            BGRAM_PACKAGE,
            ANIWAYS,
            MY_TELEGRAM,
            MOBOGRAM_TWO,
            TELEGRAM_PACKAGE_BETA,
            TELEGRAM_X,
            TELEDR,
            ROYAL_TELEGRAM
    };

    public static final int ALL_FLAVORS = 0;
    public static final String IS_USING_EMPTY_IMAGE = "IS_USING_EMPTY_IMAGE";
    public static final String APPLICATION_PERSIAN_FONT_ADDRESS_IN_ASSET = "Vazir.ttf";
    public static final String APPLICATION_ENGLISH_FONT_ADDRESS_IN_ASSET = "Ubuntu.ttf";
    public static final String IMAGE_ROTATION = "IMAGE_ROTATION";
    public static final String PNG_NO_DOT = "png";
    public static final String MOVED_STICKERS_INFO = Environment.getExternalStorageDirectory() + Constants.STICKERGRAM + "/moveInfo.txt";

    public static final String IS_AN_IMAGE_PICKER = "IS_AN_IMAGE_PICKER";
    public static final String LAUNCHED_TO_ADD_IMAGE = "LAUNCHED_TO_ADD_IMAGE";

    public static GalleryConfig galleryConfig = new GalleryConfig.Build()
//            .limitPickPhoto(3)
            .singlePhoto(true)
            .hintOfPick("this is pick hint")
            .filterMimeTypes(new String[]{"image/*"})
            .build();

    public static final String USER_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory().toString() + STICKERGRAM + "/.user/";
    public static final String BASE_PHONE_ORGANIZED_STICKERS_DIRECTORY = Environment.getExternalStorageDirectory().toString() + STICKERGRAM + "/.phone_organized/";
    public static final String BASE_PHONE_WHATSAPP_WEBP_DIRECTORY = Environment.getExternalStorageDirectory().toString() + STICKERGRAM + "/.webps/";
    public static final String STICKERGRAM_ROOT = Environment.getExternalStorageDirectory().toString() + STICKERGRAM + File.separator;
    public static final String PICTURES_DIRECTORY = Environment.getExternalStorageDirectory().toString() + File.separator + "Pictures" + STICKERGRAM + File.separator;
    public static final String FONT_DIRECTORY = Environment.getExternalStorageDirectory().toString() + STICKERGRAM + "/font/";

}
