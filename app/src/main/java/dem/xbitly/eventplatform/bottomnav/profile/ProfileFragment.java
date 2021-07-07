package dem.xbitly.eventplatform.bottomnav.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private ImageView profile_image;

    private boolean isUpdateRV = true;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Uri resultUri;

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
                chooseImage();
            }
        });

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

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {

        if(resultUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.putFile(resultUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            FancyToast.makeText(getContext(),"Image uploaded",FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            FancyToast.makeText(getContext(),"Upload failed: " + e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            CropImage.activity(filePath)
                    .start(getActivity());
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), resultUri);
                    profile_image.setImageBitmap(bitmap);
                    uploadImage();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}