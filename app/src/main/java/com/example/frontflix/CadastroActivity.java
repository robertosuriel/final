package com.example.frontflix;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    private EditText emailEditText, senhaEditText;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastro);

        emailEditText = findViewById(R.id.editTextTextEmailAddress);
        senhaEditText = findViewById(R.id.editTextTextPassword);
        databaseManager = new DatabaseManager(this);

        Button cadastrarButton = findViewById(R.id.button_cadastrar);
        cadastrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarUsuario();
            }
        });

        Button voltarButton = findViewById(R.id.button_voltar);
        voltarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cadastrarUsuario() {
        String email = emailEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(CadastroActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseManager.addUser(email, senha)) {
            Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish(); // Termina a atividade atual para que o usuário não possa voltar a ela com o botão de volta
        } else {
            Toast.makeText(CadastroActivity.this, "Erro ao cadastrar. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }
}
