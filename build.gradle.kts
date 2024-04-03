// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    with(libs.plugins){
        alias(androidApplication) apply false
        alias(jetbrainsKotlinAndroid) apply false
        alias(googleDevtoolsKsp) apply false
        alias(jetbrainsKotlinKapt) apply false
        alias(daggerHiltAndroid) apply false
        alias(googleService) apply false
    }
}