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

- Go to in develop > database
- create a new realtime database and name it.
- set in the rules
```
    {
        "rules": {
            ".read": true,
            ".write": true
        }
    }
```
- change **null** value by **root**

## Create a Cloud Storage

- Go to in develop > storage
- cretae new storage
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

## in the app project

- copy the downloaded file to <root-project-folder>/app/google-services.json
- run the project
- add new shortcut and save it.
- verify in Firebase console that the shortcut was added in the realtime database




