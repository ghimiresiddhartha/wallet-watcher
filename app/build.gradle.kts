plugins {
    with(libs.plugins){
        alias(androidApplication)
        alias(jetbrainsKotlinAndroid)
        alias(jetbrainsKotlinKapt)
        alias(googleDevtoolsKsp)
        alias(daggerHiltAndroid)
        alias(googleService)
    }
}

android {
    namespace = "com.siddhartha.walletwatcher"

    libs.versions.apply {
        compileSdk = compileSdkVersion.get().toInt()

        defaultConfig {
            applicationId = "com.siddhartha.walletwatcher"
            minSdk = minSdkVersion.get().toInt()
            targetSdk = targetSdkVersion.get().toInt()
            versionCode = targetSdkVersion.get().toInt()
            versionName = "1.0"


            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        kotlinOptions {
            jvmTarget = jvmTargetVersion.get()
        }
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

    dataBinding {
        enable = true
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    with(libs){
        //Android Essentials
        implementation(material)
        testImplementation(junit)
        ksp(databinding)

        androidx.apply {
            implementation(core.ktx)
            implementation(appcompat)
            implementation(constraintlayout)
            androidTestImplementation(junit)
            androidTestImplementation(espresso.core)
            ksp(hilt.compiler)

            databinding.apply {
                ksp(common)
                ksp(compiler)
            }

            navigation.apply {
                implementation(fragment)
                implementation(ui)
            }

            //Clean Architecture
            lifecycle.apply {
                implementation(livedata)
                implementation(viewmodel)
                implementation(runtine)
            }

            //Database
            room.apply {
                implementation(room)
                implementation(runtime)
                ksp(compiler)
            }
        }

        //Multi Factor Authentication
        firebase.apply {
            implementation(platform(bom))
            implementation(auth)
        }

        //Dependency Injection
        hilt.apply {
            implementation(hilt)
            ksp(compiler)
        }

        //Image Processing
        implementation(glide)
        ksp(glide.compiler)

        //Layout
        implementation(neumorphism)
        implementation(chipnavigationbar)
        implementation(lottie)
        implementation(shimmer)
        implementation(circleimage)
        implementation(spinkit)
        implementation(chart)

        //Performance Optimizer
        debugImplementation(leakcanary)
    }
}