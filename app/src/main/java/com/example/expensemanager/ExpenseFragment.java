package com.example.expensemanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

import Model.Data;


public class ExpenseFragment extends Fragment {
    //Firebase Database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Recycler View
    private RecyclerView recyclerView;

    //textview
    private TextView expenseTotalSum;
    //Update edit text

    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    //Button for update and delete

    private Button btnUpdate;
    private Button btnDelete;

    //data item value
    private String type;
    private String note;
    private int amount;
    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_expense, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("EXPENSEDATABASE").child(uid);
        expenseTotalSum=myview.findViewById(R.id.expense_txt_result);
        recyclerView = myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalValue = 0;
                for (DataSnapshot mysnapshot:dataSnapshot.getChildren()){
                    Data data=mysnapshot.getValue(Data.class);
                    totalValue+=data.getAmount();
                    String stTotalValue=String.valueOf(totalValue);
                    expenseTotalSum.setText(stTotalValue+".00");


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
      //firebase
        FirebaseRecyclerAdapter<Data,MyViewHolder> adapter = null;
        
        adapter=new FirebaseRecyclerAdapter<Data, MyViewHolder>( Data.class,
                R.layout.expense_recycler_data,
                MyViewHolder.class,
                mExpenseDatabase) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmmount(model.getAmount());
                
                holder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key=  getRef(position).getKey();
                        type= model.getType();
                        note = model.getNote()  ;
                        amount= model.getAmount();

                        updateDataItem();
                    }
                });
                recyclerView.setAdapter(adapter);
            }
            
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };
        
            }


    private static class MyViewHolder extends RecyclerView.ViewHolder {

        View myview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
        }

        private void setType(String type) {
            TextView mType = myview.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note) {
            TextView mNote = myview.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }
        private void setDate(String date){
            TextView mDate=myview.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }
        private void setAmmount (int ammount){
            TextView mAAmmount=myview.findViewById(R.id.ammount_txt_expense);
            String strammount = String.valueOf(ammount);
            mAAmmount.setText(strammount);
        }
    }

    private void updateDataItem() {
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.updat_data_item, null);

        mydialog.setView(myview);

        //set data to edit text
        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        edtAmount=myview.findViewById(R.id.amount_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        edtType=myview.findViewById(R.id.type_edt);


        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnuPD_Delete);
        AlertDialog dialog = mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=edtType.getText().toString().trim();
                note=edtNote.getText().toString().trim();

                String mdamount =String.valueOf(amount);

                mdamount=edtAmount.getText().toString().trim();

                int myAmount= Integer.parseInt(mdamount);

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(myAmount,type,note,post_key,mDate);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    }