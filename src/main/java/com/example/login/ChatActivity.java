package com.example.login;

import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertController;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.vision.text.Line;
//import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseSmartReply;
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestion;
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult;
//import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private String myName;
    private FirebaseStorage firebaseStorage;
    private RecyclerView mRecyclerView;
    private LinearLayout mSuggestionParent;
    private EditText mMessageEt;
    private Button mSend;
    private FirebaseRecyclerOptions options;
    private List<FirebaseTextMessage> messageList = new ArrayList<>();


    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mSuggestionParent = (LinearLayout)findViewById(R.id.suggestionParent);
        mMessageEt = (EditText)findViewById(R.id.message);
        mSend = (Button)findViewById(R.id.send);

         databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                myName = userProfile.getUserName();
              }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ChatActivity.this,databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEt.getText().toString();
                MessagePojo pojo = new MessagePojo();
                pojo.setName(myName);
                pojo.setMessage(message);
                pojo.setTimestamp(System.currentTimeMillis());
                databaseReference.push().setValue(pojo);
                mMessageEt.setText("");
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        options = new FirebaseRecyclerOptions.Builder<MessagePojo>().setQuery(databaseReference.orderByChild("timestamp"),MessagePojo.class).build();

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessagePojo pojo = dataSnapshot.getValue(MessagePojo.class);
                if(pojo.getName().equals(myName))
                {
                    messageList.add(FirebaseTextMessage.createForLocalUser(pojo.getMessage(),pojo.getTimestamp()));
                }
                else
                {
                    messageList.add(FirebaseTextMessage.createForRemoteUser(pojo.getMessage(),pojo.getTimestamp(),pojo.getName()));
                }
                suggestReplies();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void suggestReplies() {
          FirebaseSmartReply smartReply = FirebaseNaturalLanguage.getInstance().getSmartReply();
        smartReply.suggestReplies(messageList).addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
            @Override
            public void onSuccess(SmartReplySuggestionResult smartReplySuggestionResult) {
                mSuggestionParent.removeAllViews();

                for(SmartReplySuggestion suggestion: smartReplySuggestionResult.getSuggestions())
                {
                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.suggestions_layout,null, false);
                    TextView reply = view.findViewById(R.id.smartReply);
                    reply.setText(suggestion.getText());

                    reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MessagePojo pojo = new MessagePojo();
                            pojo.setName(myName);
                            pojo.setMessage(reply.getText().toString());
                            pojo.setTimestamp(System.currentTimeMillis());
                            databaseReference.push().setValue(pojo);
                        }
                    });
                    mSuggestionParent.addView(view);
                }

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<MessagePojo,MyViewHolder> adapter = new FirebaseRecyclerAdapter<MessagePojo, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i, @NonNull MessagePojo messagePojo) {

                myViewHolder.message.setText(messagePojo.getMessage());
                myViewHolder.name.setText(messagePojo.getName());


            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if(viewType == 1)
                {
                    view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.send_sms,parent,false);
                }
                else
                {
                    view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.receive_sms,parent,false);
                }
                return new MyViewHolder(view);
            }

            @Override
            public int getItemViewType(int position) {
                MessagePojo pojo = getItem(position);
                if(pojo.getName().equals(myName))
                {
                    return 1;
                }
                else
                {
                    return 2;
                }

            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                mRecyclerView.scrollToPosition((getItemCount() - 1));
            }
        };

        adapter.startListening();
        mRecyclerView.setAdapter(adapter);
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.message)TextView message;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

        }
    }
}
