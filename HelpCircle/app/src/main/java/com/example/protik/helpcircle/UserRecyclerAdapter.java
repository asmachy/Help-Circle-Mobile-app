package com.example.protik.helpcircle;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> implements Filterable {

    public List<User> user_list;
    public List<User> user_list_full;

    public UserRecyclerAdapter(List<User> user_list) {
        this.user_list = user_list;
        //user_list_full = new ArrayList<>(user_list);
        this.user_list_full = user_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String nameData = user_list.get(position).getName();
        String phoneData = user_list.get(position).getPhone();
        String emailData = user_list.get(position).getEmail();
        String userIdData = user_list.get(position).getUser_id();

        holder.setText(nameData, phoneData, emailData, userIdData);
    }

    @Override
    public int getItemCount() {
        return user_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView nameView, phoneView, emailView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setText(String nameTxt, String phoneTxt, String emailTxt, String userIdTxt) {

            nameView = mView.findViewById(R.id.nameId);
            phoneView = mView.findViewById(R.id.phoneId);
            emailView = mView.findViewById(R.id.emailId);

            nameView.setText(nameTxt);
            phoneView.setText(phoneTxt);
            emailView.setText(emailTxt);
            final String userId = userIdTxt;

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mView.getContext(), "Id: "+ userId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    intent.putExtra("user_id", userId);
                    v.getContext().startActivity(intent);
                }
            });

        }
    }


    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();

            List<User> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(user_list_full);

            } else {

                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (User item : user_list_full) {

//                    Log.i("taga", item.getName());
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

//            user_list.clear();
//            user_list.addAll((List) filterResults.values);
            user_list = (List<User>) filterResults.values;
            notifyDataSetChanged();
        }
    };
}
