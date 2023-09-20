package com.example.friendlyneighborhood.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.friendlyneighborhood.Adapter.PaymentAdapter;
import com.example.friendlyneighborhood.Adapter.PaymentPdfAdapter;
import com.example.friendlyneighborhood.Model.PaymentModel;
import com.example.friendlyneighborhood.R;
import com.example.friendlyneighborhood.databinding.PaymentPdfTableBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentPdfFragment extends Fragment {

    PaymentPdfTableBinding binding;
    RecyclerView recyclerView;
    ArrayList<PaymentModel> list;
    ArrayList<PaymentModel> Oldlist;
    int TotalAmt = 0;
    private static final int CREATE_FILE = 1;
    PdfDocument document;
    String fromDate;
    String toDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = PaymentPdfTableBinding.inflate(inflater,container,false);

        fromDate = getArguments().getString("from");
        toDate = getArguments().getString("to");

        recyclerView = binding.PaymentsTableRv;
        list= new ArrayList<>();
        Oldlist = new ArrayList<>();


        ActivityCompat.requestPermissions(getActivity(),new String[]
        {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(getActivity(),new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);



        PaymentPdfAdapter adapter = new PaymentPdfAdapter(getContext(), list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);


        FirebaseDatabase.getInstance().getReference().child("payments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                PaymentModel payment = dataSnapshot.getValue(PaymentModel.class);
                                try {
                                    if(validDate(payment.getPaidAt()))
                                    {
                                    payment.setPaymentID(dataSnapshot.getKey());
                                    TotalAmt = TotalAmt + Integer.parseInt(payment.getPaymentAmount());
                                    Oldlist.add(payment);
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                            for (int i = Oldlist.size() - 1; i >= 0; i--) {
                                // Append the elements in reverse order
                                list.add(Oldlist.get(i));
                            }
                            binding.TotalAmt.setText("" + TotalAmt);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            binding.DownloadPdfBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createPDF(binding.tableLayoutPdf);
                }
            });

            return binding.getRoot();
    }

    private void createPDF(View view) {
        Bitmap bitmap = getBitmapFromView(view);
        document = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();
        PdfDocument.Page page1 = document.startPage(pageInfo1);

        Canvas canvas = page1.getCanvas();
        canvas.drawBitmap(bitmap,0,0,null);
        document.finishPage(page1);
        createFile();

    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
        {
            bgDrawable.draw(canvas);
        }
        else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void createFile()
    {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "payments.pdf");
        startActivityForResult(intent, CREATE_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CREATE_FILE
        && resultCode== Activity.RESULT_OK)
        {
            Uri uri = null;
            if(data != null)
            {
                uri = data.getData();

                if(document != null)
                {
                    ParcelFileDescriptor pfd = null;
                    try{
                        pfd = getContext().getContentResolver().openFileDescriptor(uri, "w");
                        FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                        document.writeTo(fileOutputStream);
                        document.close();
                        Toast.makeText(getContext(), "Pdf Saved Successfully ...", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        try {
                            DocumentsContract.deleteDocument(getContext().getContentResolver(), uri);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean validDate(String newDate) throws ParseException {
        Date fDate = new SimpleDateFormat("dd-MM-yyyy").parse(fromDate);
        Date tDate = new SimpleDateFormat("dd-MM-yyyy").parse(toDate);
        Date nDate = new SimpleDateFormat("dd-MM-yyyy").parse(newDate);

        return nDate.after(fDate) && nDate.before(tDate);

    }
}