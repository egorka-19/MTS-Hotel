package com.example.mts_hotel.bottomnav.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mts_hotel.databinding.FragmentMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private Uri filePath;

    // Лаунчеры для получения изображения из галереи и камеры
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    filePath = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), filePath);
                        showImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> captureImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    showImage(photo);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", null);

        // Изначально скрываем new_image
        binding.newImage.setVisibility(View.GONE);

        binding.butGal.setOnClickListener(v -> selectImageFromGallery());
        binding.butSkan.setOnClickListener(v -> captureImageWithCamera());

        binding.butGen.setOnClickListener(v -> {
            if (filePath != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("users-number-requests/" + System.currentTimeMillis());

                storageRef.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference()
                                .child("Users")
                                .child(phone)  // Используем телефон пользователя в пути
                                .child("requests");

                        // Создаем уникальный объект req{i}
                        String requestKey = myRef.push().getKey();  // Генерируем уникальный ключ
                        if (requestKey != null) {
                            myRef.child(requestKey).child("imageUri").setValue(uri.toString())
                                    .addOnSuccessListener(aVoid -> {
                                        Intent intent = new Intent(getActivity(), GeneratedActivity.class);
                                        intent.putExtra("imageUri", uri.toString());
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> Log.e("Firebase", "Ошибка при записи URL", e));
                        }
                    }).addOnFailureListener(e -> Log.e("Firebase", "Ошибка при получении URL", e));
                }).addOnFailureListener(e -> Log.e("Firebase", "Ошибка при загрузке", e));
            }
        });


        return binding.getRoot();
    }

    private String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void captureImageWithCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureImageLauncher.launch(intent);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }

    private void showImage(Bitmap bitmap) {
        // Устанавливаем изображение в new_image и делаем его видимым
        binding.newImage.setImageBitmap(bitmap);
        binding.newImage.setVisibility(View.VISIBLE);

        // Скрываем кнопки после выбора или создания изображения
        binding.butGal.setVisibility(View.GONE);
        binding.butSkan.setVisibility(View.GONE);

        saveImageToFile(bitmap);
    }

    private void saveImageToFile(Bitmap bitmap) {
        try {
            // Создание файла во внутреннем хранилище
            File file = new File(requireContext().getFilesDir(), "image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();

            // Путь к файлу
            filePath = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            captureImageWithCamera();
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
