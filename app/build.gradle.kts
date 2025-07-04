plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.mg"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mg"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }

    /*ActivityMainBinding*/
    buildFeatures {
        viewBinding = true
    }

}


dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.github.rey5137:material:1.3.1")
    implementation("com.firebaseui:firebase-ui-database:8.0.2")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("io.github.pilgr:paperdb:2.7.2")

    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.12.0") // Use the latest version
}