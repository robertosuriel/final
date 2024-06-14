package com.example.frontflix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText emailEditText, senhaEditText;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        senhaEditText = findViewById(R.id.editTextTextPassword);
        databaseManager = new DatabaseManager(this);

        TextView cadastreseTextView = findViewById(R.id.cadastrese);
        cadastreseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });
    }

    public void btnClick(View view) {
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseManager.authenticateUser(email, senha)) {
            Toast.makeText(MainActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, HelloActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish(); // Termina a atividade atual para que o usuário não possa voltar a ela com o botão de volta
        } else {
            Toast.makeText(MainActivity.this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
        }
    }
}
