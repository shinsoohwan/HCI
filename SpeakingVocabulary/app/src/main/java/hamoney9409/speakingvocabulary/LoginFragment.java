package hamoney9409.speakingvocabulary;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hamoney9409.speakingvocabulary.listener.OnChangeFragmentListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment
{
    OnChangeFragmentListener mOnChangeFragmentListener = null;

    public void SetChangeFragmentListener(OnChangeFragmentListener listener)
    {
        mOnChangeFragmentListener = listener;
    }


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {



        View resultView = inflater.inflate(R.layout.fragment_login, container, false);;

        Button buttonSignUp = (Button)resultView.findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (mOnChangeFragmentListener != null)
                        mOnChangeFragmentListener.OnChangeFragment(3);
                }
            }
        );

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

        return resultView;
    }

}
