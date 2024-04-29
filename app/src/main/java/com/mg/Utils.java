package com.mg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Utils {

    public static final String AD_STATUS_AVAILABLE = "AVAILABLE";
    public static final String AD_STATUS_SOLD = "SOLD";

    public static final String[] practical = {
            "Adoption",
            "Sale"
    };

//    public static final int[] practicalIcons={
//            R.drawable.paw,
//            R.drawable.ic_sale
//    };


    public static void  toast(Context context , String message){
        Toast.makeText(context , message , Toast.LENGTH_SHORT).show();
    }

    public static long getTimestamp(){
        return System.currentTimeMillis();
    }

    public static String formatTimestampDate(Long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

        calendar.setTimeInMillis(timestamp);

        String date = DateFormat.format( "dd/MM/yyyy", calendar).toString();

        return date;
    }

    public static void mapIntent(Context context, double latitude, double longitude){

        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr="+ latitude +","+ longitude);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if(mapIntent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(mapIntent);
        }else {
            Utils.toast(context, "Google MAP Not installed!");
        }

    }

//    public static void addToFavorite(Context context, String adId){
//        long timestamp = Utils.getTimestamp();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("adId", adId);
//        hashMap.put("timestamp", timestamp);
//        String emailId = First_page.userEmail.replace("@gmail.com","");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favorites");
//        ref.child(emailId).child(adId)
//                .setValue(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Utils.toast(context, "Failed to add to Favorite due to "+e.getMessage());
//                    }
//                });
//
//    }
//
//    public static void removeFormFavorite(Context context, String adId){
//        String emailId = First_page.userEmail.replace("@gmail.com","");
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Favorites");
//        ref.child(emailId).child(adId)
//                .removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Utils.toast(context, "Failed to remove to Favorite due to "+e.getMessage());
//                    }
//                });
//    }
//
//    public static void callIntent(Context context, String phone){
//
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+ Uri.encode(phone)));
//        context.startActivity(intent);
//
//    }
//
//    public static void openWhatsapp(Context context, String phone){
//
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + Uri.encode(phone)));
//        context.startActivity(intent);
//    }
//
//    public static void openEmail(Context context, String email){
//
//        // Create an intent with the ACTION_SENDTO action and the mailto URI
//        Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("mailto:"+ Uri.encode(email)));
//        context.startActivity(intent);
//    }

}
