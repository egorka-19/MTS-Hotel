package com.example.mts_hotel.bottomnav.main;

import static android.content.ContentValues.TAG;

import static com.google.firebase.components.Dependency.setOf;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mts_hotel.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OnnxValue;

// Другие импорты остаются прежними

public class GeneratedActivity extends AppCompatActivity {

    private OrtEnvironment env;
    private OrtSession session;
    private ImageView displayedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generated);

        copyModelToInternalStorage();

        displayedImage = findViewById(R.id.new_image);

        String imageUriString = getIntent().getStringExtra("imageUri");
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            displayedImage.setImageURI(imageUri);
        }
        // Получаем путь к модели
        String modelPath = getFilesDir() + "/best11.onnx";
        try {
            env = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            session = env.createSession(modelPath, options);

            // Логгирование имен входных и выходных тензоров для проверки
            for (String inputName : session.getInputNames()) {
                Log.d("ONNX", "Input name: " + inputName);
            }
            for (String outputName : session.getOutputNames()) {
                Log.d("ONNX", "Output name: " + outputName);
            }

        } catch (Exception e) {
            Log.e("ONNX", "Ошибка при создании сессии", e);
        }

        // Загружаем тестовое изображение из drawable
        Bitmap bitmap = loadBitmapFromDrawable(R.drawable._01);
        if (bitmap != null) {
            Log.d("BitmapInfo", "Width: " + bitmap.getWidth() + ", Height: " + bitmap.getHeight() + ", Config: " + bitmap.getConfig());
            runInference(bitmap); // Запускаем инференс
        } else {
            Log.e("BitmapInfo", "Bitmap is null");
        }
    }

    private void copyModelToInternalStorage() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getAssets().open("best11.onnx");
            File modelFile = new File(getFilesDir(), "best11.onnx");
            outputStream = new FileOutputStream(modelFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            Log.d(TAG, "Модель успешно скопирована в " + modelFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Ошибка при копировании модели: " + e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Ошибка при закрытии потоков: " + e.getMessage(), e);
            }
        }
    }

    // Загрузка изображения из ресурсов drawable
    private Bitmap loadBitmapFromDrawable(int resourceId) {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
        return Bitmap.createScaledBitmap(originalBitmap, 640, 640, true);
    }

    // Запуск инференса модели
    private void runInference(Bitmap bitmap) {
        try {
            Log.d("ONNX", "Начинается инференс модели.");

            // Подготовка изображения для модели
            OnnxTensor inputTensor = preprocessImage(bitmap);

            // Запуск инференса
            OrtSession.Result result = session.run(
                    Collections.singletonMap("images", inputTensor)
            );
            Log.d("ONNX", "Инференс завершен успешно.");
            processResults(result);

        } catch (Exception e) {
            Log.e("ONNX", "Ошибка инференса", e);
        }
    }

    // Подготовка изображения в формате OnnxTensor
    private OnnxTensor preprocessImage(Bitmap bitmap) throws Exception {
        // Изменение размера изображения до 640x640
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true);

        // Создаем floatBuffer с размером для RGB изображения (3 канала на каждый пиксель)
        float[] floatValues = new float[3 * 640 * 640];
        int index = 0;

        // Заполняем floatValues нормализованными значениями пикселей
        for (int y = 0; y < 640; y++) {
            for (int x = 0; x < 640; x++) {
                int pixel = resizedBitmap.getPixel(x, y);

                // Извлекаем и нормализуем каждый канал RGB
                floatValues[index++] = ((pixel >> 16) & 0xFF) / 255.0f; // R
                floatValues[index++] = ((pixel >> 8) & 0xFF) / 255.0f;  // G
                floatValues[index++] = (pixel & 0xFF) / 255.0f;         // B
            }
        }

        // Создаем тензор с формой [1, 3, 640, 640]
        long[] shape = {1, 3, 640, 640};
        return OnnxTensor.createTensor(env, FloatBuffer.wrap(floatValues), shape);
    }

    // Обработка результатов модели
    private void processResults(OrtSession.Result result) {
        try {
            OnnxValue outputValue = result.get("output0").get();
            if (outputValue instanceof OnnxTensor) {
                OnnxTensor outputTensor = (OnnxTensor) outputValue;

                // Получаем массив значений как объект float[][][]
                float[][][] outputData = (float[][][]) outputTensor.getValue();

                // Выводим значения для отладки
                //
            } else {
                Log.e("ONNX", "Неожиданный тип данных выхода: " + outputValue.getType());
            }
        } catch (Exception e) {
            Log.e("ONNX", "Ошибка обработки результатов", e);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Закрытие сессии и окружения
        try {
            if (session != null) session.close();
            if (env != null) env.close();
        } catch (Exception e) {
            Log.e("ONNX", "Ошибка при закрытии сессии или окружения", e);
        }
    }
}
