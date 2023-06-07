package com.example.expensemanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.Data;


public class IncomeFragment extends Fragment {

//Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;

    //Recycler View
    private RecyclerView recyclerView;

    //textview
    private TextView incomTotalSum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_income, container, false);
        mAuth =FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("INCOMEDATA").child(uid);

        incomTotalSum=myview.findViewById(R.id.income_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalValue = 0;
                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){
                    Data data=mysnapshot.getValue(Data.class);
                    totalValue+=data.getAmount();
                    String stTotalValue=String.valueOf(totalValue);
                    incomTotalSum.setText(stTotalValue);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter;
        adapter = new FirebaseRecyclerAdapter<Data,MyViewHolder>(){

            {
                Data.class,
                R.layout.income_recycler_data;
                MyViewHolder.class;
                mIncomeDatabase,
            }
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder,Data model,int position){
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmmount(model.getAmount());

            }
        };
        recyclerView.setAdapter(adapter);
    }
    private static class MyViewHolder extends RecyclerView.ViewHolder{

        View myview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        private void setType(String type){
            TextView mType = myview.findViewById(R.id.type_txt_income);
        }
        private void setNote(String note){
            TextView mNote=myview.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        private void setDate(String date){
            TextView mDate=myview.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }
        private void setAmmount (int ammount){
            TextView mAmmount=myview.findViewById(R.id.ammount_txt_income);
            String stammount = String.valueOf(ammount);
            mAmmount.setText(stammount);
        }

    }

}