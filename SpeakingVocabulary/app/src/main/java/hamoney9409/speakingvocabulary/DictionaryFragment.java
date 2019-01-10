package hamoney9409.speakingvocabulary;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import hamoney9409.speakingvocabulary.R;
import hamoney9409.speakingvocabulary.listener.OnChangeFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DictionaryFragment extends Fragment {
    TextView mTextviewEnglish;
    TextView mTextViewMean ;

    OnChangeFragmentListener mOnChangeFragmentListener = null;

    public void SetChangeFragmentListener(OnChangeFragmentListener listener)
    {
        mOnChangeFragmentListener = listener;
    }

    String mSearchWord = "apple";

    public DictionaryFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View resultView = inflater.inflate(R.layout.fragment_dictionary, container, false);

        Button button = (Button)resultView.findViewById(R.id.buttonBack);
        button.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mOnChangeFragmentListener != null)
                        mOnChangeFragmentListener.OnChangeFragment(1);
                }
            }
        );

        mTextviewEnglish = (TextView)resultView.findViewById(R.id.textViewEnglish);
        mTextViewMean = (TextView)resultView.findViewById(R.id.textViewMean);
        mTextviewEnglish.setText(mSearchWord);

        button = (Button)resultView.findViewById(R.id.buttonInsert);
        button.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                AddWord(mTextviewEnglish.getText().toString(), mTextViewMean.getText().toString());
                            }
                        }
                );
        return resultView;
    }

    public void SetEnglishWord(String englishWord)
    {
        mSearchWord = englishWord;
    }

    private void AddWord(String english, String mean)
    {
        Resources res = getResources();

        String[] means = WordDBHelper.getInstance(getActivity().getApplicationContext()).getInsertedMeans(english);
        // 중복되는 단어가 없을 경우
        // means.length <= 0인 경우 중간에 return 되는 것을 가정하고 짠 코드가 있으므로 지울시 주의할 것.
        if (means.length <= 0)
        {
            WordManager.getInstance(getContext()).InsertActiveWord(english, mean);
            Toast.makeText(getContext(), String.format(res.getString(R.string.sentenceInsertWord06), english, mean ), Toast.LENGTH_SHORT).show();
            return;
        }

        Context context = getView().getContext();

        // means.length <= 0인 경우 중간에 return 되는 것을 가정하고 짠 코드
        for(int i=0; i < means.length; i++)
        {
            // 단어와 뜻이 모두 중복되는 단어가 있는 경우
            if (mean.equalsIgnoreCase(means[i]))
            {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(res.getString(R.string.wordError))
                        .setMessage(String.format( res.getString(R.string.sentenceDuplicatedWordAndMean), english, mean))
                        .setNegativeButton(res.getString(android.R.string.ok), null )
                        .show();
                return;
            }
        }

        WordManager.getInstance(getContext()).InsertActiveWord(english, mean);
        Toast.makeText(getContext(), String.format(res.getString(R.string.sentenceInsertWord06), english, mean ), Toast.LENGTH_SHORT).show();
    }


}
