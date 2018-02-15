## Age and Gender Estimation of Faces

![Android](https://img.shields.io/badge/platform-android-lightgrey.svg)

An Android application utilising the OpenCV and Tensorflow frameworks to develop a mobile platform that can perform an estimation of age & gender of a subject in a given image.

## Functionality
- Capture Image is used to capture a new image with the devices camera.
  - On capturing new image the application will begin face detection, only 1 face can be present in order to perform an estimation.
- Import Image is used to import a new image from the devices gallery.
   - Estimation can only be performed on 1 face, if multiple faces are detected in a single image then the user must select a face presented to the user in order to extract it.
- All results will displayed in a separate UI instance. 

## TODO
- [ ] General
  - [X] Change application logo to updated minimal background icon.
  - [ ]  Replace Toast messages with Snackbar messages for neater UI alerts.
- [ ] Capture Image
  - [ ] Add front facing camera functionality.
  - [ ] Add graphic to UI alerting user to rotate screen for better image capture.
- [ ] Import Image
  - [ ] Restyle native gallery intent to theme of application.
- [ ] Multiple Faces
  - [ ] Fix bug that removes fragment from view when device is rotated.
- [ ] Results 
  - [ ] Fix bug that crashes application when device is rotated or placed in background then resumed.
  - [ ] Restyle results from model output.
- [ ] Testing
  - [ ] Add unit tests to test code.
  - [ ] Add tests to test UI elements and functionality.
  - [ ] Add tests to automatically test age/gender classifiers. (100 images)
  - [ ] Test functionality on numerous screensizes, I.e. various mobile screen sizes, tablets.
