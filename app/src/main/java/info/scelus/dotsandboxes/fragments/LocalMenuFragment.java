package info.scelus.dotsandboxes.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.external.Game;
import info.scelus.dotsandboxes.utils.Globals;

public class LocalMenuFragment extends Fragment {
    public static final int FRAGMENT_ID = 5267;
    private OnFragmentInteractionListener mListener;

    private TextView mainTitle;
    private Button player2PlayButton;
    private Button cpuPlayButton;

    public static LocalMenuFragment newInstance(Bundle args) {
        LocalMenuFragment fragment = new LocalMenuFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LocalMenuFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFonts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_local, container, false);
        mainTitle = (TextView) root.findViewById(R.id.mainLocalTitleText);
        player2PlayButton = (Button) root.findViewById(R.id.buttonPlayLocalPlayer);
        cpuPlayButton = (Button) root.findViewById(R.id.buttonPlayLocalComputer);

        player2PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    Bundle args = new Bundle();
                    args.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.PLAYER);
                    mListener.onLocalMenuFragmentInteraction(GameLocalFragment.FRAGMENT_ID, args);
                }
            }
        });

        cpuPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    Bundle args = new Bundle();
                    args.putSerializable(GameLocalFragment.ARG_MODE, Game.Mode.CPU);
                    mListener.onLocalMenuFragmentInteraction(ComingSoonFragment.FRAGMENT_ID, args);
                }
            }
        });

        return root;
    }

    private void setFonts () {
        mainTitle.setTypeface(Globals.kgTrueColors);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onLocalMenuFragmentInteraction(int fragmentId, Bundle args);
    }
}
