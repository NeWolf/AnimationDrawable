
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id ("maven-publish")
}

group = "com.github.NeWolf"
artifact=""
version = libs.versions.versionName.get()

android {
    namespace = "com.newolf.widget.drawable.apng"
    compileSdk = libs.versions.compileSdk.get().toInt()

    buildFeatures{
        buildConfig=true
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        buildConfigField("int", "versionCode", "${libs.versions.versionCode.get().toInt()}")
        buildConfigField("String", "versionName", "\"${libs.versions.versionName.get()}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    api("com.github.NeWolf:FrameAnimation:1.0.0")
}

afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            release(MavenPublication) {
                from components.release
                groupId = "com.github.NeWolf"
                artifactId = "APNG"
            }
        }
    }
}

