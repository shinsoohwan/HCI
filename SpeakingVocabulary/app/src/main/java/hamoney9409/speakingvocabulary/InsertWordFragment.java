package hamoney9409.speakingvocabulary;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import hamoney9409.util.FileSystem;


public class InsertWordFragment extends Fragment
{
    private class ActivityRequestCode
    {
        private static final int TEXT_FILE_CHOOSER = 1; // onActivityResult request
    }

    private class PermissionRequestCode
    {
        private static final int READ_EXTERNAL_STORAGE = 1; // onActivityResult request
    }

    public InsertWordFragment()
    {
        // Required empty public constructor
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    EditText m_editTextEnglish;
    EditText m_editTextForeign;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InsertWordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InsertWordFragment newInstance(String param1, String param2) {
        InsertWordFragment fragment = new InsertWordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        final View resultView = inflater.inflate(R.layout.fragment_insert_word, container, false);

        m_editTextEnglish = (EditText)resultView.findViewById(R.id.editTextEnglish);
        m_editTextForeign = (EditText)resultView.findViewById(R.id.editTextForeign);

        Button button = (Button)resultView.findViewById(R.id.buttonPlay);
        button.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String englishText = m_editTextEnglish.getText().toString();
                    String foreignText = m_editTextForeign.getText().toString();

                    TTSManager.getInstance(getActivity().getApplicationContext()).SpeakWord(englishText, foreignText);
                }
            }
        );

        button = (Button)resultView.findViewById(R.id.buttonInsert);
        button.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    AddWord(m_editTextEnglish.getText().toString(), m_editTextForeign.getText().toString());
                }
            }
        );

        button = (Button)resultView.findViewById(R.id.buttonLoadFile);
        button.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = Intent.createChooser
                    (
                            new Intent(Intent.ACTION_GET_CONTENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE),
                            "title"
                    );
                    try
                    {
                        startActivityForResult(intent, ActivityRequestCode.TEXT_FILE_CHOOSER);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        // The reason for the existence of aFileChooser
                    }
                }
            }
        );

        button = (Button)resultView.findViewById(R.id.buttonRemove);
        button.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    RemoveWord(m_editTextEnglish.getText().toString(), m_editTextForeign.getText().toString());
                }
            }
        );

        // Inflate the layout for this fragment
        return resultView;
    }

    private void RemoveWord(String english, String mean)
    {
        Resources res = getResources();
        final String english2 = english;
        final String mean2 = mean;
        if (english.length() <= 0)
        {
            m_editTextEnglish.requestFocus();
            Toast.makeText(getContext(), R.string.sentenceInsertWord01, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mean.length() <= 0)
        {
            m_editTextForeign.requestFocus();
            Toast.makeText(getContext(), R.string.sentenceInsertWord02, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] means = WordDBHelper.getInstance(getActivity().getApplicationContext()).getInsertedMeans(english);
        // 중복되는 단어가 없을 경우
        if (means.length <= 0)
        {
            Toast.makeText(getContext(), R.string.sentenceInsertWord05, Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i=0; i < means.length; i++)
        {
            // 단어와 뜻이 모두 중복되는 단어가 있는 경우
            if (mean.equalsIgnoreCase(means[i]))
            {
                new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(res.getString(R.string.wordWarning))
                .setMessage(String.format( res.getString(R.string.sentenceAskRemoveWord), english2, mean))
                .setPositiveButton
                (
                    res.getString(android.R.string.yes),
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which )
                        {
                            WordManager.getInstance(getContext()).RemoveActiveWord(english2, mean2);
                            Toast.makeText(getContext(), R.string.sentenceInsertWord04, Toast.LENGTH_SHORT).show();
                        }
                    }
                )
                .setNegativeButton(res.getString(android.R.string.no), null )
                .show();

                return;
            }
        }

        Toast.makeText(getContext(), R.string.sentenceInsertWord05, Toast.LENGTH_SHORT).show();
    }

    private void AddWord(String english, String mean)
    {
        if (english.length() <= 0)
        {
            m_editTextEnglish.requestFocus();
            Toast.makeText(getContext(), R.string.sentenceInsertWord01, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mean.length() <= 0)
        {
            m_editTextForeign.requestFocus();
            Toast.makeText(getContext(), R.string.sentenceInsertWord02, Toast.LENGTH_SHORT).show();
            return;
        }

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

        StringBuffer sb = new StringBuffer(means[0].length() + 3);
        sb.append("\'");
        sb.append(means[0]);
        sb.append("\'");
        for(int i=1; i<means.length; i++)
        {
            sb.append(", \'");
            sb.append(means[i]);
            sb.append("\'");
        }

        new AlertDialog.Builder(context)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(res.getString(R.string.wordWarning))
        .setMessage(String.format( res.getString(R.string.sentenceAlreadyExistMean), english, sb.toString(), mean))
        .setPositiveButton
        (
            res.getString(android.R.string.yes), //승인버튼을 눌렀을때..
            new AlreadyExistMeanListener(english, mean)
        )
        .setNegativeButton(res.getString(android.R.string.no), null )
        .show(); //취소버튼을 눌렀을때..
    }

    private class AlreadyExistMeanListener implements DialogInterface.OnClickListener
    {
        String englishText;
        String meanText;
        public AlreadyExistMeanListener(String englishText, String meanText)
        {
            this.englishText = englishText;
            this.meanText = meanText;
        }

        @Override
        public void onClick( DialogInterface dialog, int which)
        {
            WordManager.getInstance(getContext()).InsertActiveWord(englishText, meanText);
            Toast.makeText(getContext(), R.string.sentenceInsertWord03, Toast.LENGTH_SHORT).show();
        }
    }

    private void GetWordListFromFile(Intent data)
    {
        // Get the URI of the selected file
        final Uri uri = data.getData();

        try
        {
            InputStreamReader inputStreamReader = FileSystem.getTextFileReader(getContext(), uri, Locale.getDefault());
            //InputStreamReader inputStreamReader = FileSystem.getTextFileReader(path, Locale.getDefault());

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            for(String text = bufferedReader.readLine(); text != null; text = bufferedReader.readLine())
            {
                int foreignStart = hamoney9409.util.Locale.indexOfForeign(text);
                if (foreignStart == -1)
                {
                    // 뜻 불러오기 실패
                    continue;
                }

                String meanText = text.substring(foreignStart);
                int englishEnd = hamoney9409.util.Locale.lastIndexOfEnglish(text, foreignStart);

                if (englishEnd == -1)
                {
                    continue;
                }

                String englishText = text.substring(0, englishEnd+1);;

                AddWord(englishText, meanText);

                //button.setText(encoding + ", " + text);
            }
        } // try
        catch (FileNotFoundException e)
        {
            if (e.getMessage().contains("(Permission denied"))
            {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                }
                else
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionRequestCode.READ_EXTERNAL_STORAGE);
                }
            }
            else
            {
                Log.e("file", Log.getStackTraceString(e));
            }
        } // catch (FileNotFoundException e)
        catch(IOException e)
        {
            Log.e("file", Log.getStackTraceString(e));
        } // catch(IOException e)

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case ActivityRequestCode.TEXT_FILE_CHOOSER:
                // If the file selection was successful
                if (resultCode != Activity.RESULT_OK)
                {
                    break;
                }

                if (data == null)
                {
                    break;
                }

                GetWordListFromFile(data);

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch(requestCode)
        {
            case PermissionRequestCode.READ_EXTERNAL_STORAGE:
                for (int i = 0; i < permissions.length; i++)
                {
                    final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                    if (permissions[i].equals(permission))
                    {
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        {
                        }
                        else
                        {
                        }
                    }
                }
                break;

            default:
                break;
        }
    }
}
