package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;

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

    FirebaseRecyclerAdapter<Data,IncomeViewHolder>incomeAdapter = null;

    FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter = null;
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

        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);

        mIncomeDatabase.keepSynced(true);
        mExpenseDatabase.keepSynced(true);
        //Connect Floating Button to layout

        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_button);


        // Connect floating text
        fab_income_text=myview.findViewById(R.id.income_ft_text);
        fab_expense_text=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense

        totalIncomeResult = myview.findViewById(R.id.income_set_result);
        totalExpenseResult = myview.findViewById(R.id.expense_set_result);

        //Recycler

        mRecyclerIncome = myview.findViewById(R.id.recycler_income);
        mRecyclerExpense = myview.findViewById(R.id.recycler_expense);

        //Animations

        FadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData();
                floatingButtonAnimation();
            }
        });

        //Calculate total income

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int total = 0;

                for(DataSnapshot mysnap: snapshot.getChildren()){
                    Data data = mysnap.getValue(Data.class);

                    total += data.getAmount();

                    String stResult = String.valueOf(total);

                    totalIncomeResult.setText(stResult+".00");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int total= 0;

                for(DataSnapshot mysnap: snapshot.getChildren()){

                    Data data = mysnap.getValue(Data.class);

                    total += data.getAmount();

                    String stResult = String.valueOf(total);

                    totalExpenseResult.setText(stResult+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);


        return myview;
    }
    //Floating button animation

    private void floatingButtonAnimation(){
        if(isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);

            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);
            fab_income_text.startAnimation(FadeClose);
            fab_expense_text.startAnimation(FadeClose);
            fab_income_text.setClickable(false);
            fab_expense_text.setClickable(false);
        }
        else{
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);
            fab_expense_text.startAnimation(FadeOpen);
            fab_income_text.startAnimation(FadeOpen);
            fab_income_text.setClickable(true);
            fab_expense_text.setClickable(true);
        }
        isOpen=!isOpen;

    }
    private void addData(){
        //Fab Button Income
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIncomeData();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertExpenseData();
            }
        });
    }

    public void insertIncomeData(){
        AlertDialog.Builder mydialog= new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);
        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount=myview.findViewById(R.id.amount_edt);
        EditText edtType=myview.findViewById(R.id.type_edt);
        EditText edtNote=myview.findViewById(R.id.note_edt);

        Button saveBtn=myview.findViewById(R.id.btnSave);
        Button cancelBtn=myview.findViewById(R.id.btnCancel);


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim();
                String amount=edtamount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Please Enter A Type");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtamount.setError("Please Enter Amount");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Please Enter A Note");
                    return;
                }
                int amountInInt=Integer.parseInt(amount);

                //Create random ID inside database
                String id=mIncomeDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(amountInInt, type, note, id, mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                floatingButtonAnimation();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingButtonAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void insertExpenseData(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount=myview.findViewById(R.id.amount_edt);
        EditText edttype=myview.findViewById(R.id.type_edt);
        EditText edtnote=myview.findViewById(R.id.note_edt);

        Button saveBtn=myview.findViewById(R.id.btnSave);
        Button cancelBtn=myview.findViewById(R.id.btnCancel);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount=edtamount.getText().toString().trim();
                String type=edttype.getText().toString().trim();
                String note=edtnote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edttype.setError("Please Enter A Type");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtamount.setError("Please Enter Amount");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtnote.setError("Please Enter A Note");
                    return;
                }
                int amountInInt= Integer.parseInt(amount);

                //Create random ID inside database
                String id=mExpenseDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(amountInInt, type, note, id, mDate);

                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                floatingButtonAnimation();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                floatingButtonAnimation();
            }
        });
        dialog.show();
    }


    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(
                Data.class,
                R.layout.dashboard_income,
                DashBoardFragment.IncomeViewHolder.class,
                mIncomeDatabase
        ) {
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder incomeViewHolder, int position, @NonNull Data model) {
                incomeViewHolder.setIncomeType(model.getType());
                incomeViewHolder.setIncomeAmount(model.getAmount());
            }

//            @Override
//            protected void populateViewHolder(IncomeViewHolder incomeViewHolder, Data data, int i) {
//                incomeViewHolder.setIncomeType(data.getType());
//                incomeViewHolder.setIncomeAmount(data.getAmount());
////                incomeViewHolder.setIncomeDate(data.getDate());
//
//            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter= new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(
                Data.class,
                R.layout.dashboard_expense,
                DashBoardFragment.ExpenseViewHolder.class,
                mExpenseDatabase
        ) {
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder expenseViewHolder, int position, @NonNull Data model) {
                expenseViewHolder.setExpenseType(model.getType());
                expenseViewHolder.setExpenseAmount(model.getAmount());
            }

//            @Override
//            protected void populateViewHolder(ExpenseViewHolder expenseViewHolder, Data data, int i) {
//                expenseViewHolder.setExpenseType(data.getType());
//                expenseViewHolder.setExpenseAmount(data.getAmount());
////                expenseViewHolder.setExpenseDate(data.getDate());
//            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);
    }

    // For income Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{
        View mIncomeView;
        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }
        public void setIncomeType(String type){
            TextView mtype=mIncomeView.findViewById(R.id.type_Income_ds);
            Log.i("TYPE", type);
            mtype.setText(type);
        }

        public void setIncomeAmount(int amount){
            TextView mAmount=mIncomeView.findViewById(R.id.amount_Income_ds);
            String strAmount=String.valueOf(amount);
            Log.i("AMOUNT", strAmount);
            mAmount.setText(strAmount);
        }

//        public void setIncomeDate(String date){
//            TextView mDate=mIncomeView.findViewById(R.id.date_Income_ds);
//            Log.i("DATE", date);
//            mDate.setText(date);
//        }

    }

    // For expense Data

    public static class ExpenseViewHolder extends  RecyclerView.ViewHolder{

        View mExpenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }
        public void setExpenseType(String type){
            TextView mtype=mExpenseView.findViewById(R.id.type_Expense_ds);
            mtype.setText(type);
        }
        public void setExpenseAmount(int amount){
            TextView mAmount=mExpenseView.findViewById(R.id.amount_Expense_ds);
            String strAmount=String.valueOf(amount);
            mAmount.setText(strAmount);

        }
//        public void setExpenseDate(String date){
//            TextView mDate=mExpenseView.findViewById(R.id.date_Expense_ds);
//            mDate.setText(date);
//        }
    }
}
