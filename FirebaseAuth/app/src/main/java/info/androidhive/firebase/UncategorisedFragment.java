package info.androidhive.firebase;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UncategorisedFragment extends Fragment {

    private String tagId;
    private Firebase mRootRef;
    private Firebase RefUid,RefTran;
    int pos;
    private TextView textView;

    private Context context;
    private List<Transaction> TransactionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransactionAdapter mAdapter;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }
    public UncategorisedFragment() {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_uncategorised, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Toast.makeText(view.getContext(),"position: "+position,Toast.LENGTH_SHORT).show();


        mRootRef=new Firebase("https://expense-2a69a.firebaseio.com/");

        mRootRef.keepSynced(true);
        com.google.firebase.auth.FirebaseAuth auth = FirebaseAuth.getInstance();
        String Uid=auth.getUid();
        RefUid= mRootRef.child(Uid);
        RefTran = RefUid.child("UnCatTran");



        recyclerView = (RecyclerView) view.findViewById(R.id.rv_uncat);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new TransactionAdapter(TransactionList);
        recyclerView.setAdapter(mAdapter);
        prepareTransactionData();
        registerForContextMenu(recyclerView);


        mAdapter.setOnItemClickListener(new TransactionAdapter.ClickListener() {
            @Override
            public void OnItemClick(int position, View v) {
                //Toast.makeText(getActivity(),TransactionList.get(position).getTid(),Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getActivity(),SMSTransShow.class);
                i.putExtra("indexPos",TransactionList.get(position).getTid());
                startActivity(i);
            }

            @Override
            public void OnItemLongClick(int position, View v) {
                Log.i("yoyoyo","yoyoyooyoyoyoyo");
                pos=position;
            }
        });




    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //int index = info.position;
        //View view=info.targetView;

        switch(item.getItemId())
        {
            case 1:{
                tagId=TransactionList.get(pos).getTid();
                Toast.makeText(getActivity(),tagId+"-"+"Delete it",Toast.LENGTH_SHORT).show();

            }break;

            case 2:{
                tagId=TransactionList.get(pos).getTid();
                Toast.makeText(getActivity(),tagId+"-"+"Change it",Toast.LENGTH_SHORT).show();

            }break;
        }
        return super.onContextItemSelected(item);
    }


    private void prepareTransactionData() {
        RefTran.addChildEventListener(new ChildEventListener() {
            String amount,cat,shname,shDay,shMonth,shYear;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int i=0;

                String tid = dataSnapshot.getKey().toString().trim();
                for (DataSnapshot S:dataSnapshot.getChildren()) {
                    //String t_id=S.getValue().toString().trim();
                    //Toast.makeText(getApplicationContext(),"->"+i,Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),t_id,Toast.LENGTH_SHORT).show();
                    switch(i)
                    {
                        case 0:
                            amount=S.getValue().toString().trim();
                            break;
                        case 1:
                            cat=S.getValue().toString().trim();
                            break;
                        case 2:
                            shDay=S.getValue().toString().trim();
                            break;
                        case 3:
                            shMonth=S.getValue().toString().trim();
                            break;
                        case 4:
                            shname=S.getValue().toString().trim();
                            break;
                        case 5:
                            shYear=S.getValue().toString().trim();
                            break;
                    }
                    //Transaction transaction=S.getValue(Transaction.class);
                    //transList.add(transaction);
                    i++;
                }
                String shdate= shDay+" - "+shMonth+" - "+shYear;
                Transaction transaction=new Transaction(tid,amount,cat,shname,shdate);
                //Toast.makeText(getApplicationContext(),transaction.getT_amt(),Toast.LENGTH_SHORT).show();
                TransactionList.add(transaction);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

}



