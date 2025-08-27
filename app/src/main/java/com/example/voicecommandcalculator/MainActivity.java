package com.example.voicecommandcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textScreen;
    private ImageView btnSpeak;
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;
    private boolean lastBracket; // Track if last action was adding brackets

    private int[] numericButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree,
            R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};
    private int[] operatorsButtons = {R.id.btnAdd, R.id.btnMin, R.id.btnMult, R.id.btnDevi, R.id.btnClear, R.id.btnBracketOpen, R.id.btnBracketClose};
    private final int REQ_CODE = 1;

    private TextToSpeech textToSpeechSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSpeak = findViewById(R.id.btnSpeak);
        textScreen = findViewById(R.id.textScreen);
        setNumericOnClickListener();
        setOperatorOnClickListener();
    }

    private void setNumericOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                if (stateError) {
                    textScreen.setText(button.getText());
                    stateError = false;
                } else {
                    textScreen.append(button.getText());
                }
                lastNumeric = true;
                lastBracket = false; // Reset the bracket flag
            }
        };

        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void setOperatorOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastNumeric && !stateError) {
                    Button button = (Button) view;
                    textScreen.append(button.getText());
                    lastNumeric = false;
                    lastDot = false;
                }
            }
        };

        for (int id : operatorsButtons) {
            findViewById(id).setOnClickListener(listener);
        }

        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textScreen.setText("");
                lastNumeric = false;
                stateError = false;
                lastDot = false;
            }
        });

        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opEqual();
            }
        });

        findViewById(R.id.btnSpeak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stateError) {
                    textScreen.setText("Try Again");
                    stateError = false;
                } else {
                    speachInput();
                }
                lastNumeric = true;
                lastBracket = false; // Reset the bracket flag
            }
        });

        findViewById(R.id.btnBracketOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textScreen.append("(");
                lastBracket = true;
            }
        });


        findViewById(R.id.btnBracketClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!lastNumeric || lastBracket) {
                    textScreen.append(")");
                    lastBracket = false;
                }
            }
        });
    }

    private void speachInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));

        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void opEqual() {
        if (lastNumeric && !stateError) {
            String text = textScreen.getText().toString();
            try {
                Expression expression = new ExpressionBuilder(text).build();
                double result = expression.evaluate();
                textScreen.setText(Double.toString(result));
            } catch (Exception e) {
                textScreen.setText("Error");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK) {
                    List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String change = result.get(0);

                    // Replace speech input with appropriate mathematical symbols and evaluate
                    change = change.replace("divide by", "/");
                    change = change.replace("into", "*");
                    change = change.replace("X", "*");
                    change = change.replace("add", "+");
                    change = change.replace("plus", "+");
                    change = change.replace("subtract", "-");
                    change = change.replace("subtract by", "-");
                    change = change.replace("equal", "=");
                    change = change.replace("equals", "=");
                    change = change.replace("sin 30 degrees", "0.5");
                    change = change.replace("sin 30 degree", "0.5");
                    change = change.replace("sin 45 degrees", "0.707106781187");
                    change = change.replace("sin 45 degree", "0.707106781187");
                    change = change.replace("sin 60 degrees", "0.866025403784");
                    change = change.replace("sin 60 degree", "0.866025403784");
                    change = change.replace("sin 90 degrees", "1");
                    change = change.replace("sin 90 degree", "1");
                    change = change.replace("cos 30 degrees", "0.866025403784");
                    change = change.replace("cos 30 degree", "0.866025403784");
                    change = change.replace("cos 45 degrees", "0.707106781187");
                    change = change.replace("cos 45 degree", "0.707106781187");
                    change = change.replace("cos 60 degrees", "0.5");
                    change = change.replace("cos 60 degree", "0.5");
                    change = change.replace("cos 90 degrees", "0");
                    change = change.replace("cos 90 degree", "0");
                    change = change.replace("tan 30 degrees", "0.57735026919");
                    change = change.replace("tan 30 degree", "0.57735026919");
                    change = change.replace("tan 45 degrees", "1");
                    change = change.replace("tan 45 degree", "1");
                    change = change.replace("tan 60 degrees", "1.73205026919");
                    change = change.replace("tan 60 degree", "1.73205026919");
                    change = change.replace("tan 90 degrees", "Undefined");
                    change = change.replace("tan 90 degree", "Undefined");

                    if (change.contains("=")) {
                        change = change.replace("=", "");
                        textScreen.setText(change);
                        opEqual();
                        spechout();
                    } else {
                        textScreen.setText(change);
                    }
                }
                break;
            }
        }
    }

    void spechout() {
        String Result = (String) textScreen.getText();
        textToSpeechSystem = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechSystem.speak(Result, TextToSpeech.QUEUE_ADD, null);
                }
            }
        });
    }
}





//package com.example.voicecommandcalculator;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.speech.RecognizerIntent;
//import android.speech.tts.TextToSpeech;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import net.objecthunter.exp4j.Expression;
//import net.objecthunter.exp4j.ExpressionBuilder;
//
//import java.util.List;
//import java.util.Locale;
//
//public class MainActivity extends AppCompatActivity {
//
//    private TextView textScreen;
//    private ImageView btnSpeak;
//    private boolean lastNumeric;
//    private boolean stateError;
//    private boolean lastDot;
//
//    private int[] numericButtons = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree,
//            R.id.btnFour, R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine};
//    private int[] operatorsButtons = {R.id.btnAdd, R.id.btnMin, R.id.btnMult, R.id.btnDevi, R.id.btnClear};
//    private final int REQ_CODE = 1;
//
//    private TextToSpeech textToSpeechSystem;
//
//
//
//
//
//
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
////        getSupportActionBar().hide();
//
//        btnSpeak = findViewById(R.id.btnSpeak);
//        textScreen = findViewById(R.id.textScreen);
//        setNumericOnClickListener();
//
//        setOperatorOnClickListener();
//
//
//    }
//
//    private void setNumericOnClickListener() {
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                Button button = (Button) view;
//
//                if (stateError) {
//
//                    textScreen.setText(button.getText());
//                    stateError = false;
//                }
//                else {
//
//                    textScreen.append(button.getText());
//                }
//                lastNumeric = true;
//            }
//        };
//
//        for (int id : numericButtons) {
//
//
//            findViewById(id).setOnClickListener(listener);
//        }
//
//    }
//
//
//    private void setOperatorOnClickListener() {
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                if (lastNumeric && !stateError) {
//
//                    Button button = (Button) view;
//                    textScreen.append(button.getText());
//                    lastNumeric = false;
//                    lastDot = false;
//
//                }
//            }
//        };
//
//        for (int id : operatorsButtons) {
//
//
//            findViewById(id).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    if (lastNumeric && !stateError && !lastDot) {
//
//
//                        textScreen.append(".");
//                        lastNumeric = false;
//                        lastDot = false;
//                    }
//                }
//            });
//
//
//            findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    textScreen.setText("");
//                    lastNumeric = false;
//                    stateError = false;
//                    lastDot = false;
//
//                }
//            });
//
//            findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    opEqual();
//                }
//            });
//
//
//
//            findViewById(R.id.btnSpeak).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    if (stateError){
//
//                        textScreen.setText("Try Again");
//                        stateError = false;
//                    }
//                    else {
//
//                        speachInput();
//
//                    }
//                    lastNumeric = true;
//                }
//            });
//
//            findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    textScreen.setText("+");
//
//
//
//                }
//            });
//
//            findViewById(R.id.btnMin).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    if (stateError){
//
//                        textScreen.setText("Error");
//                        stateError = false;
//                    }
//                    else {
//
//                        textScreen.setText("-");
//
//                    }
//                    lastNumeric = true;
//                }
//            });
//
//            findViewById(R.id.btnMult).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    if (stateError){
//
//                        textScreen.setText("Try Again");
//                        stateError = false;
//                    }
//                    else {
//
//                        speachInput();
//
//                    }
//                    lastNumeric = true;
//                }
//            });
//
//            findViewById(R.id.btnDevi).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//
//                    if (stateError){
//
//                        textScreen.setText("Try Again");
//                        stateError = false;
//                    }
//                    else {
//
//                        speachInput();
//
//                    }
//                    lastNumeric = true;
//                }
//            });
//
//
//        }
//
//
//    }
//
//    private void speachInput() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt)); // Corrected typo
//
//        try {
//            startActivityForResult(intent, REQ_CODE);
//        } catch (Exception e) {
//            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    private void opEqual() {
//
//        if (lastNumeric && !stateError) {
//
//            String text = textScreen.getText().toString();
//
//
//            try {
//
//                Expression expression = null;
//
//                try {
//                    expression = new ExpressionBuilder(text).build();
//                    double result = expression.evaluate();
//                    textScreen.setText(Double.toString(result));
//                }
//                catch (Exception e) {
//                    textScreen.setText("Error");
//                }
//            } catch (Exception e) {
//
//                textScreen.setText("Error");
//                stateError = true;
//                lastNumeric = false;
//            }
//
//
//        }
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//        switch (requestCode){
//
//
//            case REQ_CODE:{
//
//                if (requestCode == REQ_CODE && resultCode == RESULT_OK){
//
//
//
//
//                    List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    String change = result.get(0);
//
//                    textScreen.setText(change);
//
//
//
//                    change = change.replace("divide by","/");
//                    change = change.replace("into","*");
//
//                    change = change.replace("X","*");
//                    change = change.replace("X","*");
//
//                    change = change.replace("add","+");
//                    change = change.replace("plus","+");
//
//                    change = change.replace("subtract","-");
//                    change = change.replace("subtract by","-");
//
//                    change = change.replace("equal","=");
//                    change = change.replace("equals","=");
//
//                    change = change.replace("sin 30 degrees","0.5");
//                    change = change.replace("sin 30 degree","0.5");
//
//                    change = change.replace("sin 45 degrees","0.707106781187");
//                    change = change.replace("sin 45 degree","0.707106781187");
//
//                    change = change.replace("sin 60 degrees","0.866025403784");
//                    change = change.replace("sin 60 degree","0.866025403784");
//
//                    change = change.replace("sin 90 degrees","1");
//                    change = change.replace("sin 90 degree","1");
//
//                    change = change.replace("cos 30 degrees","0.866025403784");
//                    change = change.replace("cos 30 degree","0.866025403784");
//
//                    change = change.replace("cos 45 degrees","0.707106781187");
//                    change = change.replace("cos 45 degree","0.707106781187");
//
//                    change = change.replace("cos 60 degrees","0.5");
//                    change = change.replace("cos 60 degree","0.5");
//
//                    change = change.replace("cos 90 degrees","0");
//                    change = change.replace("cos 90 degree","0");
//
//                    change = change.replace("tan 30 degrees","0.57735026919");
//                    change = change.replace("tan 30 degree","0.57735026919");
//
//                    change = change.replace("tan 45 degrees","1");
//                    change = change.replace("tan 45 degree","1");
//
//                    change = change.replace("tan 60 degrees","1.73205026919");
//                    change = change.replace("tan 60 degree","1.73205026919");
//
//                    change = change.replace("tan 90 degrees","Undefined");
//                    change = change.replace("tan 90 degree","Undefined");
//
//                    if (change.contains("=")){
//
//                        change = change.replace("=","");
//                        textScreen.setText(change);
//
//                        opEqual();
//                        spechout();
//
//
//
//                    }
//                    else {
//
//                        textScreen.setText(change);
//
//                    }
//
//                }
//                break;
//            }
//        }
//    }
//
//
//
//
//
//    void spechout(){
//        String Resut1= (String) textScreen.getText();
//        textToSpeechSystem = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status == TextToSpeech.SUCCESS) {
//                    textToSpeechSystem.speak(Resut1, TextToSpeech.QUEUE_ADD, null);
//                }
//            }
//        });
//
//
//
//    }
//
//
//}
//
//
//
//
//
//
//
//
