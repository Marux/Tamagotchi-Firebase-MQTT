import org.gradle.internal.component.model.Exclude

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tamagotchi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tamagotchi"
        minSdk = 24
        targetSdk = 33
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
}

dependencies {
    //implement firebase
    implementation("com.google.firebase:firebase-firestore:23.0.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.android.support:multidex:1.0.3")
    //

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:15.0.1")
    implementation("androidx.core:core:1.12.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // mqtt
    implementation("com.android.support:support-v4:28.0.0") //permite conexion continua
    implementation("com.android.support:localbroadcastmanager:28.0.0") //permite agregar permisos

    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0")

    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1"){
        exclude(group = "com.android.support")
        exclude(module = "appcompat-v7")
        exclude(module = "support-v4")
    }
}