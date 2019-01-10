package hamoney9409.speakingvocabulary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import hamoney9409.speakingvocabulary.dataformat.WORD_DATA;

public class AdminActivity extends AppCompatActivity
{
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button button = (Button)findViewById(R.id.buttonExecute);
        button.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    EditText editText = (EditText)findViewById(R.id.editTextInputCommand);

                    Editable text = editText.getText();
                    execute(text.toString());
                    text.clear();

                }
            }
        );

        mTextView = (TextView)findViewById(R.id.textViewOutput);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
    }

    protected void execute(String message)
    {
        StringBuffer output = new StringBuffer(mTextView.getText().toString());

        if (message.isEmpty())
        {
            return;
        }

        if (message.charAt(0) != '/')
        {
            return;
        }

        //String[] args = message.split(" ");
        //String command = args[0].substring(1); // 0번쨰 인덱스는 '/'일거라고 가정허고 쓴 명령
        int space = message.indexOf(' ');
        String command = message.substring(1, space);
        String arg = (space+1 < message.length()) ? message.substring(space+1) : "";
        String[] args = arg.split(" ");
        switch(command)
        {
            case "echo":
                output.append(arg);
                output.append(System.lineSeparator());
                break;
            case "sql":
                output.append("query:");
                output.append(System.lineSeparator());
                output.append(arg);
                output.append(System.lineSeparator());
                output.append("result:");
                output.append(System.lineSeparator());
                output.append(WordDBHelper.getInstance(this).executeRawQuery(arg));
                output.append(System.lineSeparator());
                break;
            case "activeword":
                if (args.length >= 2)
                {
                    // 단어와 뜻을 둘 다 쓴 경우
                    WordManager.getInstance(this).setWordActive(args[0], args[1]);
                }
                else
                {
                    WORD_DATA[] datas = WordManager.getInstance(this).getActiveWordList();
                    for(WORD_DATA data : datas)
                    {
                        if (data.englishWord.equalsIgnoreCase(args[0]))
                        {
                            WordManager.getInstance(this).setWordActive(data.englishWord, data.mean);
                        }
                    }
                }
                //
                break;
            case "deactiveword":
                if (args.length >= 2)
                {
                    // 단어와 뜻을 둘 다 쓴 경우
                    WordManager.getInstance(this).setWordDeactive(args[0], args[1]);
                }
                else
                {
                    WORD_DATA[] datas = WordManager.getInstance(this).getActiveWordList();
                    for(WORD_DATA data : datas)
                    {
                        if (data.englishWord.equalsIgnoreCase(args[0]))
                        {
                            WordManager.getInstance(this).setWordDeactive(data.englishWord, data.mean);
                        }
                    }
                }
        }

        mTextView.setText(output.toString());
        final int scrollAmount = mTextView.getLayout().getLineTop(mTextView.getLineCount()) - mTextView.getHeight();
        // if there is no need to scroll, scrollAmount will be <=0
        if (scrollAmount > 0)
            mTextView.scrollTo(0, scrollAmount);
    }

}
