package com.touchawesome.dotsandboxes.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.touchawesome.dotsandboxes.App;
import com.touchawesome.dotsandboxes.R;
import com.touchawesome.dotsandboxes.db.GameScore;

import java.util.List;

/**
 * Created by scelus on 04.05.17
 */

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView list = (RecyclerView) findViewById(R.id.recycler_view);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        list.setAdapter(new HistoryAdapter());

    }

    private class HistoryAdapter extends RecyclerView.Adapter<GameScoreViewHolder> {
        List<GameScore> gameScoreEntries;

        HistoryAdapter() {
            gameScoreEntries = ((App) getApplication()).getDaoSession().getGameScoreDao().loadAll();
        }

        @Override
        public GameScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GameScoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_game_score, parent, false));
        }

        @Override
        public void onBindViewHolder(GameScoreViewHolder holder, int position) {
            GameScore entry = gameScoreEntries.get(position);

            holder.data.setText(entry.getDate().toString());
            holder.opponent.setText(entry.getOpponent());
            holder.mode.setText(entry.getMode());
            holder.score.setText(entry.getScore());
        }

        @Override
        public int getItemCount() {
            return gameScoreEntries.size();
        }
    }

    private class GameScoreViewHolder extends RecyclerView.ViewHolder {

        private TextView data;
        private TextView opponent;
        private TextView score;
        private TextView mode;

        public GameScoreViewHolder(View itemView) {
            super(itemView);

            data = (TextView) itemView.findViewById(R.id.history_data_label);
            opponent = (TextView) itemView.findViewById(R.id.history_opponent_label);
            score = (TextView) itemView.findViewById(R.id.history_score_label);
            mode = (TextView) itemView.findViewById(R.id.history_mode_label);
        }
    }
}
