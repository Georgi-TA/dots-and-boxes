package info.scelus.dotsandboxes.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.external.Board;
import info.scelus.dotsandboxes.external.Game;
import info.scelus.dotsandboxes.views.BoardView;
import info.scelus.dotsandboxes.views.ScoreView;

public class GameLocalFragment extends Fragment {
    public static final String ARG_MODE = "bg.blackbear.scelus.args.mode";
    public static final int FRAGMENT_ID = 6164;

    private Game.Mode mode;
    private Board board;
    private BoardView view;
    private ScoreView score;
    private OnFragmentInteractionListener mListener;

    public static GameLocalFragment newInstance(Bundle args) {
        GameLocalFragment fragment = new GameLocalFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public GameLocalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_MODE))
                mode = (Game.Mode) getArguments().getSerializable(ARG_MODE);
            else
                mode = Game.Mode.PLAYER;
        }
        else {
            mode = Game.Mode.PLAYER;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_game_local, container, false);
        view = (BoardView) root.findViewById(R.id.boardView);
        RelativeLayout scoreLayout = (RelativeLayout) root.findViewById(R.id.scoreLayout);
        board = new Board(6, 6);
        view.setBoard(board);
        score = (ScoreView) LayoutInflater.from(getActivity()).inflate(R.layout.score_layout, scoreLayout, false);
        board.setGameListener(score);
        scoreLayout.addView(score);
        return root;
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
        public void onGameLocalFragmentInteraction(Uri uri);
    }

}
