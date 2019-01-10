package hamoney9409.speakingvocabulary;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by 상범 on 2017-05-24.
 */

public class TTSManager
{
    private static TTSManager singleton = null;
    public static TTSManager getInstance(Context context)
    {
        if (singleton != null)
            return singleton;

        //  아직 context 인자 저장은 없지만, 저장이 생길때를 대비해 본인도 싱글턴이라 누수걱정이 없는 ApplicationContext를 인자로 넘겨준다.
        singleton = new TTSManager(context.getApplicationContext());
        return singleton;
    }

    public static TTSManager getInstance()
    {
        if (singleton != null)
            return singleton;

        return null;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////

    private TextToSpeech m_TTSEngine;
    private ReadWriteLock m_Lock = new ReentrantReadWriteLock();

    private TTSManager(Context context)
    {
        m_TTSEngine = new TextToSpeech
        (
            context, new TextToSpeech.OnInitListener()
            {
                @Override
                public void onInit(int status)
                {
                    if (status != TextToSpeech.ERROR)
                    {
                    }
                }

            } // getApplicationContext(), new TextToSpeech.OnInitListener()

        ); // ttsEnglish = new TextToSpeech

    }

    public boolean IsSpeaking()
    {
        boolean result;
        try
        {
            m_Lock.readLock().lock();
            result = m_TTSEngine.isSpeaking();
        }
        finally
        {
            m_Lock.readLock().unlock();
        }

        return result;
    }

    public long SpeakWord(String englishText, String foreignText)
    {
        try
        {
            m_Lock.writeLock().lock();

            m_TTSEngine.setLanguage(Locale.ENGLISH);
            Speak(englishText, TextToSpeech.QUEUE_FLUSH);
            m_TTSEngine.setLanguage(Locale.getDefault());
            Speak(foreignText, TextToSpeech.QUEUE_ADD);
        }
        finally {
            m_Lock.writeLock().unlock();
        }
        return 100 * (englishText.length() + foreignText.length());
    }

    private int Speak(String text, int queueMode)
    {
        //http://stackoverflow.com/a/29777304
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT)
        {
            return TTSGreater21(text, queueMode);
        }
        else
        {
            return TTSUnder20(text, queueMode);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private int TTSGreater21(String text, int queueMode)
    {
        return m_TTSEngine.speak(text, queueMode, null,  this.hashCode() + "");
    }

    @SuppressWarnings("deprecation")
    private int TTSUnder20(String text, int queueMode)
    {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        return m_TTSEngine.speak(text, queueMode, hashMap);
    }

}
