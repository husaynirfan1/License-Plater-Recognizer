package com.rev1.licenseplate.recognizer;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int pic_id = 123;
    public MaterialButton galleryBtn, scanBtn, listBtn;
    public String uriString = "";
    public Uri image_uri;
    public TextView noimageTV;
    private FirebaseAuth mAuth;
    ViewModalPlate viewModalPlate;
    public ImageView prevImageView;
    private HashMap<String, Long> list;
    private ArrayList<String> data;
    public Query plateQuerybyPlate;
    public ValueEventListener plateListenerbyPlate;
    public TextView emailstudentTV, studentNameTV;
    public MaterialCardView infocard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mAuth = FirebaseAuth.getInstance();
        //define button
        noimageTV = findViewById(R.id.noimageTV);
        galleryBtn = findViewById(R.id.galleryButton);
        scanBtn = findViewById(R.id.scanButton);
        listBtn = findViewById(R.id.viewListButton);
        studentNameTV = findViewById(R.id.studentNameTV);
        emailstudentTV = findViewById(R.id.emailstudentTV);
        infocard = findViewById(R.id.infocard);
        infocard.setVisibility(View.GONE);

        prevImageView = findViewById(R.id.prevImageView);
        HashMap<Integer, String> map = new HashMap<Integer, String>();
        map.put (1, "Mark");
        map.put (2, "Tarryn");
        List<String> list = new ArrayList<String>(map.values());
        for (String s : list) {
            Log.d("listtest", s);
        }
        if (prevImageView.getDrawable() == null){
            noimageTV.setVisibility(View.VISIBLE);
        } else noimageTV.setVisibility(View.GONE);

        //Setup ViewModal
        viewModalPlate = new ViewModelProvider(this).get(ViewModalPlate.class);

        scanBtn.setOnClickListener(v -> {
            openCamera();
        });

        galleryBtn.setOnClickListener(v -> {
            getImage.launch("image/*");
        });
        listBtn.setOnClickListener(v ->{
            Intent i = new Intent(MainActivity.this, list_firebase_plate.class);
            startActivity(i);
        });
        
    }

    ActivityResultLauncher<String> getImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null){
                        uriString = result.toString();

                        if (!uriString.equals("") && !uriString.isEmpty()) {

                            getTextMLKit(MainActivity.this, result);
                            ImageDecoder.Source source = ImageDecoder.createSource(MainActivity.this.getContentResolver(), result);
                            try {
                                Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                                prevImageView.setImageBitmap(bitmap);

                            } catch (IOException exception){
                                Log.d("MainActivity-cameraActivityResultLauncher", exception.getMessage().toString());
                            }

                        }

                    }

                }
            }
    );

    public void checkPlate(String plate){
        viewModalPlate.findbyPlate(plate).observe(MainActivity.this, new Observer<PlateModal>() {
            @Override
            public void onChanged(PlateModal plateModal) {
                if (plateModal != null){

                    if (plateModal.isRegistered()){
                        Toast.makeText(MainActivity.this, plate+" is registered.", Toast.LENGTH_SHORT).show();
                    } else  {
                        Toast.makeText(MainActivity.this, plate+" is not registered !", Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(MainActivity.this, plate+" is not in the list.", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public void queryPlate(String plateI){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference myRef = database.getReference();
        plateQuerybyPlate = myRef.child("users");

        plateListenerbyPlate = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    Log.d("uidkeychildren", ds.getKey());
                    if (ds.getKey() != null){
                        if (snapshot.child(ds.getKey()).hasChild("vehicles")){
                            String studentUID = ds.getKey();
                            String key  = snapshot.child(studentUID).child("vehicles").getKey();
                            Map<String, Long> valss = (Map) snapshot.child(studentUID).child("vehicles").getValue();

                            if (valss != null){
                                Log.d("uidkeychildrenvalss", valss.toString());
                                for (Map.Entry<String,Long> entry : valss.entrySet()) {
                                    String text = entry.getKey();
                                    Long value = entry.getValue();
                                    StringBuilder str = new StringBuilder(100);

                                    str.append("\n"+entry.getKey() + " " + entry.getValue());

                                    String plateFinal = text+" "+String.valueOf(value);

                                    if (plateFinal.equals(plateI)){
                                        String email = (String) snapshot.child(studentUID).child("email").getValue();
                                        String username = (String) snapshot.child(studentUID).child("username").getValue();

                                        if (email != null && username != null){
                                            emailstudentTV.setText(email);
                                            studentNameTV.setText(username);



                                            break;
                                        }


                                    }

                                }


                            }


                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        };

        plateQuerybyPlate.addListenerForSingleValueEvent(plateListenerbyPlate);
    }
    public void checkPlateFirebase(String plate){

        //To split text and number
        String str = plate.replace(" ", "").trim();
        String[] part = str.split("(?<=\\D)(?=\\d)");
        Log.d("StringarraySize", String.valueOf(part.length));
        if (part.length < 2 || part == null || part.length == 0){
            Toast.makeText(MainActivity.this, "Failed to recognize plate number. Please zoom in the plate number for better recognition.", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println(part[0]);
            System.out.println(part[1]);

            if (!isNumeric(part[0]) && isNumeric(part[1])){
                String text = part[0];
                Long numbers = Long.valueOf(part[1]);

                list = new HashMap<>();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                mAuth = FirebaseAuth.getInstance();
                DatabaseReference myRef = database.getReference();
                Query plateQuery = myRef.child("PlateList");


                plateQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                            String key = noteSnapshot.getKey(); //parent key
                            long value = (long) noteSnapshot.getValue();
                            list.put(key, value);


                        }
                        if (list.containsKey(text)){
                            if (Objects.equals(list.get(text), numbers)){
                                Toast.makeText(MainActivity.this, text+" "+numbers+" is registered.", Toast.LENGTH_SHORT).show();
                                queryPlate(text+ " "+ numbers);
                                infocard.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(MainActivity.this, text+" "+numbers+" is not registered.", Toast.LENGTH_SHORT).show();
                                infocard.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, text+" "+numbers+" is not registered.", Toast.LENGTH_SHORT).show();
                            infocard.setVisibility(View.GONE);
                        }
                        Log.d("maplistplate", list.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("color","Error: " + error.getMessage());
                    }
                });
            } else Toast.makeText(MainActivity.this, "Please retry.", Toast.LENGTH_SHORT).show();


        }



    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void getTextMLKit(Context context, Uri uri) {
        TextRecognizer recognizer =
                TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        try {
            InputImage image = InputImage.fromFilePath(context, uri);
            Task<Text> textTask = recognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            //Use only the first block to identify license plate block
                            //To prevent reading car name, etc...
                            String blockText = text.getTextBlocks().get(0).getText();
                            String singleLine = blockText.replaceAll("[\r\n]+", " ").trim();
                            Log.d("License Plate LOG:", singleLine);
                            //checkPlate(blockText);
                            checkPlateFirebase(singleLine);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("License Plate LOG:", e.toString());
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        cameraActivityResultLauncher.launch(cameraIntent);
    }

    //TODO capture the image using camera and display it
    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        getTextMLKit(MainActivity.this, image_uri);
                        ImageDecoder.Source source = ImageDecoder.createSource(MainActivity.this.getContentResolver(), image_uri);
                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                            prevImageView.setImageBitmap(bitmap);

                        } catch (IOException exception){
                            Log.d("MainActivity-cameraActivityResultLauncher", exception.getMessage().toString());
                        }

                    }
                }
            });

    //TODO : MAKE DIALOG FOR LOGOUT
    @Override
    public void onBackPressed() {
        if (mAuth!= null){
            if (mAuth.getCurrentUser() != null){

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Log out");
                alertDialog.setMessage("Do you want to log out?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                dialog.dismiss();
                                MainActivity.this.finish();
                                Intent i = new Intent(MainActivity.this, Login.class);
                                startActivity(i);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }

    }
}