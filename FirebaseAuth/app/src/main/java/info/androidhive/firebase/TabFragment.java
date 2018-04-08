package info.androidhive.firebase;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {
    private Firebase mRootRef;
    private Firebase RefUid,RefTran;
    int pos;
    private String tagId;
    private TextView textView;

    private List<Transaction> TransactionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransAdapter mAdapter1;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos1", position);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }
    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab, container, false);


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
        RefTran = RefUid.child("Transactions");

        Calendar calendar = Calendar.getInstance();
        int currentDay = (calendar.get(Calendar.DAY_OF_MONTH));
        int currentMonth = (calendar.get(Calendar.MONTH)+1);
        int currentYear = (calendar.get(Calendar.YEAR));

        Toast.makeText(getContext(),currentMonth+"/"+currentYear,Toast.LENGTH_SHORT).show();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter1 = new TransAdapter(TransactionList);
        recyclerView.setAdapter(mAdapter1);
        prepareTransactionData();


        registerForContextMenu(recyclerView);


        mAdapter1.setOnItemClickListener(new TransAdapter.ClickListener() {
            @Override
            public void OnItemClick(int position, View v) {
                Toast.makeText(getActivity(),TransactionList.get(position).getTid(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void OnItemLongClick(int position, View v) {
                Log.i("yoyoyo","Here: "+position);
                pos=position;
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case 11:{
                int show = item.getGroupId();
                tagId=TransactionList.get(show).getTid();
                Toast.makeText(getActivity(),tagId+"-"+"Delete it",Toast.LENGTH_SHORT).show();

            }break;

            case 12:{
                int show = item.getGroupId();
                tagId=TransactionList.get(show).getTid();
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
                mAdapter1.notifyDataSetChanged();
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
