package hamoney9409.speakingvocabulary;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hamoney9409.speakingvocabulary.R;
import hamoney9409.speakingvocabulary.listener.OnChangeFragmentListener;

public class SignUpFragment extends Fragment {

    OnChangeFragmentListener mOnChangeFragmentListener = null;

    public void SetChangeFragmentListener(OnChangeFragmentListener listener)
    {
        mOnChangeFragmentListener = listener;
    }

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View resultView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Button buttonSignUp = (Button)resultView.findViewById(R.id.buttonSignUp);
        buttonSignUp.setOnClickListener
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
