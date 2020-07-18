package com.ahmednabil.whatsappclone;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList;
    private ArrayList<String> idsList;
    private DatabaseReference userGroupsRef;
    private DatabaseReference groupsRef;
    private FirebaseAuth firebaseAuth;
    public GroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);

        // getting user's list of groups
        firebaseAuth = FirebaseAuth.getInstance();
        userGroupsRef = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getUid()).child("groups");
        groupsRef = FirebaseDatabase.getInstance().getReference().child("groups");
        initializeFields();
        retrieveUserGroups();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String groupId = idsList.get(position);
                String groupName = arrayList.get(position);
                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupId", groupId);
                groupChatIntent.putExtra("groupName", groupName);
                startActivity(groupChatIntent);
            }
        });
        return groupFragmentView;
    }

    private void retrieveUserGroups() {
        userGroupsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    final String groupId = snapshot.getKey();
                    groupsRef.child(groupId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot != null && snapshot.getValue() != null) {
                                String groupName = snapshot.getValue().toString();
                                arrayList.add(groupName);
                                idsList.add(groupId);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {            }
        });
    }

    private void initializeFields() {
        listView = groupFragmentView.findViewById(R.id.groups_listView);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        idsList = new ArrayList<>(); // to keep track of the id corresponding to each name (to use later to get the chat from firebase.


    }
}