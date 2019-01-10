package hamoney9409.speakingvocabulary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import hamoney9409.speakingvocabulary.dataformat.WORD_DATA;
import hamoney9409.speakingvocabulary.listener.OnChangeFragmentListener;


public class WordListFragment extends Fragment implements WordManager.OnInsertActiveWordListener {
    private TTSPlayerThread m_ttsPlayerThread = new TTSPlayerThread();

    OnChangeFragmentListener mOnChangeFragmentListener = null;

    public void SetChangeFragmentListener(OnChangeFragmentListener listener)
    {
        mOnChangeFragmentListener = listener;
    }


    //ArrayList<String> mListViewList = new ArrayList<>();
    WordsAdapter mListViewAdapter;

    class WordsAdapter extends BaseAdapter
    {
        public ArrayList<WORD_DATA> mDatas = new ArrayList<>();

        public int mCurSelected = -1;

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {

            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_listview_words, parent, false);
            }

            WORD_DATA word = mDatas.get(position);

            TextView textViewEnglish = (TextView) convertView.findViewById(R.id.textViewEnglish);
            TextView textViewMean = (TextView) convertView.findViewById(R.id.textViewMean);

            textViewEnglish.setText(word.englishWord);
            textViewMean.setText(word.mean);

            if (mCurSelected == position) {
                convertView.setBackgroundColor(Color.RED);
            } else {
                convertView.setBackgroundColor(Color.BLACK);
            }
            return convertView;
        }
    }

    public WordListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        m_ttsPlayerThread.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        WordManager.getInstance(getContext()).removeActiveWordListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        m_ttsPlayerThread.SetState(TTSPlayerThread.STATE.QUIT);
        super.onDestroy();
    }

    @Override
    public void onStart()
    {
        // active 단어가 추가되었을때 이 ui에서 벌어질 이벤트를 등록한다.
        WordManager.getInstance(getContext()).addActiveWordListener(this);
        super.onStart();
    }


    class ItemLongClickListener implements DialogInterface.OnClickListener
    {
        Context m_context;
        Resources m_res;
        WORD_DATA m_selectedData;

        ItemLongClickListener(Context context, Resources res, WORD_DATA selectedData)
        {
            m_context = context;
            m_res = res;
            m_selectedData = selectedData;
        }

        @Override
        public void onClick( DialogInterface dialog, int which )
        {
            CharSequence[] selector = {m_res.getString(R.string.wordDeactive), m_res.getString(R.string.wordRemove), m_res.getString(R.string.wordBack)};

            switch(which)
            {
                case 0:
                    // deactive

                    // deactive에서 팝업창을 한번 더 띄울 경우 사용자가 remove할 때도 사용자가 반사적으로 확인을 누르게 된다.
                    // 그러면 remove에서 팝업창 띄우는 의미가 없으므로 deactive는 즉시 적용되게 한다.

                    WordManager.getInstance(getContext()).setWordDeactive(m_selectedData.englishWord, m_selectedData.mean);
                    break;
                case 1:
                    // remove
                    new AlertDialog.Builder(m_context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(m_res.getString(R.string.wordWarning))
                    .setMessage(String.format( m_res.getString(R.string.sentenceAskRemoveWord), m_selectedData.englishWord, m_selectedData.mean))
                    .setPositiveButton
                    (
                        m_res.getString(android.R.string.yes),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick( DialogInterface dialog, int which )
                            {
                                WordManager.getInstance(getContext()).RemoveActiveWord(m_selectedData.englishWord, m_selectedData.mean);
                            }
                        }
                    )
                    .setNegativeButton(m_res.getString(android.R.string.no), null )
                    .show();
                    break;
                case 2:
                    // Back
                    break;
                default:

            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View resultView = inflater.inflate(R.layout.fragment_word_list, container, false);

        // 데이터 가져오기
        final WORD_DATA[] data = WordManager.getInstance(resultView.getContext()).getActiveWordList();

        mListViewAdapter = new WordsAdapter();

        // 데이터를 문자열로 변환해서 리스트뷰에 넣음
        mListViewAdapter.mDatas.ensureCapacity(data.length);
        for(int i=0; i<data.length; i++)
        {
            mListViewAdapter.mDatas.add(data[i]);
            //mListViewList.add(data[i].englishWord + ":        " + data[i].mean);
        }

        ListView listView = (ListView)resultView.findViewById(R.id.listViewWords);
        listView.setAdapter(mListViewAdapter);

        // active 단어가 추가되었을때 이 ui에서 벌어질 이벤트를 등록한다.
        WordManager.getInstance(resultView.getContext()).addActiveWordListener(this);

        // 리스트뷰의 항목을 누르고있으면 뜨는 이벤트
        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                        long id)
                {
                    if (mListViewAdapter == null)
                        return;

                    if (mListViewAdapter.mCurSelected == position)
                        mListViewAdapter.mCurSelected = -1;
                    else
                        mListViewAdapter.mCurSelected = position;
                    mListViewAdapter.notifyDataSetChanged();
                }
            }
        );
//        listView.setOnItemLongClickListener
//        (
//            new AdapterView.OnItemLongClickListener()
//            {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
//                {
//                    Context context = parent.getContext();
//                    Resources res = getResources();
//
//                    CharSequence[] selector = {res.getString(R.string.wordDeactive), res.getString(R.string.wordRemove), res.getString(R.string.wordBack)};
//                    final WORD_DATA[] data = WordManager.getInstance(getContext()).getActiveWordList();
//                    final WORD_DATA selectedData = data[position];
//
//                    new AlertDialog.Builder(context)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setTitle(res.getString(R.string.wordWarning))
//                    .setItems
//                    (
//                        selector,
//                        new ItemLongClickListener(context, res, selectedData)
//                    )
//                    .show();
//
//                    return false;
//                }
//            }
//        );

        m_ttsPlayerThread
        .StartWrite()
        .Clear()
        .Add(data)
        .EndWrite();

        // playButton
        Button playButton = (Button)resultView.findViewById(R.id.buttonPlay);
        playButton.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (m_ttsPlayerThread.GetState() == TTSPlayerThread.STATE.PLAY)
                    {
                        m_ttsPlayerThread.SetState(TTSPlayerThread.STATE.PAUSED);
                    }
                    else
                    {
                        m_ttsPlayerThread.SetState(TTSPlayerThread.STATE.PLAY);
                    }
                }
            }
        );

        // playButton
        Button buttonRemove = (Button)resultView.findViewById(R.id.buttonRemove);
        buttonRemove.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mListViewAdapter.mDatas.size() <= mListViewAdapter.mCurSelected || mListViewAdapter.mCurSelected < 0)
                        return;

                    WORD_DATA data = mListViewAdapter.mDatas.get(mListViewAdapter.mCurSelected);

                    Resources res = getResources();
                    new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(res.getString(R.string.wordWarning))
                    .setMessage(String.format( res.getString(R.string.sentenceAskRemoveWord), data.englishWord, data.mean))
                    .setPositiveButton
                    (
                        res.getString(android.R.string.yes),
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick( DialogInterface dialog, int which )
                            {
                                if (mListViewAdapter.mDatas.size() <= mListViewAdapter.mCurSelected || mListViewAdapter.mCurSelected < 0)
                                    return;

                                WORD_DATA data = mListViewAdapter.mDatas.get(mListViewAdapter.mCurSelected);

                                WordManager.getInstance(getContext()).RemoveActiveWord(data.englishWord, data.mean);
                            }
                        }
                    )
                    .setNegativeButton(res.getString(android.R.string.no), null )
                    .show();
                }
            }
        );

        Button buttonLogin = (Button)resultView.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mOnChangeFragmentListener != null)
                        mOnChangeFragmentListener.OnChangeFragment(2);
                }
            }
        );

        //mOnChangeFragmentListener

        return resultView;
    }

    @Override
    public void OnInsertActiveWord(Collection<WORD_DATA> words)
    {
        ListView listView = (ListView)getView().findViewById(R.id.listViewWords);
        try
        {
            int i = 0;
            for(WORD_DATA word : words)
            {
                mListViewAdapter.mDatas.add(word);
                //mListViewList.add(word.englishWord + ": " + word.mean);

            }
        } finally {}

        mListViewAdapter.notifyDataSetChanged();

        m_ttsPlayerThread
                .StartWrite()
                .Add(words)
                .EndWrite();
    }

    @Override
    public void OnInsertActiveWord(WORD_DATA word)
    {
        mListViewAdapter.mDatas.add(word);
        //mListViewList.add(word.englishWord + ": " + word.mean);
        m_ttsPlayerThread
                .StartWrite()
                .Add(word)
                .EndWrite();
        mListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnRemoveActiveWord(WORD_DATA word)
    {
        int index = -1;
        for(int i=0; i<mListViewAdapter.mDatas.size(); i++) // 비효율적인 코드. remove가 잘 일어나지 않는다는 가정 하에 설계됨.
        {
            if (mListViewAdapter.mDatas.get(i).equals(word))
            {
                index = i;
                break;
            }
        }

        if (index != -1)
        {
            mListViewAdapter.mDatas.remove(index);
            m_ttsPlayerThread
                    .StartWrite()
                    .Remove(word)
                    .EndWrite();
            mListViewAdapter.notifyDataSetChanged();
        }

    }
}
