package com.example.android.hellovideo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    public static void showSelectImageDialogBox (Uri uri, Context context, ProfilePictureUpdateResultListener listener) {
        Dialog dialog=new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.select_image);
        dialog.show();
        ImageView profilePicture = dialog.findViewById(R.id.selectedImage);
        profilePicture.setImageURI(uri);
        Button cancelButton, doneButton;
        cancelButton = dialog.findViewById(R.id.selectImageCancelButton);
        doneButton = dialog.findViewById(R.id.selectImageDoneButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.upload(uri, context, false);
                if (UserData.profilePictureUrl != null)
                    Database.deleteFromFirebseStorage(UserData.profilePictureUrl);
                profilePicture.setImageURI(uri);
                UserData.profilePictureUrl = uri.toString();
                listener.onComplete();
                dialog.dismiss();
            }
        });

    }

}
