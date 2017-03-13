package info.scelus.dotsandboxes.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import info.blackbear.scelus.dotsandboxes.R;

public class MainMenuFragment extends Fragment {
    public static final int FRAGMENT_ID = 4367;
    private OnFragmentInteractionListener mListener;

    public static MainMenuFragment newInstance(Bundle args) {
        MainMenuFragment fragment = new MainMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MainMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_main, container, false);
        Button localPlayButton = (Button) root.findViewById(R.id.buttonPlayLocal);
        Button networkPlayButton = (Button) root.findViewById(R.id.buttonPlayNetwork);

        localPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMainMenuFragmentInteraction(LocalMenuFragment.FRAGMENT_ID, new Bundle());
                }
            }
        });

        networkPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMainMenuFragmentInteraction(ComingSoonFragment.FRAGMENT_ID, new Bundle());
                }
            }
        });

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onMainMenuFragmentInteraction(int fragmentId, Bundle args);
    }
}
