package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

import Model.Data;


public class DashBoardFragment extends Fragment {

//Floating button
    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textview
    private TextView fab_income_text;
    private TextView fab_expense_text;

    //boolean
    private boolean isOpen=false;

    //animation
    private Animation FadeOpen, FadeClose;

    //Firebase..
    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private  DatabaseReference mExpenseDatabase;
    //Dashboard income and expense result...
    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Recycler view
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("INCOMEDATA").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("EXPENSEDATABASE").child(uid);


        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);
        //Connect floating button to layout

        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_button);

        //Connect float text

        fab_income_text=myview.findViewById(R.id.income_ft_text);
        fab_expense_text=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result set..
        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);

        //Recycler
        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);

        //Animation connect..
        FadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                if (isOpen){
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);

                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_text.startAnimation(FadeClose);
                    fab_expense_text.startAnimation(FadeClose);
                    fab_income_text.setClickable(false);
                    fab_expense_text.setClickable(false);
                    isOpen=false;
                }else{
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_text.startAnimation(FadeOpen);
                    fab_expense_text.startAnimation(FadeOpen);
                    fab_income_text.setClickable(true);
                    fab_expense_text.setClickable(true);
                    isOpen=true;
                }
            }
        });
            //Calculate total income..
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum = 0;
                for (DataSnapshot mysnap:snapshot.getChildren()){
                    Data data=mysnap.getValue(Data.class);
                    totalsum+=data.getAmount();

                    String stResult=String.valueOf(totalsum);
                    totalIncomeResult.setText(stResult+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//Calculate total expense..
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalsum = 0;
                for (DataSnapshot mysnapshot:snapshot.getChildren()){
                    Data data=mysnapshot.getValue(Data.class);
                    totalsum+=data.getAmount();

                    String strTotalSum=String.valueOf(totalsum);
                    totalExpenseResult.setText(strTotalSum+".00");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler
        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);


        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

            return myview;
    }
//Floating button animation
    private void ftAnimation(){
        if (isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);

            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_text.startAnimation(FadeClose);
            fab_expense_text.startAnimation(FadeClose);
            fab_income_text.setClickable(false);
            fab_expense_text.setClickable(false);
            isOpen=false;
        }else{
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_text.startAnimation(FadeOpen);
            fab_expense_text.startAnimation(FadeOpen);
            fab_income_text.setClickable(true);
            fab_expense_text.setClickable(true);
            isOpen=true;
        }
    }

    private void addData(){
        //Fab button income
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeDataInsert();
            }
        });
        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });
    }
    public void incomeDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);
        AlertDialog dialog = mydialog.create();

        dialog.setCancelable(false);

        EditText edtAmmount = myview.findViewById(R.id.amount_edt);
        EditText edtType = myview.findViewById(R.id.type_edt);
        EditText edtNote = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = edtType.getText().toString().trim();
                String ammount = edtAmmount.getText().toString().trim();
                String note = edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)){
                    edtType.setError("Required field..");
                    return;
                }
                if (TextUtils.isEmpty(ammount)){
                    edtAmmount.setError("Required field..");
                    return;
                }
                int ourammountint = Integer.parseInt(ammount);

                if (TextUtils.isEmpty(note)){
                    edtNote.setError("Required field..");
                    return;
                }
                String id = mIncomeDatabase.push().getKey();

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(ourammountint,type,note,mDate,id);

                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"DATA ADDED",Toast.LENGTH_SHORT).show();

                ftAnimation();

                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
            dialog.show();

    }

    public void expenseDataInsert(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= LayoutInflater.from(getActivity());

        View myview = inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        final AlertDialog dialog =mydialog.create();

        dialog.setCancelable(false);

        EditText amount = myview.findViewById(R.id.amount_edt);
        EditText type = myview.findViewById(R.id.type_edt);
        EditText note = myview.findViewById(R.id.note_edt);

        Button btnSave = myview.findViewById(R.id.btnSave);
        Button btnCancel = myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmType = type.getText().toString().trim();
                String tmAmmount = amount.getText().toString().trim();
                String tmNote = note.getText().toString().trim();


                if (TextUtils.isEmpty(tmType)){
                    type.setError("Required field..");
                    return;
                }
                if (TextUtils.isEmpty(tmAmmount)){
                    amount.setError("Required field..");
                    return;
                }
                int inammount = Integer.parseInt(tmAmmount);

                if (TextUtils.isEmpty(tmNote)){
                    note.setError("Required field..");
                    return;
                }

                String id = mExpenseDatabase.push().getKey();
                String mdate= DateFormat.getDateInstance().format(new Date());

                Data  data = new Data(inammount,tmType,tmNote,id,mdate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"DATA ADDED",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();



    }

    @Override
    public void onStart() {
        super.onStart();
//3rd firebase word incomeAdapter
        FirbaseRecylerAdapter<Data,IncomeViewHolder>incomeAdapter=new FirebaseRecylcerAdapter<Data,IncomeViewHolder>(
                Data.class,
                R.layout.dashboard_income,
                DashBoardFragment.IncomeViewHolder.class,
                mIncomeDatabase
        ){
            @Override
            protected void populateViewHolder(IncomeViewHolder viewHolder,Data model,int position){
                viewHolder.setmIncomeType(model.getType());
                viewHolder.setmIncomeAmount(model.getAmount());
                viewHolder.setmIncomeDate(model.getDate());
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);
        FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecylcerAdapter<Data,ExpenseViewHolder>(
                Data.class,
                R.layout.dashboard_expense,
                DashBoardFragment.IncomeViewHolder.class,
                mExpenseDatabase
        ){
            @Override
            protected void populateViewHolder(ExpenseViewHolder viewHolder,Data model,int position){
                viewHolder.setExpenseDate(model.getDate());
                viewHolder.setmExpeneType(model.getType());
                viewHolder.setmExpenseAmount(model.getAmount());
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);



    }

    //For income Data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mIncomeView;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public void setmIncomeType(String type){
            TextView mType =mIncomeView.findViewById(R.id.type_Income_ds);
            mType.setText(type);
        }

        public void setmIncomeAmount(int amount){
            TextView mAmount =mIncomeView.findViewById(R.id.amount_Income_ds);
            String strAmount =String.valueOf(amount);
            mAmount.setText(strAmount);
        }

        public void setmIncomeDate(String date){
            TextView mDate = mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }


    }

    //For expense data
   public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mExpenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }
        public void setmExpeneType(String type){
            TextView mType =mExpenseView.findViewById(R.id.type_Expense_ds);
            mType.setText(type);
        }

        public void setmExpenseAmount(int amount){
            TextView mAmount =mExpenseView.findViewById(R.id.amount_Expense_ds);
            String strAmount =String.valueOf(amount);
            mAmount.setText(strAmount);
        }

        public void setExpenseDate(String date){
            TextView mDate = mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }

    }


}