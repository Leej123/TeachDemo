package com.vejoe.techdemo.voice;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * 语音配置
 * Created by Administrator on 2017/2/9 0009.
 */
public class VoiceCfg {
    private final static String dir = "cfg";
    private final static String filename = "voice.cfg";
    private static VoiceCfg ourInstance = new VoiceCfg();

    /**
     * 语音识别的语言
     */
    private String language = "mandarin";

    /**
     * 语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理。
     */
    private String iatVadbos = "4000";

    /**
     * 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入,自动停止录音
     */
    private String iatVadeos = "1000";

    /**
     * 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
     */
    private String punctuation = "1";

    /**
     * 合成语速
     */
    private String ttsSpeed = "50";

    /**
     * 合成音调
     */
    private String ttsPitch = "50";

    /**
     * 合成音量
     */
    private String ttsVolume = "50";

    /**
     * 云端发音人
     */
    private String voicer = "xiaoyan";

    public static VoiceCfg getInstance() {
        return ourInstance;
    }

    private VoiceCfg() {
    }

    public void setDefaultIatSetting() {
        setLanguage("mandarin");
        setIatVadbos("4000");
        setIatVadeos("1000");
        setPunctuation("1");
    }

    public void setDefaultTtsSetting() {
        setVoicer("xiaoyan");
        setTtsSpeed("50");
        setTtsPitch("50");
        setTtsVolume("50");
    }

    public String getIatVadbos() {
        return iatVadbos;
    }

    public void setIatVadbos(String iatVadbos) {
        this.iatVadbos = iatVadbos;
    }

    public String getIatVadeos() {
        return iatVadeos;
    }

    public void setIatVadeos(String iatVadeos) {
        this.iatVadeos = iatVadeos;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPunctuation() {
        return punctuation;
    }

    public void setPunctuation(String punctuation) {
        this.punctuation = punctuation;
    }

    public String getTtsSpeed() {
        return ttsSpeed;
    }

    public void setTtsSpeed(String ttsSpeed) {
        this.ttsSpeed = ttsSpeed;
    }

    public String getTtsPitch() {
        return ttsPitch;
    }

    public void setTtsPitch(String ttsPitch) {
        this.ttsPitch = ttsPitch;
    }

    public String getTtsVolume() {
        return ttsVolume;
    }

    public void setTtsVolume(String ttsVolume) {
        this.ttsVolume = ttsVolume;
    }

    public String getVoicer() {
        return voicer;
    }

    public void setVoicer(String voicer) {
        this.voicer = voicer;
    }

    public void load(String workDir) {
        File parentDir = new File(workDir, dir);
        File file = new File(parentDir, filename);
        if (!parentDir.exists() || !file.exists()) {
            setDefaultIatSetting();
            setDefaultTtsSetting();
            return;
        }

        try {
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("language")) {
                    language = reader.nextString();
                } else if (name.equals("vadbos")) {
                    iatVadbos = reader.nextString();
                } else if (name.equals("punc")) {
                    punctuation = reader.nextString();
                } else if (name.equals("voicer")) {
                    voicer = reader.nextString();
                } else if (name.equals("speed")) {
                    ttsSpeed = reader.nextString();
                } else if (name.equals("pitch")) {
                    ttsPitch = reader.nextString();
                } else if (name.equals("volume")) {
                    ttsVolume = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(String workDir) {
        File parentDir = new File(workDir, dir);
        if (!parentDir.exists()) {//目录不存在，则创建目录
            parentDir.mkdir();
        }

        File file = new File(parentDir, filename);

        try {
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            writer.beginObject();
            writer.name("language").value(language);
            writer.name("vadbos").value(iatVadbos);
            writer.name("vadeos").value(iatVadeos);
            writer.name("punc").value(punctuation);
            writer.name("voicer").value(voicer);
            writer.name("speed").value(ttsSpeed);
            writer.name("pitch").value(ttsPitch);
            writer.name("volume").value(ttsVolume);
            writer.endObject();
            writer.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
