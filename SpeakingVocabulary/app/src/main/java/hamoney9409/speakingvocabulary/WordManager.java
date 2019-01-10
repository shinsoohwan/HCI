package hamoney9409.speakingvocabulary;

import android.content.Context;
import android.database.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import hamoney9409.speakingvocabulary.dataformat.DEACTIVE_WORD_INFO;
import hamoney9409.speakingvocabulary.dataformat.WORD_DATA;

/**
 * Created by 상범 on 2017-06-07.
 */

public class WordManager
{
    public interface OnInsertActiveWordListener
    {
        void OnInsertActiveWord(Collection<WORD_DATA> words);
        void OnInsertActiveWord(WORD_DATA word);
        void OnRemoveActiveWord(WORD_DATA word);
    }
    public interface OnInsertDeactiveWordListener
    {
        void OnInsertDeactiveWord(Collection<WORD_DATA> words);
        void OnInsertDeactiveWord(WORD_DATA word);
        void OnRemoveDeactiveWord(WORD_DATA word);
    }

    private static WordManager singleton = null;

    public static WordManager getInstance(Context context)
    {
        if (singleton != null)
            return singleton;

        //  아직 context 인자 저장은 없지만, 저장이 생길때를 대비해 본인도 싱글턴이라 누수걱정이 없는 ApplicationContext를 인자로 넘겨준다.
        return singleton = new WordManager(context);
    }

    Context mApplicationContext;
    ArrayList<OnInsertActiveWordListener> mOnInsertActiveWordListeners = new ArrayList<>();
    ArrayList<OnInsertDeactiveWordListener> mOnInsertDeactiveWordListeners = new ArrayList<>();
    ArrayList<WORD_DATA> mActiveWords = new ArrayList<>();

    public WordManager(Context context)
    {
        mApplicationContext = context.getApplicationContext();
        Load();
    }

    protected void Load()
    {
        mActiveWords = WordDBHelper.getInstance(mApplicationContext).getActiveWordList();
        Collections.shuffle(mActiveWords);
    }

    public void InsertActiveWord(String englishWord, String mean) throws SQLException
    {
        WordDBHelper.getInstance(mApplicationContext).insert(englishWord, mean);

        addActiveWord(englishWord, mean);
    }

    private void addActiveWord(String englishWord, String mean)
    {
        mActiveWords.add( new WORD_DATA(englishWord, mean) );

//        for(OnInsertActiveWordListener listener : mOnInsertActiveWordListeners)
//        {
//            listener.OnInsertActiveWord( new WORD_DATA(englishWord, mean) );
//        }
    }

    public void setWordActive(String englishWord, String mean) throws SQLException
    {
        try
        {
            WordDBHelper.getInstance(mApplicationContext).setActive(englishWord, mean);
        }
        catch(SQLException e)
        {
            throw e;
        }

        TriggerRemoveDeactiveWord(englishWord, mean);
        TriggerAddActiveWord(englishWord, mean);
    }

    private void TriggerAddActiveWord(String englishWord, String mean)
    {
        mActiveWords.add( new WORD_DATA(englishWord, mean) );

        for(OnInsertActiveWordListener listener : mOnInsertActiveWordListeners)
        {
            listener.OnInsertActiveWord( new WORD_DATA(englishWord, mean) );
        }
    }

    private void TriggerRemoveActiveWord(String englishWord, String mean)
    {
        int index = -1;
        for(int i = 0; i<mActiveWords.size(); i++)
        {
            final WORD_DATA data = mActiveWords.get(i);
            if (data.englishWord == englishWord && data.mean == mean)
            {
                index = i;
                break;
            }
        }

        if (index != -1)
        {
            mActiveWords.remove(index);
        }

        for(OnInsertActiveWordListener listener : mOnInsertActiveWordListeners)
        {
            listener.OnRemoveActiveWord( new WORD_DATA(englishWord, mean) );
        }
    }

    public WORD_DATA[] getActiveWordList()
    {
        return mActiveWords.toArray(new WORD_DATA[mActiveWords.size()]);
    }

    public void addActiveWordListener(OnInsertActiveWordListener listener)
    {
        mOnInsertActiveWordListeners.add(listener);
    }

    public void removeActiveWordListener(OnInsertActiveWordListener listener)
    {
        mOnInsertActiveWordListeners.remove(listener);
    }

    public void RemoveActiveWord(String englishWord, String mean)
    {
        WordDBHelper.getInstance(mApplicationContext).remove(englishWord, mean);

        TriggerRemoveActiveWord(englishWord, mean);
        TriggerRemoveDeactiveWord(englishWord, mean);
    }

    public void setWordDeactive(String englishWord, String mean) throws SQLException
    {
        try
        {
            WordDBHelper.getInstance(mApplicationContext).setDeactive(englishWord, mean);
        }
        catch(SQLException e)
        {
            throw e;
        }

        TriggerRemoveActiveWord(englishWord, mean);
        TriggerAddDeactiveWord(englishWord, mean);
    }

    private void TriggerAddDeactiveWord(String englishWord, String mean)
    {
        //mDeactiveWords.add( new WORD_DATA(englishWord, mean) );

        for(OnInsertDeactiveWordListener listener : mOnInsertDeactiveWordListeners)
        {
            listener.OnInsertDeactiveWord( new WORD_DATA(englishWord, mean) );
        }
    }

    private void TriggerRemoveDeactiveWord(String englishWord, String mean)
    {
//        int index = -1;
//        for(int i = 0; i<mDeactiveWords.size(); i++)
//        {
//            final WORD_DATA data = mDeactiveWords.get(i);
//            if (data.englishWord == englishWord && data.mean == mean)
//            {
//                index = i;
//                break;
//            }
//        }
//
//        if (index != -1)
//        {
//            mDeactiveWords.remove(index);
//        }

        for(OnInsertDeactiveWordListener listener : mOnInsertDeactiveWordListeners)
        {
            listener.OnRemoveDeactiveWord( new WORD_DATA(englishWord, mean) );
        }
    }

    // 비활성화된 모든 단어를 불러오기 때문에 과부하의 위험이 있다.
    @Deprecated
    public WORD_DATA[] getDeactiveWordList()
    {
        ArrayList<WORD_DATA> data = WordDBHelper.getInstance(mApplicationContext).getDeactiveWordList();
        return data.toArray(new WORD_DATA[data.size()]);
    }

    public DEACTIVE_WORD_INFO[] getDeactiveWordListByEnglishWordSampling(int number)
    {
        ArrayList<DEACTIVE_WORD_INFO> data = WordDBHelper.getInstance(mApplicationContext).getDeactiveWordListByEnglishWordSampling(number);
        return data.toArray(new DEACTIVE_WORD_INFO[data.size()]);
    }

    public DEACTIVE_WORD_INFO[] getDetailActiveWordList()
    {
        ArrayList<DEACTIVE_WORD_INFO> data = WordDBHelper.getInstance(mApplicationContext).getDetailActiveWordList();
        return data.toArray(new DEACTIVE_WORD_INFO[data.size()]);
    }

    public void addDeactiveWordListener(OnInsertDeactiveWordListener listener)
    {
        mOnInsertDeactiveWordListeners.add(listener);
    }

    public void removeDeactiveWordListener(OnInsertDeactiveWordListener listener)
    {
        mOnInsertDeactiveWordListeners.remove(listener);
    }

    public void WordToTextFile(WORD_DATA[] datas)
    {

    }

    public ArrayList<WORD_DATA> TextFileToWord()
    {
        return new ArrayList<>();
    }
}
