
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id ("maven-publish")
}

//group = "com.github.NeWolf"
//version = libs.versions.versionName.get()

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

    publishing {
        publishing {
            singleVariant("release") {
                withSourcesJar()
//                withJavadocJar()
            }
        }
    }
//    namespace = "com.github.NeWolf.APNG"
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                //from(components.findByName("release"))
                from(components["release"])
                groupId = "com.github.NeWolf"
                artifactId = "APNG"
                version = "V${libs.versions.versionName.get()}"
//                artifact("$projectDir/outputs/aar/${project.name}-release.aar")
            }
        }

        // Add the following block for configuring the repository
        repositories {
            maven {
                name = "jitpack"
                url = uri("https://jitpack.io")
            }
        }
    }
}



dependencies {

    api(project(":FrameAnimation"))
    compileOnly(libs.androidx.core.ktx)
    compileOnly(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    api("com.github.NeWolf:FrameAnimation:1.0.0")
//    api(libs.frame)

}



