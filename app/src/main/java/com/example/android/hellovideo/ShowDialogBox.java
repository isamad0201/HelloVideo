package com.example.android.hellovideo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ShowDialogBox {
    public static void showLoginDialogBox (Context context) {
        Dialog myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.activity_login);
        myDialog.setCancelable(true);

        Button loginButton = (Button) myDialog.findViewById(R.id.loginButton);
        TextView redirectSignup = (TextView) myDialog.findViewById(R.id.redirectSignup);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailField = myDialog.findViewById(R.id.loginEmail);
                EditText passwardField = myDialog.findViewById(R.id.loginPassward);
                Auth.login(emailField.getText().toString(), passwardField.getText().toString(),context,(Activity) context);
                myDialog.dismiss();
            }
        });

        redirectSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogBox.showSignupDialogBox(context);
                if(myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
        });
        myDialog.show();
    }

    public static void showSignupDialogBox (Context context) {
        Dialog myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.activity_signup);
        myDialog.setCancelable(true);

        Button signupButton = (Button) myDialog.findViewById(R.id.signupButton);
        TextView redirectSignup = (TextView) myDialog.findViewById(R.id.redirectLogin);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailField = myDialog.findViewById(R.id.signupEmail);
                EditText passwardField = myDialog.findViewById(R.id.signupPassward);
                EditText nameField = myDialog.findViewById(R.id.signupName);
                Auth.signin(emailField.getText().toString(), passwardField.getText().toString(), nameField.getText().toString(), context, (Activity) context);
                myDialog.dismiss();
            }
        });

        redirectSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogBox.showLoginDialogBox(context);
                if(myDialog.isShowing()) {
                    myDialog.dismiss();
                }
            }
        });
        myDialog.show();
    }
}
