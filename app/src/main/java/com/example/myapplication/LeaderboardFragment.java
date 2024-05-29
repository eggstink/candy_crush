package com.example.myapplication;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class LeaderboardFragment extends Fragment {
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        loadLeaderboardData();

        return view;
    }

    private void loadLeaderboardData() {
        firestore.collection("users")
                .orderBy("highestScore", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> userList = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            String username = doc.getString("username");
                            long highestScore = doc.getLong("highestScore");
                            userList.add(new User(username, highestScore));
                        }
                        adapter = new LeaderboardAdapter(userList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "Failed to load leaderboard", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
        private final List<User> userList;

        public LeaderboardAdapter(List<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = userList.get(position);
            holder.username.setText(user.getUsername());
            holder.highestScore.setText(String.valueOf(user.getHighestScore()));
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView username, highestScore;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                username = itemView.findViewById(R.id.username);
                highestScore = itemView.findViewById(R.id.highestScore);
            }
        }
    }

    public class LeaderboardDialogFragment extends DialogFragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
            Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.gravity = Gravity.CENTER;
                window.setAttributes(params);
            }
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Initialize views and set up any event listeners
        }
    }


    private static class User {
        private final String username;
        private final long highestScore;

        public User(String username, long highestScore) {
            this.username = username;
            this.highestScore = highestScore;
        }

        public String getUsername() {
            return username;
        }

        public long getHighestScore() {
            return highestScore;
        }
    }
}

