# Android App connected to Firebase

## Steps to config Firebase

- Create a new project in **https://console.firebase.google.com**
- name new firebase project
- add your app (android)
- add the app package name 
```
    com.ernestovaldez.keyboardshortcuts
```
- add friendly app name (optional)
- click on **Register App**
- download file **google-services.json**

## Create a RealTime database

- Go to in **develop** > **database**
- create a new realtime database and name it.
- set in the rules (without authentication)
```
    {
        "rules": {
            ".read": true,
            ".write": true
        }
    }
```
- or set in the rules (with authentication)
```
    {
        "rules": {
            ".read": true,
            ".write": "auth !== null"
        }
    }
```
- change **null** value by **root**

## Create a Cloud Storage

- Go to in **develop** > **storage**
- create new storage
- define data center (if not defined previously)
- set in the rules
```
    rules_version = '2';
    service firebase.storage {
        match /b/{bucket}/o {
             match /{allPaths=**} {
                allow read, write;
            }
        }
    }
```
- create a new folder named **images**

## Email and Google Authentication

- Go to in **develop** > **authentication** > **Sing-in Method**
- Enable **Email/Password** and **Google**

**For Google Auth**
- needs configure the SHA1 fingerprint in the Firebase project settings, to obtain it can be run a command in console:

**For Debug mode:**
```
    keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android 
```
**for Release mode:**
```
    keytool -list -v -keystore {keystore_name} -alias {alias_name}
```
**example:**
```
    keytool -list -v -keystore C:\Users\MG\Desktop\test.jks -alias test
```
**or in Android Studio use simple step:**

- Run your project
- Click on **Gradle** menu
- Expand **Gradle app -> Tasks** tree
- Double click on **android -> signingReport** and see the magic
- It will tell you everything on the **Run** tab

## In the android app project

- copy the downloaded file to <root-project-folder>/app/google-services.json
- run the project
- add new shortcut and save it.
- verify in Firebase console that the shortcut was added in the realtime database

## Dependencies

```
    implementation 'com.jakewharton:butterknife:10.1.0'
    implementation 'com.google.firebase:firebase-core:17.2.0'
    implementation 'com.google.firebase:firebase-analytics:17.2.0'
    implementation 'com.google.firebase:firebase-database:19.0.0'
    implementation 'com.google.firebase:firebase-storage:19.0.0'
    implementation 'com.firebaseui:firebase-ui-auth:4.3.1'
    implementation 'com.google.firebase:firebase-auth:19.0.0'
    implementation 'com.github.bumptech.glide:glide:4.9.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0'
    annotationProcessor 'com.jakewharton:butterknife-compiler:10.1.0'
```

## References

**Firebase**
- https://firebase.google.com/docs/android/setup?authuser=0#available-libraries
- https://firebase.google.com/docs/auth/android/start?authuser=0
- https://firebase.google.com/docs/auth/android/firebaseui
- https://github.com/firebase/quickstart-android/tree/master/auth
- https://firebase.google.com/docs/database/security/quickstart?authuser=0

**Glide**
- https://github.com/bumptech/glide

**ButterKnife**
- https://github.com/JakeWharton/butterknife





