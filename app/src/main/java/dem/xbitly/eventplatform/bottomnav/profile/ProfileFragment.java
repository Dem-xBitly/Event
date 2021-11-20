package dem.xbitly.eventplatform.bottomnav.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import dem.xbitly.eventplatform.R;
import dem.xbitly.eventplatform.activities.InternetErrorConnectionActivity;
import dem.xbitly.eventplatform.activities.MainActivity;
import dem.xbitly.eventplatform.activities.SettingsActivity;
import dem.xbitly.eventplatform.activities.StartActivity;
import dem.xbitly.eventplatform.network.NetworkManager;
import dem.xbitly.eventplatform.tape.TapeAdapter;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private RecyclerView rv;

    private TextView profile_name;

    private String username;

    private CircleImageView profile_image;

    private boolean isUpdateRV = true;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri resultUri;

    private boolean e = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        checkNetwork();

        FirebaseDatabase dBase = FirebaseDatabase.getInstance();
        DatabaseReference ref = dBase.getReference("Users");
        profile_name = root.findViewById(R.id.profile_name);
        profile_image = root.findViewById(R.id.profile_image);
        rv = root.findViewById(R.id.profile_posts_recycler);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        getProfileImage();

        ref.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                username = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                profile_name.setText(username);

                String[] sR = !Objects.requireNonNull(snapshot.child("myReviews").getValue().toString()).equals("") ? Objects.requireNonNull(snapshot.child("myReviews").getValue()).toString().split(",") : new String[0];
                String[] sI = !Objects.requireNonNull(snapshot.child("myInvites").getValue().toString()).equals("") ? Objects.requireNonNull(snapshot.child("myInvites").getValue()).toString().split(",") : new String[0];
                if(isUpdateRV) {

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    rv.setLayoutManager(linearLayoutManager);
                    rv.setHasFixedSize(true);
                    try {
                        isUpdateRV = false;
                        TapeAdapter tapeAdapter = new TapeAdapter(sR, sI, Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), root.getContext(), getParentFragmentManager());
                        rv.setAdapter(tapeAdapter);
                    } catch (Exception e){
                        isUpdateRV = true;
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageButton logout_btn = root.findViewById(R.id.logout_btn);
        ImageButton settings_btn = root.findViewById(R.id.settings_btn);

        logout_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent (getContext(), StartActivity.class);
            startActivity(intent);
        });

        settings_btn.setOnClickListener(v -> {
            Intent intent = new Intent (getContext(), SettingsActivity.class);
            startActivity(intent);
        });

        return root;
    }

    private void getProfileImage(){
        e = true;
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile_image")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (e){
                            String ref = snapshot.getValue().toString();
                            Log.d("profile_image", ref);
                            Glide.with(getContext()).load(ref).into(profile_image);
                            e = false;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    public void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContext().getContentResolver(),
                                filePath);
                profile_image.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }

            uploadImage();
        }
    }

    // method to upload profile image
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            String name = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference ref = storageReference.child("images/" + name);

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();

                                    FancyToast.makeText(getContext(),"Profile photo updated",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();

                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profile_image")
                                                    .setValue(uri.toString());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("profile_image", "ERROR: "+e.getMessage());
                                        }
                                    });


                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            FancyToast.makeText(getContext(),"Failed to update profile photo: " + e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }

    public void checkNetwork(){
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            if (!connected) {
                Intent in_intent = new Intent (getContext(), InternetErrorConnectionActivity.class);
                startActivity(in_intent);
            }
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
    }
}