package com.vejoe.techdemo;

import com.iflytek.cloud.SpeechUtility;
import com.vejoe.lib.app.BaseBluetoothApplication;
import com.vejoe.lib.core.utils.Tools;
import com.vejoe.lib.voice.iflytek.IflyTekSettings;
import com.vejoe.techdemo.voice.VoiceCfg;

import java.io.File;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

public class TechDemoApp extends BaseBluetoothApplication {
    private static final String IFLYTEK_APP_ID = "583e358c";
    private static final String IFLYTEK_VOICE_CLOUD_API_KEY = "i1Q4b8D074i7f1E9V4L8Mh2oHNwjVDHcTVVJemDW";
    private IflyTekSettings speechSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        speechSettings = new IflyTekSettings();
        initIlyTekSettings(speechSettings);
        SpeechUtility.createUtility(this, speechSettings.getSpeechParams());
    }

    /**
     * 初始设置科大讯飞语音的参数。
     * <p>此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true".</p>
     * <p>设置你申请的应用appid</p>
     * <p>注意： appid 必须和下载的SDK保持一致，否则会出现10407错误</p>
     * @param settings
     */
    protected void initIlyTekSettings(IflyTekSettings settings) {
        settings.setDefaultIflyTekSettings();

        settings.setAppId(IFLYTEK_APP_ID);
        settings.setAudioSavePath(getWorkDirectory() + "/msc/iat.wav");
        settings.setTtsAudioPath(getWorkDirectory() + "/msc/tts.wav");
        settings.setVoiceCloudApiKey(IFLYTEK_VOICE_CLOUD_API_KEY);

        VoiceCfg cfg = VoiceCfg.getInstance();
        cfg.load(getWorkDirectory());
        settings.setLanguage(cfg.getLanguage());
        settings.setVadbos(cfg.getIatVadbos());
        settings.setVadeos(cfg.getIatVadeos());
        settings.setPunctuation(cfg.getPunctuation());
        settings.setVoicer(cfg.getVoicer());
        settings.setTtsSpeed(cfg.getTtsSpeed());
        settings.setTtsPitch(cfg.getTtsPitch());
        settings.setTtsVolume(cfg.getTtsVolume());
    }

    public static String getWorkDirectory() {
        File dir = Tools.createDirectory("vejoe");
        return dir == null? null : dir.getAbsolutePath();
    }

    public IflyTekSettings getIflyTekSettings() {
        return speechSettings;
    }
}
