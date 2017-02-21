package info.scelus.dotsandboxes.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.blackbear.scelus.dotsandboxes.R;
import info.scelus.dotsandboxes.utils.Globals;

public class ComingSoonFragment extends Fragment {
    public static final int FRAGMENT_ID = 2346;

    private TextView mainTitle;

    public static ComingSoonFragment newInstance(Bundle args) {
        ComingSoonFragment fragment = new ComingSoonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ComingSoonFragment() {
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
        View root = inflater.inflate(R.layout.fragment_coming_soon, container, false);
        mainTitle = (TextView) root.findViewById(R.id.comingSoonText);
        return root;
    }

    private void setFonts () {
        mainTitle.setTypeface(Globals.kgTrueColors);
    }
}
