package hamoney9409.speakingvocabulary;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import hamoney9409.speakingvocabulary.listener.OnChangeFragmentListener;

public class MainActivity extends AppCompatActivity implements OnChangeFragmentListener
{
    MainActivity self = this;

    WordListFragment mWordListFragment = new WordListFragment();
    //InsertWordFragment mInsertWordFragment = new InsertWordFragment(); // 폐기
    DictionaryFragment mDictionaryFragment = new DictionaryFragment();
    LoginFragment mLoginFragment = new LoginFragment();
    SignUpFragment mSignUpFragment = new SignUpFragment();
    EditText mEditTextSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TTSManager.getInstance(getApplicationContext()); // 미리 초기화

        mEditTextSearch = (EditText)findViewById(R.id.editTextSearch);
        Button buttonEnter = (Button)findViewById(R.id.buttonEnter);
        buttonEnter.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction()
                        .replace( R.id.fragment_frame_layout, mDictionaryFragment)
                        .addToBackStack(null)
                        .commit();

                    mDictionaryFragment.SetEnglishWord(mEditTextSearch.getText().toString());

                }
            }
        );

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace( R.id.fragment_frame_layout, mWordListFragment )
                .addToBackStack(null)
                .commit();

        mWordListFragment.SetChangeFragmentListener(this);
        mDictionaryFragment.SetChangeFragmentListener(this);
        mLoginFragment.SetChangeFragmentListener(this);
        mSignUpFragment.SetChangeFragmentListener(this);
    }

    private int tryQuitCount = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            if (BuildConfig.DEBUG)
            {
                tryQuitCount ++;
                if (5 < tryQuitCount)
                {
                    tryQuitCount = 0;
                    startActivity(new Intent(MainActivity.this, AdminActivity.class));
                    return true;
                }
            }

            new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(this.getString(R.string.wordWarning)) // 제목부분 텍스트
                .setMessage(this.getString(R.string.sentenceAskQuit)) // 내용부분 텍스트
                .setPositiveButton
                (
                    this.getString(android.R.string.yes), //승인버튼을 눌렀을때..
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which )
                        {
                            self.finishAffinity(); //종료
                            System.exit(0);
                        }
                    }
                ).setNegativeButton(this.getString(android.R.string.no), null ).show(); //취소버튼을 눌렀을때..

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void OnChangeFragment(int fragment)
    {
        Fragment to;
        switch(fragment)
        {
            case 0:
                to = mDictionaryFragment;
                break;
            case 1:
                to = mWordListFragment;
                break;
            case 2:
                to = mLoginFragment;
                break;
            case 3:
                to = mSignUpFragment;
                break;
            default:
                to = mWordListFragment;
                break;
        }

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace( R.id.fragment_frame_layout, to )
                .addToBackStack(null)
                .commit();
    }
}
