package com.example.friendlyneighborhood.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendlyneighborhood.Adapter.PostAdapter;
import com.example.friendlyneighborhood.Model.NotificationModel;
import com.example.friendlyneighborhood.Model.PostModel;
import com.example.friendlyneighborhood.Model.UserModel;
import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.FragmentForumBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class ForumFragment extends Fragment {

    FragmentForumBinding binding;
    RecyclerView PostRv;
    ArrayList <PostModel> postList;
    ArrayList <PostModel> OldList;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    Uri uri = Uri.parse("android.resource://com.example.friendlyneighborhood/drawable/build");
    UserModel user;

    public ForumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentForumBinding.inflate(inflater,container, false);

        binding.progressBarForum.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference().child("Users").
                child(FirebaseAuth.getInstance().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    user = snapshot.getValue(UserModel.class);
                                    binding.ForumUser.setText(user.getName());
                                    binding.blockFlatDetails.setText(user.getBlockFlat() + " (" + user.getType() + ")");

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        binding.posttext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String details = binding.posttext.getText().toString();
                if(!details.isEmpty())
                {
                    binding.PostBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_bg));
                    binding.PostBtn.setTextColor(getContext().getResources().getColor(R.color.black));
                    binding.PostBtn.setEnabled(true);
                }
                else
                {
                    binding.PostBtn.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_bg_disable));
                    binding.PostBtn.setTextColor(getContext().getResources().getColor(R.color.grey));
                    binding.PostBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.AddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 11);
            }
        });


        binding.PostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final StorageReference reference = storage.getReference().child("posts")
                        .child(FirebaseAuth.getInstance().getUid())
                        .child(new Date().getTime() +"");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                PostModel post = new PostModel();
                                post.setPostImage(uri.toString());
                                post.setPostedBy(FirebaseAuth.getInstance().getUid());
                                post.setPostDetails(binding.posttext.getText().toString());
                                SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                                Date date = new Date();
                                post.setPostedAt(String.valueOf(format.format(date)));

                                database.getReference().child("posts").push()
                                        .setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Posted Successfully", Toast.LENGTH_SHORT).show();

                                                binding.uploadedImage.setImageResource(R.drawable.build);
                                                binding.posttext.setText("");
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });


        binding.RedirectToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment());
                transaction.commit();
            }
        });

        binding.ForumUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment());
                transaction.commit();
            }
        });

        PostRv = binding.postRecView;
        postList = new ArrayList<>();
        OldList = new ArrayList<>();

        PostAdapter postAdapter = new PostAdapter(postList, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        PostRv.setLayoutManager(layoutManager);
        PostRv.setNestedScrollingEnabled(true);
        PostRv.setAdapter(postAdapter);

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    PostModel post = dataSnapshot.getValue(PostModel.class);
                    post.setPostId(dataSnapshot.getKey());
                    OldList.add(post);
                }
                for (int i = OldList.size() - 1; i >= 0; i--)
                {
                    // Append the elements in reverse order
                    postList.add(OldList.get(i));
                }
                postAdapter.notifyDataSetChanged();
                binding.progressBarForum.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getData()!=null)
        {
            uri = data.getData();
            binding.uploadedImage.setImageURI(uri);
        }

    }
}